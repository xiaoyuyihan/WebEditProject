package com.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by lw on 2018/1/6.
 */

public class MediaService extends Service {

    public static final String DATA_AUDIO_DURATION="DATA_AUDIO_DURATION";
    public static final String ACTION_MEDIA_SERVICE="com.edit.media.service.action";

    private MediaBinder mMediaBinder = new MediaBinder();
    private MediaPlayer mMediaPlayer;
    private String mPath;
    private static final String Tag=MediaService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMediaBinder;
    }

    @Override
    public void onCreate() {
        if (mMediaPlayer ==null)
            mMediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaDataSource(String path){
        Log.d(Tag,"set path "+path);
        if (mPath!=null&&mPath.equals(path)){
            onPauseAudio();
        }else {
            mPath=path;
            initMediaDataSource(mPath);
        }
    }

    private void initMediaDataSource(String path){
        try {
            mMediaPlayer.setDataSource(path);
            // 使用系统的媒体音量控制
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
            }
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    sendDurationBroad(mp.getDuration());
                    mp.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mMediaPlayer=null;
        }
    }

    public void sendDurationBroad(int duration) {
        Intent intent = new Intent();
        intent.setAction(ACTION_MEDIA_SERVICE);
        intent.putExtra(DATA_AUDIO_DURATION,duration);
        sendBroadcast(intent);
    }

    public void onStopAudio(){
        if (mMediaPlayer!=null)
            mMediaPlayer.stop();
    }

    public void onSeekTo(int position){
        if (mMediaPlayer!=null)
            mMediaPlayer.seekTo(position);
    }

    public void onPauseAudio(){
        if (mMediaPlayer!=null)
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }else
                mMediaPlayer.start();
    }

    public void onResetAudio(){
        if (mMediaPlayer!=null)
            mMediaPlayer.reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MediaBinder extends Binder{
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
        public MediaService getService(){
            return MediaService.this;
        }
    }
}
