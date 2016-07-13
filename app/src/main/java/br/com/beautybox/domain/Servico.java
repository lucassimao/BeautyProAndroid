package br.com.beautybox.domain;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by lsimaocosta on 17/06/16.
 */
public class Servico implements Serializable {

    private String key;
    private String descricao;
    private long valorAVista, valorAPrazo;
    private int qtdeSessoes;


    public Servico() {
        this.valorAVista = 0;
        this.valorAPrazo = 0;
        this.qtdeSessoes = 1;
    }

    public Servico(String key) {
        this.key = key;
    }

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

    public int getQtdeSessoes() {
        return qtdeSessoes;
    }

    public void setQtdeSessoes(int qtdeSessoes) {
        this.qtdeSessoes = qtdeSessoes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return descricao;
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
