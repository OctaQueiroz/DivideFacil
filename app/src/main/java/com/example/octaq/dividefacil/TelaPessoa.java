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

import java.text.DecimalFormat;


import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaPessoa extends AppCompatActivity {

    //Para administrar a list view
    String[] nomeAlimentos;

    //Controlando o banco de dados
    FirebaseDatabase banco;
    DatabaseReference referencia;
    String extra;
    Double valorTotalComAcrescimo;
    Pessoa pessoaSelecionada;
    AlertDialog alerta;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    //Controlando dados mostrados na tela
    TextView valorPessoalFinal;
    TextView valorPessoalComAcrescimo;
    TextView nome;
    DecimalFormat df = new DecimalFormat("#,###.00");
    Button btnfecharConta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pessoa);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);

        btnfecharConta = findViewById(R.id.btn_finalizarContaPessoal);
        valorPessoalFinal = findViewById(R.id.valorTotalPessoal);
        valorPessoalComAcrescimo = findViewById(R.id.valor10PorCentoPessoal);
        nome = findViewById(R.id.nomePessoaTelaPessoa);

        //Conectando o Firebase
        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();

        referencia.child(currentUser.getUid()).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    pessoaSelecionada = new Pessoa();

                    for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                        if(dadosDataSnapshot.getValue(Pessoa.class).id.equals(extra)){
                            pessoaSelecionada = dadosDataSnapshot.getValue(Pessoa.class);
                        }
                    }

                    nomeAlimentos = new String[pessoaSelecionada.historicoAlimentos.size()];

                    valorTotalComAcrescimo = 0.0;

                    nome.setText(pessoaSelecionada.nome);

                    valorTotalComAcrescimo += pessoaSelecionada.valorTotal*1.1;

                    valorPessoalFinal.setText("Valor Total: R$"+df.format(pessoaSelecionada.valorTotal));
                    valorPessoalComAcrescimo.setText("Valor com 10%: R$"+df.format(valorTotalComAcrescimo));

                    //Inicializa array list, list view e cria um adapter para ela
                    ListView lv = findViewById(R.id.listaAlimentosTelaPessoa);

                    AdapterListaAlimento adapterAlimento = new AdapterListaAlimento(pessoaSelecionada.historicoAlimentos, TelaPessoa.this);

                    lv.setAdapter(adapterAlimento);
                }catch (Exception ex){

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Finalização de conta");

        builder.setMessage("Ao finalizar a conta pessoal, é entendido que o valor foi pago. Todos os dados da conta pessoal serão apagados, deseja prosseguir?");

        builder.setPositiveButton("Confirmar Finalização", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                //Apaga todos os dados da tabela

                referencia.child(currentUser.getUid()).child(currentUser.getUid()).child(pessoaSelecionada.id).removeValue();

                Toast toast = Toast.makeText(TelaPessoa.this, "Conta pessoal finalizada e apagada com sucesso!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

                finish();
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
