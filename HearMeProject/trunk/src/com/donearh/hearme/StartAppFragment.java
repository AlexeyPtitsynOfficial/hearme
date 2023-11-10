package com.donearh.hearme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
	private LinearLayout mUpdateLayout;
	private LinearLayout mInfoLayout;
	private LinearLayout mErrorLayout;
	private TextView mLoadInfo;
	private Button mBtnUpdate;
	private Button mBtnRepeat;
	private Button mBtnInfoExit;
	private Button mBtnExit;
	
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
		
		mUpdateLayout = (LinearLayout)rootView.findViewById(R.id.update_layout);
		mInfoLayout = (LinearLayout)rootView.findViewById(R.id.info_layout);
		mErrorLayout = (LinearLayout)rootView.findViewById(R.id.error_layout);
		
		mErrorText = (TextView)rootView.findViewById(R.id.error_text);
		mLoadInfo = (TextView)rootView.findViewById(R.id.load_info);
		
		mBtnUpdate = (Button)rootView.findViewById(R.id.btn_update_app);
		mBtnInfoExit = (Button)rootView.findViewById(R.id.btn_info_exit);
		mBtnRepeat = (Button)rootView.findViewById(R.id.btn_repeat);
		mBtnExit = (Button)rootView.findViewById(R.id.btn_exit);
		
		mBtnUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String appPackageName = getActivity().getPackageName();
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
				((MainControlBarActivity)getActivity()).finish();
			}
		});
		
		mBtnInfoExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainControlBarActivity)getActivity()).finish();
			}
		});
		
		mBtnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mErrorLayout.setVisibility(View.GONE);
				((MainControlBarActivity)getActivity()).initApp();
				mCircle.startAnimation(animScale);
			}
		});
		
		mBtnExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((MainControlBarActivity)getActivity()).finish();
			}
		});		
		
		return rootView;
	}
	
	public void setLoadInfo(String text){
		mLoadInfo.setText(text);
	}
	
	public void updateApp(){
		mUpdateLayout.setVisibility(View.VISIBLE);
		mCircleHideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_small);
		mCircle.startAnimation(mCircleHideAnim);
	}
	
	public void showInfo(String text){
		mInfoLayout.setVisibility(View.VISIBLE);
		TextView info_text = (TextView)mInfoLayout.findViewById(R.id.info_text);
		info_text.setText(text);
		mCircleHideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_small);
		mCircle.startAnimation(mCircleHideAnim);
	}
	
	public void startError(String title){
		mLoadInfo.setText("");
		mErrorText.setText(title);
		mErrorLayout.setVisibility(View.VISIBLE);
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
		}
		else if(animation == mMoveRightBtnAnim){
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
