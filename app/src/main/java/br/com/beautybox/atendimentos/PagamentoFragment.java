package br.com.beautybox.atendimentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;

/**
 * Created by lsimaocosta on 22/06/16.
 */

public class PagamentoFragment extends Fragment implements AtendimentoTabListener {

    private static final String TAG = PagamentoFragment.class.getSimpleName();
    private Atendimento atendimento;

    public static PagamentoFragment newInstance(Atendimento atendimento) {
        PagamentoFragment fragment = new PagamentoFragment();
        fragment.atendimento = atendimento;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagamento, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editCartaoDebito = (EditText) getView().findViewById(R.id.edit_cartao_debito);
        EditText editCartaoCredito = (EditText) getView().findViewById(R.id.edit_cartao_credito);
        EditText editCartaoCredito1X = (EditText) getView().findViewById(R.id.edit_cartao_credito_1x);
        EditText editDinheiro = (EditText) getView().findViewById(R.id.edit_dinheiro);
        EditText editDesconto = (EditText) getView().findViewById(R.id.edit_desconto);
        EditText editTaxas = (EditText) getView().findViewById(R.id.edit_taxas);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        editCartaoDebito.setText(nf.format(atendimento.getPgmtCartaoDebito()/100.0));
        editCartaoCredito.setText(nf.format(atendimento.getPgmtCartaoCredito()/100.0));
        editDinheiro.setText(nf.format(atendimento.getPgmtDinheiro()/100.0));
        editDesconto.setText(nf.format(atendimento.getDesconto()/100.0));
        editCartaoCredito1X.setText(nf.format(atendimento.getPgmtCartaoCredito1X()/100.0));
        editTaxas.setText(nf.format(atendimento.getTaxas()/100.0));
    }

    @Override
    public void writeChanges() {
        EditText editCartaoDebito = (EditText) getView().findViewById(R.id.edit_cartao_debito);
        EditText editCartaoCredito = (EditText) getView().findViewById(R.id.edit_cartao_credito);
        EditText editCartaoCredito1X = (EditText) getView().findViewById(R.id.edit_cartao_credito_1x);
        EditText editDinheiro = (EditText) getView().findViewById(R.id.edit_dinheiro);
        EditText editDesconto = (EditText) getView().findViewById(R.id.edit_desconto);
        EditText editTaxas = (EditText) getView().findViewById(R.id.edit_taxas);

        BigDecimal _100 = BigDecimal.valueOf(100);

        long pgmtCartaoDebito = new BigDecimal(editCartaoDebito.getText().toString()).multiply(_100).longValue();
        atendimento.setPgmtCartaoDebito(pgmtCartaoDebito);

        long pgmtCartaoCredito = new BigDecimal(editCartaoCredito.getText().toString()).multiply(_100).longValue();
        atendimento.setPgmtCartaoCredito(pgmtCartaoCredito);

        long pgmtCartaoCredito1X = new BigDecimal(editCartaoCredito1X.getText().toString()).multiply(_100).longValue();
        atendimento.setPgmtCartaoCredito1X(pgmtCartaoCredito1X);

        long pgmtDinheiro = new BigDecimal(editDinheiro.getText().toString()).multiply(_100).longValue();
        atendimento.setPgmtDinheiro(pgmtDinheiro);

        long desconto = new BigDecimal(editDesconto.getText().toString()).multiply(_100).longValue();
        atendimento.setDesconto(desconto);

        long taxas = new BigDecimal(editTaxas.getText().toString()).multiply(_100).longValue();
        atendimento.setTaxas(taxas);
    }

    @Override
    public boolean validate() {

        EditText editCartaoDebito = (EditText) getView().findViewById(R.id.edit_cartao_debito);
        try {
            new BigDecimal(editCartaoDebito.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor do pagamento no cartão de débito", Toast.LENGTH_SHORT).show();
            editCartaoDebito.requestFocus();
            return false;
        }

        EditText editCartaoCredito = (EditText) getView().findViewById(R.id.edit_cartao_credito);
        try {
            new BigDecimal(editCartaoCredito.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor do pagamento no cartão de crédito", Toast.LENGTH_SHORT).show();
            editCartaoCredito.requestFocus();
            return false;
        }

        EditText editCartaoCredito1X = (EditText) getView().findViewById(R.id.edit_cartao_credito_1x);
        try {
            new BigDecimal(editCartaoCredito1X.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor do pagamento no cartão 1X", Toast.LENGTH_SHORT).show();
            editCartaoCredito1X.requestFocus();
            return false;
        }

        EditText editDinheiro = (EditText) getView().findViewById(R.id.edit_dinheiro);
        try {
            new BigDecimal(editDinheiro.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor do pagamento em dinheiro", Toast.LENGTH_SHORT).show();
            editDinheiro.requestFocus();
            return false;

        }

        EditText editDesconto = (EditText) getView().findViewById(R.id.edit_desconto);
        try {
            new BigDecimal(editDesconto.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor do desconto", Toast.LENGTH_SHORT).show();
            editDesconto.requestFocus();
            return false;

        }

        EditText editTaxas = (EditText) getView().findViewById(R.id.edit_taxas);
        try {
            new BigDecimal(editTaxas.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Informe o valor das taxas", Toast.LENGTH_SHORT).show();
            editTaxas.requestFocus();
            return false;
        }

        return true;
    }

}
