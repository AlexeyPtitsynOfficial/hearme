package com.donearh.hearme;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.donearh.hearme.datatypes.FavUsersData;

public class SpinnerFavUsersAdapter extends ArrayAdapter<FavUsersData>{

	private ArrayList<FavUsersData> mFavUsers;
	private int mRes;
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
		ImageButton btn_remove_user;
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
			viewHolder.btn_remove_user = (ImageButton)convertView.findViewById(R.id.btn_remove_user);
			viewHolder.btn_remove_user.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//((MainControlBarActivity)mContext).OnCl
				}
			});

			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.text.setText(mFavUsers.get(position).name);
		
		if(mFavUsers.get(position).id == null) {
			viewHolder.icon.setImageResource(R.drawable.icon_many_avatars);
			viewHolder.btn_remove_user.setVisibility(View.GONE);
		}
		else {
			Glide.with(getContext()).load(mFavUsers.get(position).avatar_url)
					.asBitmap()
					.centerCrop()
					.placeholder(R.drawable.icon_no_avatar)
					.into(new BitmapImageViewTarget(viewHolder.icon) {
						@Override
						protected void setResource(Bitmap resource) {
							RoundedBitmapDrawable circularBitmapDrawable =
									RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
							circularBitmapDrawable.setCircular(true);
							view.setImageDrawable(circularBitmapDrawable);
						}
					});
			viewHolder.btn_remove_user.setVisibility(View.VISIBLE);
		}


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
