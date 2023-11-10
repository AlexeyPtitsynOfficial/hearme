package com.donearh.hearme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class YesNoFragment extends Fragment implements AnimationListener
{
	private Animation animMove;
	private LinearLayout mLinearLayout;
	private String mTitleText = "";
	private String mYesText = "";
	private String mNoText = "";
	
	private TextView mYesTextView;
	private TextView mNoTextView;
	
	private View mBtnYes;
	private View mBtnNo;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_yes_no, container, false);
		
		mLinearLayout = (LinearLayout)rootView.findViewById(R.id.exit_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.move_dialog);
		mLinearLayout.startAnimation(a1);
		
		TextView tv = (TextView)rootView.findViewById(R.id.common_text);
		tv.setText(mTitleText);
		
		mYesTextView = (TextView)rootView.findViewById(R.id.yes_text);
		mNoTextView = (TextView)rootView.findViewById(R.id.no_text);
		
		mYesTextView.setText(mYesText);
		mNoTextView.setText(mNoText);
		
		mBtnYes = (View)rootView.findViewById(R.id.yes_btn);
		mBtnYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainControlBarActivity)getActivity()).finish();
			}
		});
		
		mBtnNo = (View)rootView.findViewById(R.id.no_btn);
		mBtnNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeAnim();
				//
			}
		});
	    
		return rootView;
	}
	
	public void setYesNoText(String title, String yes, String no){
		mTitleText = title;
		mYesText = yes;
		mNoText = no;
	}
	
	public void closeAnim()
	{
		animMove = AnimationUtils.loadAnimation(getActivity(), R.anim.move_dialog_out);
		animMove.setAnimationListener(this);
		mLinearLayout.startAnimation(animMove);
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// Take any action after completing the animation

		// check for zoom in animation
		if (animation == animMove) 
		{
			((MainControlBarActivity)getActivity()).mYesNoFragment = null;
			getActivity().getSupportFragmentManager().beginTransaction().remove(YesNoFragment.this).commit();
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
