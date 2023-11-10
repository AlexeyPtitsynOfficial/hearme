package com.donearh.imageslider;


import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.R;
import com.donearh.hearme.datatypes.SliderData;
import com.nineoldandroids.view.ViewHelper;

public class MyPagerAdapter extends FragmentPagerAdapter implements
ViewPager.OnPageChangeListener {

	private static ArrayList<SliderData> mSliderData;
	
	private boolean swipedLeft=false;
	private int lastPage=9;
	private SliderRelativeLayout cur = null;
	private SliderRelativeLayout next = null;
	private SliderRelativeLayout prev = null;
	private SliderRelativeLayout prevprev = null;
	private SliderRelativeLayout nextnext = null;
	private Context context;
	private FragmentManager fm;
	private float scale;
	private boolean IsBlured;
	private static float minAlpha=0.6f;
	private static float maxAlpha=1f;
	private static float minDegree=60.0f;
	private int counter=0;

	public static float getMinDegree()
	{
		return minDegree;
	}
	public static float getMinAlpha()
	{
		return minAlpha;
	}
	public static float getMaxAlpha()
	{
		return maxAlpha;
	}
	
	public MyPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.fm = fm;
		this.context = context;
		mSliderData = ((MainControlBarActivity)context).mSliderData;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}
	@Override
	public Fragment getItem(int position) 
	{



		// make the first pager bigger than others
		if (position == MainControlBarActivity.FIRST_PAGE)
			scale = MainControlBarActivity.BIG_SCALE;     	
		else
		{
			scale = MainControlBarActivity.SMALL_SCALE;
			IsBlured=true;

		}

		Log.d("position", String.valueOf(position));
		Fragment curFragment= MyFragment.newInstance(position, mSliderData.size(), scale,IsBlured, mSliderData.get(position));
		cur = getRootView(position);
		next = getRootView(position +1);
		prev = getRootView(position -1);

		
		
		return curFragment;
	}

	@Override
	public int getCount()
	{		
		return mSliderData.size();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) 
	{	
		if (positionOffset >= 0f && positionOffset <= 1f)
		{
			positionOffset=positionOffset*positionOffset;
			cur = getRootView(position);
			next = getRootView(position +1);
			prev = getRootView(position -1);
			nextnext=getRootView(position +2);

			if(cur!=null)
				ViewHelper.setAlpha(cur, maxAlpha-0.5f*positionOffset);
			if(next!=null)
				ViewHelper.setAlpha(next, minAlpha+0.5f*positionOffset);
			if(prev!=null)
				ViewHelper.setAlpha(prev, minAlpha+0.5f*positionOffset);
			
			
			if(nextnext!=null)
			{	
				ViewHelper.setAlpha(nextnext, minAlpha);
				ViewHelper.setRotationY(nextnext, -minDegree);
			}
			if(cur!=null)
			{
				cur.setScaleBoth(MainControlBarActivity.BIG_SCALE 
						- MainControlBarActivity.DIFF_SCALE * positionOffset);

				ViewHelper.setRotationY(cur, 0);
			}

			if(next!=null)
			{
				next.setScaleBoth(MainControlBarActivity.SMALL_SCALE 
						+ MainControlBarActivity.DIFF_SCALE * positionOffset);
				ViewHelper.setRotationY(next, -minDegree);
			}
			if(prev!=null)
			{
				ViewHelper.setRotationY(prev, minDegree);
			}
			if(prevprev!=null)
			{	
				ViewHelper.setAlpha(prevprev, minAlpha);
				ViewHelper.setRotationY(prevprev, minDegree);
			}

			
			/*To animate it properly we must understand swipe direction
			 * this code adjusts the rotation according to direction.
			 */
			if(swipedLeft)
			{
				if(next!=null)
					ViewHelper.setRotationY(next, -minDegree+minDegree*positionOffset);
				if(cur!=null)
					ViewHelper.setRotationY(cur, 0+minDegree*positionOffset);
			}
			else 
			{
				if(next!=null)
					ViewHelper.setRotationY(next, -minDegree+minDegree*positionOffset);
				if(cur!=null)
				{
					ViewHelper.setRotationY(cur, 0+minDegree*positionOffset);
				}
			}
		}
		if(positionOffset>=1f)
		{
			if(cur!=null)
				ViewHelper.setAlpha(cur, maxAlpha);
		}
	}

	@Override
	public void onPageSelected(int position) {

/*
 * to get finger swipe direction
 */
		if(lastPage<=position)
		{
			swipedLeft=true;
		}
		else if(lastPage>position)
		{
			swipedLeft=false;
		}
		lastPage=position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}



	private SliderRelativeLayout getRootView(int position)
	{
		SliderRelativeLayout ly;
		try {
			ly = (SliderRelativeLayout)fm.findFragmentByTag(this.getFragmentTag(position))
					.getView().findViewById(R.id.root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		if(ly!=null)
			return ly;
		return null;
	}

	private String getFragmentTag(int position)
	{
		String str = "android:switcher:" + ((MainControlBarActivity)context).mMainPage.mImageSliderViewPager.getId() + ":" + position;
		return str;
	}
}
