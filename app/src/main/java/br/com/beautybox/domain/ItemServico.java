package br.com.beautybox.domain;

import java.io.Serializable;

/**
 * Created by lsimaocosta on 08/07/16.
 */
public class ItemServico implements Serializable {

    private long valorAPrazo;
    private long valorAVista;
    private Servico servico;
    private int quantidade;

    public ItemServico(Servico servico, int quantidade,long valorAPrazo,long valorAVista) {
        this.servico = servico;
        this.quantidade = quantidade;
        this.valorAPrazo = valorAPrazo;
        this.valorAVista = valorAVista;
    }

    public ItemServico(Servico servico, int quantidade) {
        this(servico,quantidade,servico.getValorAPrazo(),servico.getValorAVista());
    }

    public ItemServico() {
        this(null,0,0,0);
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public long getValorAPrazo() {
        return valorAPrazo;
    }

    public long getValorAVista() {
        return valorAVista;
    }

    public void setValorAPrazo(long valorAPrazo) {
        this.valorAPrazo = valorAPrazo;
    }

    public void setValorAVista(long valorAVista) {
        this.valorAVista = valorAVista;
    }
}
