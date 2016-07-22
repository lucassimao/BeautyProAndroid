package br.com.beautybox.dao;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.beautybox.DatabaseUtil;
import br.com.beautybox.domain.MovimentoCaixa;

/**
 * Created by lsimaocosta on 19/07/16.
 */
public class MovimentoCaixaDAO {

    private static final String FIREBASE_NODE = "caixas";

    public static DatabaseReference getCurrent() {
        DatabaseReference root = DatabaseUtil.root();
        String caixaAtual = DatabaseUtil.date2Bucket(new Date());

        return root.child(FIREBASE_NODE).child(caixaAtual);
    }

    public static Query list(){
        return getCurrent().orderByChild("data");
    }

    public static Task<Void> save(MovimentoCaixa movimentoCaixa){
        DatabaseReference currentCaixaRoot = getCurrent();
        DatabaseReference path = null;

        if (TextUtils.isEmpty(movimentoCaixa.getKey())){
            path = currentCaixaRoot.push();
        }else
            path = currentCaixaRoot.child(movimentoCaixa.getKey());

        Map<String,Object> map = toMap(movimentoCaixa);
        return path.setValue(map);
    }

    public static Task<Void> delete(MovimentoCaixa movimentoCaixa){
        DatabaseReference currentCaixaRoot = getCurrent();
        DatabaseReference path = currentCaixaRoot.child(movimentoCaixa.getKey());
        return path.removeValue();
    }

    private static Map<String, Object> toMap(MovimentoCaixa movimentoCaixa) {

        Map<String,Object> map = new HashMap<>();
        map.put("valores",movimentoCaixa.getValores());
        map.put("descricao",movimentoCaixa.getDescricao());
        map.put("atendimentoKey",movimentoCaixa.getAtendimentoKey());
        map.put("taxas",movimentoCaixa.getTaxas());
        map.put("positivo",movimentoCaixa.isPositivo());
        map.put("data",movimentoCaixa.getData().getTime());
        return map;
    }

    public static MovimentoCaixa load(DataSnapshot dataSnapshot){
        Map<String,Object> obj = (Map<String, Object>) dataSnapshot.getValue();

        MovimentoCaixa movimentoCaixa = new MovimentoCaixa();
        movimentoCaixa.setKey(dataSnapshot.getKey());
        movimentoCaixa.setValores((Map<String, Long>) obj.get("valores"));
        movimentoCaixa.setDescricao(String.valueOf(obj.get("descricao")));
        movimentoCaixa.setAtendimentoKey(String.valueOf(obj.get("atendimentoKey")));
        movimentoCaixa.setTaxas(Long.valueOf(obj.get("taxas").toString()));
        movimentoCaixa.setPositivo(Boolean.valueOf(obj.get("positivo").toString()));

        Date dt = new Date(Long.valueOf(obj.get("data").toString()));
        movimentoCaixa.setData(dt);

        return movimentoCaixa;
    }


}
