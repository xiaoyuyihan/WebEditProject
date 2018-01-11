package com.provider.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.utils.TypeConverUtil;
import com.webeditproject.R;

import java.util.ArrayList;

/**
 * Created by lw on 2017/11/30.
 */

public class ViewHolder {

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private ImageView imageView;

        public ImageViewHolder(View paramView) {
            super(paramView);
            imageView = (ImageView) paramView.findViewById(R.id.view_item_provider_image);
            checkBox = (CheckBox) paramView.findViewById(R.id.view_item_provider_image_check);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            paramView.findViewById(R.id.view_item_provider_time).setVisibility(View.GONE);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageViewLinear(View.OnClickListener paramOnClickListener) {
            imageView.setOnClickListener(paramOnClickListener);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        private ArrayList imageList = new ArrayList();
        private ImageView imageView_1;
        private ImageView imageView_2;
        private ImageView imageView_3;
        private ImageView imageView_4;
        private View itemView;
        private TextView textView;

        public TextViewHolder(View paramView) {
            super(paramView);
            itemView = paramView;
            textView = (TextView) paramView.findViewById(R.id.view_item_provider_text);
            imageView_1 = (ImageView) paramView.findViewById(R.id.view_item_provider_text_image_0);
            imageList.add(imageView_1);
            imageView_2 =(ImageView) paramView.findViewById(R.id.view_item_provider_text_image_1);
            imageList.add(imageView_2);
            imageView_3 = (ImageView) paramView.findViewById(R.id.view_item_provider_text_image_2);
            imageList.add(imageView_3);
            imageView_4 = (ImageView) paramView.findViewById(R.id.view_item_provider_text_image_3);
            imageList.add(imageView_4);
        }

        public ArrayList<ImageView> getImageList() {
            return imageList;
        }

        public View getItemView() {
            return itemView;
        }

        public void setTextView(String paramString, int paramInt) {
            textView.setText(paramString + "(" + paramInt + ")");
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView mTime;

        public VideoViewHolder(View paramView) {
            super(paramView);
            mTime = (TextView) paramView.findViewById(R.id.view_item_provider_time);
            imageView = (ImageView) paramView.findViewById(R.id.view_item_provider_image);
            paramView.findViewById(R.id.view_item_provider_image_check).setVisibility(View.GONE);
        }

        public void setImageView(Bitmap paramBitmap) {
            if (paramBitmap!=null){
                imageView.setImageBitmap(paramBitmap);
            }
        }

        public void setImageViewLinear(View.OnClickListener paramOnClickListener) {

            imageView.setOnClickListener(paramOnClickListener);
        }

        public void setTime(String time) {
            mTime.setText(TypeConverUtil.TimeMSToMin(time));
        }
    }
}
