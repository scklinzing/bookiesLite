package com.bookies.bookkeeper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BookScanner extends ActionBarActivity {

	private final String KEY = "AIzaSyBDavskpIhMGwK_mL6w4EwuHA_2xOxyHvo";

	private Button previewBtn;
	private Button linkBtn;
	private TextView authorText;
	private TextView titleText;
	private TextView descriptionText;
	private TextView dateText;
	private TextView ratingCountText;

	private LinearLayout starLayout;
	private ImageView thumbView;
	private ImageView[] starViews;

	private Bitmap thumbImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_scanner);

		previewBtn = (Button) findViewById(R.id.preview_btn);
		previewBtn.setVisibility(View.GONE);
		linkBtn = (Button) findViewById(R.id.link_btn);
		linkBtn.setVisibility(View.GONE);

		authorText = (TextView) findViewById(R.id.book_author);
		titleText = (TextView) findViewById(R.id.book_title);
		descriptionText = (TextView) findViewById(R.id.book_description);
		dateText = (TextView) findViewById(R.id.book_date);
		starLayout = (LinearLayout) findViewById(R.id.star_layout);
		ratingCountText = (TextView) findViewById(R.id.book_rating_count);
		thumbView = (ImageView) findViewById(R.id.thumb);

		starViews = new ImageView[5];
		for (int s = 0; s < starViews.length; s++) {
			starViews[s] = new ImageView(this);
		}
		
		// if (savedInstanceState == null) {
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.container, new PlaceholderFragment()).commit();
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_scanner, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void scan(View view) {
		IntentIntegrator intentIntegrator = new IntentIntegrator(this);
		intentIntegrator.initiateScan();
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

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanFormat = scanningResult.getFormatName();
			String scanContent = scanningResult.getContents();
			Log.d("SCAN", "content: " + scanContent + " - format: "
					+ scanFormat);
			if (scanContent != null && scanFormat != null
					&& scanFormat.equalsIgnoreCase("EAN_13")) {
				previewBtn.setTag(scanContent);
				String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"
						+ "q=isbn:" + scanContent + "&key=" + KEY;

				// Execute the web request via AsyncTask
				new GetBookInfo().execute(bookSearchString);
			} else {
				Toast.makeText(getApplicationContext(), "Not A Valid Scan!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "No scan data received!",
					Toast.LENGTH_SHORT).show();
		}
	}

	// Fetching Book information from google's servers
	private class GetBookInfo extends AsyncTask<String, Void, String> {

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
				previewBtn.setVisibility(View.VISIBLE);

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
					titleText.setText("TITLE: "
							+ volumeObject.getString("title"));
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
					authorText.setText("AUTHOR(S): " + authorBuild.toString());
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

				// Check if we have a preview image
				try {
					boolean isEmbeddable = Boolean.parseBoolean(bookObject
							.getJSONObject("accessInfo")
							.getString("embeddable"));
					if (isEmbeddable) {
						previewBtn.setEnabled(true);
					} else {
						previewBtn.setEnabled(false);
					}
				} catch (JSONException jse) {
					previewBtn.setEnabled(false);
					jse.printStackTrace();
				}

				// Get URL
				try {
					linkBtn.setTag(volumeObject.getString("infoLink"));
					linkBtn.setVisibility(View.VISIBLE);
				} catch (JSONException jse) {
					linkBtn.setVisibility(View.GONE);
					jse.printStackTrace();
				}

				// Getting preview image
				try {
					JSONObject imageInfo = volumeObject
							.getJSONObject("imageLinks");
					new GetBookThumb().execute(imageInfo
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
		}

		// Retrives the Thumbnail image
		private class GetBookThumb extends AsyncTask<String, Void, String> {

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
			}
		}

	}
}
