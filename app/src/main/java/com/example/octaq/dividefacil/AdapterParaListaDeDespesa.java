package com.example.octaq.dividefacil;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class AdapterParaListaDeDespesa extends BaseAdapter {
    List<Despesa> lista;
    final Context context;
    private TransicaoDeDadosEntreActivities objTr;
    DecimalFormat df = new DecimalFormat("#,###.00");

    public AdapterParaListaDeDespesa(List<Despesa> lista, Context context, TransicaoDeDadosEntreActivities objTr){
        this.objTr = objTr;
        this.lista = lista;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // cria uma view com o layout  do seu item
        view = mInflater.inflate(R.layout.layout_lista_despesa, null);

        // Atribuição normal dos campos de uma view
        TextView nome = view.findViewById(R.id.tv_nome_historico);
        TextView valor = view.findViewById(R.id.tv_valor_historico);
        TextView status = view.findViewById(R.id.tv_status_historico);
        TextView data = view.findViewById(R.id.tv_data_historico);
        final ImageView deletar = view.findViewById(R.id.iv_delete);

        deletar.setTag(position);

        deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletarDespesa(v);
            }
        });
        nome.setText(lista.get(position).nome);
        if(lista.get(position).fechou){
            if(lista.get(position).valorRoleFechado > 0.0){
                valor.setText("Valor: R$"+df.format(lista.get(position).valorRoleFechado));
            }else{
                valor.setText("Valor: R$00,00");
            }
        }else{
            if(lista.get(position).valorRoleAberto > 0.0){
                valor.setText("Valor: R$"+df.format(lista.get(position).valorRoleAberto));
            }else{
                valor.setText("Valor: R$00,00");
            }
        }


        if(lista.get(position).fechou){
            status.setText("Conta fechada");
        }else{
            status.setText("Conta aberta");
        }
        data.setText("Criação da despesa: "+ lista.get(position).dia);

        return view;

    }

    public void deletarDespesa(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        final View view = v;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80,30,80,0);

        TextView textoAlerta = new TextView(context);
        textoAlerta.setTypeface(ResourcesCompat.getFont(context, R.font.cabin));
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
                Despesa despesaTemp = lista.get(tag);
                despesaTemp.excluido =true;
                if(isOnline(context)){
                    try{
                        referencia.child(objTr.userUid).child(despesaTemp.idDadosDespesa).child("Despesa").setValue(despesaTemp);
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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
    }
}