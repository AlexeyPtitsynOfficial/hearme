package com.donearh.hearme;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;

public class ExtendedSearchDialog extends DialogFragment implements android.view.View.OnClickListener{

	private EditText mSearchText;
	private CheckBox mFilterByAuthors;
	private CheckBox mFilterByTitles;
	private CheckBox mFilterByDesc;
	
	public interface OnExtendedSearch{
		public void onExtendedSearch(ArrayList<Integer>filter_list, String search_text);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.extended_search_dialog, container, false);
		getDialog().setTitle(R.string.ext_search);
		mSearchText = (EditText)rootView.findViewById(R.id.search_text);
		
		mFilterByAuthors = (CheckBox)rootView.findViewById(R.id.filter_by_authors);
		mFilterByTitles = (CheckBox)rootView.findViewById(R.id.filter_by_titles);
		mFilterByDesc = (CheckBox)rootView.findViewById(R.id.filter_by_desc);
		
		Button btn_apply = (Button)rootView.findViewById(R.id.btn_apply);
		btn_apply.setOnClickListener(this);
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
		super.onResume();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ArrayList<Integer> filter_list = new ArrayList<Integer>();
		if(mFilterByAuthors.isChecked()){
			filter_list.add(1);
		}
		else{
			filter_list.add(0);
		}
		if(mFilterByTitles.isChecked()){
			filter_list.add(1);
		}
		else{
			filter_list.add(0);
		}
		if(mFilterByDesc.isChecked()){
			filter_list.add(1);
		}
		else{
			filter_list.add(0);
		}
		((MainControlBarActivity)getActivity()).onExtendedSearch(filter_list, mSearchText.getText().toString().trim());
		dismiss();
	}

	
}
