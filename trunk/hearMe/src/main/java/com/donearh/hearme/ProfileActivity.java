package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
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

public class ProfileActivity extends AppCompatActivity implements DecodeCompleteListener, 
																  UploadCompleteListener,
																  InsertCompleteListener,
																  GetJSONListener,
																  ConnectionCallbacks, 
																OnConnectionFailedListener{
	
	static final int REQUEST_CAMERA = 0;
	static final int SELECT_FILE = 1;
	static final int CROP = 2;
	static final int GOOGLE_LOG_OUT = 3;
	static final int LOG_OUT = 4;
	public static int UPDATE_AVATAR = 5;
	public static int UPDATE_NAME = 6;
	static final int GOOGLE_REMOVE_ACCOUNT = 7;
	static final int REMOVE_ACCOUNT = 8;
	
	private GoogleApiClient mGoogleApiClient;
	private AccountManager mAccountManager;
	
	private Integer mAccPos;

	private ImageUploader mImageUploader;
	
	private LinearLayout mUserLayout;
	private LinearLayout logout_back;
	private LinearLayout org_view;
	private TextView desc_text;
	private RelativeLayout mLoadCircle;
	private RelativeLayout mRepeatCircle;
	private ImageView mAvatarImage;
	private CollapsingToolbarLayout collapsingToolbar;
	
	private boolean mIsUpdAvatar = false;
	private boolean mIsUpdAvatarProcess = false;
	private boolean mIsUpdName = false;
	
	private Uri picUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

		mAccPos = getIntent().getIntExtra(AccountGeneral.KEY_ACC_POS, -1);
		GoogleSignInOptions plusOptions = new GoogleSignInOptions.Builder().build();
				//.addActivityTypes(MomentUtil.ACTIONS)
		// ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, plusOptions)
				//.addScope(Plus.SCOPE_PLUS_LOGIN)
				//.addScope(new Scope(Scopes.PROFILE))
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				//.addApi(AppIndex.API)
				.build();
		mAccountManager = AccountManager.get(this);

		mImageUploader = new ImageUploader(ProfileActivity.this,
				"update_avatar");
		mImageUploader.setUploadListener(this);
		mImageUploader.setDecodeListener(this);

		mUserLayout = (LinearLayout) findViewById(R.id.user_layout);
		org_view = (LinearLayout) findViewById(R.id.org_view);
		desc_text = (TextView) findViewById(R.id.org_desc);

		mAvatarImage = (ImageView) findViewById(R.id.avatar_image);
		final LinearLayout user_settings = (LinearLayout) findViewById(R.id.user_settings);
		logout_back = (LinearLayout) findViewById(R.id.log_out_back);
		logout_back.setVisibility(View.GONE);
		Button Btn_logout = (Button) findViewById(R.id.btn_logout);

		Button btn_remove_account = (Button) findViewById(R.id.remove_account);
		/*if(mGoogleApiClient.isConnected() && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			mUserName.setText(currentPerson.getDisplayName());
			if(currentPerson.getAboutMe().length() != 0 && !currentPerson.getAboutMe().equals("null")){
				org_view.setVisibility(View.VISIBLE);
				desc_text.setText(currentPerson.getAboutMe());
			}
		}
		else */

		Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		String social_type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
		if (social_type.equals("0")) {
			loadProfile();
		}

		mAvatarImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsUpdAvatarProcess = true;
				selectImage();
			}
		});

		mLoadCircle = (RelativeLayout) findViewById(R.id.load_circle);
		mLoadCircle.setVisibility(View.GONE);
		mRepeatCircle = (RelativeLayout) findViewById(R.id.repeat_circle);
		mRepeatCircle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AccessTokenActionsTask().execute("repeat_upload");
			}
		});

		Btn_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mGoogleApiClient.isConnected()) {
					//Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					setResult(GOOGLE_LOG_OUT);
					finish();
				} else {
					setResult(LOG_OUT);
					finish();
				}


			}
		});

		btn_remove_account.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mGoogleApiClient.isConnected()) {
					//Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					setResult(GOOGLE_REMOVE_ACCOUNT);
					finish();
				} else {
					setResult(REMOVE_ACCOUNT);
					finish();
				}
			}
		});
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Connect to Drive and Google+
		//mGoogleApiClient.connect();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Profile Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.donearh.hearme/http/host/path")
		);
		AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Profile Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.donearh.hearme/http/host/path")
		);
		AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);*/

		if (mGoogleApiClient.isConnected() && !mIsUpdAvatarProcess) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.profile, menu);
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
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	private class AccessTokenActionsTask extends AsyncTask<String, Void, String> {

		private String social_type;
		protected View view;
		private String type;
		
		private Bitmap mBitmap;
		
		public void setBitmap(Bitmap bm){
			mBitmap = bm;
		}
	    @Override
	    protected String doInBackground(String... params) {
	    	type = params[0];
	    	Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	    	social_type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = "";//Plus.AccountApi.getAccountName(mGoogleApiClient);
		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(ProfileActivity.this, account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, ProfileActivity.this, null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, ProfileActivity.this, null, null);
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
	    		new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.no_connection));
	    		return;
	    	}
	        if (access_token != null) {
	        	String url;
	        	if(social_type.equals("1"))
	        		url = Urls.G_USER;
	        	else
	        		url = Urls.USER;
	        	if(type.equals("upload_image")){
					Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	        		mImageUploader.setCropWH(200);
		        	mImageUploader.uploadBitmap(mBitmap, mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_IMAGE_URL), url, access_token);
	        	}
	        	else if(type.equals("repeat_upload")){
	        		mImageUploader.repeatUpload(url, access_token);
	        	}
	        		
	        	
	        } else {
	        	Intent intent = new Intent();
				intent.putExtra("error", "expired_token");
				return;
	        }
	    }

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		loadProfile();
	}



	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadProfile(){
		Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		mUserLayout.setVisibility(View.VISIBLE);
		logout_back.setVisibility(View.VISIBLE);
		collapsingToolbar.setTitle(mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_DISPLAY_NAME));
		String desc = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_DESC);
		if(mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_HM_ACC_TYPE) == "2"){
			
			if(desc != null && desc.length() != 0 && !desc.equals("null")){
				org_view.setVisibility(View.VISIBLE);
				desc_text.setText(desc);
			}
		}
		
		//mImageUploader.setUserId(mUserData.get(LoginKeys.KEY_ID));
		Glide.with(ProfileActivity.this)
				.load(mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_IMAGE_URL))
				.asBitmap()
				.placeholder(R.drawable.icon_no_avatar)
				.into(new BitmapImageViewTarget(mAvatarImage){
					@Override
					protected void setResource(Bitmap resource) {
						RoundedBitmapDrawable circularBitmapDrawable =
								RoundedBitmapDrawableFactory.create(getResources(), resource);
						circularBitmapDrawable.setCircular(true);
						view.setImageDrawable(circularBitmapDrawable);
					}
				});
	}
	
	private void selectImage() {
		final CharSequence[] items = { getString(R.string.take_photo), 
									   getString(R.string.from_gallery),
									   getString(R.string.btn_cancel) };

		AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
		builder.setTitle(getString(R.string.choose_source));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals(getString(R.string.take_photo))) {
					try {
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						
						//takePictureIntent.putExtra("crop", "true");
					    //indicate aspect of desired crop
						/*takePictureIntent.putExtra("aspectX", 1);
						takePictureIntent.putExtra("aspectY", 1);
						takePictureIntent.putExtra("scale", true);
						    //indicate output X and Y
						takePictureIntent.putExtra("outputX", 256);
						takePictureIntent.putExtra("outputY", 256);
						    //retrieve data on return
						takePictureIntent.putExtra("return-data", true);*/

						startActivityForResult(takePictureIntent, REQUEST_CAMERA);
					}
					catch (ActivityNotFoundException anfe) {
			            new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.error_crop_not_support));
			        }
				} else if (items[item].equals(getString(R.string.from_gallery))) {
					try {
						Intent intent = new Intent("com.android.camera.action.CROP");
					    //set crop properties
			            intent.setType("image/*");
			            intent.putExtra("crop", "true");
			            intent.putExtra("aspectX", 1);
			            intent.putExtra("aspectY", 1);
			                //indicate output X and Y
			            intent.putExtra("outputX", 256);
			            intent.putExtra("outputY", 256);
			            intent.putExtra("return-data", true);
			            intent.setAction(Intent.ACTION_PICK);
			            
						startActivityForResult(
								Intent.createChooser(intent, getString(R.string.choose_file)),
								SELECT_FILE);
					}
					catch (ActivityNotFoundException anfe) {
						new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.error_crop_not_support));
			        }
				} else if (items[item].equals(getString(R.string.btn_cancel))) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mIsUpdAvatarProcess = false;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	
		        	mLoadCircle.setVisibility(View.VISIBLE);
		        	AccessTokenActionsTask task = new AccessTokenActionsTask();
		        	task.setBitmap(bmp);
		        	task.execute("upload_image");
		        }
		        else{
		        	try{
		        		mImageUploader.setCropWH(200);
			        	mImageUploader.selectFile(data.getData());
						mLoadCircle.setVisibility(View.VISIBLE);
		        	}catch(Exception e){
		        		new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.error_take_picture));
		        	}
		        	
		        	return;
		        }
				
			} else if (requestCode == SELECT_FILE) {

				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");
			        mLoadCircle.setVisibility(View.VISIBLE);
			        AccessTokenActionsTask task = new AccessTokenActionsTask();
		        	task.setBitmap(bmp);
		        	task.execute("upload_image");
			        return;
		        }
		        else{
		        	try{
			        	mImageUploader.setCropWH(200);
			        	mImageUploader.selectFile(data.getData());
			        	mLoadCircle.setVisibility(View.VISIBLE);
		        	}
		        	catch(Exception e){
		        		new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.error_take_picture));
		        	}
		        	return;
		        }
		        
		           
		        
			}
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
	    return Uri.parse(path);
	}
	
	public void cropImg(){
		try {
			/*Intent intent = new Intent("com.android.camera.action.CROP");
		    //set crop properties
			intent.setDataAndType(picUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
                //indicate output X and Y
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("return-data", true);
            
            startActivityForResult(intent, SELECT_FILE);*/

			//startActivityForResult(
			//		Intent.createChooser(intent, getString(R.string.choose_file)),
			//		SELECT_FILE);

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setClassName("com.android.camera", "com.android.camera.CropImage");
			intent.setData(picUri);
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 96);
			intent.putExtra("outputY", 96);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, SELECT_FILE);
		}
		catch (ActivityNotFoundException anfe) {
			new CustomToast(ProfileActivity.this).showErrorToast(getString(R.string.error_crop_not_support));
        }
	}
	@Override
	public void getDecodeCompleteState(Bitmap bm) {
		// TODO Auto-generated method stub
		AccessTokenActionsTask task = new AccessTokenActionsTask();
    	task.setBitmap(bm);
    	task.execute("upload_image");
	}
	
	@Override
	public void getUploadCompleteState(HashMap<String, String> result) {
		// TODO Auto-generated method stub
		mLoadCircle.setVisibility(View.GONE);
		if(result.get("error") != null){
			if(result.get("error").equals("no_connection"))
				mRepeatCircle.setVisibility(View.VISIBLE);
			if(result.get("error").equals("error"))
				mRepeatCircle.setVisibility(View.VISIBLE);
			if(result.get("error").equals("repeat")){
				mRepeatCircle.setVisibility(View.VISIBLE);
			}
			if(result.get("error").equals("refresh_token_expired")){
				setResult(MainControlBarActivity.EXPIRED_REFRESH_TOKEN);
				finish();
				return;
			}
				
		}
		else{
			
			//mUserFunctions.updateAvatar(ProfileActivity.this, url);
			Glide.with(ProfileActivity.this)
					.load(result.get("image_url"))
					.asBitmap()
					.placeholder(R.drawable.icon_no_avatar)
					.into(new BitmapImageViewTarget(mAvatarImage){
						@Override
						protected void setResource(Bitmap resource) {
							RoundedBitmapDrawable circularBitmapDrawable =
									RoundedBitmapDrawableFactory.create(getResources(), resource);
							circularBitmapDrawable.setCircular(true);
							view.setImageDrawable(circularBitmapDrawable);
						}
					});
			
			Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
			mAccountManager.setUserData(acc[mAccPos], AccountGeneral.KEY_IMAGE_URL, result.get("image_url"));
			mIsUpdAvatar = true;
			
			Intent intent = new Intent();
			if(result.get(AccountGeneral.KEY_REFRESH_TOKEN) != null){
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, result.get(AccountManager.KEY_AUTHTOKEN));
				intent.putExtra(AccountGeneral.KEY_REFRESH_TOKEN, result.get(AccountGeneral.KEY_REFRESH_TOKEN));
			}
			intent.putExtra("image_url", result.get("image_url"));
			intent.putExtra("upd_avatar", mIsUpdAvatar);
			intent.putExtra("upd_name", mIsUpdName);
			setResult(RESULT_OK, intent);
		}
	}



	@Override
	public void getInsertCompleteState(Object result) {
		// TODO Auto-generated method stub
		if(result == null)
			return;
		
		Intent intent = new Intent();
		intent.putExtra("upd_avatar", mIsUpdAvatar);
		intent.putExtra("upd_name", mIsUpdName);
		setResult(RESULT_OK, intent);
	}

	private void onSignOutClicked() {
	    // Clear the default account so that GoogleApiClient will not automatically
	    // connect in the future.
	    /*if (mGoogleApiClient.isConnected()) {
	        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
	        mGoogleApiClient.disconnect();
	    }

	    showSignedOutUI();*/
	}



	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub	
		setResult(LOG_OUT);
		finish();
	}



	
}
