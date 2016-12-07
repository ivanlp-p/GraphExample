package com.example.ivan.graphexample.linechart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

/**
 * Created by I.Laukhin on 04.12.2016.
 */

public class DrawLineChartThread extends Thread {

    private boolean runFlag = false;
    private SurfaceHolder holder;

    public DrawLineChartThread(SurfaceHolder holder) {
        this.holder = holder;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    @Override
    public void run() {
        Canvas canvas = null;

        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                canvas.drawColor(Color.BLUE);
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
