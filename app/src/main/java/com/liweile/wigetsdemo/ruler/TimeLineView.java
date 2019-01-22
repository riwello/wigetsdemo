package com.liweile.wigetsdemo.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.TimeUnit;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author liweile
 * @date 2019/1/9
 */
public class TimeLineView extends View {
    private float mWidth;
    private float mHeihgt;


    private float mRulingWidth = 1;
    private Paint mRulingPaint;

    private float mMinDividerWidth = dip2px(10);
    private float mDividerWidth = mMinDividerWidth;
    private long mStartTime;

    private long mEndTime;

    private long mCurrentTime = System.currentTimeMillis();

    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
    private Paint mTagTextPaint;
    private Paint mIndicatorPaint;

    public TimeLineView(Context context) {
        super(context);
        init();
    }

    public TimeLineView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeLineView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRulingPaint = new Paint();
        mRulingPaint.setColor(Color.BLACK);
        mRulingPaint.setStrokeWidth(mRulingWidth);
        mRulingPaint.setAntiAlias(true);

        mTagTextPaint = new Paint();
        mTagTextPaint.setColor(Color.BLACK);
        mTagTextPaint.setAntiAlias(true);
        mTagTextPaint.setTextAlign(Paint.Align.CENTER);
        mTagTextPaint.setTextSize(dip2px(10));

        mIndicatorPaint =new Paint();
        mIndicatorPaint.setColor(Color.GREEN);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStrokeWidth(dip2px(1));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeihgt = MeasureSpec.getSize(heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRuling(canvas);
    }

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawRuling(Canvas canvas) {
        //上下边框
        canvas.drawLine(0, 0, mWidth, 0, mRulingPaint);
        canvas.drawLine(0, mHeihgt, mWidth, mHeihgt, mRulingPaint);

        float centerX = mWidth / 2;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(mCurrentTime);
        int centerMin = instance.get(Calendar.MINUTE);
        float offset = instance.get(Calendar.SECOND) / 60F * mDividerWidth;
        float leftStart = centerX - offset;
        int tempCenterMin = centerMin;

        canvas.drawLine(centerX,0,centerX,mHeihgt,mIndicatorPaint);

        while (leftStart > 0) {

            float hightRatio = tempCenterMin % 5 == 0 ? .2f : .1f;
            canvas.drawLine(leftStart, mHeihgt, leftStart, mHeihgt * (1f - hightRatio), mRulingPaint);
            canvas.drawLine(leftStart, 0, leftStart, mHeihgt * hightRatio, mRulingPaint);
            leftStart -= mDividerWidth;
            tempCenterMin--;


        }

        float rightStatX = centerX + mDividerWidth - offset;
        tempCenterMin = centerMin + 1;
        while (rightStatX < mWidth) {
            float hightRatio = tempCenterMin % 5 == 0 ? .3f : .1f;
            canvas.drawLine(rightStatX, 0, rightStatX, mHeihgt * hightRatio, mRulingPaint);
            canvas.drawLine(rightStatX, mHeihgt, rightStatX, mHeihgt * (1f - hightRatio), mRulingPaint);
            rightStatX += mDividerWidth;
            tempCenterMin++;
        }
        String time = sdf.format(instance.getTime());
        canvas.drawText(time, mWidth / 2, mHeihgt / 2, mTagTextPaint);

    }


    private float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static final String TAG = "TimeLineView";

    float dx;
    float dy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN ");
                dx = event.getX();
                dy = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float offset = (dx - x);
                int offSetTime = (int) (offset / mWidth * 60f * 1000f);
                mCurrentTime += offSetTime;
                dx = x;
                Log.e(TAG, "mX " + x + "   dx" + dx + "   offset" + offset + "offsetTime" + offSetTime + " timeStamp" + mCurrentTime);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;

    }


    /**
     * @param startTime SECOND
     * @param endTime   SECOND
     */
    public void setTimeRange(int startTime, int endTime) {
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    public void setCurrentTime(int currentTime) {
        this.mCurrentTime = currentTime;
    }


}
