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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartAppFragment extends Fragment implements AnimationListener{

	RelativeLayout mMainLayout;
	
	private Animation animScale;
	private View mCircle;
	private Animation mCloseAnim;
	private Animation mCircleHideAnim;
	private Animation mMoveLeftBtnAnim;
	private Animation mMoveRightBtnAnim;
	
	private TextView mErrorText;
	private LinearLayout mErrorLayout;
	private View mBtnRepeat;
	private View mBtnExit;
	
	private TextView mRepeatTextView;
	private TextView mExitTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.start_app_fragment, container, false);
		
		mMainLayout = (RelativeLayout)rootView.findViewById(R.id.main_layout);
		
		//Anim
		animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim2);
		animScale.setAnimationListener(this);
		mCircle = (View)rootView.findViewById(R.id.load_circle);
		mCircle.startAnimation(animScale);
		
		mErrorLayout = (LinearLayout)rootView.findViewById(R.id.error_layout);
		mErrorLayout.setVisibility(View.GONE);
		
		mErrorText = (TextView)rootView.findViewById(R.id.error_text);
		
		mBtnRepeat = (View)rootView.findViewById(R.id.btn_repeat);
		mBtnExit = (View)rootView.findViewById(R.id.btn_exit);
		
		mBtnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mErrorLayout.setVisibility(View.GONE);
				((MainControlBarActivity)getActivity()).initApp();
				mCircle.startAnimation(animScale);
			}
		});
		
		mBtnExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainControlBarActivity)getActivity()).finish();
			}
		});
		
		mRepeatTextView = (TextView)rootView.findViewById(R.id.repeat_text);
		mExitTextView = (TextView)rootView.findViewById(R.id.exit_text);
		
		
		return rootView;
	}
	
	public void startError(String title){
		mErrorText.setText(title);
		mErrorLayout.setVisibility(View.VISIBLE);
		mRepeatTextView.setVisibility(View.GONE);
		mExitTextView.setVisibility(View.GONE);
		mCircleHideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_small);
		mCircle.startAnimation(mCircleHideAnim);
		
		mMoveLeftBtnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.move_left_btn);
		mMoveLeftBtnAnim.setAnimationListener(this);
		mBtnRepeat.startAnimation(mMoveLeftBtnAnim);
		mMoveRightBtnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.move_right_btn);
		mMoveRightBtnAnim.setAnimationListener(this);
		mBtnExit.startAnimation(mMoveRightBtnAnim);
	}
	
	public void startClose()
	{
		mCloseAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
		mCloseAnim.setAnimationListener(this);
		mMainLayout.startAnimation(mCloseAnim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == mCloseAnim) 
		{
			getActivity().getSupportFragmentManager().beginTransaction().remove(StartAppFragment.this).commit();
		}
		else if(animation == mMoveLeftBtnAnim){
			mRepeatTextView.setVisibility(View.VISIBLE);
		}
		else if(animation == mMoveRightBtnAnim){
			mExitTextView.setVisibility(View.VISIBLE);
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
