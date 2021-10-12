package com.greencabbage.patch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.greencabbage.patch.util.ResourceUtils;


public class Feedback extends Activity {

	TextView tvFeedback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.feedback);

		// Update text on launch of Disclaimer activity
		tvFeedback = findViewById(ResourceUtils.tvFeedbackText);
		tvFeedback.setTextSize(10f);
		tvFeedback.setText(getString(ResourceUtils.feedback_text));

	}

}
