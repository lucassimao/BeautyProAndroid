package br.com.beautybox.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.beautybox.domain.Servico;

/**
 * Created by lsimaocosta on 21/06/16.
 */
public class ServicoService {

    public static Task<Void> save(Servico servico){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Servico.FIREBASE_NODE);
        return ref.push().setValue(servico);
    }

    public static Task<Void> delete(DatabaseReference ref) {
        return ref.removeValue();
    }

    public static Task<Void> update(DatabaseReference ref, Servico servico) {
        Map<String, Object> map = new HashMap<>();
        map.put("descricao",servico.getDescricao());
        map.put("valorAVista",servico.getValorAVista());
        map.put("valorAPrazo",servico.getValorAPrazo());
        return ref.updateChildren(map);
    }
}
