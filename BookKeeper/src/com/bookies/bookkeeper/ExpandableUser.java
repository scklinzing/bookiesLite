package com.bookies.bookkeeper;

import java.util.ArrayList;

public class ExpandableUser {
	private String mainString;
	
	private ArrayList<ExpandableChild> children;
	
	
	public String getMainString(){
		return mainString;
	}
	
	public void setMainString(String a){
		mainString = a;
	}
	
	public ArrayList<ExpandableChild> getChildren(){
		return children;
	}
	public void setChildren(ArrayList<ExpandableChild> c){
		children = c;
	}
	
	public ExpandableUser(String main){
		mainString = main;
		
	}
}
