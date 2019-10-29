package com.example.octaq.dividefacil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
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

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaPessoaVisualizacao extends AppCompatActivity {

    //Para administrar a list view
    String[] nomeAlimentos;

    //Controlando o banco de dados
    FirebaseDatabase banco;
    DatabaseReference referencia;
    Pessoa pessoaSelecionada;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    //Controlando dados mostrados na tela
    TextView valorPessoalFinal;
    TextView nome;
    DecimalFormat df = new DecimalFormat("#,###.00");
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pessoa_visualizacao);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaPessoaVisualizacao.this,R.color.colorPrimaryDark));

        if(isOnline(TelaPessoaVisualizacao.this)){
            try{
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                //Conectando o Firebase
                banco = FirebaseDatabase.getInstance();
                referencia = banco.getReference();
            }catch (Exception e){
                //lidar com erro de conexão
            }
        }else {
            //lidar com erro de conexão
        }

        gson = new Gson();

        String extra;
        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        valorPessoalFinal = findViewById(R.id.valorTotalPessoal);
        nome = findViewById(R.id.nomePessoaTelaPessoa);

        referencia.child(currentUser.getUid()).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pessoaSelecionada = new Pessoa();

                    if(isOnline(TelaPessoaVisualizacao.this)){
                        try{
                            for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                                if(dadosDataSnapshot.getKey().equals(objTr.pessoa.id)){
                                    pessoaSelecionada = dadosDataSnapshot.getValue(Pessoa.class);
                                }
                            }
                        }catch (Exception e){
                            //lidar com erro  de conexao
                        }
                    }else{
                        //lidar com erro de conexao
                    }

                    nomeAlimentos = new String[pessoaSelecionada.historicoItemDeGastos.size()];

                    nome.setText(pessoaSelecionada.nome);

                    if(pessoaSelecionada.valorTotal > 0.0){
                        valorPessoalFinal.setText("R$"+df.format(pessoaSelecionada.valorTotal));
                    }else{
                        valorPessoalFinal.setText("R$00,00");
                    }
                    //Inicializa array list, list view e cria um adapter para ela
                    ListView lv = findViewById(R.id.listaAlimentosTelaPessoa);

                    AdapterparaListaDeItemDeGastoVisualizacao adapterAlimento = new AdapterparaListaDeItemDeGastoVisualizacao(pessoaSelecionada.historicoItemDeGastos, TelaPessoaVisualizacao.this);

                    lv.setAdapter(adapterAlimento);
                }catch (Exception ex){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
