package com.greencabbage.patch.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.greencabbage.patch.util.ResourceUtils;

//import android.support.v4.app.Fragment;

public class GglAdFragment extends Fragment {

    OnAdErorListener mCallBack;
    Context applicationContext;
    int failCount = 0;

    public GglAdFragment() {
    }

    public static GglAdFragment newInstance() {
        GglAdFragment f = new GglAdFragment();
        Bundle b = new Bundle();
        // b.putCharSequence("layout_alignParentBottom", "true");
        f.setArguments(b);
        return f;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (OnAdErorListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAdErorListener");

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AdRequest adRequest = null;

        AdView mAdView = (AdView) getView().findViewById(ResourceUtils.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // TODO Auto-generated method stub
                super.onAdFailedToLoad(errorCode);
                mCallBack.onAdError("ggl");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(ResourceUtils.fragment_ad, container, false);
    }


    public interface OnAdErorListener {
        public void onAdError(String adProvider);
    }


}
