package com.bookies.bookkeeper;

import org.json.JSONException;
import org.json.JSONObject;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Checks to see if book is in library and adds it if it is not.
 */

public class AddBook extends ActionBarActivity implements QueryCallback    {
	private static final int ADD_BOOK = 0;
	
	//for log identification
	private static final String TAG = "AddBook";
	
	//fields in form
	private EditText updateISBN;
	private EditText updateTitle;
	private EditText updateAuthor;
	private EditText updateDate;
	private EditText updateComments;
	private RadioButton radioRead;
	//private RadioButton radioWantToRead;
	private RadioButton radioReading;
	private RadioButton isOwnedYes;
	private RadioButton isOwnedNo;
	private RadioButton radioOne;
	private RadioButton radioTwo;
	private RadioButton radioThree;
	private RadioButton radioFour;
	private RadioButton radioFive;
	
	//used to store data in form
	private String ISBN; //will not edit
	private String title;  //will not edit
	private String author;  //will not edit
	private String status = "2"; // want to read
	private String isOwned = "no"; // by default
	private String rating = "0"; // no rating by default
	private String dateRead;
	private String comments;
			
	protected void onCreate(Bundle savedInstanceState) {
		try {
			Log.d(TAG, "Made it to AddBook.java");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add_book);
			
			//pull data from passed intent
			Intent intent = getIntent();
			Log.d(TAG, "Made it past the intent");
			
			ISBN = intent.getStringExtra(BookSummary.EXTRA_BOOK_ISBN);
			author = intent.getStringExtra(BookSummary.EXTRA_BOOK_AUTHOR);
			title = intent.getStringExtra(BookSummary.EXTRA_BOOK_TITLE);
			Log.d(TAG, "Got all intent extras!");
				
			//save fields to set values
			updateTitle = (EditText) findViewById(R.id.updateTitle);
			updateAuthor= (EditText) findViewById(R.id.updateAuthor);
			updateISBN= (EditText) findViewById(R.id.updateISBN);
			updateDate= (EditText) findViewById(R.id.editDate);
			updateComments= (EditText) findViewById(R.id.editComments);
			radioRead = (RadioButton) findViewById(R.id.radioRead);
			//radioWantToRead = (RadioButton) findViewById(R.id.radioWantToRead);
			radioReading = (RadioButton) findViewById(R.id.radioReading);
			isOwnedYes = (RadioButton) findViewById(R.id.isOwnedYes);
			isOwnedNo = (RadioButton) findViewById(R.id.isOwnedNo);
			radioOne = (RadioButton) findViewById(R.id.radioOne);
			radioTwo = (RadioButton) findViewById(R.id.radioTwo);
			radioThree = (RadioButton) findViewById(R.id.radioThree);
			radioFour = (RadioButton) findViewById(R.id.radioFour);
			radioFive = (RadioButton) findViewById(R.id.radioFive);
			
			//set text values
			updateISBN.setText(ISBN);
			updateAuthor.setText(author);
			updateTitle.setText(title);
			radioRead.setChecked(true);
			isOwnedNo.setChecked(true);
			//radioPublic.setChecked(true);
			
			Log.d(TAG, "Got all the values");
			
		} catch (Exception e) {
			Log.d(TAG, "Caught an exception in onCreate()");
			e.printStackTrace();
		}
	}
	
	public void addBook(View view){
		Log.d(TAG, "Made it to addBook()!");
		//check to see if book exists in BOOK table
		String ISBN = updateISBN.getText().toString();
		String query = "select * from BOOK where ISBN = \"" + ISBN + "\"";
		Log.d(TAG, "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
				Variables.getSalt(), query, ADD_BOOK, this, 
				Variables.getRest(), findViewById(R.id.progressBar)).execute();
	}
	
	public void manualAddBook(View view){
		Log.d(TAG, "Made it to manualAddBook()!");
		//check to see if book exists in BOOK table
		String ISBN = updateISBN.getText().toString();
		String query = "select * from BOOK where ISBN = \"" + ISBN + "\"";
		Log.d(TAG, "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
				Variables.getSalt(), query, ADD_BOOK, this, 
				Variables.getRest(), findViewById(R.id.progressBar)).execute();
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		Log.d(TAG, "Made it to onQueryTaskCompleted(int code, JSONObject result)!");
		try {
			//if book not found, add book to BOOK
			if(code == ADD_BOOK) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("error")) {
				   if(result.getString("error").equalsIgnoreCase("No records found")){
					   
						// ISBN
						if(!ISBN.equals(updateISBN.getText().toString())){
							ISBN = updateISBN.getText().toString();			
						}
						
						// author
						if(!author.equals(updateAuthor.getText().toString())){
							author = updateAuthor.getText().toString();
						}
						
						// title
						if(!title.equals(updateTitle.getText().toString())){
							title = updateTitle.getText().toString();
						}
					
					   // status -- wishlist is default
					   if (radioRead.isChecked()){
							status = "1";
						} else if (radioReading.isChecked()){
							status = "3";
						}
					   
					   // is owned?
					   if(isOwnedYes.isChecked()){
							isOwned = "yes";
						}
						
						//rating
						if(radioOne.isChecked()){
							rating = "1";
						} else if(radioTwo.isChecked()){
							rating = "2";
						} else if(radioThree.isChecked()){
							rating = "3";
						} else if(radioFour.isChecked()){
							rating = "4";
						} else if(radioFive.isChecked()){
							rating = "5";
						}
						
						//date read
						//dateRead = "0000-00-00";
						if(updateDate.length() > 0){
							dateRead = updateDate.getText().toString();
						}
						
						//comments
						comments=" ";
						if(updateComments.length() > 0){
							comments = updateComments.getText().toString();			
						}
						Log.d(TAG, "Comment length = " + comments.length());
						
						// build the query
						String query = "insert into BOOK (ISBN, Author, Title, Status, Comments, rating, dateRead, isOwned) "+
								"values (\""+ ISBN +"\", \"" + author + "\", \"" + title + "\", \"" + status +"\", \"" + 
								comments +"\", \""  + rating +"\", \"" + dateRead + "\", \"" + isOwned + "\")";
								Log.d(TAG, "Query add BOOK = " + query);
				
						// execute query
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
								Variables.getSalt(), query, ADD_BOOK, this, 
								Variables.getRest(), findViewById(R.id.progressBar)).execute();
						
						// print to say it was added
						Toast.makeText(getApplicationContext(), "Book added to Library.", 
								Toast.LENGTH_LONG).show();
				   }
				} else {
					Log.e(TAG, "*** Error: unknown code when adding book. Error: " + result);
				}
			}
			
			// If successfully added to user library, return to main form
			if(code == ADD_BOOK) {
					Intent intent = new Intent( this, MainForm.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
		        	finish();
		
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}