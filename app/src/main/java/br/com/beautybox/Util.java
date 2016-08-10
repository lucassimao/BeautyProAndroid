package br.com.beautybox;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lsimaocosta on 18/07/16.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static DatabaseReference databaseRoot() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            FirebaseCrash.logcat(Log.DEBUG, TAG, "metodo Util.databaseRoot() sendo chamado sem usuario logado!");
            return null;
        }

        String bucket = firebaseUser.getEmail().replace("@","-").replace(".","-");
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        return root.getReference(bucket);
    }

    /**
     * Dada uma data, determina o timestamp para o 1º dia do mês às 00:00:00     *
     *
     * @param date
     * @return timestamp
     */
    public static final long getMonthTimestamp(Date date) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.clear(Calendar.HOUR);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        return c.getTimeInMillis();
    }

    /**
     * Retorna o timestamp para o 1º dia do mês corrente às 00:00:00
     * @return
     */
    public static long getCurrentMonthTimestamp() {
        return getMonthTimestamp(new Date());
    }
}
