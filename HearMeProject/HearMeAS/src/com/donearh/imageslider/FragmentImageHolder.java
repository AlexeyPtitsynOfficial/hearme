package com.donearh.imageslider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.R;
import com.donearh.hearme.R.id;
import com.donearh.hearme.R.layout;
import com.donearh.imageloader.ImageFetcherBack;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class FragmentImageHolder extends Fragment
{
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String IMAGE_URL = "image_url";
	
	private static ImageFetcherBack mImageFetcher;
	private ImageView mImageView;
	private BitmapDrawable mImage;
	private String mimageUrl;
	
	public static FragmentImageHolder newInstance(Context context, int sectionNumber, String url, boolean IsBlured)
	{
		if(mImageFetcher == null)
			mImageFetcher = ((MainControlBarActivity)context).mImageFetcherBack;
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
		mImageFetcher.loadImage(mimageUrl, mImageView);
		boolean isBlured=this.getArguments().getBoolean("IsBlured");
		if(isBlured)
		{
			ViewHelper.setAlpha(layout, AdapterImageSlider.getMinAlpha());
			ViewHelper.setRotationY(layout, AdapterImageSlider.getMinDegree());
		}
		
		return rootView;
	}
	
}