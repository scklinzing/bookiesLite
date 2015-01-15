package com.bookies.bookkeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Code for the preference menu.
 * >res>xml>settings.xml
 */
public class Prefs extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String ARRANGE_BY = "arrangeBy";
	private static final String ARRANGE_BY_DEF = "t";
	private static final String FILTER_BY_STATUS = "filterByStatus";
	private static final String FILTER_BY_RATING = "filterByRating";
	private static final String TITLE_SEARCH = "titleSearch";
	private static final String AUTHOR_SEARCH = "authorSearch";
	private static final String FILTER_BY_STATUS_DEF = "0";
	private static final String FILTER_BY_RATING_DEF = "n";
	private static final String TITLE_SEARCH_DEF = "";
	private static final String AUTHOR_SEARCH_DEF = "";
	private Preference resetDialogPreference;
	// An intent object, that holds the intent that started this Activity.
	private Intent startIntent;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// the new version takes effect in version 11- can add implementation
		// for
		// multiplatform support....
		addPreferencesFromResource(R.xml.settings);

		/*
		 * Set the default values by reading the "android:defaultValue"
		 * attributes from each preference at the 'activity_prefs.xml' file.
		 */
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		// Initialize the preference object by obtaining a handle to the
		// ResetDefDiagPref object as a Preference
		this.resetDialogPreference = getPreferenceScreen().findPreference(
				"resetDialog");

		// Store the Intent that started this Activity at this.startIntent.
		this.startIntent = getIntent();

		// Set the OnPreferenceChangeListener for the resetDialogPreference
		this.resetDialogPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// Both enter and exit animations are set to zero, so no
						// transition animation is applied
						overridePendingTransition(0, 0);
						// Call this line, just to make sure that the system
						// doesn't apply an animation
						startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						// Close this Activity
						finish();
						// Again, don't set an animation for the transition
						overridePendingTransition(0, 0);
						// Start the activity by calling the Intent that have
						// started this same Activity
						startActivity(startIntent);
						// Return false, so that nothing happens to the
						// preference values
						return false;
					}
				});
		Preference button = (Preference) findPreference("returnButton");
		button.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				Log.d("preferences", "clicked return preference");
				Context context = preference.getContext();
				Intent intent = new Intent(context, MainForm.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}

		});
		
		Preference addButton = (Preference) findPreference("addBook");
		addButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				Log.d("preferences", "ADD NEW BOOK in Prefs.java");
				Context context = preference.getContext();
				Intent intent = new Intent(context, AddBookChoice.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}

		});
	}

	public static void returnToMain() {
		// the onclick!
		Log.d("Preferences", "Return to main clicked");
	}

	public static char getArrangeBy(Context context) {
		String arrange = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(ARRANGE_BY, ARRANGE_BY_DEF);
		return arrange.charAt(0);
	}

	public static char getFilterByStatus(Context context) {
		String status = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(FILTER_BY_STATUS, FILTER_BY_STATUS_DEF);
		return status.charAt(0);
	}

	public static char getFilterByRating(Context context) {
		String rating = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(FILTER_BY_RATING, FILTER_BY_RATING_DEF);
		return rating.charAt(0);
	}

	public static String getAuthorSearch(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(AUTHOR_SEARCH, AUTHOR_SEARCH_DEF);
	}

	public static String getTitleSearch(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(TITLE_SEARCH, TITLE_SEARCH_DEF);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// implement listener
	}
}
