package com.bookies.bookkeeper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import supportClasses.*;

public class RecommendAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	ArrayList<SelectableUser> users;
	
	
	public RecommendAdapter(Context context, ArrayList<SelectableUser> a){
		mInflater = LayoutInflater.from(context);
		users = a;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.rec_friend, parent, false);
			holder.check = (CheckBox) convertView.findViewById(R.id.recommendCheck);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		holder.check.setText(users.get(position).getUserName());
		holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				users.get(position).setSelected(holder.check.isChecked());
				
			}
		});
		return convertView;
	}
	public class Holder{
		public CheckBox check;
	}

}
