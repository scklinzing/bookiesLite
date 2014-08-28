package com.bookies.bookkeeper;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class AddBookChoice extends Activity {
	
	public final static String EXTRA_SCAN_FORMAT = "com.bookies.bookkeeper.SCAN_FORMAT";
	public final static String EXTRA_SCAN_ISBN = "com.bookies.bookkeeper.SCAN_CONTENT";
	
	private EditText isbn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book_choice);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		isbn = (EditText)findViewById(R.id.isbn);
	}
	
	public void help( View view )
	{
		Intent intent = new Intent( this, ScanHelp.class );
		startActivity(intent);
	}
	
	public void scan( View view )
	{
		IntentIntegrator intentIntegrator = new IntentIntegrator(this);
		intentIntegrator.initiateScan();
	}
	
	public void lookUp( View view )
	{
		if( isbn.getText().toString().isEmpty() || isbn.getText().toString().length() != 10 && isbn.getText().toString().length() != 13 )
		{
			Toast.makeText(getApplicationContext(), "Enter a valid ISBN Number", Toast.LENGTH_SHORT).show();
		}
		else
		{
			String isbnStr = isbn.getText().toString();
			
			Log.d("Testing", "Length: " + isbn.length());
			
			if ( isbnStr.length() == 10 )
			{
				isbnStr = "978" + isbnStr;
				Log.d("Testing", "ISBN1: " + isbnStr);
			}

			if (isbnStr.length() == 13 && isbnStr.indexOf("978") == 0)
			{
				isbnStr = isbnStr.substring(0,12);
				int checkNum = 0;
				
				for( int i = 0 ; i < isbnStr.length() ; i++ )
				{
					if ( i % 2 == 1 )
					{
						checkNum += Integer.parseInt( ""+isbnStr.charAt(i) ) * 3;
					}
					else
					{
						checkNum += Integer.parseInt( ""+isbnStr.charAt(i) );
					}
				}
				
				checkNum = checkNum % 10;
				
				if ( checkNum == 0 )
				{
					isbnStr += checkNum;
				}
				else
				{
					isbnStr += 10 - checkNum;
				}
				
				Log.d("Testing", "ISBN2: " + isbnStr);
			}
			Log.d("Testing", "ISBN3: " + isbnStr);
			String scanFormat = "EAN13";
			Log.d("SCAN", "Format: " + scanFormat + " ISBN: " + isbnStr);
			Intent bookSummaryIntent = new Intent(this, BookSummary.class);
			bookSummaryIntent.putExtra(AddBookChoice.EXTRA_SCAN_ISBN, isbnStr);
			bookSummaryIntent.putExtra(AddBookChoice.EXTRA_SCAN_FORMAT, scanFormat);
			startActivity(bookSummaryIntent);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanFormat = scanningResult.getFormatName();
			String isbn = scanningResult.getContents();
			Log.d("SCAN", "content: " + isbn + " - format: "
					+ scanFormat);
			if (isbn != null && scanFormat != null
					&& scanFormat.equalsIgnoreCase("EAN_13") ) {
				
				Intent bookSummaryIntent = new Intent(this, BookSummary.class);
				bookSummaryIntent.putExtra(AddBookChoice.EXTRA_SCAN_ISBN, isbn);
				bookSummaryIntent.putExtra(AddBookChoice.EXTRA_SCAN_FORMAT, scanFormat);
				startActivity(bookSummaryIntent);
				
			} else {
				Toast.makeText(getApplicationContext(), "Not A Valid Scan!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "No scan data received!",
					Toast.LENGTH_SHORT).show();
		}
	}
}
