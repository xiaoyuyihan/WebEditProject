package com.utils;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.webeditproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lw on 2018/1/4.
 */

public class MessageDialogFragment extends DialogFragment {

    @BindView(R.id.dialog_message_text)
    TextView mMessageView;
    @BindView(R.id.dialog_message_cancel)
    Button mCancelBut;
    @BindView(R.id.dialog_message_sure)
    Button mSureBut;

    String msg;

    public OnMsgDialogClickListener onMsgDialogClickListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dialog_message,container,false);
        ButterKnife.bind(this,view);
        if (msg!=null)
            mMessageView.setText(msg);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.shape_html_top));
        getDialog().getWindow().getDecorView().setPadding(0,0,0,0);
    }

    @OnClick(R.id.dialog_message_cancel)
    public void onCancel(View v){
        if (onMsgDialogClickListener!=null)
            onMsgDialogClickListener.onCancel();
    }

    public void setOnMsgDialogClickListener(OnMsgDialogClickListener onMsgDialogClickListener) {
        this.onMsgDialogClickListener = onMsgDialogClickListener;
    }

    @OnClick(R.id.dialog_message_sure)
    public void onSure(View view){
        if (onMsgDialogClickListener!=null)
            onMsgDialogClickListener.onSureBut();

    }

    public void setMessage(String msg){
        this.msg=msg;
    }

    public interface OnMsgDialogClickListener{
        void onSureBut();
        void onCancel();
    }
}
