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
 * Checks to see if book is in application lib and user lib.
 * If not in application lib, will add new book to app lib and user lib.
 * If in application lib, will check user lib and add if not there.
 * 
 * @author Heather Persson
 *
 */

public class AddBook extends ActionBarActivity implements QueryCallback    {
	
	private static final int ADD_BOOK = 0;
	private static final int ADD_USERLIB_0 = 10; // so we can remove recommendations
	private static final int ADD_USERLIB = 1;
	private static final int QUERY_BOOK = 2;
	private static final int QUERY_USERLIB = 3;
	
	//for log identification
	private static final String TAG = "AddBook";
	
	//fields in form
	private EditText updateISBN;
	private EditText updateTitle;
	private EditText updateAuthor;
	private EditText updateDate;
	private EditText updateComments;
	private RadioButton radioRead;
	private RadioButton radioWantToRead;
	private RadioButton radioReading;
	private RadioButton radioOne;
	private RadioButton radioPublic;
	private RadioButton radioFriends;
	private RadioButton radioPrivate;
	private RadioButton radioThree;
	private RadioButton radioTwo;
	private RadioButton radioFour;
	private RadioButton radioFive;
	
	//used to store data in form
	private String ISBN; //will not edit
	private String title;  //will not edit
	private String author;  //will not edit
	private String status;
	private String security;
	private String rating;
	private String dateRead;
	private String comments;
			
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
		
		//pull data from passed intent
		Intent intent = getIntent();
		
		ISBN = intent.getStringExtra(BookSummary.EXTRA_BOOK_ISBN);
		author = intent.getStringExtra(BookSummary.EXTRA_BOOK_AUTHOR);
		title = intent.getStringExtra(BookSummary.EXTRA_BOOK_TITLE);
			
		//save fields to set values
		updateTitle = (EditText) findViewById(R.id.updateTitle);
		updateAuthor= (EditText) findViewById(R.id.updateAuthor);
		updateISBN= (EditText) findViewById(R.id.updateISBN);
		updateDate= (EditText) findViewById(R.id.editDate);
		updateComments= (EditText) findViewById(R.id.editComments);
		radioRead = (RadioButton) findViewById(R.id.radioRead);
		radioWantToRead = (RadioButton) findViewById(R.id.radioWantToRead);
		radioReading = (RadioButton) findViewById(R.id.radioReading);
		radioPublic = (RadioButton) findViewById(R.id.radioPublic);
		radioFriends = (RadioButton) findViewById(R.id.radioFriends);
		radioPrivate = (RadioButton) findViewById(R.id.radioPrivate);
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
		radioPublic.setChecked(true);
	}
	
	public void addBook(View view){
		//check to see if book exists in BOOK table
		String ISBN = updateISBN.getText().toString();
		String query = "select * from BOOK where ISBN = \"" + ISBN + "\"";
		Log.d(TAG, "Query = " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
				Variables.getSalt(), query, QUERY_BOOK, this, 
				Variables.getRest(), findViewById(R.id.progressBar)).execute();
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {

		try {
			//if book not found, add book to BOOK and USER_LIB
			if(code == QUERY_BOOK) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("error")) {
				   if(result.getString("error").equalsIgnoreCase("No records found")){
					
						//add to BOOK
						String query = "insert into BOOK (ISBN, Author, Title) "+
								"values (\""+ ISBN +"\", \"" + author + "\", \"" + title +"\")";
								Log.d(TAG, "Query add BOOK = " + query);
				
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
								Variables.getSalt(), query, ADD_BOOK, this, 
								Variables.getRest(), findViewById(R.id.progressBar)).execute();
				   }
				}
				
				//if book exists in BOOK, check to see if USER_LIB
				else if(result != null && !result.isNull("response_status") && 
				   result.getString("response_status").equalsIgnoreCase("success")){
					
					String ISBN = updateISBN.getText().toString();
					String query = "select * from USER_LIB where ISBN = \"" + ISBN + 
							"\" and userID= \"" + Variables.getUserId() + "\"";
					Log.d(TAG, "Query CHECK USER LIB= " + query);
					new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
							Variables.getSalt(), query, QUERY_USERLIB, this, 
							Variables.getRest(), findViewById(R.id.progressBar)).execute();
				}
				else {
					Log.e(TAG, "*** Error: unknown code");
				}
			}
			if(code == ADD_USERLIB_0){
				if(result != null && !result.isNull("response_status") 
						&& result.getString("response_status").equalsIgnoreCase("success")) {
					
					Toast.makeText(getApplicationContext(), "Book added to personal library.", 
							Toast.LENGTH_LONG).show();
					String query = "delete from Recommendations where BookID = " + ISBN + " and FriendID = " + Variables.getUserId(); //isbn is in first child
					new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, ADD_USERLIB, this, Variables.getRest(), findViewById(R.id.progressBar)).execute();
				}
				else{
					Toast.makeText(getApplicationContext(), "Error - Book not added to personal Library", 
							Toast.LENGTH_LONG).show();
				}

			}
			//check to see if book in USER_LIB, if not - pull updated fields and 
			//add to personal library
			if(code == QUERY_USERLIB) {
				if(result != null && !result.isNull("response_status") && 
						   result.getString("response_status").equalsIgnoreCase("error")) {
					   if(result.getString("error").equalsIgnoreCase("No records found")){
						   //update variables with updates on screen.
						   //status
						   status = "1";
//							if (radioRead.isChecked()){
//								status = "1";
//							}
							if (radioWantToRead.isChecked()){
								status = "2";
							}
							else if (radioReading.isChecked()){
								status = "3";
							}
							
							//security
							security="0";
//							if (radioPublic.isChecked()){
//								security = "0";
//							}
							if (radioFriends.isChecked()){
								security = "1";
							}
							else if(radioPrivate.isChecked()){
								security = "2";
							}
							
							//rating
							rating = "0";
							if(radioOne.isChecked()){
								rating = "1";
							}
							else if(radioTwo.isChecked()){
								rating = "2";
							}
							else if(radioThree.isChecked()){
								rating = "3";
							}
							else if(radioFour.isChecked()){
								rating = "4";
							}
							else if(radioFive.isChecked()){
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
							
							//book does not exist in USER LIB, so add
							String query = "insert into USER_LIB (ISBN, userID, Status, bookSecurity, Comments, rating, dateRead) "+
							"values (\""+ ISBN +"\", \"" + Variables.getUserId() +"\", \"" + status +"\", \"" + security 
							+ "\", \""  + comments +"\", \""  + rating +"\", \"" + dateRead + "\")";
									Log.d(TAG, "Query = " + query);
					
							new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
									Variables.getSalt(), query, ADD_USERLIB_0, this, 
									Variables.getRest(), findViewById(R.id.progressBar)).execute();
					   }
				}
				else {
					Toast.makeText(getApplicationContext(), "Book is already in personal library", 
						Toast.LENGTH_LONG).show();
				}
			}
			
			if(code == ADD_BOOK) {
				if(result != null && !result.isNull("response_status") 
						&& result.getString("response_status").equalsIgnoreCase("success")) {
					
					//update variables with updates on screen.
					//status
					status = "1";
//					if (radioRead.isChecked()){
//						status = "1";
//					}
					if (radioWantToRead.isChecked()){
						status = "2";
					}
					else if (radioReading.isChecked()){
						status = "3";
					}
					
					//security
					security="0";
//					if (radioPublic.isChecked()){
//						security = "0";
//					}
					if (radioFriends.isChecked()){
						security = "1";
					}
					else if(radioPrivate.isChecked()){
						security = "2";
					}
					
					//rating
					rating = "0";
					if(radioOne.isChecked()){
						rating = "1";
					}
					else if(radioTwo.isChecked()){
						rating = "2";
					}
					else if(radioThree.isChecked()){
						rating = "3";
					}
					else if(radioFour.isChecked()){
						rating = "4";
					}
					else if(radioFive.isChecked()){
						rating = "5";
					}
					
					//date read
					//dateRead = "0000/00/00";
					if(updateDate.length() > 0){
						dateRead = updateDate.getText().toString();
					}
					
					//comments
					comments=" ";
					if(updateComments.length() > 0){
						comments = updateComments.getText().toString();			
					}
					Log.d(TAG, "Comment length = " + comments.length());

					//add to USER_LIB
					String query2 = "insert into USER_LIB (ISBN, userID, Status, bookSecurity, Comments, rating, dateRead) "+
							"values (\""+ ISBN +"\", \"" + Variables.getUserId() +"\", \"" + status +"\", \"" + security 
							+ "\", \""  + comments +"\", \""  + rating +"\", \"" + dateRead + "\")";
					Log.d(TAG, "Query = " + query2);
			
					new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
							Variables.getSalt(), query2, ADD_USERLIB, this, 
							Variables.getRest(), findViewById(R.id.progressBar)).execute();
					
					Toast.makeText(getApplicationContext(), "Book added to application library.", 
							Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "Error - Book not added to application Library", 
							Toast.LENGTH_LONG).show();
				}
			}
			
			if(code == ADD_USERLIB) {
				
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
