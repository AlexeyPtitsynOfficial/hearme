package com.donearh.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.donearh.hearme.AsyncTask;

public class ImageFromRes extends AsyncTask<Void, Void, Drawable>{

	private Context mContext;
	private ImageView mImageView;
	private int mRes;
	
	public ImageFromRes(Context context, ImageView image, int res){
		mContext = context;
		mImageView = image;
		mRes = res;
	}
	@Override
	protected Drawable doInBackground(Void... params) {
		// TODO Auto-generated method stub
		Drawable mImage = null;
	if(mRes != 0)
		mImage = mContext.getResources().getDrawable(mRes);
		return mImage;
	}
	@Override
	protected void onPostExecute(Drawable result) {
		// TODO Auto-generated method stub
		if(result != null)
			mImageView.setImageDrawable(result);
	}
}
