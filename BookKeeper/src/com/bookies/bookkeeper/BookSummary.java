package com.bookies.bookkeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.amazon.advertising.api.sample.SignedRequestsHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BookSummary extends ActionBarActivity implements QueryCallback  {
	
	public final static String EXTRA_BOOK_ISBN = "com.bookies.bookkeeper.BOOK_ISBN";
	public final static String EXTRA_BOOK_AUTHOR = "com.bookies.bookkeeper.BOOK_AUTHOR";
	public final static String EXTRA_BOOK_TITLE = "com.bookies.bookkeeper.BOOK_TITLE";
	
	private static final String TAG = "BOOK SUMMARY";

	private static final String KEY = "AIzaSyBDavskpIhMGwK_mL6w4EwuHA_2xOxyHvo";
	
	//Amazon stuff
	private static final String AWS_ACCESS_KEY_ID = "AKIAIIEUTNPJT3QV53HA";
    private static final String AWS_SECRET_KEY = "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";
    private static final String ENDPOINT = "ecs.amazonaws.com";
    //private static final String ITEM_ID = "9780345337665";
    
    private static final int QUERY_USERLIB = 0;

	private Button previewBtn;
	private Button linkBtn;
	private Button offersBtn;
	private Button addBtn;
	private TextView authorText;
	private TextView titleText;
	private TextView descriptionText;
	private TextView dateText;
	private TextView ratingCountText;
	private TextView newPrice;
	private TextView usedPrice;

	private LinearLayout starLayout;
	private ImageView thumbView;
	private ImageView[] starViews;

	private Bitmap thumbImg;
	
	private String scanFormat;
	private String isbn;
	
	private String title;
	private String author;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_summary);

		previewBtn = (Button) findViewById(R.id.preview_btn);
		previewBtn.setVisibility(View.GONE);
		linkBtn = (Button) findViewById(R.id.link_btn);
		linkBtn.setVisibility(View.GONE);
		offersBtn = (Button) findViewById(R.id.offers_btn);
		offersBtn.setVisibility(View.GONE);
		addBtn = (Button) findViewById(R.id.add_book);
		addBtn.setVisibility(View.GONE);

		authorText = (TextView) findViewById(R.id.book_author);
		titleText = (TextView) findViewById(R.id.book_title);
		descriptionText = (TextView) findViewById(R.id.book_description);
		dateText = (TextView) findViewById(R.id.book_date);
		starLayout = (LinearLayout) findViewById(R.id.star_layout);
		ratingCountText = (TextView) findViewById(R.id.book_rating_count);
		thumbView = (ImageView) findViewById(R.id.thumb);
		newPrice = (TextView) findViewById(R.id.new_price);
		usedPrice = (TextView) findViewById(R.id.used_price);

		starViews = new ImageView[5];
		for (int s = 0; s < starViews.length; s++) {
			starViews[s] = new ImageView(this);
		}
		
		Intent intent = getIntent();
		
		scanFormat = intent.getStringExtra(AddBookChoice.EXTRA_SCAN_FORMAT);
		isbn = intent.getStringExtra(AddBookChoice.EXTRA_SCAN_ISBN);
		
		Log.d(TAG, "Format: " + scanFormat + " Content: " + isbn);
		
		previewBtn.setTag(isbn);
		String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"
				+ "q=isbn:" + isbn + "&key=" + KEY;
		
		Log.d(TAG, "Book Search String: " + bookSearchString);

		// Execute the web request via AsyncTask
		new GetBookInfo( findViewById(R.id.progressBar) ).execute(bookSearchString);
		
		//Amazon Pricing
		new GetPriceInfo( findViewById(R.id.progressBar) ).execute(getAmazonSignedRequest(isbn),isbn13to10(isbn));
		
		if (savedInstanceState != null){
		    authorText.setText(savedInstanceState.getString("author"));
		    titleText.setText(savedInstanceState.getString("title"));
		    descriptionText.setText(savedInstanceState.getString("description"));
		    dateText.setText(savedInstanceState.getString("date"));
		    ratingCountText.setText(savedInstanceState.getString("ratings"));
		    int numStars = savedInstanceState.getInt("stars");//zero if null
		    for(int s=0; s<numStars; s++){
		        starViews[s].setImageResource(R.drawable.star);
		        starLayout.addView(starViews[s]);
		    }
		    starLayout.setTag(numStars);
		    thumbImg = (Bitmap)savedInstanceState.getParcelable("thumbPic");
		    thumbView.setImageBitmap(thumbImg);
		    previewBtn.setTag(savedInstanceState.getString("isbn"));
		     
		    //if(savedInstanceState.getBoolean("isEmbed")) previewBtn.setEnabled(true);
		    //else previewBtn.setEnabled(false);
		    if(savedInstanceState.getInt("isLink")==View.VISIBLE) linkBtn.setVisibility(View.VISIBLE);
		    else linkBtn.setVisibility(View.GONE);
		    //previewBtn.setVisibility(View.VISIBLE);
		    if(savedInstanceState.getInt("offers")==View.VISIBLE) linkBtn.setVisibility(View.VISIBLE);
		    else linkBtn.setVisibility(View.GONE);
		}
		
		String query = "select * from USER_LIB where ISBN = \"" + isbn + 
				"\" and userID= \"" + Variables.getUserId() + "\"";
		Log.d(TAG, "Query CHECK USER LIB= " + query);
		new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), 
				Variables.getSalt(), query, QUERY_USERLIB, this, 
				Variables.getRest(), findViewById(R.id.progressBar)).execute();
		//new GetPriceInfo().execute(getAmazonSignedRequest(isbn));
	}
	
	protected void onSaveInstanceState(Bundle savedBundle) {
	    savedBundle.putString("title", ""+titleText.getText());
	    savedBundle.putString("author", ""+authorText.getText());
	    savedBundle.putString("description", ""+descriptionText.getText());
	    savedBundle.putString("date", ""+dateText.getText());
	    savedBundle.putString("ratings", ""+ratingCountText.getText());
	    savedBundle.putParcelable("thumbPic", thumbImg);
	    if(starLayout.getTag()!=null)
	        savedBundle.putInt("stars", Integer.parseInt(starLayout.getTag().toString()));
	    //savedBundle.putBoolean("isEmbed", previewBtn.isEnabled());
	    savedBundle.putInt("isLink", linkBtn.getVisibility());
	    savedBundle.putInt("offers", offersBtn.getVisibility());
	    if(previewBtn.getTag()!=null)
	        savedBundle.putString("isbn", previewBtn.getTag().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.book_scanner, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	public void link(View view) {
		// get the url tag
		String tag = (String) linkBtn.getTag();
		// launch the url
		Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse(tag));
		startActivity(webIntent);
	}

	public void preview(View view) {
		String tag = (String) previewBtn.getTag();
		Log.d("BOOKSCANNER", "Book preview should be here");

		Intent intent = new Intent(this, EmbeddedBook.class);
		intent.putExtra("isbn", tag);
		startActivity(intent);
	}
	
	public void offers(View view) {
		// get the url tag
		String tag = (String) offersBtn.getTag();
		// launch the url
		Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse(tag));
		startActivity(webIntent);
	}

	public void addBook(View view) {
		
		Intent intent = new Intent(this, AddBook.class);
		intent.putExtra(BookSummary.EXTRA_BOOK_ISBN, isbn);
		intent.putExtra(BookSummary.EXTRA_BOOK_AUTHOR, author);
		intent.putExtra(BookSummary.EXTRA_BOOK_TITLE, title);
		startActivity(intent);
	}
	
	private String getAmazonSignedRequest( String isbn )
	{
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        String requestUrl = null;
        String title = null;

        String queryString = "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Large&AssociateTag=splint-20&SearchIndex=Books&IdType=ISBN&ItemId="
                + isbn;
        requestUrl = helper.sign(queryString);
        Log.d("Amazon", "Request is \"" + requestUrl + "\"");
        
        Log.d("Amazon", "After: Isbn13= " + isbn + " Isbn10= " + isbn13to10(isbn));
        
        return requestUrl;
	}
	
	private String isbn13to10(String isbn13)
	{
		String isbn10 = null;
		
		if( isbn13.length() == 13 && isbn13.substring(0, 3).equals("978") )
		{
			isbn10 = isbn13.substring(3,12);
		}
		
		int factor = 10;
		int sum = 0;
		if( isbn10 != null )
		{
			for ( int i = 0 ; i < isbn10.length() ; i++ )
			{
				sum += Integer.parseInt("" + isbn10.charAt(i)) * factor;
				factor--;
			}
		}
		
		int checkNum = sum % 11;
		
		switch( checkNum )
		{
		case 10:
			isbn10 += 'x';
			break;
		default:
			checkNum = 11 - checkNum;
			isbn10 += Integer.toString(checkNum);
		}
		
		return isbn10;
	}
    
    private Document parseXML(InputStream stream) throws Exception
	{
	    DocumentBuilderFactory objDocumentBuilderFactory = null;
	    DocumentBuilder objDocumentBuilder = null;
	    Document doc = null;
	    try
	    {
	        objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
	        objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
	
	        doc = objDocumentBuilder.parse(stream);
	    }
	    catch(Exception ex)
	    {
	        throw ex;
	    }       
	
	    return doc;
	}
	
	private class GetPriceInfo extends AsyncTask<String, Void, Document> {
		
		private ProgressBar bar = null;
		
		public GetPriceInfo( View view)
		{
			bar = (ProgressBar)view;
		}
		
		protected void onPreExecute() {
			if( bar != null )
			{
				bar.setVisibility(View.VISIBLE);
			}	
	    }
		
		protected Document doInBackground(String... amazonReqs ) 
		{
			for ( String req : amazonReqs )
			{
				try
				{
					URL url = new URL(req);
					URLConnection connection = url.openConnection();

					Document doc = parseXML(connection.getInputStream());
			        return doc;
				}
				catch ( Exception e )
				{
					Log.d("Amazon", "Error: " + e.toString());
				}
		        //NodeList descNodes = doc.getElementsByTagName("Price");  
			}
			return null;
		}
		
		protected void onPostExecute(Document doc)
		{			
			if( doc == null )
			{
				Log.d("Amazon", "Document is null");
			}
			
			NodeList offers = doc.getElementsByTagName("DetailPageURL");
			
			for( int i = 0 ; i < offers.getLength() ; i++)
			{
				if( !offers.item(i).getTextContent().equals("0"))
				{
					offersBtn.setTag(offers.item(i).getTextContent());
					offersBtn.setVisibility(View.VISIBLE);
				}
				else
				{
					offersBtn.setVisibility(View.VISIBLE);
					offersBtn.setEnabled(false);
				}
				Log.d("Amazon", "More Offers URL: " + offers.item(i).getTextContent() );
			}
			
			NodeList items = doc.getElementsByTagName("Item");
	        
	        for( int i = 0 ; i < items.getLength() ; i++ )
	        { 	
	        	if( items.item(i).getFirstChild().getNodeName().equals("ASIN") && items.item(i).getFirstChild().getTextContent().equalsIgnoreCase(isbn13to10(isbn)))
	        	{
	        		Log.d("Amazon","Found");
	        		NodeList childList = items.item(i).getChildNodes();
	        		for( int j = 0 ; j < childList.getLength() ; j++ )
	        		{
	        			if( childList.item(j).getNodeName().equalsIgnoreCase("offersummary") )
	        			{
	        				NodeList cat = childList.item(j).getChildNodes();
	        				for( int c = 0; c < cat.getLength() ; c++ )
	        				{
	        					if ( cat.item(c).getNodeName().equalsIgnoreCase("lowestnewprice"))
	        					{
	        						NodeList l = cat.item(c).getChildNodes();
	        						for( int v = 0 ; v < l.getLength() ; v++ )
	        						{
	        							if ( l.item(v).getNodeName().equalsIgnoreCase("formattedprice") )
	        							{
	        								newPrice.setText("New: " + l.item(v).getTextContent());
	        								Log.d("Amazon", l.item(v).getTextContent());
	        							}
	        						}
	        					}
	        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("lowestusedprice"))
	        					{
	        						NodeList l = cat.item(c).getChildNodes();
	        						for( int v = 0 ; v < l.getLength() ; v++ )
	        						{
	        							if ( l.item(v).getNodeName().equalsIgnoreCase("formattedprice") )
	        							{
	        								usedPrice.setText("Used: " + l.item(v).getTextContent());
	        								Log.d("Amazon", l.item(v).getTextContent());
	        							}
	        						}
	        					}
	        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("totalNew"))
	        					{
	        						newPrice.setText(newPrice.getText() + " (" + cat.item(c).getTextContent() + ")");
	        						Log.d("Amazon", cat.item(c).getTextContent());
	        					}
	        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("totalused"))
	        					{
	        						usedPrice.setText(usedPrice.getText() + " (" + cat.item(c).getTextContent() + ")");
	        						Log.d("Amazon", cat.item(c).getTextContent());
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	        
			if( bar != null )
			{
				bar.setVisibility(View.GONE);
			}
		}
		
	}

	// Fetching Book information from google's servers
	private class GetBookInfo extends AsyncTask<String, Void, String> {
		
		private ProgressBar bar = null;
		
		public GetBookInfo( View view )
		{
			bar = (ProgressBar)view;
		}
		
		protected void onPreExecute() {
			if( bar != null )
			{
				bar.setVisibility(View.VISIBLE);
			}	
	    }

		@Override
		protected String doInBackground(String... bookURLs) {

			StringBuilder bookBuilder = new StringBuilder();

			// Loop in case we have more than 1 URL, in our case, we only have
			// one
			for (String bookSearchURL : bookURLs) {
				// HTTP Cliet
				HttpClient bookClient = new DefaultHttpClient();

				try {
					// Making a web request
					HttpGet bookGet = new HttpGet(bookSearchURL);
					HttpResponse bookResponse = bookClient.execute(bookGet);

					// Check Status of the response
					StatusLine bookSearchStatus = bookResponse.getStatusLine();
					if (bookSearchStatus.getStatusCode() == 200) {
						// Getting message entry from the response
						HttpEntity bookEntity = bookResponse.getEntity();

						// Reading message from the response
						InputStream bookContent = bookEntity.getContent();
						InputStreamReader bookInput = new InputStreamReader(
								bookContent);
						BufferedReader bookReader = new BufferedReader(
								bookInput);

						// Append content to the string builder
						String lineIn;
						while ((lineIn = bookReader.readLine()) != null) {
							bookBuilder.append(lineIn);
						}
						return bookBuilder.toString();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.d("GET_BOOK_INFO", "book builder " + bookBuilder.toString());
			}

			return null;
		}

		// Parse search results
		@Override
		protected void onPostExecute(String result) {
			Log.d("GET_BOOK_INFO", "onPostExecute " + result);
			try {
				//previewBtn.setVisibility(View.VISIBLE);

				// retrive JSON
				JSONObject resultObject = new JSONObject(result);
				JSONArray bookArray = resultObject.getJSONArray("items");

				JSONObject bookObject = bookArray.getJSONObject(0);
				JSONObject volumeObject = bookObject
						.getJSONObject("volumeInfo");

				Log.d("GET_BOOK_INFO_Object", bookObject.toString());
				Log.d("GET_BOOK_INFO_Volume", volumeObject.toString());

				// Get title
				try {
					title = volumeObject.getString("title");
					titleText.setText("TITLE: "
							+ title);
				} catch (JSONException jse) {
					titleText.setText("");
					jse.printStackTrace();
				}

				// Get Authors
				StringBuilder authorBuild = new StringBuilder("");
				try {
					JSONArray authorArray = volumeObject
							.getJSONArray("authors");
					for (int a = 0; a < authorArray.length(); a++) {
						if (a > 0)
							authorBuild.append(", ");
						authorBuild.append(authorArray.getString(a));
					}
					author = authorBuild.toString();
					authorText.setText("AUTHOR(S): " + author);
				} catch (JSONException jse) {
					authorText.setText("");
					jse.printStackTrace();
				}

				// Get public date
				try {
					dateText.setText("PUBLISHED: "
							+ volumeObject.getString("publishedDate"));
				} catch (JSONException jse) {
					dateText.setText("");
					jse.printStackTrace();
				}

				// Get Description
				try {
					descriptionText.setText("DESCRIPTION: "
							+ volumeObject.getString("description"));
				} catch (JSONException jse) {
					descriptionText.setText("");
					jse.printStackTrace();
				}

				// Get Rating stars
				try {
					// Getting number or stars
					double decNumStars = Double.parseDouble(volumeObject
							.getString("averageRating"));
					int numStars = (int) decNumStars;
					// Setting the stars
					starLayout.setTag(numStars);
					starLayout.removeAllViews();
					for (int s = 0; s < numStars; s++) {
						starViews[s].setImageResource(R.drawable.star);
						starLayout.addView(starViews[s]);
					}
				} catch (JSONException jse) {
					starLayout.removeAllViews();
					jse.printStackTrace();
				}

				// Get Rating count
				try {
					ratingCountText.setText(" - "
							+ volumeObject.getString("ratingsCount")
							+ " ratings");
				} catch (JSONException jse) {
					ratingCountText.setText("");
					jse.printStackTrace();
				}

				// Check if there is a preview
//				try {
//					boolean isEmbeddable = Boolean.parseBoolean(bookObject
//							.getJSONObject("accessInfo")
//							.getString("embeddable"));
//					if (isEmbeddable) {
//						previewBtn.setEnabled(true);
//					} else {
//						previewBtn.setEnabled(false);
//					}
//				} catch (JSONException jse) {
//					previewBtn.setEnabled(false);
//					jse.printStackTrace();
//				}

				// Get URL
				try {
					linkBtn.setTag(volumeObject.getString("infoLink"));
					Log.d("Amazon", "URI: " + volumeObject.getString("infoLink"));
					linkBtn.setVisibility(View.VISIBLE);
				} catch (JSONException jse) {
					linkBtn.setVisibility(View.GONE);
					jse.printStackTrace();
				}

				// Getting preview image
				try {
					JSONObject imageInfo = volumeObject
							.getJSONObject("imageLinks");
					new GetBookThumb( findViewById(R.id.progressBar) ).execute(imageInfo
							.getString("smallThumbnail"));
				} catch (JSONException jse) {
					thumbView.setImageBitmap(null);
					jse.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				titleText.setText("NOT FOUND");
				authorText.setText("");
				descriptionText.setText("");
				dateText.setText("");
				starLayout.removeAllViews();
				ratingCountText.setText("");
				thumbView.setImageBitmap(null);
				previewBtn.setVisibility(View.GONE);
			}
			
			if( bar != null )
			{
				bar.setVisibility(View.GONE);
			}
		}

		// Retrives the Thumbnail image
		private class GetBookThumb extends AsyncTask<String, Void, String> {
			
			private ProgressBar bar = null;
			
			public GetBookThumb( View view )
			{
				bar = (ProgressBar)view;
			}
			
			protected void onPreExecute() {
				if( bar != null )
				{
					bar.setVisibility(View.VISIBLE);
				}	
		    }

			@Override
			protected String doInBackground(String... thumbURLs) {
				try {
					// Attempt connection to retrive thumbnail
					URL thumbURL = new URL(thumbURLs[0]);
					URLConnection thumbConn = thumbURL.openConnection();
					thumbConn.connect();

					// Processing thumbnail as BMP
					InputStream thumbIn = thumbConn.getInputStream();
					BufferedInputStream thumbBuff = new BufferedInputStream(
							thumbIn);

					thumbImg = BitmapFactory.decodeStream(thumbBuff);

					thumbBuff.close();
					thumbIn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}

			protected void onPostExecute(String result) {
				thumbView.setImageBitmap(thumbImg);
				if( bar != null )
				{
					bar.setVisibility(View.GONE);
				}	
			}
		}

	}

	@Override
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try
		{
			if(code == QUERY_USERLIB) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("error")) 
				{
					   if(result.getString("error").equalsIgnoreCase("No records found"))
					   {
						   addBtn.setText("Add Book To Personal Library");
						   addBtn.setEnabled(true);
						   addBtn.setVisibility(View.VISIBLE);
					   }
				}
				else {
					addBtn.setText("Book Already In Personal Library");
					addBtn.setEnabled(false);
					addBtn.setVisibility(View.VISIBLE);
				}
			}
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
		}
	}
}
