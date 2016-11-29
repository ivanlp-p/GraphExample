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
                new PointF(0, 30), // {x, y}
                new PointF(5, 20),
                new PointF(15, 10),
                new PointF(20, 20),
                new PointF(25, 30),
                new PointF(35, 40),
                new PointF(45, 20),
                new PointF(60, 30)
        };

        PointF[] maxValue = new PointF[] {
                new PointF(0, 40), // {x, y}
                new PointF(5, 30),
                new PointF(15, 20),
                new PointF(20, 30),
                new PointF(25, 40),
                new PointF(35, 50),
                new PointF(45, 30),
                new PointF(60, 40)
        };

        chart.setCurvesForFillArea(minValue, maxValue);

     //   LineChart lineChart = (LineChart) findViewById(R.id.linechart);

    }
}