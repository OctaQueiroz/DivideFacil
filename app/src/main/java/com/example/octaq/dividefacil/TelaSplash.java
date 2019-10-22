package com.example.octaq.dividefacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TelaSplash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_splash);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                login(currentUser);
            }
        }, 2000);

    }

    private void login(FirebaseUser usuario){
        if (usuario != null){
            Intent it  = new Intent(TelaSplash.this, TelaPrincipal.class);
            startActivity(it);
            finish();
        }else{
            Intent intent = new Intent(this, TelaLogin.class);
            startActivity(intent);
            finish();
        }
    }
}
