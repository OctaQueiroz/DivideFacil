package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;


public class TelaInicial extends AppCompatActivity {

    //Botões da tela
    Button btnCadastroPessoa;
    Button btnConcluiCadastro;

    //Dialogo de cadastro de pessoa
    AlertDialog alerta;
    EditText nomePessoa;

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    FirebaseDatabase banco;
    DatabaseReference referencia;
    ArrayList <Pessoa> dados;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Gson gson;
    TransicaoDados objTr;
    ProgressDialog progressDialog;
    boolean primeiroCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);


    }

    @Override
    protected void onStart() {
        super.onStart();
        String extra;
        gson = new Gson();

        primeiroCadastro = true;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDados.class);

        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();

        //Inicializando variáveis
        btnCadastroPessoa = findViewById(R.id.btn_CadastroPessoa);
        btnConcluiCadastro = findViewById(R.id.btn_ConcluirCadastro);
        participantes = new ArrayList<>();



        //Carregando a list view sempre com os dados do banco

            referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                        Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                        if(!pessoaCadastrada.fechouConta){
                            dados.add(pessoaCadastrada);
                        }
                    }

                    nomeParticipantes = new String[dados.size()];

                    for(int i = 0; i<dados.size();i++){
                        nomeParticipantes[i] = dados.get(i).nome;
                    }

                    //Inicializa array list, list view e cria um adapter para ela
                    ListView lv = findViewById(R.id.listaPessoasTelaInicial);

                    ArrayAdapter<String> lvAdapter  = new ArrayAdapter<>(TelaInicial.this, android.R.layout.simple_list_item_1,nomeParticipantes);

                    lv.setAdapter(lvAdapter);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        btnCadastroPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCadastroPessoa();
            }
        });

        //Chama a próxima página
        btnConcluiCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dados != null){
                    if(dados.size()>0){
                        Intent it = new Intent(TelaInicial.this, TelaConta.class);
                        String extra = gson.toJson(objTr);
                        it.putExtra(EXTRA_UID, extra);
                        startActivity(it);
                        finish();
                    }

                }else{
                    Toast toast = Toast.makeText(TelaInicial.this, "Insira ao menos um integrante no Rolê", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });
    }


    private void dialogoCadastroPessoa() {

        //Inicializa o Edit text que será  chamado no diálogo
        nomePessoa = new EditText(TelaInicial.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomePessoa.setInputType(InputType.TYPE_CLASS_TEXT);

        //Seta as dicas de cada Edit text criado
        nomePessoa.setHint("Insira o nome da nova pessoa");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Cadastro de nova Pessoa");

        //Coloca a view criada no diálogo
        builder.setView(nomePessoa);

        builder.setPositiveButton("Confirmar Cadastro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomePessoa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(TelaInicial.this, "O nome do integrante não pode ser nulo!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else{
                    Pessoa novoParticipante = new Pessoa();
                    novoParticipante.nome = nomePessoa.getText().toString();
                    novoParticipante.id = referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).push().getKey();
                    referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).child(novoParticipante.id).setValue(novoParticipante);

                }

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dados.size() < 1){
                    finish();
                }
            }
        });

        alerta = builder.create();


        alerta.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(dados.size() < 1){
                    finish();
                }
            }
        });

        alerta.show();
    }


    @Override
    public void onBackPressed() {
        if(dados.size()<1){
            referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).removeValue();
            finish();
        }else{
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dados.size()<1){
            referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).removeValue();
            finish();
        }
    }



}
