package com.donearh.imageslider;

import com.bumptech.glide.Glide;
import com.donearh.hearme.R;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class FragmentImageHolder extends Fragment
{
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String IMAGE_URL = "image_url";

	private ImageView mImageView;
	private BitmapDrawable mImage;
	private String mimageUrl;
	
	public static FragmentImageHolder newInstance(Context context, int sectionNumber, String url, boolean IsBlured)
	{
		FragmentImageHolder fragment = new FragmentImageHolder();
		Bundle args = new Bundle();
		args.putString(IMAGE_URL, url);
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putBoolean("IsBlured", IsBlured);
		fragment.setArguments(args);
		return fragment;
	}
	
	public FragmentImageHolder() 
	{
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_image_slider,
				container, false);
		
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.slider_root);
		mimageUrl = getArguments().getString(IMAGE_URL);
		mImageView = (ImageView)rootView.findViewById(R.id.slider_glossy);
		//imageView.setImageResource(R.drawable.slider_glossy);
		//imageView.setBackgroundResource(R.drawable.slider_image);
		Glide.with(getContext()).load(mimageUrl).into(mImageView);

		boolean isBlured=this.getArguments().getBoolean("IsBlured");
		if(isBlured)
		{
			ViewHelper.setAlpha(layout, AdapterImageSlider.getMinAlpha());
			ViewHelper.setRotationY(layout, AdapterImageSlider.getMinDegree());
		}
		
		return rootView;
	}
	
}