package com.bookies.bookkeeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class UserAgreement extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_agreement);
		
		String str = getString(R.string.user_agreement) + "\n\n" + 
						getString(R.string.tos1) + "\n\n" +
						getString(R.string.tos2) + "\n\n" +
						getString(R.string.tos3) + "\n\n" +
						getString(R.string.tos4) + "\n\n" +
						getString(R.string.tos5) + "\n\n" +
						getString(R.string.tos6) + "\n\n" +
						getString(R.string.tos7);
		
		((TextView)findViewById(R.id.tos)).setText(str);
	}
}
