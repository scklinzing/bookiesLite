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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bookies.bookkeeper.R;
import com.madmarcos.resttest.QueryCallback;
import com.madmarcos.resttest.QueryTask;

public class MyExpandableAdapter extends BaseExpandableListAdapter implements QueryCallback {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<ExpandableParent> parentItems;
	private ArrayList<ExpandableChild> child;
	private int ParentClickStatus=-1;
	private int ChildClickStatus=-1;
	private int QUERY_ADD_BOOK = 10;
	private static final int QUERY_DELETE_REC = 5;
	private Context context;
	private View barView;
	
	//holds info to pass  HP added
	public final static String EXTRA_ISBN = "com.bookies.bookkeeper.ISBN";
	public final static String EXTRA_AUTHOR = "com.bookies.bookkeeper.AUTHOR";
	public final static String EXTRA_TITLE = "com.bookies.bookkeeper.TITLE";
	public final static String EXTRA_RATING = "com.bookies.bookkeeper.RATING";
	public final static String EXTRA_STATUS = "com.bookies.bookkeeper.STATUS";
	public final static String EXTRA_COMMENT = "com.bookies.bookkeeper.COMMENT";
	public final static String EXTRA_SECURITY = "com.bookies.bookkeeper.SECURITY";
	public final static String EXTRA_ID = "com.bookies.bookkeeper.ID";
	public final static String EXTRA_DATE = "com.bookies.bookkeeper.DATE";
	public final static String EXTRA_USERID = "com.bookies.bookkeeper.USERID";

	public MyExpandableAdapter(ArrayList<ExpandableParent> parents, View v) {
        this.parentItems = parents;
        barView = v;
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
	
	@SuppressLint("NewApi")
	public void onQueryTaskCompleted(int code, JSONObject result) {
		try {
			if(code == QUERY_ADD_BOOK) {
				if(result != null && !result.isNull("response_status") && result.getString("response_status").equalsIgnoreCase("success")) {
					Log.d("WebQuery", "Added Book" + result);
				} else {
					Log.e("WebQuery", "*** Error: " + result);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		if( ChildClickStatus!=childPosition) {
           ChildClickStatus = childPosition; 
        }  
        return childPosition;
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parentView) {
    	final ExpandableParent parent = parentItems.get(groupPosition);
    	ExpandableChild child = parent.getChildren().get(childPosition);
    	
        convertView = inflater.inflate(R.layout.group, parentView, false);
        final Context context = parentView.getContext();
        if(child.getButton()){
        	( (TextView) convertView.findViewById(R.id.optionalChildButton)).setText(child.getText());
        	
        	/* this is for Edit Book on the Book Click Menu */
        	if(child.getText().equals("Edit Book")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String isbn = parent.getChildren().get(0).getText();
						Intent intent = new Intent(context, EditPersonalBook.class);
						intent.putExtra(EXTRA_ISBN, isbn);  //HP changed
						BookInfo book;
						book = MainForm.getMyList().getISBN(isbn);
						
						if(book == null){
							Toast.makeText(context, "Could not find ISBN " + isbn + " in my list!", Toast.LENGTH_SHORT).show();
							return;
						}
						intent.putExtra(EXTRA_AUTHOR, book.getBookAuthor());//HP changed
						intent.putExtra(EXTRA_TITLE, book.getBookName());//HP changed
						intent.putExtra(EXTRA_RATING, book.getRating());//HP changed
						intent.putExtra(EXTRA_STATUS, book.getBookStatus());//HP changed
						intent.putExtra(EXTRA_COMMENT,  book.getUserComment());//HP added
						intent.putExtra(EXTRA_SECURITY, book.getBookSecurity()); //HP added
						intent.putExtra(EXTRA_ID, book.getBookID()); //HP added
						intent.putExtra(EXTRA_DATE, book.getDateRead().getTime()); //HP added
						intent.putExtra("VIEWING", MainForm.getViewing());
						context.startActivity(intent);
					}      			
        		});
        	}//end if "edit Review"
        	
        	if(child.getText().equals("View Summary")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String isbn = parent.getChildren().get(0).getText();
						Intent intent = new Intent(context, BookSummary.class);
						intent.putExtra(AddBookChoice.EXTRA_SCAN_ISBN, isbn);
						intent.putExtra(AddBookChoice.EXTRA_SCAN_FORMAT, "EAN13");
						context.startActivity(intent);
					}      			
        		});
        	}//end if "View Summary"
        	
        	if(child.getText().equals("Add to my list")){
        		//add action listener if book = edit book.
        		( convertView.findViewById(R.id.optionalChildButton)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String isbn = parent.getChildren().get(0).getText();
						//Toast.makeText(context, "Button for " + isbn + " is clicked !", Toast.LENGTH_SHORT).show();
						//- not for use by user - scanned = b.getString(EXTRA_SCANNED);
						//- should be called at time of reset-numRatings = b.getString(EXTRA_NUMOFRATINGS);
						//- should be called at time of reset-sumRatings = b.getString(EXTRA_SUMOFRATINGS);
						String query = "insert into USER_LIB (ISBN, Status, userID) "+
								"values ("+ isbn +",2, " + Variables.getUserId() +")";
								Log.d("ExpandableAdapter", "Query = " + query);
								
								Variables.setIsbn(isbn);
								
						new QueryTask(Variables.getWS_URL(), Variables.getSessionId(), Variables.getSalt(), query, QUERY_ADD_BOOK, (QueryCallback) context, Variables.getRest(), barView).execute();
					}      			
        		});
        	}
        } else {
        	((TextView) convertView.findViewById(R.id.textView1)).setText(child.getText());
        	( convertView.findViewById(R.id.optionalChildButton)).setVisibility(View.GONE);	
        }
        return convertView;
    }
    @Override
    public boolean isEmpty() {
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
    	ExpandableParent parent = parentItems.get(groupPosition);
    	Context context = parentView.getContext();
    	
    	convertView = inflater.inflate(R.layout.expandable_parent, null);
    	((TextView) convertView.findViewById(R.id.expandableMainText)).setText(parent.getMainString());
        ((TextView) convertView.findViewById(R.id.expandableSubText)).setText(parent.getSubString());
        ImageView image=(ImageView)convertView.findViewById(R.id.expandableImage);
        
        image.setImageResource( context.getResources().getIdentifier(
                   "com.bookies.bookkeeper:drawable/"+parent.getIcon(),null,null));       
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
