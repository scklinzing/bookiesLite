package com.bookies.bookkeeper;

import java.util.ArrayList;

public class ExpandableParent {
	private String mainString;
	private String subString;
	private String icon;
	
	private ArrayList<ExpandableChild> children;
	
	
	public String getMainString(){
		return mainString;
	}
	public String getSubString(){
		return subString;
	}
	public void setMainString(String a){
		mainString = a;
	}
	public void setSubString(String a){
		subString = a;
	}
	public ArrayList<ExpandableChild> getChildren(){
		return children;
	}
	public void setChildren(ArrayList<ExpandableChild> c){
		children = c;
	}
	public String getIcon(){
		return icon;
	}
	public void setIcon(String icon){
		this.icon = icon;
	}
	public ExpandableParent(String main, String sub, String icon){
		mainString = main;
		subString = sub;
		this.icon = icon;
	}
}
