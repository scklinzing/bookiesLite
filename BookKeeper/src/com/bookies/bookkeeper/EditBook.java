package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;

import utility.CryptoStuff;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

/**
 * Updates fields in BOOK table with data entered into edit_app_book.xml
 *  
 * Heather Persson
 */

@SuppressLint("NewApi")
public class EditBook extends AppBookSearch  implements QueryCallback  {
	//used by caller to identify between multiple queries if 1 handler for all of them
	private static final int UPDATE_BOOK = 0;
	//for log identification
	private static final String TAG = "UpdateBook";
	//query for update
	private String query;
	
	//used to store data entered in form
	private String ISBN ="";
	private String author;
	private String title;
	
	//fields that can be updated
	private EditText ISBNET;
	private EditText authorET;
	private EditText titleET;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_app_book);
		Bundle b = getIntent().getExtras();
			
		//pull variables
		ISBN = b.getString(EXTRA_ISBN);
		author = b.getString(EXTRA_AUTHOR);
		title = b.getString(EXTRA_TITLE);
				
		//save fields to set values
		ISBNET = (EditText) findViewById(R.id.UpdateISBN);
		authorET = (EditText) findViewById(R.id.UpdateAuthor);
		titleET = (EditText) findViewById(R.id.UpdateTitle);
	
		//set text values
		ISBNET.setText(ISBN);
		authorET.setText(author);
		titleET.setText(title);
		
	}
	
	/**
	 * Update info
	 */
	public void update(View view) {
		/* create a string to save the set part of the query */
		String set = "SET";
		
		//check to see if new data matches old data, if not, start setting up 
		//query
		if(!ISBN.equals(ISBNET.getText().toString())){
			set += " ISBN= '" + ISBNET.getText().toString() + "', ";			
		}
		if(!author.equals(authorET.getText().toString())){
			set += " Author= '" + authorET.getText().toString() + "', ";
		}
		if(!title.equals(titleET.getText().toString())){
			set += " Title= '" + titleET.getText().toString() + "', ";
		}
		
		/* chop the last comma & space off of set */
		set = set.substring(0, set.length()-2);
		
		//update Book in database
		query = "update BOOK " + set + " where ISBN= \"" + ISBN + "\"";
		
		//logging for debugging
		Log.d(TAG, "Query: " + query);
				
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, UPDATE_BOOK, this,
				Variables.getRest(), null).execute();
	}
	
	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		// TODO Auto-generated method stub
		try {
			if (code == UPDATE_BOOK) {
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
					
					/* Print out that update was successful */
					Toast.makeText(getApplicationContext(),
							"Successfully updated book", Toast.LENGTH_LONG).show();
					Intent intent = new Intent( this, AppBookSearch.class);
					startActivity(intent);
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"Unable to update book", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			} else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
