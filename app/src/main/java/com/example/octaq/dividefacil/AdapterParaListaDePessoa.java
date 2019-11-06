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

public class AdapterParaListaDePessoa extends BaseAdapter {

    List<Pessoa> lista;
    Context context;
    TransicaoDeDadosEntreActivities objTr;
    DecimalFormat df = new DecimalFormat("#,###.00");

    public AdapterParaListaDePessoa(List<Pessoa> lista,TransicaoDeDadosEntreActivities objTr, Context context){
        this.lista = lista;
        this.context = context;
        this.objTr = objTr;
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

        // Atribuição normal dos campos de uma view
        TextView campo1 = view.findViewById(R.id.tv_nome_despesa_grafico);
        TextView campo2 = view.findViewById(R.id.tv_valor_despesa_grafico);

        campo1.setText(lista.get(position).nome);

        if(lista.get(position).valorTotal > 0.0){
            campo2.setText("R$"+df.format(lista.get(position).valorTotal));
        }else{
            campo2.setText("R$00.00");
        }

        final DadosDespesaParaGraficos dadosParaEstatisticasPessoa = new DadosDespesaParaGraficos(objTr.despesa.tipoDeDespesa, objTr.daltonismo);

        DecoView arcView = view.findViewById(R.id.grafico_porcentagem_despesa);
        final TextView porcentagemDespesa = view.findViewById(R.id.tv_porcentagem_despesa_grafico);

        final float porcentagem = (float)(lista.get(position).valorTotal/objTr.despesa.valorRoleAberto*100);

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setLineWidth(15f)
                .build());

        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, dadosParaEstatisticasPessoa.red, dadosParaEstatisticasPessoa.green, dadosParaEstatisticasPessoa.blue))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(15f)
                .setInterpolator(new OvershootInterpolator())
                .setShowPointWhenEmpty(false)
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(6000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        porcentagemDespesa.setText("00,00%");

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if(currentPosition>0){
                    porcentagemDespesa.setText(df.format(percentComplete * porcentagem)+"%");
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder((int)porcentagem).setIndex(series1Index).setDelay(1000).build());

        return view;

    }
}
