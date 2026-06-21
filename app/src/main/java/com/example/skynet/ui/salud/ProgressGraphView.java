package com.example.skynet.ui.salud;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressGraphView extends View {
    private List<Float> dataPoints = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Paint linePaint;
    private Paint fillPaint;
    private Paint pointPaint;
    private Paint gridPaint;
    private Paint textPaint;

    public ProgressGraphView(Context context) {
        super(context);
        init();
    }

    public ProgressGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#CD0277"));
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#1AFFFFFF"));
        gridPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#99FFFFFF"));
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setData(List<Float> points, List<String> dates) {
        this.dataPoints = points;
        this.labels = dates;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float width = getWidth();
        float height = getHeight();
        float paddingLeft = 80f; // More padding for Y axis labels
        float paddingRight = 40f;
        float paddingTop = 40f;
        float paddingBottom = 60f; // More padding for X axis labels
        
        float graphWidth = width - paddingLeft - paddingRight;
        float graphHeight = height - paddingTop - paddingBottom;

        if (dataPoints == null || dataPoints.isEmpty()) {
            return;
        }

        float max = -Float.MAX_VALUE;
        float min = Float.MAX_VALUE;
        for (float p : dataPoints) {
            if (p > max) max = p;
            if (p < min) min = p;
        }
        
        // Add padding to vertical range
        max += 5; 
        min -= 5;
        if (min < 0) min = 0;
        float range = max - min;
        if (range <= 0) range = 1;

        // Draw Y Axis labels and horizontal grid
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < 4; i++) {
            float ratio = i / 3f;
            float y = height - paddingBottom - ratio * graphHeight;
            float labelValue = min + ratio * range;
            
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint);
            canvas.drawText(String.format(Locale.getDefault(), "%.0f", labelValue), paddingLeft - 10f, y + 8f, textPaint);
        }

        float stepX = dataPoints.size() > 1 ? graphWidth / (dataPoints.size() - 1) : graphWidth / 2;
        
        Path linePath = new Path();
        Path fillPath = new Path();

        for (int i = 0; i < dataPoints.size(); i++) {
            float x = paddingLeft + i * stepX;
            float y = height - paddingBottom - ((dataPoints.get(i) - min) / range) * graphHeight;

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, height - paddingBottom);
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
            
            if (i == dataPoints.size() - 1) {
                fillPath.lineTo(x, height - paddingBottom);
                fillPath.close();
            }

            // Draw X Axis labels
            if (labels != null && i < labels.size() && (dataPoints.size() < 7 || i % (dataPoints.size()/5 + 1) == 0 || i == dataPoints.size()-1)) {
                textPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(labels.get(i), x, height - 15f, textPaint);
            }
        }

        // Fill area
        fillPaint.setShader(new LinearGradient(0, paddingTop, 0, height - paddingBottom, 
                Color.parseColor("#4DCD0277"), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawPath(fillPath, fillPaint);

        // Draw line
        canvas.drawPath(linePath, linePaint);

        // Draw points
        for (int i = 0; i < dataPoints.size(); i++) {
            float x = paddingLeft + i * stepX;
            float y = height - paddingBottom - ((dataPoints.get(i) - min) / range) * graphHeight;
            canvas.drawCircle(x, y, 8f, pointPaint);
            canvas.drawCircle(x, y, 8f, linePaint);
        }
    }
}