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
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStrokeWidth(6);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        // Nova linha laranja para os dados
        dataLinePaint = new Paint();
        dataLinePaint.setColor(Color.parseColor("#FF8C00")); // Laranja
        dataLinePaint.setStrokeWidth(8);
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
        pointPaint.setColor(Color.parseColor("#FF8C00")); // Pontos laranja também
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        axisPaint = new Paint();
        axisPaint.setColor(Color.DKGRAY);
        axisPaint.setStrokeWidth(4);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setAntiAlias(true);
        axisPaint.setStrokeJoin(Paint.Join.ROUND);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);
        // Remover CornerPathEffect - vamos desenhar manualmente

        dashedLinePaint = new Paint();
        dashedLinePaint.setColor(Color.DKGRAY);
        dashedLinePaint.setStrokeWidth(2);
        dashedLinePaint.setStyle(Paint.Style.STROKE);
        dashedLinePaint.setAntiAlias(true);
        dashedLinePaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 0));

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(40);
        labelPaint.setAntiAlias(true);

        // Paint para o fundo arredondado do gráfico
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE); // Ou a cor que preferir
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        // Paint para borda do fundo (opcional)
        borderPaint = new Paint();
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Desenhar fundo arredondado primeiro (atrás de tudo) - REMOVIDO para focar nos eixos
        // float cornerRadius = 22f; // 22dp de raio nos cantos
        // RectF backgroundRect = new RectF(0, 0, getWidth(), getHeight());
        // canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);
        // canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, borderPaint); // Borda opcional

        float padding = 55;
        float width = getWidth() - padding * 2;
        float height = getHeight() - padding * 2;

        float originX = padding;
        float originY = getHeight() - padding;

        int count = dataPoints.length;
        float maxVal = getMaxValue(dataPoints);
        float spaceBetweenPoints = width / (count - 1);

        int verticalLines = count;
        int horizontalLines = 4;

        // Desenhar linhas de grade (verticais) - Path para aplicar cantos arredondados
        for (int i = 0; i < verticalLines; i++) {
            float x = originX + i * spaceBetweenPoints;
            Path verticalPath = new Path();
            verticalPath.moveTo(x, padding);
            verticalPath.lineTo(x, originY);
            canvas.drawPath(verticalPath, dashedLinePaint);
        }

        // Desenhar linhas de grade (horizontais) - Path para aplicar cantos arredondados
        for (int i = 0; i <= horizontalLines; i++) {
            float y = padding + i * (height / horizontalLines);
            Path horizontalPath = new Path();
            horizontalPath.moveTo(originX, y);
            horizontalPath.lineTo(getWidth() - padding, y);
            canvas.drawPath(horizontalPath, dashedLinePaint);
        }

        // Desenhar eixos principais com cantos arredondados MANUAIS
        float cornerRadius = 22f;

        // Eixo Y (vertical) - do topo até antes do canto
        canvas.drawLine(originX, padding, originX, originY - cornerRadius, axisPaint);

        // Eixo X (horizontal) - do canto até o fim
        canvas.drawLine(originX + cornerRadius, originY, getWidth() - padding, originY, axisPaint);

        // Desenhar o canto arredondado manualmente usando Path com quadTo
        Path cornerPath = new Path();
        cornerPath.moveTo(originX, originY - cornerRadius); // Começa no final do eixo Y
        cornerPath.quadTo(originX, originY, originX + cornerRadius, originY); // Curva suave até o eixo X
        canvas.drawPath(cornerPath, axisPaint);

        // Criar gradiente para preenchimento (laranja claro para transparente)
        LinearGradient gradient = new LinearGradient(
                0, padding, // Início (topo)
                0, originY, // Fim (base)
                Color.parseColor("#66FF8C00"), // Laranja claro transparente (66 = ~40% opacidade)
                Color.TRANSPARENT, // Transparente na base
                Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);

        // Desenhar área preenchida abaixo da linha
        if (dataPoints.length > 1) {
            Path fillPath = new Path();

            // Começar do canto inferior esquerdo
            fillPath.moveTo(originX, originY);

            // Subir para o primeiro ponto de dados
            float firstX = originX;
            float firstY = originY - (dataPoints[0] / maxVal) * height;
            fillPath.lineTo(firstX, firstY);

            // Conectar todos os pontos de dados
            for (int i = 1; i < dataPoints.length; i++) {
                float x = originX + i * spaceBetweenPoints;
                float y = originY - (dataPoints[i] / maxVal) * height;
                fillPath.lineTo(x, y);
            }

            // Fechar o path descendo para a base
            float lastX = originX + (dataPoints.length - 1) * spaceBetweenPoints;
            fillPath.lineTo(lastX, originY); // Descer para a linha base
            fillPath.lineTo(originX, originY); // Voltar ao início
            fillPath.close();

            // Desenhar o preenchimento
            canvas.drawPath(fillPath, fillPaint);
        }

        // Desenhar linha conectando os pontos de dados (LARANJA)
        if (dataPoints.length > 1) {
            Path dataPath = new Path();

            // Primeiro ponto
            float firstX = originX;
            float firstY = originY - (dataPoints[0] / maxVal) * height;
            dataPath.moveTo(firstX, firstY);

            // Conectar todos os pontos
            for (int i = 1; i < dataPoints.length; i++) {
                float x = originX + i * spaceBetweenPoints;
                float y = originY - (dataPoints[i] / maxVal) * height;
                dataPath.lineTo(x, y);
            }

            // Desenhar a linha
            canvas.drawPath(dataPath, dataLinePaint);
        }

        // Desenhar pontos de dados (círculos laranja)
        for (int i = 0; i < dataPoints.length; i++) {
            float x = originX + i * spaceBetweenPoints;
            float y = originY - (dataPoints[i] / maxVal) * height;
            canvas.drawCircle(x, y, 12, pointPaint); // Círculos de raio 12
        }
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