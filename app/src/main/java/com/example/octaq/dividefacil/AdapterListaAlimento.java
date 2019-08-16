package com.example.octaq.dividefacil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterListaAlimento extends BaseAdapter {

    List<Alimento> lista;
    Context context;
    DecimalFormat df = new DecimalFormat("#,###.00");

    public AdapterListaAlimento(List<Alimento> lista, Context context){
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
        view = mInflater.inflate(R.layout.layout_lista_item_valor, null);

        // Atribuição normal dos campos de uma view
        TextView campo1 = view.findViewById(R.id.textView_campo1);
        TextView campo2 = view.findViewById(R.id.textView_campo2);

        campo1.setText(lista.get(position).nome);
        campo2.setText("Valor: R$"+df.format(lista.get(position).valor));

        return view;

    }
}