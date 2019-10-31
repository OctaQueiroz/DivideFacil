package com.example.octaq.dividefacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.ArrayList;
import java.util.List;

import static com.example.octaq.dividefacil.TelaLogin.referencia;

public class FragmentEstatisticasDoUsuario extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "Dados do usuario";
    private TransicaoDeDadosEntreActivities objTr;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Para administrar a list view
    List<Despesa> despesas;
    ProgressDialog dialog;

    //Variáveis do dialogo para criar novo despesa
    EditText nomeDespesa;
    boolean[] checados;
    AlertDialog alerta;
    ListView lv;

    public FragmentEstatisticasDoUsuario() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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
        View view = inflater.inflate(R.layout.fragment_fragment_estatisticas_do_usuario, container, false);

        DecoView arcView = view.findViewById(R.id.dynamicArcView);
        lv = view.findViewById(R.id.lv_graficos_despesas);

        dialog = ProgressDialog.show(getActivity(), "","Carregando suas Despesas...", true);

        final List<DadosDespesaParaGraficos> listaDadosDespesaParaGraficos = new ArrayList<>();
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(1000, "Bar e Restaurante", 50));
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(700, "Transporte", 15));
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(400, "Saude", 10));
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(200, "Supermercado", 9));
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(180, "Contas de Casa", 7));
        listaDadosDespesaParaGraficos.add(new DadosDespesaParaGraficos(100, "Lazer", 3));

        referencia.child(objTr.userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                dialog.show();

                despesas = new ArrayList<>();
                if(isOnline(getContext())){
                    try{
                        for(DataSnapshot dadosDataSnapshot: dataSnapshot.getChildren()){
                            Despesa despesaAtualizado = dadosDataSnapshot.child("Despesa").getValue(Despesa.class);
                            if(despesaAtualizado != null) {
                                if (!despesaAtualizado.excluido) {
                                    despesas.add(despesaAtualizado);
                                }
                            }
                        }
                    }catch (Exception e){
                        //Lidar com erro de conexao
                    }
                }else{
                    //Lidar com erro de conexao
                }
                //Cria um adapter para a list View

                AdapterParaListaEstatistica adapterParaListaEstatistica = new AdapterParaListaEstatistica(listaDadosDespesaParaGraficos, getContext());
                lv.setAdapter(adapterParaListaEstatistica);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Create background track

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setLineWidth(49f)
                .build());
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setInset(new PointF(50f, 50f))
                .setLineWidth(49f)
                .build());
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setInset(new PointF(100f, 100f))
                .setLineWidth(49f)
                .build());
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setInset(new PointF(150f, 150f))
                .setLineWidth(49f)
                .build());
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setInset(new PointF(200f, 200f))
                .setLineWidth(49f)
                .build());
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 245, 245, 245))
                .setRange(0, 100, 100)
                .setInset(new PointF(250f, 250f))
                .setLineWidth(49f)
                .build());

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
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
        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.argb(255, 50, 88, 50))
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
        SeriesItem seriesItem3 = new SeriesItem.Builder(Color.argb(255, 62, 178, 100))
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
        SeriesItem seriesItem4 = new SeriesItem.Builder(Color.argb(255, 64, 196, 33))
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

        SeriesItem seriesItem5 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
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

        SeriesItem seriesItem6 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
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

        int series1Index = arcView.addSeries(seriesItem1);
        int series2Index = arcView.addSeries(seriesItem2);
        int series3Index = arcView.addSeries(seriesItem3);
        int series4Index = arcView.addSeries(seriesItem4);
        int series5Index = arcView.addSeries(seriesItem5);
        int series6Index = arcView.addSeries(seriesItem6);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        final TextView textPercentage = view.findViewById(R.id.tv_graph);
        textPercentage.setText("R$1000,00");
        textPercentage.setTypeface((ResourcesCompat.getFont(getContext(), R.font.cabin)));

        arcView.addEvent(new DecoEvent.Builder(50).setIndex(series1Index).setDelay(1000).build());
        arcView.addEvent(new DecoEvent.Builder(15).setIndex(series2Index).setDelay(1000).build());
        arcView.addEvent(new DecoEvent.Builder(10).setIndex(series3Index).setDelay(1000).build());
        arcView.addEvent(new DecoEvent.Builder(9).setIndex(series4Index).setDelay(1000).build());
        arcView.addEvent(new DecoEvent.Builder(7).setIndex(series5Index).setDelay(1000).build());
        arcView.addEvent(new DecoEvent.Builder(3).setIndex(series5Index).setDelay(1000).build());


        arcView.configureAngles(360, 0);

        return view;
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
