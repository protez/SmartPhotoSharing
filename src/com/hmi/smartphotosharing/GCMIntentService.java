package com.hmi.smartphotosharing;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.hmi.json.FetchJSON;
import com.hmi.json.OnDownloadListener;
import com.hmi.json.StringRepsonse;
import com.hmi.smartphotosharing.R;


public class GCMIntentService extends GCMBaseIntentService implements OnDownloadListener {

	private static final int CODE_REGISTER = 1;
	private static final int CODE_UNREGISTER = 2;
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRegistered(Context context, String regId) {

		SharedPreferences settings = getSharedPreferences(Login.SESSION_PREFS, MODE_PRIVATE);
		String hash = settings.getString(Login.SESSION_HASH, null);
		
        String registerUrl = String.format(getResources().getString(R.string.gcm_register),hash,regId);		
        new FetchJSON(this, CODE_REGISTER).execute(registerUrl);

	}

	@Override
	protected void onUnregistered(Context context, String regId) {

		SharedPreferences settings = getSharedPreferences(Login.SESSION_PREFS, MODE_PRIVATE);
		String hash = settings.getString(Login.SESSION_HASH, null);
		
        String unregisterUrl = String.format(getResources().getString(R.string.gcm_unregister),hash,regId);		
        new FetchJSON(this, CODE_UNREGISTER).execute(unregisterUrl);

	}

	@Override
	public void parseJson(String json, int code) {

		Gson gson = new Gson();
		StringRepsonse response = gson.fromJson(json, StringRepsonse.class);
		
		switch(code){
		
		case CODE_REGISTER:
			Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show();
			break;
		case CODE_UNREGISTER:
			Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show();
			
		}
		
	}

}
