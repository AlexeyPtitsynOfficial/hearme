package com.donearh.imageslider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.donearh.hearme.FullSlideActivity;
import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.R;
import com.donearh.hearme.Urls;
import com.donearh.hearme.datatypes.SliderData;
import com.donearh.imageloader.ImageFetcherBack;
import com.nineoldandroids.view.ViewHelper;

public class MyFragment extends Fragment {
	
	private static final String IMAGE_URL = "image_url";
	private static final String CONTENT_URL = "content_url";
	private int mPos;
	private int mSliderCount;
	private float mScale;
	private boolean mIsBlured;
	private String mimageUrl;
	private String mContentUrl;
	private static ImageFetcherBack mImageFetcher;
	private ImageView mImageView;

	public static Fragment newInstance(int pos, int max_size,
			float scale,boolean IsBlured, SliderData data)
	{
		Fragment fragment = new MyFragment();
		Bundle args = new Bundle();
		args.putInt("pos", pos);
		args.putFloat("scale", scale);
		args.putBoolean("IsBlured", IsBlured);
		args.putString(IMAGE_URL, data.url);
		args.putString(CONTENT_URL, data.content_url);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		
		if(mImageFetcher == null)
			mImageFetcher = ((MainControlBarActivity)getActivity()).mImageFetcherBack;
		LinearLayout l = (LinearLayout)
				inflater.inflate(R.layout.mf, container, false);
		
		if (savedInstanceState != null) {
	        mPos = savedInstanceState.getInt("pos");
			mSliderCount = savedInstanceState.getInt("slider_count");
			mimageUrl = savedInstanceState.getString(IMAGE_URL);
			mContentUrl = savedInstanceState.getString(CONTENT_URL);
			mScale = savedInstanceState.getFloat("scale");
			mIsBlured=savedInstanceState.getBoolean("IsBlured");
	    }
		else{
			mPos = this.getArguments().getInt("pos");
			mSliderCount = ((MainControlBarActivity)getActivity()).getSliderCount();
			mimageUrl = getArguments().getString(IMAGE_URL);
			mContentUrl = getArguments().getString(CONTENT_URL);
			mScale = this.getArguments().getFloat("scale");
			mIsBlured=this.getArguments().getBoolean("IsBlured");
		}
		RelativeLayout slider_layout = (RelativeLayout)l.findViewById(R.id.slider_layout);
		RelativeLayout corner_layout = (RelativeLayout)l.findViewById(R.id.corner_layout);

		mImageView = (ImageView) l.findViewById(R.id.slider_image);
		mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int t;
				t = 0;
			}
		});
		
		ImageButton btn_fill_slide = (ImageButton)l.findViewById(R.id.btn_full);
		btn_fill_slide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), FullSlideActivity.class);
				intent.putExtra(CONTENT_URL, mContentUrl);
				startActivity(intent);
			}
		});
		if(mPos == 0 || mPos == mSliderCount-1){
			slider_layout.setVisibility(View.GONE);
			corner_layout.setVisibility(View.VISIBLE);
		}
		else{
			if(mImageFetcher !=null){
				mImageFetcher.loadImage(Urls.IMAGE_SLIDER + mimageUrl, mImageView);
				Log.e("hm", Urls.IMAGE_SLIDER + mimageUrl);
			}
			else
				Log.e("hm", "fetcher is null");
		}
		
        SliderRelativeLayout root = (SliderRelativeLayout) l.findViewById(R.id.root);
		
		root.setScaleBoth(mScale);
		
		if(mIsBlured){
			ViewHelper.setAlpha(root,MyPagerAdapter.getMinAlpha());
			//ViewHelper.setRotationY(root, MyPagerAdapter.getMinDegree());
		}
		return l;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mImageFetcher = null;
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    outState.putInt("pos", mPos);
	    outState.putInt("slider_count", mSliderCount);
	    outState.putString(IMAGE_URL, mimageUrl);
	    outState.putString(CONTENT_URL, mContentUrl);
	    outState.putFloat("scale", mScale);
	    outState.putBoolean("IsBlured", mIsBlured);
	    super.onSaveInstanceState(outState);
	}
	
	
	
}
