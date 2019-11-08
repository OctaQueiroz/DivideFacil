package com.example.octaq.dividefacil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import java.util.List;

public class AdapterParaListaDeTipoDeDaltonismo extends BaseAdapter {

    List<String> lista;
    Context context;

    public AdapterParaListaDeTipoDeDaltonismo(List<String> lista, Context context){
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

        // Cria uma view com o layout  do seu item
        view = mInflater.inflate(R.layout.layout_lista_tipos_de_daltonismo, null);

        // Atribuição normal dos campos de uma view
        TextView campo1 = view.findViewById(R.id.tv_nome_tipo_daltonismo);
        campo1.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.cabin));
        campo1.setText(lista.get(position));

        return view;

    }
}
