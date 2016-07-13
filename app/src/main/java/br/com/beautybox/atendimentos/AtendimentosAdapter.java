package br.com.beautybox.atendimentos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import br.com.beautybox.R;
import br.com.beautybox.dao.AtendimentoDAO;
import br.com.beautybox.dao.ClienteDAO;
import br.com.beautybox.dao.ServicoDAO;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;
import br.com.beautybox.domain.ItemServico;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 22/06/16.
 *
 * Adapter do listView do AtendimentosListFragment
 */
public class AtendimentosAdapter extends BaseExpandableListAdapter {

    private static final String TAG = AtendimentosAdapter.class.getSimpleName();
    private final Context ctx;
    private final DateFormat groupFormatter;
    private final DateFormat hourFormatter;
    private final Query queryAtendimentos;
    private TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");
    private AtendimentoListener atendimentoListener = new AtendimentoListener();

    // timestamps dos dias que tem atendimento
    private List<Long> grupos;

    // mapa timestamp do dia -> atendimentos
    private Map<Long, List<Sessao>> sessoes;

    // mapa key -> serviço
    private Map<String, Servico> servicos;

    public AtendimentosAdapter(Context context) {
        this.ctx = context;
        servicos = new HashMap<>();
        grupos = new ArrayList<>();
        sessoes = new HashMap<>();

        groupFormatter = new SimpleDateFormat("dd/MM/yyyy - EEEE", Locale.getDefault());
        hourFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        groupFormatter.setTimeZone(timeZone);
        hourFormatter.setTimeZone(timeZone);

        // carregando os serviços previamente
        ServicoDAO.list(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Servico servico = ServicoDAO.load(child);
                    servicos.put(servico.getKey(), servico);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "Erro ao carregar serviços");
                FirebaseCrash.report(databaseError.toException());
            }
        });

        // carregando os atendimentos
        queryAtendimentos = AtendimentoDAO.list();
        queryAtendimentos.addValueEventListener(atendimentoListener);
    }


    private static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    @Override
    public int getGroupCount() {
        return grupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        long grupo = grupos.get(groupPosition);
        return this.sessoes.get(grupo).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return grupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        long grupo = grupos.get(groupPosition);
        return sessoes.get(grupo).get(childPosition);
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
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            convertView = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
        }

        TextView txtView1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView txtView2 = (TextView) convertView.findViewById(android.R.id.text2);

        long grupo = grupos.get(groupPosition);
        txtView1.setText(groupFormatter.format(new Date(grupo)));

        int count = sessoes.get(grupo).size();
        txtView2.setText(ctx.getResources().getQuantityString(R.plurals.atendimentos_str, count, count));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            convertView = layoutInflater.inflate(R.layout.list_item_atendimentos, null);
        }
        final Sessao sessao = (Sessao) getChild(groupPosition, childPosition);
        final Atendimento atendimento = sessao.getAtendimento();

        int numSessao = atendimento.getSessoes().indexOf(sessao)+1;

        TextView textNumSessao = (TextView) convertView.findViewById(R.id.text_num_sessao);
        textNumSessao.setText(numSessao + "º / " + atendimento.getSessoes().size());

        ImageView imgViewFormaPgmnto = (ImageView) convertView.findViewById(R.id.image_forma_pagamento);
        Drawable drawable = null;

        switch (atendimento.getFormaPagamento()) {
            case APrazo:
                drawable = ContextCompat.getDrawable(ctx, R.drawable.credit_card_icon);
                break;
            case AVista:
                drawable = ContextCompat.getDrawable(ctx, R.drawable.dollar_currency_sign);
                break;
        }

        long totalPago = atendimento.getPgmtCartaoCredito()
                        + atendimento.getPgmtCartaoDebito()
                        + atendimento.getPgmtDinheiro();

        // indicando que o atendimento ainda nao foi pago
        if ( atendimento.getValorTotal() != totalPago)
            drawable = convertDrawableToGrayScale(drawable);

        imgViewFormaPgmnto.setImageDrawable(drawable);


        final TextView textViewCliente = (TextView) convertView.findViewById(R.id.txt_cliente);
        textViewCliente.setText("...");
        Cliente cliente = atendimento.getCliente();

        if (!TextUtils.isEmpty(cliente.getNome())) {
            textViewCliente.setText(cliente.getNome());
        } else {
            ClienteDAO.find(cliente.getKey(), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Cliente cliente = ClienteDAO.load(dataSnapshot);
                    textViewCliente.setText(cliente.getNome());

                    // substituindo o lazy object pelo objeto carregado por completo
                    atendimento.setCliente(cliente);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.logcat(Log.DEBUG, TAG, "Erro ao carregar cliente " + atendimento.getCliente().getKey());
                    FirebaseCrash.report(databaseError.toException());
                    textViewCliente.setText(" :: Erro ::");
                }
            });
        }

        TextView textHorario = (TextView) convertView.findViewById(R.id.txt_horario);
        textHorario.setText(hourFormatter.format(sessao.getTimestamp()));

        StringBuilder sb = new StringBuilder();

        for (ItemServico itemServico : sessao.getServicos()) {
            String key = itemServico.getServico().getKey();
            int quantidade = itemServico.getQuantidade();
            Servico servico = servicos.get(key);

            // substituindo  o lazy object pelo objeto completamente carregado
            itemServico.setServico(servico);

            String line = String.format("%d %s\n", quantidade,servico.getDescricao());
            sb.append(line);
        }

        TextView txtViewServicos = (TextView) convertView.findViewById(R.id.txt_view_servicos);
        txtViewServicos.setText(sb.toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void unregisterListeners() {
        queryAtendimentos.removeEventListener(atendimentoListener);
    }

    private class AtendimentoListener implements ValueEventListener {

        private final String TAG = AtendimentoListener.class.getSimpleName();

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            grupos.clear();
            sessoes.clear();

            // o evento é disparado em cima do bucket e todos os filhos são enviados, tem que iterar p/ ler os filhos
            for (DataSnapshot bucket : dataSnapshot.getChildren()) {

                for (DataSnapshot child : bucket.getChildren()) {
                    Atendimento atendimento = AtendimentoDAO.load(child);
                    adicionarSessoes(atendimento);
                }
            }

            // ordenar os grupos por ordem decrescente
            Collections.sort(grupos, groupComparator);

            // p/ cada grupo, ordenar os atendimentos em ordem crescente de horario
            for (List<Sessao> sessoesList : sessoes.values())
                Collections.sort(sessoesList);

            notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            FirebaseCrash.logcat(Log.DEBUG, TAG, "Erro ao carregar atendimentos");
            FirebaseCrash.report(databaseError.toException());
        }

        // serve p/ ordenar os grupos por ordem decrescente
        Comparator<Long> groupComparator = new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return lhs.compareTo(rhs) * -1;
            }
        };

        private void adicionarSessoes(Atendimento atendimento) {

            for (Sessao sessao : atendimento.getSessoes()) {
                long sessaoTimestamp = sessao.getTimestamp();
                long grupo = clearTimeFields(sessaoTimestamp);

                if (!sessoes.containsKey(grupo)) {
                    grupos.add(grupo);
                    sessoes.put(grupo, new ArrayList<Sessao>());
                }

                sessoes.get(grupo).add(sessao);
            }
        }

        private long clearTimeFields(long dataHorario) {
            Calendar c = GregorianCalendar.getInstance(timeZone);
            c.setTimeInMillis(dataHorario);
            c.clear(Calendar.HOUR_OF_DAY);
            c.clear(Calendar.HOUR);
            c.clear(Calendar.MINUTE);
            c.clear(Calendar.SECOND);
            c.clear(Calendar.MILLISECOND);
            return c.getTimeInMillis();
        }
    }
}
