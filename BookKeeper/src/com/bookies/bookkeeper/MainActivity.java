package com.bookies.bookkeeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.madmarcos.resttest.LoginCallback;
import com.madmarcos.resttest.LoginTask;
import com.madmarcos.resttest.RestFetcher;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
/**
 * Logs onto the DB and loads Library
 */
public class MainActivity extends ActionBarActivity implements LoginCallback {
	private static final String DEFAULT_LOGIN = "cs4953s14t3user";
	private static final String DEFAULT_PW = "gK23yp4D@5jwX62b226eS";
	//USE THIS ONE FOR A SECURE CONNECTION TO THE WEB SERVICE
	//private static final String WS_URL = "https://galadriel.fulgentcorp.com/services.php?json=";
	//private static final String WS_URL = "http://galadriel.cs.utsa.edu/bifrost/services.php?json=";
	private static final String TAG = "MainActivity";
	
	public final static String EXTRA_USERID = "com.bookies.bookkeeper.USERID";
	public final static String EXTRA_MESSAGE = "com.bookies.BookKeeper.MESSAGE";
	
	List<Map<String, String>> rows;
	
	private void initSession() {
		Variables.setSalt("");
	}

	private void setSessionInfo(int i, String s) {
		Variables.setSessionId(i);
		Variables.setSalt(s);
	}
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Entering OnCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        initSession();
		Variables.setRest( new RestFetcher());
		try {
			InputStream caInput = getAssets().open("fulgentcorp.crt");
			Variables.getRest().initKeyStore(caInput);
			caInput.close();
		} catch (IOException e) {
			Log.e(TAG, "*** initKeyStore error: " + e.getMessage());
		}
		new LoginTask(Variables.getWS_URL(), DEFAULT_LOGIN, DEFAULT_PW, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
    }
	
	@Override
	public void onLoginTaskCompleted(JSONObject result) {
		try {
			int tempSessionId = 0;
			String tempSalt = "";
			if(!result.isNull("session_id")) {
				tempSessionId = result.getInt("session_id");
				tempSalt = result.getString("salt");
				setSessionInfo(tempSessionId, tempSalt);
				Log.d(TAG, "Login Success " + tempSessionId + " " + tempSalt);
				
				// --------------------- START bypass login -- SHELLEY
				Log.d(TAG, "onLoginTaskCompleted Attempting to bypass login");
				Intent intent = new Intent( this, MainForm.class);
				Log.d(TAG, "onLoginTaskCompleted Created Intent to MainForm");
				startActivity(intent);
				// --------------------- END bypass login
				
			} else {
				setSessionInfo(0, "Error");
				Log.d(TAG, "DB Login Error");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// in the event the user hits the back button from MainForm
	public void refresh(View view) {
		Log.d(TAG, "Refresh the MainForm");
		Intent intent = new Intent( this, MainForm.class);
		startActivity(intent);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    
    public void next( View view ) {
    	Intent intent = new Intent( this, MainForm.class);
    	startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }
    }
}