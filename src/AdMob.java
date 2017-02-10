package org.godotengine.godot;

import android.app.Activity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdMob extends AdListener {

	public static final int BANNER = 0;
	public static final int INTERSTITIAL_AD = 1;

	private Activity activity;
	private AdRequest adRequest;
	private AdView adView;
	private InterstitialAd interstitialAd;
	private String adUnitId;
	private boolean loaded = false;
	private int instanceID;
	private int type;

	public AdMob(Activity activity){
		this.activity = activity;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setAdUnitId(String adUnitId) {
		this.adUnitId = adUnitId;
	}

	public void load() {
		switch (type) {
			case BANNER:
				break;
			case INTERSTITIAL_AD:
				interstitialAd = new InterstitialAd(activity);
				adRequest = new AdRequest.Builder().build();
				interstitialAd.setAdUnitId(adUnitId);
				interstitialAd.setAdListener(this);
				activity.runOnUiThread(new Runnable() {
					public void run() {
						interstitialAd.loadAd(adRequest);
					}
				});
		}
	}

	public void show() {
		switch (type) {
			case BANNER:
				break;
			case INTERSTITIAL_AD:
				activity.runOnUiThread(new Runnable() {
					public void run() {
						interstitialAd.show();
					}
				});
		}
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setInstanceId(int instanceID) {
		this.instanceID = instanceID;
	}

	@Override
	public void onAdClosed() {
		loaded = false;
		GodotLib.calldeferred(instanceID, "_on_ad_closed", new Object[]{});
	}

	@Override
	public void onAdFailedToLoad(int i) {
		GodotLib.calldeferred(instanceID, "_on_ad_failed_to_load", new Object[]{});
	}

	@Override
	public void onAdOpened() {
		GodotLib.calldeferred(instanceID, "_on_ad_opened", new Object[]{});
	}

	@Override
	public void onAdLoaded() {
		loaded = true;
		GodotLib.calldeferred(instanceID, "_on_ad_loaded", new Object[]{});
	}

}

