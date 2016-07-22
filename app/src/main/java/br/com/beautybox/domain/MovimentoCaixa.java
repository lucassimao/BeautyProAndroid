package br.com.beautybox.domain;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsimaocosta on 19/07/16.
 */
public class MovimentoCaixa {
    private String key;
    private Date data;
    private String descricao;
    private Map<String, Long> valores;
    private String atendimentoKey;
    private long taxas;
    private boolean positivo;

    public MovimentoCaixa() {
        this.valores = new HashMap<>();
        this.taxas = 0;
        this.positivo = true;
        this.data = new Date();
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

    public void setValores(Map<String, Long> valores) {
        this.valores = valores;
    }

    public Map<String, Long> getValores() {
        return Collections.unmodifiableMap(valores);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void addValor(FormaPagamento formaPagamento, long valor) {
        String key = formaPagamento.name();

        if (valores.containsKey(key)) {
            valor += valores.get(key);
            valores.put(key, valor);
        } else
            valores.put(key, valor);
    }

    public long getTotal() {
        long total = 0;
        for (String formaPagamento : valores.keySet())
            total += valores.get(formaPagamento);

        return total;
    }
}
