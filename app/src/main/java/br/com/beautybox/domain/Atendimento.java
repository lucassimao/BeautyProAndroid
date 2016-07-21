package br.com.beautybox.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lsimaocosta on 22/06/16.
 */
public class Atendimento implements Serializable {


    private String key;
    private Cliente cliente;
    private List<Sessao> sessoes;
    private FormaPagamento formaPagamento;
    private long pgmtCartaoDebito, pgmtCartaoCredito, pgmtCartaoCredito1X, pgmtDinheiro, desconto, taxas;
    private Date dateCreated, dateUpdated;

    public Atendimento() {
        this.pgmtCartaoDebito = 0;
        this.pgmtCartaoCredito = 0;
        this.pgmtCartaoCredito1X=0;
        this.pgmtDinheiro = 0;
        this.desconto = 0;
        this.taxas = 0;
        this.sessoes = new LinkedList<>();
        this.cliente = new Cliente();
        this.formaPagamento = FormaPagamento.AVista;
    }

    public long getDesconto() {
        return desconto;
    }

    public void setDesconto(long desconto) {
        this.desconto = desconto;
    }

    public FormaPagamento getFormaPagamento() {
        return this.formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        if (sessoes != null && sessoes.size() > 1)
            Collections.sort(sessoes);

        this.sessoes = sessoes;
    }

    public long getPgmtCartaoCredito1X() {
        return pgmtCartaoCredito1X;
    }

    public void setPgmtCartaoCredito1X(long pgmtCartaoCredito1X) {
        this.pgmtCartaoCredito1X = pgmtCartaoCredito1X;
    }

    public long getPgmtCartaoDebito() {
        return pgmtCartaoDebito;
    }

    public void setPgmtCartaoDebito(long pgmtCartaoDebito) {
        this.pgmtCartaoDebito = pgmtCartaoDebito;
    }

    public long getPgmtCartaoCredito() {
        return pgmtCartaoCredito;
    }

    public void setPgmtCartaoCredito(long pgmtCartaoCredito) {
        this.pgmtCartaoCredito = pgmtCartaoCredito;
    }

    public long getPgmtDinheiro() {
        return pgmtDinheiro;
    }

    public void setPgmtDinheiro(long pgmtDinheiro) {
        this.pgmtDinheiro = pgmtDinheiro;
    }

    public long getTaxas() {
        return taxas;
    }

    public void setTaxas(long taxas) {
        this.taxas = taxas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    public String toString() {
        return "Atendimento{" +
                "key='" + key + '\'' +
                ", cliente=" + cliente +
                ", sessoes=" + sessoes +
                ", formaPagamento=" + formaPagamento +
                ", pgmtCartaoDebito=" + pgmtCartaoDebito +
                ", pgmtCartaoCredito=" + pgmtCartaoCredito +
                ", pgmtDinheiro=" + pgmtDinheiro +
                ", desconto=" + desconto +
                ", taxas=" + taxas +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }

    public long getValorTotal() {
        long total = 0;
        for (Sessao sessao : sessoes)
            total += sessao.getSubTotal(this.formaPagamento);

        return total - desconto;
    }

    public void addSessao(Sessao sessao) {
        this.sessoes.add(sessao);

        if (sessoes.size() > 1)
            Collections.sort(sessoes);
    }
}
