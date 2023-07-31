package com.datong.radiodab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DabTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DabTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean mAfterScan;

    //Aero
    private static final String TAG = "DAB.Tab";
    private DabStationDBEngine mEngine;

    public DabTabFragment() {
        // Required empty public constructor
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DabTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DabTabFragment newInstance(String param1, String param2) {
        DabTabFragment fragment = new DabTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        DabStationDBEngine mEngine= DabStationDBEngine.newInstance(getContext());
        setAfterScan(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dab_tab, container, false);
        if(DabSharePreference.getDabFirst(getContext())==0) {  //default value = 1
            showDabFragment();
        }else{
            DabSharePreference.setDabFirst(getContext(),0);
            showDabFragmentScan();
        }
        return view;
    }

    public void showDabFragment(){
        getChildFragmentManager().beginTransaction()
                .add(R.id.dab_tab_fragment_container, DabFragment.newInstance(getContext(),this))   // 此处的R.id.fragment_container是要盛放fragment的父容器
                //.addToBackStack(null)  TestFragment
                .commit();
    }

    public void showDabFragmentScan(){
        getChildFragmentManager().beginTransaction()
                .add(R.id.dab_tab_fragment_container, DabFragmentScan.newInstance(getContext(),this,true))   // 此处的R.id.fragment_container是要盛放fragment的父容器
                //.addToBackStack(null)  TestFragment
                .commit();
    }

    public void replaceDabFragmentScan() {
        if (isAdded()) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.dab_tab_fragment_container, DabFragmentScan.newInstance(getContext(), this)
                            , null)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void replaceDabFragment() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dab_tab_fragment_container, DabFragment.newInstance(getContext(),this)
                        , null)
                .commit();
    }

    public void popDabFragmentScan() {
        getChildFragmentManager().popBackStack();
        ////getActivity().onBackPressed();
    }
    //deal with database  not used
    private void IServiceStationDbEngine(){
        Log.i(TAG,"Tab -> openDabStationDBEngine " );
        DabStationDBEngine engine= DabStationDBEngine.newInstance(getContext());
        //addItem(engine);
        //    engine.deleteAllDabStations();
        engine.queryFavoriteDabStations();
       // engine.updateDabStations(new DabStation(26,"BBC0  sials R1 silas",1,1));
        //engine.updateDabStations();
        engine.queryFavoriteDabStations();
        engine.queryAllDabStations();
    }

    public void setAfterScan(boolean flag){
        mAfterScan= flag;
    }

    public boolean getAfterScan(){
        return mAfterScan;
    }
}