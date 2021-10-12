package com.greencabbage.patch;

import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.greencabbage.patch.util.ResourceUtils;

public class Splash extends Activity {

	public static final String AppPreferences = "AppPreferences";

	@Override
	protected void onCreate(Bundle splashInstanceState) {
		
		super.onCreate(splashInstanceState);
		//setContentView(ResourceUtils.splash);

		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openStartingPoint = new Intent(getString(ResourceUtils.app_activity));
					PackageManager mPackageManager = getPackageManager();

					final List<ResolveInfo> activities =  mPackageManager.queryIntentActivities(openStartingPoint, 0);
					
					for (ResolveInfo info : activities) {
						String label = (String) info.activityInfo.loadLabel(mPackageManager);
						if (getString(ResourceUtils.app_name).equalsIgnoreCase(label)) {
							openStartingPoint.setPackage(info.activityInfo.packageName);
							startActivity(openStartingPoint);
						}
					}
				}
			} 
			
		};
		timer.start();
		//set app launched time to shared preferences
		SharedPreferences sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
		Long date_of_launch= sharedPreferences.getLong("date_of_launch",0);
		Long date_of_first_launch= sharedPreferences.getLong("date_of_first_launch",0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (date_of_launch == 0) {
			editor.putLong("date_of_launch", System.currentTimeMillis());
		}
		if (date_of_first_launch == 0) {
			editor.putLong("date_of_first_launch", System.currentTimeMillis());
		}
		//increment launch counter
		long launch_count = sharedPreferences.getLong("launch_count",0) + 1;
		long launch_count_shortcut = sharedPreferences.getLong("launch_count_shortcut",0) + 1;
		long launch_count_all = sharedPreferences.getLong("launch_count_all",0) + 1;
		editor.putLong("launch_count",launch_count);
		editor.putLong("launch_count_shortcut",launch_count_shortcut);
		editor.putLong("launch_count_all",launch_count_all);
		editor.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
