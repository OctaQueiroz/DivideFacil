package com.example.octaq.dividefacil;


import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaContaVisualizacao extends AppCompatActivity {

    //Botões da tela
    Button btnFecharConta;

    //Variáveis de diálogo
    Boolean nomeValor, selecao, adiciona, clique;

    //Edit Texts do Diálogo
    TextView valorFinalConta;
    TextView valorFinalContaComAcrescimo;

    DecimalFormat df = new DecimalFormat("#,###.00");

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    FirebaseDatabase banco;
    DatabaseReference referencia;
    Double valorTotalConta, valorTotalContaComAcrescimo;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Gson gson;
    TransicaoDados objTr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_conta_visualizacao);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Conectando o Firebase
        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Pega os dados referentes ao role atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDados.class);

        //Inicializando variáveis
        valorFinalConta = findViewById(R.id.valorTotal);
        valorFinalContaComAcrescimo = findViewById(R.id.valor10PorCento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        adiciona = clique = nomeValor = selecao = false;

        ListView lv = findViewById(R.id.listaPessoasTelaConta);

        //Configurando o clique no item da lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o usuário slecionado e a referencia do rolê
                objTr.pessoa = dados.get(position);
                String extra = gson.toJson(objTr);
                Intent it = new Intent(TelaContaVisualizacao.this, TelaPessoaVisualizacao.class);
                it.putExtra(EXTRA_UID, extra);
                startActivity(it);
            }
        });

        //Carregando a list view sempre com os dados  de pessoa do banco
        referencia.child(currentUser.getUid()).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dados = new ArrayList<>();
                valorTotalConta = 0.0;
                valorTotalContaComAcrescimo = 0.0;

                for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                    Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                    dados.add(pessoaCadastrada);
                }

                nomeParticipantes = new String[dados.size()];

                for(int i = 0; i<dados.size();i++){
                    nomeParticipantes[i] = dados.get(i).nome;
                    valorTotalConta+=dados.get(i).valorTotal;
                }

                valorTotalContaComAcrescimo += valorTotalConta*1.1;

                if(valorTotalConta != 0.0){
                    valorFinalConta.setText("R$"+df.format(valorTotalConta));
                    valorFinalContaComAcrescimo.setText("R$"+df.format(valorTotalContaComAcrescimo));
                }else{
                    valorFinalConta.setText("R$00,00");
                    valorFinalContaComAcrescimo.setText("R$00,00");
                }

                //Inicializa array list, list view e cria um adapter para ela
                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterListaPessoa adapterPessoa = new AdapterListaPessoa(dados,TelaContaVisualizacao.this);

                lv.setAdapter(adapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}