package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import utility.CryptoStuff;
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
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Verify extends ActionBarActivity implements QueryCallback{
	
	private String message;
	private String username;
	private String email;
	private String password;
	private int userId;
	
	private String verificationCode = "1234";
	
	private TextView verificationCodeTextView;
	
	private static final String TAG = "Verify";
	
	private static final int QUERY_INSERT_TEST = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify);
		
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
		verificationCodeTextView = (TextView)findViewById(R.id.VerificationCodeTextView);
		
		Intent intent = getIntent();
		message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		Log.d(TAG, "Message: " + message);
		
		if ( message.equals("Register"))
		{
			username = intent.getStringExtra(Register.EXTRA_USERNAME);
			email = intent.getStringExtra(Register.EXTRA_EMAIL);
			password = intent.getStringExtra(Register.EXTRA_PASSWORD);
		}
		else if ( message.equals("Forgot_PW"))
		{
			userId = intent.getIntExtra(ForgotPw.EXTRA_USERID, -1);
		}		
	}
	
	public void verify( View view ) {
		if( verificationCodeTextView.getText().toString().equals(verificationCode))
		{
			if(message.equals("Register")){
				String query = "insert into USER (Username, Email, Password) values ('" + username + "','" + email + "','" + password + "')";
				Log.d(TAG, query);
				new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_INSERT_TEST, this, Variables.getRest(), null).execute();
			}
			else if ( message.equals("Forgot_PW"))
			{
				Intent intent = new Intent( this, NewPassword.class);
				intent.putExtra(ForgotPw.EXTRA_USERID, userId);
				startActivity(intent);
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Code Does Not Match", Toast.LENGTH_SHORT).show();
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
//			View rootView = inflater.inflate(R.layout.fragment_verify,
//					container, false);
//			return rootView;
//		}
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			if(code == QUERY_INSERT_TEST) 
			{
				if(result != null && !result.isNull("response_status")) 
				{
					//number of sql rows returned from query
					int row = 0;
					//size of json response from server
					int size = result.length();
					Log.d(TAG, "Results: " + size);
					Log.d(TAG, result.toString());
					
					if( result.getString("response_status").equalsIgnoreCase("success") )
					{
						Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent( this, Login.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				} 
				else 
				{
					Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} 
			else 
			{
				Log.e(TAG, "*** Error: unknown code");
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}

}
