package com.greencabbage.patch.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.greencabbage.patch.data.Banner;
import com.greencabbage.patch.util.async.AsyncGetBitmapFromUrl;
import com.greencabbage.patch.util.async.AsyncGetFileFromUrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CabbageUtils {
    private String cookieFileUrl = "https://drive.google.com/uc?id=0BwXrOEAZFj_acUtBb1Y2WFdMbVE&authuser=0&export=download";

    public ResolveInfo getResolveInfoForActivity(Context context, Intent intent) {
        PackageManager mPackageManager = context.getPackageManager();
        final List<ResolveInfo> activities = mPackageManager
                .queryIntentActivities(intent, 0);

        for (ResolveInfo info : activities) {
            String label = (String) info.activityInfo
                    .loadLabel(mPackageManager);
            if (context.getString(ResourceUtils.app_name).equalsIgnoreCase(label)) {
                return info;
            }
        }
        return null;
    }

    public Banner getRandomCustomBanner(File cacheDir) {
        Banner banner = new Banner();
        Bitmap bitmapBanner1 = null;
        Bitmap bitmapBanner2 = null;
        File bannerDir = new File(cacheDir.toString() + "/banners");

        String bannerUrl = getRandomBannerUrlFromCookie(cacheDir);
        if (bannerUrl == null) {
            return null;
        }

        String[] bannerArrRaw;
        String[] bannerImageUrlArr;
        String bannerRawUrl;
        String bannerUrl1 = null;
        String bannerUrl2 = null;
        String bannerFileId2;
        File bannerFile2 = null;
        if (bannerUrl.contains("~")) {
            bannerArrRaw = bannerUrl.split("~");
            if (bannerArrRaw != null) {
                banner.setPackageName(bannerArrRaw[0]);
                bannerRawUrl = bannerArrRaw[1];
                if (bannerRawUrl.contains("^")) {
                    bannerImageUrlArr = bannerRawUrl.split("\\^");
                    bannerUrl1 = bannerImageUrlArr[0];
                    bannerUrl2 = bannerImageUrlArr[1];
                } else {
                    bannerUrl1 = bannerRawUrl;
                }
            }
        }
        String bannerFileId1 = parseUrl(bannerUrl1);
        if (bannerUrl2 != null) {
            bannerFileId2 = parseUrl(bannerUrl2);
            bannerFile2 = new File(bannerDir, bannerFileId2);
        }

        if (!bannerDir.exists()) {
            bannerDir.mkdirs();
        }
        File bannerFile1 = new File(bannerDir, bannerFileId1);

        if (bannerFile1.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmapBanner1 = BitmapFactory.decodeFile(bannerFile1.toString(), bmOptions);
            banner.setBitmap1(bitmapBanner1);
        } else {

            //get bm1
            AsyncGetBitmapFromUrl asyncGetBitmapFromUrl1 = new AsyncGetBitmapFromUrl();
            asyncGetBitmapFromUrl1.execute(bannerUrl1);

            try {
                bitmapBanner1 = asyncGetBitmapFromUrl1.get();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            } catch (ExecutionException e) {
                //e.printStackTrace();
            }

            try {
                if (bitmapBanner1 != null) {
                    FileOutputStream out = new FileOutputStream(bannerFile1);
                    bitmapBanner1.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();

                }

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            banner.setBitmap1(bitmapBanner1);
        }
        //get bm2
        if (bannerFile2 != null && bannerFile2.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmapBanner2 = BitmapFactory.decodeFile(bannerFile2.toString(), bmOptions);
            banner.setBitmap2(bitmapBanner2);
        } else if (bannerFile2 != null){
            AsyncGetBitmapFromUrl asyncGetBitmapFromUrl2 = new AsyncGetBitmapFromUrl();
            asyncGetBitmapFromUrl2.execute(bannerUrl2);

            try {
                bitmapBanner2 = asyncGetBitmapFromUrl2.get();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            } catch (ExecutionException e) {
                //e.printStackTrace();
            }

            try {
                if (bitmapBanner2 != null) {
                    FileOutputStream out = new FileOutputStream(bannerFile2);
                    bitmapBanner2.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                }

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            banner.setBitmap2(bitmapBanner2);
        }

        return banner;
    }

    private String parseUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.substring(url.indexOf("id="), url.indexOf("&authuser"));
    }

    private String getRandomBannerUrlFromCookie(File cacheDir) {
        return getDataFromCookie(cacheDir, "");
    }
    public String getDataFromCookie(File cacheDir, String key) {

        final long MAXFILEAGE = 2678400000L;//1 month

        File bannerDir = new File(cacheDir.toString() + "/banners");
        File lockFile = new File(bannerDir, "lockFile");

        File cookieFile = new File(bannerDir, parseUrl(cookieFileUrl));
        try {
            if (!lockFile.exists()) {
                bannerDir.mkdirs();
                lockFile.createNewFile();
            } else {
                Long lastModified = lockFile.lastModified();
                if (lastModified + MAXFILEAGE < System.currentTimeMillis()) {
                    deleteDirAndContents(bannerDir);
                    bannerDir.mkdirs();
                    lockFile.createNewFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!cookieFile.exists()) {
            AsyncGetFileFromUrl asyncGetFileFromUrl = new AsyncGetFileFromUrl();
            asyncGetFileFromUrl.execute(cookieFileUrl, cookieFile.toString());
            try {
                cookieFile = asyncGetFileFromUrl.get();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            } catch (ExecutionException e) {
                //e.printStackTrace();
            }
        }

        if (cookieFile == null) {
            return null;
        }
        String line;
        List<String> inputFileArray = new ArrayList<String>();
        String returnLine = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cookieFile)));
            while ((line = br.readLine()) != null) {
                if (!line.contains("com.properties.key")) {
                    inputFileArray.add(line);
                } else {//load the property
                    if (line.contains("com.properties.key."+key)) {
                        returnLine = line.substring(line.indexOf("~")+1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        if (key == null || key.length() <=0) {
            //get random
            int randomValue = random(0, inputFileArray.size() - 1);
            returnLine = inputFileArray.get(randomValue);
        }

        return returnLine;
    }

    public void deleteDirAndContents(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i< children.length; i++) {
                new File(dir,children[i]).delete();
            }
        }
    }
    public static int random(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
}
