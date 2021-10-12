package com.greencabbage.patch.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.greencabbage.patch.data.Banner;
import com.greencabbage.patch.util.CabbageUtils;
import com.greencabbage.patch.util.ResourceUtils;

import java.io.File;

//import android.support.v4.app.Fragment;

public class CustomAdFragment extends Fragment {
    OnAdErorListener mCallBack;
    private File cacheDir;

    public CustomAdFragment() {
	}

    public void setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
    }


    public interface OnAdErorListener {
		public void onAdError(String adProvider);
	}
	
	public static CustomAdFragment newInstance() {
        CustomAdFragment f = new CustomAdFragment();
        Bundle b = new Bundle();
       // b.putCharSequence("layout_alignParentBottom", "true");
        f.setArguments(b);
        return f;
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

        if (cacheDir == null) {
            return;
        }

        ImageButton customBannerButton = (ImageButton)getView().findViewById(ResourceUtils.adViewCustom);


        CabbageUtils cabbageUtils = new CabbageUtils();
        Bitmap bannerBitmap1 = null;
        Bitmap bannerBitmap2 = null;
        String packageName = null;
        Banner banner = cabbageUtils.getRandomCustomBanner(cacheDir);
        if (banner != null) {
            bannerBitmap1 = banner.getBitmap1();
            bannerBitmap2 = banner.getBitmap2();
            packageName =banner.getPackageName();
        }

        boolean showDeveloperPage = false;
        if (banner == null || bannerBitmap1 == null || bannerBitmap1.getHeight() < 10 || bannerBitmap1.getWidth() < 10) {
            customBannerButton.setBackground(getResources().getDrawable(ResourceUtils.default_banner));
        } else {
            customBannerButton.setBackgroundResource(ResourceUtils.banner_animation1);
            AnimationDrawable bannerAnimationDrawable = (AnimationDrawable)customBannerButton.getBackground();
            bannerAnimationDrawable.mutate();
            if (bannerAnimationDrawable.isRunning()) {
                bannerAnimationDrawable.stop();
            }
            Drawable draw1 = new BitmapDrawable(getResources(),bannerBitmap1);

            bannerAnimationDrawable.addFrame(draw1,2500);
            if (bannerBitmap2 != null && bannerBitmap2.getHeight() > 10) {
                Drawable draw2 = new BitmapDrawable(getResources(),bannerBitmap2);
                bannerAnimationDrawable.addFrame(draw2,2500);
            }
            bannerAnimationDrawable.start();
        }

        if (packageName == null || packageName.length() <= 0) {
            showDeveloperPage = true;
            packageName = "bcanproducts@gmail.com";
        }
        final String finalPackageName = packageName;
        final boolean finalShowDeveloperPage = showDeveloperPage ;

		customBannerButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
                try {
                    if (finalShowDeveloperPage) {
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q="+finalPackageName)));
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + finalPackageName)));
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(ResourceUtils.fragment_ad_custom, container, false);

	}
	
}
