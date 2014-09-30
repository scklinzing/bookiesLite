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

public class MainForm extends ExpandableListActivity implements QueryCallback {
	private static BookList bookList = null;//personal
	private static BookList mainBookList = null;
	private ArrayList<ExpandableParent> parentItems = new ArrayList<ExpandableParent>();
	private static int viewingList = 0; 
	public static String isbn;
	/* viewingList - 0 mylist; 1 mainlist; 2 friend list */
	
	SharedPreferences prefs;
	private PreferenceChangeListener mPreferenceListener = null;

	private static final int QUERY_SELECT_TEST = 1;
	private static final int QUERY_MAINLIST = 2;
	private static final int QUERY_DELETE_REC_AA = 6; /* I'll get rid of this later */
	private static final int QUERY_ADD_BOOK = 10;
	List<Map<String, String>> rows;
	public static BookList getMyList(){
		return bookList;
	}
	public static BookList getMainList(){
		//Warning MAY RETURN NULL
		return mainBookList;
	}
	public static int getViewing(){
		return viewingList;
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
		 	case 0:
		 		getMyBookList();
		 		break;
		 	case 1:
		 		getAppBookList(); //replaced get main list
		 		break;
		 	default:
		 		//-1 etc
		 		getMyBookList();
		 }
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
		 setTitle(); // set the title of the page
		
		 MyExpandableAdapter adapter = new MyExpandableAdapter(parentItems, findViewById(R.id.progressBar));
		 
		 adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		 expandableList.setAdapter(adapter);
		 expandableList.setOnChildClickListener((OnChildClickListener) this);
	}
	/* Display the title at the top of the page */
	public void setTitle(){
		switch(viewingList){
	 	case 0:
	 		setTitle("My Book List");
	 		break;
	 	case 1:
	 		setTitle("App Book List");
	 		break;
		}
	}
	/* get My Personal Book List */
	public void getMyBookList(){
		bookList = null;
		//this method pulls the book list from the web. Since we don't currently have this set up...
		String query = "select USER_LIB.*, BOOK.*"+
		"from USER_LIB join BOOK on USER_LIB.ISBN = BOOK.ISBN "+
		"where USER_LIB.userID = " + Variables.getUserId();
		Log.d("Mainform getMyList", "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_SELECT_TEST, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
	}
	/* get the App Book List */
	public void getAppBookList(){
		mainBookList = null;
		//this method pulls the book list from the web. Since we don't currently have this set up...
		String query = "select BOOK.ISBN, BOOK.Title, BOOK.Author, BOOK.numOfRatings, BOOK.sumOfRatings "+
		"from BOOK where BOOK.bookStatus = 1";
		Log.d("Mainform getMainList", "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_MAINLIST, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
		
	}
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			switch(code){
				case QUERY_SELECT_TEST:
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int size = result.length();
						Log.d("WebQuery SELECT_TEST", "Results: " + size);
						Log.d("WebQuery SELECT_TEST Str", result.toString());
						ArrayList<BookInfo> listing = new ArrayList<BookInfo>();
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	// is next value a String or a JSONObject?
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		// decode the decoded json... 
					    		SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd");
					    		o2 = new JSONObject(result.getString(key));
					    		try {
									listing.add(new BookInfo(o2.getString("ISBN"), o2.getString("Title"),o2.getString("Author"), o2.getInt("Rating"), o2.getInt("Status"), parser.parse(o2.getString("dateRead")), o2.getString("Comments"), o2.getInt("bookSecurity")));
								} catch (ParseException e) {
									e.printStackTrace();
								}
					    	}
					    }
					    bookList = new BookList(listing);
					    viewingList = 0;
					    ApplySettings();
					} else {
						Toast.makeText(getApplicationContext(), "No Books Found", Toast.LENGTH_LONG).show();
						Log.d("WebQuery SELECT_TEST No Books Found", "No books found " + result);
						bookList = new BookList(new ArrayList<BookInfo>());
					}
					break; /* end SELECT_TEST */
				case QUERY_MAINLIST: /* display App Library */
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						int row = 0;
						int size = result.length();
						Log.d("WebQuery MAINLIST", "Results: " + size);
						Log.d("WebQuery MAINLIST Str", result.toString());
						ArrayList<BookInfo> listing = new ArrayList<BookInfo>();
						JSONObject o2;
						Iterator<String> keys = result.keys();
					    while (keys.hasNext()){
					    	String key = keys.next();
					    	// is next value a String or a JSONObject?
					    	if(key.equals("checksum") || key.equals("response_status")) continue;
					    	if(utility.JSONStuff.isNumericInt(key)){
					    		//decode the decoded json... 
					    		o2 = new JSONObject(result.getString(key));
					    		int rating;
					    		if(o2.getInt("numOfRatings") == 0){
					    			rating = 0;
					    		}else{
					    		   rating = o2.getInt("sumOfRatings") / o2.getInt("numOfRatings");
					    		}
					    		listing.add(new BookInfo(o2.getString("ISBN"), o2.getString("Title"),o2.getString("Author"), rating, 4 ));
					    	}
					    }
					    mainBookList = new BookList(listing);
					    updateMainList();
					    viewingList = 1;
			    		ApplySettings();
					} else {
						Toast.makeText(getApplicationContext(), "No Books Founds.", Toast.LENGTH_LONG).show();
						Log.d("WebQuery MAINLIST No Books Found", "No Books found " + result);
						setTitle();
					}
					break;
				case QUERY_ADD_BOOK: /* add a book */
					if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
						Log.d("WebQuery ADD_BOOK", "Added BOOK YAY!");
						Toast.makeText(getApplicationContext(), "Added Book to Library", Toast.LENGTH_LONG).show();
						/*String query = "delete from Recommendations where BookID = " + Variables.getIsbn() + " and FriendID = " + Variables.getUserId(); //isbn is in first child
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_DELETE_REC_AA, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();*/
					} else {
						Toast.makeText(getApplicationContext(), "Could not add", Toast.LENGTH_LONG).show();
						Log.e("WebQuery ADD_BOOK", "*** Error: " + result);
					}
					break;
				/*case QUERY_DELETE_REC_AA: // DELETE LATER 
					break;*/
				default:
					Log.e("WebQuery FRIEND_LIST", "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void updateMainList(){
		//this method goes through the "main" list for each book in "my" list to set the correct status.
		//for a full sized app- mainBookList would need to be ordered by ISBN with a binary search
		//since we are working with < 10,000 items.... >.>;
		//we can order by using the select statment if anyone thinks we should.
		Log.d("Mainform updateMainList","Entered updateMainList");
		if(bookList == null){
			Log.d("Mainform mainlist NULL", "myList, my list!!! IT'S NULL!!!!!!!");
			return;
		}
		String isbn;
		BookInfo a;
		
		for(int i = 0; i < bookList.getList().size(); i++){
			isbn = bookList.getList().get(i).getBookID();
			mainFor:
			for(int j=0; j<mainBookList.getList().size(); j++){
				a = mainBookList.getList().get(j);
				if(a.getBookID().equals(isbn)){
					a.setStatus(bookList.getList().get(i).getBookStatus());
					break mainFor; // break out of the for-loop because we have our book.
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	public void setData() {
		Log.d("Mainform setData", "Entered set data");
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
		ArrayList<BookInfo> listing;
		switch(viewingList){
			case 1:
				listing = mainBookList.getList(filter, arrange, title, author);
				break;
			default:
				listing = bookList.getList(filter, arrange, title, author);
		}
						
		//fill in the parent and child fields.
		for(int i = 0; i < listing.size(); i++){
			//Set first item based off of sort
			String icon = "booklist";// temp value to be overwritten
			ExpandableParent parent;
			child = new ArrayList<ExpandableChild>();
			child.add(new ExpandableChild(listing.get(i).getBookID() + "" , false));
			if(listing.get(i).getRating() == -1){
				child.add(new ExpandableChild("Rating: Not yet rated", false));
			}else{
				child.add(new ExpandableChild("Rating: " + listing.get(i).getRating(), false));
			}
			if(viewingList == 0){ /* My Book List */
				DateFormat dtStr = new SimpleDateFormat("mm/dd/yyyy");
				String dt = dtStr.format(listing.get(i).getDateRead());
				if(!dt.equals("00/31/0002")){
					child.add(new ExpandableChild("Read Date: "+ dt, false));
				}else{
					child.add(new ExpandableChild("Read Date: Not Set", false));
				}
				child.add(new ExpandableChild("Your comments: "+ listing.get(i).getUserComment(), false));
			}
			/*Status
			 * 1= Read
			 * 2= Want to Read/ wish list
			 * 3= Currently Reading
			 */
			switch(listing.get(i).getBookStatus()){
				case 2:
					child.add(new ExpandableChild("Status: Wishlist", false));
					icon = "wishlist";
					child.add(new ExpandableChild("Edit Book", true));
					child.add(new ExpandableChild("View Summary", true));
					break;
				case 1:
					child.add(new ExpandableChild("Status: Read", false));
					icon = "bookread";
					child.add(new ExpandableChild("Edit Book", true));
					child.add(new ExpandableChild("View Summary", true));
					break;
				case 3:
					child.add(new ExpandableChild("Status: Reading", false));
					icon = "bookreading";
					child.add(new ExpandableChild("Edit Book", true));
					child.add(new ExpandableChild("View Summary", true));
					break;
				default:
					icon = "booklist";
					child.add(new ExpandableChild("Add to my list", true));
					child.add(new ExpandableChild("View Summary", true));
			}
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
			parentItems.add(new ExpandableParent("You have no books!!","Use the menu to add some.","booklist"));
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
				Log.d("Menu action_settings", "Preferences Selected");
				startActivity(new Intent(this, Prefs.class));
				return true;
			case R.id.viewMyList:
				Log.d("Menu viewMyList", "My List Selected");
				getMyBookList();
				return true;
			case R.id.viewMainList:
				Log.d("Menu viewMainList", "Main List Selected");
				getAppBookList();
				return true;
	        case R.id.editUser:
	        	Log.d("Menu editUser", "Main List Selected");
	        	Intent editUser = new Intent( this, SearchUser.class);
	        	startActivity(editUser);
	            return true;
	        case R.id.editBook:
	        	Log.d("Menu editBook", "Book List Selected");
	        	Intent editBook = new Intent( this, AppBookSearch.class);
				startActivity(editBook);
	            return true;
	        case R.id.scanBook:
	        	Log.d("Menu scanBook", "Main List Selected");
	        	Intent scanBook = new Intent(this, AddBookChoice.class);
	        	startActivity(scanBook);
	    		return true;
	        case R.id.logOut:
	        	Log.d("Menu logout", "Main List Selected");
	        	Variables.setAdmin(false);
	        	Variables.setUserId(-1);
	        	Intent login = new Intent(this, Login.class);
	        	login.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        	login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(login);
	        	finish();
	        	return true;
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