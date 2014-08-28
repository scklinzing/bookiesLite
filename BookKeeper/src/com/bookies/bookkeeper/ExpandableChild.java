package com.bookies.bookkeeper;

public class ExpandableChild {
	private String text;
	private boolean button;
	
	public String getText(){
		return text;
	}
	public boolean getButton(){
		return button;
	}
	
	public void setText(String a){
		text = a;
	}
	public void setButton(boolean a){
		button = a;
	}
	public ExpandableChild (String a, boolean b){
		text = a;
		button = b;
	}
}
