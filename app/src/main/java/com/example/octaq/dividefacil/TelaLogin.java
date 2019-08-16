package com.example.octaq.dividefacil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TelaLogin extends AppCompatActivity {


    EditText email;
    EditText senha;
    Button concluirLogin;
    Button novoCadastro;

    public final static String EXTRA_UID = "com.example.octaq.dividefacil.UID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        email = findViewById(R.id.textEmail);
        senha = findViewById(R.id.textSenha);

        concluirLogin = findViewById(R.id.btn_Login);
        novoCadastro = findViewById(R.id.btn_NovoCadastro);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        novoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                AlertDialog alerta;

                //Criando o Layout para que possam ser colocados 2 Edit Texts no Diálogo
                Context context = TelaLogin.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                //Inicializa os Edit texts que serão  chamados no diálogo
                final EditText novoEmail = new EditText(TelaLogin.this);
                final EditText senha1 = new EditText(TelaLogin.this);
                final EditText senha2 = new EditText(TelaLogin.this);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(TelaLogin.this);

                //Define o título do diálogo
                builder.setTitle("Novo cadastro");

                builder.setView(layout);

                builder.setPositiveButton("Concluir cadastro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(senha1.getText().toString().length() > 5){
                            if(senha1.getText().toString().equals(senha2.getText().toString())){
                                mAuth.createUserWithEmailAndPassword(novoEmail.getText().toString(), senha1.getText().toString())
                                        .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    Toast toast = Toast.makeText(TelaLogin.this, "Novo usuário criado com sucesso!",Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                                    toast.show();

                                                    //Copia os dados do novo usuário para a tela de login
                                                    email.setText(novoEmail.getText().toString());
                                                    senha.setText(senha1.getText().toString());
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast toast = Toast.makeText(TelaLogin.this, "Falha ao criar  novo usuário",Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                                    toast.show();
                                                }
                                            }
                                        });
                            }else{
                                Toast toast = Toast.makeText(TelaLogin.this, "As senhas devem ser iguais",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        }else{
                            Toast toast = Toast.makeText(TelaLogin.this, "Insira uma senha com no minimo 6 caractéres",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
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

                FirebaseAuth.getInstance().signOut();

                mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                        .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Intent it  = new Intent(TelaLogin.this, TelaInicial.class);

                                    startActivity(it);

                                } else {
                                    Toast toast = Toast.makeText(TelaLogin.this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

}
