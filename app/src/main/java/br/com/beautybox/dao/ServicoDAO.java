package br.com.beautybox.dao;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.beautybox.domain.Servico;

/**
 * Created by lsimaocosta on 21/06/16.
 */
public class ServicoDAO {

    private static final String TAG = ServicoDAO.class.getSimpleName();
    public final static transient String FIREBASE_NODE = "servicos";


    public static Task<Void> save(Servico servico) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FIREBASE_NODE);
        return ref.push().setValue(servico);
    }

    public static Task<Void> delete(DatabaseReference ref) {
        return ref.removeValue();
    }

    public static Task<Void> update(DatabaseReference ref, Servico servico) {
        Log.d(TAG, ref.toString());

        Map<String, Object> map = new HashMap<>();
        map.put("descricao", servico.getDescricao());
        map.put("valorAVista", servico.getValorAVista());
        map.put("valorAPrazo", servico.getValorAPrazo());
        map.put("qtdeSessoes", servico.getQtdeSessoes());

        return ref.updateChildren(map);
    }

    public static Query list() {
        return list(null);
    }

    public static Query list(ValueEventListener valueEventListener) {
        Query query = FirebaseDatabase.getInstance().getReference(FIREBASE_NODE).orderByChild("descricao");

        if (valueEventListener != null)
            query.addListenerForSingleValueEvent(valueEventListener);

        return query;
    }

    public static Servico load(DataSnapshot child) {

        Map<String,Object> map = (Map<String, Object>) child.getValue();
        Servico servico = new Servico();

        servico.setKey(child.getKey());

        if (map.containsKey("qtdeSessoes")) {
            Integer qtdeSessoes = Integer.valueOf(map.get("qtdeSessoes").toString());
            servico.setQtdeSessoes(qtdeSessoes);
        }
        else
            servico.setQtdeSessoes(1);

        servico.setDescricao((String) map.get("descricao"));
        servico.setValorAPrazo((Long) map.get("valorAPrazo"));
        servico.setValorAVista((Long) map.get("valorAVista"));

        return  servico;
    }
}
