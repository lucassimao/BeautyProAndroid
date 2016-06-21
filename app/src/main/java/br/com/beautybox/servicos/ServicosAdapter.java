package br.com.beautybox.servicos;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;

import br.com.beautybox.R;
import br.com.beautybox.domain.Servico;

/**
 * Created by lsimaocosta on 20/06/16.
 */
public class ServicosAdapter extends FirebaseListAdapter<Servico> {

    public ServicosAdapter(Activity activity, DatabaseReference ref) {
        super(activity, Servico.class,R.layout.list_item_servicos,ref);
    }

    @Override
    protected void populateView(View view, Servico servico, int position) {
        final BigDecimal _100 = BigDecimal.valueOf(100);

        TextView txtServico = (TextView) view.findViewById(R.id.txt_servico);
        txtServico.setText(servico.getDescricao());

        final BigDecimal valorAVistaEmCentavos = BigDecimal.valueOf(servico.getValorAVista());
        TextView txtValorAVista = (TextView) view.findViewById(R.id.txt_valor_a_vista);
        txtValorAVista.setText("R$ " + valorAVistaEmCentavos.divide(_100).toString());

        final BigDecimal valorAPrazoEmCentavos = BigDecimal.valueOf(servico.getValorAPrazo());
        TextView txtValorAPrazo = (TextView) view.findViewById(R.id.txt_valor_a_prazo);
        txtValorAPrazo.setText("R$ " + valorAPrazoEmCentavos.divide(_100).toString());

    }
}
