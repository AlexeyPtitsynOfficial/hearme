package com.donearh.hearme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.DrawerListAdapter.RowType;

public class DrawerListItems implements Item
{
	private Context mContext;
	private final Drawable         mIcon;
	private final int				mIconRes;
	private final Drawable         mGetIcon;
    private final String         mTitle;
    private final LayoutInflater inflater;
    private final int mMenuType;

    public DrawerListItems(Context context, LayoutInflater inflater, Drawable img, int icon_res, String text2, int menutype) 
    {
    	this.mContext = context;
        this.mIcon = img;
        this.mGetIcon = img;
        this.mIconRes = icon_res;
        this.mTitle = text2;
        this.inflater = inflater;
        this.mMenuType = menutype;
    }
    
	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.LIST_ITEM.ordinal();
	}
	
	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return mMenuType;
	}
	
	@Override
	public String getMenuTitle() {
		// TODO Auto-generated method stub
		return mTitle;
	}

	private static class ViewHolder
	{
		ImageView icon;
		TextView title;
	}
	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();	
			convertView = inflater.inflate(R.layout.drawer_list_item, null);
			holder.icon = (ImageView)convertView.findViewById(R.id.image);
			holder.title = (TextView)convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		if(mIcon != null)
			holder.icon.setImageDrawable(mIcon);
		
		holder.title.setText(mTitle);
		holder.title.bringToFront();
		return convertView;
	}

	@Override
	public Drawable getIcon() {
		// TODO Auto-generated method stub
		return mGetIcon;
	}

	@Override
	public int getIconId() {
		// TODO Auto-generated method stub
		return mIconRes;
	}

	

	

}
