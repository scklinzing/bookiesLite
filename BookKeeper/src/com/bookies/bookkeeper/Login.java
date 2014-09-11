package com.bookies.bookkeeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import utility.CryptoStuff;

import com.madmarcos.resttest.LoginCallback;
import com.madmarcos.resttest.LoginTask;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;
import com.madmarcos.resttest.RestFetcher;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Login extends ActionBarActivity implements LoginCallback, QueryCallback {
	
	private TextView userNameTextView;
	private TextView passwordTextView;
	
	private static final String DEFAULT_LOGIN = "cs4953s14t3user";
	private static final String DEFAULT_PW = "gK23yp4D@5jwX62b226eS";
	//USE THIS ONE FOR A SECURE CONNECTION TO THE WEB SERVICE
	//private static final String WS_URL = "https://galadriel.fulgentcorp.com/services.php?json=";
	//private static final String WS_URL = "http://galadriel.cs.utsa.edu/bifrost/services.php?json=";
	private static final String TAG = "Login";
	
	private static final int QUERY_SELECT_TEST = 0;
	public final static String EXTRA_USERID = "com.bookies.bookkeeper.USERID";
	
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userNameTextView = (TextView) findViewById(R.id.UserNameTextView);
		passwordTextView = (TextView) findViewById(R.id.PasswordTextView);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	public void createAccount( View view) {
		Intent intent = new Intent( this, Register.class);
		startActivity(intent);
	}
	
	public void signin( View view ) {
		//TODO
		//Query Database for username to see if the user exists
		
		if( userNameTextView.getText().toString().isEmpty() )
		{
			Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_LONG).show();
		}
		//Check for email
		else if ( userNameTextView.getText().toString().contains("@")) {
			String email = userNameTextView.getText().toString();
			String query = "select * from USER where Email = \"" + email + "\"";
			//String query = ((TextView)findViewById(R.id.Query)).getText().toString();
			//((TextView)findViewById(R.id.Query)).setText(query);
			Log.d(TAG, "Query = " + query);
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SELECT_TEST, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
		}
		//check for username
		else
		{
			String userName = userNameTextView.getText().toString();
			String query = "select * from USER where Username = \"" + userName + "\"";
			//((TextView)findViewById(R.id.Query)).setText(query);
			Log.d(TAG, "Query = " + query);
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SELECT_TEST, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
		}
		
	}
	
	public void forgotPassword( View view ) {
		Intent intent = new Intent( this, ForgotPw.class);
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

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			if(code == QUERY_SELECT_TEST) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
					int row = 0;
					int size = result.length();
					Log.d(TAG, "Results: " + size);
					Log.d(TAG, result.toString());
					while(!result.isNull(Integer.toString(row))) {
						row++;
					}
					
					//row will always be 1 since we have unique users and we ar using a slect with a where clause
					if( row == 1 ) {
						/* ------------- SKIP PASSWORD CHECK --------------------- */
						JSONObject o2 = new JSONObject(result.getString(Integer.toString(0)));
						
						Intent intent = new Intent( this, MainForm.class);
						
						//for Heather debugging
						//Intent intent = new Intent( this, AppBookSearch.class);
						int admin = o2.getInt("userType");
						Log.d("Login", "usertype is gotten");
						if(admin == 1) Variables.setAdmin(true);	
						
						Variables.setUserId(o2.getInt("userID"));
						intent.putExtra(Login.EXTRA_USERID, Variables.getUserId());
						startActivity(intent);
					}
					else {
						Toast.makeText(getApplicationContext(), "User Name or Email not found", Toast.LENGTH_LONG).show();
					}
					Log.d(TAG,"Rows= " + row);
				} else {
					Toast.makeText(getApplicationContext(), "User Name or Email not found", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
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
			} else {
				setSessionInfo(0, "Error");
				Log.d(TAG, "Login Error");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
}
