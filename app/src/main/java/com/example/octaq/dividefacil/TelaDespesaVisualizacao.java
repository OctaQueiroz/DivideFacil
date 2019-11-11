package com.example.octaq.dividefacil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class TelaDespesaVisualizacao extends AppCompatActivity {

    //Edit Texts do Diálogo
    TextView valorFinalConta;

    DecimalFormat df = new DecimalFormat("#,###.00");

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    Double valorTotalConta;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    ValueEventListener listenerDosIntegrantes;
    ValueEventListener listenerDasDespesas;
    ListView lv;
    private ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_despesa_visualizacao);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaDespesaVisualizacao.this,R.color.colorPrimaryDark));

        //Pega os dados referentes ao despesa atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        //Inicializando variáveis
        valorFinalConta = findViewById(R.id.valorTotal);
        participantes = new ArrayList<>();

        lv = findViewById(R.id.listaPessoasTelaConta);

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

        listenerDosIntegrantes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dados = new ArrayList<>();

                if (isOnline(TelaDespesaVisualizacao.this)){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                            dados.add(pessoaCadastrada);
                        }
                    }catch (Exception  e){
                        //Lidar com problemas de conexão
                    }
                }else{
                    //Lidar com problemas de conexão
                }

                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterParaListaDePessoa adapterPessoa = new AdapterParaListaDePessoa(dados,objTr, TelaDespesaVisualizacao.this);

                lv.setAdapter(adapterPessoa);

                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listenerDasDespesas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                objTr.despesa = dataSnapshot.getValue(Despesa.class);

                if(objTr.despesa.valorRoleAberto > 0.0){
                    valorFinalConta.setText("R$"+df.format(objTr.despesa.valorRoleAberto));
                }else{
                    valorFinalConta.setText("R$00,00");
                }

                if(dialog.isShowing()){
                    dialog.dismiss();
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

        dialog = ProgressDialog.show(TelaDespesaVisualizacao.this,"","Carregando dados...",true,false);

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
