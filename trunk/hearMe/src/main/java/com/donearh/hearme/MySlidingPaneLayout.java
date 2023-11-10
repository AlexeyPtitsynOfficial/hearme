package com.donearh.hearme;

import android.content.Context;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MySlidingPaneLayout extends SlidingPaneLayout{
	
	private boolean slidingEnabled = true;
	private int mOffsetY = 0;
	private int mNotSlidableZone = 0;
	
	private OnSlideDetectListener mOnSlideDetectListener;
	
	public interface OnSlideDetectListener{
		
		public boolean onInterceptTouchEvent(MotionEvent ev);
	}
	public MySlidingPaneLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnSlideDetectListener(OnSlideDetectListener listener){
		mOnSlideDetectListener = listener;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		mOnSlideDetectListener.onInterceptTouchEvent(ev);
	    return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if(ev.getPointerCount() == 1){ 
			int Y = (int)ev.getY();
		    if (!slidingEnabled || Y < mNotSlidableZone - mOffsetY) {
		        // Careful here, view might be null
		        getChildAt(1).dispatchTouchEvent(ev);
		        return true;
		    }
		}
		try{
	    return super.onTouchEvent(ev);
		}
		catch(Exception e){
			
			Log.e("test", e.toString());
			return false;
		}
	}
	
	public void setEnable(boolean is_enable){
		slidingEnabled = is_enable;
	}
	
	public boolean getEnable(){
		return slidingEnabled;
	}
	
	public void setNotSlidableZone(int zone){
		mNotSlidableZone = zone;
	}
	
	public void setOffsetY(int offset){
		mOffsetY = offset*-1;
	}

}
