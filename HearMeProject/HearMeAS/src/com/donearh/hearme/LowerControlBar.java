package com.donearh.hearme;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LowerControlBar extends Fragment implements AnimationListener
{

	private Animation animMove;
	private LinearLayout mLinearLayout;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_lower_bar, container, false);
		
		mLinearLayout = (LinearLayout)rootView.findViewById(R.id.bar_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.move_dialog);
		mLinearLayout.startAnimation(a1);
		
		LinearLayout btn1 = (LinearLayout)rootView.findViewById(R.id.bar_btn_1);
		LinearLayout btn2 = (LinearLayout)rootView.findViewById(R.id.bar_btn_2);
		LinearLayout btn3 = (LinearLayout)rootView.findViewById(R.id.bar_btn_3);
		LinearLayout btn4 = (LinearLayout)rootView.findViewById(R.id.bar_btn_4);
		
		ImageView icon1 = (ImageView)rootView.findViewById(R.id.bar_img_1);
		ImageView icon2 = (ImageView)rootView.findViewById(R.id.bar_img_2);
		ImageView icon3 = (ImageView)rootView.findViewById(R.id.bar_img_3);
		ImageView icon4 = (ImageView)rootView.findViewById(R.id.bar_img_4);
		
		for(int i=0; i<((MainControlBarActivity)getActivity()).mLowerBarItems.size(); i++)
		{
			switch (i) {
			case 0:
				Drawable img1 = ((MainControlBarActivity)getActivity()).getIconByMenuType(((MainControlBarActivity)getActivity()).mLowerBarItems.get(0).getMenuType());
				icon1.setImageDrawable(img1);
				btn1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MainControlBarActivity)getActivity()).selectItem(((MainControlBarActivity)getActivity()).mLowerBarItems.get(0).getMenuType(),-1);
					}
				});
				break;
			case 1:
				Drawable img2 = ((MainControlBarActivity)getActivity()).getIconByMenuType(((MainControlBarActivity)getActivity()).mLowerBarItems.get(1).getMenuType());
				icon2.setImageDrawable(img2);
				btn2.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MainControlBarActivity)getActivity()).selectItem(((MainControlBarActivity)getActivity()).mLowerBarItems.get(1).getMenuType(), -1);
					}
				});
				break;
			case 2:
				Drawable img3 = ((MainControlBarActivity)getActivity()).getIconByMenuType(((MainControlBarActivity)getActivity()).mLowerBarItems.get(2).getMenuType());
				icon3.setImageDrawable(img3);
				btn3.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MainControlBarActivity)getActivity()).selectItem(((MainControlBarActivity)getActivity()).mLowerBarItems.get(2).getMenuType(), -1);
					}
				});
				break;
			case 3:
				Drawable img4 = ((MainControlBarActivity)getActivity()).getIconByMenuType(((MainControlBarActivity)getActivity()).mLowerBarItems.get(3).getMenuType());
				icon4.setImageDrawable(img4);
				btn4.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MainControlBarActivity)getActivity()).selectItem(((MainControlBarActivity)getActivity()).mLowerBarItems.get(3).getMenuType(),-1);
					}
				});
				break;
			default:
				break;
			}
			
		}
	    
		return rootView;
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
			((MainControlBarActivity)getActivity()).mLowerControlBar = null;
			((MainControlBarActivity)getActivity()).mLowerBarFrame.setVisibility(View.GONE);
			getActivity().getSupportFragmentManager().beginTransaction().remove(LowerControlBar.this).commit();
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
