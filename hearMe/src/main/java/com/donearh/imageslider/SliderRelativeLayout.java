package com.donearh.imageslider;

import com.donearh.hearme.MainControlBarActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SliderRelativeLayout extends RelativeLayout {
	private float scale = MainControlBarActivity.BIG_SCALE;

	public SliderRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SliderRelativeLayout(Context context) {
		super(context);
	}

	public void setScaleBoth(float scale)
	{
		this.scale = scale;
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// The main mechanism to display scale animation, you can customize it
		// as your needs
		int w = this.getWidth();
		int h = this.getHeight();
		canvas.scale(scale, scale, w/2, h/2);
		
		super.onDraw(canvas);
	}
}
