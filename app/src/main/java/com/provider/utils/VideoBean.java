package com.provider.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by lw on 2017/11/30.
 */

public class VideoBean implements Parcelable {
    private int ID;
    private String ParentName;
    private String duration;
    private String name;
    private String parentPath;
    private String path;

    public VideoBean(int paramInt, String paramString1, String paramString2, String paramString3) {
        this.ID = paramInt;
        this.path = paramString1;
        this.name = paramString2;
        this.duration = paramString3;
        this.parentPath = new File(paramString1).getParent();
        this.ParentName = ImageBean.getParentName(this.parentPath);
        getThumbnail();
    }

    protected VideoBean(Parcel in) {
        ID = in.readInt();
        ParentName = in.readString();
        duration = in.readString();
        name = in.readString();
        parentPath = in.readString();
        path = in.readString();
        videoBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    private void getThumbnail() {
        MediaMetadataRetriever localMediaMetadataRetriever = new MediaMetadataRetriever();

        try {
            localMediaMetadataRetriever.setDataSource(path);
            videoBitmap = localMediaMetadataRetriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            videoBitmap = null;
        } finally {
            localMediaMetadataRetriever.release();
        }
    }


    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }

    public int getID() {

        return ID;
    }

    public String getParentName() {
        return ParentName;
    }

    public String getDuration() {
        return duration;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getVideoBitmap() {
        return videoBitmap;
    }

    public String getName() {
        return name;
    }

    private Bitmap videoBitmap;

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
        parcel.writeParcelable(videoBitmap, i);
    }
}
