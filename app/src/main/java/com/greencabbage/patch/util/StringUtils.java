package com.greencabbage.patch.util;

import android.content.Context;
import android.widget.Toast;

public class StringUtils {

	@SuppressWarnings("deprecation")
	public static boolean copyToClipboard(Context context, String text) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(text);
		} else {
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip =  android.content.ClipData.newPlainText("label", text);
			clipboardManager.setPrimaryClip(clip);
		}
		
		Toast.makeText(context, "Text is copied. Press Long to paste anywhere", Toast.LENGTH_SHORT).show();
		return true;
	}
}
