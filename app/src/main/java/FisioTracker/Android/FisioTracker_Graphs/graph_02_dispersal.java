package FisioTracker.Android.FisioTracker_Graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class graph_02_dispersal extends View {

    public graph_02_dispersal(Context context) {
        super(context);
        init(null);
    }

    public graph_02_dispersal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public graph_02_dispersal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStrokeWidth(6);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        pointPaint = new Paint();
        pointPaint.setColor(Color.DKGRAY);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        axisPaint = new Paint();
        axisPaint.setColor(Color.DKGRAY);
        axisPaint.setStrokeWidth(4);
        axisPaint.setStyle(Paint.Style.STROKE);

        dashedLinePaint = new Paint();
        dashedLinePaint.setColor(Color.DKGRAY);
        dashedLinePaint.setStrokeWidth(2);
        dashedLinePaint.setStyle(Paint.Style.STROKE);
        dashedLinePaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 0));

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(40);
        labelPaint.setAntiAlias(true);
    }


    private Paint linePaint;
    private Paint pointPaint;
    private Paint axisPaint;
    private Paint dashedLinePaint;
    private Paint labelPaint;

    private float[] dataPoints = {10, 40, 25, 60, 30, 80, 55};


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float padding = 55;
        float width = getWidth() - padding * 2;
        float height = getHeight() - padding * 2;

        float bottom = getHeight() - padding;
        float left = padding;

        float originX = padding;
        float originY = getHeight() - padding;

        int count = dataPoints.length;
        float maxVal = getMaxValue(dataPoints);
        float spaceBetweenPoints = width / (count - 1);

        int verticalLines = count;
        int horizontalLines = 4;

        // Linhas verticais
        for (int i = 0; i < verticalLines; i++) {
            float x = originX + i * spaceBetweenPoints;
            canvas.drawLine(x, padding, x, originY, dashedLinePaint);
        }

        for (int i = 0; i <= horizontalLines; i++) {
            float y = padding + i * (height / horizontalLines);
            canvas.drawLine(originX, y, getWidth() - padding, y, dashedLinePaint);
        }

        canvas.drawLine(originX, originY, getWidth() - padding, originY, axisPaint); // X
        canvas.drawLine(originX, padding, originX, originY, axisPaint);              // Y
    }

    private float getMaxValue(float[] values) {
        float max = Float.MIN_VALUE;
        for (float v : values) {
            if (v > max) max = v;
        }
        return max;
    }
}