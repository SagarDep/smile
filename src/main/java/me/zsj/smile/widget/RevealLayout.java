package me.zsj.smile.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import me.zsj.smile.R;


/**
 * Created by zsj on 2015/10/24 0024.
 */
public class RevealLayout extends LinearLayout {

    private Paint mRevealPaint;
    private ObjectAnimator mRevealAnimator;
    private OnRevealFinishListener onRevealFinishListener;

    private boolean mIsReveal;
    private float maxRadius = 20;
    private float radius;


    public RevealLayout(Context context) {
        this(context, null);
    }

    public RevealLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRevealPaint = new Paint();
        mRevealPaint.setAntiAlias(true);
        mRevealPaint.setColor(getResources().getColor(R.color.reveal_color));

        setWillNotDraw(false);

        setGravity(Gravity.BOTTOM);
    }

    public void startReveal() {
        setAnimator(mIsReveal);
        mRevealAnimator.setInterpolator(new DecelerateInterpolator());
        mRevealAnimator.setDuration(450);
        mRevealAnimator.start();
        mRevealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onRevealFinishListener != null) {
                    onRevealFinishListener.onRevealFinish(mIsReveal);
                    if (mIsReveal) {
                        mIsReveal = false;
                    }else {
                        mIsReveal = true;
                    }
                }
            }
        });
    }

    private void setAnimator(boolean isReveal) {
        if (isReveal) {
            mRevealAnimator = ObjectAnimator.ofFloat(this, "radius", maxRadius, 0);
        }else {
            mRevealAnimator = ObjectAnimator.ofFloat(this, "radius", 5, maxRadius);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxRadius = w + pxTodp(20);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() - pxTodp(50), getHeight() - pxTodp(50), radius, mRevealPaint);

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    public void setOnRevealFinishListener(OnRevealFinishListener onRevealFinishListener) {
        this.onRevealFinishListener = onRevealFinishListener;
    }

    private int pxTodp(int dp) {
        float size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, getContext().getResources().getDisplayMetrics());
        return (int) size;
    }

    public interface OnRevealFinishListener {

        void onRevealFinish(boolean isReveal);
    }
}
