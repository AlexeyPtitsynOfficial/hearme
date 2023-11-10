package com.donearh.hearme;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageFromResource;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class CategoryActivity extends ActionBarActivity  implements AdapterView.OnItemClickListener {
	
	private GridView gridView;
	private CategoryAdapter mCategoryAdapter;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private ArrayList<CategoryData> mCategoryDatas = new ArrayList<CategoryData>();
	
	private ArrayList<CategoryData> mCategoryArray = new ArrayList<CategoryData>();
	
	private ImageFromResource mImageFromRes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		gridView = (GridView)findViewById(R.id.category_grid);
		
		MainDatabaseHandler db = new MainDatabaseHandler(CategoryActivity.this);
		mCategoryDatas = db.getCategoryData();
		
		mCategoryAdapter = new CategoryAdapter(this);
		
		mImageFromRes = new ImageFromResource(CategoryActivity.this);
		
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mCategoryAdapter);
		swingBottomInAnimationAdapter.setAbsListView(gridView);
		//swingBottomInAnimationAdapter.setInitialDelayMillis(100);
		gridView.setAdapter(swingBottomInAnimationAdapter);
		
		gridView.setOnItemClickListener(this);
        
        for(int i=0; i<mCategoryDatas.size(); i++)
        {
        	if(mCategoryDatas.get(i).ParentId == 0)
        	{
        		mCategoryArray.add(mCategoryDatas.get(i));
        	}
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mCategoryArray.clear();
		for(int i=0; i<mCategoryDatas.size(); i++)
        {
        	if(mCategoryDatas.get(i).ParentId == id)
        	{
        		mCategoryArray.add(mCategoryDatas.get(i));
        	}
        }
		if(mCategoryArray.size() != 0)
		{
			mCategoryAdapter.notifyDataSetChanged();
			swingBottomInAnimationAdapter.reset();
		}
	}

	private static class ViewHolder
	{
		ImageView imageView;
		TextView title;
	}
	
	private class CategoryAdapter extends BaseAdapter
	{
		private Context mContext;

		public CategoryAdapter(Context context)
		{
			mContext = context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCategoryArray.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return mCategoryArray.get(position).Id;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				
				convertView = LayoutInflater.from(mContext).inflate(R.layout.category_adapter, null);
				
				holder.imageView = (RecyclingImageView)convertView.findViewById(R.id.category_image);
				holder.title = (CheckBox)convertView.findViewById(R.id.category_text);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
            mImageFromRes.loadBitmap(mCategoryArray.get(position).icon_img, holder.imageView);
            
            holder.title.setText(mCategoryArray.get(position).Name);
            return convertView;
		}
		
		
	}

	
}
