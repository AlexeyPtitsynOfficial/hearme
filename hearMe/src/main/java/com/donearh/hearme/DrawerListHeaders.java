package com.donearh.hearme;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.donearh.hearme.DrawerListAdapter.RowType;

public class DrawerListHeaders implements Item
{
	private final String name;
	
	public DrawerListHeaders(LayoutInflater inflater, String name) {
        this.name = name;
    }

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.HEADER_ITEM.ordinal();
	}
	
	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_list_header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.separator);
        text.setText(name);

        return view;
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
