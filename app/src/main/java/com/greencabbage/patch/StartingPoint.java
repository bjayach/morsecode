package com.greencabbage.patch;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.greencabbage.patch.fragments.CustomAdFragment;
import com.greencabbage.patch.fragments.GglAdFragment;
import com.greencabbage.patch.util.AdUtils;
import com.greencabbage.patch.util.AppRater;
import com.greencabbage.patch.util.CabbageUtils;
import com.greencabbage.patch.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class StartingPoint extends AppCompatActivity implements GglAdFragment.OnAdErorListener, CustomAdFragment.OnAdErorListener, AppRater.OnDismissListener  {

    public static final String AppPreferences = "AppPreferences";
    public static final String ShortCut = "PowerfulFlashLightShortcut";
    int adFailCount = 0;
    boolean checkedGgl;
    boolean checkedCustom;
    int notificationId = ResourceUtils.activity_id;
    Button ibMinimize;
    ImageButton playPause;
    Button iViewMenu;
    Button muteButton;
    TextView tvBatteryLevel;
    int backPressCount = 0;
    Intent menuIntent = null;
    CabbageUtils cabbageUtils = new CabbageUtils();
    NotificationManager mNotificationManager = null;
    Drawable greyButton;
    Drawable blueButton;
    Drawable imgUnMuted;
    Drawable imgMuted;
    Parameters mParams;
    Toast toast = null;
    boolean cameraOn = false;
    boolean cameraPermissionGranted = false;
    Camera mCamera = null;
    MyContainerClass myContainerClass = null;
    boolean muted = true;
    SharedPreferences sharedPreferences;
    Activity mActivity = this;
    File applicationCacheDir;
    Size smallestSize = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.activity_main);

        //get buttons
        greyButton = getResources().getDrawable(
                ResourceUtils.grey_button);
        blueButton = getResources().getDrawable(
                ResourceUtils.blue_button);

        //check build and initialize camera
        if (Build.VERSION.SDK_INT < 23) {
            // for light
            try {
                if (mCamera == null) {
                    mCamera = Camera.open();
                }
                mParams = mCamera.getParameters();
                this.startCamera();
                cameraOn = true;
            } catch (Exception e1) {
            }
        } else {//version 23 or higher
            try {
                myContainerClass = new MyContainerClass();
                myContainerClass.turnNewCameraOn();
            } catch (Exception e) {
                restartActivity();
            }

        }

        final Context context = getApplicationContext();
        sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
        applicationCacheDir = context.getCacheDir();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        imgUnMuted = getResources().getDrawable(
                ResourceUtils.unmuted);
        imgMuted = getResources().getDrawable(
                ResourceUtils.muted);


        // Initialize all
        ibMinimize = findViewById(ResourceUtils.ibMinimize);

        // Setup listener
        ibMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
            }
        });

        muteButton = findViewById(ResourceUtils.muteButton);

        final ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_DTMF, 100);

        // Setup listener
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (muted) {
                    muted = false;
                } else {
                    muted = true;
                }

                if (muted) {
                    muteButton.setBackground(imgMuted);
                } else {
                    muteButton.setBackground(imgUnMuted);
                }
            }
        });

        // Image play-pause
        playPause = findViewById(ResourceUtils.ibTorchOnOff);

        final MyContainerClass finalMyContainerClass = myContainerClass;

        if (cameraOn) {
            playPause.setImageDrawable(blueButton);
        } else {
            playPause.setImageDrawable(greyButton);
        }

        playPause.setSoundEffectsEnabled(true);

        playPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performClickAction(toneG, finalMyContainerClass, context);
                showBatteryLevel(context);

            }
        });

        if (findViewById(ResourceUtils.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            loadGglFragment();
        }
        //end ad fragments


        // Image menu
        iViewMenu = findViewById(ResourceUtils.ibMenu);

        iViewMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    menuIntent = new Intent(context, Class
                            .forName("com.greencabbage.patch.activity.Menu"));

                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }

                ResolveInfo rInfo = cabbageUtils.getResolveInfoForActivity(
                        context, menuIntent);
                if (rInfo != null) {
                    menuIntent.setPackage(rInfo.activityInfo.packageName);
                }

                // startActivity(menuIntent);
                startActivityForResult(menuIntent, ResourceUtils.activity_id);
            }
        });

        //showBatteryLevel
        showBatteryLevel(context);

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //ad fragments

        //New notificatin
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(ResourceUtils.ic_notification_image)
                .setContentTitle("Torch light");
        //Intent resultIntent = new Intent(context, MainActivity.class);
        Intent notificationIntent = new Intent(context, StartingPoint.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                ResourceUtils.activity_id, notificationIntent, 0);
        builder.setProgress(100, 0, false);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, builder.build());
        //end new notificcation

    }


    private void addHomeShortcut() {

        //Add shortcut to homescreen
        boolean shortCutTobeAdded = false;
        boolean versionHigherThan26 = false;

        try {
            if (!sharedPreferences.getBoolean(ShortCut, false)) {

                if (Build.VERSION.SDK_INT > 23 && Build.VERSION.SDK_INT <26 ) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INSTALL_SHORTCUT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, 123);
                    } else {
                        shortCutTobeAdded = true;
                    }
                } else if (Build.VERSION.SDK_INT >= 26 ) {
                    versionHigherThan26 = true;
                } else {
                    shortCutTobeAdded = true;
                }
                if (shortCutTobeAdded) {
                    addShortCut();
                    setShortcutToPreferences();
                } else if (versionHigherThan26) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        ShortcutManager manager = getApplicationContext().getSystemService(ShortcutManager.class);
                        List<ShortcutInfo> l = manager.getPinnedShortcuts();
                        boolean scLabelFound = false;
                        for (ShortcutInfo sci:l) {
                            String scLabel = sci.getShortLabel().toString();
                            if (scLabel != null && getString(ResourceUtils.app_name).equals(scLabel)) {
                                setShortcutToPreferences();
                                scLabelFound = true;
                            }
                        }
                        if (!scLabelFound){
                            long launch_count_shortcut = sharedPreferences.getLong("launch_count_shortcut",0);
                            if (launch_count_shortcut > 25 || launch_count_shortcut < 2) {
                                setversionHigherThan26Shortcut();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putLong("launch_count_shortcut", 2);
                                editor.commit();
                            }
                        }
                    }
                }
           }
        } catch (Exception e) {
        }
    }


    //https://medium.com/@ssaurel/create-a-morse-code-converter-application-for-android-d28bbccc7257
    //unit - define as 300 ms
    //dot is 1 unit
    //dash is 3 unit
    //space between part of same letter is 1 unit
    //space between letters is 3 units
    //space between words is seven units

    private void startMorseSignalling(String code) {
        //get morse code as 1s and 0s array. 1-dash. 0-dot

        ArrayList arrayList = new ArrayList();
        arrayList.add("0");
        arrayList.add("0");
        arrayList.add("0");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");
        arrayList.add("1");

        //sos
        //... --- ...
        //1a1b1a1b1a3b3a1b3a1b3a1b3a3b1a1b1a1b1a7b
        for (int i=0; i<arrayList.size(); i++) {
            //each peri
            //if 0 flash for 300 ms
            //if 1 flash for 3 seconds

        }
    }
    private void setShortcutToPreferences() {
        //update sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ShortCut, true);
        editor.commit();
    }

    private void performClickAction(ToneGenerator toneG, MyContainerClass finalMyContainerClass, Context context) {
        if (!muted) {
            toneG.startTone(ToneGenerator.TONE_DTMF_1, 25);
        }
        if (Build.VERSION.SDK_INT < 23) {
            if (cameraOn) {
                if (mCamera != null) {
                    mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(mParams);
                    mCamera.stopPreview();
                    cameraOn = false;
                }
            } else {
                if (mCamera != null) {
                    mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(mParams);
                    mCamera.startPreview();
                    cameraOn = true;
                }
            }
            if (cameraOn) {
                playPause.setImageDrawable(blueButton);
            } else {
                playPause.setImageDrawable(greyButton);
            }
        } else {//version > 23
            if (cameraOn) {
                finalMyContainerClass.turnNewCameraOff();
            } else {
                try {
                    finalMyContainerClass.turnNewCameraOn();
                } catch (CameraAccessException e) {
                    restartActivity();
                }
            }

        }

    }

    private void checkAndshowRateDialog() {
        int DAYS_UNTIL_PROMPT = 10;
        int LAUNCH_COUNT_UNTIL_PROMPT = 10;

        boolean dontShowDialog = false;
        if (sharedPreferences.getBoolean("dontshowagain",false)) {
            dontShowDialog = true;
        }
        long launch_count = sharedPreferences.getLong("launch_count",0);
        if (launch_count < LAUNCH_COUNT_UNTIL_PROMPT) {
            dontShowDialog = true;
        }
        long date_of_first_launch = sharedPreferences.getLong("date_of_launch",0);

        if (!dontShowDialog && System.currentTimeMillis() > (date_of_first_launch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))) {
            dontShowDialog = false;
        } else {
            dontShowDialog = true;
        }

        if (dontShowDialog) {
            goToHome();
        } else {
            AppRater apprater = new AppRater();
            apprater.showRateDialog(StartingPoint.this);
            setLaunchCount(0,0);
        }

    }

    private void setLaunchCount(long launch_count_rate, long date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("launch_count_rate", launch_count_rate);
        editor.putLong("date_of_first_launch", date);
        editor.commit();
    }

    @Override
    public void onDismiss(String value) {
        if ("never_ask_again".equals(value) || "rated_app".equals(value)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dontshowagain", true);
            editor.apply();
        }
        goToHome();
    }

    private void goToHome() {
        showHome();
        if (Build.VERSION.SDK_INT < 23) {
            exitApp();
        } else {
            myContainerClass.exitApp1();
        }
    }

    private void loadGglFragment() {
        checkedGgl = true;
        int DAYS_UNTIL_PROMPT = 1;
        long date_of_first_launch = sharedPreferences.getLong("date_of_first_launch", 0);
        if (System.currentTimeMillis() < (date_of_first_launch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))) {
            return;
        }
        try {
            if (findViewById(ResourceUtils.fragment_container) != null) {

                GglAdFragment gglAdFragment = GglAdFragment.newInstance();
                gglAdFragment.setFailCount(adFailCount);
                gglAdFragment.setApplicationContext(getApplicationContext());
                gglAdFragment.setArguments(getIntent().getExtras());

                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(ResourceUtils.fragment_container, gglAdFragment);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace(); -- Ignore exception
        }
        // End Fragments
    }

    private void loadCustomFragment() {
        checkedCustom = true;
        int DAYS_UNTIL_PROMPT = 1;
        long date_of_first_launch = sharedPreferences.getLong("date_of_first_launch", 0);
        if (System.currentTimeMillis() < (date_of_first_launch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))) {
            return;
        }
        try {
            if (findViewById(ResourceUtils.fragment_container) != null) {

                CustomAdFragment customAdFragment = CustomAdFragment.newInstance();
                customAdFragment.setArguments(getIntent().getExtras());
                customAdFragment.setCacheDir(applicationCacheDir);

                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(ResourceUtils.fragment_container, customAdFragment);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace(); -- Ignore exception
        }
        // End Fragments
    }

    @Override
    public void onAdError(String adProvider) {
        adFailCount++;
        if ("ggl".equalsIgnoreCase(adProvider)) {
            if (adFailCount < 3) {
                loadGglFragment();
            } else {
                loadCustomFragment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean canUseCamera = false;

        switch (requestCode) {

            case (11): {
                canUseCamera = false;
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseCamera = true;
                    cameraPermissionGranted = true;
                    try {
                        myContainerClass.turnNewCameraOn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!canUseCamera) {
                    cameraOn = false;
                    Toast.makeText(getApplicationContext(), "Cannot turn on camera flashlight without requested permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 12: {
                canUseCamera = false;
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseCamera = true;
                    cameraPermissionGranted = true;
                    try {
                        if (cameraOn) {
                            myContainerClass.turnNewCameraOff();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!canUseCamera) {
                    cameraOn = false;
                    Toast.makeText(getApplicationContext(), "Cannot turn on camera flashlight without requested permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 123: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //update sharedPreferences
                    setShortcutToPreferences();
                    addShortCut();
                }
                break;
            }
        }
    }

    private void restartActivity() {
        finish();
        startActivity(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setversionHigherThan26Shortcut() {
        try {
            Intent shortCutIntent = new Intent(this, Splash.class);
            shortCutIntent.setAction(Intent.ACTION_MAIN);
            ShortcutManager manager = getApplicationContext().getSystemService(ShortcutManager.class);
            Icon icon = Icon.createWithResource(StartingPoint.this,ResourceUtils.ic_launcher);
            ShortcutInfo pinShortcutInfo =
                    new ShortcutInfo.Builder(getApplicationContext(), "shortcutID")
                            .setIcon(icon)
                            .setShortLabel(getString(ResourceUtils.app_name))
                            .setIntent(shortCutIntent)
                            .build();
            if (manager != null) {
                if(manager.isRequestPinShortcutSupported()) {
                 manager.requestPinShortcut(pinShortcutInfo, null);
                }
            }



        } catch (Exception e) {
            //e.printStackTrace();

        }
    }
    private void addShortCut() {
        //remove if shortcut already exists
        try {
            removeShortCut();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent shortCutIntent = new Intent(StartingPoint.this, Splash.class);
            shortCutIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortCutIntent.setAction(Intent.ACTION_MAIN);

            Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(StartingPoint.this, ResourceUtils.ic_launcher);
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(ResourceUtils.app_name));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("duplicate", false);
            sendBroadcast(addIntent);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void removeShortCut() {

        try {
            Intent removeShortCutIntent = new Intent(StartingPoint.this, Splash.class);
            removeShortCutIntent.setAction(Intent.ACTION_MAIN);

            Intent removeIntent = new Intent();
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, removeShortCutIntent);
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(ResourceUtils.app_name));
            removeIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            sendBroadcast(removeIntent);
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }


    private void showBatteryLevel(final Context context) {
        //Battery level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale * 100;
        tvBatteryLevel = findViewById(ResourceUtils.tvBatteryLevel);
        tvBatteryLevel.setText("Battery Level " + Math.round(batteryPct) + " %");

        if (Math.round(batteryPct) < 25) {
            tvBatteryLevel.setTextColor(Color.RED);
        } else {
            tvBatteryLevel.setTextColor(Color.GREEN);
        }
    }

    private void showHome() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    private void exitApp() {
        if (Build.VERSION.SDK_INT < 23) {
            if (mCamera != null) {
                //Parameters params = mCamera.getParameters();
                mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(mParams);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        cameraOn = false;
        AdUtils.clearAd();
        finishActivity(ResourceUtils.activity_id);
        finish();
        mNotificationManager.cancel(notificationId);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    void startCamera() {
        if (mCamera != null) {
            mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
        }
    }

    @Override
    public void onBackPressed() {
        backPressCount++;
        //Add Home shortcut
        addHomeShortcut();
        if (backPressCount == 1) {
            toast = Toast.makeText(getApplicationContext(), "Press once again to exit", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (backPressCount > 1) {
            checkAndshowRateDialog();
        }
    }

    @TargetApi(23)
    class MyContainerClass {
        CameraManager mCameraManager = null;
        CameraDevice mNewCamera = null;
        CaptureRequest.Builder mCaptureRequestBuilder;
        CameraCaptureSession mCameraCaptureSession;
        SurfaceTexture mSurfaceTexture;
        Surface mSurface;
        List<Surface> mSurfaceList = null;

        @TargetApi(23)
        private void exitApp1() {

            if (mCameraCaptureSession != null) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
            if (mNewCamera != null) {
                mNewCamera.close();

                mNewCamera = null;
            }

            cameraOn = false;
            AdUtils.clearAd();
            finishActivity(ResourceUtils.activity_id);
            finish();
            mNotificationManager.cancel(notificationId);
        }

        @TargetApi(23)
        private void configureCamera(CameraDevice cameraDevice) {
            mNewCamera = null;
            mNewCamera = cameraDevice;
            try {
                cameraOn = true;
                playPause.setImageDrawable(blueButton);
                mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
                //default on
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                mSurfaceList = new ArrayList<Surface>();
                mSurfaceTexture = new SurfaceTexture(1);
                if (smallestSize == null) {
                    smallestSize = getSmallestSize(mNewCamera.getId());
                }
                mSurfaceTexture.setDefaultBufferSize(smallestSize.getWidth(), smallestSize.getHeight());
                mSurface = new Surface(mSurfaceTexture);

                mSurfaceList.add(mSurface);
                mCaptureRequestBuilder.addTarget(mSurface);
                mNewCamera.createCaptureSession(mSurfaceList, new MyCameraCaptureSessionCallback(), null);
            } catch (CameraAccessException e) {
                // e.printStackTrace();
            }
        }

        @TargetApi(23)
        Size getSmallestSize(String id) throws CameraAccessException {
            Size[] outputSizes = mCameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .getOutputSizes(SurfaceTexture.class);
            if (outputSizes == null || outputSizes.length == 0) {
                throw new IllegalStateException("Camera " + id + " doesn't support output size");
            }
            Size chosen = outputSizes[0];
            for (int i = 0; i < outputSizes.length; i++) {
                Size s = outputSizes[i];
                if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight()) {
                    chosen = s;
                }
            }
            return chosen;
        }

        @TargetApi(23)
        private void turnNewCameraOff() {
            if (!cameraPermissionGranted && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, 12);
            }
            playPause.setImageDrawable(greyButton);
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            cameraOn = false;
            try {
                mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
                mNewCamera.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        @TargetApi(23)
        private void turnNewCameraOn() throws CameraAccessException {

                if (!cameraPermissionGranted && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, 11);
                }
                initNewerCamera();

        }

        @TargetApi(23)
        private void initNewerCamera() throws CameraAccessException {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraManager.openCamera("0", new MyNewCameraStateCallBack(), null);

            } catch (SecurityException e) {
                //e.printStackTrace();
            }
        }

        @TargetApi(23)
        class MyNewCameraStateCallBack extends CameraDevice.StateCallback {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                configureCamera(cameraDevice);
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
            }

            @Override
            public void onError(CameraDevice cameraDevice, int i) {
            }
        }

        @TargetApi(23)
        class MyCameraCaptureSessionCallback extends CameraCaptureSession.StateCallback {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                mCameraCaptureSession = cameraCaptureSession;

                try {
                    mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
                } catch (Exception e) {
                    restartActivity();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            }
        }
    }
}
