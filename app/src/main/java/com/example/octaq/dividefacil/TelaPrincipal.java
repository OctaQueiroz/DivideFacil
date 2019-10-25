package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.mAuth;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class TelaPrincipal extends AppCompatActivity {

    //Para administrar a list view
    List<Despesa> despesas;
    TransicaoDeDadosEntreActivities objTr;
    ImageView deletar;
    ProgressDialog dialog;

    //Variáveis do dialogo para criar novo despesa
    EditText nomeDespesa;
    boolean[] checados;
    AlertDialog alerta;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaPrincipal.this,R.color.colorPrimaryDark));

        objTr = new TransicaoDeDadosEntreActivities();

        //pega o id do usuário atual, para ser utilizado
        objTr.userUid = mAuth.getCurrentUser().getUid();

        dialog = ProgressDialog.show(TelaPrincipal.this, "",
                "Carregando suas Despesas...", true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        deletar = findViewById(R.id.iv_delete);
        FloatingActionButton novoRole = findViewById(R.id.fab_novo_role);
        novoRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  dialogoEscolhaDeTipoDeDespesa();
            }
        });

        lv = findViewById(R.id.lv_historico_roles);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o id do usuário selecionado e os  meios dde comunicação com o banco
                if(isOnline(TelaPrincipal.this)){
                    try{
                        objTr.despesa = despesas.get(position);
                        Gson gson = new Gson();
                        String extra = gson.toJson(objTr);
                        if(!objTr.despesa.fechou){
                            Intent it = new Intent(TelaPrincipal.this, TelaDespesa.class);
                            it.putExtra(EXTRA_UID, extra);
                            startActivityForResult(it, 2);
                        }else{
                            Intent it = new Intent(TelaPrincipal.this, TelaDespesaVisualizacao.class);
                            it.putExtra(EXTRA_UID, extra);
                            startActivityForResult(it, 2);
                        }
                    }catch (Exception e){
                       //Lidar com problemas de conexão
                    }
                }else{
                    //Lidar com problemas de conexão
                }
            }
        });
        //Carregando a list view sempre com os dados do banco
        referencia.child(objTr.userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.show();

                despesas = new ArrayList<>();
                if(isOnline(TelaPrincipal.this)){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            String despesaCadastrada = dadosDataSnapshot.getKey();
                            Despesa despesaAtualizado = dadosDataSnapshot.child(despesaCadastrada).getValue(Despesa.class);
                            if(despesaAtualizado != null) {
                                if (!despesaAtualizado.excluido) {
                                    despesas.add(despesaAtualizado);
                                }
                            }
                        }
                    }catch (Exception e){
                        //Lidar com erro de conexao
                    }
                }else{
                    //Lidar com erro de conexao
                }
                //Cria um adapter para a list View
                AdapterParaListaDeDespesa adapterParaListaDeDespesa = new AdapterParaListaDeDespesa(despesas, TelaPrincipal.this);

                lv.setAdapter(adapterParaListaDeDespesa);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            verificaLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                String resultado = data.getStringExtra(EXTRA_UID);
                this.objTr = gson.fromJson(resultado, TransicaoDeDadosEntreActivities.class);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Aviso");

        builder.setMessage("Deseja sair do App?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
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

    public void deletarRole(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this, R.style.AlertDialogCustom);

        final View view = v;

        Context context = TelaPrincipal.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(TelaPrincipal.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        textoAlerta.setText("Deseja remover essa despesa do seu histórico?");
        textoAlerta.setTextSize(17);

        //Define o título do diálogo
        builder.setTitle("Apagar");
        builder.setIcon(R.drawable.ic_delete);

        layout.addView(textoAlerta);
        builder.setView(layout);


        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    int tag = (Integer) view.getTag();
                    Despesa despesaTemp = despesas.get(tag);
                    despesaTemp.excluido =true;
                    if(isOnline(TelaPrincipal.this)){
                        try{
                            referencia.child(objTr.userUid).child(despesaTemp.idDadosRole).child(despesaTemp.idDadosRole).setValue(despesaTemp);
                        }catch (Exception e){
                            //Lidar com erro de conexão
                        }
                    }else{
                        //Lidar com erro de conexao
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



    private void verificaLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        Context context = TelaPrincipal.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(TelaPrincipal.this);
        textoAlerta.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        textoAlerta.setText("Deseja desconectar da sua conta?");
        textoAlerta.setTextSize(17);

        //Define o título do diálogo
        builder.setTitle("Logoff");
        builder.setIcon(R.drawable.ic_logout_verde);

        layout.addView(textoAlerta);
        builder.setView(layout);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(isOnline(TelaPrincipal.this)){
                    try{
                        //Este metodo desconecta a conta google do usuário do fire base
                        revokeAccess();
                        FirebaseAuth.getInstance().signOut();
                        Intent it  = new Intent(TelaPrincipal.this, TelaLogin.class);
                        startActivity(it);
                        finish();
                    }catch (Exception e){
                        //Lidar com erro de conexao
                    }
                }else{
                    //Lidar com erro de conexao
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

    private boolean revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        return true;
    }

    private void dialogoEscolhaDeTipoDeDespesa() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Qual o tipo da despesa?");
        builder.setIcon(R.drawable.ic_filter);
        //Declara os  vetores de controle de quem será escolhido para participar na conta
        final TipoDeDespesa tipoDeDespesa = new TipoDeDespesa();
        //Vetor boolean para identificar quem foi e quem não foi selecionado
        checados = new boolean[tipoDeDespesa.listaDeTiposDeDespesa.size()];

        //adapter utilizando um layout customizado (TextView)
        AdapterParaListaDeTipoDeDespesa adapter = new AdapterParaListaDeTipoDeDespesa(tipoDeDespesa.listaDeTiposDeDespesa,this);

        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                objTr.despesa = new Despesa();
                objTr.despesa.tipoDeDespesa = tipoDeDespesa.listaDeTiposDeDespesa.get(arg1);
                alerta.dismiss();
                dialogoCadastroDeDespesa();
            }
        });

        alerta = builder.create();
        alerta.show();

    }

    private void dialogoCadastroDeDespesa() {

        LinearLayout layout = new LinearLayout(TelaPrincipal.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa o Edit text que será  chamado no diálogo
        nomeDespesa = new EditText(TelaPrincipal.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomeDespesa.setInputType(InputType.TYPE_CLASS_TEXT);

        //Seta as dicas de cada Edit text criado
        nomeDespesa.setHint("Insira o nome da despesa");
        nomeDespesa.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));

        layout.addView(nomeDespesa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Criar nova Despesa");
        builder.setIcon(R.drawable.ic_add_income);
        //Coloca a view criada no diálogo
        builder.setView(layout);

        builder.setPositiveButton("Avançar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomeDespesa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(TelaPrincipal.this, "Não é possível criar uma Despesa sem nome", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else{

                    //Pega a data atual formatada para o formato brasileiro
                    SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
                    Date data = new Date();
                    String dataFormatada = formataData.format(data);

                    //objTr = new TransicaoDeDadosEntreActivities();


                    objTr.pessoa = new Pessoa();

                    //seta previamente dados sobre o rolê

                    objTr.despesa.dia = dataFormatada;
                    objTr.despesa.nome = nomeDespesa.getText().toString();

                    dialogoCadastroPessoa();

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

    private void dialogoCadastroPessoa() {

        LinearLayout layout = new LinearLayout(TelaPrincipal.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa o Edit text que será  chamado no diálogo
        final EditText nomePessoa = new EditText(TelaPrincipal.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomePessoa.setInputType(InputType.TYPE_CLASS_TEXT);

        nomePessoa.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        //Seta as dicas de cada Edit text criado
        nomePessoa.setHint("Insira ao menos um integrante");

        layout.addView(nomePessoa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Cadastro de novo  integrante");
        builder.setIcon(R.drawable.ic_add_person);

        //Coloca a view criada no diálogo
        builder.setView(layout);

        builder.setPositiveButton("Avançar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomePessoa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(TelaPrincipal.this, "O nome do integrante não pode ser nulo!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else {
                    if(isOnline(TelaPrincipal.this)){
                        try {
                            //Cria o despesa no banco e adiciona o novo integrante.
                            //Isso é  feito para evitar que rolês sejam criados sem nenhum integrante, caso o usuario chegue até essa tela e feche o app

                            String idRole = referencia.child(objTr.userUid).push().getKey();
                            String idPessoas = referencia.child(objTr.userUid).push().getKey();

                            //Termina de setar os dados faltantes e cadastra o rolê  junto do integrante incial ao banco

                            objTr.despesa.idDadosRole = idRole;
                            objTr.despesa.idDadosPessoas = idPessoas;

                            referencia.child(objTr.userUid).child(objTr.despesa.idDadosRole).child(objTr.despesa.idDadosRole).setValue(objTr.despesa);

                            Pessoa novoParticipante = new Pessoa();
                            novoParticipante.nome = nomePessoa.getText().toString();
                            novoParticipante.id = referencia.child(objTr.userUid).child(objTr.despesa.idDadosRole).child(objTr.despesa.idDadosPessoas).push().getKey();
                            referencia.child(objTr.userUid).child(objTr.despesa.idDadosRole).child(objTr.despesa.idDadosPessoas).child(novoParticipante.id).setValue(novoParticipante);

                            Gson gson = new Gson();
                            String extra = gson.toJson(objTr);
                            Intent it = new Intent(TelaPrincipal.this, TelaDespesa.class);
                            it.putExtra(EXTRA_UID, extra);
                            startActivityForResult(it, 2);
                        }catch (Exception e){
                            //Lidar  com o erro de conexão
                        }
                    }else{
                        //Lidar com o erro de conexão
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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
    }
}
