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

public class ChangePw extends ActionBarActivity implements QueryCallback{
	
	private static final String TAG = "CHANGE_PW";
	private static final int SELECT_USER = 0;
	private static final int UPDATE_USER = 1;
	
	private EditText oldPwEditText;
	private EditText newPwEditText1;
	private EditText newPwEditText2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pw);
		
		oldPwEditText = (EditText)findViewById(R.id.OldNewPasswordTextView);
		newPwEditText1 = (EditText)findViewById(R.id.NewPasswordTextView);
		newPwEditText2 = (EditText)findViewById(R.id.ReenterPasswordTextView);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}
	
	public void submit( View view ) {
		
		String oldPw = oldPwEditText.getText().toString();
		String newPw1 = newPwEditText1.getText().toString();
		String newPw2 = newPwEditText2.getText().toString();
		
		if( oldPw.isEmpty() )
		{
			Toast.makeText(getApplicationContext(), "Enter your current Password", Toast.LENGTH_SHORT).show();
		}
		if( newPw1.isEmpty() )
		{
			Toast.makeText(getApplicationContext(), "Enter a new password", Toast.LENGTH_SHORT).show();
		}
		if( newPw2.isEmpty() )
		{
			Toast.makeText(getApplicationContext(), "Confirm new password", Toast.LENGTH_SHORT).show();
		}
		
		if ( !oldPw.isEmpty() && !newPw1.isEmpty() && !newPw2.isEmpty() )
		{
			String query = "select * from USER where UserID = " + Variables.getUserId();
			Log.d(TAG, "Query: " + query);
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, SELECT_USER, this, Variables.getRest(), null).execute();
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

	}

	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			if(code == SELECT_USER) {
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
						JSONObject o2 = new JSONObject(result.getString(Integer.toString(0)));
						if( o2.getString("Password").equals(CryptoStuff.hashPWSha256(oldPwEditText.getText().toString())) ) {
							if( newPwEditText1.getText().toString().equals(newPwEditText2.getText().toString()))
							{
								String pw = newPwEditText1.getText().toString();
								String query = "update USER set Password=\"" + CryptoStuff.hashPWSha256(pw) + "\" where userID = " + Variables.getUserId();
								Log.d(TAG, "Query: " + query);
								new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, UPDATE_USER, this, Variables.getRest(), null).execute();
							}
						}
						else {
							Toast.makeText(getApplicationContext(), "Old Password does not match!", Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
					}
					Log.d(TAG,"Rows= " + row);
				} else {
					Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} 
			else if ( code == UPDATE_USER) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
					Intent intent = new Intent(this, MainForm.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
				else {
					Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();;
				}
			}
			else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
