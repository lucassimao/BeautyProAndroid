package br.com.beautybox.dao;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.beautybox.Util;
import br.com.beautybox.domain.Cliente;

/**
 * Created by lsimaocosta on 21/06/16.
 */
public class ClienteDAO {

    public static final String FIREBASE_NODE = "clientes";

    public static Task<Void> save(Cliente cliente) {
        DatabaseReference ref = Util.databaseRoot().child(FIREBASE_NODE);

        if (TextUtils.isEmpty(cliente.getKey()))
            return ref.push().setValue(cliente);
        else
            return update(ref.child(cliente.getKey()), cliente);
    }

    public static void find(String key, ValueEventListener valueEventListener) {
        DatabaseReference ref = Util.databaseRoot().child(FIREBASE_NODE).child(key);
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    public static Task<Void> delete(DatabaseReference ref) {
        return ref.removeValue();
    }

    public static Task<Void> update(DatabaseReference ref, Cliente cliente) {
        Map<String, Object> map = toMap(cliente);
        return ref.updateChildren(map);
    }

    public static Map<String, Object> toMap(Cliente cliente) {
        Map<String, Object> map = new HashMap<>();
        map.put("nome", cliente.getNome());
        map.put("celular", cliente.getCelular());
        map.put("dddCelular", cliente.getDddCelular());
        map.put("telefone", cliente.getTelefone());
        map.put("dddTelefone", cliente.getDddTelefone());
        if (cliente.getDataNascimento() != null)
            map.put("dataNascimento", cliente.getDataNascimento().getTime());

        return map;
    }

    public static Cliente parseSnapshot(DataSnapshot dataSnapshot) {
        Cliente cliente = dataSnapshot.getValue(Cliente.class);
        cliente.setKey(dataSnapshot.getKey());
        return cliente;
    }
}
