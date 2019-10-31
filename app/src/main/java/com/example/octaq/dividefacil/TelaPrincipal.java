package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    ProgressDialog dialog;

    //Variáveis do dialogo para criar novo despesa
    EditText nomeDespesa;
    boolean[] checados;
    AlertDialog alerta;
    ListView lv;


    FrameLayout containerDosFragments;
    TabLayout tabLayout;
    Fragment telaDeGraficos;
    Fragment telaDeHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TelaPrincipal.this, R.color.colorPrimaryDark));

        //containerDosFragments = findViewById(R.id.containerForFragment);
        tabLayout = findViewById(R.id.nav_tabs);

        objTr = new TransicaoDeDadosEntreActivities();

        //pega o id do usuário atual, para ser utilizado e salva no nó de usuários os dados para acesso de demais usuários
        objTr.userUid = mAuth.getCurrentUser().getUid();
        objTr.userEmail = mAuth.getCurrentUser().getEmail();
        String[] nomeUsuarioAtual = mAuth.getCurrentUser().getDisplayName().split(" ");
        String nomeASerSalvo;
        if (nomeUsuarioAtual.length > 1) {
            nomeASerSalvo = nomeUsuarioAtual[0] + " " + nomeUsuarioAtual[nomeUsuarioAtual.length - 1];
        } else {
            nomeASerSalvo = nomeUsuarioAtual[0];
        }
        UsuarioAutenticadoDoFirebase usuarioAtual = new UsuarioAutenticadoDoFirebase(objTr.userUid, objTr.userEmail, nomeASerSalvo);
        //String ref = referencia.child("users").push().getKey();

        referencia.child("AAAAAUSERS").child(objTr.userUid).setValue(usuarioAtual);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        telaDeGraficos = FragmentEstatisticasDoUsuario.newInstance(objTr);
        managerFragment(telaDeGraficos, "Fragment graficos");

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    telaDeGraficos = FragmentEstatisticasDoUsuario.newInstance(objTr);
                    managerFragment(telaDeGraficos, "Fragment graficos");
                } else if (tab.getPosition() == 1) {
                    telaDeHistorico = FragmentHistoricoDeDespesas.newInstance(objTr);
                    managerFragment(telaDeHistorico, "Fragment historico");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

    private void managerFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerForFragment, fragment, tag);
        fragmentTransaction.commit();
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
