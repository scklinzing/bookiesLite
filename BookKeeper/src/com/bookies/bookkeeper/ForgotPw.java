package com.bookies.bookkeeper;

import java.util.regex.Pattern;

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

public class ForgotPw extends ActionBarActivity implements QueryCallback {
	
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String TAG = "FORGOT_PW";
	private static final int QUERY_SELECT_TEST = 0;
	
	public static final String EXTRA_USERID = "com.bookies.bookkeeper.USERID_FORGOTPW";
	
	private EditText emailEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_pw);
		
		emailEditText = (EditText)findViewById(R.id.EmailTextView);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}
	
	public void submit( View view ) {
//		Intent intent = new Intent( this, MainForm.class );
//		startActivity(intent);
		String email = emailEditText.getText().toString();
		
		if ( Pattern.matches(EMAIL_PATTERN, email) )
		{
			String query = "select * from USER where email=\"" + email + "\"";
			Log.d(TAG, "Query:" + query);
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SELECT_TEST, this, Variables.getRest(), null).execute();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
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
//			View rootView = inflater.inflate(R.layout.fragment_forgot_pw,
//					container, false);
//			return rootView;
//		}
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			if(code == QUERY_SELECT_TEST) {
				if(result != null && !result.isNull("response_status")) {
					//number of sql rows returned from query
					int row = 0;
					//size of json response from server
					int size = result.length();
					Log.d(TAG, "Results: " + size);
					Log.d(TAG, result.toString());
					if ( result.getString("response_status").equalsIgnoreCase("success") ) {
						while(!result.isNull(Integer.toString(row))) {
							row++;
						}
						
						if ( row > 0 )
						{
							Log.d(TAG, "Email Found!");
							JSONObject o2 = new JSONObject(result.getString(Integer.toString(0)));
							int userId = o2.getInt("userID");
							Log.d(TAG,"User Id: " + userId);
							Intent intent = new Intent( this, Verify.class);
							intent.putExtra(MainActivity.EXTRA_MESSAGE, "Forgot_PW");
							intent.putExtra(ForgotPw.EXTRA_USERID, userId);
							startActivity(intent);
						}
					}
					else if ( result.getString("response_status").equalsIgnoreCase("error") && !result.isNull("error") && result.getString("error").equalsIgnoreCase("no records found") ) 
					{
						Toast.makeText(getApplicationContext(), "Email Not Found!", Toast.LENGTH_SHORT).show();
					}
				} 
				else {
					Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
