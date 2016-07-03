package br.com.beautybox.service;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;

/**
 * Created by lsimaocosta on 25/06/16.
 */
public class AtendimentoService {

    private static final String TAG = AtendimentoService.class.getSimpleName();

    /**
     * os atendimentos são organizados a partir de "buckets" que
     * representam organizam os atendimentos por mes e ano
     *
     * @return
     */
    public static final String getCurrentBucket() {
        return date2Bucket(new Date());
    }

    public static String timestamp2Bucket(long milliseconds) {
        return date2Bucket(new Date(milliseconds));
    }

    /**
     * Dado uma data, determina o timestamp do bucket em que o objeto
     * requisitante deve ser armazenado
     *
     * @param date
     * @return timestamp
     */
    public static final String date2Bucket(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");
        Calendar c = GregorianCalendar.getInstance(timeZone);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.clear(Calendar.HOUR_OF_DAY);
        c.clear(Calendar.HOUR);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        return String.valueOf(c.getTimeInMillis());
    }

    public static Task<Void> save(Atendimento atendimento, Cliente cliente) {
        DatabaseReference instance = FirebaseDatabase.getInstance().getReference();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> childUpdates = new HashMap<>();
        String clienteKey = cliente.getKey();

        if (TextUtils.isEmpty(clienteKey)) {
            clienteKey = instance.child(Cliente.FIREBASE_NODE).push().getKey();
            atendimento.setClienteRef(clienteKey);
        }

        String clientePath = String.format("/%s/%s",Cliente.FIREBASE_NODE,clienteKey);
        childUpdates.put(clientePath, objectMapper.convertValue(cliente, Map.class));

        long dataHorario = atendimento.getDataHorario();
        String bucket = date2Bucket(new Date(dataHorario));
        String atendimentoKey = atendimento.getKey();

        if (TextUtils.isEmpty(atendimentoKey))
            atendimentoKey = instance.child(Atendimento.FIREBASE_NODE).child(bucket).push().getKey();

        String atendimentoPath =  String.format("/%s/%s/%s",Atendimento.FIREBASE_NODE, bucket,atendimentoKey);

        childUpdates.put(atendimentoPath, objectMapper.convertValue(atendimento, Map.class));

        return instance.updateChildren(childUpdates);
    }

    public static Task<Void> delete(DatabaseReference ref) {
        return ref.removeValue();
    }


}
