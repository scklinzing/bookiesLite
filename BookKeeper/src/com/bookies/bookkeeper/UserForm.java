package com.bookies.bookkeeper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.madmarcos.resttest.LoginCallback;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import supportClasses.*;
import utility.CryptoStuff;
import utility.JSONStuff;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

import org.json.*;


public class UserForm extends ExpandableListActivity implements QueryCallback {
	private static UserList friends = null;//confirmed friends
	private static UserList pendingFriends = null; //friends pending
	private static UserList pendingRequests = null; // requests for them
	private static UserList mainUserList = null;
	private ArrayList<ExpandableUser> parentItems = new ArrayList<ExpandableUser>();
	private int viewingList = 0; 
	//0= all users
	//1- friends list
	public static int editingUser = 0;
	
	SharedPreferences prefs;
	private PreferenceChangeListener mPreferenceListener = null;

	private static final int QUERY_USER_LIST = 1;
	private static final int QUERY_FRIENDS = 2;
	private static final int QUERY_PENDING = 3;
	private static final int QUERY_ACCEPT_REQUEST = 4;
	private static final int QUERY_REJECT_REQUEST = 5;
	private static final int QUERY_ADD_YOUR_FRIEND = 6;
	//List<Map<String, String>> rows;
	public static UserList getFriends(){
		return friends;
	}
	public static UserList getUserList(){
		return mainUserList;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		
		 //GET INTENT
		 Intent intent = getIntent();
		 int view = intent.getIntExtra("VIEWING", -1);
		 //CALL PROPER QUERY
		 switch(view){
		 	case 1:
		 		setFriendList();
		 		break;
		 	default:
		 		setUserList(); //replaced get main list
		 }
		  // The rest of the activity will be setup when the web returns
		
		 
		 prefs = PreferenceManager.getDefaultSharedPreferences(this);
		 mPreferenceListener = new PreferenceChangeListener();
		 prefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
	}
	private class PreferenceChangeListener implements
	   OnSharedPreferenceChangeListener {
	  @Override
	  public void onSharedPreferenceChanged(SharedPreferences prefs,
	    String key) {
	   ApplySettings();
	  }
	 }
	
	public void ApplySettings() {
		  // edittext preference
		 parentItems = new ArrayList<ExpandableUser>();
		 ExpandableListView expandableList = getExpandableListView();
		 expandableList.setDividerHeight(2);
		 expandableList.setGroupIndicator(null);
		 expandableList.setClickable(true);
		 		 
		 setData();
		 switch(viewingList){
		 	case 0:
		 		setTitle("All Users");
		 		break;
		 	default:
		 		setTitle("Friends List");
		 }
		 ExpandableUserAdapter adapter = new ExpandableUserAdapter(parentItems);
		 
		 adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		 expandableList.setAdapter(adapter);
		 expandableList.setOnChildClickListener((OnChildClickListener) this);
	}
	public void setFriendList(){
		//this method pulls the book list from the web. Since we don't currently have this set up...
		String query = "select USER.*, FriendList.* "+
		"from USER join FriendList on USER.userID = FriendList.FriendUserID "+
		"where FriendList.UserID = " + Variables.getUserId();
		Log.d("Userform", "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_FRIENDS, this, Variables.getRest(), null).execute();
		
		
	}
	public void setUserList(){
		
		String query = "select USER.* from USER";
		Log.d("Mainform", "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_USER_LIST, this, Variables.getRest(), null).execute();
		
	}
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			
			switch(code){
				case QUERY_USER_LIST:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int size = result.length();
						Log.d("WebQueryUserList", "Results: " + size);
						Log.d("WebQueryUserList", result.toString());
						ArrayList<User> list = new ArrayList<User>();
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	//System.out.println("key = " + key);
					    	//is next value a String or a JSONObject?
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		//decode the decoded json... 
					    		SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd");
					    		o2 = new JSONObject(result.getString(key));
					    		try {
									list.add(new User(o2.getInt("userID"), o2.getString("Username"), o2.getString("Email"),parser.parse(o2.getString("userStartDate")),  o2.getInt("userStatus"), o2.getInt("userType")));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					    		
					    	}
					    }
					    mainUserList = new UserList(list);
					    viewingList = 0;
			    		ApplySettings();
					    //end of while, add some nonsense values so we can look at m...
					   
					} else {
						Toast.makeText(getApplicationContext(), "No users found", Toast.LENGTH_LONG).show();
						Log.e("WebQuery", "no users: " + result);
					}
				break;
				case QUERY_FRIENDS:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int size = result.length();
						Log.d("WebQueryFriends", "Results: " + size);
						Log.d("WebQueryFriends", result.toString());
						ArrayList<User> friendList = new ArrayList<User>();
						ArrayList<User> pendingFriendList = new ArrayList<User>();
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
					    		//Fill in the 2 friends list for accepted friends and 
					    		//pending friends - second query required to pull up your pending requests
					    		if(o2.getInt("Status") == 0){//pending
					    			pendingFriendList.add(new User(o2.getInt("FriendUserID"), o2.getString("Username")));
					    		}else{
					    			friendList.add(new User(o2.getInt("FriendUserID"), o2.getString("Username")));
					    		}
					    	}
					    }
					    friends = new UserList(friendList);
					    pendingFriends = new UserList(pendingFriendList);
					    viewingList = 1;
					    String query = "select USER.*, FriendList.*"+
					    		"from USER join FriendList on USER.userID = FriendList.UserID "+
					    		"where FriendList.FriendUserID = " + Variables.getUserId() + " and FriendList.Status = 0";
					    Log.d("UserformQueryFriends", "Query = " + query);
					    new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_PENDING, this, Variables.getRest(), null).execute();
					    		
					    //end of while, add some nonsense values so we can look at m...
					   
					}else{
						friends = new UserList(new ArrayList<User>());
					    pendingFriends = new UserList(new ArrayList<User>());
					    viewingList = 1;
					    String query = "select USER.*, FriendList.*"+
					    		"from USER join FriendList on USER.userID = FriendList.UserID "+
					    		"where FriendList.FriendUserID = " + Variables.getUserId() + " and FriendList.Status = 0";
					    Log.d("UserformQueryFriends", "Query = " + query);
					    new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_PENDING, this, Variables.getRest(), null).execute();
					}
					break;
				case QUERY_PENDING:
					Log.d("Query Pending","Result = " + result.toString());
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int size = result.length();
						Log.d("WebQueryFriends", "Results: " + size);
						Log.d("WebQueryFriends", result.toString());
						ArrayList<User> pending = new ArrayList<User>();
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
					    		//Fill in the 2 friends list for accepted friends and 
					    		//pending friends - second query required to pull up your pending requests
					    		pending.add(new User(o2.getInt("UserID"), o2.getString("Username")));
					    		
					    	}
					    }
					    pendingRequests = new UserList(pending);
					    ApplySettings();
					    
					}else{
						//no records found
						Log.d("Pending Query","No records found");
						pendingRequests = new UserList(new ArrayList<User>());
					    ApplySettings();
					}
					break;
				case QUERY_ACCEPT_REQUEST:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						String query = "insert into FriendList (UserID, FriendUserID, Status)" +
								" values (" + Variables.getUserId() + "," + editingUser + ", 1)";
						Log.d("QueryAcceptRequest", "Query: " + query);
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_ADD_YOUR_FRIEND, this, Variables.getRest(), null).execute();

					}else{
						//no records found
						Log.e("Accept Request","ERROR");
						Toast.makeText(this.getBaseContext(), "Could not process request", 5).show();
					}//end else not "Accept query
					break;
				case QUERY_REJECT_REQUEST:
					Log.d("Query Pending","Result = " + result.toString());
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						setFriendList();
					}else{
						//no records found
						Log.e("Reject Request","ERROR");
						Toast.makeText(this.getBaseContext(), "Could not process request", 5).show();
					}//end else not "Accept query
					break;
				case QUERY_ADD_YOUR_FRIEND:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						setFriendList();
					}else{
						//no records found
						Log.e("Add Accepted","ERROR");
						Toast.makeText(this.getBaseContext(), "Could not process request", 5).show();
					}//end else not "Accept query
					break;
				default:
					Toast.makeText(this.getBaseContext(), "Error: Could not process request", 5).show();
			}//end switch statment
			
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void setData() {
		Log.d("UserList: ", "Entered set data");
		//call to pull book information from server and file into Listing
		ArrayList<ExpandableChild> child;
		//apply filters and sorts -- this can be added later
		//char arrange = Prefs.getArrangeBy(getBaseContext());
		//char[] filter = new char[2];
		//filter[0] = Prefs.getFilterByStatus(getBaseContext());
		//filter[1] = Prefs.getFilterByRating(getBaseContext());
		//String title = Prefs.getTitleSearch(getBaseContext());
		//if(title.equals("")) title = null;
		//String author = Prefs.getAuthorSearch(getBaseContext());
		//if(author.equals("")) author = null;
		//Run sort and filter on the list
		
		if(viewingList == 0){
			//viewing the main user list
			ArrayList<User> listing = mainUserList.getList();
		//	listing = bookList.getList(filter, arrange, title, author);
			for(int i = 0; i < listing.size(); i++){
				//Set first item based off of sort
				ExpandableUser parent;
				child = new ArrayList<ExpandableChild>();
				child.add(new ExpandableChild(listing.get(i).getID() + "" , false));
				child.add(new ExpandableChild("E-mail Address: " + listing.get(i).getEmail() , false));
				DateFormat dtStr = new SimpleDateFormat("mm/dd/yyyy");
				String dt = dtStr.format(listing.get(i).getStartDate());
				if(!dt.equals("00/31/0002")){
					child.add(new ExpandableChild("Start Date: "+ dt, false));
				}else{
					child.add(new ExpandableChild("Start Date: Not Set", false));
				}
				switch(listing.get(i).getUserType()){
					case 1:
						child.add(new ExpandableChild("User Type: Admin", false));
						break;
					default:
						child.add(new ExpandableChild("User Type: User", false));
				}
				switch(listing.get(i).getStatus()){
					case 1:
						child.add(new ExpandableChild("User Status: Active", false));
						break;
					default:
						child.add(new ExpandableChild("User Status: Inactive", false));
				}
				//add admin buttons
				child.add(new ExpandableChild("Edit User" , true));
				child.add(new ExpandableChild("View User Book List" , true));
				parent = new ExpandableUser(listing.get(i).getUserName());
				parent.setChildren(child);
				parentItems.add(parent);
			}//end for loop
		}else{
			//viewing Friend's List
			ArrayList<User> friendsList = friends.getList();
			ArrayList<User> pendingFriendsList = pendingFriends.getList();
			ArrayList<User> pendingRequestsList = pendingRequests.getList();
		//	listing = mainBookList.getList(filter, arrange, title, author);
			if(friendsList.size() == 0){
				parentItems.add(new ExpandableUser("You have no friends :("));
			}else{
				parentItems.add(new ExpandableUser("Current Friends"));
			}
			
			for(int i = 0; i < friendsList.size(); i++){
				//Set first item based off of sort
				ExpandableUser parent;
				child = new ArrayList<ExpandableChild>();
				child.add(new ExpandableChild(friendsList.get(i).getID() + "" , false));
				//add admin buttons
				child.add(new ExpandableChild("View User Book List" , true));
				parent = new ExpandableUser(friendsList.get(i).getUserName());
				parent.setChildren(child);
				parentItems.add(parent);
			}//end for loop
			if(pendingFriendsList.size() != 0){
				parentItems.add(new ExpandableUser("Pending Friend Requests"));
			}
			
			for(int i = 0; i < pendingFriendsList.size(); i++){
				//Set first item based off of sort
				ExpandableUser parent;
				child = new ArrayList<ExpandableChild>();
				child.add(new ExpandableChild(pendingFriendsList.get(i).getID() + "" , false));
				//add admin buttons
				parent = new ExpandableUser(pendingFriendsList.get(i).getUserName());
				parent.setChildren(child);
				parentItems.add(parent);
			}//end for loop
			if(pendingRequestsList.size() == 0){
				parentItems.add(new ExpandableUser("No friend requests pending your acceptance"));
			}else{
				parentItems.add(new ExpandableUser("Friend Requests Pending Your Acceptance"));
			}
			
			for(int i = 0; i < pendingRequestsList.size(); i++){
				//Set first item based off of sort
				ExpandableUser parent;
				child = new ArrayList<ExpandableChild>();
				child.add(new ExpandableChild(pendingRequestsList.get(i).getID() + "" , false));
				//add admin buttons
				child.add(new ExpandableChild("Accept Request" , true));
				child.add(new ExpandableChild("Reject Request" , true));
				parent = new ExpandableUser(pendingRequestsList.get(i).getUserName());
				parent.setChildren(child);
				parentItems.add(parent);
			}//end for loop
		}
    }
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(Variables.getAdmin()){
			inflater.inflate(R.menu.user_form_admin, menu);
		}else{
			inflater.inflate(R.menu.user_form, menu);
		}
		
		// Inflate the menu; this adds items to the action bar if it is present.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case R.id.action_settings:
				Log.d("Menu", "Preferences Selected");
				//startActivity(new Intent(this, Prefs.class));
				return true;
			case R.id.viewMyList:
				Log.d("Menu", "My List Selected");
				Intent myList = new Intent( this, MainForm.class);
				myList.putExtra("VIEWING", 0);
				startActivity(myList);
				return true;
			case R.id.viewMainList:
				Log.d("Menu", "Main List Selected");
				Intent mainList = new Intent( this, MainForm.class);
				mainList.putExtra("VIEWING", 1);
				startActivity(mainList);
				return true;
	        case R.id.editUser:
	        	Intent editUser = new Intent( this, SearchUser.class);
	        	startActivity(editUser);
	            return true;
	        case R.id.editBook:
	        	Intent editBook = new Intent( this, AppBookSearch.class);
				startActivity(editBook);
	            return true;
	        case R.id.scanBook:
//	    		IntentIntegrator intentIntegrator = new IntentIntegrator(this);
//	    		intentIntegrator.initiateScan();
	        	Intent scanBook = new Intent(this, AddBookChoice.class);
	        	startActivity(scanBook);
	    		return true;
	        case R.id.change_pw:
	        	Intent changePW = new Intent( this, ChangePw.class);
	        	startActivity(changePW);
	        	return true;
	        case R.id.logOut:
	        	Variables.setAdmin(false);
	        	Variables.setUserId(-1);
	        	Intent login = new Intent(this, Login.class);
	        	login.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        	login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	//login.putExtra("EXIT", true);
	        	startActivity(login);
	        	finish();
	        	return true;
	        case R.id.viewUserList:
	        	Intent userList = new Intent( this, UserForm.class);
				userList.putExtra("VIEWING", 0);
				startActivity(userList);
				return true;
	        case R.id.viewFriendsList:
	        	Intent friendList = new Intent( this, UserForm.class);
				friendList.putExtra("VIEWING", 1);
				startActivity(friendList);
				return true;
	        case R.id.sendRequest:
	        	Intent friendRequest = new Intent(this, FriendRequest.class);
	        	startActivity(friendRequest);
	        	
		}
		return false;
	}
	
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
//				requestCode, resultCode, intent);
//
//		if (scanningResult != null) {
//			String scanFormat = scanningResult.getFormatName();
//			String isbn = scanningResult.getContents();
//			Log.d("SCAN", "content: " + isbn + " - format: "
//					+ scanFormat);
//			if (isbn != null && scanFormat != null
//					&& scanFormat.equalsIgnoreCase("EAN_13") ) {
//				
//				Intent bookSummaryIntent = new Intent(this, BookSummary.class);
//				bookSummaryIntent.putExtra(MainForm.EXTRA_SCAN_ISBN, isbn);
//				bookSummaryIntent.putExtra(MainForm.EXTRA_SCAN_FORMAT, scanFormat);
//				startActivity(bookSummaryIntent);
//				
//			} else {
//				Toast.makeText(getApplicationContext(), "Not A Valid Scan!",
//						Toast.LENGTH_SHORT).show();
//			}
//		} else {
//			Toast.makeText(getApplicationContext(), "No scan data received!",
//					Toast.LENGTH_SHORT).show();
//		}
//	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	@SuppressLint("NewApi")
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_form,
					container, false);
			return rootView;
		}
	}
		
}
