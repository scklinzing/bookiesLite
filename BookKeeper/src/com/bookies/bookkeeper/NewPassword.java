package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;

import utility.CryptoStuff;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class NewPassword extends ActionBarActivity implements QueryCallback {
	
	private static final int QUERY_UPDATE_TEST = 0;
	
	private static final String TAG = "NEW_PW";
	
	private int userId;
	
	private EditText password;
	private EditText password2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_password);
		
		Intent intent = getIntent();
		
		userId = intent.getIntExtra(ForgotPw.EXTRA_USERID, -1);
		
		password = (EditText)findViewById(R.id.passwordEditText1);
		password2 = (EditText)findViewById(R.id.passwordEditText2);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}
	
	public void submit( View view ) {
		if ( password.getText().toString().equals(password2.getText().toString()))
		{
			String pw = password.getText().toString();
			String query = "update USER set Password=\"" + CryptoStuff.hashPWSha256(pw) + "\" where userID = \"" + userId + "\"";
			Log.d(TAG,"Query: " + query);
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_UPDATE_TEST, this, Variables.getRest(), null).execute();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Passwords do not macth!", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
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

//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_new_password,
//					container, false);
//			return rootView;
//		}
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		if(code == QUERY_UPDATE_TEST) {
			try 
			{
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) 
				{					
					int row = 0;
					int size = result.length();
					Log.d(TAG, "Results: " + size);
					Log.d(TAG, result.toString());

					Intent intent = new Intent(this, Login.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Databse Error", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}

}
