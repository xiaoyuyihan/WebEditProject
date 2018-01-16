package com.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.main.bean.EditBean;
import com.provider.utils.ContentProviderUtils;
import com.provider.utils.IntentBean;
import com.provider.view.ProviderActivity;
import com.webeditproject.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lw on 2018/1/12.
 */

public class CameraBrowseActivity extends AppCompatActivity {

    public static final String CAMERA_IMAGE_PATH="CAMERA_IMAGE_PATH";
    public static final String CAMERA_IMAGE_TYPE="CAMERA_IMAGE_TYPE";

    @BindView(R.id.camera_browse_top_back)
    TextView mBrowseBackView;
    @BindView(R.id.camera_browse_top_subject)
    TextView mBrowseSubjectView;
    @BindView(R.id.camera_browse_image)
    ImageView mBrowseImageView;

    private String mImagePath;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_browse);
        ButterKnife.bind(this);
        mImagePath=getIntent().getExtras().getString(CAMERA_IMAGE_PATH);
        mType=getIntent().getExtras().getInt(CAMERA_IMAGE_TYPE);
        Glide.with(this).load(new File(mImagePath)).into(mBrowseImageView);
    }

    @OnClick(R.id.camera_browse_top_back)
    public void onCameraBrowseBack(View view){
        //返回 拍摄页面
        Intent intent=new Intent("com.provider.ACTION_START_CAMERA");
        if (mType== ContentProviderUtils.TYPE_PHOTO){
            intent.putExtra(CameraActivity.CAMERA_OPEN_TYPE,ContentProviderUtils.TYPE_PHOTO);
        }else if (mType== ContentProviderUtils.TYPE_VIDEO){
            intent.putExtra(CameraActivity.CAMERA_OPEN_TYPE,ContentProviderUtils.TYPE_PHOTO);
        }
        startActivityForResult(intent, ProviderActivity.RESULT_CODE);
        finish();
    }

    @OnClick(R.id.camera_browse_top_subject)
    public void onCameraBrowseSubject(View view){
        EditBean editBean= new EditBean();
        if (mType==ContentProviderUtils.TYPE_PHOTO){
            editBean.setType(EditBean.TYPE_PHOTO).setPath(mImagePath);
        }else {
            editBean.setType(EditBean.TYPE_VIDEO).setPath(mImagePath);
        }
        IntentBean.getInstance().setCheck(editBean);
        setResult(2);
        finish();
    }
}
