package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GMSG extends Godot.SingletonBase
	implements ConnectionCallbacks, OnConnectionFailedListener {

	private Activity activity;
	private AdMob adMob;
	private GoogleApiClient googleApiClient;
	private Leaderboard leaderboard;
	private int instanceID;

	static public Godot.SingletonBase initialize(Activity p_activity) {
		return new GMSG(p_activity);
	}

	public GMSG(Activity p_activity) {
		this.activity = p_activity;
		googleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
			.addOnConnectionFailedListener(this)
			.addConnectionCallbacks(this)
			.setViewForPopups(activity.findViewById(android.R.id.content))
			.addApi(Games.API).addScope(Games.SCOPE_GAMES)
			.build();
		leaderboard = new Leaderboard(activity, googleApiClient);
		adMob = new AdMob(activity);
		adMob.setType(AdMob.INTERSTITIAL_AD);
		String adUnitId = GodotLib.getGlobal("ad_mob/ad_unit_id");
		if (adUnitId.isEmpty()) {
			adMob.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
		} else {
			adMob.setAdUnitId(adUnitId);
		}

		registerClass("GMSG", new String[]{"is_connected", "connect", "disconnect",
			"set_instance_ID", "show_leaderboard", "submit_score", "load", "is_loaded","show"});

		activity.runOnUiThread(new Runnable() {
			public void run() {
				//useful way to get config info from engine.cfg
				//String key = GodotLib.getGlobal("plugin/api_key");
				//SDK.initializeHere();
			}
		});
	}

	private static int RC_SIGN_IN = 9001;

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(activity, RC_SIGN_IN);
			} catch (SendIntentException e) {
				GodotLib.calldeferred(instanceID, "_on_connection_failed", new Object[]{e.getMessage()});
			}
			GodotLib.calldeferred(instanceID, "_on_connection_failed", new Object[]{result.getErrorMessage()});
		}
	}

	@Override
	protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_SIGN_IN) {
			if (resultCode == activity.RESULT_OK) {
				if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
					googleApiClient.connect();
				}
			}
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		GodotLib.calldeferred(instanceID, "_on_connected", new Object[]{});
	}

	@Override
	public void onConnectionSuspended(int i) {
		GodotLib.calldeferred(instanceID, "_on_connection_suspended", new Object[]{i});
	}	

	public void show_leaderboard(String leaderboardID) {
		leaderboard.showLeaderboard(leaderboardID);
	}

	public void submit_score(String leaderboardID, int score) {
		leaderboard.submitScore(leaderboardID, score);
	}

	public void set_instance_ID(int instanceID) {
		this.instanceID = instanceID;
		adMob.setInstanceId(instanceID);
	}

	public void connect() {
		googleApiClient.connect();
	}

	public void disconnect() {
		if (googleApiClient.isConnected()) {
			Games.signOut(googleApiClient);
			googleApiClient.disconnect();
		}
	}

	public boolean is_connected() {
		return googleApiClient.isConnected();
	}

	public void load() {
		adMob.load();
	}

	public boolean is_loaded() {
		return adMob.isLoaded();
	}

	public void show() {
		adMob.show();
	}

}

