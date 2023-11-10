package com.donearh.hearme;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.datatypes.FavUsersData;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;

public class SpinnerFavUsersAdapter extends ArrayAdapter<Void>{

	private ArrayList<FavUsersData> mFavUsers = new ArrayList<FavUsersData>();
	private int mRes;
	private ImageLoaderRounded mImageLoaderRounded;
	private Context mContext;
	
	public SpinnerFavUsersAdapter(Context context, int textViewResourceId,
			ArrayList<Void> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		mImageLoaderRounded = new ImageLoaderRounded(mContext, ImageLoader.DIR_USER_AVATAR);
		mRes = textViewResourceId;
	}
	
	public void setData(ArrayList<FavUsersData> data){
		mFavUsers = data;
		FavUsersData fav_user = new FavUsersData();
		fav_user.id = -1;
		fav_user.name = "Все";
		mFavUsers.add(0, fav_user);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFavUsers.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mFavUsers.get(position).id;
	}

	private class ViewHolder{
		TextView text;
		ImageView icon;
	}
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(mRes, null);
			
			
			viewHolder.text = (TextView)convertView.findViewById(R.id.text);
			viewHolder.icon = (RecyclingImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.text.setText(mFavUsers.get(position).name);
		mImageLoaderRounded.mImageFetcherRounded.loadImage(mFavUsers.get(position).avatar_url, viewHolder.icon);
		return convertView;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		TextView text;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spinner_area_title, null);
			
			text = (TextView)convertView.findViewById(R.id.text);
			convertView.setTag(text);
		}
		else
		{
			
			text = (TextView)convertView.getTag();
		}
		
		text.setText(mFavUsers.get(position).name);
		return convertView;
	}

}
