package com.example.octaq.dividefacil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class TelaDespesa extends AppCompatActivity {

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

    DecimalFormat df = new DecimalFormat("#,###.00");

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    List<UsuarioAutenticadoDoFirebase> usuariosCadastrados;
    List<Pessoa> integrantesSemCntaFechada;
    ValueEventListener listenerDosIntegrantes;
    ValueEventListener listenerDasDespesas;
    ValueEventListener listenerDosUsuarios;
    private ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_despesa);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaDespesa.this,R.color.colorPrimaryDark));

        //Pega os dados referentes ao despesa atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        //Inicializando variáveis
        btnAdicionaPessoa = findViewById(R.id.btn_NovaPessoa);
        btnAdicionaAlimento = findViewById(R.id.btn_CadastroAlimento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        valorFinalConta = findViewById(R.id.valorTotal);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        integrantesSemCntaFechada = new ArrayList<>();
        adiciona = clique = nomeValor = selecao = false;

        btnAdicionaPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPreCadastroDepessoa();
            }
        });

        btnAdicionaAlimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoEscolhaDePessoasParaDividirOGasto();
            }
        });

        ListView lv = findViewById(R.id.listaPessoasTelaConta);

        //Configurando o clique no item da lista
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Chama a tela de pessoa, passando o id do usuário selecionado e
            if(isOnline(TelaDespesa.this)){
                try{
                    objTr.pessoa = dados.get(position);
                    String extra = gson.toJson(objTr);
                    Intent it = new Intent(TelaDespesa.this, TelaPessoa.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivity(it);
                }catch (Exception e){
                    //lidar com erro de conexao
                }
            }else{
                //lidar  com erro de conexão
            }
            }
        });

        listenerDasDespesas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Despesa despesaTemporaria = dataSnapshot.getValue(Despesa.class);

                if(despesaTemporaria.valorRoleAberto > 0.0){
                    valorFinalConta.setText("R$"+df.format(objTr.despesa.valorRoleAberto));
                }else{
                    valorFinalConta.setText("R$00,00");
                }
                if(despesaTemporaria.fechou!= objTr.despesa.fechou){
                    objTr.despesa = despesaTemporaria;
                    Toast toast = Toast.makeText(TelaDespesa.this, "Despesa finalizada por outro integrante!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.show();

                    Intent it  = new Intent(TelaDespesa.this, TelaPrincipal.class);
                    startActivity(it);
                    finish();
                }else{
                    objTr.despesa = despesaTemporaria;
                }
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listenerDosIntegrantes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dados = new ArrayList<>();

                if (isOnline(TelaDespesa.this)){
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

                nomeParticipantes = new String[dados.size()];

                for(int i = 0; i<dados.size();i++){
                    nomeParticipantes[i] = dados.get(i).nome;
                }

                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterParaListaDePessoa adapterPessoa = new AdapterParaListaDePessoa(dados,objTr, TelaDespesa.this);

                lv.setAdapter(adapterPessoa);

                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listenerDosUsuarios = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosCadastrados = new ArrayList<>();
                if(isOnline(TelaDespesa.this)){
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
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        btnFecharConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoFinalizaConta();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        dialog = ProgressDialog.show(TelaDespesa.this,"","Carregando dados...",true,false);

        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").addValueEventListener(listenerDosIntegrantes);
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Despesa").addValueEventListener(listenerDasDespesas);
        referencia.child("AAAAAUSERS").addValueEventListener(listenerDosUsuarios);
    }

    @Override
    protected void onPause() {
        super.onPause();
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").removeEventListener(listenerDosIntegrantes);
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Despesa").removeEventListener(listenerDasDespesas);
        referencia.child("AAAAAUSERS").removeEventListener(listenerDosUsuarios);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void dialogoPreCadastroDepessoa(){
        LinearLayout layout = new LinearLayout(TelaDespesa.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,40,80,10);

        LinearLayout layoutButtonShareIt = new LinearLayout(TelaDespesa.this);
        layoutButtonShareIt.setOrientation(LinearLayout.VERTICAL);
        layoutButtonShareIt.setPadding(0,0,0,20);

        LinearLayout layoutButtonPadrao = new LinearLayout(TelaDespesa.this);
        layoutButtonPadrao.setOrientation(LinearLayout.VERTICAL);

        Button usuarioShareIt = new Button(TelaDespesa.this);
        Button usuarioPadrao = new Button(TelaDespesa.this);

        usuarioShareIt.setBackgroundResource(R.drawable.button_shape_round_border_green);
        usuarioShareIt.setTextColor(Color.WHITE);
        usuarioShareIt.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        usuarioShareIt.setText("Usuário ShareIt");

        usuarioPadrao.setBackgroundResource(R.drawable.button_shape_round_border_green);
        usuarioPadrao.setTextColor(Color.WHITE);
        usuarioPadrao.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        usuarioPadrao.setText("Usuário padrão");

        layoutButtonShareIt.addView(usuarioShareIt);
        layoutButtonPadrao.addView(usuarioPadrao);
        layout.addView(layoutButtonShareIt);
        layout.addView(layoutButtonPadrao);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Quem será o novo integrante?");
        builder.setIcon(R.drawable.ic_simbolo_verde1);

        //Coloca a view criada no diálogo
        builder.setView(layout);

        usuarioShareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.dismiss();
                dialogoCadastroPessoaEmail();
            }
        });

        usuarioPadrao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.dismiss();
                dialogoCadastroPessoaNome();
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

    public void dialogoCadastroPessoaNome(){
        LinearLayout layout = new LinearLayout(TelaDespesa.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa o Edit text que será  chamado no diálogo
        nomePessoa = new EditText(TelaDespesa.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomePessoa.setInputType(InputType.TYPE_CLASS_TEXT);
        //Seta as dicas de cada Edit text criado
        nomePessoa.setHint("Insira o nome do novo integrante");

        nomePessoa.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));

        //Adiciona os Edit Texts na nova view
        layout.addView(nomePessoa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Cadastro de novo integrante");
        builder.setIcon(R.drawable.ic_add_person);

        //Coloca a view criada no diálogo
        builder.setView(layout);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomePessoa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(TelaDespesa.this, "O nome do integrante não pode ser nulo!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else {
                    //Adicionando novo cadastro ao banco da despesa
                    if(isOnline(TelaDespesa.this)) {
                        try {
                            Pessoa novoParticipante = new Pessoa();
                            novoParticipante.nome = nomePessoa.getText().toString();
                            novoParticipante.nome = novoParticipante.nome.substring(0,1).toUpperCase() + novoParticipante.nome.substring(1);
                            novoParticipante.id = referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").push().getKey();
                            for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                referencia.child(objTr.despesa.uidIntegrantes.get(i).uid).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(novoParticipante.id).setValue(novoParticipante);
                            }
                        } catch (Exception e) {
                            //Lidar com erro de conexão
                        }
                    }else{
                        //Lidar com problemas de conexão
                    }
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

    public void dialogoCadastroPessoaEmail() {

        LinearLayout layout = new LinearLayout(TelaDespesa.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa o Edit text que será  chamado no diálogo
        nomePessoa = new EditText(TelaDespesa.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomePessoa.setInputType(InputType.TYPE_CLASS_TEXT);
        //Seta as dicas de cada Edit text criado
        nomePessoa.setHint("Insira o email do novo integrante");

        nomePessoa.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));

        //Adiciona os Edit Texts na nova view
        layout.addView(nomePessoa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Novo integrante ShareIt");
        builder.setIcon(R.drawable.ic_add_person);

        //Coloca a view criada no diálogo
        builder.setView(layout);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            if(nomePessoa.getText().toString().equals("")){
                Toast toast = Toast.makeText(TelaDespesa.this, "O nome do integrante não pode ser nulo!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }else {
                //Adicionando novo cadastro ao banco da despesa
                if(isOnline(TelaDespesa.this)) {
                    try {
                        Pessoa novoParticipante = new Pessoa();
                        novoParticipante.nome = nomePessoa.getText().toString();
                        if(novoParticipante.nome.contains("@")){
                            String uidIntegrantenovo = "";
                            boolean achouUsuárioCadastrado = false;
                            for(int i = 0; i < usuariosCadastrados.size(); i++){
                                if(novoParticipante.nome.equals(usuariosCadastrados.get(i).email)){
                                    uidIntegrantenovo = usuariosCadastrados.get(i).uid;
                                    IntegrantesUsuariosDoFirebase  novoIntegrante = new IntegrantesUsuariosDoFirebase(usuariosCadastrados.get(i).uid);
                                    objTr.despesa.uidIntegrantes.add(novoIntegrante);
                                    novoParticipante.nome = usuariosCadastrados.get(i).nome.substring(0,1).toUpperCase() + usuariosCadastrados.get(i).nome.substring(1);
                                    novoParticipante.id = usuariosCadastrados.get(i).uid;
                                    achouUsuárioCadastrado = true;
                                    break;
                                }
                            }
                            if(!achouUsuárioCadastrado){
                                Toast toast = Toast.makeText(TelaDespesa.this, "Não foi encontrado nenhum usuário com o endereço de e-mail informado!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }else{
                                for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                    referencia.child(objTr.despesa.uidIntegrantes.get(i).uid).child(objTr.despesa.idDadosDespesa).child("Despesa").setValue(objTr.despesa);
                                }
                                for(int i = 0; i < dados.size(); i++){
                                    referencia.child(uidIntegrantenovo).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(dados.get(i).id).setValue(dados.get(i));
                                }
                                for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                    referencia.child(objTr.despesa.uidIntegrantes.get(i).uid).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(uidIntegrantenovo).setValue(novoParticipante);
                                }
                            }
                        }else{
                            Toast toast = Toast.makeText(TelaDespesa.this, "Insira um endereço de e-mail válido!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    } catch (Exception e) {
                        //Lidar com erro de conexão
                    }
                }else{
                    //Lidar com problemas de conexão
                }
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


    private void dialogoEscolhaDePessoasParaDividirOGasto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Selecione quem irá dividir ou pagar");

        //Declara os  vetores de controle de quem será escolhido para participar na conta
        nomes = nomeParticipantes;
        //Vetor boolean para identificar quem foi e quem não foi selecionado
        checados = new boolean[nomes.length];
        //Configura as checkboxes
        builder.setMultiChoiceItems(nomes, checados, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                checados[arg1] = arg2;
            }
        });

        //Configura a saída após a confirmação de seleção
        builder.setPositiveButton("Avançar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                boolean verificaSelecionado = false;
                for(int i = 0; i < checados.length; i++){
                    if(checados[i]){
                        dialogoNovoItemDeGasto();
                        verificaSelecionado = true;
                        break;
                    }
                }
                if(!verificaSelecionado){
                    Toast toast = Toast.makeText(TelaDespesa.this, "Selecione ao menos uma pessoa que irá consumir o item", Toast.LENGTH_SHORT);
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

    private void dialogoNovoItemDeGasto() {

        //Criando o Layout para que possam ser colocados 2 Edit Texts no Diálogo
        Context context = TelaDespesa.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa os Edit texts que serão  chamados no diálogo
        nomeItem = new EditText(TelaDespesa.this);
        valorItem = new EditText(TelaDespesa.this);

        //Seta o tipo de entrada aceitada pelos Edit Texts
        nomeItem.setInputType(InputType.TYPE_CLASS_TEXT);
        valorItem.setInputType(InputType.TYPE_CLASS_PHONE);

        //Seta as dicas de cada Edit text criado
        nomeItem.setHint("Insira o nome do gasto");
        valorItem.setHint("Insira o valor do gasto");

        nomeItem.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        valorItem.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));

        //Adiciona os Edit Texts na nova view
        layout.addView(nomeItem);
        layout.addView(valorItem);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Insira os dados do gasto");
        builder.setIcon(R.drawable.ic_bag);
        //Coloca a view criada no diálogo
        builder.setView(layout);

        //Declara os  vetores de controle de quem será escolhido para participar na conta
        nomes = nomeParticipantes;

        //Configura a saída após a confirmação de seleção
        builder.setPositiveButton("Adicionar novo item", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            if (isOnline(TelaDespesa.this)){
                try {
                    int dividirParaPessoas = 0;
                    ItemDeGasto novoItemDeGasto = new ItemDeGasto();
                    for (int i = 0; i < checados.length; i++) {
                        if (checados[i]) {
                            ConsumidorItemDeGasto novoConsumidor = new ConsumidorItemDeGasto(dados.get(i).nome, dados.get(i).id);
                            novoItemDeGasto.usuariosQueConsomemEsseitem.add(novoConsumidor);
                            dividirParaPessoas++;
                        }
                    }
                    String[] verificadorDigito = valorItem.getText().toString().split(",");
                    if (verificadorDigito.length == 2) {
                        valorItem.setText(verificadorDigito[0] + "." + verificadorDigito[1]);
                    }
                    if (!valorItem.getText().toString().equals("")) {
                        if (!nomeItem.getText().toString().equals("")) {
                            Double valorPorPessoa = Double.valueOf(valorItem.getText().toString()) / dividirParaPessoas;
                            novoItemDeGasto.id = referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").push().getKey();
                            novoItemDeGasto.nome = nomeItem.getText().toString().substring(0,1).toUpperCase() + nomeItem.getText().toString().substring(1);
                            novoItemDeGasto.valor = valorPorPessoa;
                            objTr.despesa.valorRoleAberto += valorPorPessoa * dividirParaPessoas;
                            for (int i = 0; i < checados.length; i++) {
                                if (checados[i]) {
                                    for (int j = 0; j < dados.size(); j++) {
                                        if (nomes[i].equals(dados.get(i).nome)) {
                                            for(int k = 0; k < objTr.despesa.uidIntegrantes.size(); k++){
                                                if(dados.get(i).id.equals(objTr.despesa.uidIntegrantes.get(k).uid)){
                                                    objTr.despesa.uidIntegrantes.get(k).gasto += valorPorPessoa;
                                                    break;
                                                }
                                            }
                                            dados.get(i).valorTotal += valorPorPessoa;
                                            dados.get(i).historicoItemDeGastos.add(novoItemDeGasto);
                                            for(int k = 0; k < objTr.despesa.uidIntegrantes.size(); k++){
                                                referencia.child(objTr.despesa.uidIntegrantes.get(k).uid).child(objTr.despesa.idDadosDespesa).child("Despesa").setValue(objTr.despesa);
                                                referencia.child(objTr.despesa.uidIntegrantes.get(k).uid).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(dados.get(i).id).setValue(dados.get(i));
                                            }
                                            nomes[i] = "";//Impede que uma mesma pessoa  seja contada mais de uma vez
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast toast = Toast.makeText(TelaDespesa.this, "Insira um nome não nulo para o item", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(TelaDespesa.this, "Insira um valor não nulo para o item", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                }catch (Exception e){
                    //Lidar com erro de conexão
                }
            }else{
                //Lidar com problemas de conexão
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

        Context context = TelaDespesa.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(TelaDespesa.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        textoAlerta.setText("Ao finalizar a conta é entendido que todos ja pagaram sua parte. Todos os dados serão apagados, deseja prosseguir?");
        textoAlerta.setTextSize(17);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        layout.addView(textoAlerta);
        builder.setView(layout);

        //Define o título do diálogo
        builder.setIcon(R.drawable.ic_cart);
        builder.setTitle("Finalização de conta");

        builder.setPositiveButton("Confirmar Finalização", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            if(isOnline(TelaDespesa.this)) {
                try{
                    objTr.despesa.fechou = true;
                    for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                        referencia.child(objTr.despesa.uidIntegrantes.get(i).uid).child(objTr.despesa.idDadosDespesa).child("Despesa").setValue(objTr.despesa);
                    }
                    Toast toast = Toast.makeText(TelaDespesa.this, "Despesa finalizada com sucesso!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.show();

                    Intent it  = new Intent(TelaDespesa.this, TelaPrincipal.class);
                    startActivity(it);
                    finish();
                }catch(Exception e){
                    //Lidar com erro de conexão
                }
            }else{
                //Lidar com problemas de conexão
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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
    }
}
