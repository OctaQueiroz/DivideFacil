package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    List<Role> roles;
    TransicaoDados objTr;
    ImageView deletar;
    ProgressDialog dialog;

    //Variáveis do dialogo para criar novo role
    EditText nomeRole;
    AlertDialog alerta;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        objTr = new TransicaoDados();

        //pega o id do usuário atual, para ser utilizado
        objTr.userUid = mAuth.getCurrentUser().getUid();

        dialog = ProgressDialog.show(TelaPrincipal.this, "",
                "Carregando seus Rolês...", true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        deletar = findViewById(R.id.iv_delete);
        FloatingActionButton novoRole = findViewById(R.id.fab_novo_role);
        novoRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoCadastroRole();
            }
        });

        lv = findViewById(R.id.lv_historico_roles);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o id do usuário selecionado e os  meios dde comunicação com o banco
                objTr.role = roles.get(position);
                Gson gson = new Gson();
                String extra = gson.toJson(objTr);
                if(!objTr.role.fechou){
                    Intent it = new Intent(TelaPrincipal.this, TelaConta.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivityForResult(it, 2);
                }else{
                    Intent it = new Intent(TelaPrincipal.this, TelaContaVisualizacao.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivityForResult(it, 2);
                }

            }
        });
        //Carregando a list view sempre com os dados do banco
        referencia.child(objTr.userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.show();

                roles = new ArrayList<>();
                for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                    String roleCadastrado = dadosDataSnapshot.getKey();
                    Role roleAtualizado = dadosDataSnapshot.child(roleCadastrado).getValue(Role.class);
                    roles.add(roleAtualizado);
                }

                //Cria um adapter para a list View
                AdapterListaHistorico adapterListaHistorico = new AdapterListaHistorico(roles, TelaPrincipal.this);

                lv.setAdapter(adapterListaHistorico);
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
                this.objTr = gson.fromJson(resultado, TransicaoDados.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this);

            final View view = v;
            //Define o título do diálogo
            builder.setTitle("Apagar");

            builder.setMessage("Deseja remover permanentemente o Rolê do seu histórico?");

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    int tag = (Integer) view.getTag();
                    Role roleTemp = roles.get(tag);

                    referencia.child(objTr.userUid).child(roleTemp.idDadosRole).child(roleTemp.idDadosRole).removeValue();
                    referencia.child(objTr.userUid).child(roleTemp.idDadosRole).child(roleTemp.idDadosPessoas).removeValue();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Logoff");

        builder.setMessage("Deseja desconectar da sua conta?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                //Este metodo desconecta a conta google do usuário do fire base
                revokeAccess();
                FirebaseAuth.getInstance().signOut();
                Intent it  = new Intent(TelaPrincipal.this, TelaLogin.class);
                startActivity(it);
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

    private void dialogoCadastroRole() {

        //Inicializa o Edit text que será  chamado no diálogo
        nomeRole = new EditText(TelaPrincipal.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomeRole.setInputType(InputType.TYPE_CLASS_TEXT);

        //Seta as dicas de cada Edit text criado
        nomeRole.setHint("Insira o nome do Rolê");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Criar novo Rolê");

        //Coloca a view criada no diálogo
        builder.setView(nomeRole);

        builder.setPositiveButton("Criar novo Rolê", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomeRole.getText().equals("")){
                    Toast toast = Toast.makeText(TelaPrincipal.this, "Não é possível criar um Rolê sem nome", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else{

                    //Pega a data atual formatada para o formato brasileiro
                    SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
                    Date data = new Date();
                    String dataFormatada = formataData.format(data);

                    //objTr = new TransicaoDados();

                    objTr.role = new Role();
                    objTr.pessoa = new Pessoa();

                    //seta previamente dados sobre o rolê

                    objTr.role.dia = dataFormatada;
                    objTr.role.nome = nomeRole.getText().toString();

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

        //Inicializa o Edit text que será  chamado no diálogo
        final EditText nomePessoa = new EditText(TelaPrincipal.this);

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomePessoa.setInputType(InputType.TYPE_CLASS_TEXT);

        //Seta as dicas de cada Edit text criado
        nomePessoa.setHint("Insira ao menos um integrante para o rolê");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Define o título do diálogo
        builder.setTitle("Cadastro de nova Pessoa");

        //Coloca a view criada no diálogo
        builder.setView(nomePessoa);

        builder.setPositiveButton("Confirmar Cadastro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomePessoa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(TelaPrincipal.this, "O nome do integrante não pode ser nulo!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else {
                    //Cria o rolê no banco e adiciona o novo integrante.
                    //Isso é  feito para evitar que rolês sejam criados sem nenhum integrante, caso o usuario chegue até essa tela e feche o app

                    String idRole = referencia.child(objTr.userUid).push().getKey();
                    String idPessoas = referencia.child(objTr.userUid).push().getKey();

                    //Termina de setar os dados faltantes e cadastra o rolê  junto do integrante incial ao banco

                    objTr.role.idDadosRole = idRole;
                    objTr.role.idDadosPessoas = idPessoas;

                    referencia.child(objTr.userUid).child(objTr.role.idDadosRole).child(objTr.role.idDadosRole).setValue(objTr.role);

                    Pessoa novoParticipante = new Pessoa();
                    novoParticipante.nome = nomePessoa.getText().toString();
                    novoParticipante.id = referencia.child(objTr.userUid).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).push().getKey();
                    referencia.child(objTr.userUid).child(objTr.role.idDadosRole).child(objTr.role.idDadosPessoas).child(novoParticipante.id).setValue(novoParticipante);

                    Gson gson = new Gson();
                    String extra = gson.toJson(objTr);
                    Intent it = new Intent(TelaPrincipal.this, TelaConta.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivityForResult(it, 2);
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
}
