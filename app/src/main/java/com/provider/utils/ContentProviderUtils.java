package com.provider.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by lw on 2017/11/30.
 */

public class ContentProviderUtils {
    public static final String FLAG_RECENT = "android.content.provider.recent";
    private static ContentProviderUtils mUtil;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_VIDEO = 3;

    private HandlerThread mHandlerThread = new HandlerThread("content_provider_util");
    private ArrayMap<String, ArrayList<ImageBean>> mImageUrls = new ArrayMap();
    private ArrayMap<String, ArrayList<VideoBean>> mVideoUrls = new ArrayMap();
    private ArrayMap<String, ArrayList<AudioBean>> mAudioUrls = new ArrayMap();

    public ContentProviderUtils setReadListener(OnReadListener mReadListener) {
        this.mReadListener = mReadListener;
        return this;
    }

    private OnReadListener mReadListener;

    private Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private Context mContent;
    private Handler mHandler;
    private Handler mMainHandler;
    private int mRecentSize = 50;

    public Context getContent() {
        return mContent;
    }

    public void setContent(Context mContent) {
        this.mContent = mContent;
    }

    public ContentProviderUtils(Context paramContext) {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());
        mContent = paramContext;
    }

    private void getAudioUrls() {
        Cursor localCursor = getContentCursor(audioUri, null, null, null, "_id DESC");
        while (localCursor.moveToNext()) {
            AudioBean localAudioBean = new AudioBean(localCursor.getInt(localCursor.getColumnIndex("_id")), localCursor.getString(localCursor.getColumnIndex("_data")), localCursor.getString(localCursor.getColumnIndex("_display_name")), localCursor.getString(localCursor.getColumnIndex("duration")));
            if (mAudioUrls.containsKey(FLAG_RECENT)&&mAudioUrls.get(FLAG_RECENT).size()<mRecentSize){
                mAudioUrls.get(FLAG_RECENT).add(localAudioBean);
            }else if (!mAudioUrls.containsKey(FLAG_RECENT)){
                mAudioUrls.put(FLAG_RECENT,new ArrayList<AudioBean>());
                mAudioUrls.get(FLAG_RECENT).add(localAudioBean);
            }
            if (mAudioUrls.containsKey(localAudioBean.getParentPath())) {
                ((ArrayList) mAudioUrls.get(localAudioBean.getParentPath())).add(localAudioBean);
            } else {
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localAudioBean);
                mAudioUrls.put(localAudioBean.getParentPath(), localArrayList);
            }
        }
        localCursor.close();
        mAudioUrls.size();
    }

    public static ContentProviderUtils getInstance(AppCompatActivity paramContext) {
        if (mUtil == null)
            mUtil = new ContentProviderUtils(paramContext);
        mUtil.setContent(paramContext);
        return mUtil;
    }

    private void getPhotoUrls() {
        Cursor localCursor = getContentCursor(imageUri, null, "mime_type=? or mime_type=?", new String[]{"image/jpeg", "image/png"}, "_id DESC");
        while (localCursor.moveToNext()) {
            ImageBean localImageBean = new ImageBean(localCursor.getInt(localCursor.getColumnIndex("_id")), localCursor.getString(localCursor.getColumnIndex("_data")), localCursor.getString(localCursor.getColumnIndex("_display_name")));
            if (mImageUrls.containsKey(FLAG_RECENT)&&mImageUrls.get(FLAG_RECENT).size()<mRecentSize){
                mImageUrls.get(FLAG_RECENT).add(localImageBean);
            }else if (!mImageUrls.containsKey(FLAG_RECENT)){
                mImageUrls.put(FLAG_RECENT,new ArrayList<ImageBean>());
                mImageUrls.get(FLAG_RECENT).add(localImageBean);
            }
            if (mImageUrls.containsKey(localImageBean.getParentPath()))
                ((ArrayList) mImageUrls.get(localImageBean.getParentPath())).add(localImageBean);
            else {
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localImageBean);
                mImageUrls.put(localImageBean.getParentPath(), localArrayList);
            }
        }
        localCursor.close();
        mImageUrls.size();
    }

    private void getVideoUrls() {
        Cursor localCursor = getContentCursor(videoUri, null, null, null, "_id DESC");
        while (localCursor.moveToNext()) {
            VideoBean localVideoBean = new VideoBean(localCursor.getInt(localCursor.getColumnIndex("_id")), localCursor.getString(localCursor.getColumnIndex("_data")), localCursor.getString(localCursor.getColumnIndex("_display_name")), localCursor.getString(localCursor.getColumnIndex("duration")));
            if (mVideoUrls.containsKey(FLAG_RECENT)&&mVideoUrls.get(FLAG_RECENT).size()<mRecentSize){
                mVideoUrls.get(FLAG_RECENT).add(localVideoBean);
            }else if (!mVideoUrls.containsKey(FLAG_RECENT)){
                mVideoUrls.put(FLAG_RECENT,new ArrayList<VideoBean>());
                mVideoUrls.get(FLAG_RECENT).add(localVideoBean);
            }
            if (mVideoUrls.containsKey(localVideoBean.getParentPath()))
                mVideoUrls.get(localVideoBean.getParentPath()).add(localVideoBean);
            else {
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localVideoBean);
                mVideoUrls.put(localVideoBean.getParentPath(), localArrayList);
            }
        }
        localCursor.close();
        mVideoUrls.size();
    }

    public Cursor getContentCursor(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2) {
        return mContent.getContentResolver().query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    }

    public ContentProviderUtils setRecentSize(int paramInt) {
        mRecentSize = paramInt;
        return this;
    }

    public void subjectType(final int type) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                asyncLoader(type);
            }
        });
    }

    private void asyncLoader(int type) {
        switch (type) {
            case TYPE_AUDIO:
                getAudioUrls();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReadListener.onRead(mAudioUrls);
                    }
                });
                break;
            case TYPE_VIDEO:
                getVideoUrls();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReadListener.onRead(mVideoUrls);
                    }
                });
                break;
            case TYPE_PHOTO:
                getPhotoUrls();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReadListener.onRead(mImageUrls);
                    }
                });
                break;
        }
    }

    public interface OnReadListener {
        void onRead(Map paramMap);
    }
}
