package com.example.octaq.dividefacil;

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

import java.util.ArrayList;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();

        //Carregando a list view sempre com os dados do banco
        referencia.child(currentUser.getUid()).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dados = new ArrayList<>();
                for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                    Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                    dados.add(pessoaCadastrada);
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

        //Inicializando variáveis
        btnCadastroPessoa = findViewById(R.id.btn_CadastroPessoa);
        btnConcluiCadastro = findViewById(R.id.btn_ConcluirCadastro);
        participantes = new ArrayList<>();

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
                        startActivity(it);
                    }

                }else{
                    Toast toast = Toast.makeText(TelaInicial.this, "Insira ao menos um integrante no Rolê", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
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

                //Adicionando novo cadastro ao banco de dados
                Pessoa novoParticipante = new Pessoa();
                novoParticipante.nome = nomePessoa.getText().toString();
                novoParticipante.id = referencia.child(currentUser.getUid()).child(currentUser.getUid()).push().getKey();
                referencia.child(currentUser.getUid()).child(currentUser.getUid()).child(novoParticipante.id).setValue(novoParticipante);
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
}
