package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageUploader;
import com.donearh.imageloader.ImageUploader.DecodeCompleteListener;
import com.donearh.imageloader.ImageUploader.UploadCompleteListener;

public class AdAddDialog extends DialogFragment implements AdapterView.OnItemSelectedListener,
															InsertCompleteListener,
															DecodeCompleteListener,
															UploadCompleteListener
{
	static int AD_ADD_DIALOG = 1;
	private ScrollView mScrollView;
	
	private ImageUploader mImageUploader;
	
	private LinearLayout mLayout;
	private String mUserId;
	private String mState;
	private Integer mSelectedAreaID = -1;
	private Integer mSelectedCategoryID = -1;
	private String mEncodeGuestName;
	private String mEncodeTitle;
	private String mEncodeDesc;
	private Integer mAdState = 4;
	private Integer mSpinnerId = 0;
	
	private Integer mSpinnerPos = 11;
	
	private TextView mAreaTitle;
	private Spinner mAreaSpinner;
	private TextView mCatTitle;
	private EditText mEditPhoneNum;
	private EditText mEditEMail;
	private ArrayList<AreaData> mAreaData;
	private ArrayList<UploadImage> mImagesList;
	
	private CustomImageScroller mImageScroller;
	private ImagesAdapter mImagesAdapter;
	
	private InsertData mInsertTask;
	private ImageButton mUploadImageBtn;
	private ArrayList<CategoryData> mCategoryData;
	private ArrayList<ArrayList<CategoryData>> mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
	
	private String upLoadServerUri = null;
	
	private RadioButton mRadioPay;
	
	private OnActivityResult2 mOnActivityResult2;
	
	private PhoneNumberFormattingTextWatcher mPhoneNumberWatcher;
	
	private class UploadImage
	{
		Bitmap image;
		int load_state;
		String url;
	}
	
	public interface OnActivityResult2 {
		public void onActivityResult2(int requestCode, int resultCode, Intent data);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		getDialog().setTitle(R.string.title_activity_ad_add);
		View rootView = inflater.inflate(R.layout.dialog_ad_add, container, false);
		
		mRadioPay = (RadioButton)rootView.findViewById(R.id.pay);
		
		mImageUploader = new ImageUploader(getActivity(), 
											Urls.SERVER_URL + Urls.API + Urls.ADS,
											"upload_file");
		mImageUploader.setDecodeListener(this);
		mAreaData = new ArrayList<AreaData>(); 
		mImagesList = new ArrayList<UploadImage>();
		
		mImageUploader.setDecodeListener(this);
		mImageUploader.setUploadListener(this);
		
		upLoadServerUri = Urls.SERVER_URL + Urls.API + Urls.ADS;
		//Get area data
		MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
		mAreaData = db.getAreaData();
		
		mCategoryData = db.getCategoryData();
		
		mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
		ArrayList<CategoryData> tCategoryData = new ArrayList<CategoryData>();
		
		mScrollView = (ScrollView)rootView.findViewById(R.id.scroller);
		
		final LinearLayout guest_layout = (LinearLayout)rootView.findViewById(R.id.guest_layout);
		DatabaseHandler user_db = new DatabaseHandler(getActivity());
		mUserId = user_db.getUserDetails().get(LoginKeys.KEY_ID);
		if(mUserId != null){
			mAdState = 3;
			guest_layout.setVisibility(View.GONE);
		}
		mImageUploader.setUserId(mUserId);
		mLayout = (LinearLayout)rootView.findViewById(R.id.layout);

		final EditText tGuestName = (EditText)rootView.findViewById(R.id.edit_guest_name);
		
		final EditText tTitle = (EditText)rootView.findViewById(R.id.edit_title);
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
		tAreaName.add(getString(R.string.must_select));
		Integer selectedAreaId = ((MainControlBarActivity)getActivity()).mSavedData.getMainAreaId();
		int selectedAreaPos = -1;
		for(int i=0; i<mAreaData.size(); i++)
		{
			if(selectedAreaId.equals(mAreaData.get(i).id))
				selectedAreaPos = i;
			
			tAreaName.add(mAreaData.get(i).name);
		}
		
		mAreaTitle = (TextView)rootView.findViewById(R.id.area_text);
		mAreaSpinner = (Spinner)rootView.findViewById(R.id.spinner_area);
		SpinnerArrayAdapter areaAdapter = new SpinnerArrayAdapter(getActivity(), 
				R.layout.adapter_spinner, 
				tAreaName);
		mAreaSpinner.setAdapter(areaAdapter);
		if(selectedAreaPos != -1)
		{
			mAreaSpinner.setSelection(selectedAreaPos+1);
		}
		else
		{
			mAreaSpinner.setSelection(0);
		}
		mAreaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position != 0)
				{
					if(mAreaTitle.getError() != null)
						mAreaTitle.setError(null);
					//((MainControlBarActivity)getActivity()).mSavedData.saveSelectedArea(mAreaData.get(position-1).id);
					mSelectedAreaID = mAreaData.get(position-1).id;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCatTitle = (TextView)rootView.findViewById(R.id.category_title);
		final Spinner tCategorySpinner = (Spinner)rootView.findViewById(R.id.spinner1);

		for(int i=0; i<mCategoryData.size(); i++)
		{
			if(mCategoryData.get(i).ParentId == 0)
			{
				tCategoryData.add(mCategoryData.get(i));
			}
		}
		Collections.sort(tCategoryData, new CatComparator());
		CategoryData cat = new CategoryData();
		cat.Name = getString(R.string.must_select);
		tCategoryData.add(0,cat);
		mCategoryTypeList.add(tCategoryData);
		SpinnerCatAdapter categoriesAdapter = new SpinnerCatAdapter(getActivity(), 
				R.layout.adapter_spinner, 
				tCategoryData);
		tCategorySpinner.setAdapter(categoriesAdapter);
		tCategorySpinner.setSelection(0);
		tCategorySpinner.setTag("spinner");
		tCategorySpinner.setOnItemSelectedListener(this);
		tCategorySpinner.setId(mSpinnerId);

		final EditText tDesc = (EditText)rootView.findViewById(R.id.edit_ad_desc);
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
		
		mEditPhoneNum = (EditText)rootView.findViewById(R.id.phone_num);
		mEditEMail = (EditText)rootView.findViewById(R.id.edit_email);
		mPhoneNumberWatcher = new PhoneNumberFormattingTextWatcher();
		mEditPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.length() != 0){
					if(mEditPhoneNum.length() == 13){
						if(mEditPhoneNum.getError() != null)
							if(mEditPhoneNum.getError().equals(getString(R.string.error_not_enough_character)))
								mEditPhoneNum.setError(null);
					}
					if(s.charAt(0) != '9'){
						mEditPhoneNum.setError(getString(R.string.error_phone_num));
					}
				}
				else{
					mEditPhoneNum.setError(null);
				}
				
				super.afterTextChanged(s);
			}
		});
		mUploadImageBtn = (ImageButton)rootView.findViewById(R.id.image_add_btn);
		mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		
		Button tAdAddBtn = (Button)rootView.findViewById(R.id.confirm_add);
		tAdAddBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean error_exist = false;
				String ad_guest_name = tGuestName.getText().toString();
				String ad_title = tTitle.getText().toString();
				String ad_desc = tDesc.getText().toString();
				
				
				if(mEditPhoneNum.length() != 0)
					if(mEditPhoneNum.length() < 13){
						mEditPhoneNum.setError(getString(R.string.error_not_enough_character));
					}
				
				
				if(mEditPhoneNum.getError() != null){
					mEditPhoneNum.requestFocus();
				}
				
				
				
				if(ad_desc.length() < 3)
				{
					if(ad_desc.isEmpty())
						tDesc.setError(getString(R.string.ad_error_desc));
					else
						tDesc.setError(getString(R.string.error_text_too_short));
					tDesc.requestFocus();
					error_exist = true;
				}
				
				for(int i=0; i<mLayout.getChildCount(); i++){
					
					if(mLayout.getChildAt(i).getTag() == "spinner"){
						
						Spinner spinner = (Spinner)mLayout.getChildAt(i);
						if(spinner.getSelectedItemPosition() == 0){
							mCatTitle.requestFocus();
							mCatTitle.setError(getString(R.string.error_subcat_not_selected));
							error_exist = true;
							break;
						}
					}
				}
				
				if(mAreaSpinner.getSelectedItemPosition() == 0){
					mAreaTitle.setError(getString(R.string.error_area_not_selected));
					mAreaTitle.requestFocus();
					error_exist = true;
				}
				//if(mSelectedCategoryID == -1)
				if(ad_title.length() < 2)
				{
					if(ad_title.isEmpty())
						tTitle.setError(getString(R.string.ad_error_title));
					else
						tTitle.setError(getString(R.string.error_text_too_short));
					tTitle.requestFocus();
					error_exist = true;
				}
				if(mSelectedCategoryID == -1)
				{
					error_exist = true;
					//tCategorySpinner.
				}
				if(!error_exist)
				{
					try {
						if(mUserId == null){
							if(ad_guest_name.length() == 0)
								ad_guest_name = getString(R.string.guest);
						}
						mEncodeGuestName = ad_guest_name;
						mEncodeTitle = ad_title;
						mEncodeDesc = ad_desc;
						mEncodeGuestName = URLEncoder.encode(ad_guest_name,"UTF-8");
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
					if(mUserId == null){
						params.add(new BasicNameValuePair("ad_guest_name", mEncodeGuestName));
					}
					params.add(new BasicNameValuePair("user_id", mUserId));
					params.add(new BasicNameValuePair("ad_title", mEncodeTitle));
					params.add(new BasicNameValuePair("ad_area", mSelectedAreaID.toString()));
					params.add(new BasicNameValuePair("ad_category", mSelectedCategoryID.toString()));
					params.add(new BasicNameValuePair("ad_desc", mEncodeDesc));
					params.add(new BasicNameValuePair("ad_state", mAdState.toString()));
					if(mEditPhoneNum.length() != 0)
						params.add(new BasicNameValuePair("ad_phone_num", mEditPhoneNum.getText().toString()));
					if(mEditEMail.getText().toString().trim().length() != 0)
						params.add(new BasicNameValuePair("ad_email", mEditEMail.getText().toString().trim()));
					for(int i=0; i<mImagesList.size(); i++)
					{
						params.add(new BasicNameValuePair("images_path[]", Urls.SERVER_URL + "upload_files/" + mImagesList.get(i).url));
					}
					
					urlWithParams.url = Urls.SERVER_URL + Urls.API + Urls.ADS;
					urlWithParams.nameValuePairs = params;
					
					mInsertTask = new InsertData();
					mInsertTask.setListener(AdAddDialog.this);
					
					mInsertTask.execute(urlWithParams);
					
					mImagesList.clear();
				}
				
				
			}
		});
		
		mImageScroller = (CustomImageScroller)rootView.findViewById(R.id.image_scroller);
		mImagesAdapter = new ImagesAdapter();
		mImageScroller.setAdapter(getActivity(), mImagesAdapter);
		
		
		
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stubsuper.onDestroy();
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
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
		super.onResume();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
    }

	private void selectImage() {
		final CharSequence[] items = { getString(R.string.take_photo), 
									   getString(R.string.from_gallery),
									   getString(R.string.btn_cancel) };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.choose_source));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals(getString(R.string.take_photo))) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra("return-data", true);
					getActivity().startActivityForResult(intent, ImageUploader.REQUEST_CAMERA);
				} else if (items[item].equals(getString(R.string.from_gallery))) {
					Intent intent = new Intent();
		            intent.setType("image/*");
		            intent.putExtra("return-data", true);
		            intent.setAction(Intent.ACTION_GET_CONTENT);
		            
		            getActivity().startActivityForResult(
							Intent.createChooser(intent, getString(R.string.choose_file)),
							ImageUploader.SELECT_FROM_GALLERY);
				} else if (items[item].equals(getString(R.string.btn_cancel))) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void getUploadCompleteState(String url) {
		// TODO Auto-generated method stub
		if(url == "no_connection"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			mImagesList.get(mImagesList.size()-1).load_state = 2;
			mImagesAdapter.notifyDataSetChanged();
			mImageScroller.setAdapter(getActivity(), mImagesAdapter);
			return;
		}
		else if(url == "error"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.error_load_data));
			mImagesList.get(mImagesList.size()-1).load_state = 2;
			mImagesAdapter.notifyDataSetChanged();
			mImageScroller.setAdapter(getActivity(), mImagesAdapter);
			return;
		}

		mImagesList.get(mImagesList.size()-1).url = url;
		mImagesList.get(mImagesList.size()-1).load_state = 3;
		mImagesAdapter.notifyDataSetChanged();
		mImageScroller.setAdapter(getActivity(), mImagesAdapter);

	}

	@Override
	public void getDecodeCompleteState(Bitmap bm) {
		// TODO Auto-generated method stub
		UploadImage img = new UploadImage();
        img.image = bm;
        img.load_state = 1;
        mImagesList.add(img);
        mImageScroller.setAdapter(getActivity(), mImagesAdapter);
	}

	@Override
	public void getInsertCompleteState(Object result) {

		if(result == "no_connection"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		else if(result == "error"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("area_id", mSelectedAreaID);
		intent.putExtra("cat_id", mSelectedCategoryID);
		intent.putExtra("ad_id", Integer.parseInt(String.valueOf(result)));
		((MainControlBarActivity)getActivity()).onActivityResult2(MainControlBarActivity.AD_ADD, getActivity().RESULT_OK, intent);
		
		dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
         
		if (resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap bmp = null;
        	if (requestCode == ImageUploader.REQUEST_CAMERA) {
        		
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	getDecodeCompleteState(bmp);
			        mImageUploader.uploadBitmap(bmp);
		        }
		        else if(data != null){
		        	mImageUploader.selectFile(data.getData());
		        }
		        
        	}
        	else if (requestCode == ImageUploader.SELECT_FROM_GALLERY) {
        		if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	getDecodeCompleteState(bmp);
			        mImageUploader.uploadBitmap(bmp);
        		}
        		else if(data != null){
		        	mImageUploader.selectFile(data.getData());
		        }
        		
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
		if(position != 0)
		{
			if(mCatTitle.getError() != null)
				mCatTitle.setError(null);
			// TODO Auto-generated method stub
			
			mSelectedCategoryID = mCategoryTypeList.get(parent.getId()).get(position).Id;
			
			ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
			boolean tExist = false;
			for(int i=0; i<mCategoryData.size(); i++)
			{
				
				if(mCategoryData.get(i).ParentId.equals(mSelectedCategoryID))
				{
					tExist = true;
					categoryData.add(mCategoryData.get(i));
				}
				
			}
			if(tExist)
			{
				LayoutParams lparams = new LayoutParams(
						   LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				Spinner sub_category = new Spinner(getActivity());
				sub_category.setLayoutParams(lparams);
				
				Collections.sort(categoryData, new CatComparator());
				CategoryData cat = new CategoryData();
				cat.Name = getString(R.string.must_select);
				categoryData.add(0,cat);
				SpinnerCatAdapter categoriesAdapter = new SpinnerCatAdapter(getActivity(), 
						R.layout.adapter_spinner, 
						categoryData);
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
			Button repeat;
			RelativeLayout load;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if(convertView == null)
			{
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_upload_images, null);
				viewHolder.load = (RelativeLayout)convertView.findViewById(R.id.upload_progress);
				viewHolder.image = (ImageView)convertView.findViewById(R.id.image);
				viewHolder.repeat = (Button)convertView.findViewById(R.id.repeat_upload);
				viewHolder.repeat.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mImageUploader.repeatUpload();
						mImagesList.get(mImagesList.size()-1).load_state = 1;
						mImagesAdapter.notifyDataSetChanged();
						mImageScroller.setAdapter(getActivity(), mImagesAdapter);
					}
				});
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
						mImageScroller.setAdapter(getActivity(), mImagesAdapter);
				        
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
			if(mImagesList.get(position).load_state == 2){
				viewHolder.repeat.setVisibility(View.VISIBLE);
				viewHolder.load.setVisibility(View.GONE);
			}
			if(mImagesList.get(position).load_state == 3)
			{
				viewHolder.cancel.setVisibility(View.VISIBLE);
				viewHolder.repeat.setVisibility(View.GONE);
				viewHolder.load.setVisibility(View.GONE);
			}
			viewHolder.cancel.setTag(position);
			viewHolder.image.setImageBitmap(mImagesList.get(position).image);
			
			return convertView;
		}
		
	}

}
