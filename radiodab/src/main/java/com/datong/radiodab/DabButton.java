package com.datong.radiodab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class DabButton extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "DABButton";
    private String mExampleString; // TODO: use a default from R.string...
    private Drawable mExampleDrawable;
    private LinearLayout layout;
    private ImageButton icon;
    private TextView text;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private OnDabButtonClickListener onClickListener;

    public DabButton(Context context) {
        super(context);

    }

    public DabButton(Context context, AttributeSet attrs) {
        this(context,attrs,0);

    }

    public DabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);

    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.dab_button,this);
        layout= findViewById(R.id.dabbutton_layout);
        icon= findViewById(R.id.dabbutton_icon);
        text= findViewById(R.id.dabbutton_text);
        icon.setOnClickListener(this);
        text.setOnClickListener(this);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DabButton, defStyle, 0);
        mExampleString = a.getString(
                R.styleable.DabButton_exampleString);
        text.setText(mExampleString);
        if (a.hasValue(R.styleable.DabButton_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.DabButton_exampleDrawable);
            icon.setImageDrawable(mExampleDrawable);//mExampleDrawable.setCallback(this);
        }
        a.recycle();
    }

    public void redraw(int resourceId){
        layout.setBackgroundResource(R.drawable.dabbutton_frame);
        layout.setBackground(getResources().getDrawable(R.drawable.dabbutton_frame));
        icon.setImageResource(resourceId);
        text.setTextColor(getResources().getColor(R.color.dab_button_text));
    }


    /**
     * click function
     */
    public interface OnDabButtonClickListener{
        void onClick(View v);
    }

    public void setOnDabButtonClickListener(OnDabButtonClickListener listener){
        onClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        int i=v.getId();

        if(i== R.id.dabbutton_icon) {
            Log.i(TAG,"<========  dabbutton_icon  =========>");
        }else if(i== R.id.dabbutton_text) {
            Log.i(TAG,"<========  dabbutton_text  =========>");
        }else{
            Log.i(TAG,"<========  dabbutton_other  =========>");
        }
        onClickListener.onClick(v);
    }

    public CharSequence getTitle(){
        return text.getText();
    }

    public void setTitle(CharSequence msg) {
        text.setText(msg);
    }
}