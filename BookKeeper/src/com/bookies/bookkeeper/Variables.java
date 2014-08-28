package com.bookies.bookkeeper;

import com.madmarcos.resttest.RestFetcher;

public class Variables {
	
	private static int sessionId;
	private static String salt;
	private static RestFetcher rest;
	private static int userId;
	private static boolean admin = false;
	private static final String WS_URL = "https://galadriel.fulgentcorp.com/services.php?json=";
	private static String isbn;
   
	public static int getSessionId(){
		return sessionId;
	}
	public static boolean getAdmin(){
		return admin;
	}
	public static void setAdmin(boolean a){
		admin = a;
	}
	
	public static void setSessionId(int sessionId){
		Variables.sessionId = sessionId;	
	}
	
	public static String getSalt(){
		return salt;
	}
	
	public static void setSalt(String salt){
		Variables.salt = salt;	
	}
	
	public static RestFetcher getRest(){
		return rest;
	}
	
	public static void setRest(RestFetcher rest){
		Variables.rest = rest;	
	}
	

	public static int getUserId(){
		return userId;
	}
	
	public static void setUserId(int userId){
		Variables.userId = userId;	
	}
	
	public static String getWS_URL(){
		return WS_URL;
	}
	
	public static void setIsbn(String isbn){
		Variables.isbn = isbn;
	}
	
	public static String getIsbn(){
		return isbn;
	}
	
	
}
