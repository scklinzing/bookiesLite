package com.bookies.bookkeeper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import supportClasses.User;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FriendRequest extends Activity implements QueryCallback{
	private static final int QUERY_USER = 1;
	private static final int QUERY_ADD_REQUEST = 2;
	private static final int QUERY_CHECK_EXISTS = 3;
	private static int friendID;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_request);
	}
	public void sendRequest( View view ) {
		EditText a = (EditText)findViewById(R.id.usernameOrEmail);
		String  text = a.getText().toString();
		String sql;
		if(text.contains("@")){
			//email
			sql = "select userID from USER where Email = '" + text + "'";
		}else{
			//username
			sql = "select userID from USER where Username = '" + text + "'";
		}
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), sql, QUERY_USER, this, Variables.getRest(), null).execute();
	}
	public void cancelRequest( View view ) {
		Intent intent = new Intent(this, UserForm.class);
		intent.putExtra("VIEWING", 1);
		startActivity(intent);
	}
	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		// TODO Auto-generated method stub
		try{
			switch(code){
				case QUERY_USER:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int userID = -1;
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	//System.out.println("key = " + key);
					    	//is next value a String or a JSONObject?
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		//decode the decoded json... 
					    		o2 = new JSONObject(result.getString(key));
								userID =o2.getInt("userID");
					    	}
					    }//end while loop
					    if(userID == -1){
					    	Toast.makeText(getBaseContext(), "No such user", 5).show();
					    	return;
					    }
					    friendID = userID;
					    String sql = "select * from FriendList where ((UserID = " + Variables.getUserId() + " and FriendUserID = " +
					    			friendID + ") or (UserID = " + friendID + " and FriendUserID = " + Variables.getUserId() + "))";
					    new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), sql, QUERY_CHECK_EXISTS, this, Variables.getRest(), null).execute();
						}else{
						Toast.makeText(getBaseContext(), "No such user", 5).show();
					}
					break;
				case QUERY_CHECK_EXISTS:
					
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						Toast.makeText(getBaseContext(), "There is already a pending request for this user", 5).show();
						((TextView)findViewById(R.id.usernameOrEmail)).setText("");
					}else{
						String sql = "insert into FriendList " +
								"(UserID, FriendUserID, Status) values (" + Variables.getUserId() + ", "
								 + friendID + ", 0)";
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), sql, QUERY_ADD_REQUEST, this, Variables.getRest(), null).execute();
					}
					break;
				case QUERY_ADD_REQUEST:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						Toast.makeText(getBaseContext(), "Request sent!", 5).show();
						((TextView)findViewById(R.id.usernameOrEmail)).setText("");
					}else{
						Toast.makeText(getBaseContext(), "Error adding request", 5).show();
					}
					break;
				default:
					Log.e("FriendRequestQuery", "Unknown code");
					Toast.makeText(getBaseContext(), "Could not process request", 5).show();
			}
			
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
