package com.bookies.bookkeeper;
/**
 * Used to search the BOOK table for a book by the ISBN number
 * 
 * Heather Persson
 */

import org.json.JSONException;
import org.json.JSONObject;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


@SuppressLint("NewApi")
public class AppBookSearch extends ActionBarActivity implements QueryCallback {
	
	//get words that we are searching for
	private String searchIsbn;
	private String query;

	//for log identification
	private static final String TAG = "BookSearch";

	//used by caller to identify between multiple queries if 1 handler for all of them
	private static final int QUERY_SELECT_TEST = 0;
	
	//holding book info to pass
	public final static String EXTRA_ISBN = "com.bookies.bookkeeper.ISBN";
	public final static String EXTRA_AUTHOR = "com.bookies.bookkeeper.AUTHOR";
	public final static String EXTRA_TITLE = "com.bookies.bookkeeper.TITLE";
	public final static String EXTRA_SCANNED = "com.bookies.bookkeeper.SCANNED";
	public final static String EXTRA_NUMOFRATINGS = "com.bookies.bookkeeper.NUMOFRATINGS";
	public final static String EXTRA_SUMOFRATINGS = "com.bookies.bookkeeper.SUMOFRATINGS";
	public final static String EXTRA_STATUS = "com.bookies.bookkeeper.STATUS";
	
	/* 
	 * on create set the view to search_app_books.xml(non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_app_books);
    }
	
	/*
	 * Pulls ISBN for search, logs search, and runs query
	 */
	public void appBookSearch(View view){	
		//field on form that we are pulling from
		EditText searchISBN = (EditText)findViewById(R.id.AppBookSearchTextView);	
		//pulling ISBN to search for
		searchIsbn = searchISBN.getText().toString();
		//construct query
		query = "select * from BOOK where ISBN = \"" + searchIsbn + "\"";
		//log query
		Log.d(TAG, "Query = " + query);
		
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), 
				query, QUERY_SELECT_TEST, this, Variables.getRest(), null).execute();
	}

	//displays menu with admin tasks (view book list, edit user, edit book information)
	public boolean onCreateOptionsMenu(Menu menu) {
		invalidateOptionsMenu();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.admin_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//handles menu selection
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.viewBookList:
	        	Intent list = new Intent( this, MainForm.class);
				list.putExtra(Login.EXTRA_USERID, Variables.getUserId());
				startActivity(list);
	            return true;
	        case R.id.editBook:
	        	Intent editBook = new Intent( this, AppBookSearch.class);
				startActivity(editBook);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		//log result
		Log.d(TAG, "Result: " + result);
		
		try {
			if(code == QUERY_SELECT_TEST) {  
				if(result != null && !result.isNull("response_status") 
						&& result.getString("response_status").equalsIgnoreCase("success")) {
					int row = 0;
					int size = result.length();
					//log size
					Log.d(TAG , "Results: " + size);
				
					//get number of rows
					while (!result.isNull(Integer.toString(row))) {
						row++;
					}
					//log rows
					Log.d(TAG, "Rows= " + row);
					
					//there should only be one result - this is true if a book is returned
					if(row == 1){
						JSONObject object = new JSONObject(result.getString(Integer.toString(0)));
						Intent next = new Intent(this, EditBook.class);	
						
						//pull from JSON object
						String ISBN = object.getString("ISBN");
						String Author = object.getString("Author");
						String Title = object.getString("Title");
						Log.d(TAG, "Title: " + Title);
						String Scanned = object.getString("Scanned");
						Log.d(TAG, "Scanned: " + Scanned);
						String numOfRatings = object.getString("numOfRatings");
						String sumOfRatings = object.getString("sumOfRatings");
						String bookStatus = object.getString("bookStatus");
						
						//store to pass to next screen
						next.putExtra(EXTRA_ISBN, ISBN);
						next.putExtra(EXTRA_AUTHOR, Author);
						next.putExtra(EXTRA_TITLE, Title);
						next.putExtra(EXTRA_SCANNED, Scanned);
						next.putExtra(EXTRA_NUMOFRATINGS, numOfRatings);
						next.putExtra(EXTRA_SUMOFRATINGS, sumOfRatings);
						next.putExtra(EXTRA_STATUS, bookStatus);
						
						this.startActivity(next);
						
						}
				}
					//book not found by ISBN
					else {
						/* Toast is error messaging */
						Toast.makeText(getApplicationContext(),
								"Book Not Found", Toast.LENGTH_LONG).show();
						Log.e(TAG, "*** Error: " + result);
					}	
				}else {
					Log.e(TAG, "*** Error: unknown code");
				}
			
		}
		 catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

