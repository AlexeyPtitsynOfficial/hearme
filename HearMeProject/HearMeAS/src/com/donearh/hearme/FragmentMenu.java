package com.donearh.hearme;

import java.util.ArrayList;

import com.donearh.imageloader.ImageFetcher;
import com.donearh.imageloader.ImageCache.ImageCacheParams;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FragmentMenu extends Fragment
{
	public static final int MENU_ITEM = 0;
	private ListView mMenuList;
	private ArrayList<Item> mItems;
	private DrawerListAdapter mDrawerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		 setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
		
		mMenuList = (ListView)rootView.findViewById(R.id.menu_list);
		mMenuList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerAdapter = new DrawerListAdapter(getActivity(), mItems);
		
		mMenuList.setAdapter(mDrawerAdapter);

		return rootView;
	}
	
	public DrawerListAdapter getAdapter()
	{
		return mDrawerAdapter;
	}
	
	public void setData(ArrayList<Item> items)
	{
		mItems = items;
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			((MainControlBarActivity)getActivity()).mCatIDsList.clear();
			((MainControlBarActivity)getActivity()).selectItem(mItems.get(position).getMenuType(),-1);
		}
		
	}

}
