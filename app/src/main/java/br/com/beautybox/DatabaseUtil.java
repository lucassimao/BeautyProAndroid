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
public class DatabaseUtil {

    private static final String TAG = DatabaseUtil.class.getSimpleName();

    public static DatabaseReference root() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            FirebaseCrash.logcat(Log.DEBUG, TAG, "metodo DatabaseUtil.root() sendo chamado sem usuario logado!");
            return null;
        }

        String bucket = firebaseUser.getEmail().replace("@","-").replace(".","-");
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        return root.getReference(bucket);
    }

    /**
     * Dado uma data, determina o timestamp do bucket em que o objeto
     * requisitante deve ser armazenado
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

    public static long getCurrentMonthTimestamp() {
        return getMonthTimestamp(new Date());
    }
}
