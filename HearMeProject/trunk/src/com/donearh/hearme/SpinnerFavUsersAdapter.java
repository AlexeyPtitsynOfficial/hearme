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

public class SpinnerFavUsersAdapter extends ArrayAdapter<FavUsersData>{

	private ArrayList<FavUsersData> mFavUsers;
	private int mRes;
	private ImageLoaderRounded mImageLoaderRounded;
	private Context mContext;
	
	public SpinnerFavUsersAdapter(Context context, int textViewResourceId,
			ArrayList<FavUsersData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		if(mFavUsers == null)
			mFavUsers = new ArrayList<FavUsersData>();
		mFavUsers.clear();
		mFavUsers.addAll(objects);
		FavUsersData fav_user = new FavUsersData();
		fav_user.name = mContext.getString(R.string.all);
		mFavUsers.add(0, fav_user);
		
		if(mImageLoaderRounded == null)
			mImageLoaderRounded = new ImageLoaderRounded(mContext, ImageLoader.DIR_USER_AVATAR);
		mRes = textViewResourceId;
	}

	public ArrayList<FavUsersData> getData(){
		return mFavUsers;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFavUsers.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
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
		
		if(mFavUsers.get(position).id == null)
			viewHolder.icon.setImageResource(R.drawable.icon_many_avatars);
		else
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
