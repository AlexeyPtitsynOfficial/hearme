package com.donearh.hearme;

import java.util.ArrayList;
import java.util.List;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.donearh.hearme.datatypes.LowerBarData;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

public class LowerControlBarSettings extends Activity
{
	private MainDatabaseHandler db;
	private DynamicGridView mGridView;
	private SettingsAdapter mSettingsAdapter;
	private AlphaInAnimationAdapter animAdapter;
	private ArrayList<LowerBarData> mData;
	
	public LowerControlBarSettings()
	{
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lower_bar_settings);
		
		db = new MainDatabaseHandler(getApplicationContext());
		mGridView = (DynamicGridView)findViewById(R.id.draganddrop_gridview);
		
		mData = db.getLowerBarData();
		
		mSettingsAdapter = new SettingsAdapter(this, mData,  4);

		//animAdapter = new AlphaInAnimationAdapter(mSettingsAdapter);
		//animAdapter.setInitialDelayMillis(100);
		//animAdapter.setAbsListView(mGridView);
		mGridView.setAdapter(mSettingsAdapter);
		mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mGridView.startEditMode(position);
				return true;
			}
		});
		
		mGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
			
			@Override
			public void onDragStarted(int position) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDragPositionsChanged(int oldPosition, int newPosition) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mGridView.setOnDropListener(new DynamicGridView.OnDropListener()
	    {
	        @Override
	        public void onActionDrop()
	        {
	        	ArrayList<LowerBarData> array_type = new ArrayList<LowerBarData>();
	        	
	    	    mGridView.stopEditMode();
	    	    for(int i=0; i<mGridView.getCount(); i++){
	    	    	LowerBarData data = new LowerBarData();
	    	    	data.menu_type = ((LowerBarData)mGridView.getItemAtPosition(i)).menu_type;
	    	    	data.icon_res = ((LowerBarData)mGridView.getItemAtPosition(i)).icon_res;
	    	    	array_type.add(data);
	    	    }
	    	    db.clearTableLowerBar();
	    	    db.addLowerBarData(array_type);
	    	    array_type.clear();
	    	    array_type = null;
	        }
	    });
		/*mListView.setOnTouchListener( new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					ArrayList<Integer> array_type = new ArrayList<Integer>();
					mItems.clear();
					for(int i=0; i<mSettingsAdapter.getCount(); i++)
					{
						mItems.add(mSettingsAdapter.getItem(i));
						array_type.add(mSettingsAdapter.getItem(i).getMenuType());
					}
					
					mSavedData.saveLowerBarArrayPos(array_type);
					return true;
				}
				return false;
			}
		});*/
	}
	
	/*public Drawable getIconByMenuType(int type)
	{
		Drawable icon = null;
		switch (type) {
		case 1:
			icon = getResources().getDrawable(R.drawable.icon_ad_add);
			break;
		case 2:
			icon = getResources().getDrawable(R.drawable.icon_ad_my);
			break;
		case 3:
			icon = getResources().getDrawable(R.drawable.icon_ad_my);
			break;
		case 4:
			icon = getResources().getDrawable(R.drawable.icon_cat_filter);
			break;
		case 5:
			icon = getResources().getDrawable(R.drawable.icon_fav_ad);
		case 6:
			icon = getResources().getDrawable(R.drawable.icon_fav_user);
			break;
		case 7:
			icon = getResources().getDrawable(R.drawable.icon_cat_filter);
			break;
		case 8:
			icon = getResources().getDrawable(R.drawable.icon_ext_search);
			break;
		case 9:
			icon = getResources().getDrawable(R.drawable.icon_exit);
			break;
		default:
			break;
		}
		return icon;
	}*/
	
	private class SettingsAdapter extends BaseDynamicGridAdapter
	{
		ViewHolder viewHolder;
		
		public SettingsAdapter(Context context, List<?> items, int columnCount) {
	        super(context, items, columnCount);
	    }        
		//public long getItemId(int position) {
       // 	return getItem(position).hashCode();
		//}



		private class ViewHolder {
        	View icon_back;
			ImageView imageView;
		}
        
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View gridview = convertView;
			if(gridview == null)
			{
				gridview = new View(LowerControlBarSettings.this);
				
				gridview = LayoutInflater.from(getContext()).inflate(R.layout.lower_bar_adapter_settings, null);
				
				viewHolder = new ViewHolder();
				
				viewHolder.icon_back = (View)gridview.findViewById(R.id.icon_back);
				gridview.setTag(viewHolder);
				
				viewHolder.imageView = new RecyclingImageView(LowerControlBarSettings.this);
				viewHolder.imageView = (ImageView)gridview.findViewById(R.id.icon);
				
				
			}
			else 
			{
				viewHolder = (ViewHolder) gridview.getTag();
			}
			if(position < 4){
				if(Build.VERSION.SDK_INT <16)
					viewHolder.icon_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_night_green));
				else
					viewHolder.icon_back.setBackground(getResources().getDrawable(R.drawable.circle_night_green));
			}
			
			viewHolder.imageView.setImageResource(((LowerBarData)getItem(position)).icon_res);

            return gridview;
		}
	
	}
}
