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
                new PointF(15, 39), // {x, y}
                new PointF(20, 21),
                new PointF(28, 9),
                new PointF(37, 21),
                new PointF(40, 25),
                new PointF(50, 31),
                new PointF(62, 24),
                new PointF(80, 28)
        };

        PointF[] maxValue = new PointF[] {
                new PointF(15, 44), // {x, y}
                new PointF(20, 26),
                new PointF(28, 14),
                new PointF(37, 26),

                new PointF(50, 36),
                new PointF(62, 29),
                new PointF(80, 33)
        };

        chart.setCurvesForFillArea(minValue, maxValue);

    }
}