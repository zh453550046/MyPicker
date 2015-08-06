package zhouwn.bm.com.mynubmberpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zhouwn on 2015/7/25.
 */
public class MyPicker extends ViewGroup {

    private List<String> list;
    private int index;
    private float downX, downY, width, height;
    private int childHeight, topHeight, bottomHeight, distance;
    private int textColor = -1;
    private VelocityTracker velocityTracker;
    private OnScrollListenner onScrollListenner;

    public MyPicker(Context context) {
        super(context);
        init();
    }

    public MyPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = getSuggestedMinimumWidth();
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = getSuggestedMinimumHeight();
        }
        setMeasuredDimension(widthSize, heightSize);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        childHeight = (int) (height / 3);
        if ((list.size() - 3) % 2 != 0) {
            bottomHeight = ((list.size() - 3) / 2 + 1) * childHeight;
        } else {
            bottomHeight = ((list.size() - 3) / 2) * childHeight;
        }
        topHeight = (list.size() - 3) / 2 * childHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            TextView view = (TextView) getChildAt(i);
            int left = 0,
                    top = i * childHeight + distance,
                    right = getMeasuredWidth(),
                    bottom;
            if (top > getMeasuredHeight() + bottomHeight) {
                top = -topHeight + top - (getMeasuredHeight() + bottomHeight);
            }
            if (top < -topHeight) {
                top = getMeasuredHeight() + (bottomHeight) - (Math.abs(top) - topHeight);
            }
            bottom = top + childHeight;
            view.setText(list.get(i));
            if (view.getTop() >= 0.5 * childHeight && view.getTop() <= 1.5 * childHeight) {
                index = i;
                if (textColor != -1) {
                    view.setTextColor(textColor);
                }
                int scale = (view.getTop() - childHeight) / (childHeight / 2);
                view.setAlpha(1.3f - scale);
            } else {
                view.setTextColor(getResources().getColor(android.R.color.darker_gray));
                view.setAlpha(0.3f);
            }
            view.layout(left, top, right, bottom);
        }
    }

    private void init() {
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        velocityTracker.addMovement(ev);
        velocityTracker.computeCurrentVelocity(100);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getRawX(), y = ev.getRawY();
                distance += (int) (y - downY);
                distance = distance % (list.size() * childHeight);
                downY = y;
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(velocityTracker.getYVelocity()) > 200) {
                    fastScroll(velocityTracker.getYVelocity());
                } else {
                    returnScroll();
                }
                break;
        }
        return true;
    }

    private void fastScroll(float vt) {
        final ValueAnimator animator = ValueAnimator.ofFloat(vt, 0);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                distance += (float) animation.getAnimatedValue() / 10;
                distance = distance % (list.size() * childHeight);
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                returnScroll();
            }
        });
        animator.start();
    }

    public void returnScroll() {
        int value = Math.abs(getChildAt(0).getTop()) % childHeight;
        if (value != 0) {
            if (getChildAt(0).getTop() > 0) {
                if (value < 0.5 * childHeight) {
                    value = distance - value;
                } else {
                    value = distance + childHeight - value;
                }
            } else {
                if (value < 0.5 * childHeight) {
                    value = distance + value;
                } else {
                    value = distance - childHeight + value;
                }
            }
            ValueAnimator animator1 = ValueAnimator.ofFloat(distance, value);
            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    distance = (int) ((float) animation.getAnimatedValue());
                    requestLayout();
                }
            });
            animator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildAt(i).getTop() < 0.9 * childHeight && getChildAt(i).getTop() > 1.1) {
                            index = i;
                            break;
                        }
                    }
                    if (onScrollListenner != null) {
                        onScrollListenner.onScrollFinish();
                    }
                }
            });
            animator1.setDuration(200);
            animator1.start();
        }
    }

    public void setResouce(Context context, List<String> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            TextView v = (TextView) LayoutInflater.from(context).inflate(R.layout.item, null);
            addView(v);
        }
    }

    public String getCurrentResource() {
        return list.get(index);
    }

    public int getCurrentItem() {
        return index;
    }

    public void setTextCorlor(int textColor) {
        this.textColor = textColor;
    }

    public void setOnScrollListenner(OnScrollListenner onScrollListenner) {
        this.onScrollListenner = onScrollListenner;
    }

    public interface OnScrollListenner {
        void onScrollFinish();
    }
}
