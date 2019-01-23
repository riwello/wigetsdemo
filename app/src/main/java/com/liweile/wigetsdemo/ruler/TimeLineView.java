package com.liweile.wigetsdemo.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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


    private float mRulingWidth = 1;//刻度宽度
    private Paint mRulingPaint;

    private float mDefaultDividerWidth = dip2px(10);
    private float mDividerWidth = mDefaultDividerWidth;
    private long mStartTime;

    private long mEndTime;

    private long mCurrentTime = System.currentTimeMillis();

    private SimpleDateFormat mRulingDataFormat;
    private SimpleDateFormat mTimeDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd");
    private Paint mTagTextPaint;
    private Paint mIndicatorPaint;

    private ScaleMode mScaleMode = ScaleMode.MIN;

    private float mTimeLineHightPercent = .8f;
    private int mTimeLineGravity = Gravity.BOTTOM;
    private RectF mTimeLineRect;
    private int mRulingIntervalRatio = 2;
    private float mDividerScale = 1f;
    private Paint mTagBgPaint;


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
        mRulingDataFormat = new SimpleDateFormat(mScaleMode.timePattern);

        mRulingPaint = new Paint();
        mRulingPaint.setColor(Color.BLACK);
        mRulingPaint.setStrokeWidth(mRulingWidth);
        mRulingPaint.setAntiAlias(true);

        mTagTextPaint = new Paint();
        mTagTextPaint.setColor(Color.BLACK);
        mTagTextPaint.setAntiAlias(true);
        mTagTextPaint.setTextAlign(Paint.Align.CENTER);
        mTagTextPaint.setTextSize(dip2px(10));

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(Color.GREEN);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStrokeWidth(mRulingWidth);

        mTagBgPaint = new Paint();
        mTagBgPaint.setColor(Color.parseColor("#FFAFA44F"));
        mTagBgPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeihgt = MeasureSpec.getSize(heightMeasureSpec);

        mTimeLineRect = new RectF();
        float offsetY = (1f - mTimeLineHightPercent) * mHeihgt;
        switch (mTimeLineGravity) {
            default:
            case Gravity.CENTER:
                mTimeLineRect.left = 0;
                mTimeLineRect.right = mWidth;
                mTimeLineRect.top = offsetY / 2f;
                mTimeLineRect.bottom = mHeihgt - offsetY / 2f;
                break;
            case Gravity.BOTTOM:
                mTimeLineRect.left = 0;
                mTimeLineRect.right = mWidth;
                mTimeLineRect.top = offsetY;
                mTimeLineRect.bottom = mHeihgt;
                break;
            case Gravity.TOP:
                mTimeLineRect.left = 0;
                mTimeLineRect.right = mWidth;
                mTimeLineRect.top = 0;
                mTimeLineRect.bottom = mHeihgt - offsetY;
                break;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //上下边框
        canvas.drawLine(mTimeLineRect.left, mTimeLineRect.top, mTimeLineRect.right, mTimeLineRect.top, mRulingPaint);
        canvas.drawLine(mTimeLineRect.left, mTimeLineRect.bottom, mTimeLineRect.right, mTimeLineRect.bottom, mRulingPaint);

        float centerX = mTimeLineRect.width() / 2;
        //画指针
        drawIndicator(canvas, centerX);


        drawRuling(canvas);
    }

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawRuling(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mCurrentTime);

        float centerOffset = getCenterOffset(calendar);//当前时间（中心点）距离前一个刻度的距离

        float startOffset = (mTimeLineRect.width() / 2 - centerOffset) % mDividerWidth; //距离第一个刻度 的距离

//        startOffset =

//        Log.e(TAG, "centerOffset: " + centerOffset + "    startOffset" + startOffset);


        int timeOffset = -(int) ((mTimeLineRect.width() / 2 - centerOffset) / mDividerWidth);//第一个刻度 和中间的刻度 的差

        //可见的第一个刻度的时间
        calendar.add(mScaleMode.calendarUnit, timeOffset);


//        Log.e(TAG, "startMin: " + startRulingTime + " centerMin" + centerMin + " offset" + minOffset);

        float startX = startOffset;
        while (startX < mTimeLineRect.width()) {
//            Log.e(TAG, "currrentMin: " + startRulingTime);

            boolean showRulingTag = isShowRulingTag(calendar);

            if (showRulingTag) {
                String time = mRulingDataFormat.format(calendar.getTime());
//                Log.e(TAG, "true: " + startRulingTime);
                //画刻度时间
                if (mRulingIntervalRatio != 24 || mScaleMode != ScaleMode.HOUR)//缩放到最小时不显示 时间标签
                    canvas.drawText(time, startX, mTimeLineRect.bottom - mTimeLineRect.height() / 2f, mTagTextPaint);


                if (mScaleMode == ScaleMode.HOUR && calendar.get(Calendar.HOUR) == 0) {
                    drawDateText(canvas, calendar, startX);
                }

            }
            float hightRatio = showRulingTag ? .2f : .1f;
            //上刻度
            canvas.drawLine(startX, mTimeLineRect.top, startX, mTimeLineRect.top + mTimeLineRect.height() * hightRatio, mRulingPaint);

            //下刻度
            canvas.drawLine(startX, mTimeLineRect.bottom, startX, mTimeLineRect.bottom - mTimeLineRect.height() * hightRatio, mRulingPaint);

            startX += mDividerWidth;
            calendar.add(mScaleMode.calendarUnit, 1);


        }

    }


    public float converTimeStampFromCurrentTimeToWidth(long timeStamp) {
        long time = timeStamp - mCurrentTime;
        return time / mScaleMode.unitSec * mDividerWidth;

    }

    private void drawDateText(Canvas canvas, Calendar calendar, float startX) {
        String date = mDateFormat.format(calendar.getTime());
        switch (mTimeLineGravity) {
            case Gravity.CENTER:
            case Gravity.BOTTOM:

                canvas.drawText(date, startX, (mTimeLineRect.top) / 2f, mTagTextPaint);
                break;
            case Gravity.TOP:
                canvas.drawText(date, startX, (mHeihgt - mTimeLineRect.bottom) / 2f, mTagTextPaint);
                break;
        }

    }

    /**
     * 获取时间轴上总共多少秒
     */
    public long getTimeLineAllTime() {
        float count = mTimeLineRect.width() / mDividerWidth;//总共多少刻度
        return (long) (count * mScaleMode.unitSec);

    }

    private boolean isShowRulingTag(Calendar calendar) {

        int rulingTime = calendar.get(mScaleMode.calendarUnit);

        Log.e(TAG, "mDividerScale: " + mDividerScale);
        switch (mScaleMode) {
            case MIN:

                if (mDividerScale >= .5f & mDividerScale <= 1) {
                    mRulingIntervalRatio = 2;
                } else if (mDividerScale >= 1.5f & mDividerScale <= 2.5f) {
                    mRulingIntervalRatio = 3;
                } else if (mDividerScale >= 2.5f & mDividerScale <= 4.5f) {
                    mRulingIntervalRatio = 6;
                } else if (mDividerScale > 4.5f) {
                    changeScaleMode(ScaleMode.HOUR);
                }

//                Log.e(TAG, "ratio: " + mRulingIntervalRatio);
//                Log.e(TAG, "isShowRulingTag: " + ratio + "    text" + rect.width() + " condition" + rect.width() * 2);
                return rulingTime % (5 * mRulingIntervalRatio) == 0;

            case HOUR:
                if (aBoolean) {
                    Log.e(TAG, "changeScaleMode: " + mRulingIntervalRatio);
                    aBoolean = false;
                }
                if (mDividerScale < .3f) {
                    changeScaleMode(ScaleMode.MIN);
                } else if (mDividerScale >= .3f & mDividerScale <= .6f) {
                    mRulingIntervalRatio = 3;
                } else if (mDividerScale >= .6f & mDividerScale <= 1) {
                    mRulingIntervalRatio = 6;
                } else if (mDividerScale >= 1 & mDividerScale <= 2) {
                    mRulingIntervalRatio = 12;
                } else if (mDividerScale >= 2) {
                    mRulingIntervalRatio = 24;
                }
//                Log.e(TAG, "ratio: " + mRulingIntervalRatio);
                return rulingTime % (1 * mRulingIntervalRatio) == 0;
        }

        return false;
    }

    boolean aBoolean = true;

    public void changeScaleMode(ScaleMode scaleMode) {
        switch (scaleMode) {
            case HOUR:
//                mDividerWidth = mScaleMode.longRulingInterval * mRulingIntervalRatio * mDividerWidth;
//                mDividerWidth = mDefaultDividerWidth;
                mDividerScale = .3f;
                mRulingIntervalRatio = 3;
                Log.e(TAG, "changeScaleMode: " + mDividerWidth);
                break;
            case MIN:
                mRulingIntervalRatio = 6;
                mDividerScale = 4.5f;

                break;
        }
        this.mScaleMode = scaleMode;
        mDividerWidth = mDefaultDividerWidth / mDividerScale;
        mRulingDataFormat = new SimpleDateFormat(scaleMode.timePattern);
//
//
    }


    private void drawIndicator(Canvas canvas, float centerX) {
        canvas.drawLine(centerX, mTimeLineRect.top, centerX, mTimeLineRect.bottom, mIndicatorPaint);//指针
        String date = mTimeDateFormat.format(new Date(mCurrentTime));
        String centerTag = date;
        Rect rect = new Rect();
        mTagTextPaint.getTextBounds(centerTag, 0, centerTag.length(), rect);
        float y;
        switch (mTimeLineGravity) {
            default:
            case Gravity.CENTER:
            case Gravity.BOTTOM:
//                canvas.drawText(centerTag, centerX, (mTimeLineRect.top) / 2f, mTagTextPaint);
                y = (mTimeLineRect.top) / 2f;
                break;
            case Gravity.TOP:
                y = (mHeihgt - mTimeLineRect.bottom) / 2f;
//                canvas.drawText(centerTag, centerX, (mHeihgt - mTimeLineRect.bottom) / 2f, mTagTextPaint);
                break;
        }


        RectF rectF = new RectF((centerX - rect.width() / 1.9f),
                y - rect.height() / 1.5f - rect.height() / 2,
                centerX + rect.width() / 1.9f,
                y + rect.height() / 1.5f - rect.height() / 2);
//
//        canvas.drawRoundRect(rectF, dip2px(1), dip2px(1), mTagBgPaint);
        canvas.drawRect(rectF, mTagBgPaint);
        canvas.drawText(centerTag, centerX, y, mTagTextPaint);
    }


    /**
     * 当前时间 分钟/小时 秒的余数 所占的距离
     *
     * @param calendar
     * @return
     */
    public float getCenterOffset(Calendar calendar) {
        int sec = 0;
        switch (mScaleMode) {
            case MIN:
                sec = calendar.get(Calendar.SECOND);
                break;
            case HOUR:
                sec = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60);
                break;


        }
        return sec / mScaleMode.unitSec * mDividerWidth;

    }


    private float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static final String TAG = "TimeLineView";

    float dx;

    long lastMultiTouch;
    double nLenStart;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pCount = event.getPointerCount();// 触摸设备时手指的数量

        long intervalTime = System.currentTimeMillis() - lastMultiTouch;
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                if (pCount == 1 && intervalTime > 300) {
                    dx = event.getX();

                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (pCount == 1 && intervalTime > 300 && dx != 0) {
                    float x = event.getX();
                    float offset = (dx - x);
                    float scrollRatio = offset / (mTimeLineRect.width());
//                    int offSetTime = (int) (scrollRatio * mScaleMode.unitSec * 1000f);
                    int offSetTime = (int) (scrollRatio * getTimeLineAllTime() * 1000f);
                    mCurrentTime += offSetTime;
                    dx = x;

                } else if (pCount == 2) {

                    lastMultiTouch = System.currentTimeMillis();
                    // 获取抬起时候两个坐标的x轴的水平距离，取绝对值
                    float xLen = Math.abs(event.getX(0) - event.getX(1));
                    // 获取抬起时候两个坐标的y轴的水平距离，取绝对值
                    float yLen = Math.abs(event.getY(0) - event.getY(1));

                    // 根据x轴和y轴的水平距离，求平方和后再开方获取两个点之间的直线距离。此时就获取到了两个手指抬起时的直线距离
                    double nLenEnd = Math.sqrt(xLen * xLen + yLen * yLen);

                    // 根据手势按下时两个手指触点之间的直线距离A和手势抬起时两个手指触点之间的直线距离B。比较A和B的大小，得出用户是手势放大还是手势缩小
                    double scale = Math.abs((nLenEnd - nLenStart) / mWidth);

                    if (nLenEnd > nLenStart) {

                        mDividerWidth += scale * mDividerWidth;
                        if (mDividerWidth > mDefaultDividerWidth * 2 && mScaleMode == ScaleMode.MIN) {//缩放下限
                            mDividerWidth = mDefaultDividerWidth * 2;
                        }

//                        calculateRulingIntervalRatio(true);
                    } else if (nLenEnd < nLenStart) {

                        if (mDividerScale >= 3 && mScaleMode == ScaleMode.HOUR) {//放大上限

                        } else {
                            mDividerWidth -= scale * mDividerWidth;
                        }


//                        calculateRulingIntervalRatio(false);


                    }
                    mDividerScale = mDefaultDividerWidth / mDividerWidth;

                    nLenStart = nLenEnd;

                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (pCount == 1 && intervalTime > 300) {

                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (pCount == 2) {
                    lastMultiTouch = System.currentTimeMillis();
                    // 获取按下时候两个坐标的x轴的水平距离，取绝对值
                    float xLen = Math.abs(event.getX(0) - event.getX(1));
                    // 获取按下时候两个坐标的y轴的水平距离，取绝对值
                    float yLen = Math.abs(event.getY(0) - event.getY(1));

                    // 根据x轴和y轴的水平距离，求平方和后再开方获取两个点之间的直线距离。此时就获取到了两个手指刚按下时的直线距离
                    nLenStart = Math.sqrt(xLen * xLen + yLen * yLen);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (pCount == 2) {
                    lastMultiTouch = System.currentTimeMillis();
                    dx = 0;

                }
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

    public void setScaleMode(ScaleMode scaleMode) {
        this.mScaleMode = scaleMode;
        mRulingDataFormat = new SimpleDateFormat(scaleMode.timePattern);
        mDividerWidth = mDefaultDividerWidth;
        mRulingIntervalRatio = 1;
    }

    enum ScaleMode {
        MIN(60, Calendar.MINUTE, "HH:mm", 5),
        //        TWO_MIN(60*2, Calendar.MINUTE, "mm:00"),
        HOUR(60 * 60, Calendar.HOUR_OF_DAY, "HH:00", 3),;

        ScaleMode(float unitSec, int calendarUnit, String timePattern, int longRulingInterval) {
            this.unitSec = unitSec;
            this.calendarUnit = calendarUnit;
            this.timePattern = timePattern;
            this.longRulingInterval = longRulingInterval;
        }

        float unitSec;//每个小刻度多少秒
        int calendarUnit;//每个刻度的 区间单位
        String timePattern;//时间格式
        int longRulingInterval;//两个大刻度间隔多少个小刻度 的基数

    }

}
