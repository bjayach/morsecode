package com.greencabbage.patch.data;


import android.graphics.Bitmap;

public class Banner {

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String package_name) {
        this.packageName = package_name;
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    Bitmap bitmap1;
    Bitmap bitmap2;
    String packageName;

    public String getBannerLinkURL() {
        return bannerLinkURL;
    }

    public void setBannerLinkURL(String bannerLinkURL) {
        this.bannerLinkURL = bannerLinkURL;
    }

    String bannerLinkURL;

}
