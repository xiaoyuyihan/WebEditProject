package com.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.provider.utils.ContentProviderUtils;
import com.provider.view.ProviderActivity;
import com.webeditproject.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity implements
        Camera2Utils.CameraCompleteCallback {

    public static final String CAMERA_OPEN_TYPE = "CAMERA_OPEN_TYPE";
    public static final int CAMERA_PICTURE = 1;
    public static final int CAMERA_VIDEO = 2;

    @BindView(R.id.auto_fit_texture)
    AutoFitTextureView mAutoFitTexture;
    @BindView(R.id.camera_back)
    ImageView mBackImage;
    @BindView(R.id.camera_conversions)
    ImageView mCameraConversions;
    @BindView(R.id.camera_take)
    ImageView mTake;
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;
    @BindView(R.id.camera_provider_check)
    ImageView mProviderView;
    RelativeLayout mMainLayout;

    private Bitmap mConversions;
    private int mType;
    private Camera2Utils mCameraUtils;
    private Handler mMainHandler;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            mCameraUtils.openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            mCameraUtils.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            Log.d("CameraActivity", "onSurfaceTextureUpdated");
        }
    };
    private boolean isFocusing=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setWindowBar();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        mMainLayout=(RelativeLayout)findViewById(R.id.camera_layout);
        mMainHandler = new Handler();
        mType = getIntent().getExtras().getInt(CAMERA_OPEN_TYPE);
    }

    private void drawFocal(float x, float y) {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        //定义画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);//去锯齿
        paint.setStyle(Paint.Style.STROKE);//空心
        // 设置paint的外框宽度
        paint.setStrokeWidth(2f);
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清楚掉上一次的画框。
        if (isFocusing){
            canvas.drawBitmap(mConversions, x - mConversions.getWidth() / 2,
                    y - mConversions.getHeight() / 2, paint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void setWindowBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            //隐藏标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //隐藏状态栏
            //定义全屏参数
            int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //设置当前窗体为全屏显示
            getWindow().setFlags(flag, flag);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraUtils = Camera2Utils.getInstance().setContext(this).setTextureView(mAutoFitTexture);
        mCameraUtils.startBackgroundThread();
        mCameraUtils.setCameraCompleteCallback(this);
        if (mAutoFitTexture.isAvailable()) {
            mCameraUtils.openCamera(mAutoFitTexture.getWidth(), mAutoFitTexture.getHeight());
        } else {
            mAutoFitTexture.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        init();
    }

    private void init() {
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float downX = event.getX();
                final float downY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN && Math.abs(downX) < mAutoFitTexture.getWidth()
                        && Math.abs(downY) < mAutoFitTexture.getHeight()) {
                    isFocusing=!isFocusing;
                    drawFocal(downX, downY);
                    mCameraUtils.setRegions(downX, downY);
                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFocusing=!isFocusing;
                            drawFocal(downX, downY);
                        }
                    },3000);
                }
                return false;
            }
        });
        mSurfaceView.setZOrderOnTop(true);//处于顶层
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);//设置surface为透明
        mConversions = BitmapFactory.decodeResource(getResources(), R.drawable.ic_focal_point);
        if (mType == CAMERA_PICTURE) {
            mProviderView.setImageDrawable(getDrawable(R.drawable.ic_photo_check));
        } else {
            mProviderView.setImageDrawable(getDrawable(R.drawable.ic_video_check));
        }
    }

    @Override
    protected void onPause() {
        mCameraUtils.closeCamera();
        mCameraUtils.stopBackgroundThread();
        super.onPause();
    }

    @OnClick(R.id.camera_back)
    public void onCameraBack(View view) {
        finish();
    }

    @OnClick(R.id.camera_conversions)
    public void onCameraConversions(View view) {
        mCameraUtils.updataCameraFacing();
    }

    @OnClick(R.id.camera_take)
    public void onCameraTake(View view) {
        if (mType == CameraActivity.CAMERA_PICTURE) {
            mCameraUtils.takePicture();
        } else {
            if (!mCameraUtils.isRecordingVideo()) {
                mCameraUtils.startRecordingVideo();
            } else
                mCameraUtils.stopRecordingVideo();
        }
    }

    @OnClick(R.id.camera_provider_check)
    public void onCameraToProvider(View view) {
        startProviderActivity();
    }

    private void startProviderActivity() {
        Intent intent = new Intent("com.provider.ACTION_PROVIDER");
        if (mType == CAMERA_PICTURE) {
            intent.putExtra(ProviderActivity.TYPE_KEY, ContentProviderUtils.TYPE_PHOTO);
        } else {
            intent.putExtra(ProviderActivity.TYPE_KEY, ContentProviderUtils.TYPE_VIDEO);
        }
        startActivityForResult(intent, 2);
        finish();
    }

    @Override
    public void onCameraComplete(final String file) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mType == CAMERA_PICTURE) {

                }
            }
        });
    }
}
