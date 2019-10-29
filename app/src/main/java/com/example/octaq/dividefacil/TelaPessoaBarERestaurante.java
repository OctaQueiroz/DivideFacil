package com.example.octaq.dividefacil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class TelaPessoaBarERestaurante extends AppCompatActivity {

    //Para administrar a list view
    String[] nomeAlimentos;

    //Controlando o banco de dados
    Double valorTotalComAcrescimo;
    Pessoa pessoaSelecionada;
    AlertDialog alerta;
    List<UsuarioAutenticadoDoFirebase> usuariosCadastrados;

    //Controlando dados mostrados na tela
    TextView valorPessoalFinal;
    TextView valorPessoalComAcrescimo;
    TextView nome;
    DecimalFormat df = new DecimalFormat("#,###.00");
    Button btnfecharConta;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    List<Pessoa> integrantes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pessoa_bar_e_restaurante);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaPessoaBarERestaurante.this,R.color.colorPrimaryDark));

        gson = new Gson();

        String extra;
        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        btnfecharConta = findViewById(R.id.btn_finalizarContaPessoal);
        valorPessoalFinal = findViewById(R.id.valorTotalPessoal);
        valorPessoalComAcrescimo = findViewById(R.id.valor10PorCentoPessoal);
        nome = findViewById(R.id.nomePessoaTelaPessoa);
        integrantes = new ArrayList<>();;
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pessoaSelecionada = new Pessoa();

                    if(isOnline(TelaPessoaBarERestaurante.this)){
                        try{
                            for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                                if(dadosDataSnapshot != null){
                                    if(dadosDataSnapshot.getValue(Pessoa.class).id.equals(objTr.pessoa.id)){
                                        pessoaSelecionada = dadosDataSnapshot.getValue(Pessoa.class);
                                    }
                                    integrantes.add(dadosDataSnapshot.getValue(Pessoa.class));
                                }
                            }

                        }catch (Exception e){
                            //lidar com erro de conexao
                        }
                    }else{
                        //lidar com erro de conexao
                    }

                    nomeAlimentos = new String[pessoaSelecionada.historicoItemDeGastos.size()];

                    valorTotalComAcrescimo = 0.0;

                    nome.setText(pessoaSelecionada.nome);

                    valorTotalComAcrescimo += pessoaSelecionada.valorTotal*1.1;
                    if(pessoaSelecionada.valorTotal > 0.0){
                        valorPessoalFinal.setText("R$"+df.format(pessoaSelecionada.valorTotal));
                        valorPessoalComAcrescimo.setText("R$"+df.format(valorTotalComAcrescimo));
                    }else{
                        valorPessoalFinal.setText("R$00,00");
                        valorPessoalComAcrescimo.setText("R$00,00");
                    }
                    //Inicializa array list, list view e cria um adapter para ela
                    ListView lv = findViewById(R.id.listaItemDeGastoTelaPessoa);

                    AdapterParaListaDeItemDeGasto adapterAlimento = new AdapterParaListaDeItemDeGasto(pessoaSelecionada.historicoItemDeGastos, TelaPessoaBarERestaurante.this);

                    lv.setAdapter(adapterAlimento);
                }catch (Exception ex){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        referencia.child("AAAAAUSERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosCadastrados = new ArrayList<>();
                if(isOnline(TelaPessoaBarERestaurante.this)){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            UsuarioAutenticadoDoFirebase usuarioDaBase = dadosDataSnapshot.getValue(UsuarioAutenticadoDoFirebase.class);

                            if(usuarioDaBase != null) {
                                usuariosCadastrados.add(usuarioDaBase);
                            }
                        }
                    }catch (Exception e){
                        //Lidar com erro de conexao
                    }
                }else{
                    //Lidar com erro de conexao
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnfecharConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoFinalizaConta();
            }
        });
    }


    private void dialogoFinalizaConta() {

        Context context = TelaPessoaBarERestaurante.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(TelaPessoaBarERestaurante.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        textoAlerta.setText("Ao finalizar a conta pessoal, é entendido que o valor foi pago. Todos os dados da conta pessoal serão apagados, deseja prosseguir?");
        textoAlerta.setTextSize(17);
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);

        layout.addView(textoAlerta);
        builder.setView(layout);

        //Define o título do diálogo
        builder.setIcon(R.drawable.ic_cart);

        builder.setTitle("Finalização de conta");

        //builder.setMessage("Ao finalizar a conta pessoal, é entendido que o valor foi pago. Todos os dados da conta pessoal serão apagados, deseja prosseguir?");

        builder.setPositiveButton("Confirmar Finalização", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(isOnline(TelaPessoaBarERestaurante.this)){
                    try {
                        //Apaga todos os dados da tabela
                        pessoaSelecionada.fechouConta = true;
                        for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                            referencia.child(objTr.despesa.uidIntegrantes.get(i)).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(pessoaSelecionada.id).setValue(pessoaSelecionada);
                        }
                        Toast toast = Toast.makeText(TelaPessoaBarERestaurante.this, "Conta pessoal finalizada e apagada com sucesso!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();

                        finish();
                    }catch (Exception e){
                        //LLidar com erro de conexao
                    }
                }else{
                    //lidar com erro de conexao
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

    public void deletarItemDeGasto(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaPessoaBarERestaurante.this, R.style.AlertDialogCustom);

        final View view = v;

        Context context = TelaPessoaBarERestaurante.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        int tag = (Integer) view.getTag();
        final ItemDeGasto itemDeGastoSelecionado = pessoaSelecionada.historicoItemDeGastos.get(tag);

        TextView textoAlerta = new TextView(TelaPessoaBarERestaurante.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        if(itemDeGastoSelecionado.usuariosQueConsomemEsseitem.size()>1){
            String textoDoAlerta = "";
            textoDoAlerta = "Deseja remover essa item do histórico dos seguintes integrantes?"+"\n" + "\n";
            for(int i = 0; i < itemDeGastoSelecionado.usuariosQueConsomemEsseitem.size(); i++){
                textoDoAlerta += itemDeGastoSelecionado.usuariosQueConsomemEsseitem.get(i).nomeConsumidor + "\n";
            }
            textoAlerta.setText(textoDoAlerta);
        }else{
            textoAlerta.setText("Deseja remover esse item do seu histórico?");
        }
        textoAlerta.setTextSize(17);

        //Define o título do diálogo
        builder.setTitle("Apagar");
        builder.setIcon(R.drawable.ic_delete);

        layout.addView(textoAlerta);
        builder.setView(layout);


        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                int tag = (Integer) view.getTag();
                ItemDeGasto itemDeGastoASerDeletado = pessoaSelecionada.historicoItemDeGastos.get(tag);

                //itemDeGastoASerDeletado.excluido =true;
                for(int i = 0; i < itemDeGastoASerDeletado.usuariosQueConsomemEsseitem.size(); i++){
                    if(isOnline(TelaPessoaBarERestaurante.this)){
                        try{
                            for(int j = 0; j < integrantes.size(); j++){
                                if (integrantes.get(j).id.equals(itemDeGastoASerDeletado.usuariosQueConsomemEsseitem.get(i).uidConsumidor)){
                                    for(int k = 0; k < integrantes.get(j).historicoItemDeGastos.size(); k ++){
                                        if(integrantes.get(j).historicoItemDeGastos.get(k).id.equals(itemDeGastoASerDeletado.id)){
                                            integrantes.get(j).valorTotal -= integrantes.get(j).historicoItemDeGastos.get(k).valor;
                                            integrantes.get(j).historicoItemDeGastos.remove(k);
                                            break;
                                        }
                                    }
                                    for(int k = 0; k < objTr.despesa.uidIntegrantes.size(); k++){
                                        referencia.child(objTr.despesa.uidIntegrantes.get(k)).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(integrantes.get(j).id).setValue(integrantes.get(j));
                                    }
                                    break;
                                }

                            }
                            //Pessoa usuario =
                        }catch (Exception e){
                            //Lidar com erro de conexão
                        }
                    }else{
                        //Lidar com erro de conexao
                    }
                }
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
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
