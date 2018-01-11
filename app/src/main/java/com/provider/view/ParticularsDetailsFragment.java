package com.provider.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.provider.utils.ContentProviderUtils;
import com.webeditproject.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lw on 2017/11/30.
 */

public class ParticularsDetailsFragment extends Fragment {

    public static final String FRAGMENT_ARGUMENT_CHECK_DATA = "PARTICULARS_FRAGMENT_ARGUMENT_NAME";
    public static final String FRAGMENT_ARGUMENT_DATA = "PARTICULARS_FRAGMENT_ARGUMENT_DATA";
    public static final String FRAGMENT_ARGUMENT_TYPE = "PARTICULARS_FRAGMENT_ARGUMENT_TYPE";

    public static final String RESULT_DATA = "result_data";

    private ArrayList<Parcelable> mChecks;
    private ArrayList mData;
    private Map mMapData = new ArrayMap();
    private int mType;

    private ProviderAdapter providerAdapter;
    private RecyclerView recyclerView;


    private ProviderAdapter.onTextClickListener onTextClickListener;
    private ProviderAdapter.onCheckedChangeListener onCheckedChangeListener;
    private ProviderAdapter.onTouchClickListener onTouchClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_particulars_details, container, false);
        mType = getArguments().getInt(FRAGMENT_ARGUMENT_TYPE);
        if (mType == ContentProviderUtils.TYPE_TEXT) {
            initLinear();
        } else {
            initGrid();
        }
        recyclerView.setAdapter(providerAdapter);
        return recyclerView;
    }

    public void setOnTouchClickListener(ProviderAdapter.onTouchClickListener onTouchClickListener) {
        this.onTouchClickListener = onTouchClickListener;
    }

    private void initGrid() {
        mData = getArguments().getParcelableArrayList(FRAGMENT_ARGUMENT_DATA);
        mChecks = getArguments().getParcelableArrayList(FRAGMENT_ARGUMENT_CHECK_DATA);
        providerAdapter = new ProviderAdapter(getActivity(), mData, mType);
        GridLayoutManager localGridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(localGridLayoutManager);
        providerAdapter.setCheckedChange(onCheckedChangeListener);
        providerAdapter.setOnTouchClickListener(onTouchClickListener);
        providerAdapter.setChecks(mChecks);
    }

    private void initLinear() {
        getBundleMap();
        providerAdapter = new ProviderAdapter(getActivity(), mMapData, mType);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(localLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        providerAdapter.setTextClick(onTextClickListener);
    }

    public void getBundleMap() {
        Bundle localBundle = getArguments();
        Iterator localIterator = localBundle.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            if (str.equals(FRAGMENT_ARGUMENT_TYPE))
                continue;
            mMapData.put(str, localBundle.getParcelableArrayList(str));
        }
    }

    public void setOnCheckedChangeListener(ProviderAdapter.onCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setUpdateChecks(ArrayList<Parcelable> mChecks) {
        this.mChecks = mChecks;
        providerAdapter.notifyDataSetChanged();
    }

    public void setOnTextClickListener(ProviderAdapter.onTextClickListener onTextClickListener) {
        this.onTextClickListener = onTextClickListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mChecks = data.getExtras().getParcelableArrayList(RESULT_DATA);
    }
}
