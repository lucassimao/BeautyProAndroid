package br.com.beautybox.atendimentos;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.FormaPagamento;
import br.com.beautybox.domain.Servico;

/**
 * Created by lsimaocosta on 20/06/16.
 * <p/>
 * Adapter de serviços utilizado pelo ListView no
 * cadastro de atendimentos
 */
public class ServicosAdapter extends FirebaseListAdapter<Servico> {

    private static final String TAG = ServicosAdapter.class.getSimpleName();

    private final NumberFormat numberFormat;
    private Set<String> servicosSelecionados;
    private FormaPagamento formaPagamento;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private OnServicoClickListener onServicoClickListener;

    public ServicosAdapter(Activity activity, Query ref, Atendimento atendimento) {
        super(activity, Servico.class, R.layout.fragment_atendimentos_list_item_servicos, ref);
        formaPagamento = FormaPagamento.AVista;
        this.numberFormat = NumberFormat.getCurrencyInstance();
        onCheckedChangeListener = onCheckBoxClick();

        this.servicosSelecionados = new HashSet<>();
        if (atendimento != null) {
            for (String str : atendimento.getServicosRefs())
                servicosSelecionados.add(str);
        }
    }

    @Override
    protected void populateView(View view, Servico servico, int position) {

        TextView txtServico = (TextView) view.findViewById(R.id.txt_servico);
        txtServico.setText(servico.getDescricao());

        TextView txtValor = (TextView) view.findViewById(R.id.txt_valor);
        if (formaPagamento.equals(FormaPagamento.AVista))
            txtValor.setText(numberFormat.format(servico.getValorAVistaEmReais()));
        else
            txtValor.setText(numberFormat.format(servico.getValorAPrazoEmReais()));

        String key = getRef(position).getKey();

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setTag(position);
        checkBox.setChecked(servicosSelecionados.contains(key));
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onCheckBoxClick() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) buttonView.getTag();
                DatabaseReference ref = getRef(position);
                String key = ref.getKey();

                if (isChecked)
                    servicosSelecionados.add(key);
                else
                    servicosSelecionados.remove(key);

                if (onServicoClickListener != null)
                    onServicoClickListener.onClick(getItem(position), ref, isChecked);
            }
        };
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
        notifyDataSetChanged();
    }

    public void setOnServicoClickListener(OnServicoClickListener onServicoClickListener) {
        this.onServicoClickListener = onServicoClickListener;
    }

    public Set<String> getServicosSelecionados() {
        return servicosSelecionados;
    }

    public interface OnServicoClickListener {
        void onClick(Servico servico, DatabaseReference ref, boolean isChecked);
    }
}
