package com.donearh.hearme;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

public class LowerControlBarSettings extends Fragment
{
	private Context mContext;
	private DynamicListView mListView;
	private SettingsAdapter mSettingsAdapter;
	private AlphaInAnimationAdapter animAdapter;
	private ArrayList<Item> mItems;
	
	public LowerControlBarSettings()
	{
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_lower_bar_settings, container, false);
		
		mContext = getActivity();
		mListView = (DynamicListView)rootView.findViewById(R.id.draganddrop_listview);
		mListView.setDivider(null);
		
		mItems = new ArrayList<Item>();
		
		mItems = ((MainControlBarActivity)getActivity()).mLowerBarItems;
		
		mSettingsAdapter = new SettingsAdapter(mItems);

		animAdapter = new AlphaInAnimationAdapter(mSettingsAdapter);
		//animAdapter.setInitialDelayMillis(100);
		animAdapter.setAbsListView(mListView);
		mListView.setAdapter(animAdapter);
		mListView.setOnTouchListener( new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					ArrayList<Integer> array_type = new ArrayList<Integer>();
					((MainControlBarActivity)getActivity()).mLowerBarItems.clear();
					for(int i=0; i<mSettingsAdapter.getCount(); i++)
					{
						((MainControlBarActivity)getActivity()).mLowerBarItems.add(mSettingsAdapter.getItem(i));
						array_type.add(mSettingsAdapter.getItem(i).getMenuType());
					}
					
					((MainControlBarActivity)getActivity()).mSavedData.saveLowerBarArrayPos(array_type);
					return true;
				}
				return false;
			}
		});
		
		return rootView;
	}
	
	private class SettingsAdapter extends ArrayAdapter<Item> 
	{
		ViewHolder viewHolder;
		public SettingsAdapter(ArrayList<Item> items) {
			super(items);
		}
        
        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }
        
        
        @Override
		public long getItemId(int position) {
        	return getItem(position).hashCode();
		}
        
        @Override
		public boolean hasStableIds() {
			return true;
		}
        
        private class ViewHolder {
        	LinearLayout backlayout;
			TextView textView;
			ImageView imageView;
		}
        
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View listView = convertView;
			if(listView == null)
			{
				listView = new View(mContext);
				
				listView = LayoutInflater.from(mContext).inflate(R.layout.lower_bar_adapter_settings, null);
				
				viewHolder = new ViewHolder();
				
				viewHolder.backlayout = (LinearLayout)listView.findViewById(R.id.back_layout);
				viewHolder.textView = (TextView)listView.findViewById(R.id.title);
				listView.setTag(viewHolder);
				
				viewHolder.imageView = new RecyclingImageView(mContext);
				viewHolder.imageView = (ImageView)listView.findViewById(R.id.icon);
				
				
			}
			else
			{
				viewHolder = (ViewHolder) listView.getTag();
			}
			if(position < 5)
				viewHolder.backlayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
			
			Drawable img = ((MainControlBarActivity)getActivity()).getIconByMenuType(getItem(position).getMenuType());
			viewHolder.imageView.setImageDrawable(img);
            viewHolder.textView.setText(getItem(position).getMenuTitle());

            return listView;
		}
	
	}
}
