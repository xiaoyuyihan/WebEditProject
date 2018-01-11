package com.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

/**
 * Created by lw on 2018/1/6.
 */

public interface OnClickViewHolder {
   void onClick(RecyclerView.ViewHolder holder);
   interface OnBroadcastReceiverListener{
      void onReceiver(Intent intent);
   }
}

