package io.github.xialincn.fakenokiaclock.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.xialincn.fakenokiaclock.utils.Size;


/**
 * Created by lin on 2016/1/18.
 */
public class HandView extends FrameLayout {
    private final String TAG = "HandView";

    // The ratio of the big surface's and the hand's width or height.
    public final static float mHandRatio = 5.5f;

    protected TextView mTextView;
    protected ImageView mImageView;
    protected Size mSize;
    private FrameLayout.LayoutParams mParams;
    private int mBoundary;
    private OnTimeUpdateListener mListener;

    public HandView(Context context, AttributeSet attrs) {
        super(context, null);

        mImageView = new ImageView(context);

        final float RATIO = 1.7f;
        mSize = Size.extract(context, attrs);
        int fontSize = (int) (mSize.width / getResources().getDisplayMetrics().density / RATIO /
                mHandRatio);

        mTextView = new TextView(context);
        mTextView.setTextSize(fontSize);
        mTextView.setGravity(Gravity.CENTER);
        mParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
    }

    protected final int CIRCUMFERENCE = 360;

    public void init(int textColor, int resourceId, int end) {
        mImageView.setImageResource(resourceId);
        addView(mImageView);

        setBoundary(end);
        mTextView.setText("00");
        mTextView.setTextColor(textColor);
        addView(mTextView, mParams);
    }

    private final int MAX_HOUR = 12;
    private final int MAX_MINUTE = 60;

    public void setBoundary(int end) {
        if (end != MAX_HOUR && end != MAX_MINUTE) {
            throw new RuntimeException("invalid boundary");
        }

        mBoundary = end;
    }

    public void launchRotation(float angleStart, float angleEnd, long duration) {
        if (angleStart - angleEnd >= CIRCUMFERENCE / 2) {
            angleEnd += CIRCUMFERENCE;
        } else if (angleStart - angleEnd <= -CIRCUMFERENCE / 2) {
            angleEnd -= CIRCUMFERENCE;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "Rotation", angleStart, angleEnd);
        animator.setDuration(duration);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (getRotation() < -10e-6) {
                    setRotation(getRotation() + CIRCUMFERENCE);
                    if (mBoundary == MAX_HOUR) {
                        NokiaClockView.switchPeriod();
                    }
                } else if (getRotation() >= CIRCUMFERENCE) {
                    setRotation(getRotation() - CIRCUMFERENCE);
                    if (mBoundary == MAX_HOUR) {
                        NokiaClockView.switchPeriod();
                    }
                }

                int time = (int) (getRotation() * mBoundary / CIRCUMFERENCE);
                if (mBoundary == MAX_HOUR && NokiaClockView.getPeriod() == "PM" && time == 0) {
                    time = MAX_HOUR;
                    mTextView.setText(Integer.toString(time));
                } else {
                    String s = String.format("%02d", (int) (getRotation() * mBoundary / CIRCUMFERENCE));
                    mTextView.setText(s);
                }

                if (mListener != null) {
                    mListener.onTimeUpdate(time);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final TextView tv = mTextView;
        float delta = angleEnd - angleStart;
        ObjectAnimator counteractAnim = ObjectAnimator.ofFloat(tv, "Rotation", tv.getRotation(),
                tv.getRotation() - delta);
        counteractAnim.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animator, counteractAnim);
        animSet.start();
    }

    public void setOnTimeUpdateListener(OnTimeUpdateListener listener) {
        mListener = listener;
    }
}