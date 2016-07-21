package br.com.beautybox.movimentoCaixa;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import br.com.beautybox.R;
import br.com.beautybox.dao.MovimentoCaixaDAO;
import br.com.beautybox.domain.MovimentoCaixa;

/**
 * Created by lsimaocosta on 21/07/16.
 */
public class MovimentoCaixaAdapter extends FirebaseListAdapter<MovimentoCaixa> {

    private SimpleDateFormat sdf = null;

    public MovimentoCaixaAdapter(Activity activity, Query ref) {
        super(activity, MovimentoCaixa.class, R.layout.list_item_movimento_caixa, ref);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    protected void populateView(View v, MovimentoCaixa model, int position) {

        Resources resources = mActivity.getResources();
        int color = 0;

        if (model.isPositivo())
            color = ContextCompat.getColor(mActivity,R.color.movimentoPositivo);
        else
            color = ContextCompat.getColor(mActivity,R.color.movimentoNegativo);

        v.setBackgroundColor(color);

        TextView txtViewDescricao = (TextView) v.findViewById(R.id.txt_descricao);
        TextView txtViewData = (TextView) v.findViewById(R.id.txt_data);
        TextView txtViewValor = (TextView) v.findViewById(R.id.txt_valor);
        double valorTotal = ( model.getTotal() - model.getTaxas()) / 100.0;

        txtViewDescricao.setText(model.getDescricao());
        txtViewData.setText(sdf.format(model.getData()));
        txtViewValor.setText(NumberFormat.getCurrencyInstance().format(valorTotal));
    }

    @Override
    protected MovimentoCaixa parseSnapshot(DataSnapshot snapshot) {
        return MovimentoCaixaDAO.load(snapshot);
    }


}
