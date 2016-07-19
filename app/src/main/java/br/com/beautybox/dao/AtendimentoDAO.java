package br.com.beautybox.dao;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.beautybox.DatabaseUtil;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;
import br.com.beautybox.domain.FormaPagamento;
import br.com.beautybox.domain.ItemServico;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 25/06/16.
 */
public class AtendimentoDAO {

    public static final transient String FIREBASE_NODE = "atendimentos";
    private static final String TAG = AtendimentoDAO.class.getSimpleName();

    /**
     * os atendimentos são organizados a partir de "buckets" que
     * representam organizam os atendimentos por mes e ano
     *
     * @return
     */
    public static final String getCurrentBucket() {
        return DatabaseUtil.date2Bucket(new Date());
    }


    /**
     * Lista os atendimentos do bucket atual
     *
     * @return
     */
    public static Query list() {
        return list(null);
    }

    public static Query list(ValueEventListener valueEventListener) {
        String currentBucket = getCurrentBucket();

        Query query = DatabaseUtil.root().child(FIREBASE_NODE).orderByKey().startAt(currentBucket);
        if (valueEventListener != null)
            query.addListenerForSingleValueEvent(valueEventListener);

        return query;

    }

    public static Task<Void> save(Atendimento atendimento) {
        DatabaseReference instance = DatabaseUtil.root();

        Map<String, Object> childUpdates = new HashMap<>();
        String clienteKey = atendimento.getCliente().getKey();

        if (TextUtils.isEmpty(clienteKey)) {
            clienteKey = instance.child(ClienteDAO.FIREBASE_NODE).push().getKey();
            atendimento.getCliente().setKey(clienteKey);
        }

        String clientePath = String.format("/%s/%s", ClienteDAO.FIREBASE_NODE, clienteKey);
        childUpdates.put(clientePath, ClienteDAO.toMap(atendimento.getCliente()));

        if (atendimento.getDateCreated() == null)
            atendimento.setDateCreated(new Date());
        else
            atendimento.setDateUpdated(new Date());

        String bucket = DatabaseUtil.date2Bucket(atendimento.getDateCreated());
        String atendimentoKey = atendimento.getKey();

        if (TextUtils.isEmpty(atendimentoKey))
            atendimentoKey = instance.child(FIREBASE_NODE).child(bucket).push().getKey();

        String atendimentoPath = String.format("/%s/%s/%s", FIREBASE_NODE, bucket, atendimentoKey);

        childUpdates.put(atendimentoPath, toMap(atendimento));

        return instance.updateChildren(childUpdates);
    }

    public static Task<Void> delete(DatabaseReference ref) {
        return ref.removeValue();
    }


    public static Atendimento load(DataSnapshot child) {
        Map<String, Object> map = (Map<String, Object>) child.getValue();
        Atendimento atendimento = new Atendimento();

        atendimento.setKey(child.getKey());
        Cliente cliente = new Cliente();

        if (map.containsKey("clienteRef")) {
            FirebaseCrash.log("atendimento " + child.getKey() + " ainda utiliza a propriedade clienteRef :: Excluir ::");
            cliente.setKey(map.get("clienteRef").toString());
        } else
            cliente.setKey(map.get("clienteKey").toString());

        atendimento.setCliente(cliente);

        List<Sessao> sessoes = new LinkedList<>();

        if (map.containsKey("dataHorario") && map.containsKey("servicosRefs")) {
            FirebaseCrash.log("atendimento " + child.getKey() + " ainda possui a propriedade dataHorario e servicosRefs :: Excluir ::");

            Sessao sessao = new Sessao((Long) map.get("dataHorario"), atendimento);

            List<String> servicosRefs = (List<String>) map.get("servicosRefs");
            for (String key : servicosRefs) {
                Servico servico = new Servico(key);
                ItemServico itemServico = new ItemServico(servico, 1);

                sessao.addItemServico(itemServico);
            }

            sessoes.add(sessao);
        } else
            sessoes = load((List<Map<String, Object>>) map.get("sessoes"), atendimento);

        atendimento.setSessoes(sessoes);

        atendimento.setFormaPagamento(FormaPagamento.valueOf(map.get("formaPagamento").toString()));

        if (map.containsKey("pgmtCartaoDebito"))
            atendimento.setPgmtCartaoDebito((Long) map.get("pgmtCartaoDebito"));

        if (map.containsKey("pgmtCartaoCredito"))
            atendimento.setPgmtCartaoCredito((Long) map.get("pgmtCartaoCredito"));

        if (map.containsKey("pgmtDinheiro"))
            atendimento.setPgmtDinheiro((Long) map.get("pgmtDinheiro"));

        if (map.containsKey("desconto"))
            atendimento.setDesconto((Long) map.get("desconto"));

        if (map.containsKey("taxas"))
            atendimento.setTaxas((Long) map.get("taxas"));

        if (map.containsKey("dateCreated")) {
            atendimento.setDateCreated(new Date((Long) map.get("dateCreated")));
        } else {
            FirebaseCrash.log(child.getKey() + " ainda não possui dateCreated");
            atendimento.setDateCreated(new Date());
        }

        if (map.containsKey("dateUpdated"))
            atendimento.setDateUpdated(new Date((Long) map.get("dateUpdated")));


        return atendimento;
    }

    private static List<Sessao> load(List<Map<String, Object>> sessoes, Atendimento atendimento) {
        List<Sessao> sessaoList = new ArrayList<>();

        for (Map<String, Object> map : sessoes) {
            Sessao sessao = new Sessao();
            sessao.setAtendimento(atendimento);

            sessao.setTimestamp((Long) map.get("timestamp"));

            List<Map<String, Object>> itens = (List<Map<String, Object>>) map.get("itens");

            for (Map<String, Object> item : itens) {
                Servico servico = new Servico(item.get("servicoKey").toString());

                ItemServico itemServico = new ItemServico();
                itemServico.setServico(servico);
                itemServico.setQuantidade(Integer.valueOf(item.get("quantidade").toString()));
                itemServico.setValorAVista((Long) item.get("valorAVista"));
                itemServico.setValorAPrazo((Long) item.get("valorAPrazo"));

                sessao.addItemServico(itemServico);
            }

            sessaoList.add(sessao);
        }

        return sessaoList;
    }

    private static Map<String, Object> toMap(Atendimento atendimento) {
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> sessoes = new LinkedList<>();
        for (Sessao sessao : atendimento.getSessoes())
            sessoes.add(toMap(sessao));

        map.put("sessoes", sessoes);
        map.put("clienteKey", atendimento.getCliente().getKey());
        map.put("formaPagamento", atendimento.getFormaPagamento().name());
        map.put("pgmtCartaoDebito", atendimento.getPgmtCartaoDebito());
        map.put("pgmtCartaoCredito", atendimento.getPgmtCartaoCredito());
        map.put("pgmtDinheiro", atendimento.getPgmtDinheiro());
        map.put("desconto", atendimento.getDesconto());
        map.put("taxas", atendimento.getTaxas());
        map.put("dateCreated", atendimento.getDateCreated().getTime());

        if (atendimento.getDateUpdated() != null)
            map.put("dateUpdated", atendimento.getDateUpdated().getTime());

        //excluindo propriedades da versão anterior
        map.put("dataHorario", null);
        map.put("clienteRef", null);
        map.put("servicosRefs", null);

        return map;
    }

    private static Map<String, Object> toMap(Sessao sessao) {
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> itens = new LinkedList<>();
        for (ItemServico itemServico : sessao.getServicos())
            itens.add(toMap(itemServico));

        map.put("itens", itens);
        map.put("timestamp", sessao.getTimestamp());

        return map;
    }

    private static Map<String, Object> toMap(ItemServico itemServico) {
        Map<String, Object> map = new HashMap<>();

        map.put("valorAPrazo", itemServico.getValorAPrazo());
        map.put("valorAVista", itemServico.getValorAVista());
        map.put("servicoKey", itemServico.getServico().getKey());
        map.put("quantidade", itemServico.getQuantidade());

        return map;
    }

    public static DatabaseReference getRef(Atendimento atendimento) {
        String bucket = DatabaseUtil.date2Bucket(atendimento.getDateCreated());
        return DatabaseUtil.root().child(FIREBASE_NODE).child(bucket).child(atendimento.getKey()).getRef();
    }

}
