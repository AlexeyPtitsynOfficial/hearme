package com.donearh.hearme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.DrawerListAdapter.RowType;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;

public class DrawerListProfile implements Item
{
	private final String name;
	private final String avatar_url;
	private ImageLoaderRounded mImageLoaderRounded;
	private ImageView mImageView;
	
	public DrawerListProfile(Context context, LayoutInflater inflater, String name, String avatar_url) {
        this.name = name;
        this.avatar_url = avatar_url;
        mImageLoaderRounded = new ImageLoaderRounded(context, ImageLoader.DIR_USER_AVATAR); 
    }

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.PROFILE_ITEM.ordinal();
	}
	
	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return DrawerListAdapter.PROFILE;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_list_profile, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.user_login);
        text.setText(name);
        mImageView = (ImageView) view.findViewById(R.id.avatar);
        mImageLoaderRounded.mImageFetcherRounded.loadImage(avatar_url, mImageView);

        return view;
	}
	
	public void updateAvatar(String url){
		mImageLoaderRounded.mImageFetcherRounded.loadImage(url, mImageView);
	}

	@Override
	public String getMenuTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
