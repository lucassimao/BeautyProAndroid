package br.com.beautybox.atendimentos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.ItemServico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 20/06/16
 * <p/>
 * Adapter de serviços utilizado pelo RecyclerView de AtendimentosListFragment
 */
public class SessoesAdapter extends RecyclerView.Adapter<SessoesAdapter.MyViewHolder> {

    private static final String TAG = SessoesAdapter.class.getSimpleName();

    private final Atendimento atendimento;
    private final NumberFormat numberFormat;
    private final Context ctx;
    private final SimpleDateFormat sdf;
    private final OnEditSessaoListener onEditSessaoListener;

    public SessoesAdapter(Context ctx, Atendimento atendimento, OnEditSessaoListener onEditSessaoListener) {
        super();
        this.numberFormat = NumberFormat.getCurrencyInstance();
        this.ctx = ctx;
        this.atendimento = atendimento;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy - H:mm");
        this.onEditSessaoListener = onEditSessaoListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.list_item_item_sessao, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sessao sessao = atendimento.getSessoes().get(position);

        holder.txtDataHorario.setText(sdf.format(new Date(sessao.getTimestamp())));

        StringBuilder stringBuilder = new StringBuilder();
        for (ItemServico itemServico : sessao.getServicos())
            stringBuilder.append(itemServico.getQuantidade() + " " + itemServico.getServico().getDescricao() + "\n");

        holder.txtServicos.setText(stringBuilder.toString());
        holder.txtSubTotal.setText(numberFormat.format(sessao.getSubTotal(atendimento.getFormaPagamento()) / 100.0));

        holder.btnEdit.setOnClickListener(onClickEditSessao(position));
        holder.btnDelete.setOnClickListener(onClickDeleteSessao(position));

    }

    private View.OnClickListener onClickDeleteSessao(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atendimento.getSessoes().remove(position);
                notifyDataSetChanged();
            }
        };
    }

    private View.OnClickListener onClickEditSessao(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditSessaoListener != null) {
                    Sessao sessao = atendimento.getSessoes().get(position);
                    onEditSessaoListener.edit(sessao);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return atendimento.getSessoes().size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDataHorario;
        TextView txtServicos, txtSubTotal;
        ImageButton btnDelete, btnEdit;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtDataHorario = (TextView) itemView.findViewById(R.id.txt_data_horario);
            txtServicos = (TextView) itemView.findViewById(R.id.txt_servicos);
            txtSubTotal = (TextView) itemView.findViewById(R.id.txt_subTotal);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btn_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);
        }
    }


    public interface OnEditSessaoListener {
        void edit(Sessao sessao);
    }

}
