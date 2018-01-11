package com.provider.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.provider.utils.AudioBean;
import com.provider.utils.ContentProviderUtils;
import com.provider.utils.ImageBean;
import com.provider.utils.VideoBean;
import com.webeditproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2017/11/30.
 *///没视频
public class ProviderAdapter extends RecyclerView.Adapter {
    private int Type;
    private Context context;
    private List data;
    private LayoutInflater inflater;
    private ArrayList<Parcelable> mChecks;
    private Map<String, ArrayList> mapData;
    private onTouchClickListener onTouchClickListener;
    private onTextClickListener textClick;
    private onCheckedChangeListener checkedChange;

    public ProviderAdapter(Context paramContext, List paramList, int paramInt) {
        data = paramList;
        context = paramContext;
        Type = paramInt;
        inflater = LayoutInflater.from(paramContext);
    }

    public ProviderAdapter(Context paramContext, Map paramMap, int paramInt) {
        data = getParentList(paramMap);
        mapData = paramMap;
        context = paramContext;
        Type = paramInt;
        inflater = LayoutInflater.from(paramContext);
    }

    private List getParentList(Map paramMap) {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(ContentProviderUtils.FLAG_RECENT);
        Iterator localIterator = paramMap.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            if (str.equals(ContentProviderUtils.FLAG_RECENT))
                continue;
            localArrayList.add(str);
        }
        return localArrayList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (Type) {
            case ContentProviderUtils.TYPE_AUDIO:
                holder = new ViewHolder.VideoViewHolder(inflater.inflate(R.layout.view_item_provider, parent, false));
                break;
            case ContentProviderUtils.TYPE_PHOTO:
                holder = new ViewHolder.ImageViewHolder(inflater.inflate(R.layout.view_item_provider, parent, false));
                break;
            case ContentProviderUtils.TYPE_TEXT:
                holder = new ViewHolder.TextViewHolder(inflater.inflate(R.layout.view_item_provider_text, parent, false));
                break;
            case ContentProviderUtils.TYPE_VIDEO:
                holder = new ViewHolder.VideoViewHolder(inflater.inflate(R.layout.view_item_provider, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (Type) {
            case ContentProviderUtils.TYPE_AUDIO:
                initAudioHolder(holder, position);
                break;
            case ContentProviderUtils.TYPE_PHOTO:
                initPhotoHolder(holder, position);
                break;
            case ContentProviderUtils.TYPE_TEXT:
                initTextHolder(holder, position);
                break;
            case ContentProviderUtils.TYPE_VIDEO:
                initVideoHolder(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }


    private void initAudioHolder(RecyclerView.ViewHolder paramViewHolder, final int position) {
        ViewHolder.VideoViewHolder localVideoViewHolder = (ViewHolder.VideoViewHolder) paramViewHolder;
        localVideoViewHolder.setTime(((AudioBean) data.get(position)).getDuration());
        localVideoViewHolder.setImageViewLinear(new View.OnClickListener() {
            public void onClick(View paramView) {
                onTouchClickListener.onClick(position);
            }
        });
    }

    private void initPhotoHolder(RecyclerView.ViewHolder paramViewHolder, final int position) {
        ViewHolder.ImageViewHolder localImageViewHolder = (ViewHolder.ImageViewHolder) paramViewHolder;
        final ImageBean localImageBean = (ImageBean) data.get(position);
        Glide.with(context).load(new File(localImageBean.getPath())).into(localImageViewHolder.getImageView());
        localImageViewHolder.getCheckBox().setChecked(mChecks.contains(localImageBean));
        localImageViewHolder.getCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedChange.onCheckedChanged(localImageBean);
            }
        });
        localImageViewHolder.setImageViewLinear(new View.OnClickListener() {
            public void onClick(View paramView) {
                onTouchClickListener.onClick(position);
            }
        });
    }

    private void initTextHolder(RecyclerView.ViewHolder paramViewHolder, int position) {
        ViewHolder.TextViewHolder localTextViewHolder = (ViewHolder.TextViewHolder) paramViewHolder;
        final String str = (String) data.get(position);
        if (str.equals(ContentProviderUtils.FLAG_RECENT)) {
            localTextViewHolder.setTextView("最近", mapData.get(data.get(position)).size());
        } else {
            localTextViewHolder.setTextView(ImageBean.getParentName(str), mapData.get(data.get(position)).size());
        }

        localTextViewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                textClick.onTextClick(str);
            }
        });
        ArrayList localArrayList = mapData.get(data.get(position));
        for (int i = 0; i < localTextViewHolder.getImageList().size(); i++) {
            if (i >= localArrayList.size()) {
                localTextViewHolder.getImageList().get(i).setVisibility(View.GONE);
            } else {
                Object localObject = localArrayList.get(i);
                if ((localObject instanceof ImageBean))
                    Glide.with(context).load(new File(((ImageBean) localObject).getPath())).into((ImageView) localTextViewHolder.getImageList().get(i));
                if ((localObject instanceof VideoBean))
                    if (((VideoBean) localObject).getVideoBitmap() != null)
                        (localTextViewHolder.getImageList().get(i)).setImageBitmap(((VideoBean) localObject).getVideoBitmap());
            }
        }
    }

    public void setChecks(ArrayList<Parcelable> mChecks) {
        this.mChecks = mChecks;
    }

    public void setCheckedChange(onCheckedChangeListener checkedChange) {

        this.checkedChange = checkedChange;
    }

    public void setTextClick(onTextClickListener textClick) {
        this.textClick = textClick;
    }

    public void setOnTouchClickListener(ProviderAdapter.onTouchClickListener onTouchClickListener) {
        this.onTouchClickListener = onTouchClickListener;
    }

    private void initVideoHolder(RecyclerView.ViewHolder paramViewHolder, final int position) {
        ViewHolder.VideoViewHolder localVideoViewHolder = (ViewHolder.VideoViewHolder) paramViewHolder;
        VideoBean localVideoBean = (VideoBean) data.get(position);

        localVideoViewHolder.setImageView(localVideoBean.getVideoBitmap());
        localVideoViewHolder.setTime(localVideoBean.getDuration());
        localVideoViewHolder.setImageViewLinear(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTouchClickListener.onClick(position);
            }
        });
    }


    public interface onCheckedChangeListener {
        void onCheckedChanged(Parcelable paramParcelable);
    }

    public interface onTextClickListener {
        void onTextClick(String paramString);
    }

    public interface onTouchClickListener {
        void onClick(int paramInt);
    }
}
