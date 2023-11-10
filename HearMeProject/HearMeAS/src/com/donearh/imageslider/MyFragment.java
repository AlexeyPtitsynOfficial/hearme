package com.donearh.imageslider;

import com.nineoldandroids.view.ViewHelper;
import com.donearh.hearme.FullSlideActivity;
import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.R;
import com.donearh.imageloader.ImageFetcher;
import com.donearh.imageloader.ImageFetcherBack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFragment extends Fragment {
	
	private static final String IMAGE_URL = "image_url";
	private String mimageUrl;
	private static Context mContext;
	private static ImageFetcherBack mImageFetcher;
	private ImageView mImageView;

	public static Fragment newInstance(Context context, int pos, 
			float scale,boolean IsBlured, String url)
	{
		
		mContext = context;
		mImageFetcher = ((MainControlBarActivity)mContext).mImageFetcherBack;
		Fragment fragment = new MyFragment();
		Bundle args = new Bundle();
		args.putInt("pos", pos);
		args.putFloat("scale", scale);
		args.putBoolean("IsBlured", IsBlured);
		args.putString(IMAGE_URL, url);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		
		LinearLayout l = (LinearLayout)
				inflater.inflate(R.layout.mf, container, false);
		
		int pos = this.getArguments().getInt("pos");
		mimageUrl = getArguments().getString(IMAGE_URL);

		mImageView = (ImageView) l.findViewById(R.id.slider_image);
		mImageView.setImageResource(R.drawable.slider_glossy);
		mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int t;
				t = 0;
			}
		});
		
		View btn_fill_slide = (View)l.findViewById(R.id.btn_full);
		btn_fill_slide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), FullSlideActivity.class);
				startActivity(intent);
			}
		});
        mImageFetcher.loadImage(mimageUrl, mImageView);
		
        SliderRelativeLayout root = (SliderRelativeLayout) l.findViewById(R.id.root);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		boolean isBlured=this.getArguments().getBoolean("IsBlured");
		if(isBlured)
		{
			ViewHelper.setAlpha(root,MyPagerAdapter.getMinAlpha());
			//ViewHelper.setRotationY(root, MyPagerAdapter.getMinDegree());
		}
		return l;
	}
}
