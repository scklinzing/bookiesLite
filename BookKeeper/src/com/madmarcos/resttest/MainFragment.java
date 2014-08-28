//package com.madmarcos.resttest;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//
//public class MainFragment extends Fragment implements LoginCallback, QueryCallback {
//	private static final int QUERY_SELECT_TEST = 1;
//	
//	private static final String DEFAULT_LOGIN = "put your WS login here";
//	private static final String DEFAULT_PW = "put your WS pw here";
//	//USE THIS ONE FOR A SECURE CONNECTION TO THE WEB SERVICE
//	private static final String WS_URL = "https://galadriel.fulgentcorp.com/services.php?json=";
//	//private static final String WS_URL = "http://galadriel.cs.utsa.edu/bifrost/services.php?json=";
//	private static final String TAG = "MainFragment";
//	private int sessionId;
//	private String salt;
//	
//	private TextView tvSessionId;
//	private TextView tvSalt;
//	private EditText editQuery;
//	private EditText editLogin;
//	private EditText editPassword;
//	
//	private Button runLogin, runQuery;
//	
//	private ListView listRows;
//	private SimpleAdapter listAdapter;
//	List<Map<String, String>> rows;
//
//	private String query; 
//	private RestFetcher rest;
//	
//	private void initSession() {
//		sessionId = 0;
//		salt = "";
//		tvSessionId.setText(Integer.toString(sessionId));
//		tvSalt.setText(salt);
//	}
//
//	private void setSessionInfo(int i, String s) {
//		sessionId = i;
//		salt = s;
//		tvSessionId.setText(Integer.toString(sessionId));
//		tvSalt.setText(salt);
//	}
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		//get view from inflated layout
//		View v = inflater.inflate(R.layout.fragment_main,  container, false);
//		
//		tvSessionId = (TextView) v.findViewById(R.id.tvSessionId);
//		tvSalt = (TextView) v.findViewById(R.id.tvSalt);
//		
//		editLogin = (EditText) v.findViewById(R.id.editLogin);
//		editLogin.setText(DEFAULT_LOGIN);
//		editPassword = (EditText) v.findViewById(R.id.editPassword);
//		editPassword.setText(DEFAULT_PW);
//		
//		//init the fetcher and give it the web service cert so it will be trusted
//		//NOTE: you must do the initKeyStore step if you want to use SSL (i.e., https)
//		rest = new RestFetcher();
//		try {
//			InputStream caInput = this.getActivity().getAssets().open("fulgentcorp.crt");
//			rest.initKeyStore(caInput);
//			caInput.close();
//		} catch (IOException e) {
//			Log.e(TAG, "*** initKeyStore error: " + e.getMessage());
//		}
//		
//		runLogin = (Button) v.findViewById(R.id.buttonLogin);
//		runLogin.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//clear session info
//				initSession();
//				//clear result list
//				rows.clear();
//	        	listAdapter.notifyDataSetChanged();
//				new LoginTask(WS_URL, editLogin.getText().toString(), editPassword.getText().toString(), MainFragment.this, rest).execute();
//			}
//		});
//		
//		editQuery = (EditText) v.findViewById(R.id.editQuery);
//		
//		listRows = (ListView) v.findViewById(R.id.listView1);
//		rows = new ArrayList<Map<String,String>>();
//		listAdapter = new SimpleAdapter(this.getActivity(), rows, android.R.layout.simple_list_item_1, new String[] {"row"}, new int[] {android.R.id.text1});
//		listRows.setAdapter(listAdapter);
//
//		runQuery = (Button) v.findViewById(R.id.buttonQuery);
//		runQuery.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//clear result list
//				rows.clear();
//	        	listAdapter.notifyDataSetChanged();
//	        	
//				query = editQuery.getText().toString();
//				new QueryTask(WS_URL, sessionId, salt, query, QUERY_SELECT_TEST, MainFragment.this, rest).execute();
//			}
//		});
//
//		initSession();
//		
//		query = "select * from test1";
//		editQuery.setText(query);
//		return v;
//	}
//	
//	//GUI updates need to be done on the main UI thread, not Background tasks
//	//All of your query-specific handling code should happen in one of these callbacks 
//	@Override
//	public void onLoginTaskCompleted(JSONObject result) {
//		try {
//			int tempSessionId = 0;
//			String tempSalt = "";
//			if(!result.isNull("session_id")) {
//				tempSessionId = result.getInt("session_id");
//				tempSalt = result.getString("salt");
//				setSessionInfo(tempSessionId, tempSalt);
//			} else {
//				setSessionInfo(0, "Error");
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	//If you need to handle multiple queries then you can pass an appropriate request code
//	//to the task which can then be passed to the handler below
//	//Thus, you would only need 1 handler to accommodate multiple queries
//	@Override
//	public void onQueryTaskCompleted(int code, JSONObject result) {
//		//code: allows 1 handler to handle different queries
//		//		use static ints in the calling class to communicate query code
//		try {
//			if(code == QUERY_SELECT_TEST) {
//				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
//					int row = 0;
//					while(!result.isNull(Integer.toString(row))) {
//						JSONObject o2 = result.getJSONObject(Integer.toString(row));
//						HashMap<String, String> hm = new HashMap<String, String>();
//						hm.put("row", o2.toString());
//						rows.add(hm);
//						row++;
//					}
//					listAdapter.notifyDataSetChanged();
//				} else 
//					Log.e(TAG, "*** Error: " + result);
//			} else {
//				Log.e(TAG, "*** Error: unknown code");
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//}
