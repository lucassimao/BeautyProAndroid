package br.com.beautybox.servicos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;

import br.com.beautybox.MainActivity;
import br.com.beautybox.R;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.dao.ServicoDAO;

/**
 * Created by lsimaocosta on 20/06/16.
 */
public class ServicoFragment extends Fragment implements OnCompleteListener<Void> {

    private ProgressDialog progressDialog;
    private Servico servico;
    private DatabaseReference ref;
    private BigDecimal _100 = BigDecimal.valueOf(100);


    public static ServicoFragment newInstance(Servico servico, DatabaseReference ref) {
        ServicoFragment fragment = new ServicoFragment();
        fragment.servico = servico;
        fragment.ref = ref;
        return fragment;
    }

    public static ServicoFragment newInstance() {
        return newInstance(null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_servico, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        btnCancelar.setOnClickListener(onClickBtnCancelar());

        Button btnSalvar = (Button) view.findViewById(R.id.btn_salvar);
        btnSalvar.setOnClickListener(onClickBtnSalvar());

        EditText editDescricao = (EditText) view.findViewById(R.id.edit_descricao);
        editDescricao.requestFocus();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        // vai entrar no modo edição
        if (servico != null) {
            EditText editValorAVista = (EditText) view.findViewById(R.id.edit_valor_a_vista);
            EditText editValorAPrazo = (EditText) view.findViewById(R.id.edit_valor_a_prazo);
            EditText editQtdeSessoes = (EditText) view.findViewById(R.id.edit_qtde_sessoes);


            editDescricao.setText(servico.getDescricao());
            editQtdeSessoes.setText(String.valueOf(servico.getQtdeSessoes()));
            // dividindo os valores por 100 pq sao representados em centavos
            editValorAVista.setText(BigDecimal.valueOf(servico.getValorAVista()).divide(_100).toString());
            editValorAPrazo.setText(BigDecimal.valueOf(servico.getValorAPrazo()).divide(_100).toString());

            toolbar.setTitle("Editar serviço");
        } else {
            toolbar.setTitle("Cadastrar novo serviço");
        }

        ((MainActivity) getActivity()).hideDrawer();
    }

    private View.OnClickListener onClickBtnSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Servico servico = new Servico();
                View form = getView();
                EditText editDescricao = (EditText) form.findViewById(R.id.edit_descricao);

                String descricao = editDescricao.getText().toString();
                if (TextUtils.isEmpty(descricao)) {
                    Toast.makeText(getActivity(), "Informe a descrição do serviço", Toast.LENGTH_SHORT).show();
                    editDescricao.requestFocus();
                    return;
                }
                servico.setDescricao(descricao);

                EditText editValorAVista = (EditText) form.findViewById(R.id.edit_valor_a_vista);
                try {
                    BigDecimal valorAVista = new BigDecimal(editValorAVista.getText().toString());
                    servico.setValorAVista(valorAVista.multiply(_100).longValue());
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Informe o valor a vista", Toast.LENGTH_SHORT).show();
                    editValorAVista.requestFocus();
                    return;
                }

                EditText editValorAPrazo = (EditText) form.findViewById(R.id.edit_valor_a_prazo);
                try {
                    BigDecimal valorAPrazo = new BigDecimal(editValorAPrazo.getText().toString());
                    servico.setValorAPrazo(valorAPrazo.multiply(_100).longValue());
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Informe o valor a prazo", Toast.LENGTH_SHORT).show();
                    editValorAPrazo.requestFocus();
                    return;
                }

                EditText editQtdeSessoes = (EditText) form.findViewById(R.id.edit_qtde_sessoes);
                try {

                    Integer qtdeSessoes = Integer.valueOf(editQtdeSessoes.getText().toString());
                    servico.setQtdeSessoes(qtdeSessoes);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Informe a quantidade de sessões", Toast.LENGTH_SHORT).show();
                    editQtdeSessoes.requestFocus();
                    return;
                }


                progressDialog = ProgressDialog.show(getActivity(), "Aguarde", "Salvando novo serviço ...", true, false);
                Task<Void> task;

                if (ref == null)
                    task = ServicoDAO.save(servico);
                else
                    task = ServicoDAO.update(ref, servico);

                task.addOnCompleteListener(getActivity(), ServicoFragment.this);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        //Ocultando o telcado que eventualmente esteja aberto
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        ((MainActivity) getActivity()).showDrawer();
    }

    private View.OnClickListener onClickBtnCancelar() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        };
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        progressDialog.dismiss();

        if (task.isSuccessful()) {
            String text = null;

            if (ref != null && servico != null)
                text = "Serviço atualizado com sucesso";
            else
                text = "Serviço salvo com sucesso";

            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().popBackStack();
        } else
            Toast.makeText(getActivity(), "Erro :" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

    }


}
