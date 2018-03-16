package com.ryansplayllc.ryansplay.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ryansplayllc.ryansplay.GameScreenActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

        Log.e("connection changed "," receiver");
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if ((activeNetInfo != null && activeNetInfo.isConnectedOrConnecting())
				|| (mobNetInfo != null && mobNetInfo.isConnectedOrConnecting())) {

			// starting a broad cast
			Intent broadcastIntent = new Intent();
			broadcastIntent
					.setAction(GameScreenActivity.CONNECTION_ACTION_RESP);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra("connected", true);
			context.sendBroadcast(broadcastIntent);
		} else {
			// starting a broad cast
			Intent broadcastIntent = new Intent();
			broadcastIntent
					.setAction(GameScreenActivity.CONNECTION_ACTION_RESP);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra("connected", false);
			context.sendBroadcast(broadcastIntent);
		}
	}
}
