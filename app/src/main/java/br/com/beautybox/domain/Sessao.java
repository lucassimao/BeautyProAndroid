package br.com.beautybox.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsimaocosta on 06/07/16.
 */
public class Sessao implements Comparable<Sessao>,Serializable {

    private Atendimento atendimento;
    private Long timestamp;
    private List<ItemServico> servicos;

    public Sessao(Long timestamp,Atendimento atendimento) {
        this.timestamp = timestamp;
        this.atendimento = atendimento;
        this.servicos = new ArrayList<>();
    }

    public Sessao() {
        this(System.currentTimeMillis(),null);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ItemServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<ItemServico> servicos) {
        this.servicos = servicos;
    }

    @Override
    public int compareTo(Sessao another) {
        long rhsDataHorario = another.getTimestamp();
        return (timestamp < rhsDataHorario) ? -1 : (timestamp == rhsDataHorario ? 0 : 1);
    }


    public void addItemServico(ItemServico itemServico) {
        if (this.servicos == null)
            this.servicos = new ArrayList<>();

        this.servicos.add(itemServico);
    }

    public long getSubTotal(FormaPagamento formaPagamento) {
        long subTotal = 0;

        for (ItemServico itemServico : servicos) {
            if (formaPagamento.equals(FormaPagamento.AVista))
                subTotal += itemServico.getValorAVista()*itemServico.getQuantidade();
            else
                subTotal += itemServico.getValorAPrazo()*itemServico.getQuantidade();
        }

        return subTotal;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }
}
