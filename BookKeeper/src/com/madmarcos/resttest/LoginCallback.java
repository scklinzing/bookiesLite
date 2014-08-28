package com.madmarcos.resttest;

import org.json.JSONObject;

public interface LoginCallback {
	void onLoginTaskCompleted(JSONObject result);
}
