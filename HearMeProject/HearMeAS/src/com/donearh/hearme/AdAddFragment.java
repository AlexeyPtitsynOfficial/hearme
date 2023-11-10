package com.donearh.hearme;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.donearh.hearme.InsertDataFragment.InsertCompleteListener;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.hearme.util.ExifUtil;
import com.donearh.imageloader.ImageUploader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdAddFragment extends Fragment implements AdapterView.OnItemSelectedListener,
														InsertCompleteListener
{
	private JSONArray mJSONArray;
	
	private InputStream is = null;
	private StringBuilder sb = null;

	private MainControlBarActivity mMainControlBarActivity;
	
	private ScrollView mScrollView;
	
	private ImageUploader mImageUploader;
	
	private LinearLayout mLayout;
	private String mUserId;
	private Integer mSelectedAreaID = -1;
	private Integer mSelectedCategoryID = -1;
	private String mEncodeTitle;
	private String mEncodeDesc;
	private Integer mSpinnerId = 0;
	
	private Integer mSpinnerPos = 5;
	
	private ArrayList<AreaData> mAreaData;
	private ArrayList<UploadImage> mImagesList;
	
	private CustomImageScroller mImageScroller;
	private ImagesAdapter mImagesAdapter;
	
	private Button mUploadImageBtn;
	private ArrayList<CategoryData> mCategoryData;
	
	private class UploadImage
	{
		Bitmap image;
		boolean load_state;
		String url;
	}
	
	private ArrayList<ArrayList<CategoryData>> mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
	
	//private TextView messageText;
	private int serverResponseCode = 0;
    
    private String upLoadServerUri = null;
    private String imagepath=null;
	private Bitmap mImageBitmap;
	
	private ArrayList<ImageUploadTask> mImageUploadTasks;
	
	public AdAddFragment()
	{
		//mImageUploader = new ImageUploader(getActivity());
		mAreaData = new ArrayList<AreaData>(); 
		mImagesList = new ArrayList<UploadImage>();
		mImageUploadTasks = new ArrayList<AdAddFragment.ImageUploadTask>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		super.onCreateView(inflater, container, saveInstanceState);
		mMainControlBarActivity = (MainControlBarActivity)getActivity();
		
		upLoadServerUri = Urls.API + Urls.AD_FILE_UPLOAD;
		//Get area data
		MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
		mAreaData = db.getAreaData();
		mCategoryData = mMainControlBarActivity.mCategoryData;
		
		mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
		ArrayList<CategoryData> tCategoryData = new ArrayList<CategoryData>();
		
		View rootView = inflater.inflate(R.layout.fragment_ad_add, container, false);
		
		mScrollView = (ScrollView)rootView.findViewById(R.id.scroller);
		
		mUserId = ((MainControlBarActivity)getActivity()).mUserData.get(LoginKeys.KEY_ID);
		
		mLayout = (LinearLayout)rootView.findViewById(R.id.layout);
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
		tAreaName.add("Выбрать");
		Integer selectedAreaId = ((MainControlBarActivity)getActivity()).mSavedData.getAreaId();
		int selectedAreaPos = -1;
		for(int i=0; i<mAreaData.size(); i++)
		{
			if(selectedAreaId.equals(mAreaData.get(i).id))
				selectedAreaPos = i;
			
			tAreaName.add(mAreaData.get(i).name);
		}
		
		final Spinner tAreaSpinner = (Spinner)rootView.findViewById(R.id.spinner_area);
		SpinnerArrayAdapter areaAdapter = new SpinnerArrayAdapter(getActivity(), 
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
					((MainControlBarActivity)getActivity()).mSavedData.saveSelectedArea(mAreaData.get(position-1).id);
					mSelectedAreaID = mAreaData.get(position-1).id;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		final Spinner tCategorySpinner = (Spinner)rootView.findViewById(R.id.spinner1);
		
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
		SpinnerArrayAdapter categoriesAdapter = new SpinnerArrayAdapter(getActivity(), 
				R.layout.adapter_spinner, 
				tCategoryName);
		tCategorySpinner.setAdapter(categoriesAdapter);
		tCategorySpinner.setSelection(0);
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
		mUploadImageBtn = (Button)rootView.findViewById(R.id.image_add_btn);
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
					
					Integer t = mSelectedCategoryID;
					
					URLWithParams urlWithParams = new URLWithParams();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("tag", AdTags.insert_new_ad));
					params.add(new BasicNameValuePair("user_id", mUserId));
					params.add(new BasicNameValuePair("ad_title", mEncodeTitle));
					params.add(new BasicNameValuePair("ad_area", mSelectedAreaID.toString()));
					params.add(new BasicNameValuePair("ad_category", mSelectedCategoryID.toString()));
					params.add(new BasicNameValuePair("ad_desc", mEncodeDesc));
					for(int i=0; i<mImagesList.size(); i++)
					{
						params.add(new BasicNameValuePair("images_path[]", mImagesList.get(i).url));
					}
					
					urlWithParams.url = Urls.API + Urls.ADS;
					urlWithParams.nameValuePairs = params;
					
					InsertDataFragment fragment = new InsertDataFragment();
					fragment.setListener(AdAddFragment.this);
					fragment.startInsert(urlWithParams, 
							InsertDataFragment.AD_ADD, 
							getString(R.string.notif_ad_add));
					FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
					fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
					
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
		
		mImageScroller = (CustomImageScroller)rootView.findViewById(R.id.image_scroller);
		mImagesAdapter = new ImagesAdapter();
		//mImageScroller.setAdapter(getActivity(), mImagesAdapter);
		return rootView;
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
	
	private void selectImage() {
		final CharSequence[] items = { "Cфотографировать", "Из галереи",
				"Отмена" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            //Bitmap photo = (Bitmap) data.getData().getPath(); 
        	/*String[] filePathColumn = { MediaStore.Images.Media.DATA };
        	Uri selectedImageUri = data.getData();
        	if (Build.VERSION.SDK_INT < 19) {
	        	  Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
	        	    filePathColumn, null, null, null);
	        	  cursor.moveToFirst();
	
	        	  int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        	  String picturePath = cursor.getString(columnIndex);
	        	  cursor.close();
	
	        	  decodeFile(picturePath);
        	}
        	else
        	{
        		String file_path = getRealPathFromURI(selectedImageUri);
        	    // Check for the freshest data.
        		ParcelFileDescriptor parcelFileDescriptor;
                try {
                    parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(selectedImageUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    decodeFile2(fileDescriptor, file_path);
                    parcelFileDescriptor.close();
                    

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        	}*/
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

	
	@SuppressLint("NewApi")
	public String getRealPathFromURI(Uri contentUri) 
	{
		String wholeID = DocumentsContract.getDocumentId(contentUri);

		// Split at colon, use second item in the array
		String id = wholeID.split(":")[1];

		String[] column = { MediaStore.Images.Media.DATA };     

		// where id is equal to             
		String sel = MediaStore.Images.Media._ID + "=?";

		Cursor cursor = getActivity().getContentResolver().
		                          query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
		                          column, sel, new String[]{ id }, null);

		String filePath = "";

		int columnIndex = cursor.getColumnIndex(column[0]);

		if (cursor.moveToFirst()) {
		    filePath = cursor.getString(columnIndex);
		}   

		cursor.close();
		return filePath;
	}
	
	public void decodeFile(String filePath) {
		 
        // Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
        	if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
        		break;
        	width_tmp /= 2;
        	height_tmp /= 2;
        	scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        mImageBitmap = BitmapFactory.decodeFile(filePath, o2);
        mImageBitmap = ExifUtil.rotateBitmap(filePath, mImageBitmap);
        decodeFile(imagepath);
        UploadImage img = new UploadImage();
        img.image = mImageBitmap;
        img.load_state = true;
        mImagesList.add(img);
        mImagesAdapter.notifyDataSetChanged();
        //mImageScroller.addItem(mImagesAdapter);
        //mImageScroller.setAdapter(getActivity(), mImagesAdapter);
        
        mImageUploadTasks.add(new ImageUploadTask());
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute();
	}
	
	public void decodeFile2(FileDescriptor fileDescriptor, String file_path) {
		 
        // Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
        	if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
        		break;
        	width_tmp /= 2;
        	height_tmp /= 2;
        	scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        mImageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o2);
        mImageBitmap = ExifUtil.rotateBitmap(file_path, mImageBitmap);
        
        UploadImage img = new UploadImage();
        img.image = mImageBitmap;
        img.load_state = true;
        mImagesList.add(img);
        
        //mImageScroller.setAdapter(getActivity(), mImagesAdapter);
        //mImageScroller.addItem(mImagesAdapter);
        
        mImageUploadTasks.add(new ImageUploadTask());
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(mImagesList.size()-1);
	}
	
	class ImageUploadTask extends AsyncTask<Integer, Void, String> 
	{
		 private String webAddressToPost = upLoadServerUri;
		 private String result = null;
		 public Integer mPos;

		 @Override
		 protected void onPreExecute() 
		 {
		 }

		 @Override
		 protected String doInBackground(Integer... params) 
		 {
			 if(!isCancelled())
			 {
			  try 
			  {
				  mPos = params[0];
				   HttpClient httpClient = new DefaultHttpClient();
				   HttpContext localContext = new BasicHttpContext();
				   HttpPost httpPost = new HttpPost(webAddressToPost);
		
				   MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
				   ByteArrayOutputStream bos = new ByteArrayOutputStream();
				   mImageBitmap.compress(CompressFormat.JPEG, 100, bos);
				   byte[] data = bos.toByteArray();
				   String file = Base64.encodeBytes(data);
		
				   entity.addPart("tag", new StringBody("upload_file"));
				   entity.addPart("file", new StringBody(file));
				   entity.addPart("someOtherStringToSend", new StringBody("your string here"));
		
				   httpPost.setEntity(entity);
				   HttpResponse response = httpClient.execute(httpPost,localContext);
				   HttpEntity entity2 = response.getEntity();
				     is = entity2.getContent();
				   /*BufferedReader reader = new BufferedReader(new InputStreamReader(
				     response.getEntity().getContent(), "UTF-8"));
		
				   String sResponse = reader.readLine();*/
				  // return sResponse;
			  } catch (Exception e) {
			   // something went wrong. connection with the server error
			  }
			 
			 
			//convert response to string
				try
				{
				  BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8));
				  sb = new StringBuilder();
				  sb.append(reader.readLine() + "\n");

				  String line="0";
				  while ((line = reader.readLine()) != null) 
				  {
				      sb.append(line + "\n");
				  }
				  is.close();
				  result=sb.toString();
				}
				catch(Exception e)
				{
					
				   Log.e("doni_error", "Error converting result "+e.toString());
				   return null;
				}
				
				JSONObject json = null;
				try {
					json = new JSONObject(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("JSON Parser", "Error parsing data " + e.toString());
					Log.e("Error pars", result);
					return null;
				}
				
				try 
				{
					JSONObject json_data=null;
					mJSONArray = json.getJSONArray("file_path");
					for(int i=0;i<mJSONArray.length();i++)
				    {
						json_data = mJSONArray.getJSONObject(i);
						Log.e("doni_error", "pos = " + mPos);
						mImagesList.get(mPos).url = json_data.getString("file_path");
						Log.e("doni_error", "posend = " + mPos);
				    }
				}
				catch (JSONException e) {
		            e.printStackTrace();
		            Log.e("doni_error", "SQL ERROR RESULT = " + result);
		            return null;
		        }
			 }
			  return null;
		 }

		 @Override
		 protected void onPostExecute(String result)
		 {
			 mImagesList.get(mPos).load_state = false;
			 mImagesAdapter.notifyDataSetChanged();
			 //mImageScroller.setAdapter(getActivity(), mImagesAdapter);
			 
		 }
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		
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
				Spinner sub_category = new Spinner(mMainControlBarActivity);
				sub_category.setLayoutParams(lparams);
				
				SpinnerArrayAdapter categoriesAdapter = new SpinnerArrayAdapter(getActivity(), 
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
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInsertCompleteState() {
		// TODO Auto-generated method stub
		((MainControlBarActivity)getActivity()).selectItem(DrawerListAdapter.AD_ALL, -1);
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_upload_images, null);
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
				        
						mImageUploadTasks.get(pos).cancel(true);
						mImageUploadTasks.remove((int)pos);

						mImagesList.remove((int)pos);

						mImagesAdapter.notifyDataSetChanged();
						mImageScroller.removeItem(pos);
						//mImageScroller.setAdapter(getActivity(), mImagesAdapter);
						
						for(int i=0; i<mImageUploadTasks.size(); i++)
						{
							if(mImageUploadTasks.get(i).mPos > pos)
							{
								mImageUploadTasks.get(i).mPos -= 1;
							}
						}
				        //mImageScroller.setAdapter(getActivity(), mImagesAdapter);
				        
				        
				        
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
}
