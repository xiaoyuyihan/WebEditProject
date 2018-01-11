package com.webedit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.webeditproject.R;

/**
 * Created by lw on 2017/12/18.
 */

public class HyperlinksFragment extends DialogFragment implements View.OnClickListener {
    private View contentView;

    EditText keyText;
    EditText valueText;
    TextView subject;
    TextView back;

    private OnClickSubject onClickSubject;

    public void setOnClickSubject(OnClickSubject onClickSubject) {
        this.onClickSubject = onClickSubject;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_dialog_hyperlinks, container, false);
        findView();
        return contentView;
    }

    private void findView() {
        keyText = contentView.findViewById(R.id.dialog_hyperlinks_key);
        valueText = contentView.findViewById(R.id.dialog_hyperlinks_value);
        subject = contentView.findViewById(R.id.dialog_hyperlinks_subject);
        subject.setOnClickListener(this);
        back = contentView.findViewById(R.id.dialog_hyperlinks_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_hyperlinks_subject:
                subjectHyperlink();
                break;
            case R.id.dialog_hyperlinks_back:
                getDialog().dismiss();
                break;
        }
    }

    private void subjectHyperlink() {
        String key=keyText.getText().toString();
        String value=valueText.getText().toString();
        if (key.equals("")||key.contains(" ")||
                !(value.substring(0,7).equals("http://")||value.substring(0,8).equals("https://"))){
            Toast.makeText(getActivity(),"超链接格式不对，请仔细检查！",Toast.LENGTH_SHORT).show();
        }else
            onClickSubject.subjectHyperlinks(key,value);
    }

    public interface OnClickSubject {
        void subjectHyperlinks(String key, String value);
    }
}
