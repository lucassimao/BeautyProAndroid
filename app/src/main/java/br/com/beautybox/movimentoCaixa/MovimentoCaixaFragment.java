package br.com.beautybox.movimentoCaixa;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.beautybox.DatePickerFragment;
import br.com.beautybox.R;
import br.com.beautybox.domain.MovimentoCaixa;

/**
 * Created by lsimaocosta on 21/07/16.
 */
public class MovimentoCaixaFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private MovimentoCaixa movimentoCaixa;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    public static MovimentoCaixaFragment newInstance(MovimentoCaixa movimentoCaixa) {

        MovimentoCaixaFragment fragment = new MovimentoCaixaFragment();
        if (movimentoCaixa == null)
            fragment.movimentoCaixa = new MovimentoCaixa();
        else
            fragment.movimentoCaixa = movimentoCaixa;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimento_caixa, container, false);

        EditText editDescricao = (EditText) view.findViewById(R.id.edit_descricao);
        TextView textView = (TextView) view.findViewById(R.id.text_data);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

        Button btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        Button btnSalvar = (Button) view.findViewById(R.id.btn_salvar);

        editDescricao.setText(movimentoCaixa.getDescricao());
        textView.setText(sdf.format(movimentoCaixa.getData()));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(MovimentoCaixaFragment.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        if (movimentoCaixa.isPositivo())
            radioGroup.check(R.id.radio_entrada);
        else
            radioGroup.check(R.id.radio_saida);

        btnCancelar.setOnClickListener(onClickCancelar());
        btnSalvar.setOnClickListener(onClickSalvar());


        return view;
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getView();
                EditText editDescricao = (EditText) view.findViewById(R.id.edit_descricao);
                TextView textView = (TextView) view.findViewById(R.id.text_data);
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

                movimentoCaixa.setDescricao(editDescricao.getText().toString());
                boolean isPositivo = radioGroup.getCheckedRadioButtonId() == R.id.radio_entrada;
                movimentoCaixa.setPositivo(isPositivo);
                try {
                    Date dt = sdf.parse(textView.getText().toString());
                    movimentoCaixa.setData(dt);
                } catch (ParseException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
            }
        };
    }

    private View.OnClickListener onClickCancelar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        };
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        TextView textView = (TextView) getView().findViewById(R.id.text_data);
        Date dt = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();

        textView.setText(sdf.format(dt));

    }
}
