package com.myapp.prediction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.anychart.graphics.vector.Fill;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.Stroke;
import com.jjoe64.graphview.series.DataPoint;

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

public class Pakistan_Fragment extends Fragment
{
    public AnyChartView graph;

    private ProgressBar progressBar;

    public List<Double> Actual_Data;
    public List<Double> Train_Prediction;
    public List<Double> Test_Prediction;
    public TextView text_;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.pakistan_fragment, container, false);

        progressBar = (ProgressBar) root.findViewById(R.id.progress);

        graph = (AnyChartView) root.findViewById(R.id.any_chart_view);
        graph.setVisibility(View.VISIBLE);



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiCall();
    }

    public void apiCall(){
        Actual_Data = new ArrayList<>();
        Train_Prediction = new ArrayList<>();
        Test_Prediction = new ArrayList<>();


        OkHttpClient okHttpClient = new OkHttpClient();
        String link = "http://192.168.18.24:8080/predict?dataset=pakistan";
        final Request request = new Request.Builder()
                .url(link)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getContext(), "Network not found!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if(response.isSuccessful())
                {
                    final String res = response.body().string();
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //text_ =root.findViewById(R.id.text);
                            //text_.setText(res);
                            initializeData(res);
                            drawlineGraph(graph);
                        }
                    });
                }
            }
        });
    }

    public void drawlineGraph(AnyChartView anyChartView){

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

//        cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

//        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();

        for(int i =0; i < Test_Prediction.size(); i++){
            seriesData.add(new CustomDataEntry(""+i, Actual_Data.get(i), Test_Prediction.get(i), Train_Prediction.get(i)));
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.color(new SolidFill("#FF0000", 1.0));
        series1.name("Actual");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(8d);


        series1.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Test");
//        series2.color(new SolidFill("#00FF00", 1.0));

        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Train");

        series3.color(new SolidFill("#00FF00", 1.0));

        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
        progressBar.setVisibility(View.GONE);
    }


private class CustomDataEntry extends ValueDataEntry {

    CustomDataEntry(String x, Number value, Number value2, Number value3) {
        super(x, value);
//        if(value2.doubleValue() > 0.0) {
            setValue("value2", value2);
//        }

//        if(value3.doubleValue() > 0.0) {
            setValue("value3", value3);
//        }
    }

}

    public DataPoint[] data(List<Double> ls)
    {
        Double[] arr = new Double[ls.size()];
        for(int i = 0; i<ls.size(); i++)
        {
            arr[i] = ls.get(i);
        }

        int n = ls.size();
        DataPoint[] values = new DataPoint[n];
        for(int i = 0; i<n; i++)
        {
            DataPoint v = new DataPoint(ls.get(i), i);
            Log.d("msg123", String.valueOf(ls.get(i)));
            values[i] = v;
        }
        return values;
    }

    public void SplitString(String s, List<Double> ls)
    {
        ArrayList<Double> temporary = new ArrayList<Double>();
        String[] data = s.split(",|\"");
        int i = 0;
        for(String w : data)
        {
            double value = Double.parseDouble(w);

            if(Double.isNaN(value))
            {
                value = 1.0;
            }

            temporary.add(value);
            Log.d("Pak", "value "+ value);
            ls.add(i, value);
            ++i;
        }
    }

    public void initializeData(String s)
    {
        try
        {
            Scanner s1 = new Scanner(s);
            s1.useDelimiter("\\[|\\]|\n|\\{|\\}");
            String line = "";
            while(s1.hasNext())
            {
                line += s1.next();
            }


            String[] words = line.split("train\":|test\":|actual data\":");
            String actualdata = "";
            String traindata = "";
            String testdata = "";

            for(String w : words)
            {
                w.replaceAll("\"", "");
            }



            traindata = words[3];
            testdata = words[2];
            actualdata = words[1];

            SplitString(actualdata, Actual_Data);
            SplitString(testdata, Test_Prediction);
            SplitString(traindata, Train_Prediction);

            /*for(int i =0; i<Train_Prediction.size(); i++)
            {
                Log.d("msg", Train_Prediction.get(i).toString());
            }*/

//            LineGraphSeries<DataPoint> series_1, series_2, series_3;
//
//            series_2 = new LineGraphSeries<>(data(Train_Prediction));
//            series_2.setDrawDataPoints(true);
//            series_2.setColor(Color.GREEN);
//
//            series_1 = new LineGraphSeries<>(data(Actual_Data));
//            series_1.setDrawDataPoints(true);
//            series_1.setColor(Color.GREEN);
//
//            series_3 = new LineGraphSeries<>(data(Test_Prediction));
//            series_3.setDrawDataPoints(true);
//            series_3.setColor(Color.GREEN);


//            graph.addSeries(series_1);
//            graph.addSeries(series_2);
//            graph.addSeries(series_3);


//            graph.getViewport().setYAxisBoundsManual(true);
//            graph.getViewport().setMinY(0.0);
//            graph.getViewport().setMaxY(7000);
//
//            graph.getViewport().setXAxisBoundsManual(true);
//            graph.getViewport().setMinX(0.0);
//            graph.getViewport().setMaxX(300);
//            graph.showContextMenu();
        }
        catch (Exception e)
        {
            Log.d("Pak>>", "error", e);
        }
    }
}