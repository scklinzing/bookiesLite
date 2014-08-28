package com.bookies.bookkeeper;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

/**
 * Adds a book from the application library to the users personal
 * library.  
 * 
 * Uses the ISBN and userID variables to run a query to pull all
 * needed information.  Creates and intent and sends information 
 * to EditPersonalBook.java for user to edit book.
 *  
 * Heather Persson
 */

public class AddBookFromAppLib extends ActionBarActivity implements QueryCallback  {
		//used by caller to identify between multiple queries if 1 handler for all of them
		private static final int ADD_BOOK_QUERY = 0;
		
		//extras for intent
		public final static String EXTRA_ISBN = "com.bookies.bookkeeper.ISBN";
		public final static String EXTRA_AUTHOR = "com.bookies.bookkeeper.AUTHOR";
		public final static String EXTRA_TITLE = "com.bookies.bookkeeper.TITLE";
//		public final static String EXTRA_RATING = "com.bookies.bookkeeper.RATING";
//		public final static String EXTRA_STATUS = "com.bookies.bookkeeper.STATUS";
//		public final static String EXTRA_COMMENT = "com.bookies.bookkeeper.COMMENT";
//		public final static String EXTRA_SECURITY = "com.bookies.bookkeeper.SECURITY";
//		public final static String EXTRA_ID = "com.bookies.bookkeeper.ID";
//		public final static String EXTRA_DATE = "com.bookies.bookkeeper.DATE";
		public final static String EXTRA_FROM = "1";
		
				
		//for log identification
		private static final String TAG = "AddBookFromAp";
		
		private String ISBN;
		private int userId;
						
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//setContentView(R.layout.edit_per_book);
			
			//pulling from Variables
			ISBN = Variables.getIsbn();
			userId = Variables.getUserId();
			
			//query
			String query = "select Author, Title from BOOK where ISBN =\"" + ISBN + "\"";
			Log.d(TAG, "Query: "+ query);
			
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), 
					query, ADD_BOOK_QUERY, this, Variables.getRest(), null).execute();		
		}

		@Override
		public void onQueryTaskCompleted(int code, JSONObject result) {
			try {
				if(code == ADD_BOOK_QUERY) {  
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
							Log.d(TAG, "JSON: "+ object);
							
							Intent next = new Intent(this, EditPersonalBook.class);	
							
							//pull from JSON object
							
							String Author = object.getString("Author");
							String Title = object.getString("Title");
//							String Rating = object.getString("Rating");
//							String Status = object.getString("Status");
//							String Comment = object.getString("Comments");
//							String Security = object.getString("bookSecurity");
//							String Date = object.getString("dateRead");
							String From = "1";
							
							//store to pass to next screen
							next.putExtra(EXTRA_ISBN, ISBN);
							next.putExtra(EXTRA_AUTHOR, Author);
							next.putExtra(EXTRA_TITLE, Title);
//							next.putExtra(EXTRA_RATING, Rating);
//							next.putExtra(EXTRA_STATUS, Status);
//							next.putExtra(EXTRA_COMMENT, Comment);
//							next.putExtra(EXTRA_SECURITY, Security);
//							next.putExtra(EXTRA_DATE, Date);
							next.putExtra(EXTRA_FROM, From);
		
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
