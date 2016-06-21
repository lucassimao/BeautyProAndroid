package br.com.beautybox.domain;

/**
 * Created by lsimaocosta on 17/06/16.
 */
public class Servico {
    public final static transient String FIREBASE_NODE="servicos";
    private String descricao;
    private long valorAVista, valorAPrazo;


    public Servico() {
    }

    public Servico(String descricao, long valorAVista, long valorAPrazo) {
        this.descricao = descricao;
        this.valorAVista = valorAVista;
        this.valorAPrazo = valorAPrazo;
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

    @Override
    public String toString() {
        return "Servico{" +
                "descricao='" + descricao + '\'' +
                ", valorAVista=" + valorAVista +
                ", valorAPrazo=" + valorAPrazo +
                '}';
    }
}
