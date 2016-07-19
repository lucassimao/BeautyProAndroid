package br.com.beautybox.domain;

/**
 * Created by lsimaocosta on 22/06/16.
 */
public enum FormaPagamento {
    AVista("À vista"),APrazo("À prazo"),
    Debito("Débito"),APrazo1X("À prazo 1x");

    private final String str;

    FormaPagamento(String str) {
        this.str=str;
    }

    @Override
    public String toString() {
        return str;
    }
}
