package com.myapp.prediction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Anchor;
import com.anychart.graphics.vector.Stroke;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Pakistan_Fragment extends Fragment {

    private static final String URL = "http://192.168.18.24:8080/predict?dataset=pakistan";

    private AnyChartView chartView;
    private List<Double> actualData = new ArrayList<>();
    private List<Double> trainData = new ArrayList<>();
    private List<Double> testData =  new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.pakistan_fragment, container, false);
        chartView = (AnyChartView) root.findViewById(R.id.any_chart_view);
        chartView.setVisibility(View.VISIBLE);
        chartView.setProgressBar(root.findViewById(R.id.progress));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchDataFromFlask();
    }

    public void fetchDataFromFlask() {
        OkHttpClient okHttpClient = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Network not found!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String res = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initializeData(res);
                            drawChart(chartView);
                        }
                    });
                }
            }
        });
    }

    public void drawChart(AnyChartView anyChartView) {

        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Economic forecasting using deep neural network LSTM.");
        cartesian.yAxis(0).title("GDP");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();

        for (int i = 0; i < testData.size(); i++) {
            seriesData.add(new ChartDataEntry("" + i, actualData.get(i), testData.get(i), trainData.get(i)));
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line graph1 = cartesian.line(series1Mapping);
        graph1.name("Actual");
        graph1.hovered().markers().enabled(true);
        graph1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        graph1.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line graph2 = cartesian.line(series2Mapping);
        graph2.name("Test");
        graph2.hovered().markers().enabled(true);
        graph2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        graph2.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line graph3 = cartesian.line(series3Mapping);
        graph3.name("Train");
        graph3.hovered().markers().enabled(true);
        graph3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        graph3.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    class ChartDataEntry extends ValueDataEntry {

        ChartDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }


    private void initializeData(String s) {
        try {
            Scanner s1 = new Scanner(s);
            s1.useDelimiter("\\[|\\]|\n|\\{|\\}");
            String line = "";
            while (s1.hasNext()) {
                line += s1.next();
            }

            String[] words = line.split("train\":|test\":|actual data\":");
            String actualdata = "";
            String traindata = "";
            String testdata = "";

            for (String w : words) {
                w.replaceAll("\"", "");
            }

            words[0] = words[1];
            words[1] = words[2];
            words[2] = words[3];
            actualdata = words[0];
            testdata = words[1];
            traindata = words[2];

            splintString(actualdata, actualData);
            splintString(testdata, testData);
            splintString(traindata, trainData);
        } catch (Exception e) {
            Log.d("Pak>>", "error", e);
        }
    }


    private void splintString(String s, List<Double> ls) {
        ArrayList<Double> temporary = new ArrayList<Double>();
        String[] data = s.split(",|\"");
        int i = 0;
        for (String w : data) {
            double value = Double.parseDouble(w);

            if (Double.isNaN(value)) {
                value = 0.0;
            }
            temporary.add(value);
            Log.d("Pak", "value " + value);
            ls.add(i, value);
            ++i;
        }
    }
}