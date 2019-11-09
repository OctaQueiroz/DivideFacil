package com.example.octaq.dividefacil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import static com.example.octaq.dividefacil.TelaLogin.EXTRA_UID;
import static com.example.octaq.dividefacil.TelaLogin.mAuth;
import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class FragmentHistoricoDeDespesas extends Fragment {

    private static final String ARG_PARAM = "Dados do usuario";
    private TransicaoDeDadosEntreActivities objTr;
    //Para administrar a list view
    private List<Despesa> despesas;
    private ProgressDialog dialog;

    //Variáveis do dialogo para criar novo despesa
    private EditText nomeDespesa;
    private boolean[] checados;
    private AlertDialog alerta;
    private ListView lv;
    private FloatingActionButton novaDespesa;
    private AdapterParaListaDeDespesa adapterParaListaDeDespesa;

    ValueEventListener listenerDasDespesas;

    public FragmentHistoricoDeDespesas() {
        // Required empty public constructor
    }

    public static FragmentHistoricoDeDespesas newInstance(TransicaoDeDadosEntreActivities objTr) {
        FragmentHistoricoDeDespesas fragment = new FragmentHistoricoDeDespesas();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String extra = gson.toJson(objTr);
        args.putString(ARG_PARAM, extra);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            objTr = gson.fromJson(getArguments().getString(ARG_PARAM), TransicaoDeDadosEntreActivities.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_historico_de_despesas, container, false);

        novaDespesa = view.findViewById(R.id.fab_novo_role);

        novaDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  dialogoEscolhaDeTipoDeDespesa();
            }
        });

        lv = view.findViewById(R.id.lv_historico_roles);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try{
                objTr.despesa = despesas.get(position);
                Gson gson = new Gson();
                String extra = gson.toJson(objTr);
                if(!objTr.despesa.fechou){
                    Intent it;
                    if(objTr.despesa.tipoDeDespesa.equals("Bar e Restaurante")){
                        it = new Intent(getContext(), TelaDespesaBarERestaurante.class);
                    }else{
                        it = new Intent(getContext(), TelaDespesa.class);
                    }
                    it.putExtra(EXTRA_UID, extra);
                    startActivityForResult(it, 2);
                }else{
                    Intent it;
                    if(objTr.despesa.tipoDeDespesa.equals("Bar e Restaurante")){
                        it = new Intent(getContext(), TelaDespesaBarERestauranteVisualizacao.class);
                    }else{
                        it = new Intent(getContext(), TelaDespesaVisualizacao.class);
                    }
                    it.putExtra(EXTRA_UID, extra);
                    startActivityForResult(it, 2);
                }
            }catch (Exception e){
                //Lidar com problemas de conexão
            }
            }
        });

        listenerDasDespesas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                despesas = new ArrayList<>();

                Stack pilhaDeDespesas = new Stack<Despesa>();

                try{
                    for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                        Despesa despesaAtualizado = dadosDataSnapshot.child("Despesa").getValue(Despesa.class);
                        if(despesaAtualizado != null) {
                            if (!despesaAtualizado.excluido) {
                                pilhaDeDespesas.push(despesaAtualizado);
                            }
                        }
                    }
                    Despesa despesaDapilha;
                    while(!pilhaDeDespesas.empty()){
                        despesaDapilha = (Despesa)pilhaDeDespesas.pop();
                        despesas.add(despesaDapilha);
                    }
                }catch (Exception e){
                    //Lidar com erro de conexao
                    despesas = new ArrayList<>();
                }

                adapterParaListaDeDespesa = new AdapterParaListaDeDespesa(despesas, getContext(),objTr);

                lv.setAdapter(adapterParaListaDeDespesa);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Carregando suas despesas...");
        dialog.isIndeterminate();
        dialog.show();

        referencia.child(objTr.userUid).addValueEventListener(listenerDasDespesas);
    }

    @Override
    public void onPause() {
        super.onPause();
        referencia.child(objTr.userUid).removeEventListener(listenerDasDespesas);
    }

    private void dialogoEscolhaDeTipoDeDespesa() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Qual o tipo da despesa?");
        builder.setIcon(R.drawable.ic_filter);
        //Declara os  vetores de controle de quem será escolhido para participar na conta
        final TipoDeDespesa tipoDeDespesa = new TipoDeDespesa();
        //Vetor boolean para identificar quem foi e quem não foi selecionado
        checados = new boolean[tipoDeDespesa.listaDeTiposDeDespesa.size()];

        //adapter utilizando um layout customizado (TextView)
        AdapterParaListaDeTipoDeDespesa adapter = new AdapterParaListaDeTipoDeDespesa(tipoDeDespesa.listaDeTiposDeDespesa,this.getContext());

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

        LinearLayout layout = new LinearLayout(this.getContext());

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60,30,60,0);

        //Inicializa o Edit text que será  chamado no diálogo
        nomeDespesa = new EditText(this.getContext());

        //Seta o tipo de entrada aceitada pelo Edit Text
        nomeDespesa.setInputType(InputType.TYPE_CLASS_TEXT);

        //Seta as dicas de cada Edit text criado
        nomeDespesa.setHint("Insira o nome da despesa");
        nomeDespesa.setTypeface(ResourcesCompat.getFont(this.getContext(), R.font.cabin));

        layout.addView(nomeDespesa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogCustom);

        //Define o título do diálogo
        builder.setTitle("Criar nova Despesa");
        builder.setIcon(R.drawable.ic_add_income);
        //Coloca a view criada no diálogo
        builder.setView(layout);

        builder.setPositiveButton("Avançar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(nomeDespesa.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Não é possível criar uma Despesa sem nome", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }else{
                    //Pega a data atual formatada para o formato brasileiro
                    SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
                    Date data = new Date();
                    String dataFormatada = formataData.format(data);

                    objTr.pessoa = new Pessoa();
                    objTr.despesa.dia = dataFormatada;
                    objTr.despesa.nome = nomeDespesa.getText().toString().substring(0,1).toUpperCase() + nomeDespesa.getText().toString().substring(1);

                    if(isOnline(getActivity().getApplicationContext())){
                        try {
                            //Cria o despesa no banco e adiciona o novo integrante.
                            //Isso é  feito para evitar que rolês sejam criados sem nenhum integrante, caso o usuario chegue até essa tela e feche o app

                            String idRole = referencia.child(objTr.userUid).push().getKey();
                            String idPessoas = referencia.child(objTr.userUid).push().getKey();

                            //Termina de setar os dados faltantes e cadastra o rolê  junto do integrante incial ao banco

                            objTr.despesa.idDadosDespesa = idRole;
                            objTr.despesa.idDadosPessoas = idPessoas;
                            IntegrantesUsuariosDoFirebase novoIntegrante = new IntegrantesUsuariosDoFirebase(objTr.userUid);
                            objTr.despesa.uidIntegrantes.add(novoIntegrante);

                            referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Despesa").setValue(objTr.despesa);

                            Pessoa novoParticipante = new Pessoa();
                            String[] nomeUsuarioCompleto = mAuth.getCurrentUser().getDisplayName().split(" ");
                            if(nomeUsuarioCompleto.length>1){
                                novoParticipante.nome = nomeUsuarioCompleto[0] + " " + nomeUsuarioCompleto[nomeUsuarioCompleto.length-1];
                            }else{
                                novoParticipante.nome = nomeUsuarioCompleto[0];
                            }
                            novoParticipante.id = objTr.userUid;
                            referencia.child(objTr.userUid).child(objTr.despesa.idDadosDespesa).child("Integrantes").child(novoParticipante.id).setValue(novoParticipante);

                            Gson gson = new Gson();
                            String extra = gson.toJson(objTr);
                            Intent it;
                            if(objTr.despesa.tipoDeDespesa.equals("Bar e Restaurante")){
                                it = new Intent(getActivity().getApplicationContext(), TelaDespesaBarERestaurante.class);
                            }else{
                                it = new Intent(getActivity().getApplicationContext(), TelaDespesa.class);
                            }
                            it.putExtra(EXTRA_UID, extra);
                            startActivity(it);

                        }catch (Exception e){
                            //Lidar  com o erro de conexão
                        }
                    }else{
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Ocorreu um problema com a conexão à internet, por favor, tente novamente!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
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
