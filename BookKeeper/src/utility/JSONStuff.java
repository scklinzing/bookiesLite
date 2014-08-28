package utility;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONStuff {
	public static boolean isNumericInt(String str) {  
		try {  
			int n = Integer.parseInt(str);  
		} catch(NumberFormatException exception) {  
			return false;  
		}  
		return true;  
	}
	
	public static HashMap<String, Object> decodeJSON(String s) throws JSONException {
		HashMap<String, Object> m = new HashMap<String, Object>();
		JSONObject j = new JSONObject(s);
		Iterator<String> keys = j.keys();
	    while (keys.hasNext()){
	    	String key = keys.next();
	    	//System.out.println("key = " + key);
	    	//is next value a String or a JSONObject?
	    	if(isNumericInt(key))
	    		m.put(key, JSONStuff.decodeJSON(j.getJSONObject(key).toString()));
	    	else
	    		m.put(key, j.getString(key));
	    }
		return m;
	}
	
	public static JSONObject encodeJSON(HashMap<String, Object> m) throws JSONException {
		JSONObject j = new JSONObject();
		for(Object key : m.keySet())
	    	j.put((String) key, m.get(key));
		return j;
	}

	public static String encodeJSONString(HashMap<String, Object> m) throws JSONException {
		JSONObject j = new JSONObject();
		for(Object key : m.keySet())
	    	j.put((String) key, m.get(key));
		return j.toString();
	}
}
