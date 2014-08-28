//package com.madmarcos.resttest;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.view.Menu;
//
//public abstract class SingleFragmentActivity extends FragmentActivity {
//	protected abstract Fragment createFragment();
//	
//	protected int getLayoutResId() {
//		return R.layout.activity_fragment;
//	}
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(getLayoutResId());
//		
//		FragmentManager fm = this.getSupportFragmentManager();
//		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
//		if(fragment == null) {
//			fragment = createFragment();
//			//below is example of fluent interface
//			//it is syntactically the same as fm.beginTransaction().add(...).commit(); 
//			fm.beginTransaction()
//				.add(R.id.fragment_container, fragment)
//				.commit();
//		}
//	}
//
//
//}
