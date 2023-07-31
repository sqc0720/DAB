package com.datong.radiodab;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DadSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DadSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "DAB_SETTING";
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private FrameLayout mLayoutBand;
    private TextView mTextViewBand;
    private TabLayout mTabLayout;
    private TabItem mTabAll;
    private TabItem mTab3;
    private TabItem mTabL;
    private FrameLayout mLayoutAnnounment;
    private FrameLayout mLayoutDls;
    private TextView mTextViewAnnounment;
    private TextView mTextViewAlarm;
    private TextView mTextViewNews;
    private TextView mTextViewSport;
    private TextView mTextViewRoad;
    private TextView mTextViewArea;
    private TextView mTextViewFinancial;
    private TextView mTextViewTransport;
    private TextView mTextViewEvent;
    private TextView mTextViewProgram;
    private TextView mTextViewWarning;
    private TextView mTextViewDLS0;
    private TextView mTextViewDLS;


    private Switch mSwitchAlarm;
    private Switch mSwitchNews;
    private Switch mSwitchSport;
    private Switch mSwitchRoad;
    private Switch mSwitchArea;
    private Switch mSwitchFinancial;
    private Switch mSwitchTransport;
    private Switch mSwitchEvent;
    private Switch mSwitchProgram;
    private Switch mSwitchWarning;
    private Switch mDLS;

    public DadSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DadSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DadSettingFragment newInstance(String param1) {
        DadSettingFragment fragment = new DadSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dab_setting, container, false);
        mLayoutBand= view.findViewById(R.id.dab_setting_layout_band);
        mTextViewBand= view.findViewById(R.id.dab_setting_textview_band);

        mTabLayout= (TabLayout)view.findViewById(R.id.dab_setting_tablayout);
        mTabLayout.selectTab(mTabLayout.getTabAt(DabSharePreference.getDabBand(getContext())));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        DabSharePreference.setDabBand(getContext(),0);
                        break;
                    case 1:
                        DabSharePreference.setDabBand(getContext(),1);
                        break;
                    case 2:
                        DabSharePreference.setDabBand(getContext(),2);
                        break;
                    default:
                        DabSharePreference.setDabBand(getContext(),0);
                }
                EventBus.getDefault().post(3);
                Log.i(TAG, "onTabSelected:: "+tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mLayoutAnnounment= view.findViewById(R.id.dab_setting_layout_announment);
        mLayoutDls= view.findViewById(R.id.dab_setting_layout_dls);
        mTextViewAnnounment= view.findViewById(R.id.dab_setting_textview_announment);
        mTextViewAlarm= view.findViewById(R.id.dab_setting_textview_alarm);
        mTextViewNews= view.findViewById(R.id.dab_setting_textview_newsflash);
        mTextViewSport= view.findViewById(R.id.dab_setting_textview_sportreport);
        mTextViewRoad= view.findViewById(R.id.dab_setting_textview_roadtrafficflash);
        mTextViewArea= view.findViewById(R.id.dab_setting_textview_areaweatherflash);
        mTextViewFinancial= view.findViewById(R.id.dab_setting_textview_financialreport);
        mTextViewTransport= view.findViewById(R.id.dab_setting_textview_transportflash);
        mTextViewEvent= view.findViewById(R.id.dab_setting_textview_event_announcemen);
        mTextViewProgram= view.findViewById(R.id.dab_setting_textview_programinfo);
        mTextViewWarning= view.findViewById(R.id.dab_setting_textview_warningservice);
        mTextViewDLS0= view.findViewById(R.id.dab_setting_dls);
        mTextViewDLS= view.findViewById(R.id.dab_setting_textview_dls);



        mSwitchAlarm= (Switch) view.findViewById(R.id.dab_setting_switch_alarm);
        mSwitchAlarm.setClickable(false);
        mSwitchAlarm.setChecked(DabSharePreference.getDabAnnounmentAlarm(getContext()));
        Log.i(TAG , "alarm get="+ DabSharePreference.getDabAnnounmentAlarm(getContext()) );
        mSwitchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentAlarm(getContext(),true);
                Log.i(TAG , "alarm set ="+ DabSharePreference.getDabAnnounmentAlarm(getContext()) );
            }
        });

        mSwitchNews= (Switch) view.findViewById(R.id.dab_setting_switch_newsflash);
        mSwitchNews.setChecked(DabSharePreference.getDabAnnounmentNewFlash(getContext()));
        Log.i(TAG , "alarm get="+ DabSharePreference.getDabAnnounmentAlarm(getContext()) );
        mSwitchNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentNewFlash(getContext(),isChecked);
            }
        });

        mSwitchSport= (Switch) view.findViewById(R.id.dab_setting_switch_sportreport);
        mSwitchSport.setChecked(DabSharePreference.getDabAnnounmentSport(getContext()));
        mSwitchSport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentSport(getContext(),isChecked);
            }
        });

        mSwitchRoad=  (Switch) view.findViewById(R.id.dab_setting_switch_roadtrafficflash);
        mSwitchRoad.setChecked(DabSharePreference.getDabAnnounmentRoadTrafficFlash(getContext()));
        mSwitchRoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentRoadTrafficFlash(getContext(),isChecked);
            }
        });

        mSwitchArea= (Switch) view.findViewById(R.id.dab_setting_switch_areaweatherflash);
        mSwitchArea.setChecked(DabSharePreference.getDabAnnounmentAreaWeatherFlash(getContext()));
        mSwitchArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentAreaWeatherFlash(getContext(),isChecked);
            }
        });

        mSwitchFinancial= (Switch) view.findViewById(R.id.dab_setting_switch_financialreport);
        mSwitchFinancial.setChecked(DabSharePreference.getDabAnnounmentFinancialReport(getContext()));
        mSwitchFinancial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentFinancialReport(getContext(),isChecked);
            }
        });

        mSwitchTransport= (Switch) view.findViewById(R.id.dab_setting_switch_transportflash);
        mSwitchTransport.setChecked(DabSharePreference.getDabAnnounmentTransportFlash(getContext()));
        mSwitchTransport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentTransportFlash(getContext(),isChecked);
            }
        });

        mSwitchEvent= (Switch) view.findViewById(R.id.dab_setting_switch_event_announcemen);
        mSwitchEvent.setChecked(DabSharePreference.getDabAnnounmentEvent(getContext()));
        mSwitchEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentEvent(getContext(),isChecked);
            }
        });

        mSwitchProgram= (Switch) view.findViewById(R.id.dab_setting_switch_programinfo);
        mSwitchProgram.setChecked(DabSharePreference.getDabAnnounmentProgramInformation(getContext()));
        mSwitchProgram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentProgramInformation(getContext(),isChecked);
            }
        });

        mSwitchWarning= (Switch) view.findViewById(R.id.dab_setting_switch_warningservice);
        mSwitchWarning.setChecked(DabSharePreference.getDabAnnounmentWarningService(getContext()));
        mSwitchWarning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabAnnounmentWarningService(getContext(),isChecked);
            }
        });

        mDLS= (Switch) view.findViewById(R.id.dab_setting_switch_dls);
        mDLS.setChecked(DabSharePreference.getDabDLS(getContext()));
        mDLS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DabSharePreference.setDabDLS(getContext(),isChecked);
            }
        });

        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResourceForLightDarkMode();

    }

    private void updateResourceForLightDarkMode() {
        Log.d(TAG, "SettingFragment ========== updateResourceForLightDarkMode()");
        mLayoutBand.setBackgroundResource(R.color.dab_setting_segment_bg);
        mTextViewBand.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTabLayout.setBackgroundResource(R.drawable.dab_setting_tab_bg);
        mTabLayout.setSelectedTabIndicator(R.drawable.dab_setting_tab_indicator);
       //mTabLayout.setTabTextColors(getResources().getColorStateList(R.color.dab_setting_tab_text_color,null));
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.dab_Setting_band_tabIndicatorColor));

        mLayoutAnnounment.setBackgroundResource(R.color.dab_setting_segment_bg);
        mLayoutDls.setBackgroundResource(R.color.dab_setting_segment_bg);
        mTextViewAnnounment.setTextColor(getResources().getColor(R.color.dab_setting_text_main));

        mTextViewAlarm.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewNews.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewSport.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewRoad.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewArea.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewFinancial.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewTransport.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewEvent.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewProgram.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewWarning.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewDLS0.setTextColor(getResources().getColor(R.color.dab_setting_text_main));
        mTextViewDLS.setTextColor(getResources().getColor(R.color.dab_setting_text_main));

    }

}