package com.example.ivan.graphexample.linechart;

import android.content.Context;
import android.graphics.PointF;

import com.example.ivan.graphexample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I.Laukhin on 01.12.2016.
 */

public class LineDataSet {

    private static final int CIRCLE_SIZE = 8;

    private Context context;
    private SmoothLineChart chart;
    private float border;
    private float maxXAxis;
    private float maxYAxis;
    private float minY;

    public LineDataSet(Context context, SmoothLineChart chart) {
        this.context = context;
        this.chart = chart;

        float scale = context.getResources().getDisplayMetrics().density;
        border = scale * CIRCLE_SIZE;

        maxXAxis = chart.getMaxXAxis();
        maxYAxis = chart.getMaxYAxis();
        minY = chart.getMinY();
    }

    public List<PointF> setLineDataSet(PointF[] dataset) {
        final float height = chart.getMeasuredHeight() - 2 * border
                - context.getResources().getDimensionPixelSize(R.dimen.axisy_padding_0)
                - context.getResources().getDimensionPixelSize(R.dimen.axisy_padding_15);
        //   final float height = getMeasuredHeight() - 2 * border;
        final float width = chart.getMeasuredWidth() - border
                - context.getResources().getDimensionPixelSize(R.dimen.axisx_padding_5)
                - context.getResources().getDimensionPixelSize(R.dimen.axisx_padding_10);
        //   final float width = getMeasuredWidth() - 2 * border;

        final float left = 0;
        final float right = maxXAxis;
        final float dX = (right - left) > 0 ? (right - left) : (2);
        final float dY = (maxYAxis - minY) > 0 ? (maxYAxis - minY) : (2);

        List<PointF> datalist = new ArrayList<PointF>(dataset.length);
        for (PointF point : dataset) {
            //float x = border + (point.x - left) * width / dX;
            float x = (point.x - left) * width / dX;
            //  float y = border + height - (point.y - minY) * height / dY;
            float y = height - (point.y - minY) * height / dY;
            datalist.add(new PointF(x, y));
        }

        return datalist;
    }
}
