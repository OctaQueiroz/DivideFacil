package com.example.octaq.dividefacil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaConta extends AppCompatActivity {

    //Botões da tela
    Button btnAdicionaPessoa;
    Button btnAdicionaAlimento;
    Button btnFecharConta;

    //Variáveis de diálogo
    AlertDialog alerta;
    boolean[] checados;
    CharSequence[] nomes;
    Boolean nomeValor, selecao, adiciona, clique;

    //Edit Texts do Diálogo
    EditText nomeItem;
    EditText valorItem;
    EditText nomePessoa;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_conta);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Conectando o Firebase
        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Inicializando variáveis
        btnAdicionaPessoa = findViewById(R.id.btn_NovaPessoa);
        btnAdicionaAlimento = findViewById(R.id.btn_CadastroAlimento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        valorFinalConta = findViewById(R.id.valorTotal);
        valorFinalContaComAcrescimo = findViewById(R.id.valor10PorCento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        adiciona = clique = nomeValor = selecao = false;

        btnAdicionaPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCadastroPessoa();
            }
        });

        btnAdicionaAlimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoNovoAlimento();
            }
        });

        ListView lv = findViewById(R.id.listaPessoasTelaConta);

        //Configurando o clique no item da lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o id do usuário selecionado e

                String extra = dados.get(position).id;
                //extra[1] = currentUser.getUid();
                Intent it = new Intent(TelaConta.this, TelaPessoa.class);
                it.putExtra(EXTRA_UID, extra);
                startActivity(it);
            }
        });

        //Carregando a list view sempre com os dados  de pessoa do banco
        referencia.child(currentUser.getUid()).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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

                valorFinalConta.setText("Valor Total: R$"+df.format(valorTotalConta));
                valorFinalContaComAcrescimo.setText("Valor com 10%: R$"+df.format(valorTotalContaComAcrescimo));

                //Inicializa array list, list view e cria um adapter para ela
                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterListaPessoa adapterPessoa = new AdapterListaPessoa(dados,TelaConta.this);

                lv.setAdapter(adapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnFecharConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoFinalizaConta();
            }
        });
    }

    private void dialogoCadastroPessoa() {

        //Inicializa o Edit text que será  chamado no diálogo
        nomePessoa = new EditText(TelaConta.this);

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


    private void dialogoNovoAlimento() {

        //Criando o Layout para que possam ser colocados 2 Edit Texts no Diálogo
        Context context = TelaConta.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Inicializa os Edit texts que serão  chamados no diálogo
        nomeItem = new EditText(TelaConta.this);
        valorItem = new EditText(TelaConta.this);

        //Seta o tipo de entrada aceitada pelos Edit Texts
        nomeItem.setInputType(InputType.TYPE_CLASS_TEXT);
        valorItem.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //Seta as dicas de cada Edit text criado
        nomeItem.setHint("Insira o nome do item");
        valorItem.setHint("Insira o valor do item");

        //Adiciona os Edit Texts na nova view
        layout.addView(nomeItem);
        layout.addView(valorItem);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Selecione um item da lista");

        //Coloca a view criada no diálogo
        builder.setView(layout);

        //Declara os  vetores de controle de quem será escolhido para participar na conta
        nomes = nomeParticipantes;
        //vetor boolean para identificar quem foi e quem não foi selecionado
        checados = new boolean[nomes.length];

        //Configura as checkboxes
        builder.setMultiChoiceItems(nomes, checados, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                checados[arg1] = arg2;
            }
        });

        //Configura a saída após a confirmação de seleção
        builder.setPositiveButton("Adicionar novo item", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                int dividirParaPessoas = 0;
                Boolean naoNulo = false;

                for(int i =0; i<checados.length;i++){
                    if(checados[i]){
                        naoNulo = true;
                        dividirParaPessoas++;
                    }
                }
                if(naoNulo){
                    String[] verificadorDigito = valorItem.getText().toString().split(",");
                    if(verificadorDigito.length==2){
                        valorItem.setText(verificadorDigito[0]+"."+verificadorDigito[1]);
                    }
                    if(!valorItem.getText().toString().equals("")){
                        if(!nomeItem.getText().toString().equals("")){
                            Double valorPorPessoa = Double.valueOf(valorItem.getText().toString())/dividirParaPessoas;
                            for(int i =0; i<checados.length;i++){
                                if(checados[i]){
                                    for(int j = 0; j<dados.size();j++){
                                        if(nomes[i].equals(dados.get(i).nome)){
                                            Alimento novoAlimento = new Alimento();
                                            novoAlimento.nome = nomeItem.getText().toString();
                                            novoAlimento.valor = valorPorPessoa;
                                            dados.get(i).valorTotal+=valorPorPessoa;
                                            dados.get(i).historicoAlimentos.add(novoAlimento);
                                            referencia.child(currentUser.getUid()).child(currentUser.getUid()).child(dados.get(i).id).setValue(dados.get(i));
                                            nomes[i] = "";//Impede que uma mesma pessoa  seja contada mais de uma vez
                                        }
                                    }
                                }
                            }
                        }else{
                            Toast toast = Toast.makeText(TelaConta.this, "Insira um nome não nulo para o item", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    }else{
                        Toast toast = Toast.makeText(TelaConta.this, "Insira um valor não nulo para o item", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(TelaConta.this, "Selecione ao menos uma pessoa que irá consumir o item", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
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

    private void dialogoFinalizaConta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Finalização de conta");

        builder.setMessage("Ao finalizar a conta é entendido que todos ja pagaram sua parte. Todos os dados serão apagados, deseja prosseguir?");

        builder.setPositiveButton("Confirmar Finalização", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                //Apaga todos os dados da tabela
                referencia.child(currentUser.getUid()).child(currentUser.getUid()).removeValue();
                Toast toast = Toast.makeText(TelaConta.this, "Conta finalizada com sucesso!", Toast.LENGTH_SHORT);
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
