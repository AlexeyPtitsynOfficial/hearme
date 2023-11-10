package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.donearh.hearme.InsertDataFragment.InsertCompleteListener;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageUploader;
import com.donearh.imageloader.ImageUploader.DecodeCompleteListener;
import com.donearh.imageloader.ImageUploader.UploadCompleteListener;

public class AdAddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
																InsertCompleteListener,
																DecodeCompleteListener,
																UploadCompleteListener
{

	private SavedData mSavedData;
	
	private ScrollView mScrollView;
	
	private ImageUploader mImageUploader;
	
	private LinearLayout mLayout;
	private String mUserId;
	private String mState;
	private Integer mSelectedAreaID = -1;
	private Integer mSelectedCategoryID = -1;
	private String mEncodeTitle;
	private String mEncodeDesc;
	private Integer mAdState = 4;
	private Integer mSpinnerId = 0;
	
	private Integer mSpinnerPos = 7;
	
	private ArrayList<AreaData> mAreaData;
	private ArrayList<UploadImage> mImagesList;
	
	private CustomImageScroller mImageScroller;
	private ImagesAdapter mImagesAdapter;
	
	private InsertDataFragment mInsertFragment;
	private Button mUploadImageBtn;
	private ArrayList<CategoryData> mCategoryData;
	private ArrayList<ArrayList<CategoryData>> mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
	
	private String upLoadServerUri = null;
	
	private RadioButton mRadioPay;
	
	private class UploadImage
	{
		Bitmap image;
		boolean load_state;
		String url;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_add);
		
		mRadioPay = (RadioButton)findViewById(R.id.pay);
		
		mSavedData = new SavedData(AdAddActivity.this);
		
		mImageUploader = new ImageUploader(AdAddActivity.this, 
											Urls.API + Urls.ADS,
											"upload_file");
		mImageUploader.setDecodeListener(this);
		mAreaData = new ArrayList<AreaData>(); 
		mImagesList = new ArrayList<UploadImage>();
		
		mImageUploader.setDecodeListener(this);
		mImageUploader.setUploadListener(this);
		
		upLoadServerUri = Urls.API + Urls.ADS;
		//Get area data
		MainDatabaseHandler db = new MainDatabaseHandler(AdAddActivity.this);
		mAreaData = db.getAreaData();
		mCategoryData = db.getCategoryData();
		
		mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
		ArrayList<CategoryData> tCategoryData = new ArrayList<CategoryData>();
		
		mScrollView = (ScrollView)findViewById(R.id.scroller);
		
		DatabaseHandler user_db = new DatabaseHandler(AdAddActivity.this);
		mUserId = user_db.getUserDetails().get(LoginKeys.KEY_ID);
		if(mUserId != null)
			mAdState = 3;
		mImageUploader.setUserId(mUserId);
		mLayout = (LinearLayout)findViewById(R.id.layout);
		final EditText tTitle = (EditText)findViewById(R.id.edit_title);
		tTitle.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(tTitle.getText().length()>0)
				{
					tTitle.setError(null);
				}
			}
		});
		ArrayList<String> tAreaName = new ArrayList<String>();
		tAreaName.add("Выбрать");
		Integer selectedAreaId = mSavedData.getAreaId();
		int selectedAreaPos = -1;
		for(int i=0; i<mAreaData.size(); i++)
		{
			if(selectedAreaId.equals(mAreaData.get(i).id))
				selectedAreaPos = i;
			
			tAreaName.add(mAreaData.get(i).name);
		}
		
		final Spinner tAreaSpinner = (Spinner)findViewById(R.id.spinner_area);
		SpinnerArrayAdapter areaAdapter = new SpinnerArrayAdapter(AdAddActivity.this, 
				R.layout.adapter_spinner, 
				tAreaName);
		tAreaSpinner.setAdapter(areaAdapter);
		if(selectedAreaPos != -1)
		{
			tAreaSpinner.setSelection(selectedAreaPos+1);
		}
		else
		{
			tAreaSpinner.setSelection(0);
		}
		tAreaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position != 0)
				{
					mSavedData.saveSelectedArea(mAreaData.get(position-1).id);
					mSelectedAreaID = mAreaData.get(position-1).id;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		final Spinner tCategorySpinner = (Spinner)findViewById(R.id.spinner1);
		
		ArrayList<String> tCategoryName = new ArrayList<String>();
		tCategoryName.add("Выбрать");
		
		for(int i=0; i<mCategoryData.size(); i++)
		{
			if(mCategoryData.get(i).ParentId == 0)
			{
				tCategoryData.add(mCategoryData.get(i));
				tCategoryName.add(mCategoryData.get(i).Name);
			}
		}
		mCategoryTypeList.add(tCategoryData);
		SpinnerArrayAdapter categoriesAdapter = new SpinnerArrayAdapter(AdAddActivity.this, 
				R.layout.adapter_spinner, 
				tCategoryName);
		tCategorySpinner.setAdapter(categoriesAdapter);
		tCategorySpinner.setSelection(0);
		tCategorySpinner.setOnItemSelectedListener(this);
		tCategorySpinner.setId(mSpinnerId);

		final EditText tDesc = (EditText)findViewById(R.id.edit_ad_desc);
		tDesc.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(tDesc.getText().length() >0)
				{
					tDesc.setError(null);
				}
			}
		});
		mUploadImageBtn = (Button)findViewById(R.id.image_add_btn);
		mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		
		Button tAdAddBtn = (Button)findViewById(R.id.confirm_add);
		tAdAddBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ad_title = tTitle.getText().toString();
				String ad_desc = tDesc.getText().toString();
				
				if(!ad_title.isEmpty()
						&& !ad_desc.isEmpty()
						&& mSelectedCategoryID != -1
						)
				{
					try {
						mEncodeTitle = ad_title;
						mEncodeDesc = ad_desc;
						mEncodeTitle = URLEncoder.encode(ad_title,"UTF-8");
						mEncodeDesc = URLEncoder.encode(ad_desc,"UTF-8");
			     		
					   } catch (UnsupportedEncodingException e) {
					       e.printStackTrace();
					   } 
					
					if(mRadioPay.isChecked())
						mAdState = 2;
					Integer t = mSelectedCategoryID;
					
					URLWithParams urlWithParams = new URLWithParams();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("tag", AdTags.insert_new_ad));
					params.add(new BasicNameValuePair("user_id", mUserId));
					params.add(new BasicNameValuePair("ad_state", mState));
					params.add(new BasicNameValuePair("ad_title", mEncodeTitle));
					params.add(new BasicNameValuePair("ad_area", mSelectedAreaID.toString()));
					params.add(new BasicNameValuePair("ad_category", mSelectedCategoryID.toString()));
					params.add(new BasicNameValuePair("ad_desc", mEncodeDesc));
					params.add(new BasicNameValuePair("ad_state", mAdState.toString()));
					for(int i=0; i<mImagesList.size(); i++)
					{
						params.add(new BasicNameValuePair("images_path[]", Urls.SERVER_URL + "upload_files/" + mImagesList.get(i).url));
					}
					
					urlWithParams.url = Urls.API + Urls.ADS;
					urlWithParams.nameValuePairs = params;
					
					mInsertFragment = new InsertDataFragment();
					mInsertFragment.setListener(AdAddActivity.this);
					mInsertFragment.startInsert(urlWithParams, 
							InsertDataFragment.AD_ADD, 
							getString(R.string.notif_ad_add));
					FragmentManager fragmentManager = getSupportFragmentManager();
					fragmentManager.beginTransaction().replace(android.R.id.content, mInsertFragment).commit();
					
					mImagesList.clear();
				}
				else
				{
					if(ad_desc.isEmpty())
					{
						tDesc.setError(getString(R.string.ad_error_desc));
						tDesc.requestFocus();
					}
					//if(mSelectedCategoryID == -1)
					if(ad_title.isEmpty())
					{
						tTitle.setError(getString(R.string.ad_error_title));
						tTitle.requestFocus();
					}
					if(mSelectedCategoryID == -1)
					{
						//tCategorySpinner.
					}
				}
				
			}
		});
		
		mImageScroller = (CustomImageScroller)findViewById(R.id.image_scroller);
		mImagesAdapter = new ImagesAdapter();
		mImageScroller.setAdapter(AdAddActivity.this, mImagesAdapter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		for(int i=0; i < mImagesList.size(); i++)
		{
			URLWithParams urlWithParams = new URLWithParams();
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("tag", AdTags.delete_file));
	        params.add(new BasicNameValuePair("delete_file_url", mImagesList.get(i).url));
	        
	        urlWithParams.url = upLoadServerUri;
			urlWithParams.nameValuePairs = params;
			
	        new InsertData().execute(urlWithParams);
		}
		mImagesList.clear();
		mImagesList = null;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.ad_add, menu);
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
	
	private void selectImage() {
		final CharSequence[] items = { "Cфотографировать", "Из галереи",
				"Отмена" };

		AlertDialog.Builder builder = new AlertDialog.Builder(AdAddActivity.this);
		builder.setTitle("Выберите источник");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Cфотографировать")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//File f = new File(android.os.Environment
					//		.getExternalStorageDirectory(), "temp.jpg");
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					intent.putExtra("return-data", true);
					startActivityForResult(intent, ImageUploader.REQUEST_CAMERA);
				} else if (items[item].equals("Из галереи")) {
					Intent intent = new Intent();
		            intent.setType("image/*");
		            intent.putExtra("return-data", true);
		            intent.setAction(Intent.ACTION_GET_CONTENT);
		            
		            startActivityForResult(
							Intent.createChooser(intent, "Выберите файл"),
							ImageUploader.SELECT_FROM_GALLERY);
				} else if (items[item].equals("Отмена")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
         
		
		if (resultCode == Activity.RESULT_OK) {
        	if (requestCode == ImageUploader.REQUEST_CAMERA) {
        		
        	}
        	else if (requestCode == ImageUploader.SELECT_FROM_GALLERY) {
        		mImageUploader.selectFile(data);
        	}
        	if(mImagesList.size() >= 3)
        	{
        		mUploadImageBtn.setVisibility(View.GONE);
        	}
        	mScrollView.requestFocus();
        	mScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(position != 0)
		{
			// TODO Auto-generated method stub
			for(int i=0; i<mLayout.getChildCount(); i++)
			{
				if(parent.getId()<mLayout.getChildAt(i).getId() &&
						mLayout.getChildAt(i).getTag() == "spinner")
				{
					int start_remove_id = mLayout.getChildAt(i).getId();
					while(mLayout.getChildAt(i).getTag() == "spinner")
					{
						mCategoryTypeList.remove(start_remove_id);
						mLayout.removeViewAt(i);
						mSpinnerPos -= 1;
						mSpinnerId -= 1;
					}
				}
			}
			mSelectedCategoryID = mCategoryTypeList.get(parent.getId()).get(position-1).Id;
			
			ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
			
			
			ArrayList<String> tSubCategoryName = new ArrayList<String>();
			tSubCategoryName.add("Выбрать");
			boolean tExist = false;
			for(int i=0; i<mCategoryData.size(); i++)
			{
				
				if(mCategoryData.get(i).ParentId.equals(mSelectedCategoryID))
				{
					tExist = true;
					categoryData.add(mCategoryData.get(i));
					tSubCategoryName.add(mCategoryData.get(i).Name);
				}
				
			}
			if(tExist)
			{
				LayoutParams lparams = new LayoutParams(
						   LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				Spinner sub_category = new Spinner(AdAddActivity.this);
				sub_category.setLayoutParams(lparams);
				
				SpinnerArrayAdapter categoriesAdapter = new SpinnerArrayAdapter(AdAddActivity.this, 
						R.layout.adapter_spinner, 
						tSubCategoryName);
				sub_category.setAdapter(categoriesAdapter);
				sub_category.setSelection(0);
				sub_category.setOnItemSelectedListener(this);
				mSpinnerId +=1;
				sub_category.setId(mSpinnerId);
				sub_category.setTag("spinner");
				mLayout.addView(sub_category, mSpinnerPos += 1);
				mCategoryTypeList.add(categoryData);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public class ImagesAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mImagesList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mImagesList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		private class ViewHolder
		{
			ImageView image;
			Button cancel;
			RelativeLayout load;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if(convertView == null)
			{
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(AdAddActivity.this).inflate(R.layout.adapter_upload_images, null);
				viewHolder.load = (RelativeLayout)convertView.findViewById(R.id.upload_progress);
				viewHolder.image = (ImageView)convertView.findViewById(R.id.image);
				viewHolder.cancel = (Button)convertView.findViewById(R.id.cancel_upload);
				viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Integer pos = (Integer)v.getTag();
						URLWithParams urlWithParams = new URLWithParams();
				        List<NameValuePair> params = new ArrayList<NameValuePair>();
				        params.add(new BasicNameValuePair("tag", AdTags.delete_file));
				        params.add(new BasicNameValuePair("delete_file_url", mImagesList.get(pos).url));
				        
				        urlWithParams.url = upLoadServerUri;
						urlWithParams.nameValuePairs = params;
						
				        new InsertData().execute(urlWithParams);
				        
						mImageUploader.cancelUpload(pos);

						mImagesList.remove((int)pos);

						mImagesAdapter.notifyDataSetChanged();
						mImageScroller.removeItem(pos);
						mImageScroller.setAdapter(AdAddActivity.this, mImagesAdapter);
				        
				        if(mImagesList.size() < 3
				        		&& mUploadImageBtn.getVisibility() == View.GONE)
				        {
				        	mUploadImageBtn.setVisibility(View.VISIBLE);
				        }
					}
				});
				
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			if(!mImagesList.get(position).load_state)
			{
				viewHolder.cancel.setVisibility(View.VISIBLE);
				viewHolder.load.setVisibility(View.GONE);
			}
			viewHolder.cancel.setTag(position);
			viewHolder.image.setImageBitmap(mImagesList.get(position).image);
			
			return convertView;
		}
		
	}

	@Override
	public void getInsertCompleteState() {
		// TODO Auto-generated method stub
		getSupportFragmentManager().beginTransaction().remove(mInsertFragment).commit();
		mInsertFragment = null;
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void getUploadCompleteState(String url) {
		// TODO Auto-generated method stub
		if(url == "no_connection"){
			
		}
		else if(url == "error"){
			
		}
		else {
			mImagesList.get(mImagesList.size()-1).url = url;
			mImagesList.get(mImagesList.size()-1).load_state = false;
			mImagesAdapter.notifyDataSetChanged();
			mImageScroller.setAdapter(AdAddActivity.this, mImagesAdapter);
		}
	}

	@Override
	public void getDecodeCompleteState(Bitmap bm) {
		// TODO Auto-generated method stub
		UploadImage img = new UploadImage();
        img.image = bm;
        img.load_state = true;
        mImagesList.add(img);
        mImageScroller.setAdapter(AdAddActivity.this, mImagesAdapter);
	}
}
