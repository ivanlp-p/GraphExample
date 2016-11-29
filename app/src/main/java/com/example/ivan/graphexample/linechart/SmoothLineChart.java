package com.example.ivan.graphexample.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.ivan.graphexample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I.Laukhin on 10.11.2016.
 */

public class SmoothLineChart extends View {

    private static final int CHART_COLOR = 0xFF0099CC;
    private static final int CIRCLE_SIZE = 8;
    private static final int STROKE_SIZE = 2;
    private static final float SMOOTHNESS = 0.3f; // the higher the smoother, but don't go over 0.5

    private final Paint paint;
    private final Path path;
    private final Path fillPath;
    private final float circleSize;
    private final float strokeSize;
    private final float border;

    private List<PointF> maxPoints;
    private List<PointF> minPoints;
    private List<PointF> firstRefPoints = new ArrayList<>();
    private List<PointF> secondRefPoints = new ArrayList<>();

    private PointF[] maxValue;
    private PointF[] minValue;
    private float minY = 0;
    private float maxXAxis;
    private float maxYAxis;


    public SmoothLineChart(Context context) {
        this(context, null, 0);
    }

    public SmoothLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        float scale = context.getResources().getDisplayMetrics().density;

        circleSize = scale * CIRCLE_SIZE;
        strokeSize = scale * STROKE_SIZE;
        border = circleSize;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeSize);

        path = new Path();
        fillPath = new Path();
    }

    public void setCurvesForFillArea(PointF[] minValue, PointF[] maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;

        if (minValue != null && maxValue != null) {
            maxXAxis = maxValue[0].x;
            maxYAxis = maxValue[0].y;

            for (PointF point : maxValue) {
                final float y = point.y;
                final float x = point.x;

                if (y > maxYAxis)
                    maxYAxis = y;

                if (x > maxXAxis)
                    maxXAxis = x;
            }

                maxXAxis = maxXAxis + 30;
            maxYAxis = maxYAxis + 30;


            //  minY = minValue[0].y;

         /*   for (PointF point : minValue) {
                final float y = point.y;

                if (y < minY)
                    minY = y;
            }*/
        }

        invalidate();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        int minSize = minValue.length;
        int maxSize = maxValue.length;

        final float height = getMeasuredHeight() - 2 * border - getResources().getDimensionPixelSize(R.dimen.axisy_padding_0) - getResources().getDimensionPixelSize(R.dimen.axisy_padding_15);
     //   final float height = getMeasuredHeight() - 2 * border;
        final float width = getMeasuredWidth() - border - getResources().getDimensionPixelSize(R.dimen.axisx_padding_5) - getResources().getDimensionPixelSize(R.dimen.axisx_padding_10);
     //   final float width = getMeasuredWidth() - 2 * border;

        final float left = 0;
        final float right = maxXAxis;
        final float dX = (right - left) > 0 ? (right - left) : (2);
        final float dY = (maxYAxis - minY) > 0 ? (maxYAxis - minY) : (2);

        minPoints = new ArrayList<PointF>(minSize);
        for (PointF point : minValue) {
            //float x = border + (point.x - left) * width / dX;
            float x = (point.x - left) * width / dX;
          //  float y = border + height - (point.y - minY) * height / dY;
            float y = height - (point.y - minY) * height / dY;
            minPoints.add(new PointF(x, y));
        }

        maxPoints = new ArrayList<PointF>(maxSize);
        for (PointF point : maxValue) {
         //   float x = border + (point.x - left) * width / dX;
            float x = (point.x - left) * width / dX;
         //   float y = border + height - (point.y - minY) * height / dY;
            float y = height - (point.y - minY) * height / dY;
            maxPoints.add(new PointF(x, y));
        }


        drawBackground(canvas);

        path.reset();

        float maxX = 0, maxY = 0;
        path.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);
        fillPath.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);

        //   float currentMaxX = border + (maxXAxis - left) * width / dX;

        for (int i = 1; i < maxSize; i++) {
            PointF p = maxPoints.get(i);    // current point

            // first control point
            PointF p0 = maxPoints.get(i - 1);    // previous point
            float d0 = (float) Math.sqrt(Math.pow(p.x - p0.x, 2) + Math.pow(p.y - p0.y, 2));    // distance between p and p0
            float x1 = Math.min(p0.x + maxX * d0, (p0.x + p.x) / 2);    // min is used to avoid going too much right
            float y1 = p0.y + maxY * d0;

            // second control point
            PointF p1 = maxPoints.get(i + 1 < minSize ? i + 1 : i);    // next point
            float d1 = (float) Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2));    // distance between p1 and p0 (length of reference line)
            maxX = (p1.x - p0.x) / d1 * SMOOTHNESS;        // (lX,lY) is the slope of the reference line
            maxY = (p1.y - p0.y) / d1 * SMOOTHNESS;
            float x2 = Math.max(p.x - maxX * d0, (p0.x + p.x) / 2);    // max is used to avoid going too much left
            float y2 = p.y - maxY * d0;

            // add line
            path.cubicTo(x1, y1, x2, y2, p.x, p.y);
            fillPath.cubicTo(x1, y1, x2, y2, p.x, p.y);

        }

        PointF lastMinPoint = minPoints.get(minPoints.size() - 1);
        fillPath.lineTo(lastMinPoint.x, lastMinPoint.y);

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        float minX = 0, minY = 0;
        path.moveTo(minPoints.get(0).x, minPoints.get(0).y);
        for (int i = 1; i < minSize; i++) {
            PointF p = minPoints.get(i);    // current point

            // first control point
            PointF p0 = minPoints.get(i - 1);    // previous point
            float d0 = (float) Math.sqrt(Math.pow(p.x - p0.x, 2) + Math.pow(p.y - p0.y, 2));    // distance between p and p0
            float x1 = Math.min(p0.x + minX * d0, (p0.x + p.x) / 2);    // min is used to avoid going too much right
            float y1 = p0.y + minY * d0;
            firstRefPoints.add(new PointF(x1, y1));

            // second control point
            PointF p1 = minPoints.get(i + 1 < minSize ? i + 1 : i);    // next point
            float d1 = (float) Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2));    // distance between p1 and p0 (length of reference line)
            minX = (p1.x - p0.x) / d1 * SMOOTHNESS;        // (lX,lY) is the slope of the reference line
            minY = (p1.y - p0.y) / d1 * SMOOTHNESS;
            float x2 = Math.max(p.x - minX * d0, (p0.x + p.x) / 2);    // max is used to avoid going too much left
            float y2 = p.y - minY * d0;
            secondRefPoints.add(new PointF(x2, y2));

            // add line
            path.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        for (int i = minPoints.size() - 2; i >= 0; i--) {
            PointF p = minPoints.get(i);    // current point
            PointF firstRefPoint = firstRefPoints.get(i);
            PointF secondRefPoint = secondRefPoints.get(i);

            // add fill
            fillPath.cubicTo(secondRefPoint.x, secondRefPoint.y, firstRefPoint.x, firstRefPoint.y, p.x, p.y);
        }
        PointF firstMaxPoint = maxPoints.get(0);

        fillPath.lineTo(firstMaxPoint.x, firstMaxPoint.y);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.pink));
        paint.setAlpha(100);

        canvas.drawPath(fillPath, paint);

        // draw circles
        paint.setColor(CHART_COLOR);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (PointF point : maxPoints) {
            canvas.drawCircle(point.x, point.y, circleSize / 2, paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        for (PointF point : maxPoints) {
            Log.d("happy", "maxPoint x = " + point.x + " y = " + point.y);
            canvas.drawCircle(point.x, point.y, (circleSize - strokeSize) / 2, paint);
        }

        paint.setColor(CHART_COLOR);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (PointF point : minPoints) {
            canvas.drawCircle(point.x, point.y, circleSize / 2, paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        for (PointF point : minPoints) {
            canvas.drawCircle(point.x, point.y, (circleSize - strokeSize) / 2, paint);
        }
    }

    private void drawBackground(Canvas canvas) {
        int range = 5;

        float paddingXForText = getResources().getDimensionPixelSize(R.dimen.axisx_padding_5) + getPaddingLeft();
        float paddingYForText = getResources().getDimensionPixelSize(R.dimen.axisy_padding_0);

        float paddingXForChart = getResources().getDimensionPixelSize(R.dimen.axisx_padding_10) + getPaddingLeft();
        float paddingYForChart = getResources().getDimensionPixelSize(R.dimen.axisy_padding_15);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.pink));
        Rect rect = new Rect();
        rect.set(0, 0, (int) (paddingXForText + paddingXForChart), getHeight() - getResources().getDimensionPixelSize(R.dimen.background_axis_y_padding));
        canvas.drawRect(rect, paint);


        paint.setColor(Color.GRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        DisplayMetrics dp = getContext().getResources().getDisplayMetrics();
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dp);
        paint.setTextSize(textSize);

        float measureXPadding = getResources().getDimensionPixelSize(R.dimen.measure_axis_y_padding_x);
        float measureYPadding = getResources().getDimensionPixelSize(R.dimen.measure_axis_y_padding_y);
        canvas.drawText("см", measureXPadding, measureYPadding, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.axis_value_text_size) / 2);

        paint.setStrokeWidth(2);

//        float paddingXForText = getResources().getDimensionPixelSize(R.dimen.axisx_padding_5) + getPaddingLeft();
//        float paddingYForText = getResources().getDimensionPixelSize(R.dimen.axisy_padding_0);

        canvas.save();

        canvas.translate(paddingXForText, paddingYForText);



        for (int y = 5; y < maxYAxis; y += range) {
            final float yPos = getYPos(y, maxYAxis);
            float textPadding = getResources().getDimensionPixelSize(R.dimen.text_padding_y);

            paint.setAntiAlias(true);
            canvas.drawText(String.valueOf(y), getPaddingLeft(), yPos - textPadding, paint);
        }

        paint.setTextAlign(Paint.Align.RIGHT);

        for (int x = 0; x <= maxXAxis; x += range) {
            final float xPos = getXPos(x, maxXAxis);
            float textPadding = getResources().getDimensionPixelSize(R.dimen.text_padding_x);

            paint.setAntiAlias(true);
            canvas.drawText(String.valueOf((int) (maxXAxis - x)), xPos + textPadding, getHeight(), paint);
        }



        canvas.translate(paddingXForChart, paddingYForChart);

        Log.d("happy", "width = " + getWidth() + "h = " + getHeight());
        for (int y = 0; y < maxYAxis; y += range) {
            final float yPos = getYPos(y, maxYAxis);
            final float xPos = getXPos(0, maxXAxis);
            paint.setAntiAlias(false);
            canvas.drawLine(0, yPos, xPos, yPos, paint);
        }

        for (int x = 0; x <= maxXAxis; x += range) {
            final float xPos = getXPos(x, maxXAxis);
            final float yPos = getYPos(maxYAxis - range, maxYAxis);

            paint.setAntiAlias(false);
            canvas.drawLine(xPos, yPos, xPos, getHeight(), paint);
        }
    }

    private float getXPos(float value, float maxValue) {

        float width = getWidth() - getPaddingLeft() - getPaddingRight()
                - getResources().getDimensionPixelSize(R.dimen.axisx_padding_5)
                - getResources().getDimensionPixelSize(R.dimen.axisx_padding_10)
                - border;

        value = (value / maxValue) * width;
        value = width - value;
        value += getPaddingLeft();

        return value;
    }

    private float getYPos(float value, float maxValue) {

        float height = getHeight() - getPaddingTop() - getPaddingBottom();

        value = (value / maxValue) * height;
        value = height - value;
        value += getPaddingTop();

        return value;
    }
}
