package com.example.helloworld_java.ui.widget;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworld_java.R;

public class CircleProgressView extends View {

    private Paint backgroundPaint;   // 背景圆环
    private Paint progressPaint;     // 进度圆环
    private Paint textPaint;         // 文字
    private RectF rectF;    //矩形绘制
    private int progress = 0;
    private int maxProgress = 100;
    private String text = "25:00";
    private int backgroundColor = 0xFFE0E0E0;
    private int progressColor = 0xFFFF5722;
    private int textColor = 0xFFE7D849;
    // 尺寸
    private float strokeWidth = 20f; // 圆环宽度

    public CircleProgressView(Context context) {
        super(context);
        init(null);
    }
    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attrs) {
        // 从 XML 属性读取自定义属性
        if (attrs != null) {
           try( TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView)) {
               backgroundColor = ta.getColor(R.styleable.CircleProgressView_backgroundColor, backgroundColor);
               progressColor = ta.getColor(R.styleable.CircleProgressView_progressColor, progressColor);
               textColor = ta.getColor(R.styleable.CircleProgressView_textColor, textColor);
               strokeWidth = ta.getDimension(R.styleable.CircleProgressView_strokeWidth, strokeWidth);
               progress = ta.getInt(R.styleable.CircleProgressView_progress, progress);
               maxProgress = ta.getInt(R.styleable.CircleProgressView_maxProgress, maxProgress);
               text = ta.getString(R.styleable.CircleProgressView_text);
               if (text == null) text = "25:00";
           }
        }
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(dpToPx(48));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        rectF = new RectF();
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;
        rectF.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);
        float sweepAngle = 360 * progress / (float) maxProgress;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
        float textY = centerY - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, centerX, textY, textPaint);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredSize = (int) dpToPx(280); // 默认280dp
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredSize, widthSize);
        } else {
            width = desiredSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredSize, heightSize);
        } else {
            height = desiredSize;
        }
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
    public void setProgress(int progress) {
        this.progress = Math.min(Math.max(progress, 0), maxProgress);
        invalidate();
    }
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
    public void setText(String text) {
        this.text = text;
        invalidate();
    }
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }
    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }
    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(color);
        invalidate();
    }
    public void setTextSize(float size) {
        textPaint.setTextSize(dpToPx(size));
        invalidate();
    }
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
