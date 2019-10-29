package com.example.octaq.dividefacil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

public class TelaDespesaVisualizacao extends AppCompatActivity {

    //Botões da tela
    Button btnFecharConta;

    //Variáveis de diálogo
    Boolean nomeValor, selecao, adiciona, clique;

    //Edit Texts do Diálogo
    TextView valorFinalConta;

    DecimalFormat df = new DecimalFormat("#,###.00");

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    FirebaseDatabase banco;
    DatabaseReference referencia;
    Double valorTotalConta;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_despesa_visualizacao);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaDespesaVisualizacao.this,R.color.colorPrimaryDark));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline(TelaDespesaVisualizacao.this)){
            try{
                //Conectando o Firebase
                banco = FirebaseDatabase.getInstance();
                referencia = banco.getReference();
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
            }catch (Exception e){
                //lidar com erro de conexao
            }
        }else{
            //lidar com erro de conexao
        }

        //Pega os dados referentes ao despesa atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        //Inicializando variáveis
        valorFinalConta = findViewById(R.id.valorTotal);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        adiciona = clique = nomeValor = selecao = false;

        ListView lv = findViewById(R.id.listaPessoasTelaConta);

        //Configurando o clique no item da lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o usuário slecionado e a referencia do rolê
                if(isOnline(TelaDespesaVisualizacao.this)){
                    try{
                        objTr.pessoa = dados.get(position);
                        String extra = gson.toJson(objTr);
                        Intent it = new Intent(TelaDespesaVisualizacao.this, TelaPessoaVisualizacao.class);
                        it.putExtra(EXTRA_UID, extra);
                        startActivity(it);
                    }catch (Exception e){
                        //lidar com erro de conexao
                    }
                }else{
                    //lidar com erro de conexão
                }
            }
        });

        //Carregando a list view sempre com os dados  de pessoa do banco
        referencia.child(currentUser.getUid()).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dados = new ArrayList<>();
                valorTotalConta = 0.0;

                if(isOnline(TelaDespesaVisualizacao.this)){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                            dados.add(pessoaCadastrada);
                        }
                    }catch (Exception e){
                        //lidar com erro de conexao
                    }
                }else{
                    //lidar com erro de coneexao
                }

                nomeParticipantes = new String[dados.size()];

                for(int i = 0; i<dados.size();i++){
                    nomeParticipantes[i] = dados.get(i).nome;
                    valorTotalConta+=dados.get(i).valorTotal;
                }

                if(valorTotalConta > 0.0){
                    valorFinalConta.setText("R$"+df.format(valorTotalConta));
                }else{
                    valorFinalConta.setText("R$00,00");
                }

                //Inicializa array list, list view e cria um adapter para ela
                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterParaListaDePessoa adapterPessoa = new AdapterParaListaDePessoa(dados, TelaDespesaVisualizacao.this);

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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
    }
}
