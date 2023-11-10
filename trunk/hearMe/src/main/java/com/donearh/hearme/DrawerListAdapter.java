package com.donearh.hearme;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DrawerListAdapter extends ArrayAdapter<Item> 
{
	static final int PROFILE = 1;
	static final int AD_ADD = 2;
	static final int AD_MY = 3;
	static final int AD_ALL = 4;
	static final int CATS_DRAWER = 6;
	static final int AD_CAT_FILTER = 7;
	static final int AD_FAVORITE_USERS = 8;
	static final int AD_FAVORITE = 9;
	static final int AD_EXT_SEARCH = 10;
	static final int SETTINGS = 11;
	static final int EXIT = 12;
	static final int LOWER_BAR = 13;
	static final int SIMPLE_SEARCH = 14;
	static final int TELL_FRIENDS = 15;
	static final int COMMERCIAL = 16;
	static final int REPORT = 17;
	static final int ABOUT = 18;
	
	private LayoutInflater mInflater;
	
	public enum RowType
	{
		PROFILE_ITEM,
        LIST_ITEM,
        HEADER_ITEM,
        LOWERBAR
        
    }

	public DrawerListAdapter(Context context, List<Item> items) {
		super(context, 0, items);
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

}
