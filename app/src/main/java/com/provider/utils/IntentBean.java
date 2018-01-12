package com.provider.utils;

import android.content.Intent;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by lw on 2017/12/19.
 */

public class IntentBean {
    private static IntentBean bean;

    public static IntentBean getInstance() {
        if (bean==null)
            bean=new IntentBean();
        return bean;
    }

    private ArrayList<Parcelable> mData=new ArrayList<>();
    private ArrayList<Parcelable> mChecks=new ArrayList<>();
    private int position;

    public ArrayList<Parcelable> getData() {
        return mData;
    }

    public IntentBean setData(ArrayList<Parcelable> mData) {
        this.mData = mData;
        return bean;
    }

    public ArrayList<Parcelable> getChecks() {
        return mChecks;
    }

    public IntentBean setChecks(ArrayList<Parcelable> mChecks) {
        this.mChecks = mChecks;
        return bean;
    }

    public int getPosition() {
        return position;
    }

    public IntentBean setPosition(int position) {
        this.position = position;
        return bean;
    }

    public IntentBean addCheck(Parcelable check){
        mChecks.add(check);
        return bean;
    }
}
