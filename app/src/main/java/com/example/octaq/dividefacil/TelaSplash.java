package com.example.octaq.dividefacil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TelaSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_splash);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser;
        Handler handle = new Handler();

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaSplash.this,R.color.colorPrimaryDark));

        if(isOnline(TelaSplash.this)){
            try{
                currentUser = mAuth.getCurrentUser();
                handle.postDelayed(new Runnable() {
                    @Override public void run() {
                        login(currentUser);
                    }
                }, 2000);
            }catch (Exception e){
                handle.postDelayed(new Runnable() {
                    @Override public void run() {
                        login(null);
                    }
                }, 2000);
            }
        }else{
            handle.postDelayed(new Runnable() {
                @Override public void run() {
                    login(null);
                }
            }, 2000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
    }
}
