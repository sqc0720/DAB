package com.datong.radiodab;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public class CategoryDialog extends DialogFragment {

    private Fragment  mFragment;
    private static CharSequence mFocus = "All";
    private static final String TAG = "DAB.CategoryDialog";
    private String[] mCategorys = {"All", "News", "Talk", "Sports", "Popular", "Classic", "No PTY"};

    private TextView mTitle;
    private Button mClose;
    private RadioGroup mGroup;
    private RadioButton mAll;
    private RadioButton mNews;
    private RadioButton mTalk;
    private RadioButton mSports;
    private RadioButton mPopular;
    private RadioButton mClassic;
    private RadioButton mNoPTY;

    // Use this instance of the interface to deliver action events
    public interface CategoryDialogListener {
        public void onDialogCategoryClick(CategoryDialog dialog);
    }
    CategoryDialogListener listener;
/*
    public static CategoryDialog newInstance(String param1) {
        if(mFragment== null) {
            mFragment = new DabFragment();
            Bundle args = new Bundle();
            args.putString(TAG, param1);
            mFragment.setArguments(args);
        }
        return mFragment;
    }*/

    public CategoryDialog(Fragment f, CharSequence name) {
        mFocus = name.toString();
        mFragment= f;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            Log.i(TAG, "onAttach silas:"+context.toString());
            listener = (CategoryDialogListener) mFragment;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(TAG + " must implement CategoryDialogListener");
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResourceForLightDarkMode();
    }

    private void updateResourceForLightDarkMode(){
        dismiss();
        EventBus.getDefault().post(4);
        /* background not work
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dab_category_background);
        window.setBackgroundDrawable
                (getResources().getDrawable(R.drawable.dab_category_background));
             mTitle.setTextColor(getResources().getColor(R.color.dab_category_text));
        mClose.setTextColor(getResources().getColor(R.color.dab_category_text));
        mAll.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mAll.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mNews.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mNews.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mTalk.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mTalk.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mSports.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mSports.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mPopular.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mPopular.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mClassic.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mClassic.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
        mNoPTY.setBackground(getResources().getDrawable(R.drawable.dab_category_shape));
        mNoPTY.setTextColor(getResources().getColor(R.color.dab_category_type_text_unchecked));
*/
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dab_category_dialog, null);
        mTitle= view.findViewById(R.id.dab_textview_category_title);
        mClose = view.findViewById(R.id.dab_imagebutton_category_close);
        mGroup = view.findViewById(R.id.dab_categorydialog_group);
        mAll = view.findViewById(R.id.dab_categorydialog_all);
        mNews = view.findViewById(R.id.dab_categorydialog_news);
        mTalk = view.findViewById(R.id.dab_categorydialog_talk);
        mSports = view.findViewById(R.id.dab_categorydialog_sports);
        mPopular = view.findViewById(R.id.dab_categorydialog_popular);
        mClassic = view.findViewById(R.id.dab_categorydialog_classic);
        mNoPTY = view.findViewById(R.id.dab_categorydialog_nopty);

        switch (mFocus.toString()) {
            case "All":
                mAll.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mAll.setTextColor(0xFFFFFFFF);
                break;
            case "News":
                mNews.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mNews.setTextColor(0xFFFFFFFF);
                break;
            case "Talk":
                mTalk.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mTalk.setTextColor(0xFFFFFFFF);
                break;
            case "Sports":
                mSports.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mSports.setTextColor(0xFFFFFFFF);
                break;
            case "Popular":
                mPopular.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mPopular.setTextColor(0xFFFFFFFF);
                break;
            case "Classic":
                mClassic.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mClassic.setTextColor(0xFFFFFFFF);
                break;
            case "No PTY":
                mNoPTY.setBackgroundResource(R.drawable.dab_category_shape_foucs);
                mNoPTY.setTextColor(0xFFFFFFFF);
                break;
            default:
                break;
        }
        ;
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"<========   CategoryDialog::dismiss ");
                dismiss();
            }
        });
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mAll.isChecked()) {
                    mFocus = "All";
                    setDabCategory(0);
                } else if (mNews.isChecked()) {
                    mFocus = "News";
                    setDabCategory(1);
                } else if (mTalk.isChecked()) {
                    mFocus = "Talk";
                    setDabCategory(2);
                } else if (mSports.isChecked()) {
                    mFocus = "Sports";
                    setDabCategory(3);
                } else if (mPopular.isChecked()) {
                    mFocus = "Popular";
                    setDabCategory(4);
                } else if (mClassic.isChecked()) {
                    mFocus = "Classic";
                    setDabCategory(5);
                } else if (mNoPTY.isChecked()) {
                    mFocus = "No PTY";
                    setDabCategory(6);
                } else {
                    ;
                }
                EventBus.getDefault().post(3);
                listener.onDialogCategoryClick(CategoryDialog.this);
                Log.i(TAG,"<========   CategoryDialog::dismiss ");
                dismiss();
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = 1110;
        params.height = 491;
        win.setAttributes(params);
    }

    public  CharSequence getCategory() {
        return mFocus;
    }

    private void getDabCategory(){
        int index= DabSharePreference.getDabCategory(this.getContext());
        switch(index){
            case 0:
                mFocus= "All";
                break;
            case 1:
                mFocus= "News";
                break;
            case 2:
                mFocus= "Talk";
                break;
            case 3:
                mFocus= "Sports";
                break;
            case 4:
                mFocus= "Popular";
                break;
            case 5:
                mFocus= "Classic";
                break;
            case 6:
                mFocus= "No PTY";
                break;
            default:
                mFocus= "All";
        }
    }

    private void setDabCategory(int index){
        DabSharePreference.setDabCategory(this.getContext(),index);
    }
}