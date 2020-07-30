package me.winds.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/9/6.
 * Desc:
 */
public class CircleProgressView extends View {

    private static final String TAG = CircleProgressView.class.getSimpleName();

    private int innerRadius; //内圆半径
    private int centerArcWidth;    //中间环宽度
    private float arcWidth; //圆弧宽度
    private float bgArcWidth; //背景弧宽度
    private int outerArcWidth;    //外环宽度

    private float indicatorOuterRadius; //小圆中心距离内圆中心点半径
    private float indicatorCircleRadius; //小圆半径
    private float indicatorOuterArcWidth; //小圆外环宽度

    private Point centerPoint = new Point(); // 圆心点坐标
    private RectF rectF = new RectF(); //圆弧边界
    private RectF centerRectF = new RectF(); //内圆
    private RectF bgRectF = new RectF(); //背景
    private RectF outerRectF = new RectF(); //外围环
    private RectF ovalRectF = new RectF(); //文本圆角矩形坐标

    private Paint bgArcPaint; //圆弧背景画笔
    private Paint mArcPaint;    //圆弧画笔
    private Paint centerArcPaint;    //中环画笔
    private Paint innerCirclePaint;  //内圆画笔
    private Paint outArcPaint;       //外环画笔
    private Paint indicatorCirclePaint;    //指示器圆画笔
    private Paint indicatorOuterArcPaint;    //指示器外环圆画笔
    private Paint degreePaint;      //刻度条
    private Paint rectPaint;        //文字圆角矩形边框画笔
    private TextPaint textPaint;    //文字画笔

    private PaintFlagsDrawFilter drawFilter; //全局过滤
    private SweepGradient sweepGradient;    //渐变器
    private RadialGradient radialGradient;  //内圆渐变
    private ColorPickerGradient pickerGradient; //渐变颜色选择器

    private int startAngle = 150;  // 开始角度
    private int maxValue = 100;  //圆弧一周最大值
    private int currentValue;  //当前值
    private String diffValue;  //变化值
    private int transformingValue; //当前持续变化中的进度 绘制需要

    private int tempValue;  //差值器缓存数

    private boolean isRun = false;
    private boolean isNeedToReset = false;

    int[] gradientColors = {
            Color.parseColor("#5FC6F6"), //深蓝
            Color.parseColor("#FD5E45"), //红
            Color.parseColor("#FFD573"), //黄
            Color.parseColor("#55F0A2"), //绿
//            Color.parseColor("#50D4FF"), //浅蓝
            Color.parseColor("#5FC6F6"), //深蓝
    };
    float[] positions = {0.2f, 0.4f, 0.6f, 0.8f, 1.0f};

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        innerRadius = dp2px(getContext(), 14); //默认外环宽度
        arcWidth = dp2px(getContext(), 5); //圆弧宽度
        bgArcWidth = dp2px(getContext(), 5); //背景弧宽度
        outerArcWidth = dp2px(getContext(), 13);
        centerArcWidth = dp2px(getContext(), 9);
        indicatorCircleRadius = dp2px(getContext(), 3.5f);
        indicatorOuterArcWidth = indicatorCircleRadius + dp2px(getContext(), 2f);
        //内圆
        innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setStyle(Paint.Style.FILL);

        //中间圆环
        centerArcPaint = new Paint();
        centerArcPaint.setAntiAlias(true);
        centerArcPaint.setColor(Color.parseColor("#5785D8"));
        centerArcPaint.setStyle(Paint.Style.STROKE);
        centerArcPaint.setStrokeWidth(centerArcWidth);

        //圆弧背景
        bgArcPaint = new Paint();
        bgArcPaint.setAntiAlias(true);
        bgArcPaint.setColor(Color.parseColor("#A2C4FF"));
        bgArcPaint.setStyle(Paint.Style.STROKE);
        bgArcPaint.setStrokeWidth(bgArcWidth);

        //进度圆弧
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(arcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);

        // 指示圆
        indicatorCirclePaint = new Paint();
        indicatorCirclePaint.setAntiAlias(true);
        indicatorCirclePaint.setStyle(Paint.Style.FILL);
        // 指示圆
        indicatorOuterArcPaint = new Paint();
        indicatorOuterArcPaint.setColor(Color.WHITE);
        indicatorOuterArcPaint.setAntiAlias(true);
        indicatorOuterArcPaint.setStyle(Paint.Style.FILL);
//        indicatorCirclePaint.setMaskFilter(new BlurMaskFilter(indicatorCircleRadius / 2, BlurMaskFilter.Blur.NORMAL));

        // 外环
        outArcPaint = new Paint();
        outArcPaint.setAntiAlias(true);
        outArcPaint.setColor(Color.parseColor("#996D9FF8"));
        outArcPaint.setStyle(Paint.Style.STROKE);
        outArcPaint.setStrokeWidth(outerArcWidth);

        //刻度画笔
        degreePaint = new Paint();
        degreePaint.setStrokeWidth(dp2px(getContext(), 1));
        degreePaint.setColor(Color.parseColor("#73A5FF"));

        //文本画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);

        //文本边框画笔
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(dp2px(getContext(), 0.5f));

        pickerGradient = new ColorPickerGradient(); //文本选色
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        setLayerType(LAYER_TYPE_SOFTWARE, null); //去除硬件加速
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //求圆弧和背景圆弧的最大宽度
        float maxArcWidth = Math.max(arcWidth, bgArcWidth);
        //求最小值作为实际值,这是直径
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - outerArcWidth,
                h - getPaddingTop() - getPaddingBottom() - outerArcWidth);

        //内圆半径： 进度圆环内半径 - 中间空余距离为17px = 内圆半径
        innerRadius = (int) (minSize / 2 - centerArcWidth - maxArcWidth - outerArcWidth);

        //获取圆的相关参数
        centerPoint.x = w / 2;
        centerPoint.y = h / 2;

        //中间环
        centerRectF.left = centerPoint.x - innerRadius - centerArcWidth / 2;
        centerRectF.top = centerPoint.y - innerRadius - centerArcWidth / 2;
        centerRectF.right = centerPoint.x + innerRadius + centerArcWidth / 2;
        centerRectF.bottom = centerPoint.y + innerRadius + centerArcWidth / 2;

        //绘制进度圆弧的边界
        float size = (arcWidth > bgArcWidth ? arcWidth + (arcWidth - bgArcWidth) / 2 : arcWidth) / 2;
        rectF.left = centerPoint.x - innerRadius - centerArcWidth - size;
        rectF.top = centerPoint.y - innerRadius - centerArcWidth - size;
        rectF.right = centerPoint.x + innerRadius + centerArcWidth + size;
        rectF.bottom = centerPoint.y + innerRadius + centerArcWidth + size;

        //绘制背景圆弧的边界
        bgRectF.left = centerPoint.x - innerRadius - centerArcWidth - bgArcWidth / 2;
        bgRectF.top = centerPoint.y - innerRadius - centerArcWidth - bgArcWidth / 2;
        bgRectF.right = centerPoint.x + innerRadius + centerArcWidth + bgArcWidth / 2;
        bgRectF.bottom = centerPoint.y + innerRadius + centerArcWidth + bgArcWidth / 2;

        //外围装饰环
        outerRectF.left = centerPoint.x - innerRadius - centerArcWidth - bgArcWidth - outerArcWidth / 2;
        outerRectF.top = centerPoint.y - innerRadius - centerArcWidth - bgArcWidth - outerArcWidth / 2;
        outerRectF.right = centerPoint.x + innerRadius + centerArcWidth + bgArcWidth + outerArcWidth / 2;
        outerRectF.bottom = centerPoint.y + innerRadius + centerArcWidth + bgArcWidth + outerArcWidth / 2;

        indicatorOuterRadius = innerRadius + centerArcWidth + bgArcWidth / 2 + 1;
        //内圆渐变
        radialGradient = new RadialGradient(centerPoint.x, centerPoint.y, innerRadius,
                new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#F1F6FF")}, new float[]{0.5f, 1f}, Shader.TileMode.MIRROR);
        innerCirclePaint.setShader(radialGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    private void drawArc(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);
        // 画内圆
        canvas.drawCircle(centerPoint.x, centerPoint.y, innerRadius, innerCirclePaint);
        //中间环
        canvas.drawArc(centerRectF, 0, 360, false, centerArcPaint);
        //绘制刻度条
        drawDegree(canvas);
        //进度环背景
        canvas.drawArc(bgRectF, 0, 360, false, bgArcPaint);
        //外围装饰环
        canvas.drawArc(outerRectF, 0, 360, false, outArcPaint);

        if (currentValue == 0) {
            return;
        }

        if (isNeedToReset) {
            isNeedToReset = false;
            tempValue = 0;
            startAnimator();
            return;
        }
        //绘制指示弧和点
        drawIndicator(canvas);
        //绘制文本
        drawText(canvas);
    }

    /**
     * 绘制指示弧和点
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        // 设置渐变
        float currentAngle = transformingValue * 1.0f / maxValue * 360;
        canvas.drawArc(rectF, startAngle, currentAngle, false, mArcPaint);
        // 计算小圆距离中心点的距离
        // 根据求圆上一点的方式，求出圆上的点相对于圆心的距离
        if (currentAngle >= 360) {
            currentAngle = 360;
        }
        float y1 = indicatorOuterRadius * (float) Math.sin((currentAngle + startAngle) * Math.PI / 180);
        float x1 = indicatorOuterRadius * (float) Math.cos((currentAngle + startAngle) * Math.PI / 180);
        // 算出小圆圆心坐标，根据此坐标，画出小圆
        canvas.drawCircle(centerPoint.x + x1, centerPoint.y + y1, indicatorOuterArcWidth, indicatorOuterArcPaint);
        canvas.drawCircle(centerPoint.x + x1, centerPoint.y + y1, indicatorCircleRadius, indicatorCirclePaint);
    }

    /**
     * 绘制文本信息
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        boolean isEmpty = TextUtils.isEmpty(diffValue);
        int color = pickerGradient.getColor(transformingValue / 100f);
        textPaint.setFakeBoldText(true);
        textPaint.setColor(color);
        int numSize = sp2px(getContext(), 28);
        textPaint.setTextSize(numSize);
        int width = calcTextWidth(textPaint, String.valueOf(transformingValue));
        int numY = centerPoint.y + numSize / 2 - (isEmpty ?  0 : dp2px(getContext(), 8));
        canvas.drawText(String.valueOf(transformingValue), centerPoint.x - width / 2, numY, textPaint);

        if(!isEmpty) {
            String text = diffValue;
            textPaint.setFakeBoldText(false);
            int textSize = sp2px(getContext(), 12);
            textPaint.setTextSize(textSize);
            int textWidth = calcTextWidth(textPaint, text);
            int textY = centerPoint.y + textSize + dp2px(getContext(), 10);
            canvas.drawText(text, centerPoint.x - textWidth / 2, textY, textPaint);

            int padding = dp2px(getContext(), 6);
            ovalRectF.left = centerPoint.x - textWidth / 2 - padding;
            ovalRectF.right = centerPoint.x + textWidth / 2 + padding;
            ovalRectF.top = textY - textSize + dp2px(getContext(), 1);
            ovalRectF.bottom = textY + dp2px(getContext(), 2);

            rectPaint.setColor(color);
            canvas.drawRoundRect(ovalRectF, textSize, textSize, rectPaint);
        }
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawDegree(Canvas canvas) {
        int lineCount = 36;
        canvas.rotate(0, centerPoint.x, centerPoint.y);
        float unitDegrees = (float) (2.0f * Math.PI / lineCount);
        float interCircleRadius = innerRadius + dp2px(getContext(), 1);
        float outerCircleRadius = interCircleRadius + dp2px(getContext(), 4);

        for (int i = 0; i < lineCount; i++) {
            if (i > 3 && i < 15) {
                continue;
            }

            float rotateDegrees = i * -unitDegrees;

            float startX = centerPoint.x + (float) Math.cos(rotateDegrees) * interCircleRadius;
            float startY = centerPoint.y - (float) Math.sin(rotateDegrees) * interCircleRadius;

            float stopX = centerPoint.x + (float) Math.cos(rotateDegrees) * outerCircleRadius;
            float stopY = centerPoint.y - (float) Math.sin(rotateDegrees) * outerCircleRadius;

            canvas.drawLine(startX, startY, stopX, stopY, degreePaint);
        }
    }

    /**
     * 开始计算差值器
     */
    public void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(0, currentValue);
        animator.setDuration(2000);
        final int finalTempCurrentValue = currentValue;
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                transformingValue = (int) (animation.getAnimatedValue());
                isRun = transformingValue != finalTempCurrentValue;
                if (transformingValue > tempValue) {
                    tempValue = transformingValue;
                    invalidate();
                }
            }
        });
        animator.start();
    }

    private void needToReset() {
        isNeedToReset = true;
        invalidate();
    }

    /**
     * 设置圆弧当前值
     */
    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        if (this.currentValue > maxValue)
            this.currentValue = maxValue;
        if (isRun) return;

        // 设置渐变
        sweepGradient = new SweepGradient(centerPoint.x, centerPoint.y, gradientColors, positions);
        mArcPaint.setShader(sweepGradient);
        indicatorCirclePaint.setShader(sweepGradient);

        needToReset();
    }

    public void setCurrentValue(int currentValue, String diffValue) {
        this.currentValue = currentValue;
        this.diffValue = diffValue;
        if (this.currentValue > maxValue)
            this.currentValue = maxValue;
        if (isRun) return;

        // 设置渐变
        sweepGradient = new SweepGradient(centerPoint.x, centerPoint.y, gradientColors, positions);
        mArcPaint.setShader(sweepGradient);
        indicatorCirclePaint.setShader(sweepGradient);

        needToReset();
    }

    /**
     * 设置最大值
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 计算文本宽度
     *
     * @param paint
     * @param text
     * @return
     */
    public int calcTextWidth(Paint paint, String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        return (int) paint.measureText(text);
    }
}
