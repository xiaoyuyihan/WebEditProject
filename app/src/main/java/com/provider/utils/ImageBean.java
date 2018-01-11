package com.provider.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by lw on 2017/11/30.
 */

public class ImageBean implements Parcelable {
    private int ID;
    private String ParentName;
    private String name;
    private String parentPath;
    private String path;

    public ImageBean(int paramInt, String paramString1, String paramString2) {
        this.ID = paramInt;
        this.path = paramString1;
        this.name = paramString2;
        this.parentPath = new File(paramString1).getParent();
        this.ParentName = getParentName(this.parentPath);
    }

    protected ImageBean(Parcel in) {
        ID = in.readInt();
        ParentName = in.readString();
        name = in.readString();
        parentPath = in.readString();
        path = in.readString();
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(ParentName);
        parcel.writeString(name);
        parcel.writeString(parentPath);
        parcel.writeString(path);
    }

    public static String getParentName(String paramString) {
        String[] arrayOfString = paramString.split("/");
        return arrayOfString[(-1 + arrayOfString.length)];
    }
}
