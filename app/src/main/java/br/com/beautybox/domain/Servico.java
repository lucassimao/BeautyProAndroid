package br.com.beautybox.domain;

import com.google.firebase.database.Exclude;

import java.math.BigDecimal;

/**
 * Created by lsimaocosta on 17/06/16.
 */
public class Servico {
    public final static transient String FIREBASE_NODE="servicos";
    private String descricao;
    private long valorAVista, valorAPrazo;


    public Servico() { }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getValorAVista() {
        return valorAVista;
    }

    public void setValorAVista(long valorAVista) {
        this.valorAVista = valorAVista;
    }

    public long getValorAPrazo() {
        return valorAPrazo;
    }

    public void setValorAPrazo(long valorAPrazo) {
        this.valorAPrazo = valorAPrazo;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "descricao='" + descricao + '\'' +
                ", valorAVista=" + valorAVista +
                ", valorAPrazo=" + valorAPrazo +
                '}';
    }

    @Exclude
    public BigDecimal getValorAPrazoEmReais() {
        BigDecimal _100 = BigDecimal.valueOf(100);
        return BigDecimal.valueOf(this.valorAPrazo).divide(_100);
    }

    @Exclude
    public BigDecimal getValorAVistaEmReais() {
        BigDecimal _100 = BigDecimal.valueOf(100);
        return BigDecimal.valueOf(this.valorAVista).divide(_100);
    }
}
