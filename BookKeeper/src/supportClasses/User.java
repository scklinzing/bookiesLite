package supportClasses;

import java.util.Date;

public class User {
	private int userID;
	private String userName;
	/* for admins only*/
	private String emailAddress = "";
	private Date userStartDate = null;
	private int userStatus = -1;
	private int userType = 0;
	
	
	public int getID(){
		return userID;
	}
	public String getUserName(){
		return userName;
	}
	public String getEmail(){
		return emailAddress;
	}
	public Date getStartDate(){
		return userStartDate;
	}
	public int getStatus(){
		return userStatus;
	}
	public int getUserType(){
		return userType;
	}
	
	
	public User(int id, String name){
		userID = id;
		userName = name;
	}
	public User(int id, String name, String email, Date startDate, int status, int type){
		userID = id;
		userName = name;
		emailAddress = email;
		userStartDate = startDate;
		userStatus = status;
		userType = type;
	}

}
