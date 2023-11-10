package com.donearh.hearme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ErrorFragment extends Fragment{

	private String mCircleText;
	public ErrorFragment(String text)
	{
		mCircleText = text;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.circle_error, container, false);
		
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.circle_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		layout.startAnimation(a1);
		
		View view = (View)rootView.findViewById(R.id.gr_back_circle);
		
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		view.startAnimation(a);
		
		TextView tv = (TextView)rootView.findViewById(R.id.circle_text);
		tv.setText(mCircleText);
		
		View circle = (View)rootView.findViewById(R.id.gr_circle);
		circle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().getSupportFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
			}
		});
	    
		return rootView;
	}
}
