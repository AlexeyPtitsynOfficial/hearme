package com.donearh.imageslider;

import java.util.ArrayList;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.widget.RelativeLayout;

import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.R;
import com.donearh.hearme.datatypes.SliderData;
import com.nineoldandroids.view.ViewHelper;

public class AdapterImageSlider  extends FragmentStatePagerAdapter implements
ViewPager.OnPageChangeListener{

	private FragmentManager fm;
	public static ArrayList<SliderData> mSliderImages;
	
	private boolean swipedLeft=false;
	private int lastPage=9;
	private RelativeLayout cur = null;
	private RelativeLayout next = null;
	private RelativeLayout prev = null;
	private RelativeLayout prevprev = null;
	private RelativeLayout nextnext = null;
	
	private float scale;
	private boolean IsBlured;
	private static float minAlpha=0.6f;
	private static float maxAlpha=1f;
	private static float minDegree=60.0f;
	private int counter=0;
	
	private Context mContext;
	
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
	
	public AdapterImageSlider(Context context, FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		mContext = context;
		mSliderImages = ((MainControlBarActivity)context).mSliderData;
	}
	
	public ArrayList<SliderData> setData(ArrayList<SliderData> data)
	{
		return mSliderImages = data;
	}
 
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		if (position == MainControlBarActivity.FIRST_PAGE)
			scale = MainControlBarActivity.BIG_SCALE;     	
		else
		{
			IsBlured=true;
		}
		Fragment currentFragment =  FragmentImageHolder.newInstance(mContext, position, mSliderImages.get(position).url, IsBlured);
		cur = getRootView(position);
		next = getRootView(position +1);
		prev = getRootView(position -1);
		return currentFragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSliderImages.size();
	}
	
	@Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO Auto-generated method stub
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
				//cur.setScaleBoth(MainControlBarActivity.BIG_SCALE 
				//		- MainControlBarActivity.DIFF_SCALE * positionOffset);

				ViewHelper.setRotationY(cur, 0);
			}

			if(next!=null)
			{
				//next.setScaleBoth(MainControlBarActivity.SMALL_SCALE 
				//		+ MainControlBarActivity.DIFF_SCALE * positionOffset);
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
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private RelativeLayout getRootView(int position){
		
		RelativeLayout layout;
		try{
			layout = (RelativeLayout)fm.findFragmentByTag(this.getFragmentTag(position))
					.getView().findViewById(R.id.slider_root);;
		}
		catch(Exception e){
			return null;
		}
		if(layout != null)
			return layout;
		return null;
	}
	
	private String getFragmentTag(int position)
	{
		String str = "android:switcher:" + ((MainControlBarActivity)mContext).mMainPage.mImageSliderViewPager.getId() + ":" + position;
		return str;
	}

}
