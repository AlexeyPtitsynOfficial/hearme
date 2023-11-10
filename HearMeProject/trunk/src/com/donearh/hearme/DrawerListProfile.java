package com.donearh.hearme;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.DrawerListAdapter.RowType;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;
import com.google.android.gms.common.api.GoogleApiClient;

public class DrawerListProfile implements Item
{
	private ImageLoaderRounded mImageLoaderRounded;
	private GoogleApiClient mGoogleApiClient;
	private ImageView mImageView;
	private TextView mUserName;
	private HashMap<String, String> mUserData;
	
	public DrawerListProfile(Context context, HashMap<String, String> user_data) {

		mUserData = user_data;
			
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

        mUserName = (TextView) view.findViewById(R.id.user_login);
        updateName(mUserData);
        mImageView = (ImageView) view.findViewById(R.id.avatar);
        mImageLoaderRounded.mImageFetcherRounded.loadImage(mUserData.get(AccountGeneral.KEY_IMAGE_URL), mImageView);

        return view;
	}
	
	public void updateAvatar(String url){
		mImageLoaderRounded.mImageFetcherRounded.loadImage(url, mImageView);
	}
	
	public void updateName(HashMap<String, String> user_data){
		mUserData = user_data;
		mUserName.setText(mUserData.get(AccountGeneral.KEY_DISPLAY_NAME));
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

	@Override
	public int getIconId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
