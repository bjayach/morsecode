package com.greencabbage.patch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.greencabbage.patch.util.ResourceUtils;
import com.greencabbage.patch.util.StringUtils;

public class Share extends Activity {

	TextView tvShare;
	Button copyButtonWeb, copyButtonMobile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.share);

		// Update text on launch of RemoveAd activity
		tvShare = (TextView) findViewById(ResourceUtils.tvShareText);
		tvShare.setText(getString(ResourceUtils.share_text)
				+ getString(ResourceUtils.google_store_web_link)
				+ getString(ResourceUtils.gstore_web_url)
				+ getString(ResourceUtils.package_name)
				+ getString(ResourceUtils.google_store_mobile_link)
				+ getString(ResourceUtils.gstore_mobile_url)
				+ getString(ResourceUtils.package_name));

		// copy web button
		copyButtonWeb = (Button) findViewById(ResourceUtils.bCopyWeb);

		copyButtonWeb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				StringUtils.copyToClipboard(getApplicationContext(),
						getString(ResourceUtils.gstore_web_url)
								+ getString(ResourceUtils.package_name));
			}
		});

		// copy mobile button
		copyButtonMobile = (Button) findViewById(ResourceUtils.bCopyMobile);

		copyButtonMobile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				StringUtils.copyToClipboard(getApplicationContext(),
						getString(ResourceUtils.gstore_mobile_url)
								+ getString(ResourceUtils.package_name));
			}
		});

	}

}
