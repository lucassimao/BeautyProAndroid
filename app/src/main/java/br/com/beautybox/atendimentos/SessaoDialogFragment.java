package br.com.beautybox.atendimentos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.beautybox.DatePickerFragment;
import br.com.beautybox.R;
import br.com.beautybox.TimePickerFragment;
import br.com.beautybox.dao.ServicoDAO;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 07/07/16.
 */
public class SessaoDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = SessaoDialogFragment.class.getSimpleName();
    private OnAdicionarSessaoListener onAdicionarSessaoListener;
    private Sessao sessao;
    private ItemServicoAdapter itemServicoAdapter;
    private boolean isEditing = true;


    public static SessaoDialogFragment newInstance(Sessao sessao, OnAdicionarSessaoListener onAdicionarSessaoListener) {
        SessaoDialogFragment dialogFragment = new SessaoDialogFragment();
        dialogFragment.sessao = sessao;
        dialogFragment.onAdicionarSessaoListener = onAdicionarSessaoListener;
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String title = "Editar Sessão";

        if (sessao == null) {
            this.isEditing = false;
            sessao = new Sessao();
            title = "Nova Sessão";
        }

        builder.setView(loadView()).
                setPositiveButton("Confirmar", null).
                setTitle(title).
                setNegativeButton("Cancelar", onClickCancelar());

        // tenho que fazer isso p/ ao clicar no positive button a tela não fechar, pq é preciso
        // validar o formulario
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickConfirmar();
                    }
                });
            }
        });

        return alertDialog;
    }

    private void onClickConfirmar() {

        if (sessao.getServicos().size() == 0) {
            Toast.makeText(getContext(), "Ao menos 1 serviço deve ser agendado", Toast.LENGTH_LONG).show();
            return;
        }

        if (onAdicionarSessaoListener != null) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - H:mm", Locale.getDefault());

            TextView txtViewData = (TextView) getDialog().findViewById(R.id.txt_view_data);
            TextView txtViewHorario = (TextView) getDialog().findViewById(R.id.txt_view_horario);

            try {

                Date dt = sdf.parse(txtViewData.getText() + " - " + txtViewHorario.getText());
                sessao.setTimestamp(dt.getTime());

                onAdicionarSessaoListener.add(sessao, isEditing);

            } catch (ParseException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
        }

        dismiss();
    }

    private View loadView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat hourMinuteFormat = new SimpleDateFormat("H:mm", Locale.getDefault());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_sessao, null);

        itemServicoAdapter = new ItemServicoAdapter(sessao);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemServicoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Date date = new Date(sessao.getTimestamp());

        TextView txtViewData = (TextView) view.findViewById(R.id.txt_view_data);
        txtViewData.setText(dateFormat.format(date));
        txtViewData.setOnClickListener(onClickTextViewData());

        TextView txtViewHorario = (TextView) view.findViewById(R.id.txt_view_horario);
        txtViewHorario.setText(hourMinuteFormat.format(date));
        txtViewHorario.setOnClickListener(onClickTextViewHorario());

        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_add_servico);
        btn.setOnClickListener(onClickAddServico());

        final Spinner spinnerServicos = (Spinner) view.findViewById(R.id.spinner_servicos);

        ServicoDAO.list(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Servico> servicos = new ArrayList<Servico>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    servicos.add(ServicoDAO.load(child));
                }

                int layout = android.R.layout.simple_spinner_dropdown_item;
                ArrayAdapter<Servico> arrayAdapter = new ArrayAdapter<Servico>(getContext(), layout, servicos);
                spinnerServicos.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "Erro ao carregar serviços na recyclerView de SessaoDialogFragment");
                FirebaseCrash.report(databaseError.toException());
            }
        });

        return view;
    }

    private View.OnClickListener onClickAddServico() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerServicos = (Spinner) getDialog().findViewById(R.id.spinner_servicos);
                Servico servico = (Servico) spinnerServicos.getSelectedItem();

                itemServicoAdapter.addServico(servico);
            }
        };
    }

    private View.OnClickListener onClickTextViewHorario() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance(SessaoDialogFragment.this);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        };
    }

    private View.OnClickListener onClickTextViewData() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(SessaoDialogFragment.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        };
    }

    private DialogInterface.OnClickListener onClickCancelar() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
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
        TextView txtViewHorario = (TextView) getDialog().findViewById(R.id.txt_view_horario);
        txtViewHorario.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        TextView txtViewData = (TextView) getDialog().findViewById(R.id.txt_view_data);
        txtViewData.setText(sdf.format(calendar.getTime()));
    }

    public interface OnAdicionarSessaoListener {
        void add(Sessao sessao, boolean isEditing);
    }
}
