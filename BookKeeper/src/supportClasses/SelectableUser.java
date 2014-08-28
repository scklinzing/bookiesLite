package supportClasses;

import java.util.Date;

public class SelectableUser {
	private int userID;
	private String userName;
	private boolean isSelected = false;
	
	public int getID(){
		return userID;
	}
	public String getUserName(){
		return userName;
	}
	public boolean getSelected(){
		return isSelected;
	}
	public void setSelected(boolean select){
		isSelected = select;
	}
	
	
	public SelectableUser(int id, String name){
		userID = id;
		userName = name;
	}

}
