package com.provider.view;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.main.BaseActivity;
import com.provider.utils.AudioBean;
import com.provider.utils.ContentProviderUtils;
import com.provider.utils.ImageBean;
import com.provider.utils.IntentBean;
import com.provider.utils.VideoBean;
import com.webeditproject.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.Attributes;

/**
 * Created by lw on 2017/11/30.
 */

public class ParticularActivity extends BaseActivity implements View.OnClickListener ,View.OnTouchListener{

    public static final String ARGUMENT_KEY_CHECK = "ARGUMENT_KEY_CHECK";
    public static final String ARGUMENT_KEY_DATA = "ARGUMENT_KEY_DATA";
    public static final String ARGUMENT_KEY_POSITION = "ARGUMENT_KEY_POSITION";
    public static final String ARGUMENT_KEY_TYPE = "ARGUMENT_KEY_TYPE";

    public static final String RESULT_KEY = "result_key";

    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
    };
    private ImageView mAuditImage;
    private TextView mAuditText;
    private CheckBox mCheckBox;
    private ArrayList<Parcelable> mChecks=new ArrayList<>();
    private ArrayList<Parcelable> mData;
    private ImageView mImageView;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private ImageView mPlay;
    private int mPosition=0;
    private int mType;
    private TextView mVideoCheck;
    private VideoView mVideoView;
    private TextView mViewBack;
    private ArrayList<ImageView> mViewList = new ArrayList<>();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        switchContentView();
    }

    private void initData() {
        mType = getIntent().getExtras().getInt(ARGUMENT_KEY_TYPE);
    }

    private void switchContentView() {
        switch (mType) {
            default:
                return;
            case ContentProviderUtils.TYPE_AUDIO:
                setContentView(R.layout.view_dialog_provider_audio);
                findAudioView();
                initAudio();
                break;
            case ContentProviderUtils.TYPE_PHOTO:
                setContentView(R.layout.view_dialog_provider_image);
                findPhotoView();
                initPhotoView();
                break;
            case ContentProviderUtils.TYPE_VIDEO:
                setContentView(R.layout.view_dialog_provider_video);
                findVideoView();
                initVideoView();
                break;
        }

    }


    private void findAudioView() {
        mViewBack = ((TextView) findViewById(R.id.view_dialog_provider_audio_back));
        mViewBack.setOnClickListener(this);
        mPlay = ((ImageView) findViewById(R.id.view_dialog_provider_audio_play));
        mPlay.setOnClickListener(this);
        mVideoCheck = ((TextView) findViewById(R.id.view_dialog_provider_audio_subject));
        mVideoCheck.setOnClickListener(this);
        mAuditImage = ((ImageView) findViewById(R.id.view_dialog_provider_audio_image));
        mAuditText = ((TextView) findViewById(R.id.view_dialog_provider_audio_name));
        mData= IntentBean.getInstance().getChecks();
        mAuditText.setText(((AudioBean) mData.get(mPosition)).getName());
    }

    private void findPhotoView() {
        mViewPager = ((ViewPager) findViewById(R.id.view_dialog_provider_image_viewpager));
        mCheckBox = ((CheckBox) findViewById(R.id.view_dialog_provider_image_check));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean) {

            }
        });
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=mViewPager.getCurrentItem();
                ImageBean localImageBean = (ImageBean) mData.get(position);
                mCheckBox.invalidate();
                if (!mChecks.contains(localImageBean)) {
                    mChecks.add(localImageBean);
                } else {
                    mChecks.remove(localImageBean);
                }
            }
        });
        mViewBack = ((TextView) findViewById(R.id.view_dialog_provider_image_back));
        mViewBack.setOnClickListener(this);
    }

    private void findVideoView() {
        mViewBack = (TextView) findViewById(R.id.provider_top_back);
        mViewBack.setOnClickListener(this);
        mVideoView = (VideoView) findViewById(R.id.view_dialog_provider_video_video);
        mVideoCheck = (TextView) findViewById(R.id.provider_top_subject);
        mVideoCheck.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.view_dialog_provider_video_image);
    }


    private void initAudio() {
        try {
            mMediaPlayer.setDataSource(((AudioBean) mData.get(mPosition)).getPath());
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
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    private void initImageViews() {
        for (Parcelable parcelable : mData) {
            ImageView localImageView = new ImageView(this);
            localImageView.setLayoutParams(new ViewPager.LayoutParams());
            localImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mViewList.add(localImageView);
            Glide.with(this).load(new File(((ImageBean) parcelable).getPath())).into(localImageView);
        }
    }

    private void initPhotoView() {
        mChecks=IntentBean.getInstance().getChecks();
        mData=IntentBean.getInstance().getData();
        mPosition=IntentBean.getInstance().getPosition();
        initImageViews();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);
        upDataCheckBox(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int position) {
            }

            public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
            }

            public void onPageSelected(int paramInt) {
                upDataCheckBox(paramInt);
            }
        });
    }

    private void upDataCheckBox(int position) {
        if (mChecks.contains(mData.get(position))) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }
    }

    private void initVideoView() {
        mData= IntentBean.getInstance().getChecks();
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse(((VideoBean) mData.get(mPosition)).getPath()));
        mImageView.setImageBitmap(((VideoBean) mData.get(mPosition)).getVideoBitmap());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_dialog_provider_audio_back:
                mMediaPlayer.stop();
                mMediaPlayer.release();
                setResult();
                break;
            case R.id.provider_top_back:
                mVideoView.pause();
            case R.id.view_dialog_provider_image_back:
                setResult();
                break;
            case R.id.view_dialog_provider_audio_play:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlay.setImageResource(R.drawable.ic_play);
                } else {
                    mMediaPlayer.start();
                    mPlay.setImageResource(R.drawable.ic_pause);
                }
                break;
            case R.id.view_dialog_provider_audio_subject:
            case R.id.provider_top_subject:
                mChecks.add(mData.get(mPosition));
                setResult();
        }
    }

    private void setResult() {
        IntentBean.getInstance().setChecks(mChecks);
        setResult(ProviderActivity.RESULT_CODE_PROVIDER);
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
