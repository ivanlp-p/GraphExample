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

    private final Paint mPaint;
    private final Path mPath;
    private final Path fillPath;
    private final float mCircleSize;
    private final float mStrokeSize;
    private final float mBorder;

    private List<PointF> firstRefPoints = new ArrayList<>();
    private List<PointF> secondRefPoints = new ArrayList<>();

    private PointF[] maxValue;
    private PointF[] minValue;
    private float mMinY;
    private float mMaxY;


    public SmoothLineChart(Context context) {
        this(context, null, 0);
    }

    public SmoothLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        float scale = context.getResources().getDisplayMetrics().density;

        mCircleSize = scale * CIRCLE_SIZE;
        mStrokeSize = scale * STROKE_SIZE;
        mBorder = mCircleSize;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeSize);

        mPath = new Path();
        fillPath = new Path();
    }

    public void setCurvesForFillArea(PointF[] minValue, PointF[] maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;

        if (minValue != null && maxValue != null) {
            mMaxY = maxValue[0].y;

            for (PointF point : maxValue) {
                final float y = point.y;

                if (y > mMaxY)
                    mMaxY = y;
            }

            mMinY = minValue[0].y;

            for (PointF point : minValue) {
                final float y = point.y;

                if (y < mMinY)
                    mMinY = y;
            }
        }

        invalidate();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        int minSize = minValue.length;
        int maxSize = maxValue.length;

        final float height = getMeasuredHeight() - 2 * mBorder;
        final float width = getMeasuredWidth() - 2 * mBorder;

        final float left = minValue[0].x;
        final float right = minValue[minValue.length - 1].x;
        final float dX = (right - left) > 0 ? (right - left) : (2);
        final float dY = (mMaxY - mMinY) > 0 ? (mMaxY - mMinY) : (2);

        mPath.reset();

        List<PointF> minPoints = new ArrayList<PointF>(minSize);
        for (PointF point : minValue) {
            float x = mBorder + (point.x - left) * width / dX;
            float y = mBorder + height - (point.y - mMinY) * height / dY;
            minPoints.add(new PointF(x, y));
        }

        List<PointF> maxPoints = new ArrayList<PointF>(maxSize);
        for (PointF point : maxValue) {
            float x = mBorder + (point.x - left) * width / dX;
            float y = mBorder + height - (point.y - mMinY) * height / dY;
            maxPoints.add(new PointF(x, y));
        }

        float maxX = 0, maxY = 0;
        mPath.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);
        fillPath.moveTo(maxPoints.get(0).x, maxPoints.get(0).y);
        for (int i = 1; i < minSize; i++) {
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
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
            fillPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }

        PointF lastMinPoint = minPoints.get(minPoints.size() - 1);
        fillPath.lineTo(lastMinPoint.x, lastMinPoint.y);

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);

        float minX = 0, minY = 0;
        mPath.moveTo(minPoints.get(0).x, minPoints.get(0).y);
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
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);

        for (int i = minPoints.size() - 2; i >= 0; i--) {
            PointF p = minPoints.get(i);    // current point
            PointF firstRefPoint = firstRefPoints.get(i);
            PointF secondRefPoint = secondRefPoints.get(i);

            // add fill
            fillPath.cubicTo(secondRefPoint.x, secondRefPoint.y, firstRefPoint.x, firstRefPoint.y, p.x, p.y);
        }
        PointF firstMaxPoint = maxPoints.get(0);

        fillPath.lineTo(firstMaxPoint.x, firstMaxPoint.y);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(fillPath, mPaint);

        // draw circles
        mPaint.setColor(CHART_COLOR);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (PointF point : maxPoints) {
            canvas.drawCircle(point.x, point.y, mCircleSize / 2, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (PointF point : maxPoints) {
            canvas.drawCircle(point.x, point.y, (mCircleSize - mStrokeSize) / 2, mPaint);
        }

        mPaint.setColor(CHART_COLOR);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (PointF point : minPoints) {
            canvas.drawCircle(point.x, point.y, mCircleSize / 2, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (PointF point : minPoints) {
            canvas.drawCircle(point.x, point.y, (mCircleSize - mStrokeSize) / 2, mPaint);
        }

    }
}
