package com.main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.main.bean.EditBean;
import com.provider.utils.ContentProviderUtils;
import com.provider.utils.IntentBean;
import com.provider.view.ProviderActivity;
import com.service.MediaService;
import com.utils.MessageDialogFragment;
import com.utils.TypeConverUtil;
import com.webedit.HtmlEditActivity;
import com.webeditproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lw on 2018/1/2.
 */

public class EditActivity extends BaseActivity implements OnClickViewHolder, OnClickViewHolder.OnBroadcastReceiverListener {
    @BindView(R.id.edit_common_top_back)
    TextView mBackView;
    @BindView(R.id.edit_common_top_next)
    TextView mNextView;
    @BindView(R.id.edit_common_top_menu)
    FloatingActionsMenu mFloatingActionsMenu;
    @BindView(R.id.edit_common_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.edit_common_menu_audio)
    View mAudio;
    @BindView(R.id.edit_common_menu_video)
    View mVideo;
    @BindView(R.id.edit_common_menu_photo)
    View mPhoto;

    MessageDialogFragment messageDialogFragment;

    private LinearLayoutManager linearLayoutManager;

    private Timer mTimer = new Timer();

    private ArrayList<EditBean> mEditBeanList = new ArrayList<>();
    private EditAdapter mEditAdapter;
    private MediaService mediaService;
    private DurationBroadcastReceiver broadcastReceiver = new DurationBroadcastReceiver(this);

    private SeekBar mViewHolderBar;
    private TextView mAudioTextView;
    private Handler mMainHandler = new Handler();

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaService = ((MediaService.MediaBinder) service).getService();
            if (mediaService.getMediaPlayer().isPlaying())
                mediaService.sendDurationBroad(mediaService.getMediaPlayer().getDuration());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_common);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(this);
        mEditAdapter = new EditAdapter(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mEditAdapter);
        SimpleItemTouchHelperCallback mCallback = new SimpleItemTouchHelperCallback<>(mEditAdapter, mEditBeanList);
        mCallback.setSwipeState(new SimpleItemTouchHelperCallback.SwipeStateAlterHelper() {
            @Override
            public void onLeftMove(float moveX, RecyclerView.ViewHolder holder) {

            }

            @Override
            public void onRightMove(float moveX, RecyclerView.ViewHolder holder) {

            }

            @Override
            public void onMoveConsummation(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = mRecyclerView.getChildLayoutPosition(viewHolder.itemView);
                showDeleteDialog(position);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onStartService();
        IntentFilter intentFilter = new IntentFilter();
        //设置接收广播的类型
        intentFilter.addAction(MediaService.ACTION_MEDIA_SERVICE);
        //调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1 && data != null) {
            mEditBeanList.add((EditBean) data.getParcelableExtra(HtmlEditActivity.HTML_EDIT_DATA_KEY));
        } else if (requestCode == 2) {
            ArrayList d = IntentBean.getInstance().getChecks();
            mEditBeanList.addAll(d);
            IntentBean.getInstance().getChecks().clear();
        } else if (requestCode == 3 && data != null) {
            EditBean editBean = (EditBean) data.getParcelableExtra(HtmlEditActivity.HTML_EDIT_DATA_KEY);
            int position = data.getIntExtra(HtmlEditActivity.HTML_EDIT_POSITION_KEY, -1);
            if (position != -1) {
                mEditBeanList.remove(position);
                mEditBeanList.add(position, editBean);
            }
        }
        mEditAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.edit_common_top_back)
    public void onBack(View v) {
        finish();
    }

    @OnClick(R.id.edit_common_top_next)
    public void onNextActivity(View v) {

    }

    private void showDeleteDialog(final int position) {
        messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setOnMsgDialogClickListener(new MessageDialogFragment.OnMsgDialogClickListener() {
            @Override
            public void onSureBut() {
                mEditBeanList.remove(position);
                messageDialogFragment.dismiss();
                mEditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                messageDialogFragment.dismiss();
                mEditAdapter.notifyDataSetChanged();
            }
        });
        messageDialogFragment.show(getSupportFragmentManager(), "MessageDialogFragment");
    }

    @OnClick({R.id.edit_common_menu_photo, R.id.edit_common_menu_video, R.id.edit_common_menu_audio})
    public void onProviderActivity(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.edit_common_menu_audio:
                intent = new Intent("com.provider.ACTION_PROVIDER");
                intent.putExtra(ProviderActivity.TYPE_KEY, ContentProviderUtils.TYPE_AUDIO);
                break;
            case R.id.edit_common_menu_photo:
                intent = new Intent("com.camera.ACTION_START_CAMERA");
                intent.putExtra("CAMERA_OPEN_TYPE", ContentProviderUtils.TYPE_PHOTO);
                intent.putExtra(ProviderActivity.TYPE_KEY, ContentProviderUtils.TYPE_PHOTO);
                break;
            case R.id.edit_common_menu_video:
                intent = new Intent("com.camera.ACTION_START_CAMERA");
                intent.putExtra("CAMERA_OPEN_TYPE", ContentProviderUtils.TYPE_VIDEO);
                intent.putExtra(ProviderActivity.TYPE_KEY, ContentProviderUtils.TYPE_VIDEO);
                break;
        }
        startActivityForResult(intent, 2);
        mFloatingActionsMenu.performClick();
    }

    @OnClick(R.id.edit_common_menu_text)
    public void onHtmlEditActivity(View v) {
        Intent intent = new Intent(this, HtmlEditActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onStartService() {
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder holder) {
        if (holder instanceof AudioViewHolder) {
            mViewHolderBar = ((AudioViewHolder) holder).getSeekBar();
            mAudioTextView = ((AudioViewHolder) holder).getCurrentTime();
        }
    }

    @Override
    public void onReceiver(Intent intent) {
        mViewHolderBar.setMax(intent.getIntExtra(MediaService.DATA_AUDIO_DURATION, 0));
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                final int current = mediaService.getMediaPlayer().getCurrentPosition();
                mViewHolderBar.setProgress(current);
                mViewHolderBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaService.onSeekTo(seekBar.getProgress());
                    }
                });
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAudioTextView.setText(TypeConverUtil.TimeMSToMin(current));
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);
    }

    class EditAdapter extends RecyclerView.Adapter {

        private LayoutInflater mInflater;
        private OnClickViewHolder onClickViewHolder;

        public EditAdapter(OnClickViewHolder onClickViewHolder) {
            mInflater = LayoutInflater.from(EditActivity.this);
            this.onClickViewHolder = onClickViewHolder;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder mViewHolder = null;
            switch (viewType) {
                case EditBean.TYPE_AUDIO:
                    mViewHolder = new AudioViewHolder(mInflater.inflate(R.layout.view_item_edit_audio, parent, false));
                    break;
                case EditBean.TYPE_PHOTO:
                    mViewHolder = new PhotoViewHolder(mInflater.inflate(R.layout.view_item_edit_photo, parent, false));
                    break;
                case EditBean.TYPE_TEXT:
                    mViewHolder = new TextViewHolder(mInflater.inflate(R.layout.view_item_edit_text, parent, false));
                    break;
                case EditBean.TYPE_VIDEO:
                    mViewHolder = new VideoViewHolder(mInflater.inflate(R.layout.view_item_edit_video, parent, false));
                    break;
            }
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.d("EditAdapter", "onBindViewHolder");
            final EditBean currentBean = mEditBeanList.get(position);
            if (holder instanceof TextViewHolder) {
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.setHtml(currentBean.getHTML5());
                textViewHolder.setHtmlSize(currentBean.getHtmlTextSize());
                textViewHolder.setGravity(currentBean.getGravity());
                textViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EditActivity.this, HtmlEditActivity.class);
                        intent.putExtra(HtmlEditActivity.HTML_EDIT_TYPE_KEY,HtmlEditActivity.HTML_EDIT_TYPE_REWRITE);
                        intent.putExtra(HtmlEditActivity.HTML_EDIT_DATA_KEY,currentBean);
                        intent.putExtra(HtmlEditActivity.HTML_EDIT_POSITION_KEY,position);
                        startActivityForResult(intent, 3);
                    }
                });
            } else if (holder instanceof AudioViewHolder) {
                AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
                audioViewHolder.setTopName(currentBean.getProviderName());
                audioViewHolder.setTime(TypeConverUtil.TimeMSToMin(currentBean.getTime()));
                audioViewHolder.setPlayClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaService != null) {
                            onClickViewHolder.onClick(holder);
                            mediaService.setMediaDataSource(currentBean.getPath());
                        }
                    }
                });
            } else if (holder instanceof VideoViewHolder) {

            } else if (holder instanceof PhotoViewHolder) {
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
                photoViewHolder.setPhotoView(currentBean.getPath());
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mEditBeanList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return mEditBeanList.size();
        }

    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_item_edit_text)
        TextView mTextView;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setHtml(String mHtml) {
            Spanned html;
            if (Build.VERSION.SDK_INT >= 24) {
                html = Html.fromHtml(mHtml, Html.FROM_HTML_MODE_LEGACY);
            } else {
                html = Html.fromHtml(mHtml);
            }
            if (html.length() > 2)
                html = (Spanned) html.subSequence(0, html.length() - 2);
            mTextView.setText(html);
        }

        public void setHtmlSize(int size) {
            mTextView.setTextSize(size);
        }

        public void setGravity(String gravity) {
            if (gravity.equals("left")) {
                mTextView.setGravity(Gravity.LEFT);
            } else if (gravity.equals("center")) {
                mTextView.setGravity(Gravity.CENTER);
            } else if (gravity.equals("right")) {
                mTextView.setGravity(Gravity.RIGHT);
            }
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        public VideoViewHolder(View itemView) {
            super(itemView);
        }
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_item_edit_photo)
        ImageView mPhotoView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPhotoView(String bitmap) {
            Glide.with(EditActivity.this).load(new File(bitmap)).into(mPhotoView);
        }
    }

    class AudioViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_item_edit_audio_play)
        ImageView mPlay;
        @BindView(R.id.view_item_edit_audio_top)
        TextView mTopName;
        @BindView(R.id.view_item_edit_audio_seekbar)
        SeekBar mSeekBar;
        @BindView(R.id.view_item_edit_audio_current_time)
        TextView mCurrentTime;
        @BindView(R.id.view_item_edit_audio_time)
        TextView mTime;

        public AudioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPlayClick(View.OnClickListener onClickListener) {
            mPlay.setOnClickListener(onClickListener);
        }

        public void setTime(String time) {
            mTime.setText(time);
        }

        public TextView getCurrentTime() {
            return mCurrentTime;
        }

        public SeekBar getSeekBar() {
            return mSeekBar;
        }

        public void setTopName(String name) {
            mTopName.setText(name);
        }
    }

    class DurationBroadcastReceiver extends BroadcastReceiver {

        private OnBroadcastReceiverListener onBroadcastReceiverListener;

        public DurationBroadcastReceiver(OnBroadcastReceiverListener onBroadcastReceiverListener) {
            this.onBroadcastReceiverListener = onBroadcastReceiverListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceiverListener.onReceiver(intent);
        }
    }
}
