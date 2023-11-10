package com.donearh.hearme;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class CategoryFragment extends Fragment implements AdapterView.OnItemClickListener
{
	private static final String IMAGE_CACHE_DIR = "ad_category";
	
	private MainControlBarActivity mMainControlBarActivity;
	
	private View mRootView;
	GridView gridView;
	
	private CategoryAdapter mCategoryAdapter;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	
	private ArrayList<String> mImgURLArray = new ArrayList<String>();
	
	private ArrayList<CategoryData> mCategoryArray;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mMainControlBarActivity = (MainControlBarActivity)getActivity();
        
        mCategoryArray = new ArrayList<CategoryData>();
        
        for(int i=0; i<mMainControlBarActivity.mCategoryData.size(); i++)
        {
        	if(mMainControlBarActivity.mCategoryData.get(i).parent_id == 0)
        	{
        		mCategoryArray.add(mMainControlBarActivity.mCategoryData.get(i));
        	}
        }
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		super.onCreateView(inflater, container, saveInstanceState);
		mRootView = inflater.inflate(R.layout.fragment_category, container, false);
		
		gridView = (GridView)mRootView.findViewById(R.id.category_grid);
		
		mCategoryAdapter = new CategoryAdapter(getActivity());
		
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mCategoryAdapter);
		swingBottomInAnimationAdapter.setAbsListView(gridView);
		//swingBottomInAnimationAdapter.setInitialDelayMillis(100);
		gridView.setAdapter(swingBottomInAnimationAdapter);
		
		gridView.setOnItemClickListener(this);
		
		return mRootView;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mRootView = null;
	}

	@Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		
		mCategoryArray.clear();
		for(int i=0; i<mMainControlBarActivity.mCategoryData.size(); i++)
        {
        	if(mMainControlBarActivity.mCategoryData.get(i).parent_id == id)
        	{
        		mCategoryArray.add(mMainControlBarActivity.mCategoryData.get(i));
        	}
        }
		if(mCategoryArray.size() != 0)
		{
			mCategoryAdapter.notifyDataSetChanged();
			swingBottomInAnimationAdapter.reset();
		}
		else
		{
	    	/*mMainControlBarActivity.createDownloadFragment(getString(R.string.server_address) + "get_selected_category_ads.php?category_id=" + id,
	    			getString(R.string.load_data), 
					DownloadFragment.LOAD_AD_LIST, 
					mMainControlBarActivity);*/
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
			return mCategoryArray.get(position).id;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null)
			{
				holder = new ViewHolder();
				
				convertView = LayoutInflater.from(mContext).inflate(R.layout.category_adapter, null);
				
				holder.imageView = new RecyclingImageView(mContext);
				holder.imageView = (ImageView)convertView.findViewById(R.id.category_image);
				holder.title = (TextView)convertView.findViewById(R.id.category_cb);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			Glide.with(mContext)
					.load(mImgURLArray.get(position))
					.into(holder.imageView);
            
            holder.title.setText(mCategoryArray.get(position).name);
            return convertView;
		}
		
		
	}
}
