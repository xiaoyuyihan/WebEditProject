package com.provider.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.camera.CameraActivity;
import com.main.BaseActivity;
import com.main.bean.EditBean;
import com.provider.utils.AudioBean;
import com.provider.utils.ContentProviderUtils;
import com.provider.utils.ImageBean;
import com.provider.utils.IntentBean;
import com.provider.utils.VideoBean;
import com.utils.MessageDialogFragment;
import com.webeditproject.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lw on 2017/11/30.
 */

public class ProviderActivity extends BaseActivity implements View.OnClickListener,
        ContentProviderUtils.OnReadListener, ProviderAdapter.onCheckedChangeListener,
        ProviderAdapter.onTextClickListener,ProviderAdapter.onTouchClickListener {

    public static final String DATA_KEY = "DATA_KEY";
   // public static final String NUMBER_KEY = "NUMBER_KEY";
    public static final String TYPE_KEY = "TYPE_KEY";

    public static int RESULT_CODE = 2;

    public static final int RESULT_CODE_PROVIDER = 200;

    public static final int TYPE_MANY = 12;
    public static final int TYPE_SINGLE = 11;

    private static final String PARTICULARS="Fragment_Particulars";
    private static final String DIRECTORY="Fragment_Directory";

    private String key = "android.content.provider.recent";
    private int mType = ContentProviderUtils.TYPE_AUDIO;
    private int mTypeNumber = 20;

    private ArrayList<Parcelable> mCheckPathList = new ArrayList();
    private Map<String, ArrayList<Parcelable>> mData;

    private ImageView topBackView;
    private TextView topSubjectView;
    private TextView topNameView;
    private TextView topNumberView;
    private FragmentManager fragmentManager;
    private LoadDialogFragment loadingDialog;
    private ParticularsDetailsFragment localParticularsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        findView();
        init();
        showLoadingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upDataCheckNumber();
    }

    private void findView() {
        topBackView = (ImageView) findViewById(R.id.provider_top_back);
        topBackView.setOnClickListener(this);
        topNameView = (TextView) findViewById(R.id.provider_top_name);
        topNumberView = (TextView) findViewById(R.id.provider_top_number);
        topSubjectView = (TextView) findViewById(R.id.provider_top_subject);
        topSubjectView.setOnClickListener(this);
    }

    private void upDataCheckNumber(){
        topNumberView.setText("( "+mCheckPathList.size()+" )");
    }

    private void init() {
        mType = getIntent().getExtras().getInt(TYPE_KEY);
        if (mType!=ContentProviderUtils.TYPE_PHOTO){
            topNumberView.setVisibility(View.GONE);
            topSubjectView.setVisibility(View.GONE);
        }
        fragmentManager = getSupportFragmentManager();
        boolean isAllGranted = checkPermissionAllGranted(
                new String[] {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        if (isAllGranted) {
            ContentProviderUtils.getInstance(this).setRecentSize(mTypeNumber).setReadListener(this).subjectType(mType);
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1
        );
    }

    private void showLoadingDialog() {
        loadingDialog = new LoadDialogFragment();
        loadingDialog.show(getSupportFragmentManager(), "LoadDialogFragment");
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 检索数据完成
     * @param dataMap
     */
    @Override
    public void onRead(Map dataMap) {
        mData = dataMap;
        loadingDialog.dismiss();
        if (mData.size()==0){
            MessageDialogFragment messageDialogFragment= new MessageDialogFragment();
            messageDialogFragment.setMessage("小公举/小王子，你的资料库是空的啊，让我们去找点填充进去吧！");
            messageDialogFragment.show(getSupportFragmentManager(),"");
        }else {
            setFragment();
        }
    }

    private void setFragment() {
        setTopName();
        FragmentTransaction localFragmentTransaction = fragmentManager.beginTransaction();
        localParticularsFragment = new ParticularsDetailsFragment();
        Bundle localBundle = new Bundle();
        localBundle.putInt(ParticularsDetailsFragment.FRAGMENT_ARGUMENT_TYPE, this.mType);
        localBundle.putParcelableArrayList(ParticularsDetailsFragment.FRAGMENT_ARGUMENT_DATA, mData.get(key));
        localBundle.putParcelableArrayList(ParticularsDetailsFragment.FRAGMENT_ARGUMENT_CHECK_DATA, mCheckPathList);
        localParticularsFragment.setArguments(localBundle);
        localParticularsFragment.setOnCheckedChangeListener(this);
        localParticularsFragment.setOnTouchClickListener(this);
        if (fragmentManager.findFragmentById(R.id.provider_fragment) == null)
            localFragmentTransaction.add(R.id.provider_fragment, localParticularsFragment, PARTICULARS);
        else
            localFragmentTransaction.replace(R.id.provider_fragment, localParticularsFragment, PARTICULARS);
        localFragmentTransaction.commitAllowingStateLoss();
    }

    private void setTopName() {
        if (!key.equals("android.content.provider.recent")){
            topNameView.setText(ImageBean.getParentName(key));
        }else
            topNameView.setText("最近");
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(this, ParticularActivity.class);
        intent.putExtra("ARGUMENT_KEY_TYPE", mType);
        if (mType==ContentProviderUtils.TYPE_PHOTO){
            IntentBean.getInstance().setPosition(position)
                    .setData(mData.get(key))
                    .setChecks(mCheckPathList);
        }else {
            IntentBean.getInstance().addCheck(mData.get(key).get(position));
        }
        startActivityForResult(intent, RESULT_CODE_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckPathList=IntentBean.getInstance().getChecks();
        localParticularsFragment.setUpdateChecks(mCheckPathList);
        if (mType!=ContentProviderUtils.TYPE_PHOTO){
            setResultData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                ContentProviderUtils.getInstance(this).setRecentSize(mTypeNumber).setReadListener(this).subjectType(mType);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("备份通讯录需要访问 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProviderActivity.this.finish();
            }
        });
        builder.show();
    }

    private void setResultData() {
        IntentBean.getInstance().setChecks(getEditBeanList());
        setResult(ProviderActivity.RESULT_CODE);
        finish();
    }

    public void onTextClick(String dataKey) {
        key = dataKey;
        setFragment();
    }

    //选择图片
    public void onCheckedChanged(Parcelable paramParcelable) {
        if (!mCheckPathList.contains(paramParcelable)) {
            mCheckPathList.add(paramParcelable);
        } else {
            mCheckPathList.remove(paramParcelable);
        }
        upDataCheckNumber();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.provider_top_back:
                //内容详情
                if (fragmentManager.findFragmentById(R.id.provider_fragment).getTag().equals(PARTICULARS)) {
                    topNameView.setText("相册");
                    FragmentTransaction localFragmentTransaction = fragmentManager.beginTransaction();
                    localParticularsFragment = new ParticularsDetailsFragment();
                    Bundle localBundle = new Bundle();
                    localBundle.putInt(ParticularsDetailsFragment.FRAGMENT_ARGUMENT_TYPE, ContentProviderUtils.TYPE_TEXT);
                    setBundleMap(localBundle);
                    localParticularsFragment.setArguments(localBundle);
                    localParticularsFragment.setOnTextClickListener(this);
                    localFragmentTransaction.replace(R.id.provider_fragment,
                            localParticularsFragment,DIRECTORY)
                            .commit();
                } else{
                    //返回 拍摄页面
                    Intent intent=new Intent("com.camera.ACTION_START_CAMERA");
                    if (mType== ContentProviderUtils.TYPE_PHOTO){
                        intent.putExtra(CameraActivity.CAMERA_OPEN_TYPE,ContentProviderUtils.TYPE_PHOTO);
                        startActivityForResult(intent,ProviderActivity.RESULT_CODE);
                    }else if (mType== ContentProviderUtils.TYPE_VIDEO){
                        intent.putExtra(CameraActivity.CAMERA_OPEN_TYPE,ContentProviderUtils.TYPE_VIDEO);
                        startActivityForResult(intent,ProviderActivity.RESULT_CODE);
                    }else if (mType == ContentProviderUtils.TYPE_AUDIO){

                    }
                    finish();
                }

                break;
            case R.id.provider_top_subject:
                setResultData();
        }
    }

    private void setBundleMap(Bundle paramBundle) {
        Iterator localIterator = mData.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            paramBundle.putParcelableArrayList(str, mData.get(str));
        }
    }

    public ArrayList<Parcelable> getEditBeanList() {
        ArrayList<Parcelable> beanList=new ArrayList<>();
        for (Parcelable p:mCheckPathList){
            if (p instanceof ImageBean){
                beanList.add(imageBeanToEditBean(p));
            }else if (p instanceof AudioBean){
                beanList.add(audioBeanToEditBean(p));
            }else if (p instanceof VideoBean){
                beanList.add(videoBeanToEditBean(p));
            }
        }
        return beanList;
    }

    private EditBean imageBeanToEditBean(Parcelable p){
        EditBean e=new EditBean();
        ImageBean i=(ImageBean)p;
        e.setType(EditBean.TYPE_PHOTO).setPath(i.getPath());
        return e;
    }
    private EditBean audioBeanToEditBean(Parcelable p){
        EditBean e=new EditBean();
        AudioBean i=(AudioBean)p;
        e.setType(EditBean.TYPE_AUDIO).setPath(i.getPath()).setProviderName(i.getName()).setTime(i.getDuration());
        return e;
    }
    private EditBean videoBeanToEditBean(Parcelable p){
        EditBean e=new EditBean();
        VideoBean i=(VideoBean)p;
        e.setType(EditBean.TYPE_VIDEO).setPath(i.getPath());
        return e;
    }
}
