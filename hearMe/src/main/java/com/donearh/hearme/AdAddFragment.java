package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.donearh.hearme.AdAddDialog.OnActivityResult2;
import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageUploader;
import com.donearh.imageloader.ImageUploader.DecodeCompleteListener;
import com.donearh.imageloader.ImageUploader.UploadCompleteListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;

public class AdAddFragment extends Fragment implements OnItemSelectedListener,
														InsertCompleteListener,
														DecodeCompleteListener,
														UploadCompleteListener,
														ConnectionCallbacks, 
														OnConnectionFailedListener,
														CatChooserFragment.OnListFragmentInteractionListener {
	private ProgressBar mInitProgressBar;
	private ScrollView mScrollView;
	private LinearLayout mBottomLayout;
	private GoogleApiClient mGoogleApiClient;
	protected AccountManager mAccountManager;
	private ImageUploader mImageUploader;
	
	private Integer mAccPos;
	
	private LinearLayout mLayout;
	private String mState;
	private Integer mSelectedAreaID = -1;
	private Integer mSelectedCategoryID = -1;
	private String mEncodeGuestName;
	private String mEncodeTitle;
	private String mEncodeDesc;
	private Integer mAdState = 4;
	private Integer mSpinnerId = 0;
	
	private Integer mSpinnerPos = 2;
	
	private CardView mGuestLayout;
	private EditText mGuestName;
	private  EditText mTitle;
	private Spinner mAreaSpinner;

	private Button mCatChooser;

	private CatChooserDialog catChooserDialog;

	private EditText mDesc;
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
	
	private Button mAdAddBtn;
	
	private CardView mPolicyLayout;
	private CheckBox mPolicy;
	
	private ProgressBar mAddProgress;

	@Override
	public void onListFragmentInteraction(CategoryData item) {
		catChooserDialog.dismiss();
	}

	private class UploadImage
	{
		Bitmap image;
		int load_state;
		String url;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.dialog_ad_add, container, false);
		
		mInitProgressBar = (ProgressBar)rootView.findViewById(R.id.load);
		mBottomLayout = (LinearLayout)rootView.findViewById(R.id.bottom_layout);
		
		Integer selectedAreaId = getArguments().getInt("id_main_area");
		mAccPos = getArguments().getInt("acc_pos");

		GoogleSignInOptions plusOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
				//.addActivityTypes(MomentUtil.ACTIONS).build();
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
					        .addApi(Auth.GOOGLE_SIGN_IN_API, plusOptions)
					        //.addScope(Plus.SCOPE_PLUS_LOGIN)
					        //addScope(new Scope(Scopes.PROFILE))
					        .addConnectionCallbacks(this)
					        .addOnConnectionFailedListener(this)
					        .build();
		
		mAccountManager = AccountManager.get(getActivity());
		mRadioPay = (RadioButton)rootView.findViewById(R.id.pay);
		
		mImageUploader = new ImageUploader(getActivity(),
											"upload_file");
		mImageUploader.setDecodeListener(this);
		mAreaData = new ArrayList<AreaData>(); 
		mImagesList = new ArrayList<UploadImage>();
		
		mImageUploader.setDecodeListener(this);
		mImageUploader.setUploadListener(this);
		
		upLoadServerUri = Urls.ADS;
		//Get area data
		MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
		mAreaData = db.getAreaData();


		mCategoryData = db.getCategoryData();
		
		mCategoryTypeList = new ArrayList<ArrayList<CategoryData>>();
		ArrayList<CategoryData> tCategoryData = new ArrayList<CategoryData>();
		
		mScrollView = (ScrollView)rootView.findViewById(R.id.scroller);
		
		mGuestLayout = (CardView)rootView.findViewById(R.id.guest_layout);
		mPolicyLayout = (CardView)rootView.findViewById(R.id.policy_layout);
		DatabaseHandler user_db = new DatabaseHandler(getActivity());

		if(mAccPos != -1){
			mAdState = 3;
			mGuestLayout.setVisibility(View.GONE);
			mPolicyLayout.setVisibility(View.GONE);
		}
		//mImageUploader.setUserId(mUserId);
		mLayout = (LinearLayout)rootView.findViewById(R.id.layout);

		mGuestName = (EditText)rootView.findViewById(R.id.edit_guest_name);
		
		mTitle = (EditText)rootView.findViewById(R.id.edit_title);
		mTitle.addTextChangedListener(new TextWatcher() {
			
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
				if(mTitle.getText().length()>0)
				{
					mTitle.setError(null);
				}
			}
		});
		ArrayList<String> tAreaName = new ArrayList<String>();
		tAreaName.add(getString(R.string.select_area));
		
		int selectedAreaPos = -1;
		for(int i=0; i<mAreaData.size(); i++)
		{
			if(selectedAreaId.equals(mAreaData.get(i).id))
				selectedAreaPos = i;
			
			tAreaName.add(mAreaData.get(i).name);
		}
		
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
					//((MainControlBarActivity)getActivity()).mSavedData.saveSelectedArea(mAreaData.get(position-1).id);
					mSelectedAreaID = mAreaData.get(position-1).id;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

		for(int i=0; i<mCategoryData.size(); i++)
		{
			if(mCategoryData.get(i).parent_id == 0)
			{
				tCategoryData.add(mCategoryData.get(i));
			}
		}
		Collections.sort(tCategoryData, new CatComparator());
		CategoryData cat = new CategoryData();
		cat.name = getString(R.string.select_category);
		tCategoryData.add(0,cat);
		mCategoryTypeList.add(tCategoryData);

		mCatChooser = (Button)rootView.findViewById(R.id.cat_chooser);
		mCatChooser.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				catChooserDialog = new CatChooserDialog();
				catChooserDialog.show(getChildFragmentManager(), "cat_chooser_dialog");
			}
		});

		mDesc = (EditText)rootView.findViewById(R.id.edit_ad_desc);
		mDesc.addTextChangedListener(new TextWatcher() {
			
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
				if(mDesc.getText().length() >0)
				{
					mDesc.setError(null);
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
		
		mAdAddBtn = (Button)rootView.findViewById(R.id.confirm_add);
		mAdAddBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addAd();
				
			}
		});
		
		mImageScroller = (CustomImageScroller)rootView.findViewById(R.id.image_scroller);
		mImagesAdapter = new ImagesAdapter();
		mImageScroller.setAdapter(getActivity(), mImagesAdapter);
		
		
		mPolicy = (CheckBox)rootView.findViewById(R.id.confirm_policy);
		mPolicy.setText(Html.fromHtml(getString(R.string.i_accept)+" <a href='http://webhearme.ru/policy/user_policy.html'>"+getString(R.string.use_case)+"</a>"
				+ " "+getString(R.string.accept_policy)));
		mPolicy.setMovementMethod(LinkMovementMethod.getInstance());
		
		mAddProgress = (ProgressBar)rootView.findViewById(R.id.add_progress);
		
		return rootView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stubsuper.onDestroy();
		mImagesList.clear();
		mImagesList = null;
		super.onDestroy();
	}	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.disconnect();
		    }
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		//mGoogleApiClient.connect();
		onConnectionFailed(null);
		super.onResume();
	}
	
	
	private class AccessTokenActionsTask extends AsyncTask<HashMap<String, ArrayList<String>>, Void, String> {

		private String social_type;
		private String type;
		protected View view;
		private HashMap<String, ArrayList<String>> mParams;
		
	    @Override
	    protected String doInBackground(HashMap<String, ArrayList<String>>... params) {
	    	type = params[0].get("type").get(0);
	    	params[0].remove("type");
	    	mParams = params[0];
	    	Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	    	social_type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = "";//Plus.AccountApi.getAccountName(mGoogleApiClient);

		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(getActivity(), account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, getActivity(), null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, getActivity(), null, null);
	   	                    	bnd = future.getResult();
	   	                    	access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	   	                    }
	   				 	}
	                } catch (Exception e) {
		                 return "no_conn";
	                }
				 
				return access_token;
	    	}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(String access_token) {
	    	if(access_token.equals("no_conn")){
	    		new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
	    		mAddProgress.setVisibility(View.GONE);
	    		mAdAddBtn.setVisibility(View.VISIBLE);
	    		return;
	    	}
	    		
	        if (access_token != null) {
	        	String url;
	        	if(social_type.equals("1")){
	        		url = Urls.G_USER;
        		}else{
        			url = Urls.USER;
        		}
	        	if(type == "ad_add"){
	        		ArrayList<String> par1 = new ArrayList<String>();
	        		par1.add(url);
	        		mParams.put("url", par1);
	        		ArrayList<String> par2 = new ArrayList<String>();
	        		par2.add(access_token);
	        		mParams.put("access_token", par2);
					mInsertTask = new InsertData();
					mInsertTask.setListener(AdAddFragment.this);
					
					mInsertTask.execute(mParams);
					
					mImagesList.clear();
        			
	        	}
	        	else if(type.equals("repeat_upload")){
	        		mImageUploader.repeatUpload(url, access_token);
					mImagesList.get(mImagesList.size()-1).load_state = 1;
					mImagesAdapter.notifyDataSetChanged();
					mImageScroller.setAdapter(getActivity(), mImagesAdapter);
	        	}
	        	
	        } else {
	        	Intent intent = new Intent();
				intent.putExtra("error", "expired_token");
				if(getActivity() instanceof MainControlBarActivity){
					((MainControlBarActivity)getActivity()).onActivityResult(MainControlBarActivity.AD_ADD, getActivity().RESULT_OK, intent);
					((AdAddDialog)getParentFragment()).dismiss();
				}
				else if(getActivity() instanceof AdAddActivity)
					((AdAddActivity)getActivity()).onActivityResult(MainControlBarActivity.AD_ADD, getActivity().RESULT_OK, intent);
				return;
	        }
	    }

	}

	public void catChoosed(String cat_tree_text, CategoryData cat){
		mCatChooser.setText(cat_tree_text);
		mSelectedCategoryID = cat.id;
		if(cat.left_color != null)
			mCatChooser.setBackgroundColor(Color.parseColor(cat.left_color));
	}

	private void addAd(){
		
		boolean error_exist = false;
		String ad_guest_name = mGuestName.getText().toString();
		String ad_title = mTitle.getText().toString();
		String ad_desc = mDesc.getText().toString();

		if(mPolicyLayout.getVisibility() == View.VISIBLE && !mPolicy.isChecked()) {
			mPolicy.setError(getString(R.string.error_not_check_policy));
			error_exist = true;
		}

		
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
				mDesc.setError(getString(R.string.ad_error_desc));
			else
				mDesc.setError(getString(R.string.error_text_too_short));
			mDesc.requestFocus();
			error_exist = true;
		}
		
		for(int i=0; i<mLayout.getChildCount(); i++){
			
			if(mLayout.getChildAt(i).getTag() == "spinner"){
				
				Spinner spinner = (Spinner)mLayout.getChildAt(i);
				if(spinner.getSelectedItemPosition() == 0){
					//mCatTitle.requestFocus();
					//mCatTitle.setError(getString(R.string.error_subcat_not_selected));
					error_exist = true;
					break;
				}
			}
		}
		
		if(mAreaSpinner.getSelectedItemPosition() == 0){
			//mAreaTitle.setError(getString(R.string.error_area_not_selected));
			//mAreaTitle.requestFocus();
			error_exist = true;
		}
		//if(mSelectedCategoryID == -1)
		
		if(mSelectedCategoryID == -1)
		{
			error_exist = true;
		}
		
		if(ad_title.length() < 2){
			if(ad_title.isEmpty())
				mTitle.setError(getString(R.string.ad_error_title));
			else
				mTitle.setError(getString(R.string.error_text_too_short));
			mTitle.requestFocus();
			error_exist = true;
		}
		
		if(!error_exist)
		{
			if(mRadioPay.isChecked())
				mAdState = 2;
			Integer t = mSelectedCategoryID;
			
			try {
				mEncodeGuestName = ad_guest_name;
				mEncodeTitle = ad_title;
				mEncodeDesc = ad_desc;
				mEncodeGuestName = URLEncoder.encode(ad_guest_name,"UTF-8");
				mEncodeTitle = URLEncoder.encode(ad_title,"UTF-8");
				mEncodeDesc = URLEncoder.encode(ad_desc,"UTF-8");
     		
		   } catch (UnsupportedEncodingException e) {
		       e.printStackTrace();
		   } 
			
			HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
			
			
			ArrayList<String> par = new ArrayList<String>();
			par.add(AdTags.insert_new_ad);
			params.put("tag", par);
			ArrayList<String> par1 = new ArrayList<String>();
			par1.add(mEncodeGuestName);
			params.put("ad_guest_name", par1);
			ArrayList<String> par2 = new ArrayList<String>();
			par2.add(mEncodeTitle);
			params.put("ad_title", par2);
			ArrayList<String> par3 = new ArrayList<String>();
			par3.add(mSelectedAreaID.toString());
			params.put("ad_area", par3);
			ArrayList<String> par4 = new ArrayList<String>();
			par4.add(mSelectedCategoryID.toString());
			params.put("ad_category", par4);
			ArrayList<String> par5 = new ArrayList<String>();
			par5.add(mEncodeDesc);
			params.put("ad_desc", par5);
			ArrayList<String> par6 = new ArrayList<String>();
			par6.add(mAdState.toString());
			params.put("ad_state", par6);
			
			if(mEditPhoneNum.length() != 0){
				ArrayList<String> par7 = new ArrayList<String>();
				par7.add(mEditPhoneNum.getText().toString());
				params.put("ad_phone_num", par7);
			}
			if(mEditEMail.getText().toString().trim().length() != 0){
				ArrayList<String> par7 = new ArrayList<String>();
				par7.add(mEditEMail.getText().toString().trim());
				params.put("ad_email", par7);
			}
			
			
			ArrayList<String> arr_param = new ArrayList<String>();
			for(int i=0; i<mImagesList.size(); i++){
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				mImagesList.get(i).image.compress(CompressFormat.JPEG, 100, bos);
			    byte[] data = bos.toByteArray();
			    String file = Base64.encodeBytes(data);
			    arr_param.add(file);
			}
			params.put("files_list[]", arr_param);
			
			ArrayList<String> par7 = new ArrayList<String>();
			par7.add("ad_add");
			params.put("type", par7);
			if (mAccPos != -1){
				new AccessTokenActionsTask().execute(params);
			}else{
				ArrayList<String> par8 = new ArrayList<String>();
				par8.add(Urls.ADS);
				params.put("url", par8);
				mInsertTask = new InsertData();
				mInsertTask.setListener(AdAddFragment.this);
				mInsertTask.execute(params);
				mImagesList.clear();
			}
			mAddProgress.setVisibility(View.VISIBLE);
			mAdAddBtn.setVisibility(View.GONE);
		}
		
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
	public void getUploadCompleteState(HashMap<String, String> result) {
		// TODO Auto-generated method stub
		if(result.get("error") != null){
			if(result.get("error").equals("no_connection")){
				new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
				mImagesList.get(mImagesList.size()-1).load_state = 2;
				mImagesAdapter.notifyDataSetChanged();
				mImageScroller.setAdapter(getActivity(), mImagesAdapter);
				return;
			}
			else if(result.get("error").equals("error")){
				new CustomToast(getActivity()).showErrorToast(getString(R.string.error_load_data));
				mImagesList.get(mImagesList.size()-1).load_state = 2;
				mImagesAdapter.notifyDataSetChanged();
				mImageScroller.setAdapter(getActivity(), mImagesAdapter);
				return;
			}
		}

		mImagesList.get(mImagesList.size()-1).url = result.get("image_url");
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
		// TODO Auto-generated method stub
		mAddProgress.setVisibility(View.GONE);
		mAdAddBtn.setVisibility(View.VISIBLE);
		if(result == "no_connection"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		else if(result == "error"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		else if(result == "expired_token"){
			
			
			return;
		}
		Intent intent = new Intent();
		
		intent.putExtra("area_id", mSelectedAreaID);
		intent.putExtra("cat_id", mSelectedCategoryID);
		intent.putExtra("ad_id", Integer.parseInt(String.valueOf(result)));
		
		if(getActivity() instanceof MainControlBarActivity){
			((MainControlBarActivity)getActivity()).onActivityResult(MainControlBarActivity.AD_ADD, getActivity().RESULT_OK, intent);
			((AdAddDialog)getParentFragment()).dismiss();
		}
		else if(getActivity() instanceof AdAddActivity)
			((AdAddActivity)getActivity()).onActivityResult(MainControlBarActivity.AD_ADD, getActivity().RESULT_OK, intent);
		
		
		//dismiss();
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
			        //mImageUploader.uploadBitmap(bmp);
		        }
		        else if(data != null){
		        	mImageUploader.selectFile(data.getData());
		        }
		        
        	}
        	else if (requestCode == ImageUploader.SELECT_FROM_GALLERY) {
        		if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	getDecodeCompleteState(bmp);
			        //mImageUploader.uploadBitmap(bmp);
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
		for(int i=0; i<mLayout.getChildCount(); i++)
		{
			if(parent.getId()<mLayout.getChildAt(i).getId() &&
					mLayout.getChildAt(i).getTag() == "spinner")
			{
				int start_remove_id = mLayout.getChildAt(i).getId();
				while(mLayout.getChildAt(i) != null && mLayout.getChildAt(i).getTag() == "spinner")
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
			//if(mCatTitle.getError() != null)
			//	mCatTitle.setError(null);
			// TODO Auto-generated method stub
			
			mSelectedCategoryID = mCategoryTypeList.get(parent.getId()).get(position).id;
			
			ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
			boolean tExist = false;
			for(int i=0; i<mCategoryData.size(); i++)
			{
				
				if(mCategoryData.get(i).parent_id.equals(mSelectedCategoryID))
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
				cat.name = getString(R.string.must_select);
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
						HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
						ArrayList<String> par = new ArrayList<String>();
						par.add("repeat_upload");
						params.put("type", par);
						new AccessTokenActionsTask().execute(params);
						
					}
				});
				viewHolder.cancel = (Button)convertView.findViewById(R.id.cancel_upload);
				viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Integer pos = (Integer)v.getTag();
						HashMap<String, ArrayList<String>> params = new HashMap<String, ArrayList<String>>();
				        
						ArrayList<String> par = new ArrayList<String>();
						par.add(AdTags.delete_file);
						params.put("tag", par);
						ArrayList<String> par1 = new ArrayList<String>();
						par1.add(mImagesList.get(pos).url);
						params.put("delete_file_url", par1);
						ArrayList<String> par2 = new ArrayList<String>();
						par2.add(upLoadServerUri);
						params.put("url", par2);
						
				        new InsertData().execute(params);
				        
						//mImageUploader.cancelUpload(pos);

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
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		mInitProgressBar.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		mBottomLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		mAdState = 3;
		mGuestLayout.setVisibility(View.GONE);
		mInitProgressBar.setVisibility(View.GONE);
		mPolicyLayout.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		mBottomLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
}
