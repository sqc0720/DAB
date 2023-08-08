package com.datong.radiodab.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.datong.radiodab.R;


public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    public interface OnMaxusSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }

    private OnMaxusSeekBarChangeListener maxusSeekBarChangeListener = null;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaxusSeekBarChangeListener(OnMaxusSeekBarChangeListener listener) {
        maxusSeekBarChangeListener = listener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    public void uiModeChange() {
        setProgressDrawable(getContext().getDrawable(R.drawable.seekbar_bg));
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i = 0;
                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(i);
                Log.e("Progress", getProgress() + "");
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (maxusSeekBarChangeListener != null) {
                        maxusSeekBarChangeListener.onStopTrackingTouch(this);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (maxusSeekBarChangeListener != null) {
                        maxusSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (maxusSeekBarChangeListener != null) {
                        maxusSeekBarChangeListener.onProgressChanged(this, getProgress(), true);
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}