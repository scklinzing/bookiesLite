package com.madmarcos.resttest;

import org.json.JSONObject;

public interface QueryCallback {
	void onQueryTaskCompleted(int code, JSONObject result);
}
