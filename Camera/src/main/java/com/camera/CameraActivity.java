package com.camera;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    public static final String CAMERA_OPEN_TYPE = "CAMERA_OPEN_TYPE";
    public static final int CAMERA_PICTURE = 1;
    public static final int CAMERA_VIDEO = 2;

    @BindView(R2.id.auto_fit_texture)
    AutoFitTextureView mAutoFitTexture;
    @BindView(R2.id.camera_back)
    ImageView mBackImage;
    @BindView(R2.id.camera_conversions)
    ImageView mCameraConversions;
    @BindView(R2.id.camera_take)
    ImageView mTake;
    @BindView(R2.id.surface_view)
    SurfaceView mSurfaceView;

    private Bitmap mConversions;
    private int mType;
    private Camera2Utils mCameraUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowBar();
        setContentView(R.layout.activity_camera1);
        ButterKnife.bind(this);
        mType = getIntent().getExtras().getInt(CAMERA_OPEN_TYPE);
        init();
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
        canvas.drawBitmap(mConversions, x - mConversions.getWidth() / 2, y - mConversions.getHeight() / 2, paint);
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
        if (mAutoFitTexture.isAvailable()) {
            mCameraUtils.openCamera(mAutoFitTexture.getWidth(), mAutoFitTexture.getHeight());
        } else {
            mAutoFitTexture.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        init();
    }

    private void init() {
        mBackImage.setOnClickListener(this);
        mCameraConversions.setOnClickListener(this);
        mTake.setOnClickListener(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float downX = event.getX();
                float downY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN && Math.abs(downX) < mAutoFitTexture.getWidth()
                        && Math.abs(downY) < mAutoFitTexture.getHeight()) {
                    drawFocal(downX, downY);
                    mCameraUtils.setRegions(downX, downY);
                }
                return false;
            }
        });
        mSurfaceView.setZOrderOnTop(true);//处于顶层
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);//设置surface为透明
        mConversions = BitmapFactory.decodeResource(getResources(), R.drawable.ic_focal_point);
    }

    @Override
    protected void onPause() {
        mCameraUtils.closeCamera();
        mCameraUtils.stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.camera_back:
                finish();
                break;
            case R2.id.camera_conversions:
                mCameraUtils.updataCameraFacing();
                break;
            case R2.id.camera_take:
                if (mType == CameraActivity.CAMERA_PICTURE) {
                    mCameraUtils.takePicture();
                } else {
                    if (!mCameraUtils.isRecordingVideo()) {
                        mCameraUtils.startRecordingVideo();
                    } else
                        mCameraUtils.stopRecordingVideo();
                }
                break;
        }
    }
}
