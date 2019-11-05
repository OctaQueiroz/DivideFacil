package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import java.util.ArrayList;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class FragmentEstatisticasDoUsuario extends Fragment {

    private static final String ARG_PARAM = "Dados do usuario";
    private TransicaoDeDadosEntreActivities objTr;

    //Para administrar a list view
    List<Despesa> despesas;
    ProgressDialog dialog;

    //Variáveis do dialogo para criar novo despesa
    ListView lv;
    AdapterParaListaEstatistica adapterParaListaEstatistica;
    DecoView arcView;
    List<DadosDespesaParaGraficos> listaDadosDespesaParaGraficos;
    TextView textPercentage;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            objTr = gson.fromJson(getArguments().getString(ARG_PARAM), TransicaoDeDadosEntreActivities.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragment_estatisticas_do_usuario, container, false);

        arcView = view.findViewById(R.id.dynamicArcView);
        lv = view.findViewById(R.id.lv_graficos_despesas);
        textPercentage = view.findViewById(R.id.tv_graph);
        textPercentage.setTypeface((ResourcesCompat.getFont(getContext(), R.font.cabin)));

        dialog = ProgressDialog.show(getActivity(), "","Carregando suas Despesas...", true);

        listaDadosDespesaParaGraficos = new ArrayList<>();

        adapterParaListaEstatistica = new AdapterParaListaEstatistica(listaDadosDespesaParaGraficos, getContext());
        lv.setAdapter(adapterParaListaEstatistica);

        referencia.child(objTr.userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                dialog.show();

                despesas = new ArrayList<>();

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
                        if(despesas.get(i).tipoDeDespesa.equals("Bar e Restaurante")){
                            //for(int  j = 0;  j < despesas.get(i).){

                            //}
                        }
                    }
                }catch (Exception e){
                    //Lidar com erro de conexao
                }

                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(1000, "Bar e Restaurante", 50, objTr.daltonismo));
                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(700, "Transporte", 15, objTr.daltonismo));
                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(400, "Saude", 10, objTr.daltonismo));
                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(200, "Supermercado", 9, objTr.daltonismo));
                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(180, "Contas de Casa", 7, objTr.daltonismo));
                listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(100, "Lazer", 3, objTr.daltonismo));

                adapterParaListaEstatistica.notifyDataSetChanged();
                ajustaOTamanhoDaListViewParaOcuparTodaAScrollView(lv);
                dialog.dismiss();
                plotaGraficos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void plotaGraficos(){

        SeriesItem seriesItem1;
        SeriesItem seriesItem2;
        SeriesItem seriesItem3;
        SeriesItem seriesItem4;
        SeriesItem seriesItem5;
        SeriesItem seriesItem6;
        int series1Index;
        int series2Index;
        int series3Index;
        int series4Index;
        int series5Index;
        int series6Index;
        if(listaDadosDespesaParaGraficos.size()>0){
            arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                    .setRange(0, 100, 100)
                    .setLineWidth(49f)
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
            arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(0).porcentagem)).setIndex(series1Index).setDelay(1000).build());
            if(listaDadosDespesaParaGraficos.size()>1){
                arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                        .setRange(0, 100, 100)
                        .setInset(new PointF(50f, 50f))
                        .setLineWidth(49f)
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
                arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(1).porcentagem)).setIndex(series2Index).setDelay(1000).build());
                if(listaDadosDespesaParaGraficos.size()>2){
                    arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                            .setRange(0, 100, 100)
                            .setInset(new PointF(100f, 100f))
                            .setLineWidth(49f)
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
                    arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(2).porcentagem)).setIndex(series3Index).setDelay(1000).build());
                    if(listaDadosDespesaParaGraficos.size()>3){
                        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                .setRange(0, 100, 100)
                                .setInset(new PointF(150f, 150f))
                                .setLineWidth(49f)
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
                        arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(3).porcentagem)).setIndex(series4Index).setDelay(1000).build());
                        if(listaDadosDespesaParaGraficos.size()>4){
                            arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                    .setRange(0, 100, 100)
                                    .setInset(new PointF(200f, 200f))
                                    .setLineWidth(49f)
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
                            arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(4).porcentagem)).setIndex(series5Index).setDelay(1000).build());
                            if(listaDadosDespesaParaGraficos.size()>5){
                                arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                                        .setRange(0, 100, 100)
                                        .setInset(new PointF(250f, 250f))
                                        .setLineWidth(49f)
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
                                arcView.addEvent(new DecoEvent.Builder((int)(listaDadosDespesaParaGraficos.get(5).porcentagem)).setIndex(series6Index).setDelay(1000).build());
                            }
                        }
                    }
                }
            }
        }

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDuration(2000)
                .build());


        textPercentage.setText("R$1000,00");
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

    public static boolean isOnline(Context context) {
        ConnectivityManager administradorDeConexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacoesDeConexao = administradorDeConexao.getActiveNetworkInfo();
        if (informacoesDeConexao != null && informacoesDeConexao.isConnected())
            return true;
        else
            return false;
        }
    }
