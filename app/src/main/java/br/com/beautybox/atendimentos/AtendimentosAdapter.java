package br.com.beautybox.atendimentos;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.service.AtendimentoService;
import br.com.beautybox.service.ClienteService;

/**
 * Created by lsimaocosta on 22/06/16.
 */
public class AtendimentosAdapter extends BaseExpandableListAdapter {

    private static final String TAG = AtendimentosAdapter.class.getSimpleName();
    private final Context ctx;
    private final DateFormat groupFormatter;
    private final DateFormat sdfHour;
    private final DateFormat sdf2;
    private final Query queryAtendimentos;
    private TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");
    private MyListener myListener = new MyListener();

    // timestamps dos dias que tem atendimento
    private List<Long> grupos;

    // mapa timestamp do dia -> atendimentos
    private Map<Long, List<Atendimento>> atendimentos;

    // mapa primary key -> serviço
    private Map<String, Servico> servicos;

    public AtendimentosAdapter(Context context) {
        this.ctx = context;
        FirebaseDatabase instance = FirebaseDatabase.getInstance();

        // carregando mapa de serviços
        servicos = new HashMap<>();
        Query queryServicos = instance.getReference(Servico.FIREBASE_NODE);
        queryServicos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    Servico servico = child.getValue(Servico.class);
                    servicos.put(key, servico);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        // carregando os atendimentos
        String bucket = AtendimentoService.getCurrentBucket();
        queryAtendimentos = instance.getReference(Atendimento.FIREBASE_NODE).orderByKey().startAt(bucket);
        queryAtendimentos.addValueEventListener(myListener);

        grupos = new ArrayList<>();
        atendimentos = new HashMap<>();

        groupFormatter = new SimpleDateFormat("dd/MM/yyyy - EEEE", Locale.getDefault());
        groupFormatter.setTimeZone(timeZone);

        sdfHour = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdfHour.setTimeZone(timeZone);

        sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm - EEEE", Locale.getDefault());
        sdf2.setTimeZone(timeZone);
    }


    @Override
    public int getGroupCount() {
        return grupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        long grupo = grupos.get(groupPosition);
        return this.atendimentos.get(grupo).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return grupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        long grupo = grupos.get(groupPosition);
        return atendimentos.get(grupo).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Application.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
        }

        TextView txtView1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView txtView2 = (TextView) convertView.findViewById(android.R.id.text2);

        long grupo = grupos.get(groupPosition);
        txtView1.setText(groupFormatter.format(new Date(grupo)));

        int count = atendimentos.get(grupo).size();
        txtView2.setText(ctx.getResources().getQuantityString(R.plurals.atendimentos_str, count, count));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            convertView = layoutInflater.inflate(R.layout.list_item_atendimentos, null);
        }
        final Atendimento atendimento = (Atendimento) getChild(groupPosition, childPosition);

        final TextView textViewCliente = (TextView) convertView.findViewById(R.id.txt_cliente);
        TextView textHorario = (TextView) convertView.findViewById(R.id.txt_horario);
        TextView txtViewServicos = (TextView) convertView.findViewById(R.id.txt_view_servicos);

        textViewCliente.setText("...");

        ClienteService.find(atendimento.getClienteRef(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cliente cliente = dataSnapshot.getValue(Cliente.class);
                textViewCliente.setText(cliente.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                textViewCliente.setText(" :: Erro ::");
            }
        });

        textHorario.setText(sdfHour.format(atendimento.getDataHorario()));

        StringBuilder sb = new StringBuilder();

        for (String key : atendimento.getServicosRefs()) {
            if (!TextUtils.isEmpty(key)) {
                Servico servico = servicos.get(key);
                sb.append(servico.getDescricao() + "\n");
            }
        }

        txtViewServicos.setText(sb.toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void unregisterListeners() {
        queryAtendimentos.removeEventListener(myListener);
    }


    private class MyListener implements ValueEventListener {

        private final String TAG = MyListener.class.getSimpleName();

        // serve p/ ordenar os grupos por ordem decrescente
        Comparator<Long> groupComparator = new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return lhs.compareTo(rhs) * -1;
            }
        };
        // ordena em ordem crescente de horario
        private Comparator<Atendimento> atendimentosHorarioComparator = new Comparator<Atendimento>() {
            @Override
            public int compare(Atendimento lhs, Atendimento rhs) {
                long lhsDataHorario = lhs.getDataHorario();
                long rhsDataHorario = rhs.getDataHorario();
                return (lhsDataHorario < rhsDataHorario ) ? -1 : (lhsDataHorario == rhsDataHorario ? 0 : 1);
            }
        };

        private long clearTime(long dataHorario) {
            Calendar c = GregorianCalendar.getInstance(timeZone);
            c.setTimeInMillis(dataHorario);
            c.clear(Calendar.HOUR_OF_DAY);
            c.clear(Calendar.HOUR);
            c.clear(Calendar.MINUTE);
            c.clear(Calendar.SECOND);
            c.clear(Calendar.MILLISECOND);
            return c.getTimeInMillis();
        }

        private void addAtendimento(Atendimento atendimento) {
            long group = clearTime(atendimento.getDataHorario());

            if (!atendimentos.containsKey(group)) {
                grupos.add(group);
                atendimentos.put(group, new LinkedList<Atendimento>());

                Collections.sort(grupos, groupComparator);
            }
            atendimentos.get(group).add(atendimento);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange ");

            grupos = new LinkedList<>();
            atendimentos = new HashMap<>();

            // o evento é disparado em cima do bucket e todos os filhos são enviadsos, tem que iterar p/ ler os filhos
            for (DataSnapshot bucket : dataSnapshot.getChildren()) {

                for(DataSnapshot child : bucket.getChildren()) {
                    Atendimento atendimento = child.getValue(Atendimento.class);
                    atendimento.setKey(child.getKey());
                    addAtendimento(atendimento);
                }
            }

            // p/ cada grupo, ordenar os atendimentos em ordem crescente de horario
            for (List<Atendimento> atendimentoList : atendimentos.values())
                Collections.sort(atendimentoList, atendimentosHorarioComparator);

            notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            FirebaseCrash.logcat(Log.DEBUG,TAG,"Erro ao carregar atendimentos");
            FirebaseCrash.report(databaseError.toException());
        }
    }
}
