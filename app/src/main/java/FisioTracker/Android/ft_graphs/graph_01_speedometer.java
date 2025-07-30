package FisioTracker.Android.ft_graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.TubeSpeedometer;

public class graph_01_speedometer extends TubeSpeedometer {

    private Paint backgroundPaint;
    private Paint gradientPaint;
    private RectF arcRect;

    public graph_01_speedometer(Context context) {
        super(context);
        init();
    }

    public graph_01_speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public graph_01_speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Paint para a barra de fundo fixa (não muda com velocidade)
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND); // Pontas arredondadas
        backgroundPaint.setColor(Color.parseColor("#404040")); // Cor #404040

        // Paint para a barra dinâmica (muda com velocidade, fica cinza)
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaint.setStyle(Paint.Style.STROKE);
        gradientPaint.setStrokeCap(Paint.Cap.ROUND); // Pontas arredondadas
        gradientPaint.setColor(Color.parseColor("#404040")); // Mantém cinza, sem gradiente

        // Remove a barra padrão do TubeSpeedometer
        setSpeedometerColor(Color.TRANSPARENT);
    }

    private void setSpeedometerColor(int transparent) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float padding = getSpeedometerWidth() + dpTOpx(5);
        arcRect = new RectF(padding, padding, w - padding, h - padding);

        backgroundPaint.setStrokeWidth(getSpeedometerWidth());
        gradientPaint.setStrokeWidth(getSpeedometerWidth());

        // Não aplica gradiente - a barra dinâmica fica cinza
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1. Desenha a barra de fundo fixa (sempre completa, não muda)
        canvas.drawArc(arcRect, getStartDegree(), getEndDegree() - getStartDegree(), false, backgroundPaint);

        // 2. Desenha a barra dinâmica menor por cima (baseada na velocidade atual, cor cinza)
        float currentSweepAngle = (getCurrentSpeed() / getMaxSpeed()) * (getEndDegree() - getStartDegree());
        canvas.drawArc(arcRect, getStartDegree(), currentSweepAngle, false, gradientPaint);

        // 3. Chama o desenho padrão (ponteiro, textos, etc)
        super.onDraw(canvas);
    }
}