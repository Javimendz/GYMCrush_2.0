package com.example.skynet.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class CustomDonutChart extends View {
    private Paint paint;
    private RectF rectF;
    private float proteinPercentage = 0;
    private float carbsPercentage = 0;
    private float fatsPercentage = 0;

    private int proteinColor = 0xFFF06292; // Rosa
    private int carbsColor = 0xFF4FC3F7;    // Azul
    private int fatsColor = 0xFFFFD54F;     // Amarillo
    private int emptyColor = 0x33FFFFFF;    // Gris transparente

    public CustomDonutChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        rectF = new RectF();
    }

    public void setData(float protein, float carbs, float fats) {
        float total = protein + carbs + fats;
        if (total > 0) {
            this.proteinPercentage = (protein / total) * 360;
            this.carbsPercentage = (carbs / total) * 360;
            this.fatsPercentage = (fats / total) * 360;
        } else {
            this.proteinPercentage = 0;
            this.carbsPercentage = 0;
            this.fatsPercentage = 0;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float thickness = Math.min(width, height) * 0.15f;
        float margin = thickness / 2;

        rectF.set(margin, margin, width - margin, height - margin);
        paint.setStrokeWidth(thickness);

        // Fondo (círculo vacío)
        paint.setColor(emptyColor);
        canvas.drawArc(rectF, 0, 360, false, paint);

        if (proteinPercentage + carbsPercentage + fatsPercentage > 0) {
            float startAngle = -90;

            // Proteínas
            paint.setColor(proteinColor);
            canvas.drawArc(rectF, startAngle, proteinPercentage, false, paint);
            startAngle += proteinPercentage;

            // Carbos
            paint.setColor(carbsColor);
            canvas.drawArc(rectF, startAngle, carbsPercentage, false, paint);
            startAngle += carbsPercentage;

            // Grasas
            paint.setColor(fatsColor);
            canvas.drawArc(rectF, startAngle, fatsPercentage, false, paint);
        }
    }
}
