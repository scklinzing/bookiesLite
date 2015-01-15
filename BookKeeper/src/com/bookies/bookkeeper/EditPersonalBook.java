package com.bookies.bookkeeper;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import android.annotation.SuppressLint;
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
 * user id passed through intent. if -1 it is personal list and you need to get
 * id from variables if it is not -1 then that is the user id and that's what
 * you need to use to pull the book for editing.
 */
public class EditPersonalBook extends ActionBarActivity implements
		QueryCallback {
	// used by caller to identify between multiple queries if 1 handler for all
	// of them
	private static final int QUERY_BOOK = 1;
	private static final int UPDATE_BOOK = 2;
	private static final int DELETE_BOOK = 3;

	// for log identification
	private static final String TAG = "UpdatePerBook";

	// queries for update
	private String query;

	// used to store data in form
	private String ISBN;
	private String title;
	private String author;
	private int status;
	private int nStatus; // updated status
	private String isOwned = "no"; // by default
	private String nIsOwned; // new isOwned status
	private int rating;
	private int nRating = 0; // new rating based on changes
	private long dateRead;
	private String dRead; // updated date read
	private String comments;
	private String from;
	private int userId = -1;
	private int viewing = 2;
	// private int rateChange = 0;

	// fields in form
	private EditText updateISBN;
	private EditText updateTitle;
	private EditText updateAuthor;
	private EditText updateDate;
	private EditText updateComments;
	/* book status */
	private RadioButton radioRead;
	private RadioButton radioWantToRead;
	private RadioButton radioReading;
	/* is owned? */
	private RadioButton isOwnedYes;
	private RadioButton isOwnedNo;
	/* radio 1 - 5 are for ratings */
	private RadioButton radioOne;
	private RadioButton radioThree;
	private RadioButton radioTwo;
	private RadioButton radioFour;
	private RadioButton radioFive;

	@SuppressLint("SimpleDateFormat") protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_per_book);

		// save fields to set values
		updateISBN = (EditText) findViewById(R.id.updateISBN);
		updateTitle = (EditText) findViewById(R.id.updateTitle);
		updateAuthor = (EditText) findViewById(R.id.updateAuthor);
		updateDate = (EditText) findViewById(R.id.editDate);
		updateComments = (EditText) findViewById(R.id.editComments);
		radioRead = (RadioButton) findViewById(R.id.radioRead);
		radioWantToRead = (RadioButton) findViewById(R.id.radioWantToRead);
		radioReading = (RadioButton) findViewById(R.id.radioReading);
		isOwnedYes = (RadioButton) findViewById(R.id.isOwnedYes);
		isOwnedNo = (RadioButton) findViewById(R.id.isOwnedNo);
		radioOne = (RadioButton) findViewById(R.id.radioOne);
		radioTwo = (RadioButton) findViewById(R.id.radioTwo);
		radioThree = (RadioButton) findViewById(R.id.radioThree);
		radioFour = (RadioButton) findViewById(R.id.radioFour);
		radioFive = (RadioButton) findViewById(R.id.radioFive);

		// pull data from passed intent
		Intent intent = getIntent();

		// user entered variables
		ISBN = intent.getStringExtra(MyExpandableAdapter.EXTRA_ISBN);
		author = intent.getStringExtra(MyExpandableAdapter.EXTRA_AUTHOR);
		title = intent.getStringExtra(MyExpandableAdapter.EXTRA_TITLE);
		status = intent.getIntExtra(MyExpandableAdapter.EXTRA_STATUS, -1);
		isOwned = intent.getStringExtra(MyExpandableAdapter.EXTRA_ISOWNED);
		rating = intent.getIntExtra(MyExpandableAdapter.EXTRA_RATING, -1);
		dateRead = intent.getLongExtra(MyExpandableAdapter.EXTRA_DATE, -1);
		comments = intent.getStringExtra(MyExpandableAdapter.EXTRA_COMMENT);

		// set text values for ISBN, Title, and Author
		updateISBN.setText(ISBN);
		updateAuthor.setText(author);
		updateTitle.setText(title);

		// set STATUS radio buttons
		if (status == 1) {
			radioRead.setChecked(true);
			radioWantToRead.setChecked(false);
			radioReading.setChecked(false);
		} else if (status == 2) {
			radioRead.setChecked(false);
			radioWantToRead.setChecked(true);
			radioReading.setChecked(false);
		} else if (status == 3) {
			radioRead.setChecked(false);
			radioWantToRead.setChecked(false);
			radioReading.setChecked(true);
		}
		
		// is OWNED?
		if (isOwned.equals("no")) {
			isOwnedNo.setChecked(true);
			isOwnedYes.setChecked(false);
		} else {
			isOwnedNo.setChecked(false);
			isOwnedYes.setChecked(true);
		}
		
		// set RATING radio buttons
		if (rating == 1) {
			radioOne.setChecked(true);
			radioTwo.setChecked(false);
			radioThree.setChecked(false);
			radioFour.setChecked(false);
			radioFive.setChecked(false);
		} else if (rating == 2) {
			radioOne.setChecked(false);
			radioTwo.setChecked(true);
			radioThree.setChecked(false);
			radioFour.setChecked(false);
			radioFive.setChecked(false);
		} else if (rating == 3) {
			radioOne.setChecked(false);
			radioTwo.setChecked(false);
			radioThree.setChecked(true);
			radioFour.setChecked(false);
			radioFive.setChecked(false);
		} else if (rating == 4) {
			radioOne.setChecked(false);
			radioTwo.setChecked(false);
			radioThree.setChecked(false);
			radioFour.setChecked(true);
			radioFive.setChecked(false);
		} else if (rating == 5) {
			radioOne.setChecked(false);
			radioTwo.setChecked(false);
			radioThree.setChecked(false);
			radioFour.setChecked(false);
			radioFive.setChecked(true);
		}
		
		// convert DATE to string and set value
		Format formatter = new SimpleDateFormat("mm/dd/yyyy");
		dRead = formatter.format(dateRead);

		// if there is a date, display it
		if (!dRead.equals("00/31/0002")) {
			updateDate.setText(dRead);
		}
		
		// set COMMENTS
		updateComments.setText(comments);
	}

	public void updateBook(View view) {
		Log.d(TAG, "updateBook()");
		/* create a string to save the set part of the query */
		String set = "set ";
		
		// check if new data matches old data, if not, start setting up
		if(!ISBN.equals(updateISBN.getText().toString())){
			set += " ISBN= '" + updateISBN.getText().toString() + "', ";			
		}
		if(!author.equals(updateAuthor.getText().toString())){
			set += " Author= '" + updateAuthor.getText().toString() + "', ";
		}
		if(!title.equals(updateTitle.getText().toString())){
			set += " Title= '" + updateTitle.getText().toString() + "', ";
		}

		// update date read
		if (!dRead.equals(updateDate.getText().toString())) {
			set += " dateRead= \"" + updateDate.getText().toString()
					+ "\", ";
		}
		// update comments
		if (!comments.equals(updateComments.getText().toString())) {
			set += " Comments= \"" + updateComments.getText().toString()
					+ "\", ";
		}
		// check status radios and update if needed
		nStatus = 0;
		if (radioRead.isChecked()) {
			nStatus = 1;
		} else if (radioWantToRead.isChecked()) {
			nStatus = 2;
		} else if (radioReading.isChecked()) {
			nStatus = 3;
		}
		if (!(status == nStatus)) {
			set += " Status= \"" + nStatus + "\", ";
		}
		
		// check isOwned radios and update if needed
		nIsOwned = "no";
		if (isOwnedYes.isChecked())
			nIsOwned = "yes";
		if (!(isOwned.equals(nIsOwned))) {
			set += " isOwned= \"" + nIsOwned + "\", ";
		}
		
		// check rating radios and update if needed
		if (radioOne.isChecked()) {
			nRating = 1;
		} else if (radioTwo.isChecked()) {
			nRating = 2;
		} else if (radioThree.isChecked()) {
			nRating = 3;
		} else if (radioFour.isChecked()) {
			nRating = 4;
		} else if (radioFive.isChecked()) {
			nRating = 5;
		}
		// if rating has changed
		if (!(rating == nRating)) {
			set += " Rating= \"" + nRating + "\", ";
		}

		/* chop the last comma & space off of set */
		set = set.substring(0, set.length() - 2);
		
		/* update BOOK in database */
		query = "update BOOK " + set + " where ISBN= \"" + ISBN + "\"";
		Log.d(TAG, "Query: " + query); // logging for debugging
		Log.d(TAG, "set: " + set); // logging for debugging
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, UPDATE_BOOK, this,
				Variables.getRest(), null).execute();

	}

	// to delete the book, confirm this is what user wants to do
	public void verifyDelete(View view) {
		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		builder2.setMessage("Are you sure you want to Delete this book from your library?");
		builder2.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						deleteBook(radioFive);
					}
				});

		builder2.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder2.show();
	}

	// delete the book once confirmed
	public void deleteBook(View view) {
		query = "delete from BOOK where ISBN= \"" + ISBN + "\"";
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(),
				Variables.getSalt(), query, DELETE_BOOK, this,
				Variables.getRest(), null).execute();
	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
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
					// 1 = mainlist, 2 = friends list
					if (from != null && from.equals("1")) {
						Toast.makeText(getApplicationContext(),
								"Book Updated",
								Toast.LENGTH_LONG).show();

						Intent intent = new Intent(this, MainForm.class);

						intent.putExtra("VIEWING", viewing);

						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}

					/* Print out that update was successful */
					else {
						Toast.makeText(getApplicationContext(),
								"Successfully updated book", Toast.LENGTH_LONG)
								.show();
						Intent intent = new Intent(this, MainForm.class);
						intent.putExtra("VIEWING", viewing);

						if (viewing == 2) {
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
			} else if (code == DELETE_BOOK) {
				if (result != null
						&& !result.isNull("response_status")
						&& result.getString("response_status")
								.equalsIgnoreCase("success")) {

					/* Print out that delete was successful */
					Toast.makeText(getApplicationContext(),
							"Successfully deleted book", Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(this, MainForm.class);
					intent.putExtra("VIEWING", viewing);
					if (viewing == 2) {
						intent.putExtra("FRIEND_ID", userId);
					}
					startActivity(intent);
				} else {
					/* Toast is error messaging */
					Toast.makeText(getApplicationContext(),
							"Unable to delete book", Toast.LENGTH_LONG).show();
					Log.e(TAG, "*** Error: " + result);
				}
			/*} else if (code == QUERY_BOOK) {
				if (result != null
						&& !result.isNull("response_status")
						&& result.getString("response_status")
								.equalsIgnoreCase("success")) {
					int row = 0;
					int size = result.length();
					// log size
					Log.d(TAG, "Results: " + size);

					// get number of rows
					while (!result.isNull(Integer.toString(row))) {
						row++;
					}
					// log rows
					Log.d(TAG, "Rows= " + row);
				}*/
			} else {
				Log.e(TAG, "*** Error: Unknown code");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}