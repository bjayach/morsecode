package com.greencabbage.patch.util;

import com.google.android.gms.ads.AdRequest;

public class AdUtils {

	static AdRequest adRequest = null;
	
	public static AdRequest getAd() {
		if (adRequest == null) {
			loadNewAd();
		}
		return adRequest;
	}
	//AdRequest.Builder.addTestDevice("C7CD99D8E52E6D4686676A7AD68DA30E");
	public static void loadNewAd() {
		//adRequest = new AdRequest.Builder().addTestDevice("C7CD99D8E52E6D4686676A7AD68DA30E").build();
		adRequest = new AdRequest.Builder().build();
	}
	
	public static void clearAd() {
		adRequest = null;
	}
}
