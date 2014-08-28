package com.bookies.bookkeeper;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import supportClasses.SelectableUser;
import supportClasses.User;
import supportClasses.UserList;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

public class Recommendation extends Activity implements QueryCallback{
	private static ArrayList<SelectableUser> friends = null; // this lists the friends you can recommend the book to!
		// we can pull just the friends that don't have the book! >.> thank you Laura for 
		//finally realizing that.... 
	private static String isbn;
	private static final int QUERY_FRIENDS = 2;
	private static final int QUERY_SEND_REQUEST = 12;
	private BaseAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		isbn = intent.getStringExtra("ISBN");
		//call query to get friendlist for ISBN - can have multiple recommendations for same book
		//all recommendations will be deleted together
		String query = "select distinct USER.* from USER join (" +
						"select FriendList.FriendUserID from FriendList where  FriendList.UserID = "
						+ Variables.getUserId() +" and FriendList.FriendUserID not in (select " +
						"FriendList.FriendUserID from FriendList join USER_LIB on FriendList.FriendUserID" + 
						" = USER_LIB.UserID where USER_LIB.ISBN = " + isbn + 
		        	"))as rec on USER.UserID = rec.FriendUserID"; //end internal select add on
					//somehow this works on the sql server, lol, heres hoping it works here too.
					//yay for nested queries!
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_FRIENDS, this, Variables.getRest(), null).execute();
	}
	private void ApplySettings(){
		setContentView(R.layout.recommend);
		ListView view = (ListView)findViewById(R.id.recommendFriendsList);
		adapter = new RecommendAdapter(this, friends);
		view.setAdapter(adapter);
		//setListAdapter(adapter);
		
	}
	public void sendRecommendation( View view ) {
		String comment;
		String query;
		ArrayList<SelectableUser> selected = new ArrayList<SelectableUser>();
		comment = ((TextView)findViewById(R.id.commentText)).getText().toString();
		RecommendAdapter a = (RecommendAdapter)adapter;
		for(int i=0; i<a.users.size();i++){
			if(a.users.get(i).getSelected()){
				selected.add(a.users.get(i));
			}
		}
		if(selected.size() < 1){
			Toast.makeText(this, "You need to select at least one friend to receive this recommendation.", 5).show();
			return;
		}
		
		query = "insert into Recommendations (RecommenderID, BookID, Comment, FriendID) "+
					"select " + Variables.getUserId() + " as recID, " + isbn + " as isbn, '" +
					comment + "' as comment, USER.userID from USER where ";
		
		for(int i = 0; i < selected.size();i++){
			if(i >0) query += " or ";
			query += "userID = " + selected.get(i).getID();
		}
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SEND_REQUEST, this, Variables.getRest(), null).execute();
	}
	public void cancelRecommendation( View view ) {
		Intent intent = new Intent(this, MainForm.class);
		startActivity(intent);
		finish();
	}
	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		// TODO Auto-generated method stub
		try {
			switch(code){
				case QUERY_FRIENDS:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						friends = new ArrayList<SelectableUser>();
						int size = result.length();
						Log.d("WebQueryFriends", "Results: " + size);
						Log.d("WebQueryFriends", result.toString());
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		//decode the decoded json... 
					    		o2 = new JSONObject(result.getString(key));
					    		friends.add(new SelectableUser(o2.getInt("userID"), o2.getString("Username")));
					    	}
					    }
					    ApplySettings();
					    
					}else{
						//no records found
						Toast.makeText(getBaseContext(), "You have no friends who do not already have this book", 5).show();
						Log.d("Pending Query","No records found");						
					    Intent intent = new Intent(this, MainForm.class);
					    //mylist is default...
					    startActivity(intent);
					    finish();
					}
					
					
					break;
				case QUERY_SEND_REQUEST:
					Toast.makeText(this, "Recommendation sent!", 5).show();
					Intent intent = new Intent(this, MainForm.class);
					startActivity(intent);
					finish();					
					break;
			}//end switch statment
			
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
