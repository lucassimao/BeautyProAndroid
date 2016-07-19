package br.com.beautybox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // not signed in
            startActivityForResult(
                     AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.mipmap.beauty_512)
                            .setTheme(R.style.AppTheme_NoActionBar)
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
//                                    AuthUI.FACEBOOK_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } /*else {
                // user is not signed in. Maybe just wait for the user to press
                Toast.makeText(this,"Usuário ou senha incorreto(s)",Toast.LENGTH_SHORT).show();
            }*/
        }
    }
}
