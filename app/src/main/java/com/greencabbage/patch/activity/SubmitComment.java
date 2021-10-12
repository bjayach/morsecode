package com.greencabbage.patch.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.greencabbage.patch.util.CabbageUtils;
import com.greencabbage.patch.util.ResourceUtils;
import com.greencabbage.patch.util.async.AsyncPostFeedback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SubmitComment extends Activity {

	TextView tvShare;
	EditText etAddComment;
	Button submitCommentButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.comment);

		// Update text on launch of RemoveAd activity
		tvShare =  findViewById(ResourceUtils.tvShareText);
		tvShare.setText(getString(ResourceUtils.comment_prompt));

		//set comments edittext
		etAddComment =  findViewById(ResourceUtils.etAddComment);
		etAddComment.requestFocus();

		// copy web button
		submitCommentButton = findViewById(ResourceUtils.bSubmitComment);

		submitCommentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				postComment(etAddComment.getText().toString());
				finish();
                Toast.makeText(getApplicationContext(), "Your feedback has been submitted", Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void postComment(String comment) {
		String jsonString = null;
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		int sdkVersion = Build.VERSION.SDK_INT;
		String androidVersion = Build.VERSION.RELEASE;


		try {

			jsonString = new JSONObject()
					.put("Item",
							(new JSONObject()
									.put("Id",System.currentTimeMillis()+"")
									.put("Desc",comment)
									.put("manufacturer",manufacturer)
									.put("model",model)
									.put("sdkVersion",Integer.toString(sdkVersion))
									.put("androidVersion",androidVersion)

							)).put("TableName","Feedback")
					.toString();

			String wsURL = "https://kzi2qm6u85.execute-api.us-west-2.amazonaws.com/default/submitFeedback";
        	makeWSCall(wsURL,jsonString);

		}  catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void makeWSCall(String wsURL,String jsonString) {
        AsyncPostFeedback asyncPostFeedback = new AsyncPostFeedback();
        asyncPostFeedback.execute(wsURL, jsonString);
        String response;
        try {
            response = asyncPostFeedback.get();
			if ("EXCEPTION".equals(response)) {
				int retryCount = 0;
				//get wsURL from cookie and retry once
				if (retryCount <=0 ) {
					File applicationCacheDir = SubmitComment.this.getCacheDir();
					wsURL = new CabbageUtils().getDataFromCookie(applicationCacheDir,"aws_submitfeedback_url");
					makeWSCall(wsURL,jsonString);
				}
				retryCount++;
			}
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
