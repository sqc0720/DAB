package com.datong.radiodab;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

//表
@Entity
public class DabStation implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int index;
    private final String name;
    private int favorite;
    private final int sec;
    private final long service_id;
    private final long frequency;
    //version 2 add item
    private final int pty;


    protected DabStation(Parcel in) {
        index = in.readInt();
        name = in.readString();
        favorite = in.readInt();
        sec = in.readInt();
        service_id= in.readLong();
        frequency= in.readLong();
        pty= in.readInt();
    }

    public DabStation(String name, int favorite, int sec,long service_id,long frequency,int pty) {
        this.name = name;
        this.favorite = favorite;
        this.sec= sec;
        this.service_id= service_id;
        this.frequency= frequency;
        this.pty=  pty;
    }

    public String getName() {
        return name;
    }

    public int getFavorite() {
        return favorite;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int id){
        this.index= id;
    }

    public int getSec() {
        return sec;
    }

    public long getService_id() {
        return service_id;
    }
    public long getFrequency() {
        return frequency;
    }

    public int getPty() {
        return pty;
    }

    public void setFavorite(int f){
        favorite= f;
    }


    @NonNull
    public String toString() {
        return "DabStation{" +
                "index=" + index +'\'' +
                ", name='" + name +'\'' +
                ", favorite=" + favorite +'\'' +
                ", sec='" + sec + '\'' +
                ", service id=" + service_id +'\'' +
                ", frequency =" + frequency +'\'' +
                ", pty =" + pty +'\'' +
                '}';
    }

   //反序列化
    public static final Creator<DabStation> CREATOR = new Creator<DabStation>() {
        @Override
        public DabStation createFromParcel(Parcel in) {
            return new DabStation(in);
        }

        @Override
        public DabStation[] newArray(int size) {
            return new DabStation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeInt(favorite);
        dest.writeInt(sec);
        dest.writeLong(service_id);
        dest.writeLong(frequency);
        dest.writeInt(pty);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
