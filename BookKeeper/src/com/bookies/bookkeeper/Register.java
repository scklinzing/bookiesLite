package com.bookies.bookkeeper;

//just adding comment to test git. hpesson

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import utility.CryptoStuff;

import com.madmarcos.resttest.LoginCallback;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Register extends ActionBarActivity implements QueryCallback{
	
	private TextView userNameTextView;
	private TextView emailTextView;
	private TextView passwordTextView;
	private TextView password2TextView;
	private CheckBox checkBox1;
	private static final String TAG = "Register";
	
	public static final String EXTRA_USERNAME = "com.bookies.bookkeeper.USERNAME";
	public static final String EXTRA_EMAIL = "com.bookies.bookkeeper.EMAIL";
	public static final String EXTRA_PASSWORD = "com.bookies.bookkeeper.PASSWORD";
	
	private static final int QUERY_SELECT_TEST = 0;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		userNameTextView = (TextView)findViewById(R.id.UserNameTextView);
		emailTextView = (TextView)findViewById(R.id.EmailTextView);
		passwordTextView = (TextView)findViewById(R.id.regPassword);
		password2TextView = (TextView)findViewById(R.id.regConfirmpassword);
		checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
	}
	
	public void userAgreement( View view ) {
		Intent intent = new Intent(this,UserAgreement.class);
		startActivity(intent);
	}
	
	public void submit( View view ) {

		//Form Validation
		if( userNameTextView.getText().toString().isEmpty() || emailTextView.getText().toString().isEmpty() || 
				passwordTextView.getText().toString().isEmpty() || password2TextView.getText().toString().isEmpty() || !checkBox1.isChecked() )
		{
			if( userNameTextView.getText().toString().isEmpty())
			{
				Log.d(TAG, "Empty");
				Toast.makeText(getApplicationContext(), "Enter a User Name", Toast.LENGTH_SHORT).show();
			}
			if( emailTextView.getText().toString().isEmpty())
			{
				Toast.makeText(getApplicationContext(), "Enter an Email", Toast.LENGTH_SHORT).show();
			}
			if( passwordTextView.getText().toString().isEmpty())
			{
				Toast.makeText(getApplicationContext(), "Enter a Password", Toast.LENGTH_SHORT).show();
			}
			if( password2TextView.getText().toString().isEmpty())
			{
				Toast.makeText(getApplicationContext(), "Comfirm Password", Toast.LENGTH_SHORT).show();
			}
			if( !checkBox1.isChecked() )
			{
				Toast.makeText(getApplicationContext(), "You must agree to the User Agreement", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if ( !(passwordTextView.getText().toString().equals( password2TextView.getText().toString()) ) )
			{
				Toast.makeText(getApplicationContext(),"Passwords do not match",Toast.LENGTH_SHORT).show();;
			}
			else if ( !(Pattern.matches(EMAIL_PATTERN, emailTextView.getText().toString()) ))
			{
				Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();;
			}
			else
			{
				String username = userNameTextView.getText().toString();
				String email = emailTextView.getText().toString();
				//checking if user name or email already exists in database, and then do an insert
				String query = "select * from USER where Email = \"" + email + "\" or Username = \"" + username + "\"" ;
				Log.d(TAG, "Query = " + query);
				new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SELECT_TEST, this, Variables.getRest(), null).execute();
			}
		}
		
		//Move this to query handling!
//		Intent intent = new Intent( this, Verify.class);
//		String message = "Register";
//		intent.putExtra(MainActivity.EXTRA_MESSAGE, message);
//		startActivity(intent);
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
//			View rootView = inflater.inflate(R.layout.fragment_register,
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
							Toast.makeText(getApplicationContext(), "Username or Email already registered!", Toast.LENGTH_SHORT).show();
						}
					}
					else if ( result.getString("response_status").equalsIgnoreCase("error") && !result.isNull("error") && result.getString("error").equalsIgnoreCase("no records found") ) 
					{
						Log.d(TAG, "Starting new intent");
						Intent intent = new Intent( this, Verify.class);
						intent.putExtra(MainActivity.EXTRA_MESSAGE, "Register");
						intent.putExtra(EXTRA_USERNAME, userNameTextView.getText().toString());
						intent.putExtra(EXTRA_EMAIL, emailTextView.getText().toString());
						intent.putExtra(EXTRA_PASSWORD, CryptoStuff.hashPWSha256(passwordTextView.getText().toString()));
						startActivity(intent);
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
