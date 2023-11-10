package com.donearh.hearme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.UserFunctions;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;
import com.donearh.imageloader.ImageUploader;
import com.donearh.imageloader.ImageUploader.DecodeCompleteListener;
import com.donearh.imageloader.ImageUploader.UploadCompleteListener;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

public class ProfileActivity extends ActionBarActivity implements DecodeCompleteListener, 
																  UploadCompleteListener {
	
	static final int REQUEST_CAMERA = 0;
	static final int SELECT_FILE = 1;
	static final int CROP = 2;
	static final int LOG_OUT = 3;
	
	UserFunctions mUserFunctions;
	
	static HashMap<String, String> mUserData;
	private static ImageLoaderRounded mImageLoaderRounded;
	private ImageUploader mImageUploader;
	
	private RelativeLayout mLoadCircle;
	private ImageView mAvatarImage;
	
	private boolean mIsUpdAvatar = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		mImageLoaderRounded = new ImageLoaderRounded(ProfileActivity.this, ImageLoader.DIR_USER_AVATAR);
		mImageUploader = new ImageUploader(ProfileActivity.this,
											Urls.API + Urls.USER,
											"update_avatar");
		mImageUploader.setUploadListener(this);
		mImageUploader.setDecodeListener(this);
		
		mUserFunctions = new UserFunctions();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		mUserData = db.getUserDetails();
		mImageUploader.setUserId(mUserData.get(LoginKeys.KEY_ID));
		mAvatarImage = (ImageView)findViewById(R.id.avatar_image);
		mImageLoaderRounded.mImageFetcherRounded.loadImage(mUserData.get(LoginKeys.KEY_IMAGE_URL), mAvatarImage);
		mAvatarImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		
		mLoadCircle = (RelativeLayout)findViewById(R.id.load_circle);
		mLoadCircle.setVisibility(View.GONE);
		TextView userLogin = (TextView)findViewById(R.id.user_login);
		userLogin.setText(mUserData.get(LoginKeys.KEY_LOGIN));
		
		
		Button Btn_logout = (Button)findViewById(R.id.btn_logout);
		Btn_logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserFunctions.logoutUser(getApplicationContext());
				
				setResult(LOG_OUT);
				finish();
			}
		});
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mImageLoaderRounded.destroy();
		if(mIsUpdAvatar)
			setResult(RESULT_OK);
		super.onDestroy();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mImageLoaderRounded.onPause();
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mImageLoaderRounded.onResume();
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
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

		AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
		builder.setTitle("Аватар");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Cфотографировать")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//File f = new File(android.os.Environment
					//		.getExternalStorageDirectory(), "temp.jpg");
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					intent.putExtra("crop", "true");
				    //indicate aspect of desired crop
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					    //indicate output X and Y
					intent.putExtra("outputX", 256);
					intent.putExtra("outputY", 256);
					    //retrieve data on return
					intent.putExtra("return-data", true);
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Из галереи")) {
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
		            intent.putExtra("return-data", true);
		            intent.setAction(Intent.ACTION_PICK);
		            
					startActivityForResult(
							Intent.createChooser(intent, "Выберите файл"),
							SELECT_FILE);
				} else if (items[item].equals("Отмена")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");

		        }
		        mImageUploader.uploadBitmap(bmp);
		        mLoadCircle.setVisibility(View.VISIBLE);
			} else if (requestCode == SELECT_FILE) {

				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");

		        }
		        mImageUploader.uploadBitmap(bmp);
		        mLoadCircle.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void getDecodeCompleteState(Bitmap bm) {
		// TODO Auto-generated method stub
		mAvatarImage.setImageBitmap(bm);
	}
	
	@Override
	public void getUploadCompleteState(String url) {
		// TODO Auto-generated method stub
		if(url == "no_connection"){
			
		}
		else if(url == "error"){
			
		}
		else{
			mLoadCircle.setVisibility(View.GONE);
			mUserFunctions.updateAvatar(ProfileActivity.this, url);
			mImageLoaderRounded.mImageFetcherRounded.loadImage(url, mAvatarImage);
			mIsUpdAvatar = true;
		}
	}

}
