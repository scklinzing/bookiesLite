package com.madmarcos.resttest;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import utility.Checksum;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class QueryTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "QueryTask";
	private String wsURL = "";
	private QueryCallback callback;
	private int sessionId = 0;
	private String salt = "";
	private String result = "";
	private RestFetcher rest = null;
	private String query = "";
	private int queryCode = 0;//used by caller to identify between multiple queries if 1 handler for all of them
	private ProgressBar bar = null;

	public QueryTask(String ws, int id, String s, String q, int code, QueryCallback cb, RestFetcher r, View v) {
		wsURL = ws;
		sessionId = id;
		salt = s;
		callback = cb;
		rest = r;
		query = q;
		queryCode = code;
		bar = (ProgressBar)v;
	}
	
	protected void onPreExecute() {
		if( bar != null )
		{
			bar.setVisibility(View.VISIBLE);
		}
    }
	
	protected Void doInBackground(Void... params) {
		try {
			if(sessionId > 0) {
				JSONObject json = new JSONObject();
				json.put("action", "runsql");
				json.put("query", query);
			
				String checksum = Checksum.calcChecksum(json.toString(), salt);
				json.put("checksum", checksum);
				json.put("session_id", Integer.toString(sessionId));

				String encoded = URLEncoder.encode(json.toString(), "UTF-8");	
				this.result = rest.getUrl(wsURL + encoded);
				publishProgress();
			} //sessionId valid so send query			
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
			if( bar != null )
			{
				bar.setVisibility(View.GONE);
			}
			callback.onQueryTaskCompleted(queryCode, o);
        } catch (JSONException e) {
            e.printStackTrace();
        }	        
    }
}
