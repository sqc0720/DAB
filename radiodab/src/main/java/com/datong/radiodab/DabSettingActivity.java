package com.datong.radiodab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class DabSettingActivity extends AppCompatActivity {

    private static final String TAG = "Setting Activity";
    private ConstraintLayout mLayout;
    private TextView mTextView;
    private ImageButton mImagebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dab_setting);
        mLayout=findViewById(R.id.dab_setting_layout);
        mTextView=findViewById(R.id.dab_setting_title);
        mImagebutton=findViewById(R.id.imagebutton_setting_back);
        mImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResourceForLightDarkMode();
    }

    private void updateResourceForLightDarkMode() {
        Log.d(TAG, "ActivitySetting ========== updateResourceForLightDarkMode()");
        //getWindow().getDecorView().setBackgroundResource(R.drawable.dab_setting_background);
        mLayout.setBackgroundResource(R.drawable.dab_setting_background);
        mImagebutton.setImageResource(R.drawable.dab_setting_bar_back);
        mTextView.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
    }
}