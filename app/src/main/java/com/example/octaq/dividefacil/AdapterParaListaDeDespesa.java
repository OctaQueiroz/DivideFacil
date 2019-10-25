package com.example.octaq.dividefacil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterParaListaDeDespesa extends BaseAdapter {
    List<Despesa> lista;
    Context context;
    DecimalFormat df = new DecimalFormat("#,###.00");

    public AdapterParaListaDeDespesa(List<Despesa> lista, Context context){
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
        ImageView deletar = view.findViewById(R.id.iv_delete);

        nome.setText(lista.get(position).nome);
        if(lista.get(position).fechou){
            if(lista.get(position).valorRoleFechado > 0.0){
                valor.setText("R$"+df.format(lista.get(position).valorRoleFechado));
            }else{
                valor.setText("R$00,00");
            }
        }else{
            if(lista.get(position).valorRoleAberto > 0.0){
                valor.setText("R$"+df.format(lista.get(position).valorRoleAberto));
            }else{
                valor.setText("R$00,00");
            }
        }


        if(lista.get(position).fechou){
            status.setText("Conta fechada");
        }else{
            status.setText("Conta aberta");
        }
        data.setText(lista.get(position).dia);
        deletar.setTag(position);
        return view;

    }
}