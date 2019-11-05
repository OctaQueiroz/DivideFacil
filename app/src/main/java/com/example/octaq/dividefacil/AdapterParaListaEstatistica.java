package com.example.octaq.dividefacil;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import java.text.DecimalFormat;
import java.util.List;

public class AdapterParaListaEstatistica extends BaseAdapter {

    List<DadosDespesaParaGraficos> lista;
    Context context;
    DecimalFormat df = new DecimalFormat("#,###.00");

    public AdapterParaListaEstatistica(List<DadosDespesaParaGraficos> lista, Context context){
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
        view = mInflater.inflate(R.layout.layout_lista_estatisticas, null);

        DecoView arcView = view.findViewById(R.id.grafico_porcentagem_despesa);
        TextView nomeDespesa = view.findViewById(R.id.tv_nome_despesa_grafico);
        TextView valorDespesa = view.findViewById(R.id.tv_valor_despesa_grafico);
        TextView porcentagemDespesa = view.findViewById(R.id.tv_porcentagem_despesa_grafico);

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 250, 250, 250))
                .setRange(0, 100, 100)
                .setLineWidth(10f)
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, lista.get(position).red, lista.get(position).green, lista.get(position).blue))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(10f)
                .setInterpolator(new OvershootInterpolator())
                .setShowPointWhenEmpty(false)
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(6000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder((int)lista.get(position).porcentagem).setIndex(series1Index).setDelay(1000).build());

        nomeDespesa.setText(lista.get(position).tipoDeDespesa);
        valorDespesa.setText("Valor gasto: R$"+df.format(lista.get(position).valor));
        porcentagemDespesa.setText(df.format(lista.get(position).porcentagem) + "%");


        return view;

    }
}
