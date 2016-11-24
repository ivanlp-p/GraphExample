package com.example.ivan.graphexample;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ivan.graphexample.linechart.SmoothLineChart;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmoothLineChart chart = (SmoothLineChart) findViewById(R.id.smoothChart);

        PointF[] minValue = new PointF[] {
                new PointF(15, 30), // {x, y}
                new PointF(20, 20),
                new PointF(28, 10),
                new PointF(37, 20),
                new PointF(40, 30),
                new PointF(50, 40),
                new PointF(62, 20),
                new PointF(80, 30)
        };

        PointF[] maxValue = new PointF[] {
                new PointF(15, 40), // {x, y}
                new PointF(20, 30),
                new PointF(28, 20),
                new PointF(37, 30),
                new PointF(40, 40),
                new PointF(50, 50),
                new PointF(62, 30),
                new PointF(80, 40)
        };

        chart.setCurvesForFillArea(minValue, maxValue);

     //   LineChart lineChart = (LineChart) findViewById(R.id.linechart);

    }
}