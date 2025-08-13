package FisioTracker.Android.OrtoTracker_Graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
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
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(1);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        dataLinePaint = new Paint();
        dataLinePaint.setColor(Color.parseColor("#00BCD4")); // Laranja
        dataLinePaint.setStrokeWidth(1);
        dataLinePaint.setStyle(Paint.Style.STROKE);
        dataLinePaint.setAntiAlias(true);
        dataLinePaint.setStrokeJoin(Paint.Join.ROUND);
        dataLinePaint.setStrokeCap(Paint.Cap.ROUND);
        dataLinePaint.setPathEffect(new CornerPathEffect(22)); // Cantos arredondados 22dp

        // Paint para área preenchida abaixo da linha
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#00BCD4")); // Pontos laranja também
        pointPaint.setAntiAlias(true);

        axisPaint = new Paint();
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(1);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setAntiAlias(true);
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);

        dashedLinePaint = new Paint();
        dashedLinePaint.setColor(Color.WHITE);
        dashedLinePaint.setStrokeWidth(1);
        dashedLinePaint.setStyle(Paint.Style.STROKE);
        dashedLinePaint.setAntiAlias(true);
        dashedLinePaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 0));

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(40);
        labelPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
        borderPaint.setAntiAlias(true);
    }

    private Paint linePaint;
    private Paint dataLinePaint; // Nova paint para a linha de dados
    private Paint fillPaint; // Paint para preenchimento abaixo da linha
    private Paint pointPaint;
    private Paint axisPaint;
    private Paint dashedLinePaint;
    private Paint labelPaint;
    private Paint backgroundPaint; // Paint para fundo arredondado
    private Paint borderPaint; // Paint para borda do fundo

    private float[] dataPoints = {10, 40, 25, 60, 30, 80, 55};
    float cornerRadiusDp = 22f;
    float radius = cornerRadiusDp * getResources().getDisplayMetrics().density;
    float cornerRadiusPx = cornerRadiusDp * getResources().getDisplayMetrics().density;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float[] radii = new float[] {
                radius, radius,  // top-left
                radius, radius,  // top-right
                radius, radius,  // bottom-right
                radius, radius   // bottom-left
        };

        Path clipPath = new Path();
        clipPath.addRoundRect(
                0, 0, getWidth(), getHeight(),
                radii,
                Path.Direction.CW
        );

        // 3. Aplicar clipping para arredondar cantos do canvas
        canvas.save();
        canvas.clipPath(clipPath);

        // 4. Desenhar o gráfico normalmente (copie aqui todo seu código de desenho)
        float padding = 4;
        float width = getWidth() - padding * 2;
        float height = getHeight() - padding * 2;

        float originX = padding;
        float originY = getHeight() - padding;

        int count = dataPoints.length;
        float maxVal = getMaxValue(dataPoints);
        float spaceBetweenPoints = width / (count - 1);

        int verticalLines = count;
        int horizontalLines = 4;

        // Linhas verticais da grade
        for (int i = 0; i < verticalLines; i++) {
            float x = originX + i * spaceBetweenPoints;
            Path verticalPath = new Path();
            verticalPath.moveTo(x, padding);
            verticalPath.lineTo(x, originY);
            canvas.drawPath(verticalPath, dashedLinePaint);
        }

        // Linhas horizontais da grade
        for (int i = 0; i <= horizontalLines; i++) {
            float y = padding + i * (height / horizontalLines);
            Path horizontalPath = new Path();
            horizontalPath.moveTo(originX, y);
            horizontalPath.lineTo(getWidth() - padding, y);
            canvas.drawPath(horizontalPath, dashedLinePaint);
        }

        // Eixos com cantos arredondados (como você já fez manualmente)
        float cornerRadius = cornerRadiusPx;

        canvas.drawLine(originX, padding, originX, originY - cornerRadius, axisPaint);
        canvas.drawLine(originX + cornerRadius, originY, getWidth() - padding, originY, axisPaint);

        Path cornerPath = new Path();
        cornerPath.moveTo(originX, originY - cornerRadius);
        cornerPath.quadTo(originX, originY, originX + cornerRadius, originY);
        canvas.drawPath(cornerPath, axisPaint);

        // Gradiente para preenchimento
        LinearGradient gradient = new LinearGradient(
                0, padding,
                0, originY,
                Color.parseColor("#00BCD4"),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);

        // Área preenchida abaixo da linha
        if (dataPoints.length > 1) {
            Path fillPath = new Path();
            fillPath.moveTo(originX, originY);

            float firstX = originX;
            float firstY = originY - (dataPoints[0] / maxVal) * height;
            fillPath.lineTo(firstX, firstY);

            for (int i = 1; i < dataPoints.length; i++) {
                float x = originX + i * spaceBetweenPoints;
                float y = originY - (dataPoints[i] / maxVal) * height;
                fillPath.lineTo(x, y);
            }

            float lastX = originX + (dataPoints.length - 1) * spaceBetweenPoints;
            fillPath.lineTo(lastX, originY);
            fillPath.lineTo(originX, originY);
            fillPath.close();

            canvas.drawPath(fillPath, fillPaint);
        }

        // Linha dos dados
        if (dataPoints.length > 1) {
            Path dataPath = new Path();
            float firstX = originX;
            float firstY = originY - (dataPoints[0] / maxVal) * height;
            dataPath.moveTo(firstX, firstY);

            for (int i = 1; i < dataPoints.length; i++) {
                float x = originX + i * spaceBetweenPoints;
                float y = originY - (dataPoints[i] / maxVal) * height;
                dataPath.lineTo(x, y);
            }
            canvas.drawPath(dataPath, dataLinePaint);
        }

        // Pontos dos dados
        for (int i = 0; i < dataPoints.length; i++) {
            float x = originX + i * spaceBetweenPoints;
            float y = originY - (dataPoints[i] / maxVal) * height;
            canvas.drawCircle(x, y, 12, pointPaint);
        }

        canvas.save();
        canvas.clipPath(clipPath);

        canvas.restore();
    }

    private float getMaxValue(float[] values) {
        float max = Float.MIN_VALUE;
        for (float v : values) {
            if (v > max) max = v;
        }
        return max;
    }

    // Método para atualizar os dados (opcional)
    public void setDataPoints(float[] newDataPoints) {
        this.dataPoints = newDataPoints;
        invalidate(); // Redesenha o gráfico
    }
}