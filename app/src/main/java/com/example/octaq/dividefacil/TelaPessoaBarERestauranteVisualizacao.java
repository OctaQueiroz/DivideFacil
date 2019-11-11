package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.text.DecimalFormat;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class TelaPessoaBarERestauranteVisualizacao extends AppCompatActivity {

    //Para administrar a list view
    String[] nomeAlimentos;

    //Controlando o banco de dados
    Double valorTotalComAcrescimo;
    Pessoa pessoaSelecionada;

    //Controlando dados mostrados na tela
    TextView valorPessoalFinal;
    TextView valorPessoalComAcrescimo;
    TextView nome;
    DecimalFormat df = new DecimalFormat("#,###.00");
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    ValueEventListener listenerDosIntegrantes;
    ValueEventListener listenerDasDespesas;
    ListView lv;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pessoa_bar_e_restaurante_visualizacao);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaPessoaBarERestauranteVisualizacao.this,R.color.colorPrimaryDark));

        gson = new Gson();

        String extra;
        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        valorPessoalFinal = findViewById(R.id.valorTotalPessoal);
        valorPessoalComAcrescimo = findViewById(R.id.valor10PorCentoPessoal);
        nome = findViewById(R.id.nomePessoaTelaPessoa);

        listenerDosIntegrantes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pessoaSelecionada = new Pessoa();

                    if(isOnline(TelaPessoaBarERestauranteVisualizacao.this)){
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

                    valorTotalComAcrescimo = 0.0;

                    valorTotalComAcrescimo += pessoaSelecionada.valorTotal*1.1;

                    if(pessoaSelecionada.valorTotal > 0.0){
                        valorPessoalFinal.setText("R$"+df.format(pessoaSelecionada.valorTotal));
                        valorPessoalComAcrescimo.setText("R$"+df.format(valorTotalComAcrescimo));
                    }else{
                        valorPessoalFinal.setText("R$00,00");
                        valorPessoalComAcrescimo.setText("R$00,00");
                    }

                    nome.setText(pessoaSelecionada.nome);

                    lv = findViewById(R.id.listaAlimentosTelaPessoa);

                    AdapterparaListaDeItemDeGastoVisualizacao adapterAlimento = new AdapterparaListaDeItemDeGastoVisualizacao(pessoaSelecionada.historicoItemDeGastos, TelaPessoaBarERestauranteVisualizacao.this);

                    lv.setAdapter(adapterAlimento);
                }catch (Exception ex){

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

        dialog = ProgressDialog.show(TelaPessoaBarERestauranteVisualizacao.this,"","Carregando dados...",true,false);

        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(listenerDosIntegrantes);
    }

    @Override
    protected void onPause() {
        super.onPause();
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").removeEventListener(listenerDosIntegrantes);
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