package com.donearh.hearme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class WaitFragment extends Fragment{

	private String mCircleText;
	public WaitFragment(String text)
	{
		mCircleText = text;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.circle, container, false);
		
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.circle_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		layout.startAnimation(a1);
		
		View view = (View)rootView.findViewById(R.id.gr_back_circle);
		
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		view.startAnimation(a);
		
		TextView tv = (TextView)rootView.findViewById(R.id.circle_text);
		tv.setText(mCircleText);
	    
		return rootView;
	}
}
