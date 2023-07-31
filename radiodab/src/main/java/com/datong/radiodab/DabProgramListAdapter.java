package com.datong.radiodab;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DabProgramListAdapter extends RecyclerView.Adapter<DabProgramListAdapter.ViewHolder>  {
    private static final String TAG = "DabProgramListAdapter";
    private static final int FAVORITE_MAX_ITEM = 18;
    private DabStationContent mDabStationContent;
    private List<DabStation> mProgramList;
    private List<DabStation> mProgramList2; //
    private DabStationFavoriteDBEngine mEngine;  //favorite list add or delete
    private int mSelectedIndex;        //记录当前选中的条目索引
    private boolean mFavorite;          //favorite list  or all list


    public class ViewHolder extends RecyclerView.ViewHolder {
        //实现点击事件
        View mProgramItem;
        TextView name;
        ImageView favorite;
        TextView sec;


        public ViewHolder(@NonNull View view) {
            super(view);
            mProgramItem = view;
            name=  (TextView) itemView.findViewById(R.id.dab_textview_name_item);
            favorite = (ImageView) itemView.findViewById(R.id.dab_imagebutton_favorite_item);
            sec = (TextView) itemView.findViewById(R.id.dab_textview_sec_item);
        }
    }

    public DabProgramListAdapter(List<DabStation> programlist, int focus, boolean favorite) {
        mProgramList = programlist;
        mSelectedIndex= focus;
        mFavorite= favorite;

    }

    public DabProgramListAdapter(List<DabStation> programlist, int focus, boolean favorite,
                                   List<DabStation> programlist2,DabStationFavoriteDBEngine engine) {
        mProgramList = programlist;
        mSelectedIndex= focus;
        mFavorite= favorite;
        mProgramList2=programlist2;
        mEngine= engine;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.dab_programitem_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.mProgramItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedIndex=  holder.getAdapterPosition();
                EventBus.getDefault().post(0);
                Log.i(TAG,"eventbus post ===>> view 0");
                notifyDataSetChanged();

                //Toast.makeText(parent.getContext(), "you clicked {item =}"+pos,Toast.LENGTH_SHORT).show();
            }
        });
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                DabStation item = mProgramList.get(pos);
                DabStation item2 =null;
                if(!mFavorite) {  // all list
                    if(item.getFavorite()==0) { //add favorite station
                        if( (mProgramList2!=null)&&
                                (mProgramList2.size()>=DabStationContent.FAVORITE_MAX_ITEM)) {
                            EventBus.getDefault().post(2);
                            return;
                        }else {
                            item.setFavorite(1);
                        }
                    }else
                        item.setFavorite(0);
                    mProgramList.set(pos, item);
                    if (item.getFavorite()==1) {
                        holder.favorite.setImageResource(R.drawable.dab_icon_favorites);
                    } else {
                        holder.favorite.setImageResource(R.drawable.dab_icon_none_favorites_focus);
                    }
                    EventBus.getDefault().post(1);
                    if( (mProgramList2==null)||(mEngine==null) ){
                        return ;
                    }
                    if(item.getFavorite()==0) {  //favorite==>none
                        for (int i = 0; i < mProgramList2.size(); i++) {
                            item2 = mProgramList2.get(i);
                            if (item2.getName().equals(item.getName())
                                    && (item2.getFrequency() == item.getFrequency())
                                    && (item2.getService_id() == item.getService_id())
                                    && (item2.getSec() == item.getSec())) {
                                mEngine.deleteDabStations(item2);
                                mProgramList2.remove(item2);
                                break;
                            }
                        }
                    }else { //none-->favorite
                        for (int i = 0; i < mProgramList2.size(); i++) {
                            item2 = mProgramList2.get(i);
                            if (item2.getName().equals(item.getName())
                                    && (item2.getFrequency() == item.getFrequency())
                                    && (item2.getService_id() == item.getService_id())
                                    && (item2.getSec() == item.getSec())) {
                                return;
                            }
                        }
                        mProgramList2.add(item);
                        mEngine.addDabStations(item);
                    }
                }else{  // favorite list
                    if(mSelectedIndex==pos) {  //focus
                       if(mSelectedIndex>0)
                           mSelectedIndex= mSelectedIndex-1;
                       else {
                           if(mProgramList.size()==0)
                                mSelectedIndex = -1;
                       }
                    }else if(mSelectedIndex>pos) {
                        mSelectedIndex = mSelectedIndex - 1;
                    }
                    for (int i = 0; i < mProgramList2.size(); i++) {  //all list= mProgramList2
                        item2 = mProgramList2.get(i);
                        if (item2.getName().equals(item.getName())
                                && (item2.getFrequency() == item.getFrequency())
                                && (item2.getService_id() == item.getService_id())
                                && (item2.getSec() == item.getSec())) {
                           item2.setFavorite(0);
                           break;
                        }
                    }
                    notifyItemRemoved(pos);
                    mProgramList.remove(pos);
                    mEngine.deleteDabStations(item);
                    notifyDataSetChanged();
                    EventBus.getDefault().post(1);
                }
               //Toast.makeText(parent.getContext(), "you clicked favorite",Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //show item
        holder.setIsRecyclable(false);
        if (position==getItemCount()-1){
            holder.itemView.setVisibility(View.INVISIBLE);//修改处1 将本就不存在的item置为INVISION 顺便处理好你本身的item点击逻辑，避免干扰
        }else {
            DabStation item = mProgramList.get(position);
            holder.name.setText(item.getName());
            if (1==item.getFavorite()) {
                holder.favorite.setImageResource(R.drawable.dab_icon_favorites);
            }
            if (0==item.getSec()) {
                holder.sec.setVisibility(View.INVISIBLE);
            }
            if (mSelectedIndex == position) {
                holder.mProgramItem.setBackgroundColor(0xFF464FBD);
                holder.name.setTextColor(0xFFFFFFFF);
                if (0==item.getFavorite()) {
                    holder.favorite.setImageResource(R.drawable.dab_icon_none_favorites_focus);
                }
                if (1==item.getSec()) {
                    holder.sec.setTextColor(Color.WHITE);
                    holder.sec.setBackgroundResource(R.color.dab_sec_solid_focus);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mProgramList.size()+1;
    }

    public int getSelectedIndex() {
        return mSelectedIndex ;
    }

    public boolean getFavorite(){
        return mFavorite;
    }

    public DabStation getSelectedItem() {
        if(mSelectedIndex<0)
            return null;
        else if(mSelectedIndex>=mProgramList.size()) {
            mSelectedIndex= mProgramList.size()-1;
            return mProgramList.get(mSelectedIndex );
        }else
            return mProgramList.get(mSelectedIndex );
    }

    public  void setmSelectedIndex(int index){
        if(index<0)
            index=0 ;
        if(index>=mProgramList.size())
            index=mProgramList.size()-1;

        if(index!=mSelectedIndex ) {
            mSelectedIndex = index;
            notifyDataSetChanged();
        }
    }

    public void AddFavorite(DabStation station){
        if(mFavorite) {  //favorite list
            if (mProgramList.contains(station))
                return;
            mProgramList.add(station);
            notifyDataSetChanged();
        }else{   //all list
            if (mProgramList.contains(station)){
                notifyDataSetChanged();
            }
        }
    }

    public void setFavoriteOperation(){
        notifyDataSetChanged();
    }
}
