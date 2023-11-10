package com.donearh.hearme;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.donearh.hearme.DrawerListAdapter.RowType;
import com.donearh.hearme.account.AccountGeneral;
import com.google.android.gms.common.api.GoogleApiClient;

public class DrawerListProfile implements Item
{
	private GoogleApiClient mGoogleApiClient;
	private ImageView mImageView;
	private TextView mUserName;
	private HashMap<String, String> mUserData;
	private Context mContext;
	
	public DrawerListProfile(Context context, HashMap<String, String> user_data) {
		mContext = context;
		mUserData = user_data;
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

		updateAvatar(mUserData.get(AccountGeneral.KEY_IMAGE_URL));

        return view;
	}
	
	public void updateAvatar(String url){
		Glide.with(mContext)
				.load(url)
				.asBitmap()
				.placeholder(R.drawable.icon_no_avatar)
				.into(new BitmapImageViewTarget(mImageView){
					@Override
					protected void setResource(Bitmap resource) {
						RoundedBitmapDrawable circularBitmapDrawable =
								RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
						circularBitmapDrawable.setCircular(true);
						view.setImageDrawable(circularBitmapDrawable);
					}
				});
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
