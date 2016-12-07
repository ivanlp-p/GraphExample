package com.example.ivan.graphexample.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.example.ivan.graphexample.R;

import java.util.ArrayList;

/**
 * Created by I.Laukhin on 10.11.2016.
 */

public class LineChart extends View {

    private final Paint paint;
    private Path path;

    private float[] values;
    public LineChart(Context context) {
        this(context, null, 0);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(false);

        path = new Path();

    }

    public void setData(float[] values) {
        this.values = values;

        if (values != null) {

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        float xMax = 50F;
        float xMin = 0F;
        float yMax = 50F;
        float yMin = 0F;
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float sx = width / (xMax - xMin);
        float sy = -height / (yMax - yMin);
        float trX = -xMin + 0.2F;
        float trY = -yMax;
        canvas.scale(sx, sy);
        canvas.translate(trX, trY);
        paint.setStrokeWidth(.2f);

        // Ось X
        canvas.drawLine(xMin, yMin, xMax, yMin, paint);
        canvas.drawLine(5F, yMin, 5F, yMax, paint);
        canvas.drawLine(10F, yMin, 10F, yMax, paint);

        // Ось Y
        canvas.drawLine(xMin, yMin, xMin, yMax, paint);

        canvas.drawLine(xMin, 5F, xMax, 5F, paint);
        canvas.drawLine(xMin, 10F, xMax, 10F, paint);
        canvas.drawLine(xMin, yMax, xMax, yMax, paint);

       // canvas.restore();

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        path.moveTo(0, 0);
        path.cubicTo(2.5f, 2.5F, 3.5F, 3.5F, 10F, 2F);
        //path.lineTo(10F, 10F);
        canvas.drawPath(path, paint);


//        path.lineTo(getWidth(), getHeight());
//        canvas.drawPath(path, paint);

       // canvas.restore();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(false);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.axis_value_text_size) /  (yMax / 2));
      //  canvas.translate(trX - 0.2F, trY);
        canvas.scale(1f, -1f, 1F, 0F - 0.3F);

        canvas.drawText(String.valueOf(5), 0F, 5F, paint);
        canvas.drawText(String.valueOf(5), 5F, 15F, paint);

        //canvas.rotate(90, 10F, 10F);

    }

    public void drawLineChart(Canvas canvas, ArrayList<PointF> lineDataMax, ArrayList<PointF> lineDataMin) {

    }

    public Path createFillArea(ArrayList<PointF> lineDataMax, ArrayList<PointF> lineDataMin) {
        Path path = new Path();
        Path fillpath = new Path();

        return path;
    }
}
