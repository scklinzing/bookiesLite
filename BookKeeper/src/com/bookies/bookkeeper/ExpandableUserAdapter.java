package com.bookies.bookkeeper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import supportClasses.BookInfo;
import supportClasses.BookList;
import supportClasses.HackInterface;
import supportClasses.User;
import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.util.Log;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bookies.bookkeeper.R;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

public class ExpandableUserAdapter extends BaseExpandableListAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<ExpandableUser> parentItems;
	private ArrayList<ExpandableChild> child;
	private int ParentClickStatus=-1;
	private int ChildClickStatus=-1;
	private int QUERY_ACCEPT_REQUEST = 4;
	private int QUERY_REJECT_REQUEST = 5;
	Context context;
	
	public final static String EXTRA_FOUNDUSER = "com.bookies.bookkeeper.FOUNDUSER";
	public final static String EXTRA_USERNAME = "com.bookies.bookkeeper.USERNAME";
	public final static String EXTRA_EMAIL = "com.bookies.bookkeeper.EMAIL";
	public final static String EXTRA_PASSWORD = "com.bookies.bookkeeper.PASSWORD";
	public final static String EXTRA_USERTYPE = "com.bookies.bookkeeper.USERTYPE";
	public final static String EXTRA_USERSTATUS = "com.bookies.bookkeeper.USERSTATUS";
		
		public ExpandableUserAdapter(ArrayList<ExpandableUser> parents) {
		        this.parentItems = parents;
	}
	//	expands the selection
	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
		this.activity = activity;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return parentItems.get(groupPosition).getChildren().get(childPosition);
	}
	
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		if( ChildClickStatus!=childPosition)
        {
           ChildClickStatus = childPosition; 
        }  
         
        return childPosition;
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parentView) {
    	final ExpandableUser parent = parentItems.get(groupPosition);
    	ExpandableChild child = parent.getChildren().get(childPosition);
    	
        convertView = inflater.inflate(R.layout.group, parentView, false);
        final Context context = parentView.getContext();
        if(child.getButton()){
        	( (TextView) convertView.findViewById(R.id.optionalChildButton)).setText(child.getText());
        	if(child.getText().equals("Edit User")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String userID = parent.getChildren().get(0).getText();
						User user = UserForm.getUserList().getID(Integer.parseInt(userID));
						Intent intent = new Intent(context, EditUser.class);
						intent.putExtra(EXTRA_FOUNDUSER, user.getID());
						intent.putExtra(EXTRA_USERNAME, user.getUserName());
						intent.putExtra(EXTRA_EMAIL, user.getEmail());
						intent.putExtra(EXTRA_USERTYPE, user.getUserType());
						intent.putExtra(EXTRA_USERSTATUS, user.getStatus());
												
						//userSecurity = b.getString(EXTRA_USERSECURITY);
												
						context.startActivity(intent);
						}      			
        		});
        	}//end if "edit user"
        	if(child.getText().equals("Accept Request")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String userID = parent.getChildren().get(0).getText();
						String query = "update FriendList "+
								"set Status = 1"+
								" where (UserID = " + Integer.parseInt(userID) + 
								" and FriendUserID = " + Variables.getUserId() +")";
								Log.d("ExpandableAdapter", "Query = " + query);
						UserForm.editingUser = Integer.parseInt(userID);
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_ACCEPT_REQUEST, (QueryCallback) context, Variables.getRest(), null).execute();
						}      			
        		});
        	}//end accept request
        	if(child.getText().equals("Reject Request")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String userID = parent.getChildren().get(0).getText();
						String query = "delete from FriendList "+
								"where (UserID = " +Integer.parseInt(userID) + 
								" and FriendUserID = " + Variables.getUserId() +")";
								Log.d("ExpandableAdapter", "Query = " + query);
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_ACCEPT_REQUEST, (QueryCallback) context, Variables.getRest(), null).execute();
						
					}      			
        		});
        	}
        	if(child.getText().equals("View User Book List")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String userID = parent.getChildren().get(0).getText();
						Intent intent = new Intent(context, MainForm.class);
						intent.putExtra("VIEWING", 2);
						intent.putExtra("FRIEND_ID", Integer.parseInt(userID));
						context.startActivity(intent);
					}      			
        		});
        	}
        }else{
        	((TextView) convertView.findViewById(R.id.textView1)).setText(child.getText());
        	( convertView.findViewById(R.id.optionalChildButton)).setVisibility(View.GONE);	
        }
        
        return convertView;
    }
    @Override
    public boolean isEmpty()
    {
        return ((parentItems == null) || parentItems.isEmpty());
    }

    @SuppressWarnings("unchecked")
	@Override
    public int getChildrenCount(int groupPosition) {
    	int size=0;
        if(parentItems.get(groupPosition).getChildren()!=null)
            size = parentItems.get(groupPosition).getChildren().size();
        return size;
    }


	@Override
	public Object getGroup(int groupPosition) {
		return parentItems.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}
	
	@Override
	public void onGroupCollapsed(int groupPosition) {
	    super.onGroupCollapsed(groupPosition);
	}
	 

	@Override
	public void onGroupExpanded(int groupPosition) {
	    super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		         
         ParentClickStatus=groupPosition;
         if(ParentClickStatus==0)
             ParentClickStatus=-1;
          
         return groupPosition;
	}

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView) {
    	ExpandableUser parent = parentItems.get(groupPosition);
    	Context context = parentView.getContext();
    	
    	convertView = inflater.inflate(R.layout.expandable_parent, null);
    	((TextView) convertView.findViewById(R.id.expandableMainText)).setText(parent.getMainString());
    	if(parent.getMainString().equals("Current Friends")){
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextColor(Color.BLUE);
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextSize(24);
    	}
    	if(parent.getMainString().equals("Pending Friend Requests")){
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextColor(Color.BLUE);
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextSize(24);
    	}
    	if(parent.getMainString().equals("Friend Requests Pending Your Acceptance")){
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextColor(Color.BLUE);
    		((TextView) convertView.findViewById(R.id.expandableMainText)).setTextSize(24);
    	}
        return convertView;
    }


	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	

}
