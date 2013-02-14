package com.example.foodlog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
    private static final String TAG = "GCMIntentService";
    public GCMIntentService() {
    	super("SENDER_ID");
    	}
	@Override
	protected void onError(Context context, String errorId) {
		// error メッセージ
	       Log.e(TAG, "onError errorId:" + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent data) {
		// TODO メッセージ受信

	}

	@Override
	protected void onRegistered(Context context, String registrationId) { 
		// 登録完了
		Log.i(TAG, "onRegistered registrationId:" + registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// 登録完了     
		Log.i(TAG, "onUnregistered registrationId:" + registrationId);
	}

}
