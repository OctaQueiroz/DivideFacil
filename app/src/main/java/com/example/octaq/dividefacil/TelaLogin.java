package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TelaLogin extends AppCompatActivity {

    EditText email;
    EditText senha;
    Button concluirLogin;
    Button novoCadastro;
    private ProgressDialog dialog;

    //Variáveis para o logIn no firebase pelo Google
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    int RC_SIGN_IN;

    public final static String EXTRA_UID = "com.example.octaq.dividefacil.UID";

    //Variávei para conexão com o Firebase
    public final static FirebaseDatabase banco = FirebaseDatabase.getInstance();
    public final static DatabaseReference referencia = banco.getReference();
    public final static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaLogin.this,R.color.colorPrimaryDark));

        email = findViewById(R.id.textEmail);
        senha = findViewById(R.id.textSenha);
        concluirLogin = findViewById(R.id.btn_Login);
        novoCadastro = findViewById(R.id.btn_NovoCadastro);

        //Inicializa os objetos necessários para utilizar o logIn no firebase pelo google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        signInButton = findViewById(R.id.google_sign_in_button);
        RC_SIGN_IN = 1;

        novoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            FirebaseAuth.getInstance().signOut();

            AlertDialog alerta;

            //Criando o Layout para que possam ser colocados 2 Edit Texts no Diálogo
            final Context context = TelaLogin.this;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(80,30,80,0);

            //Inicializa os Edit texts que serão  chamados no diálogo
            final EditText novoEmail = new EditText(TelaLogin.this);
            novoEmail.setTypeface(ResourcesCompat.getFont(TelaLogin.this, R.font.cabin));
            novoEmail.setTextSize(17);
            final EditText senha1 = new EditText(TelaLogin.this);
            senha1.setTypeface(ResourcesCompat.getFont(TelaLogin.this, R.font.cabin));
            senha1.setTextSize(17);
            final EditText senha2 = new EditText(TelaLogin.this);
            senha2.setTypeface(ResourcesCompat.getFont(TelaLogin.this, R.font.cabin));
            senha2.setTextSize(17);

            //Seta o tipo de entrada aceitada pelos Edit Texts
            novoEmail.setInputType(InputType.TYPE_CLASS_TEXT);
            senha1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            senha2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //Seta as dicas de cada Edit text criado
            novoEmail.setHint("Email");
            senha1.setHint("Senha");
            senha2.setHint("Senha");

            //Adiciona os Edit Texts na nova view
            layout.addView(novoEmail);
            layout.addView(senha1);
            layout.addView(senha2);

            AlertDialog.Builder builder = new AlertDialog.Builder(TelaLogin.this, R.style.AlertDialogCustom);

            //Define o título do diálogo
            builder.setTitle("Novo cadastro");
            builder.setIcon(R.drawable.ic_add_person);

            builder.setView(layout);

            builder.setPositiveButton("Concluir cadastro", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                if(isOnline(context)){
                    try{
                        if(senha1.getText().toString().length() > 5){
                            if(senha1.getText().toString().equals(senha2.getText().toString())){
                                mAuth.createUserWithEmailAndPassword(novoEmail.getText().toString(), senha1.getText().toString())
                                    .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Toast toast = Toast.makeText(TelaLogin.this, "Novo usuário criado com sucesso!",Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                            toast.show();

                                            //Copia os dados do novo usuário para a tela de login
                                            email.setText(novoEmail.getText().toString());
                                            senha.setText(senha1.getText().toString());
                                        } else {
                                            //If sign in fails, display a message to the user.
                                            Toast toast = Toast.makeText(TelaLogin.this, "Falha ao criar  novo usuário",Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                            toast.show();
                                        }
                                        }
                                    });
                            }else{
                                Toast toast = Toast.makeText(TelaLogin.this, "As senhas devem ser iguais",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                toast.show();
                            }
                        }else{
                            Toast toast = Toast.makeText(TelaLogin.this, "Insira uma senha com no minimo 6 caractéres",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                    }catch (Exception e){
                        //Lidar com erro de conexão aqui
                    }
                }else{
                    //Lidar com problemas de conexão
                }
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alerta = builder.create();
            alerta.show();
            }
        });

        concluirLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isOnline(TelaLogin.this)){
                try{
                    FirebaseAuth.getInstance().signOut();

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                        .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent it  = new Intent(TelaLogin.this, TelaPrincipal.class);
                                    startActivity(it);
                                    finish();
                                } else {
                                    Toast toast = Toast.makeText(TelaLogin.this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                                    toast.show();
                                }
                            }
                        });
                }catch(Exception e ){
                    //Lidar ocm erro de conexao
                }
            }else{
                //Lidar com problemas de conexão
            }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isOnline(TelaLogin.this)){
                try{
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }catch (Exception e){

                }

            }else{
                Toast toast = Toast.makeText(TelaLogin.this, "Conexão fraca com a internet, tente novamente!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.show();
            }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isOnline(TelaLogin.this)){
            dialog = ProgressDialog.show(TelaLogin.this, "","Entrando...", true);
            dialog.show();
            try {
                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                    //Pegaos dados do  retorno
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // pega a conta de usuário selecionada e pasa para a função de LogIn
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        dialog.dismiss();
                        // Google Sign In failed, update UI appropriately
                        Toast toast = Toast.makeText(TelaLogin.this, e.toString(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                }else{
                    dialog.dismiss();

                    Toast toast = Toast.makeText(TelaLogin.this, "Ocorreu uma falha ao entrar com sua conta Google. Por favor, tente novamente!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            } catch(Exception e) {
                dialog.dismiss();
            }
        }else{
            //Lidar com problemas de conexão
        }
    }

    @Override
    public void onBackPressed() {

        Context context = TelaLogin.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(TelaLogin.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(TelaLogin.this, R.font.cabin));
        textoAlerta.setText("Deseja sair do App?");
        textoAlerta.setTextSize(17);

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaLogin.this, R.style.AlertDialogCustom);

        layout.addView(textoAlerta);
        builder.setView(layout);

        //Define o título do diálogo
        builder.setTitle("Aviso");
        builder.setIcon(R.drawable.ic_logout_verde);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        if (isOnline(TelaLogin.this)){
            try {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent it  = new Intent(TelaLogin.this, TelaPrincipal.class);
                            startActivity(it);
                            dialog.dismiss();
                            finish();
                        } else {
                            dialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast toast = Toast.makeText(TelaLogin.this, "Ocorreu uma falha ao entrar com sua conta Google. Por favor, tente novamente!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                        }
                    });
            } catch(Exception e) {
                dialog.dismiss();
                // trata o erro de conexão.
            }
        }else{
            dialog.dismiss();
            //Lidar com problemas de conexão
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
