package com.bookies.bookkeeper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import supportClasses.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class MainForm extends ExpandableListActivity implements QueryCallback {
	private static final String TAG = "MainForm";
	private static BookList mainBookList = null; // library list
	private ArrayList<ExpandableParent> parentItems = new ArrayList<ExpandableParent>();
	public static String isbn;
	
	SharedPreferences prefs;
	private PreferenceChangeListener mPreferenceListener = null;

	private static final int QUERY_MAINLIST = 1;
	List<Map<String, String>> rows;
	public static BookList getMainList(){
		//Warning MAY RETURN NULL
		return mainBookList;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		
		getAppBookList(); // get app book list
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
	/* apply all the settings in the app */
	public void ApplySettings() {
		 parentItems = new ArrayList<ExpandableParent>();
		 ExpandableListView expandableList = getExpandableListView();
		 expandableList.setDividerHeight(2);
		 expandableList.setGroupIndicator(null);
		 expandableList.setClickable(true);
		 
		 setData(); // load specific library
		 setTitle("Library"); // set the title of the page
		
		 MyExpandableAdapter adapter = new MyExpandableAdapter(parentItems, findViewById(R.id.progressBar));
		 
		 adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		 expandableList.setAdapter(adapter);
		 expandableList.setOnChildClickListener((OnChildClickListener) this);
	}
	
	/* This method pulls the book list from the web */
	public void getAppBookList(){
		String query = "SELECT * FROM BOOK";
		Log.d(TAG, "getAppBookList() Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_MAINLIST, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
	}
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			switch(code){
				case QUERY_MAINLIST: /* display App Library */
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int size = result.length();
						Log.d(TAG, "MainList Results: " + size);
						Log.d(TAG, "String: " + result.toString());
						ArrayList<BookInfo> listing = new ArrayList<BookInfo>();
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	// is next value a String or a JSONObject?
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		//decode the decoded json... 
					    		SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd");
					    		o2 = new JSONObject(result.getString(key));
					    		try { // add the book to mainList
									listing.add(new BookInfo(o2.getString("ISBN"), o2.getString("Title"),o2.getString("Author"), o2.getInt("Rating"), o2.getInt("Status"), o2.getString("isOwned"), parser.parse(o2.getString("dateRead")), o2.getString("Comments")));
								} catch (ParseException e) {
									e.printStackTrace();
								}
					    	}
					    }
					    mainBookList = new BookList(listing);
			    		ApplySettings(); // filters
					} else {
						Toast.makeText(getApplicationContext(), "No Books Found.", Toast.LENGTH_LONG).show();
						Log.d(TAG, "MainList No Books found " + result);
						setTitle("Library"); // set the title of the page
					}
					break;
				default:
					Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	public void setData() {
		Log.d(TAG, "Entered setData()");
		/* call to pull book information from server and file into Listing */
		ArrayList<ExpandableChild> child;
		//apply filters and sorts 
		//pull preferences
		char arrange = Prefs.getArrangeBy(getBaseContext());
		char[] filter = new char[2];
		filter[0] = Prefs.getFilterByStatus(getBaseContext());
		filter[1] = Prefs.getFilterByRating(getBaseContext());
		String title = Prefs.getTitleSearch(getBaseContext());
		if(title.equals("")) title = null;
		String author = Prefs.getAuthorSearch(getBaseContext());
		if(author.equals("")) author = null;
		//Run sort and filter on the list
		ArrayList<BookInfo> listing = mainBookList.getList(filter, arrange, title, author);
						
		//fill in the parent and child fields.
		for(int i = 0; i < listing.size(); i++){
			//Set first item based off of sort
			String icon = "booklist";// temp value to be overwritten
			ExpandableParent parent;
			child = new ArrayList<ExpandableChild>();
			child.add(new ExpandableChild("ISBN: " + listing.get(i).getBookID(), false));
			/** is owned? */
			if (listing.get(i).getBookIsOwned().equals("no")){
				child.add(new ExpandableChild("Book Owned: No", false));
			} else {
				child.add(new ExpandableChild("Book Owned: Yes", false));
			}
			/*****Status*****
			 * 1= Read
			 * 2= Want to Read/ wish list
			 * 3= Currently Reading
			 */
			switch(listing.get(i).getBookStatus()){
				case 2:
					child.add(new ExpandableChild("Status: Wishlist", false));
					// check to see if read, and supply correct icon
					if (listing.get(i).getBookIsOwned().equals("yes")){
						icon = "wishlist_owned";
					} else {
						icon = "wishlist";
					}
					break;
				case 1:
					child.add(new ExpandableChild("Status: Read", false));
					// check to see if read, and supply correct icon
					if (listing.get(i).getBookIsOwned().equals("yes")){
						icon = "bookread_owned";
					} else {
						icon = "bookread";
					}
					break;
				case 3:
					child.add(new ExpandableChild("Status: Reading", false));
					// check to see if read, and supply correct icon
					if (listing.get(i).getBookIsOwned().equals("yes")){
						icon = "bookreading_owned";
					} else {
						icon = "bookreading";
					}
					break;
				default: // this should no longer happen
					icon = "booklist";
			}
			/** rating */
			if(listing.get(i).getRating() < 1){
				child.add(new ExpandableChild("Rating: Not yet rated", false));
			}else{
				child.add(new ExpandableChild("Rating: " + listing.get(i).getRating(), false));
			}
			/** date */
			DateFormat dtStr = new SimpleDateFormat("mm/dd/yyyy");
			String dt = dtStr.format(listing.get(i).getDateRead());
			if(!dt.equals("00/31/0002")){
				child.add(new ExpandableChild("Date Read: "+ dt, false));
			}else{
				child.add(new ExpandableChild("Date Read: Not Set", false));
			}
			/** comments */
			child.add(new ExpandableChild("Your comments: "+ listing.get(i).getUserComment(), false));
			/** edit book button */
			child.add(new ExpandableChild("Edit Book", true));
			/** edit summary button */
			child.add(new ExpandableChild("View Summary", true));
			
			switch(arrange){
			case 'a':
				parent = new ExpandableParent(listing.get(i).getBookAuthor(), listing.get(i).getBookName(), icon);
				break;
			case 'r':
				parent = new ExpandableParent("Rating: " + listing.get(i).getRating(), listing.get(i).getBookName() + " by " + listing.get(i).getBookAuthor(), icon);
				break;
			default:
				parent = new ExpandableParent(listing.get(i).getBookName(), listing.get(i).getBookAuthor(), icon);
				//includes status and title as they will be the same
		}
			parent.setChildren(child);
			parentItems.add(parent);
		}//end for
		if(parentItems.isEmpty()){
			parentItems.add(new ExpandableParent("You have no books!","Add some.","booklist"));
		}
    }
	
	protected boolean validDate(String date, DateFormat df){
		try {
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
	}
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(Variables.getAdmin()){
			inflater.inflate(R.menu.main_form_admin, menu);
		}else{
			inflater.inflate(R.menu.main_form, menu);
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
				Log.d(TAG, "Preferences Selected");
				startActivity(new Intent(this, Prefs.class));
				return true;
			/*case R.id.viewMyList:
				Log.d(TAG, "My List Selected");
				//getMyBookList();
				return true;
	        case R.id.editBook:
	        	Log.d(TAG, "Book List Selected");
	        	Intent editBook = new Intent( this, AppBookSearch.class);
				startActivity(editBook);
	            return true;
	        case R.id.scanBook:
	        	Log.d(TAG, "Main List Selected");
	        	Intent scanBook = new Intent(this, AddBookChoice.class);
	        	startActivity(scanBook);
	    		return true;*/
		}
		return false;
	}
	
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