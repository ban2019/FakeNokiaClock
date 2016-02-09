package io.github.xialincn.fakenokiaclock.views;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import static io.github.xialincn.fakenokiaclock.utils.Constants.*;

/**
 * Created by lin on 2016/1/31.
 */
public class ClockSurface extends ImageView {
    private final String TAG = "ClockSurface";

    private ClockHand mHand;
    private float mPivotXOnSurface, mPivotYOnSurface;
    private float mTouchX, mTouchY;

    private int count = 0;
    private int SAMPLE_PERIOD = 3;

    // Assume the rotation time around one circle  to 30ms when there's a finger dragging.
    private final float STEP_ROTATE_VELOCITY = CIRCUMFERENCE / 30f;
    // Assume the rotation time around one circle to 500ms.
    // Animation after long click should have a different velocity from the one above,
    // otherwise, there are not enough frames to show a complete animation.
    private final float LONG_CLICK_ROTATE_VELOCITY = CIRCUMFERENCE / 500f;

    public ClockSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addHand(ClockHand hand) {
        mHand = hand;
        initHand();
    }

    private void initHand() {
        mHand.setPivotX(NokiaClockView.mLayoutSize.width / ClockHand.mHandRatio / 2);
        ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams) mHand.getLayoutParams();
        ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
        mHand.setPivotY(mPivotYOnSurface - (lp1.topMargin - lp2.topMargin));
    }

    public void init(FrameLayout.LayoutParams params) {
        mPivotXOnSurface = params.width / 2;
        mPivotYOnSurface = params.height / 2;

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = event.getX();
                mTouchY = event.getY();

                // One tap(finger touches down and leaves up quickly) will not rotate the hand,
                // only finger movement on clock surface and long click can.
                int action = MotionEventCompat.getActionMasked(event);
                if (action != MotionEvent.ACTION_MOVE)
                    return false;

                if (count++ % SAMPLE_PERIOD == 0) {
                    double touchAngle = getTouchPointAngle(mTouchX, mTouchY);
                    double rotation = mHand.getRotation();
                    long duration = getDuration(touchAngle, rotation, ContactType.TOUCH);
                    launchAnim((float) rotation, duration, (float) touchAngle);
                }

                return false;
            }
        });

        this.setLongClickable(true);
        this.setClickable(true);
        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                double touchAngle = getTouchPointAngle(mTouchX, mTouchY);
                double rotation = mHand.getRotation();
                long duration = getDuration(touchAngle, rotation, ContactType.LONG_CLICK);
                launchAnim((float) rotation, duration, (float) touchAngle);
                return true;
            }
        });
        // disable vibration
        this.setHapticFeedbackEnabled(false);
    }

    private long getDuration(double value1, double value2, ContactType type) {
        float vel;
        if (type == ContactType.TOUCH)
            vel = STEP_ROTATE_VELOCITY;
        else
            vel = LONG_CLICK_ROTATE_VELOCITY;

        return (long) (Math.abs(value1 - value2) / vel);
    }

    private enum ContactType {TOUCH, LONG_CLICK}

    private double getTouchPointAngle(double x, double y) {
        double horizontal = x - mPivotXOnSurface;
        double vertical = y - mPivotYOnSurface;

        double rightAngleSide = Math.sqrt(horizontal * horizontal + vertical * vertical);
        double angle = Math.acos(-vertical / rightAngleSide) * CIRCUMFERENCE / 2 / Math.PI;
        if (horizontal < -FLOAT_MIN) {
            angle = CIRCUMFERENCE - angle;
        }
        return angle;
    }

    private void launchAnim(float angleStart, long duration, float angleEnd) {
        mHand.launchRotation(angleStart, angleEnd, duration);
    }
}