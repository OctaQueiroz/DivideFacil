package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
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
import android.text.InputType;
import android.view.Gravity;
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
    TextView valorFinalContaComAcrescimo;

    DecimalFormat df = new DecimalFormat("#,###.00");

    //Para administrar a list view
    ArrayList<Pessoa> participantes;
    String[] nomeParticipantes;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    ArrayList <Pessoa> dadosSemAlteracao;
    Double valorTotalContaComAcrescimo;
    Gson gson;
    TransicaoDeDadosEntreActivities objTr;
    List<UsuarioAutenticadoDoFirebase> usuariosCadastrados;
    List<Pessoa> integrantesSemCntaFechada;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_conta);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaDespesa.this,R.color.colorPrimaryDark));

        //Pega os dados referentes ao despesa atual
        String extra;
        gson = new Gson();

        Intent it = getIntent();
        extra = it.getStringExtra(EXTRA_UID);
        objTr = gson.fromJson(extra, TransicaoDeDadosEntreActivities.class);

        //dialog = ProgressDialog.show(TelaDespesa.this, "", "Carregando os dados dos integrantes do Rolê...", true);

        //Inicializando variáveis
        btnAdicionaPessoa = findViewById(R.id.btn_NovaPessoa);
        btnAdicionaAlimento = findViewById(R.id.btn_CadastroAlimento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        valorFinalConta = findViewById(R.id.valorTotal);
        valorFinalContaComAcrescimo = findViewById(R.id.valor10PorCento);
        btnFecharConta = findViewById(R.id.btn_FecharConta);
        participantes = new ArrayList<>();
        integrantesSemCntaFechada = new ArrayList<>();
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Carregando a list view sempre com os dados  de pessoa do banco
        referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dados = new ArrayList<>();
                dadosSemAlteracao = new ArrayList<>();

                //valorTotalConta = 0.0;
                valorTotalContaComAcrescimo = 0.0;

                if (isOnline(TelaDespesa.this)){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            Pessoa pessoaCadastrada = dadosDataSnapshot.getValue(Pessoa.class);
                            if (!pessoaCadastrada.fechouConta){
                                dados.add(pessoaCadastrada);
                            }
                            dadosSemAlteracao.add(pessoaCadastrada);
                        }
                    }catch (Exception  e){
                        //Lidar com problemas de conexão
                    }
                }else{
                    //Lidar com problemas de conexão
                }

                nomeParticipantes = new String[dados.size()];

                //Reseta os valores do despesa antes de serem usados para que não se acumulem de multiplas chamadas
                objTr.despesa.valorRoleFechado = 0.0;
                objTr.despesa.valorRoleAberto = 0.0;

                //Guarda o valor apenas de quem ainda não fechou a conta pessoal
                for(int i = 0; i<dados.size();i++){
                    nomeParticipantes[i] = dados.get(i).nome;
                    objTr.despesa.valorRoleAberto+=dados.get(i).valorTotal;

                }
                //Guarda  o valor total da conta, incluindo as pessoas que ja fecharam suas contas individuais
                for(int i = 0; i<dadosSemAlteracao.size();i++){
                    objTr.despesa.valorRoleFechado+=dadosSemAlteracao.get(i).valorTotal;
                }
                //Guarda no banco os dados atualizados do despesa
                referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosDespesa).setValue(objTr.despesa);

                valorTotalContaComAcrescimo += objTr.despesa.valorRoleAberto*1.1;

                if(objTr.despesa.valorRoleAberto != 0.0){
                    valorFinalConta.setText("R$"+df.format(objTr.despesa.valorRoleAberto));
                    valorFinalContaComAcrescimo.setText("R$"+df.format(valorTotalContaComAcrescimo));
                }else{
                    valorFinalConta.setText("R$00,00");
                    valorFinalContaComAcrescimo.setText("R$00,00");
                }

                //Inicializa array list, list view e cria um adapter para ela
                ListView lv = findViewById(R.id.listaPessoasTelaConta);

                AdapterParaListaDePessoa adapterPessoa = new AdapterParaListaDePessoa(dados, TelaDespesa.this);

                lv.setAdapter(adapterPessoa);

                //dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        referencia.child("AAAAAUSERS").addValueEventListener(new ValueEventListener() {
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
                            if(novoParticipante.nome.contains("@")){
                                String uidIntegrantenovo = "";
                                boolean achouUsuárioCadastrado = false;
                                for(int i = 0; i < usuariosCadastrados.size(); i++){
                                    if(novoParticipante.nome.equals(usuariosCadastrados.get(i).email)){
                                        uidIntegrantenovo = usuariosCadastrados.get(i).uid;
                                        objTr.despesa.uidIntegrantes.add(usuariosCadastrados.get(i).uid);
                                        novoParticipante.nome = usuariosCadastrados.get(i).nome;
                                        novoParticipante.id = usuariosCadastrados.get(i).uid;
                                        achouUsuárioCadastrado = true;
                                        break;
                                    }
                                }
                                if(!achouUsuárioCadastrado){
                                    novoParticipante.nome = novoParticipante.nome.split("@")[0];
                                    novoParticipante.id = referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).push().getKey();
                                    for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                        referencia.child(objTr.despesa.uidIntegrantes.get(i)).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(novoParticipante.id).setValue(novoParticipante);
                                    }
                                }else{
                                    referencia.child(uidIntegrantenovo).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosDespesa).setValue(objTr.despesa);
                                    for(int i = 0; i < dados.size(); i++){
                                        referencia.child(uidIntegrantenovo).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(dados.get(i).id).setValue(dados.get(i));
                                    }
                                    for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                        referencia.child(objTr.despesa.uidIntegrantes.get(i)).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(uidIntegrantenovo).setValue(novoParticipante);
                                    }
                                }
                            }else{
                                novoParticipante.id = referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).push().getKey();
                                for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                                    referencia.child(objTr.despesa.uidIntegrantes.get(i)).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(novoParticipante.id).setValue(novoParticipante);
                                }
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
                dialogoNovoItemDeGasto();
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
                        Boolean naoNulo = false;

                        for (int i = 0; i < checados.length; i++) {
                            if (checados[i]) {
                                naoNulo = true;
                                dividirParaPessoas++;
                            }
                        }
                        if (naoNulo) {
                            String[] verificadorDigito = valorItem.getText().toString().split(",");
                            if (verificadorDigito.length == 2) {
                                valorItem.setText(verificadorDigito[0] + "." + verificadorDigito[1]);
                            }
                            if (!valorItem.getText().toString().equals("")) {
                                if (!nomeItem.getText().toString().equals("")) {
                                    Double valorPorPessoa = Double.valueOf(valorItem.getText().toString()) / dividirParaPessoas;
                                    for (int i = 0; i < checados.length; i++) {
                                        if (checados[i]) {
                                            for (int j = 0; j < dados.size(); j++) {
                                                if (nomes[i].equals(dados.get(i).nome)) {
                                                    ItemDeGasto novoItemDeGasto = new ItemDeGasto();
                                                    novoItemDeGasto.nome = nomeItem.getText().toString();
                                                    novoItemDeGasto.valor = valorPorPessoa;
                                                    dados.get(i).valorTotal += valorPorPessoa;
                                                    dados.get(i).historicoItemDeGastos.add(novoItemDeGasto);
                                                    for(int k = 0; k < objTr.despesa.uidIntegrantes.size(); k++){
                                                        referencia.child(objTr.despesa.uidIntegrantes.get(k)).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(dados.get(i).id).setValue(dados.get(i));
                                                    }
                                                    //referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosPessoas).child(dados.get(i).id).setValue(dados.get(i));
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
                        } else {
                            Toast toast = Toast.makeText(TelaDespesa.this, "Selecione ao menos uma pessoa que irá consumir o item", Toast.LENGTH_SHORT);
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
                        //Apaga todos os dados da tabela
                        objTr.despesa.fechou = true;
                        for(int i = 0; i < objTr.despesa.uidIntegrantes.size(); i++){
                            referencia.child(objTr.despesa.uidIntegrantes.get(i)).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosDespesa).setValue(objTr.despesa);
                        }
                        //referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child(objTr.despesa.idDadosDespesa).setValue(objTr.despesa);
                        Toast toast = Toast.makeText(TelaDespesa.this, "Conta finalizada com sucesso!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
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
