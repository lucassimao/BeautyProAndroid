package br.com.beautybox.atendimentos;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import br.com.beautybox.MainActivity;
import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;
import br.com.beautybox.domain.FormaPagamento;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.service.AtendimentoService;
import br.com.beautybox.service.ClienteService;

/**
 * Created by lsimaocosta on 22/06/16.
 */

public class AtendimentoFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Atendimento atendimento;
    private ServicosAdapter adapter;

    public AtendimentoFragment() {  }

    public static AtendimentoFragment newInstance(Atendimento atendimento) {
        AtendimentoFragment fragment = new AtendimentoFragment();
        fragment.atendimento = atendimento;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_atendimento, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtViewData = (TextView) view.findViewById(R.id.txt_view_data);
        txtViewData.setOnClickListener(onClickTextViewData());

        TextView txtViewHorario = (TextView) view.findViewById(R.id.txt_view_horario);
        txtViewHorario.setOnClickListener(onClickTextViewHorario());

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner_forma_pagamento);
        ArrayAdapter<FormaPagamento> formaPagamentoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, FormaPagamento.values());
        spinner.setAdapter(formaPagamentoAdapter);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        final ListView listView = (ListView) view.findViewById(R.id.list_view);

        final Query query = FirebaseDatabase.getInstance()
                .getReference(Servico.FIREBASE_NODE).orderByChild("descricao");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                adapter = new ServicosAdapter(getActivity(), query,atendimento);
                adapter.setOnServicoClickListener(onServicoClickListener());
                listView.setAdapter(adapter);

                spinner.setOnItemSelectedListener(onFormaPagamentoSelected());
                spinner.setSelection(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log("Erro ao consultar serviços");
                FirebaseCrash.report(databaseError.toException());
            }
        });



        Button btnSalvar = (Button) view.findViewById(R.id.btn_salvar);
        btnSalvar.setOnClickListener(onClickSalvar());

        Button btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(onClickCancelar());

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        // entrando no modo edição
        if (this.atendimento != null) {

            ClienteService.find(atendimento.getClienteRef(), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView textViewNomeCliente = (TextView) getView().findViewById(R.id.edit_cliente);
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    textViewNomeCliente.setText(cliente.getNome());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.log("Erro ao consultar cliente " + atendimento.getClienteRef());
                    FirebaseCrash.report(databaseError.toException());
                }
            });

            TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(timeZone);
            Date dataHorario = new Date(atendimento.getDataHorario());

            txtViewData.setText(sdf.format(dataHorario));

            sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(timeZone);
            txtViewHorario.setText(sdf.format(dataHorario));


            FormaPagamento formaPagamento = FormaPagamento.valueOf(atendimento.getFormaPagamento());
            int position = formaPagamentoAdapter.getPosition(formaPagamento);
            spinner.setSelection(position);

            EditText editTextSessoes = (EditText) view.findViewById(R.id.edit_qtde_sessoes);
            editTextSessoes.setText(String.valueOf(atendimento.getSessoes()));

            atualizarValorTotal();

            toolbar.setTitle("Editar Atendimento");
        }else
            toolbar.setTitle("Cadastrar Atendimento");

        ((MainActivity) getActivity()).hideDrawer();

    }

    private View.OnClickListener onClickCancelar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        };
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<String> servicosSelecionados = adapter.getServicosSelecionados();

                if (servicosSelecionados.size() == 0) {
                    Toast.makeText(getContext(), "Selecione ao menos 1 serviço", Toast.LENGTH_SHORT).show();
                    return;
                }

                TextView textViewNomeCliente = (TextView) getView().findViewById(R.id.edit_cliente);
                CharSequence nomeCliente = textViewNomeCliente.getText();

                if (TextUtils.isEmpty(nomeCliente)) {
                    Toast.makeText(getContext(), "O nome do cliente deve ser informado", Toast.LENGTH_SHORT).show();
                    textViewNomeCliente.requestFocus();
                    return;
                }

                TextView txtViewData = (TextView) getView().findViewById(R.id.txt_view_data);
                TextView txtViewHorario = (TextView) getView().findViewById(R.id.txt_view_horario);

                Spinner spinner = (Spinner) getView().findViewById(R.id.spinner_forma_pagamento);
                FormaPagamento formaPagamento = (FormaPagamento) spinner.getSelectedItem();

                TextView editTxtQtdeSessoes = (TextView) getView().findViewById(R.id.edit_qtde_sessoes);

                Cliente cliente = new Cliente();
                cliente.setNome(nomeCliente.toString());

                if (atendimento == null)
                    atendimento = new Atendimento();
                else{
                    atendimento.setServicosRefs(null);
                    cliente.setKey(atendimento.getClienteRef());
                }


                try {
                    String string = txtViewData.getText() + " " + txtViewHorario.getText();
                    TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    sdf.setTimeZone(timeZone);
                    Date dt = sdf.parse(string);

                    atendimento.setDataHorario(dt.getTime());
                    atendimento.setFormaPagamento(formaPagamento);
                    atendimento.setSessoes(Integer.valueOf(editTxtQtdeSessoes.getText().toString()));

                    for (String servicoKey : servicosSelecionados)
                        atendimento.addServico(servicoKey);

                    Task task = AtendimentoService.save(atendimento, cliente);
                    final ProgressDialog dialog = ProgressDialog.show(getContext(),"Aguarde","Salvando atendimento ...",true,false);
                    dialog.show();

                    task.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            dialog.dismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Atendimento agendado!", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();

                            } else {
                                String text = "Erro ao agendar atendimento: " + task.getException().getMessage();
                                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                    Toast.makeText(getContext(), "A data e o horário devem ser informados", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private ServicosAdapter.OnServicoClickListener onServicoClickListener() {
        return new ServicosAdapter.OnServicoClickListener() {
            @Override
            public void onClick(Servico servico, DatabaseReference ref, boolean isChecked) {
                atualizarValorTotal();
            }
        };
    }

    private void atualizarValorTotal() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(Servico.FIREBASE_NODE);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            TextView txtValorTotal = (TextView) getView().findViewById(R.id.txt_valor_total);
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long valorTotal = 0;
                Spinner spinner = (Spinner) getView().findViewById(R.id.spinner_forma_pagamento);
                FormaPagamento formaPagamento = (FormaPagamento) spinner.getSelectedItem();
                Set<String> servicosSelecionados = adapter.getServicosSelecionados();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (!servicosSelecionados.contains(child.getKey()))
                        continue;

                    Servico servico = child.getValue(Servico.class);

                    if (formaPagamento.equals(FormaPagamento.APrazo))
                        valorTotal += servico.getValorAPrazo();
                    else
                        valorTotal += servico.getValorAVista();
                }

                txtValorTotal.setText("Total: " + numberFormat.format(valorTotal / 100.0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log("Erro ao calcular valor total do atendimento");
                FirebaseCrash.report(databaseError.toException());

                txtValorTotal.setText("Total: " + numberFormat.format(0));
                Toast.makeText(getContext(),"Erro ao calcular valor total",Toast.LENGTH_SHORT);
            }
        });
    }

    private AdapterView.OnItemSelectedListener onFormaPagamentoSelected() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FormaPagamento fp = (FormaPagamento) parent.getItemAtPosition(position);
                ListView listView = (ListView) getView().findViewById(R.id.list_view);
                ServicosAdapter servicosAdapter = (ServicosAdapter) listView.getAdapter();

                if (servicosAdapter != null)
                    servicosAdapter.setFormaPagamento(fp);

                atualizarValorTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {    }
        };
    }

    private View.OnClickListener onClickTextViewHorario() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance(AtendimentoFragment.this);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        };
    }

    private View.OnClickListener onClickTextViewData() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(AtendimentoFragment.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        };
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
        TextView txtViewHorario = (TextView) getView().findViewById(R.id.txt_view_horario);
        txtViewHorario.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        TextView txtViewData = (TextView) getView().findViewById(R.id.txt_view_data);
        txtViewData.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onPause() {
        super.onPause();
        //Ocultando o telcado que eventualmente esteja aberto
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        ((MainActivity) getActivity()).showDrawer();
    }
}
