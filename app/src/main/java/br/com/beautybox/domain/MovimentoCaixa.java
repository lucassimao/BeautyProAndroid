package br.com.beautybox.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsimaocosta on 19/07/16.
 */
public class MovimentoCaixa {
    private String key;
    private Map<FormaPagamento, Long> valores;
    private String atendimentoKey;
    private long taxas;
    private boolean positivo;

    public MovimentoCaixa() {
        this.valores = new HashMap<>();
        this.taxas = 0;
        this.positivo = true;
    }

    public String getAtendimentoKey() {
        return atendimentoKey;
    }

    public void setAtendimentoKey(String atendimentoKey) {
        this.atendimentoKey = atendimentoKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTaxas() {
        return taxas;
    }

    public void setTaxas(long taxas) {
        this.taxas = taxas;
    }

    public boolean isPositivo() {
        return positivo;
    }

    public void setPositivo(boolean positivo) {
        this.positivo = positivo;
    }

    public Map<FormaPagamento, Long> getValores() {
        return valores;
    }

    public void setValores(Map<FormaPagamento, Long> valores) {
        this.valores = valores;
    }

    public void addValor(FormaPagamento formaPagamento, long valor) {
        if (valores.containsKey(formaPagamento)) {
            valor += valores.get(formaPagamento);
            valores.put(formaPagamento, valor);
        } else
            valores.put(formaPagamento, valor);
    }

    public long getTotal() {
        long total = 0;
        for (FormaPagamento formaPagamento : valores.keySet())
            total += valores.get(formaPagamento);

        return total;
    }
}
