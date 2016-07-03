package br.com.beautybox.domain;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lsimaocosta on 22/06/16.
 */
@IgnoreExtraProperties
public class Atendimento {

    @Exclude
    public static final transient String FIREBASE_NODE = "atendimentos";

    private String key;
    private int sessoes;
    private long dataHorario;
    private String clienteRef;
    private List<String> servicosRefs;
    private long desconto;
    private String formaPagamento;

    public Atendimento() {
        this.desconto = 0;this.desconto=0;
    }

    public long getDataHorario() {
        return dataHorario;
    }

    public void setDataHorario(long dataHorario) {
        this.dataHorario = dataHorario;
    }

    public String getClienteRef() {
        return clienteRef;
    }

    public void setClienteRef(String clienteRef) {
        this.clienteRef = clienteRef;
    }

    public void addServico(String key){
        if (this.servicosRefs == null)
            this.servicosRefs = new LinkedList<>();

        this.servicosRefs.add(key);
    }

    public int getSessoes() {
        return sessoes;
    }

    public void setSessoes(int sessoes) {
        this.sessoes = sessoes;
    }

    public List<String> getServicosRefs() {
        return servicosRefs;
    }

    public void setServicosRefs(List<String> servicosRefs) {
        this.servicosRefs = servicosRefs;
    }

    public long getDesconto() {
        return desconto;
    }

    public void setDesconto(long desconto) {
        this.desconto = desconto;
    }

    public String getFormaPagamento() {
        return this.formaPagamento;
    }
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Exclude
    public void setFormaPagamento(FormaPagamento formaPagamento) {
        setFormaPagamento((formaPagamento!=null)?formaPagamento.name():null);
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Atendimento{" +
                "dataHorario=" + dataHorario +
                ", clienteRef='" + clienteRef + '\'' +
                ", servicosRefs=" + servicosRefs +
                ", desconto=" + desconto +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }

}
