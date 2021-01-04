package com.myapp.prediction;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class USA_Fragment extends Fragment
{
    private GraphView graph_;
    public ArrayList<Integer> Actual_Data;
    public ArrayList<Integer> Train_Prediction;
    public ArrayList<Integer> Test_Prediction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.usa_fragment, container, false);
        graph_ = root.findViewById(R.id.usa_graph);
        graph_.setVisibility(View.VISIBLE);

        Actual_Data = new ArrayList<>();
        Train_Prediction = new ArrayList<>();
        Test_Prediction = new ArrayList<>();

        for(int i = 0; i < 300; i++)
        {
            Actual_Data.add(i+6);
        }

        for(int i = 0; i < 300; i++)
        {
            Train_Prediction.add(i+2);
        }

        for(int i = 0; i<300; i++)
        {
            Test_Prediction.add(i+15);
        }


        Graph();
        return root;
    }

    protected void Populate_Actual_Values(ArrayList<Integer> actual_values)
    {
        for(int i = 0; i < actual_values.size(); i++)
        {
            Actual_Data.add(actual_values.get(i));
        }
    }

    protected void Populate_Test_Values(ArrayList<Integer> test_values)
    {
        for(int i = 0; i < test_values.size(); i++)
        {
            Test_Prediction.add(test_values.get(i));
        }
    }

    protected void Populate_Trained_Values(ArrayList<Integer> trained_values)
    {
       for (int i = 0; i < trained_values.size(); i++)
       {
           Train_Prediction.add(trained_values.get(i));
       }
    }

    protected void Graph()
    {
        try
        {
            DataPoint[] actual_data_points = new DataPoint[300];
            DataPoint[] train_predictions_points = new DataPoint[300];
            DataPoint[] test_predictions_points = new DataPoint[300];

            for(int i = 0; i < Actual_Data.size(); i++)
            {
                actual_data_points[i] = new DataPoint(Integer.valueOf(Actual_Data.get(i)), Integer.valueOf(i));
            }

            for(int i = 0; i < Train_Prediction.size(); i++)
            {
                train_predictions_points[i] = new DataPoint(Integer.valueOf(Train_Prediction.get(i)), Integer.valueOf(i));
            }

            for (int i = 0; i < Test_Prediction.size(); i++)
            {
               test_predictions_points[i] = new DataPoint(Integer.valueOf(Test_Prediction.get(i)), Integer.valueOf(i));
            }

            LineGraphSeries<DataPoint> actual_data = new LineGraphSeries<>(actual_data_points);
            LineGraphSeries<DataPoint> test_prediction = new LineGraphSeries<>(test_predictions_points);
            LineGraphSeries<DataPoint> train_prediction = new LineGraphSeries<>(train_predictions_points);

            actual_data.setColor(Color.BLUE);
            train_prediction.setColor(Color.BLACK);
            test_prediction.setColor(Color.GREEN);

            actual_data.setDrawDataPoints(true);
            actual_data.setDataPointsRadius(2);
            actual_data.setThickness(5);

            train_prediction.setDrawDataPoints(true);
            train_prediction.setDataPointsRadius(2);
            train_prediction.setThickness(5);

            test_prediction.setDrawDataPoints(true);
            test_prediction.setDataPointsRadius(2);
            test_prediction.setThickness(5);

            graph_.addSeries(actual_data);
            graph_.addSeries(train_prediction);
            graph_.addSeries(test_prediction);

            graph_.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        catch (IllegalArgumentException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
