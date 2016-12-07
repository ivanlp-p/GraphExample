package com.example.ivan.graphexample;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ivan.graphexample.linechart.SurfaceLineChart;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   SmoothLineChart chart = (SmoothLineChart) findViewById(R.id.smoothChart);
        SurfaceLineChart surfaceLineChart = (SurfaceLineChart) findViewById(R.id.surfaceLineChart);

//        Log.d("happy", "main h = " + surfaceLineChart.getHeight());


     //   Canvas canvas = surfaceLineChart.getHeight();

      //  Log.d("happy", "main = " + chart.getHeight());
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

        PointF[] dataline = new PointF[] {
                new PointF(0, 35), // {x, y}
                new PointF(5, 25),
                new PointF(15, 15),
                new PointF(20, 25),
                new PointF(25, 35),
                new PointF(35, 45),
                new PointF(45, 25),
                new PointF(60, 35)
        };
        surfaceLineChart.setCurvesForFillArea(minValue, maxValue);
        surfaceLineChart.setCurvesForFillArea(minValue, maxValue);
        surfaceLineChart.setMaxLineColor(getResources().getColor(R.color.main_line_girl));
        surfaceLineChart.setMinLineColor(getResources().getColor(R.color.main_line_girl));
        surfaceLineChart.setMinMaxLineStrikeWidth(5);
        surfaceLineChart.setFillColor(getResources().getColor(R.color.pink));
        surfaceLineChart.setFillAlpha(100);

        /*chart.setCurvesForFillArea(minValue, maxValue);
        chart.setMaxLineColor(getResources().getColor(R.color.main_line_girl));
        chart.setMinLineColor(getResources().getColor(R.color.main_line_girl));
        chart.setMinMaxLineStrikeWidth(5);
        chart.setFillColor(getResources().getColor(R.color.pink));
        chart.setFillAlpha(100);
        chart.invalidate();*/


       // chart.setLineData(dataline);
      //  List<PointF> dataset = new LineDataSet(this, chart).setLineDataSet(dataline);



     //   LineChart lineChart = (LineChart) findViewById(R.id.linechart);

    }
}