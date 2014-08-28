package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

public class SearchUser extends ActionBarActivity implements QueryCallback {

	/* get the UserName */
	private TextView SearchUser;

	/* Identifies where you are in the log */
	private static final String TAG = "EditUser";

	private static final int SEARCH_USER = 0;
	
	/* holder for the id to pass to the next activity */
	public final static String EXTRA_FOUNDUSER = "com.bookies.bookkeeper.FOUNDUSER";
	public final static String EXTRA_USERNAME = "com.bookies.bookkeeper.USERNAME";
	public final static String EXTRA_EMAIL = "com.bookies.bookkeeper.EMAIL";
	public final static String EXTRA_PASSWORD = "com.bookies.bookkeeper.PASSWORD";
	public final static String EXTRA_USERTYPE = "com.bookies.bookkeeper.USERTYPE";
	public final static String EXTRA_USERSECURITY = "com.bookies.bookkeeper.USERSECURITY";
	public final static String EXTRA_USERSTATUS = "com.bookies.bookkeeper.USERSTATUS";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_user);
		
		
	}

	/**
	 * If an Admin searches for a user, bring them to the page to edit that
	 * user. This will execute when the GO button is clicked.
	 * 
	 * @param view
	 */
	public void searchUser(View view) {
		SearchUser = (TextView) findViewById(R.id.SearchUser);
		String userName = SearchUser.getText().toString();
		String query = "select * from USER where Username = '" + userName + "'";
		Log.d(TAG, "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, SEARCH_USER, this,
				Variables.getRest(), null).execute();

	} /* end searchUser() */

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			/* We got here from searchUser */
			if (code == SEARCH_USER) {
				if (result != null
						&& !result.isNull("response_status")
						&& result.getString("response_status")
								.equalsIgnoreCase("success")) {
					int row = 0;
					/* if there is a response status and if it is successful */
					int size = result.length();
					/* log the results */
					Log.d(TAG, "Results: " + size);
					Log.d(TAG, result.toString());
					while (!result.isNull(Integer.toString(row))) {
						row++;
					}
					Log.d(TAG, "Rows= " + row);
					
					/* Shelley Code */
					if(row > 0) {
						/* grab the json value out of the key 0 (one response from server) */
						JSONObject object = new JSONObject(result.getString(Integer.toString(0)));
						Intent intent = new Intent(this, EditUser.class);
						int userID = object.getInt("userID");
						String username = object.getString("Username");
						String email = object.getString("Email");
						String password = object.getString("Password");
						int userType = object.getInt("userType");
						int userStatus = object.getInt("userStatus");
						
						/* save all the information you want to pass to the next activity */
						intent.putExtra(EXTRA_FOUNDUSER, userID);
						intent.putExtra(EXTRA_USERNAME, username);
						intent.putExtra(EXTRA_EMAIL, email);
						intent.putExtra(EXTRA_PASSWORD, password);
						intent.putExtra(EXTRA_USERTYPE, userType);
						intent.putExtra(EXTRA_USERSTATUS, userStatus);
						
						startActivity(intent); /* starts EditUser */
						
					}
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"User Not Found", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {
		//invalidateOptionsMenu();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.admin_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//handles menu selection
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.viewBookList:
	        	Intent list = new Intent( this, MainForm.class);
				startActivity(list);
	            return true;
	        case R.id.editUser:
	        	Intent editUser = new Intent( this, SearchUser.class);
	        	startActivity(editUser);
	            return true;
	        case R.id.editBook:
	        	Intent editBook = new Intent( this, AppBookSearch.class);
				startActivity(editBook);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
} /* end onQueryTaskCompleted() */
