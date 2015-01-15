package com.madmarcos.resttest;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import utility.CryptoStuff;

public class LoginTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "LoginTask";
	private String wsURL = "";
	private LoginCallback callback;
	private String result;
	private String login = "";
	private String password = "";
	private RestFetcher rest = null;
	ProgressBar bar = null;
	
	public LoginTask(String ws, String l, String pw, LoginCallback cb, RestFetcher r, View v) {
		wsURL = ws;
		callback = cb;
		login = l;
		password = pw;
		rest = r;
		bar = (ProgressBar)v;
	}
	
	protected void onPreExecute() {
		bar.setVisibility(View.VISIBLE);
    }
	
	protected Void doInBackground(Void... params) {
		try {
			JSONObject json = new JSONObject();
			json.put("action", "login");
			json.put("login", login);
			json.put("password", CryptoStuff.hashPWSha256(password));
			String encoded = URLEncoder.encode(json.toString(), "UTF-8");	
			this.result = rest.getUrl(wsURL + encoded);

			publishProgress();
		} catch(Exception e) {
			Log.e(TAG, "Failed to fetch URL: ", e);
		}
		return null;
	}	

	//onProgressUpdate runs on the UI thread
	@Override
	protected void onProgressUpdate(Void... values) {
    	try {
			JSONObject o = (JSONObject) new JSONTokener(result).nextValue();
			bar.setVisibility(View.GONE);
			callback.onLoginTaskCompleted(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }	        
    }
}