package br.com.beautybox.atendimentos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.beautybox.R;
import br.com.beautybox.domain.ItemServico;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 04/07/16.
 * <p/>
 * Adapter do RecyclerView no SessaoDialogFragment
 */
public class ItemServicoAdapter extends RecyclerView.Adapter<ItemServicoAdapter.MyViewHolder> {

    private final Sessao sessao;

    public ItemServicoAdapter(Sessao sessao) {
        this.sessao = sessao;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_item_servico, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemServico itemServico = sessao.getServicos().get(position);

        holder.txtServico.setText(itemServico.getServico().getDescricao());
        holder.txtQuantidade.setText(String.valueOf(itemServico.getQuantidade()));
        holder.btnAdd.setOnClickListener(onClickAddItemServico(position));
        holder.btnMinus.setOnClickListener(onClickMinusItemServico(position));
    }

    private View.OnClickListener onClickMinusItemServico(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemServico itemServico = sessao.getServicos().get(position);
                int quantidade = itemServico.getQuantidade();

                if (quantidade == 1)
                    sessao.getServicos().remove(position);
                else
                    itemServico.setQuantidade(quantidade - 1);

                notifyDataSetChanged();
            }
        };
    }

    private View.OnClickListener onClickAddItemServico(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemServico itemServico = sessao.getServicos().get(position);
                itemServico.setQuantidade(itemServico.getQuantidade() + 1);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return sessao.getServicos().size();
    }

    public void addServico(Servico servico) {

        ItemServico item = null;

        // verificando se o serviço ja foi adicionado, para apenas
        // incrimentar a quantidade
        for (ItemServico itemServico : sessao.getServicos()) {
            if (itemServico.getServico().getKey().equals(servico.getKey())) {
                item = itemServico;
                break;
            }
        }

        if (item != null)
            item.setQuantidade(item.getQuantidade() + 1);
        else
            sessao.addItemServico(new ItemServico(servico, 1));

        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtServico;
        TextView txtQuantidade;
        ImageButton btnAdd, btnMinus;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtServico = (TextView) itemView.findViewById(R.id.txt_servico);
            txtQuantidade = (TextView) itemView.findViewById(R.id.txt_quantidade);
            btnAdd = (ImageButton) itemView.findViewById(R.id.btn_add);
            btnMinus = (ImageButton) itemView.findViewById(R.id.btn_minus);
        }
    }
}