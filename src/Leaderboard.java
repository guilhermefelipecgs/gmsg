package org.godotengine.godot;

import android.app.Activity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class Leaderboard {

	Activity activity;
	GoogleApiClient googleApiClient;

	public Leaderboard(Activity activity, GoogleApiClient googleApiClient) {
		this.activity = activity;
		this.googleApiClient = googleApiClient;
	}

	public void showLeaderboard(String leaderboardID){
			activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, leaderboardID), 0);
	}

	public void submitScore(String leaderboardID, int score) {
			Games.Leaderboards.submitScore(googleApiClient, leaderboardID, score);
	}

}

