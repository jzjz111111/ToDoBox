package com.example.helloworld_java;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

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
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
            backgroundColor = ta.getColor(R.styleable.CircleProgressView_backgroundColor, backgroundColor);
            progressColor = ta.getColor(R.styleable.CircleProgressView_progressColor, progressColor);
            textColor = ta.getColor(R.styleable.CircleProgressView_textColor, textColor);
            strokeWidth = ta.getDimension(R.styleable.CircleProgressView_strokeWidth, strokeWidth);
            progress = ta.getInt(R.styleable.CircleProgressView_progress, progress);
            maxProgress = ta.getInt(R.styleable.CircleProgressView_maxProgress, maxProgress);
            text = ta.getString(R.styleable.CircleProgressView_text);
            if (text == null) text = "25:00";
            ta.recycle();
        }

        // 初始化背景圆环画笔
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // 初始化进度圆环画笔
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // 初始化文字画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(dpToPx(48));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // 计算半径（减去圆环宽度的一半，确保完整显示）
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;

        // 设置绘制区域
        rectF.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // 绘制背景圆环（360度全圆）
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);

        // 绘制进度圆环（从顶部开始，-90度是12点钟方向）
        float sweepAngle = 360 * progress / (float) maxProgress;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        // 绘制文字（垂直居中）
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

        // 确保是正方形
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    // 设置进度
    public void setProgress(int progress) {
        this.progress = Math.min(Math.max(progress, 0), maxProgress);
        invalidate(); // 重绘
    }

    // 设置最大进度
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    // 设置显示文字
    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    // 设置背景颜色
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    // 设置进度颜色
    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }

    // 设置文字颜色
    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    // 设置文字大小
    public void setTextSize(float size) {
        textPaint.setTextSize(dpToPx(size));
        invalidate();
    }

    // dp转px
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
