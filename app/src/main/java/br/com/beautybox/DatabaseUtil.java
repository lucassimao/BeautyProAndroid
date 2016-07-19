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
import java.util.TimeZone;

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
}
