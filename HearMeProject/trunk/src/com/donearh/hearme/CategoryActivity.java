package com.donearh.hearme;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;

import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageFromResource;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class CategoryActivity extends ActionBarActivity  implements AdapterView.OnItemClickListener {
	
	static final int SUB_CAT_ACTIVITY = 1;
	
	private SavedData mSavedData;
	private GridView gridView;
	private CategoryAdapter mCategoryAdapter;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private ArrayList<CategoryData> mCategoryDatas = new ArrayList<CategoryData>();
	
	private ArrayList<CategoryData> mCategoryArray = new ArrayList<CategoryData>();
	private ArrayList<Integer> mOutCatList = new ArrayList<Integer>();
	
	private ImageFromResource mImageFromRes;
	private int mParentID;
	private int mParentImgRes = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		gridView = (GridView)findViewById(R.id.category_grid);
		
		MainDatabaseHandler db = new MainDatabaseHandler(CategoryActivity.this);
		mCategoryDatas = db.getCategoryData();
		db.close();
		db = null;
		
		mCategoryAdapter = new CategoryAdapter(this);
		
		mImageFromRes = new ImageFromResource(CategoryActivity.this);
		
		if(getIntent().getExtras() != null){
			mParentID = getIntent().getExtras().getInt("parent_id", -1);
			mParentImgRes = getIntent().getExtras().getInt("parent_img", -1);
		}
		
		mSavedData = SavedData.getInstance(CategoryActivity.this);
		mOutCatList = mSavedData.getOutCats();
		
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mCategoryAdapter);
		swingBottomInAnimationAdapter.setAbsListView(gridView);
		//swingBottomInAnimationAdapter.setInitialDelayMillis(100);
		
		
		gridView.setOnItemClickListener(this);
        
		if(mParentID == -1)
	        for(int i=0; i<mCategoryDatas.size(); i++)
	        {
	        	if(mCategoryDatas.get(i).ParentId == 0)
	        	{
	        		mCategoryArray.add(mCategoryDatas.get(i));
	        	}
	        }
		else
			for(int i=0; i<mCategoryDatas.size(); i++)
	        {
	        	if(mCategoryDatas.get(i).ParentId == mParentID)
	        	{
	        		mCategoryArray.add(mCategoryDatas.get(i));
	        	}
	        }
			if(mCategoryArray.size() != 0)
			{
				
			}
			
		Collections.sort(mCategoryArray, new CatComparator());
		gridView.setAdapter(swingBottomInAnimationAdapter);
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mCategoryDatas.clear();
		mCategoryDatas = null;
		mCategoryArray.clear();
		mCategoryArray = null;
		mOutCatList.clear();
		mOutCatList = null;
		super.onDestroy();
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
			Collections.sort(mCategoryArray, new CatComparator());
			mCategoryAdapter.notifyDataSetChanged();
			swingBottomInAnimationAdapter.reset();
		}
	}
	
	public void createNeedCatTree(Integer pos, Integer id){
			
		boolean already_exist = false;
		for(Integer out_cat : mOutCatList){
			if(out_cat.equals(id)){
				already_exist = true;
				break;
			}
		}
		if(!already_exist)
			mOutCatList.add(id);
		if(mCategoryDatas.get(pos).hasChild.equals(1)){
			for(int i=0; i<mCategoryDatas.size(); i++){
					if(mCategoryDatas.get(i).ParentId.equals(id))
					{
						createNeedCatTree(i, mCategoryDatas.get(i).Id);
					}
			}
		}
	}
	
	public void removeFromOutCats(Integer pos, Integer id){
		mOutCatList.remove(id);
		if(mCategoryDatas.get(pos).hasChild.equals(1)){
			for(int i=0; i<mCategoryDatas.size(); i++){
					if(mCategoryDatas.get(i).ParentId.equals(id))
					{
						removeFromOutCats(i, mCategoryDatas.get(i).Id);
					}
			}
		}
	}

	private static class ViewHolder
	{
		ImageView imageView;
		CheckBox title;
	}
	
	private class CategoryAdapter extends BaseAdapter
	{
		private Context mContext;
		private boolean mClicked = false;

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
				holder.imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(((Integer)v.getTag(R.string.tag_has_child)).equals(0)){
							new CustomToast(CategoryActivity.this).show(getString(R.string.no_sub_cat));
							return;
						}
						Intent cat_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
						cat_intent.putExtra("parent_id", (Integer)v.getTag(R.string.tag_id));
						if(mParentImgRes == -1)
							cat_intent.putExtra("parent_img", (Integer)v.getTag(R.string.tag_img_res));
						else
							cat_intent.putExtra("parent_img", mParentImgRes);
						startActivityForResult(cat_intent, SUB_CAT_ACTIVITY);
					}
				});
				holder.title = (CheckBox)convertView.findViewById(R.id.category_cb);
				holder.title.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!((CheckBox)v).isChecked()){
							for (int i = 0; i < mCategoryDatas.size(); i++) {
								if(mCategoryDatas.get(i).Id.equals((Integer)v.getTag())){
									createNeedCatTree(i, (Integer)v.getTag());
									break;
								}
							}
							
						}
						else if(mOutCatList.size() != 0){
							for (int i = 0; i < mCategoryDatas.size(); i++) {
								if(mCategoryDatas.get(i).Id.equals((Integer)v.getTag())){
									removeFromOutCats(i, (Integer)v.getTag());
									break;
								}
							}
						}
						
						setResult(RESULT_OK);
						mSavedData.saveOutCats(mOutCatList);
					}
				});
				holder.title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if (!mClicked)
							return;
						mClicked = false;
						
					}
				});
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imageView.setTag(R.string.tag_id, mCategoryArray.get(position).Id);
			holder.imageView.setTag(R.string.tag_img_res, mCategoryArray.get(position).icon_img);
			holder.imageView.setTag(R.string.tag_has_child, mCategoryArray.get(position).hasChild);
			/*if(mParentImgRes == -1)
				mImageFromRes.loadBitmap(mCategoryArray.get(position).icon_img, holder.imageView);
			else
				mImageFromRes.loadBitmap(mParentImgRes, holder.imageView);*/
            
			holder.title.setTag(mCategoryArray.get(position).Id);
            holder.title.setText(mCategoryArray.get(position).Name);
            holder.title.setChecked(true);
            for(Integer out_cat : mOutCatList){
            	if(out_cat.equals(mCategoryArray.get(position).Id)){
            		holder.title.setChecked(false);
            		break;
            	}
            }
            return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			setResult(RESULT_OK);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	
}
