package com.bookies.bookkeeper;


import java.text.Format;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
/**
 * Updates data in USER_LIB and BOOK  table
 * 
 * @author Heather Persson
 *
 */


/**
 * user id passed through intent.  if -1 it is personal list
 * and you need to get id from variables
 * if it is not -1 then that is the user id and that's what you need
 * to use to pull the book for editing.
 */
public class EditPersonalBook extends ActionBarActivity implements QueryCallback  {
	//used by caller to identify between multiple queries if 1 handler for all of them
	private static final int UPDATE_USER_LIB = 0;
	private static final int QUERY_BOOK = 1;
	private static final int UPDATE_BOOK = 2;
	private static final int DELETE_BOOK = 3;
	
	//for log identification
	private static final String TAG = "UpdatePerBook";
	
	//queries for update
	private String query;
	private String query2;
	private String query3;
	
	//used to store data in form
	private String ISBN; //will not edit
	private String title;  //will not edit
	private String author;  //will not edit
	private int status;
	private int security;
	private int rating;
	private int nRating = 0; //new rating based on changes
	private long dateRead;
	private String dRead;
	private String comments;
	private int sumOfRatings;
	private int numOfRatings;
	private String from;
	private int userId = -1;
	private int viewing = 2;
	private int rateChange = 0;
	
	//fields in form
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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_per_book);
		
		//save fields to set values
		updateTitle = (EditText) findViewById(R.id.updateTitle);
		updateAuthor= (EditText) findViewById(R.id.updateAuthor);
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
				
		
		//pull data from passed intent
		Intent intent = getIntent();
		from = intent.getStringExtra(AddBookFromAppLib.EXTRA_FROM);
		
		Log.d(TAG, "from: "+ from);
		
				Log.d(TAG, "from: "+ from);
		
		//if intent came from AddBookFromAppLib/Main
		if(from != null && from.equals("1")){
			
			
			ISBN = intent.getStringExtra(AddBookFromAppLib.EXTRA_ISBN);
			author = intent.getStringExtra(AddBookFromAppLib.EXTRA_AUTHOR);
			title = intent.getStringExtra(AddBookFromAppLib.EXTRA_TITLE);
			
			radioWantToRead.isChecked();
			
		}
		
		//if intent came from adapter (book is already in per lib and may contain
		//user entered variables
		else {
			ISBN = intent.getStringExtra(MyExpandableAdapter.EXTRA_ISBN);
			author = intent.getStringExtra(MyExpandableAdapter.EXTRA_AUTHOR);
			title = intent.getStringExtra(MyExpandableAdapter.EXTRA_TITLE);
			status = intent.getIntExtra(MyExpandableAdapter.EXTRA_STATUS, -1);
			security = intent.getIntExtra(MyExpandableAdapter.EXTRA_SECURITY, -1);
			rating = intent.getIntExtra(MyExpandableAdapter.EXTRA_RATING, -1);
			dateRead = intent.getLongExtra(MyExpandableAdapter.EXTRA_DATE, -1);
			comments = intent.getStringExtra(MyExpandableAdapter.EXTRA_COMMENT);
		
		
			//---UNCOMMENT when intent is up and working---
			userId = intent.getIntExtra(MyExpandableAdapter.EXTRA_USERID, -1);
			
			if( userId == -1)
			{
				userId = Variables.getUserId();
			}
			viewing = intent.getIntExtra("VIEWING", 0); //defaults to mylist		
			//set comments
			updateComments.setText(comments);
			
			//convert date to string and set value
			Format formatter = new SimpleDateFormat("mm/dd/yyyy");
			dRead = formatter.format(dateRead);
			
			//if there is a date, display it
			if(!dRead.equals("00/31/0002")){
				updateDate.setText(dRead);
			}
	
			//set status radio buttons
			if(status == 1){
				radioRead.setChecked(true);
				radioWantToRead.setChecked(false);
				radioReading.setChecked(false);			
			}
			else if(status == 2){
				radioRead.setChecked(false);
				radioWantToRead.setChecked(true);
				radioReading.setChecked(false);			
			}
			else if(status == 3){
				radioRead.setChecked(false);
				radioWantToRead.setChecked(false);
				radioReading.setChecked(true);			
			}
			
			//set security radio buttons
			if(security == 0){
				radioPublic.setChecked(true);
				radioFriends.setChecked(false);
				radioPrivate.setChecked(false);	
			}
			else if(security == 1){
				radioPublic.setChecked(false);
				radioFriends.setChecked(true);
				radioPrivate.setChecked(false);	
			}
			else if(security == 2){
				radioPublic.setChecked(false);
				radioFriends.setChecked(false);
				radioPrivate.setChecked(true);	
			}
			
			//set rating radio buttons
			if(rating == 1){
				radioOne.setChecked(true);
				radioTwo.setChecked(false);
				radioThree.setChecked(false);
				radioFour.setChecked(false);
				radioFive.setChecked(false);
			}
			else if(rating == 2){
				radioOne.setChecked(false);
				radioTwo.setChecked(true);
				radioThree.setChecked(false);
				radioFour.setChecked(false);
				radioFive.setChecked(false);
			}
			else if(rating == 3){
				radioOne.setChecked(false);
				radioTwo.setChecked(false);
				radioThree.setChecked(true);
				radioFour.setChecked(false);
				radioFive.setChecked(false);
			}
			else if(rating == 4){
				radioOne.setChecked(false);
				radioTwo.setChecked(false);
				radioThree.setChecked(false);
				radioFour.setChecked(true);
				radioFive.setChecked(false);
			}
			else if(rating == 5){
				radioOne.setChecked(false);
				radioTwo.setChecked(false);
				radioThree.setChecked(false);
				radioFour.setChecked(false);
				radioFive.setChecked(true);
			}
		}
		updateAuthor.setText(author);
		updateTitle.setText(title);
	}
	
	public void updateBook(View view) {
		/* create a string to save the set part of the query */
		String set = "set ";
		int prev = 0;
		
		//if this is a newly added book, you do not need to compare
		//against old data
		if(from != null && from.equals("1")){
			//date
			if (dateRead > 0){
				set += " dateRead= \"" + updateDate.getText().toString() + "\", ";
			}
			//comments
			if(comments != null){
				set += " Comments= \""  + updateComments.getText().toString() +"\", ";	
				
				Log.d(TAG, "SET: " + set);
			}
			//status
			int nStatus = 0;
			if (radioRead.isChecked()){
				nStatus = 1;
				set += " Status= \"" + nStatus + "\", ";
			}
			else if (radioWantToRead.isChecked()){
				nStatus = 2;
				set += " Status= \"" + nStatus + "\", ";
			}
			else if (radioReading.isChecked()){
				nStatus = 3;
				set += " Status= \"" + nStatus + "\", ";
			}
			
			//security
			String nSecurity="";
			if (radioPublic.isChecked()){
				nSecurity = "0";
				set += " bookSecurity= \"" + nSecurity + "\", ";
			}
			else if (radioFriends.isChecked()){
				nSecurity = "1";
				set += " bookSecurity= \"" + nSecurity + "\", ";
			}
			else if(radioPrivate.isChecked()){
				nSecurity = "2";
				set += " bookSecurity= \"" + nSecurity + "\", ";
			}
			
			//rating
	
			if(radioOne.isChecked()){
				nRating = 1;
				set += " Rating= \"" + nRating + "\", ";
			}
			else if(radioTwo.isChecked()){
				nRating = 2;
				set += " Rating= \"" + nRating + "\", ";
			}
			else if(radioThree.isChecked()){
				nRating = 3;
				set += " Rating= \"" + nRating + "\", ";
			}
			else if(radioFour.isChecked()){
				nRating = 4;
				set += " Rating= \"" + nRating + "\", ";
			}
			else if(radioFive.isChecked()){
				nRating = 5;
				set += " Rating= \"" + nRating + "\", ";
			}
			
			rating = 0;
			
			if (nRating > 0){
			//look up sumOfRatings
			query = "select sumOfRatings, numOfRatings from `BOOK` where ISBN= \"" + ISBN + "\"";
				
			//pull sumOfRatings and numOfRatings
			new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), 
					query, QUERY_BOOK, this, Variables.getRest(), null).execute();	
			}
		}
		else{
			//check to see if new data matches old data, if not, start setting up 
			//date read
			if(!dRead.equals(updateDate.getText().toString())){
				set += " dateRead= \"" + updateDate.getText().toString() + "\", ";			
			}
			//comments
			if(!comments.equals(updateComments.getText().toString())){
				set += " Comments= \""  + updateComments.getText().toString() +"\", ";			
			}
	
			//check status radios and update if needed
			int nStatus = 0;
			if (radioRead.isChecked()){
				nStatus = 1;
			}
			else if (radioWantToRead.isChecked()){
				nStatus = 2;
			}
			else if (radioReading.isChecked()){
				nStatus = 3;
			}
			if(!(status == nStatus)){
				set += " Status= \"" + nStatus + "\", ";
			}
			
			//check security radios and update if needed
			String nSecurity="";
			if (radioPublic.isChecked()){
				nSecurity = "0";
			}
			else if (radioFriends.isChecked()){
				nSecurity = "1";
			}
			else if(radioPrivate.isChecked()){
				nSecurity = "2";
			}
			if(!(nSecurity.equals(status))){
				set += " bookSecurity= \"" + nSecurity + "\", ";
			}
			
			//check rating radios and update if needed
			if(radioOne.isChecked()){
				nRating = 1;
			}
			else if(radioTwo.isChecked()){
				nRating = 2;
			}
			else if(radioThree.isChecked()){
				nRating = 3;
			}
			else if(radioFour.isChecked()){
				nRating = 4;
			}
			else if(radioFive.isChecked()){
				nRating = 5;
			}
			//if rating has changed
			if(!(rating == nRating)){
				set += " Rating= \"" + nRating + "\", ";
				
				
				//if changing an existing rating, update sumOfRatings in BOOK table
				//look up sumOfRatings
				query = "select sumOfRatings, numOfRatings from `BOOK` where ISBN= \"" + ISBN + "\"";
					
				//pull sumOfRatings and numOfRatings
				new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), 
						query, QUERY_BOOK, this, Variables.getRest(), null).execute();		
			}
		}
		
		/* chop the last comma & space off of set */
		set = set.substring(0, set.length()-2);
		
		query = "update USER_LIB " + set + " where ISBN= \"" + ISBN + "\" and userID= \"" + userId + "\"";
		Log.d(TAG, "Query= " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, UPDATE_USER_LIB, this,
				Variables.getRest(), null).execute();
		
	}
	
	//if user wants to delete the book, confirm that this is what they really want to do
	public void verifyDelete(View view){
		AlertDialog.Builder builder2=new AlertDialog.Builder(this);
		builder2.setMessage("Are You sure you want to Delete this book from your library?");
		builder2.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteBook(radioFive);
			}
		});

		builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
	   });
	  builder2.show();
	}
	
	//delete the book once confirmed
	public void deleteBook(View view){
		query = "delete from USER_LIB where ISBN= \"" + ISBN + "\" and userID= \"" + userId + "\"";
		
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, DELETE_BOOK, this,
				Variables.getRest(), null).execute();
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		// TODO Auto-generated method stub
		try {
			if (code == UPDATE_USER_LIB) {
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
					//1 = mainlist, 2 = friends list
					if(from != null && from.equals("1")){
						Toast.makeText(getApplicationContext(),
								"Book Added to Personal Library", Toast.LENGTH_LONG).show();
						
						Intent intent = new Intent( this, MainForm.class);
						
						intent.putExtra("VIEWING", viewing);
						
						if (viewing == 2){
							intent.putExtra("FRIEND_ID", userId);
						}
												
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
					
					/* Print out that update was successful */
					else{
						Toast.makeText(getApplicationContext(),
							"Successfully updated book", Toast.LENGTH_LONG).show();
					Intent intent = new Intent( this, MainForm.class);
					intent.putExtra("VIEWING", viewing);
					
					if (viewing == 2){
						intent.putExtra("FRIEND_ID", userId);
					}
					
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					}
					
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"Unable to update book", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			}
			else if (code == DELETE_BOOK) {
				if (result != null
						&& !result.isNull("response_status")
						&& result.getString("response_status")
								.equalsIgnoreCase("success")) {
					
					/* Print out that delete was successful */
					Toast.makeText(getApplicationContext(),
							"Successfully deleted book", Toast.LENGTH_LONG).show();
					Intent intent = new Intent( this, MainForm.class);
					intent.putExtra("VIEWING", viewing);
					if (viewing == 2){
						intent.putExtra("FRIEND_ID", userId);
					}
					startActivity(intent);
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"Unable to delete book", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			}
			else if( code == QUERY_BOOK) {
				
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
						//pull from JSON object
						String sum = object.getString("sumOfRatings");
						String num = object.getString("numOfRatings");
						sumOfRatings = Integer.parseInt(sum);
						numOfRatings = Integer.parseInt(num);
						
						//update sumOfRatings						
						int nSum = sumOfRatings - (rating - nRating); 				
						query2 = "update BOOK set sumOfRatings=\"" + nSum + "\" where ISBN= \"" + ISBN +"\"";
						//update book table with new rating.
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
								Variables.getSalt(), query2, UPDATE_BOOK, this,
								Variables.getRest(), null).execute();
						
						//if original rating was 0, update numOfRatings by 1
						if(rating == 0){
							numOfRatings ++;
						
						query3 = "update BOOK set numOfRatings=\"" + numOfRatings + "\" where ISBN= \"" + ISBN +"\"";
						//update book table with new rating.
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
								Variables.getSalt(), query3, UPDATE_BOOK, this,
								Variables.getRest(), null).execute();
						}	
					}
				}
			}
			else {
				Log.e(TAG, "*** Error: unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
	
	
	
	
	
