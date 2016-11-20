package com.example.ivan.graphexample.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

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

    private List<PointF> firstRefPoints = new ArrayList<>();
    private List<PointF> secondRefPoints = new ArrayList<>();

    private PointF[] maxLine;
    private PointF[] minLine;
    private float minY;
    private float maxY;


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

    public void setCurvesForFillArea(PointF[] minLine, PointF[] maxLine) {
        this.minLine = minLine;
        this.maxLine = maxLine;

        if (minLine != null && maxLine != null) {

            maxY = getMaxY(maxLine);
            minY = getMinY(minLine);
        }

        invalidate();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        path.reset();

        List<PointF> minPoints = getAbsolutePointsList(minLine, border, minY, maxY);
        List<PointF> maxPoints = getAbsolutePointsList(maxLine, border, minY, maxY);

        float maxX = 0, maxY = 0;
        path.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);
        fillPath.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);
        for (int i = 1; i < minPoints.size(); i++) {
            PointF p = maxPoints.get(i);    // current point

            // first control point
            PointF p0 = maxPoints.get(i - 1);    // previous point
            float d0 = (float) Math.sqrt(Math.pow(p.x - p0.x, 2) + Math.pow(p.y - p0.y, 2));    // distance between p and p0
            float x1 = Math.min(p0.x + maxX * d0, (p0.x + p.x) / 2);    // min is used to avoid going too much right
            float y1 = p0.y + maxY * d0;

            // second control point
            PointF p1 = maxPoints.get(i + 1 < minPoints.size() ? i + 1 : i);    // next point
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
        for (int i = 1; i < minPoints.size(); i++) {
            PointF p = minPoints.get(i);    // current point

            // first control point
            PointF p0 = minPoints.get(i - 1);    // previous point
            float d0 = (float) Math.sqrt(Math.pow(p.x - p0.x, 2) + Math.pow(p.y - p0.y, 2));    // distance between p and p0
            float x1 = Math.min(p0.x + minX * d0, (p0.x + p.x) / 2);    // min is used to avoid going too much right
            float y1 = p0.y + minY * d0;
            firstRefPoints.add(new PointF(x1, y1));

            // second control point
            PointF p1 = minPoints.get(i + 1 < minPoints.size() ? i + 1 : i);    // next point
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
        paint.setColor(Color.BLUE);
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

    /**
     * Метод высчитывает абсолюбтные координаты точек на экране
     *
     * @param line
     * @param border
     * @param minY
     * @param maxY
     * @return
     */
    private List<PointF> getAbsolutePointsList(PointF[] line, float border, float minY, float maxY) {

        final float height = getMeasuredHeight() - 2 * border;
        final float width = getMeasuredWidth() - 2 * border;

        final float left = line[0].x;
        final float right = line[line.length - 1].x;
        final float dX = (right - left) > 0 ? (right - left) : (2);
        final float dY = (maxY - minY) > 0 ? (maxY - minY) : (2);

        List<PointF> pointsList = new ArrayList<>();
        for (PointF point : line) {
            float x = border + (point.x - left) * width / dX;
            float y = border + height - (point.y - minY) * height / dY;
            pointsList.add(new PointF(x, y));
        }

        return pointsList;
    }

    /**
     * Метод для поиска максимального значения по Y в массиве PointF
     *
     * @param points массив PointF
     * @return максимальное значение по Y
     */
    private static float getMaxY(PointF[] points) {

        float maxY = Float.MIN_VALUE;

        for (PointF point : points) {

            if (point.y > maxY) {

                maxY = point.y;
            }
        }

        return maxY;
    }

    private static float getMinY(PointF[] points) {

        float minY = Float.MAX_VALUE;

        for (PointF point : points) {

            if (point.y < minY) {

                minY = point.y;
            }
        }

        return minY;
    }
}
