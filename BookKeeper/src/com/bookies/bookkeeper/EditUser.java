package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;

import utility.CryptoStuff;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

public class EditUser extends Activity implements QueryCallback {
	
	/* used by caller to identify between multiple queries if 1 handler for all of them */
	private static final int UPDATE_USER = 0;
	/* for log identification */
	private static final String TAG = "UpdateUser";
	/* store the update query */
	private String query;

 	/* declare variables for all items to pull from previous activity */
	private int userID = -1;
	private String username = "";
	private String email = "";
	private String password = "";
	private int userType = 0;
	private int userStatus = 1;
	
	/* declare the names of the field views to edit them */
	private EditText emailTextView;
	private EditText passwordTextView1;
	private EditText passwordTextView2;
	private RadioButton userRadioButton;
	private RadioButton adminRadioButton;
	private RadioButton activeRadioButton;
	private RadioButton inactiveRadioButton;
	
	public final static String EXTRA_FOUNDUSER = "com.bookies.bookkeeper.FOUNDUSER";
	public final static String EXTRA_USERNAME = "com.bookies.bookkeeper.USERNAME";
	public final static String EXTRA_EMAIL = "com.bookies.bookkeeper.EMAIL";
	public final static String EXTRA_PASSWORD = "com.bookies.bookkeeper.PASSWORD";
	public final static String EXTRA_USERTYPE = "com.bookies.bookkeeper.USERTYPE";
	public final static String EXTRA_USERSTATUS = "com.bookies.bookkeeper.USERSTATUS";

	/**
	 * When the activity begins, we want some fields and buttons to be 
	 * already clicked.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_user);
		//Intent intent = getIntent();
		Intent b = getIntent();
		
		/* grab variables from previous activity */
		userID = b.getIntExtra(EXTRA_FOUNDUSER, -1);
		username = b.getStringExtra(EXTRA_USERNAME);
		email = b.getStringExtra(EXTRA_EMAIL);
		userType = b.getIntExtra(EXTRA_USERTYPE, 0);
		userStatus = b.getIntExtra(EXTRA_USERSTATUS, 0);
		
		/* save the fields to set the values for them */
		emailTextView = (EditText) findViewById(R.id.UpdateUserEmail);
		
		passwordTextView1 = (EditText) findViewById(R.id.UpdateUserPassword1);
		passwordTextView2 = (EditText) findViewById(R.id.UpdateUserPassword2);
		userRadioButton = (RadioButton) this.findViewById(R.id.TypeUser);
		adminRadioButton = (RadioButton) findViewById(R.id.TypeAdmin);
		activeRadioButton = (RadioButton) findViewById(R.id.StatusActive);
		inactiveRadioButton = (RadioButton) findViewById(R.id.StatusInactive);
		
		/* dislay the correct username */
		setTitle("Editing "+  username);
		
		/* display the current email */
		emailTextView.setText(email);
		
		/* password - nothing to fill out */
		
		/* check the correct radio button for user or admin */
		if (userType == 0) { /* if user */
			userRadioButton.setChecked(true);
			adminRadioButton.setChecked(false);
		} else if (userType == 1) { /* if admin */
			userRadioButton.setChecked(false);
			adminRadioButton.setChecked(true);
		} else {
			Log.e(TAG, "*** Error: Unable to update User Type.");
		}

				
		/* check the correct radio button if user is active or inactive */
		/* 0 = inactive; 1 = active*/
		if (userStatus == 1) { /* if active */
			activeRadioButton.setChecked(true);
			inactiveRadioButton.setChecked(false);
		} else if (userStatus == 0) { /* if inactive */
			activeRadioButton.setChecked(false);
			inactiveRadioButton.setChecked(true);
		} else {
			Log.e(TAG, "*** Error: Unable to update User Status.");
		}
		
	}

	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
	}
	
	/**
	 * Update all user information and send it to the database.
	 */
	public void update(View view) {
		/**
		 * UPDATE table_name
		 * SET column1=...
		 * WHERE some_column=some_value;
		 */
		/* create a string to save the set part of the query */
		String set = "set ";
		int prev = 0;
		
		/* ------------ UPDATE USER EMAIL ------------ */
		if (!email.equals(emailTextView.getText().toString())) {
			set += " Email = '" + emailTextView.getText().toString() + "'";
			prev = 1;
		}
		/* ------------ UPDATE USER PASSWORD ------------ */
		if (!passwordTextView1.getText().toString().isEmpty() ){
			if ( !passwordTextView2.getText().toString().isEmpty() ){
				if(passwordTextView1.getText().toString().equals(passwordTextView2.getText().toString())){
					if(prev == 1){
						set += ", ";
					}
					set += "Password = '" + CryptoStuff.hashPWSha256(passwordTextView1.getText().toString()) + "'";
					prev = 1;
				}else{
					Toast.makeText(getBaseContext(), "Passwords don't match!", 5).show();
					return;
				}
			}else{
				Toast.makeText(getBaseContext(), "Please confirm password", 5).show();
				return;
			}
		}
		/* ------------ UPDATE USER TYPE ------------ */
		if (userType == 1  && userRadioButton.isChecked()) {
			if(prev == 1){
				set += ", ";
			}
			set += "userType = 0";
			prev = 1;
		} else {
			if(userType == 0 && adminRadioButton.isChecked()){
				if(prev == 1){
					set += ", ";
				}
				set += "userType = 1";
				prev = 1;
			}
		}

		/* ------------ UPDATE USER STATUS ------------ */
		if (userStatus == 1 && inactiveRadioButton.isChecked()) {
			if(prev == 1){
				set += ", ";
			}
			set += " userStatus = 0";
			prev = 1;
		} else {
			if(userStatus == 0 && activeRadioButton.isChecked()){
				if(prev == 1){
					set += ", ";
				}
				set += " userStatus = 1";
				prev = 1;
			}
		}
		
		
		query = "update USER " + set + " where userID = " + userID;
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, UPDATE_USER, this,
				Variables.getRest(), null).execute();
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			/* We got here from searchUser */
			if (code == UPDATE_USER) {
				if (result != null
						&& !result.isNull("response_status")
						&& result.getString("response_status")
								.equalsIgnoreCase("success")) {
					
					
					/* Print out that update was successful */
					Toast.makeText(getApplicationContext(),
							"Successfully updated user", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(this, UserForm.class);
					startActivity(intent);
					
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"Unable to update user", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	} /* end onQueryTaskCompleted(int code, JSONObject result) */

	
} /* end EditUser class*/
