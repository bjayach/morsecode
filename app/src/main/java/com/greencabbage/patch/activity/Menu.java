package com.greencabbage.patch.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.greencabbage.patch.StartingPoint;
import com.greencabbage.patch.fragments.CustomAdFragment;
import com.greencabbage.patch.fragments.GglAdFragment;
import com.greencabbage.patch.util.AppRater;
import com.greencabbage.patch.util.CabbageUtils;
import com.greencabbage.patch.util.ResourceUtils;

import java.io.File;

public class Menu extends AppCompatActivity implements GglAdFragment.OnAdErorListener,CustomAdFragment.OnAdErorListener  {

	TextView tvMenuDisclaimer, tvMenuFeedback, tvMenuShare, tvMenuRate;

	CabbageUtils cabbageUtils = new CabbageUtils();
	boolean checkedGgl;
	boolean checkedCustom;

	File applicationCacheDir;
	public static final String AppPreferences = "AppPreferences";
	SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.menu);

		final Context context = getApplicationContext();
        applicationCacheDir = context.getCacheDir();

		sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);

		// Launch RemoveAd activity
		tvMenuDisclaimer = (TextView) findViewById(ResourceUtils.tvMenuDisclaimer);

		tvMenuDisclaimer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start Activity RemoveAd
				Intent disclaimerIntent = new Intent(
						"com.greencabbage.patch.activity.DISCLAIMER");
				ResolveInfo rInfo = cabbageUtils.getResolveInfoForActivity(
						context, disclaimerIntent);
				if (rInfo != null) {
					
					disclaimerIntent.setPackage(rInfo.activityInfo.packageName);
				}
				//startActivity(disclaimerIntent);
				startActivityForResult(disclaimerIntent,ResourceUtils.activity_id);
			}
		});

		// Launch Feedback activity
		tvMenuFeedback = (TextView) findViewById(ResourceUtils.tvMenuFeedback);

		tvMenuFeedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start Activity Feedback
				Intent feedbackIntent = new Intent(
						"com.greencabbage.patch.activity.FEEDBACK");
				//feedbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ResolveInfo rInfo = cabbageUtils.getResolveInfoForActivity(
						context, feedbackIntent);
				if (rInfo != null) {
					feedbackIntent.setPackage(rInfo.activityInfo.packageName);
				}
				//startActivity(feedbackIntent);
				startActivityForResult(feedbackIntent,ResourceUtils.activity_id);
			}
		});

		// Launch Share activity
		tvMenuShare = (TextView) findViewById(ResourceUtils.tvMenuShare);

		tvMenuShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start Activity Share
				Intent shareIntent = new Intent(
						"com.greencabbage.patch.activity.SHARE");
				//shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ResolveInfo rInfo = cabbageUtils.getResolveInfoForActivity(
						context, shareIntent);
				if (rInfo != null) {
					shareIntent.setPackage(rInfo.activityInfo.packageName);
				}
				//startActivity(shareIntent);
				startActivityForResult(shareIntent,ResourceUtils.activity_id);
			}
		});

		// Launch rate activity
		tvMenuRate = findViewById(ResourceUtils.tvMenuRate);

		tvMenuRate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppRater apprater = new AppRater();
				apprater.checkAcceptOrCancel(Menu.this);
			}
		});

		//ad fragments

		if (findViewById(ResourceUtils.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			loadGglFragment();
		}
		//end ad fragments
	}

	private void loadGglFragment() {
		checkedGgl = true;
		int DAYS_UNTIL_PROMPT = 1;
		long date_of_first_launch = sharedPreferences.getLong("date_of_first_launch",0);

		if (System.currentTimeMillis() < (date_of_first_launch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))) {
			return;
		}
		try {
			if (findViewById(ResourceUtils.fragment_container) != null) {

				GglAdFragment gglAdFragment = GglAdFragment.newInstance();
				gglAdFragment.setArguments(getIntent().getExtras());

				android.app.FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.add(ResourceUtils.fragment_container,gglAdFragment);
				fragmentTransaction.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(); -- Ignore exception
		}
	}

	@Override
	public void onAdError(String adProvider) {
		if ("ggl".equalsIgnoreCase(adProvider)) {
			loadCustomFragment();
		}
	}
	private void loadCustomFragment() {
		checkedCustom = true;
		int DAYS_UNTIL_PROMPT = 1;
		long date_of_first_launch = sharedPreferences.getLong("date_of_first_launch",0);

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
				fragmentTransaction.add(ResourceUtils.fragment_container,customAdFragment);
				fragmentTransaction.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(); -- Ignore exception
		}
		// End Fragments
	}
}
