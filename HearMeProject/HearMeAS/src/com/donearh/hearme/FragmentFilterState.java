package com.donearh.hearme;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentFilterState extends Fragment implements AnimationListener{
	
	private TextView mFilterText;
	
	private Animation closeAnim;
	private Animation openAnim;
	
	private LinearLayout main_layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_filter_state, container, false);
		
		main_layout = (LinearLayout)rootView.findViewById(R.id.main_layout);
		
		openAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.open_filter);
		closeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.close_filter);
		closeAnim.setAnimationListener(this);
		ArrayList<String> filter_array = getArguments().getStringArrayList("filter_array");
		mFilterText = (TextView)rootView.findViewById(R.id.filter_text);
		setFilter(filter_array);	
		
		
		
		return rootView;
	}
	
	public void setFilter(ArrayList<String> filter_array){
		
		if(filter_array.size() == 1)
			mFilterText.setText(getActivity().getString(R.string.filter_active) +" *"+ filter_array.get(0));
		else{
			mFilterText.setText(getActivity().getString(R.string.filter_active));
			for (String filter : filter_array) {
				mFilterText.setText(mFilterText.getText() +" *"+ filter);
			}
		}
		if(main_layout.getAnimation() != openAnim){
			if(main_layout.getVisibility() == View.GONE)
				main_layout.setVisibility(View.VISIBLE);
			main_layout.startAnimation(openAnim);
		}
	}

	public void removeFilter(){
		
	}
	
	public void hideFilter(){
		main_layout.startAnimation(closeAnim);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animation == closeAnim){
			main_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
