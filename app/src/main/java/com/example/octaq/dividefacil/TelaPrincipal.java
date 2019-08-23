package com.example.octaq.dividefacil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;

public class TelaPrincipal extends AppCompatActivity {

    //Para administrar a list view
    List<Role> roles;
    TransicaoDados objTr;

    //Controlando o banco de dados
    ArrayList <Pessoa> dados;
    FirebaseDatabase banco;
    DatabaseReference referencia;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //Variáveis do dialogo para criar novo role
    EditText nomeRole;
    AlertDialog alerta;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton novoRole = findViewById(R.id.fab_novo_role);
        novoRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoCadastroRole();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        banco = FirebaseDatabase.getInstance();
        referencia = banco.getReference();

        lv = findViewById(R.id.lv_historico_roles);

        //Carregando a list view sempre com os dados do banco
        referencia.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                roles = new ArrayList<Role>();
                for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                    String roleCadastrado = dadosDataSnapshot.getKey();
                    Role roleAtualizado = dadosDataSnapshot.child(roleCadastrado).getValue(Role.class);
                    roles.add(roleAtualizado);
                }
                //Inicializa array list, list view e cria um adapter para ela


                AdapterListaHistorico adapterListaHistorico = new AdapterListaHistorico(roles, TelaPrincipal.this);

                lv.setAdapter(adapterListaHistorico);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Chama a tela de pessoa, passando o id do usuário selecionado e
                objTr = new TransicaoDados(roles.get(position));
                Gson gson = new Gson();
                String extra = gson.toJson(objTr);
                if(!objTr.role.fechou){
                    Intent it = new Intent(TelaPrincipal.this, TelaConta.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivity(it);
                }else{
                    Intent it = new Intent(TelaPrincipal.this, TelaContaVisualizacao.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivity(it);
                }

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

                    String idRole = referencia.child(currentUser.getUid()).push().getKey();
                    String idPessoas = referencia.child(currentUser.getUid()).push().getKey();

                    Role novoRole = new Role(idRole,idPessoas,nomeRole.getText().toString(),dataFormatada);

                    referencia.child(currentUser.getUid()).child(novoRole.idDadosRole).child(novoRole.idDadosRole).setValue(novoRole);
                    TransicaoDados objTr = new TransicaoDados(novoRole);
                    Gson gson = new Gson();
                    String extra = gson.toJson(objTr);
                    Intent it  = new Intent(TelaPrincipal.this, TelaInicial.class);
                    it.putExtra(EXTRA_UID, extra);
                    startActivity(it);
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

}
