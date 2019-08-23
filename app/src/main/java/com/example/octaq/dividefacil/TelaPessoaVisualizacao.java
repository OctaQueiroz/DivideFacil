package com.example.octaq.dividefacil;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;


import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaPessoaVisualizacao extends AppCompatActivity {

    //Para administrar a list view
    String[] nomeAlimentos;

    //Controlando o banco de dados
    FirebaseDatabase banco;
    DatabaseReference referencia;
    Double valorTotalComAcrescimo;
    Pessoa pessoaSelecionada;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    //Controlando dados mostrados na tela
    TextView valorPessoalFinal;
    TextView valorPessoalComAcrescimo;
    TextView nome;
    DecimalFormat df = new DecimalFormat("#,###.00");
    Gson gson;
    TransicaoDados objTr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pessoa_visualizacao);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        gson = new Gson();

        String extra;
        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDados.class);

        valorPessoalFinal = findViewById(R.id.valorTotalPessoal);
        valorPessoalComAcrescimo = findViewById(R.id.valor10PorCentoPessoal);
        nome = findViewById(R.id.nomePessoaTelaPessoa);

        //Conectando o Firebase
        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();

        referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pessoaSelecionada = new Pessoa();

                    for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                        if(dadosDataSnapshot.getKey().equals(objTr.pessoa.id)){
                            pessoaSelecionada = dadosDataSnapshot.getValue(Pessoa.class);
                        }
                    }

                    nomeAlimentos = new String[pessoaSelecionada.historicoAlimentos.size()];

                    valorTotalComAcrescimo = 0.0;

                    nome.setText(pessoaSelecionada.nome);

                    valorTotalComAcrescimo += pessoaSelecionada.valorTotal*1.1;

                    if(pessoaSelecionada.valorTotal != 0.0){
                        valorPessoalFinal.setText("R$"+df.format(pessoaSelecionada.valorTotal));
                        valorPessoalComAcrescimo.setText("R$"+df.format(valorTotalComAcrescimo));
                    }else{
                        valorPessoalFinal.setText("R$00,00");
                        valorPessoalComAcrescimo.setText("R$00,00");
                    }
                    //Inicializa array list, list view e cria um adapter para ela
                    ListView lv = findViewById(R.id.listaAlimentosTelaPessoa);

                    AdapterListaAlimento adapterAlimento = new AdapterListaAlimento(pessoaSelecionada.historicoAlimentos, TelaPessoaVisualizacao.this);

                    lv.setAdapter(adapterAlimento);
                }catch (Exception ex){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}