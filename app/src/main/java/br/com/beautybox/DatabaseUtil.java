package br.com.beautybox;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
