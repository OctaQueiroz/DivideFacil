package com.example.octaq.dividefacil;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import java.util.ArrayList;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class TelaDespesaBarERestauranteVisualizacao extends AppCompatActivity {

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
    Double valorTotalConta, valorTotalContaComAcrescimo;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    ValueEventListener listenerDosIntegrantes;
    ValueEventListener listenerDasDespesas;
    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_despesa_bar_e_restaurante_visualizacao);

        //Pega os dados referentes ao despesa atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        //Inicializando variáveis
        valorFinalConta = findViewById(R.id.valorTotal);
        valorFinalContaComAcrescimo = findViewById(R.id.valor10PorCento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        adiciona = clique = nomeValor = selecao = false;

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaDespesaBarERestauranteVisualizacao.this,R.color.colorPrimaryDark));

        lv = findViewById(R.id.listaPessoasTelaConta);

        //Configurando o clique no item da lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o usuário slecionado e a referencia do rolê
                if(isOnline(TelaDespesaBarERestauranteVisualizacao.this)){
                    try{
                        objTr.pessoa = dados.get(position);
                        String extra = gson.toJson(objTr);
                        Intent it = new Intent(TelaDespesaBarERestauranteVisualizacao.this, TelaPessoaBarERestauranteVisualizacao.class);
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

        listenerDosIntegrantes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dados = new ArrayList<>();

                if(isOnline(TelaDespesaBarERestauranteVisualizacao.this)){
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

                AdapterParaListaDePessoa adapterPessoa = new AdapterParaListaDePessoa(dados,objTr, TelaDespesaBarERestauranteVisualizacao.this);

                lv.setAdapter(adapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listenerDasDespesas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                objTr.despesa = dataSnapshot.getValue(Despesa.class);

                valorTotalContaComAcrescimo = 0.0;

                valorTotalContaComAcrescimo += objTr.despesa.valorRoleAberto*1.1;

                if(objTr.despesa.valorRoleAberto > 0.0){
                    valorFinalConta.setText("R$"+df.format(objTr.despesa.valorRoleAberto));
                    valorFinalContaComAcrescimo.setText("R$"+df.format(valorTotalContaComAcrescimo));
                }else{
                    valorFinalConta.setText("R$00,00");
                    valorFinalContaComAcrescimo.setText("R$00,00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(listenerDosIntegrantes);
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Despesa").addValueEventListener(listenerDasDespesas);
    }

    @Override
    protected void onPause() {
        super.onPause();
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").removeEventListener(listenerDosIntegrantes);
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Despesa").removeEventListener(listenerDasDespesas);
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