package io.github.xialincn.fakenokiaclock.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import io.github.xialincn.fakenokiaclock.R;
import io.github.xialincn.fakenokiaclock.utils.Size;

/**
 * Created by lin on 2016/1/31.
 */
public class NokiaClockView extends FrameLayout {

    private static final String TAG = "NokiaClockView";

    // The big surface's size is the baseline.
    // The ratio of the big surface's and the small one's width or height.
    protected final static float mSurfaceRatio = 1.64f;
    // The view size.
    public static Size mLayoutSize = null;

    private ClockSurface mSmallSurface, mBigSurface;
    private ClockHand mShortHand, mLongHand;
    private int mHour, mMinute;

    private enum Period {AM, PM}
    private static Period sPeriod = Period.AM;
    public static void switchPeriod() {
        sPeriod = (sPeriod == Period.AM ? Period.PM : Period.AM);
    }
    public static String getPeriod() {
        return sPeriod.toString();
    }

    public NokiaClockView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLayoutSize = Size.extract(context, attrs);

        addBigSurface(context, attrs);
        addSmallSurface(context, attrs);
        addLongHand(context, attrs);
        addShortHand(context, attrs);

        mBigSurface.addHand(mLongHand);
        mSmallSurface.addHand(mShortHand);
    }

    private void addBigSurface(Context context, AttributeSet attrs) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.width = mLayoutSize.width;
        params.height = mLayoutSize.height;

        mBigSurface = new ClockSurface(context, attrs);
        mBigSurface.setImageResource(R.drawable.big_surface);
        addView(mBigSurface, params);

        mBigSurface.init(params);
    }

    private void addSmallSurface(Context context, AttributeSet attrs) {
        int width = (int) (mLayoutSize.width / mSurfaceRatio);
        int height = (int) (mLayoutSize.height / mSurfaceRatio);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        final int margin = (mLayoutSize.height - height) / 2;
        params.setMargins(margin, margin, margin, margin);

        mSmallSurface = new ClockSurface(context, attrs);
        mSmallSurface.setImageResource(R.drawable.small_surface);
        addView(mSmallSurface, params);

        mSmallSurface.init(params);
    }

    private void addLongHand(Context context, AttributeSet attrs) {
        int width = (int) (mLayoutSize.width / ClockHand.mHandRatio);
        int height = (int) (mLayoutSize.height / ClockHand.mHandRatio);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        int topMargin = (mLayoutSize.height - (int) (mLayoutSize.height / mSurfaceRatio) - 2 * height) / 4;
        params.setMargins(params.leftMargin, topMargin, params.rightMargin, params.bottomMargin);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        mLongHand = new ClockHand(context, attrs);
        mLongHand.init(Color.WHITE, R.drawable.long_hand, 60);
        addView(mLongHand, params);
        mLongHand.setOnTimeUpdateListener(new OnTimeUpdateListener() {
            @Override
            public void onTimeUpdate(int value) {
                mMinute = value;
                if (mMinuteListener != null) {
                    mMinuteListener.onTimeUpdate(mMinute);
                }
            }
        });
    }

    private void addShortHand(Context context, AttributeSet attrs) {
        int width = (int) (mLayoutSize.width / ClockHand.mHandRatio);
        int height = (int) (mLayoutSize.height / ClockHand.mHandRatio);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);

        int handHeight = (int) (mLayoutSize.height / ClockHand.mHandRatio);
        int m = (mLayoutSize.height - (int) (mLayoutSize.height / NokiaClockView.mSurfaceRatio) - 2 * handHeight) / 4;
        int topMargin = m + height + m * 2;
        params.setMargins(params.leftMargin, topMargin, params.rightMargin, params.bottomMargin);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        mShortHand = new ClockHand(context, attrs);
        mShortHand.init(Color.BLACK, R.drawable.short_hand, 12);
        addView(mShortHand, params);

        mShortHand.setOnTimeUpdateListener(new OnTimeUpdateListener() {
            @Override
            public void onTimeUpdate(int value) {
                mHour = value;
                if (mHourListener != null) {
                    mHourListener.onTimeUpdate(mHour);
                }
            }
        });
    }

    private OnTimeUpdateListener mHourListener;
    private OnTimeUpdateListener mMinuteListener;

    public void setOnMinuteUpdateListener(OnTimeUpdateListener listener) {
        mMinuteListener = listener;
    }

    public void setOnHourUpdateListener(OnTimeUpdateListener listener) {
        mHourListener = listener;
    }
}
