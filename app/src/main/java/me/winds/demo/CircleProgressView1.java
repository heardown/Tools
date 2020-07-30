package me.winds.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/9/6.
 * Desc:
 */
public class CircleProgressView1 extends View {

    private static final String TAG = "CircleProgressView";

    /**
     * 内圆半径
     */
    private int mRadius;
    /**
     * 圆弧宽度
     */
    private float mArcWidth = 40;
    /**
     * 背景弧宽度
     */
    private float mBgArcWidth = 20;

    /**
     * 小圆半径
     */
    private float mSmallRadius = 30;

    /**
     * 圆心点坐标
     */
    private Point mCenterPoint = new Point();

    /**
     * 圆弧边界
     */
    private RectF mRectF = new RectF();

    private RectF mBgRectF = new RectF();

    /**
     * 圆弧一周最大值
     */
    private int mMaxValue;

    /**
     * 开始角度
     */
    private int mStartAngle = 150;

    /**
     * 当前值
     */
    private int mCurrentValue;

    /**
     * 圆弧背景画笔
     */
    private Paint mBgArcPaint;

    /**
     * 圆弧画笔
     */
    private Paint mArcPaint;

    /**
     * 内圆画笔
     */
    private Paint mCirclePaint;

    /**
     * 小圆圈画笔
     */
    private Paint mSmallCirclePaint;

    /**
     * 渐变器
     */
    private SweepGradient mSweepGradient;

    /**
     * 当前需要画的进度
     */
    private int finalCurrentValue;

    /**
     * 差值器缓存数
     */
    private int tempValue;

    /**
     * 进度圆环内半径
     */
    private int progressInsideRadius;


    private boolean isRun = false;

    private boolean isNeedToReset = false;


    public CircleProgressView1(Context context) {
        this(context, null);
    }

    public CircleProgressView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();

    }

    private void initPaint() {
        // 内圆
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStyle(Paint.Style.FILL);

        // 圆弧背景
        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(true);
        mBgArcPaint.setColor(Color.WHITE);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);

        // 圆弧
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        // 发光小圆
        mSmallCirclePaint = new Paint();
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setColor(Color.parseColor("#7cffff"));
        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setMaskFilter(new BlurMaskFilter(mSmallRadius / 2, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        //求圆弧和背景圆弧的最大宽度
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //求最小值作为实际值,这是直径
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,

                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);

        //内圆半径： 进度圆环内半径 - 中间空余距离为17px = 内圆半径
        mRadius = minSize / 2 - 28;

        //获取圆的相关参数
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;

        //绘制进度圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;

        //绘制背景圆弧的边界
        mBgRectF.left = mCenterPoint.x - mRadius - (mArcWidth - mBgArcWidth) - mBgArcWidth / 2;
        mBgRectF.top = mCenterPoint.y - mRadius - (mArcWidth - mBgArcWidth) - mBgArcWidth / 2;
        mBgRectF.right = mCenterPoint.x + mRadius + (mArcWidth - mBgArcWidth) + mBgArcWidth / 2;
        mBgRectF.bottom = mCenterPoint.y + mRadius + (mArcWidth - mBgArcWidth) + mBgArcWidth / 2;

        updateArcPaint();
        mArcPaint.setShader(mSweepGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArc(canvas);
    }

    private void drawArc(Canvas canvas) {
        // 逆时针旋转94度
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);

        // 画内圆
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, mCirclePaint);

        // 圆环背景
        canvas.drawArc(mBgRectF, 0, 360, false, mBgArcPaint);

        if (mCurrentValue == 0) {
            return;
        }

        if (isNeedToReset) {
            isNeedToReset = false;
            tempValue = 0;
            startAnimator();
            return;
        }
        // 设置渐变

        float currentAngle = finalCurrentValue * 1.0f / mMaxValue * 360;
        // +4 是因为绘制的时候出现了圆弧起点有尾巴的问题
        canvas.drawArc(mRectF, 4, currentAngle, false, mArcPaint);

        // 设置发光的圆
//        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 计算小圆距离中心点的距离
        float tempRadius = mRadius + mArcWidth / 2 + 1;
        // 根据求圆上一点的方式，求出圆上的点相对于圆心的距离
        if (currentAngle >= 360) currentAngle = 358;
        float y1 = tempRadius * (float) Math.sin((currentAngle + 4) * Math.PI / 180);
        float x1 = tempRadius * (float) Math.cos((currentAngle + 4) * Math.PI / 180);
        // 算出小圆圆心坐标，根据此坐标，画出小圆
//        canvas.drawCircle(mCenterPoint.x + x1, mCenterPoint.y + y1, mSmallRadius, mSmallCirclePaint);
        canvas.drawCircle(mCenterPoint.x + x1, mCenterPoint.y + y1, mSmallRadius, mArcPaint);

    }


    private void updateArcPaint() {
        // 设置渐变
//        int[] mGradientColors = {Color.parseColor("#FD5E45"), Color.parseColor("#FFD573")};
        int[] mGradientColors = {Color.parseColor("#FD5E45"), Color.parseColor("#FFD573"), Color.parseColor("#50D4FF"), Color.parseColor("#55F0A2"), Color.parseColor("#FD5E45")};
        // 0点钟和9点钟位置
        float[] positions = {0f, 0.3f, 0.5f, 0.8f, 1.0f};
        mSweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColors, positions);

    }

    /**
     * 开始计算差值器
     */
    public void startAnimator() {

        ValueAnimator animator = ValueAnimator.ofInt(0, mCurrentValue);
        animator.setDuration(5000);
        final int finalTempCurrentValue = mCurrentValue;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                finalCurrentValue = (int) (animation.getAnimatedValue());
                isRun = finalCurrentValue != finalTempCurrentValue;
                if (finalCurrentValue > tempValue) {
                    tempValue = finalCurrentValue;
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
    public void setCurrentValue(int mCurrentValue) {
        this.mCurrentValue = mCurrentValue;
        if (this.mCurrentValue > mMaxValue)
            this.mCurrentValue = mMaxValue;
        if (isRun) return;
        needToReset();
    }

    /**
     * 设置最大值
     */
    public void setMaxValue(int mMaxValue) {
        this.mMaxValue = mMaxValue;
    }


}
