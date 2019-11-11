package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class FragmentEstatisticasDoUsuario extends Fragment {

    private static final String ARG_PARAM = "Dados do usuario";
    private TransicaoDeDadosEntreActivities objTr;

    List<Despesa> despesas;
    ProgressDialog dialog;
    ListView lv;
    AdapterParaListaEstatistica adapterParaListaEstatistica;
    DecoView arcView;
    List<DadosDespesaParaGraficos> listaDadosDespesaParaGraficos;
    TextView textPercentage;
    double valorTotalGastoPessoal;
    ValueEventListener listenerDasDespesas;
    DecimalFormat df = new DecimalFormat("#,###.00");
    private AlertDialog alerta;

    public FragmentEstatisticasDoUsuario() {
        // Required empty public constructor
    }

    public static FragmentEstatisticasDoUsuario newInstance(TransicaoDeDadosEntreActivities objTr) {
        FragmentEstatisticasDoUsuario fragment = new FragmentEstatisticasDoUsuario();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String extra = gson.toJson(objTr);
        args.putString(ARG_PARAM, extra);
        fragment.setArguments(args);
        return fragment;
    }

    public interface metodosFragmentEstatisticas{
        public void plotaGraficos();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //dialog = ProgressDialog.show(getActivity(),"","Carregando suas despesas...",true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            objTr = gson.fromJson(getArguments().getString(ARG_PARAM), TransicaoDeDadosEntreActivities.class);
        }


        listenerDasDespesas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                despesas = new ArrayList<>();
                listaDadosDespesaParaGraficos = new ArrayList<>();

                try{
                    for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                        Despesa despesaAtualizado = dadosDataSnapshot.child("Despesa").getValue(Despesa.class);
                        if(despesaAtualizado != null) {
                            if (!despesaAtualizado.excluido) {
                                despesas.add(despesaAtualizado);
                            }
                        }
                    }
                    double gastoBarERestaurante = 0.0;
                    double gastoTransporte = 0.0;
                    double gastoSaude = 0.0;
                    double gastoSupermercado = 0.0;
                    double gastoContasDeCasa = 0.0;
                    double gastoLazer = 0.0;

                    for(int i = 0; i < despesas.size(); i++){
                        if(despesas.get(i).tipoDeDespesa.equals("Bar e Restaurante") && despesas.get(i).fechou){
                            gastoBarERestaurante += pegaValorPessoal(despesas.get(i));
                        }else if(despesas.get(i).tipoDeDespesa.equals("Transporte") && despesas.get(i).fechou){
                            gastoTransporte += pegaValorPessoal(despesas.get(i));
                        }else if(despesas.get(i).tipoDeDespesa.equals("Saude")&& despesas.get(i).fechou){
                            gastoSaude += pegaValorPessoal(despesas.get(i));
                        }else if(despesas.get(i).tipoDeDespesa.equals("Supermercado")&& despesas.get(i).fechou){
                            gastoSupermercado += pegaValorPessoal(despesas.get(i));
                        }else if(despesas.get(i).tipoDeDespesa.equals("Contas de Casa")&& despesas.get(i).fechou){
                            gastoContasDeCasa += pegaValorPessoal(despesas.get(i));
                        }else if(despesas.get(i).tipoDeDespesa.equals("Lazer")&& despesas.get(i).fechou){
                            gastoLazer += pegaValorPessoal(despesas.get(i));
                        }
                    }
                    valorTotalGastoPessoal = 0.0;
                    valorTotalGastoPessoal += gastoBarERestaurante + gastoTransporte + gastoSaude + gastoSupermercado + gastoContasDeCasa + gastoLazer;

                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoBarERestaurante, "Bar e Restaurante", calculaPorcentagemGasto(gastoBarERestaurante), objTr.daltonismo));
                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoTransporte, "Transporte", calculaPorcentagemGasto(gastoTransporte), objTr.daltonismo));
                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoSaude, "Saude", calculaPorcentagemGasto(gastoSaude), objTr.daltonismo));
                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoSupermercado, "Supermercado", calculaPorcentagemGasto(gastoSupermercado), objTr.daltonismo));
                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoContasDeCasa, "Contas de Casa", calculaPorcentagemGasto(gastoContasDeCasa), objTr.daltonismo));
                    listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(gastoLazer, "Lazer", calculaPorcentagemGasto(gastoLazer), objTr.daltonismo));

                }catch (Exception e){
                    //Lidar com erro de conexao
                }

                ordenaListaDeDespesasporGasto();

                //adapterParaListaEstatistica.notifyDataSetChanged();
                adapterParaListaEstatistica = new AdapterParaListaEstatistica(listaDadosDespesaParaGraficos, getContext());
                lv.setAdapter(adapterParaListaEstatistica);

                ajustaOTamanhoDaListViewParaOcuparTodaAScrollView(lv);

                dialog.dismiss();

                plotaGraficos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragment_estatisticas_do_usuario, container, false);

        arcView = view.findViewById(R.id.dynamicArcView);
        lv = view.findViewById(R.id.lv_graficos_despesas);
        textPercentage = view.findViewById(R.id.tv_graph);
        textPercentage.setTypeface((ResourcesCompat.getFont(getContext(), R.font.cabin)));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        dialog = ProgressDialog.show(getActivity(),"","Carregando suas despesas...",true,false);

        referencia.child(objTr.userUid).addValueEventListener(listenerDasDespesas);
    }

    @Override
    public void onPause() {
        super.onPause();
        referencia.child(objTr.userUid).removeEventListener(listenerDasDespesas);
    }


    public float calculaPorcentagemGasto(double valor){
        return (float) ((valor/valorTotalGastoPessoal)*100);
    }

    public double pegaValorPessoal(Despesa despesa){
        for(int i = 0; i < despesa.uidIntegrantes.size(); i++){
            if(despesa.uidIntegrantes.get(i).uid.equals(objTr.userUid)){
                return despesa.uidIntegrantes.get(i).gasto;
            }
        }
        return 0.0;
    }

    public void ordenaListaDeDespesasporGasto(){

        if(listaDadosDespesaParaGraficos.size()>0){
            DadosDespesaParaGraficos dadoAuxiliarTemporario;
            for(int i = 0;  i < listaDadosDespesaParaGraficos.size();i++){
                dadoAuxiliarTemporario = listaDadosDespesaParaGraficos.get(i);
                for(int j = i; j < listaDadosDespesaParaGraficos.size(); j++){
                    if(listaDadosDespesaParaGraficos.get(j).valor > dadoAuxiliarTemporario.valor){
                        listaDadosDespesaParaGraficos.add(i,listaDadosDespesaParaGraficos.get(j));
                        listaDadosDespesaParaGraficos.remove(i+1);
                        listaDadosDespesaParaGraficos.add(j,dadoAuxiliarTemporario);
                        listaDadosDespesaParaGraficos.remove(j+1);

                        dadoAuxiliarTemporario = listaDadosDespesaParaGraficos.get(i);
                    }
                }
            }
        }


    }
    public void plotaGraficos(){

        final SeriesItem seriesItem0;
        SeriesItem seriesItem1;
        SeriesItem seriesItem2;
        SeriesItem seriesItem3;
        SeriesItem seriesItem4;
        SeriesItem seriesItem5;
        SeriesItem seriesItem6;
        int series0Index;
        int series1Index;
        int series2Index;
        int series3Index;
        int series4Index;
        int series5Index;
        int series6Index;

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 255, 255, 255))
                .setRange(0, 100, 100)
                .setLineWidth(0f)
                .build());
        seriesItem0 = new SeriesItem.Builder(Color.argb(255, 255, 255, 255))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(0f)
                .setInterpolator(new OvershootInterpolator())
                .setShowPointWhenEmpty(false)
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(4000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();



        seriesItem0.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float numeroMostrado = (float) (percentComplete * valorTotalGastoPessoal);
                if(currentPosition>0 && numeroMostrado <= valorTotalGastoPessoal){
                    textPercentage.setText("R$"+df.format(numeroMostrado));
                }else if(currentPosition>0){
                    textPercentage.setText("R$"+df.format(valorTotalGastoPessoal));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });
        series0Index = arcView.addSeries(seriesItem0);
        arcView.addEvent(new DecoEvent.Builder((int)(100)).setIndex(series0Index).setDelay(250).build());

        if(listaDadosDespesaParaGraficos.size()>0){
            arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                    .setRange(0, 100, 100)
                    .setLineWidth(20f)
                    .build());
            seriesItem1 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(0).red, listaDadosDespesaParaGraficos.get(0).green, listaDadosDespesaParaGraficos.get(0).blue))
                    .setRange(0, 100, 0)
                    .setInitialVisibility(false)
                    .setLineWidth(50f)
                    .setInterpolator(new OvershootInterpolator())
                    .setShowPointWhenEmpty(false)
                    .setDrawAsPoint(false)
                    .setSpinClockwise(true)
                    .setSpinDuration(6000)
                    .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                    .build();
            series1Index = arcView.addSeries(seriesItem1);
            arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(0).porcentagem)).setIndex(series1Index).setDelay(250).build());
            if(listaDadosDespesaParaGraficos.size()>1){
                arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                        .setRange(0, 100, 100)
                        .setInset(new PointF(50f, 50f))
                        .setLineWidth(20f)
                        .build());
                seriesItem2 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(1).red, listaDadosDespesaParaGraficos.get(1).green, listaDadosDespesaParaGraficos.get(1).blue))
                        .setRange(0, 100, 0)
                        .setInitialVisibility(false)
                        .setLineWidth(50f)
                        .setInterpolator(new OvershootInterpolator())
                        .setShowPointWhenEmpty(false)
                        .setInset(new PointF(50f, 50f))
                        .setDrawAsPoint(false)
                        .setSpinClockwise(true)
                        .setSpinDuration(6000)
                        .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                        .build();
                series2Index = arcView.addSeries(seriesItem2);
                arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(1).porcentagem)).setIndex(series2Index).setDelay(250).build());
                if(listaDadosDespesaParaGraficos.size()>2){
                    arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                            .setRange(0, 100, 100)
                            .setInset(new PointF(100f, 100f))
                            .setLineWidth(20f)
                            .build());
                    seriesItem3 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(2).red, listaDadosDespesaParaGraficos.get(2).green, listaDadosDespesaParaGraficos.get(2).blue))
                            .setRange(0, 100, 0)
                            .setInitialVisibility(false)
                            .setLineWidth(50f)
                            .setInterpolator(new OvershootInterpolator())
                            .setShowPointWhenEmpty(false)
                            .setInset(new PointF(100f, 100f))
                            .setDrawAsPoint(false)
                            .setSpinClockwise(true)
                            .setSpinDuration(6000)
                            .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                            .build();
                    series3Index = arcView.addSeries(seriesItem3);
                    arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(2).porcentagem)).setIndex(series3Index).setDelay(250).build());
                    if(listaDadosDespesaParaGraficos.size()>3){
                        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                .setRange(0, 100, 100)
                                .setInset(new PointF(150f, 150f))
                                .setLineWidth(20f)
                                .build());
                        seriesItem4 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(3).red, listaDadosDespesaParaGraficos.get(3).green, listaDadosDespesaParaGraficos.get(3).blue))
                                .setRange(0, 100, 0)
                                .setInitialVisibility(false)
                                .setLineWidth(50f)
                                .setInterpolator(new OvershootInterpolator())
                                .setShowPointWhenEmpty(false)
                                .setInset(new PointF(150f, 150f))
                                .setDrawAsPoint(false)
                                .setSpinClockwise(true)
                                .setSpinDuration(6000)
                                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                                .build();
                        series4Index = arcView.addSeries(seriesItem4);
                        arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(3).porcentagem)).setIndex(series4Index).setDelay(250).build());
                        if(listaDadosDespesaParaGraficos.size()>4){
                            arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                    .setRange(0, 100, 100)
                                    .setInset(new PointF(200f, 200f))
                                    .setLineWidth(20f)
                                    .build());
                            seriesItem5 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(4).red, listaDadosDespesaParaGraficos.get(4).green, listaDadosDespesaParaGraficos.get(4).blue))
                                    .setRange(0, 100, 0)
                                    .setInitialVisibility(false)
                                    .setLineWidth(50f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setShowPointWhenEmpty(false)
                                    .setInset(new PointF(200f, 200f))
                                    .setDrawAsPoint(false)
                                    .setSpinClockwise(true)
                                    .setSpinDuration(6000)
                                    .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                                    .build();
                            series5Index = arcView.addSeries(seriesItem5);
                            arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(4).porcentagem)).setIndex(series5Index).setDelay(250).build());
                            if(listaDadosDespesaParaGraficos.size()>5){
                                arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                        .setRange(0, 100, 100)
                                        .setInset(new PointF(250f, 250f))
                                        .setLineWidth(20f)
                                        .build());
                                seriesItem6 = new SeriesItem.Builder(Color.argb(255, listaDadosDespesaParaGraficos.get(5).red, listaDadosDespesaParaGraficos.get(5).green, listaDadosDespesaParaGraficos.get(5).blue))
                                        .setRange(0, 100, 0)
                                        .setInitialVisibility(false)
                                        .setLineWidth(50f)
                                        .setInterpolator(new OvershootInterpolator())
                                        .setShowPointWhenEmpty(false)
                                        .setInset(new PointF(250f, 250f))
                                        .setDrawAsPoint(false)
                                        .setSpinClockwise(true)
                                        .setSpinDuration(6000)
                                        .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                                        .build();
                                series6Index = arcView.addSeries(seriesItem6);
                                arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(5).porcentagem)).setIndex(series6Index).setDelay(250).build());
                            }
                        }
                    }
                }
            }
        }

        arcView.configureAngles(360, 0);
    }



    public static void ajustaOTamanhoDaListViewParaOcuparTodaAScrollView(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
        View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
        (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
