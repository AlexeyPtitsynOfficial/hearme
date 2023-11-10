package com.donearh.hearme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.DrawerListAdapter.RowType;

public class DrawerListLowerBar implements Item
{
	private final Context  mContext;
	private final Drawable  mIcon;
	private final String name;
	private final int mMenuType;
	
	public DrawerListLowerBar(Context context, LayoutInflater inflater, Drawable img, String name, int menutype) {
		
		this.mContext = context;
        this.name = name;
        this.mMenuType = menutype;
        this.mIcon = img;
    }

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.LOWERBAR.ordinal();
	}
	
	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return mMenuType;
	}
	
	private class ViewHolder
	{
		ImageView icon;
		TextView text;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		ViewHolder holder;
        if (convertView == null) {
        	holder = new ViewHolder();
        	convertView = (View) inflater.inflate(R.layout.drawer_list_lower_bar, null);
        	
        	holder.icon = (ImageView)convertView.findViewById(R.id.image);
        	holder.text = (TextView) convertView.findViewById(R.id.lower_bar_text);
            // Do some initialization
        	
        	convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
		if(mIcon != null)
			holder.icon.setImageDrawable(mIcon);
			holder.icon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainControlBarActivity)mContext).createLowerBarSettingsFragment();
			}
		});
			holder.text.setText(name);

        return convertView;
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
