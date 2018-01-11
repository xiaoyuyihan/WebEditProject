package com.provider.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by lw on 2017/11/30.
 */

public class AudioBean implements Parcelable {
    private int ID;
    private String ParentName;
    private String duration;
    private String name;
    private String parentPath;
    private String path;

    public AudioBean(int paramInt, String paramString1, String paramString2, String paramString3) {
        this.ID = paramInt;
        this.path = paramString1;
        this.name = paramString2;
        this.duration = paramString3;
        this.parentPath = new File(paramString1).getParent();
        this.ParentName = ImageBean.getParentName(this.parentPath);
    }

    protected AudioBean(Parcel in) {
        ID = in.readInt();
        ParentName = in.readString();
        duration = in.readString();
        name = in.readString();
        parentPath = in.readString();
        path = in.readString();
    }

    public static final Creator<AudioBean> CREATOR = new Creator<AudioBean>() {
        @Override
        public AudioBean createFromParcel(Parcel in) {
            return new AudioBean(in);
        }

        @Override
        public AudioBean[] newArray(int size) {
            return new AudioBean[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(ParentName);
        parcel.writeString(duration);
        parcel.writeString(name);
        parcel.writeString(parentPath);
        parcel.writeString(path);
    }
}
