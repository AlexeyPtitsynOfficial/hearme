package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.account.ServerAuth;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.library.UserFunctions;
import com.donearh.hearme.library.UserFunctions.UserLoginListener;
import com.donearh.imageloader.ImageUploader;
import com.donearh.imageloader.ImageUploader.DecodeCompleteListener;
import com.donearh.imageloader.ImageUploader.UploadCompleteListener;

public class RegisterActivity extends AppCompatActivity implements OnClickListener,
																	UserLoginListener,
																	GetJSONListener,
																	DecodeCompleteListener
																	
{
	static final int REQUEST_CAMERA = 0;
	static final int SELECT_FILE = 1;
	static final int CROP = 2;
	
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
	
	private Context mContext;
	private ImageUploader mImageUploader;

	private JSONParser mTaskLoginCheck;
	private boolean mLoginCorrect = false;
	
	private String mAccountType;
	
	private TextView mAccTypeText;
	private Spinner  mHMAccType;
	//private EditText mFirstNameEdit;
	//private EditText mLastNameEdit;
	private EditText mLoginEdit;
	private EditText mPassEdit;
	private EditText mPassConfEdit;
	private EditText mEmailEdit;
	private TextView mDisplayNameText;
	private EditText mDisplayNameEdit;
	private TextView mDescText;
	private EditText mDescEdit;
	//private ImageButton mAvatar;
	private Bitmap mAvatarBitmap;
	
	private CheckBox mPolicy;
	
	private ProgressBar mRegisterProgress;
	private Button mRegBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
		ArrayList<String> data = new ArrayList<String>();
		data.add(getString(R.string.must_select));
		data.add(getString(R.string.user));
		data.add(getString(R.string.organization));
		
		mAccountType = getIntent().getStringExtra(LoginActivity.ARG_ACCOUNT_TYPE);
		
		mRegisterProgress = (ProgressBar)findViewById(R.id.register_progress);
		
		mImageUploader = new ImageUploader(RegisterActivity.this,
				"update_avatar");
		//mImageUploader.setUploadListener(this);
		mImageUploader.setDecodeListener(this);
		
		mAccTypeText = (TextView)findViewById(R.id.acc_type_text);
		
		mHMAccType = (Spinner)findViewById(R.id.account_type);
		mHMAccType.setAdapter(new SpinnerArrayAdapter(RegisterActivity.this, R.layout.adapter_spinner, data));
		mHMAccType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mAccTypeText.setError(null);
				if(position == 1){
					mDisplayNameText.setText(getString(R.string.display_name_text));
					mDescText.setVisibility(View.GONE);
					mDescEdit.setVisibility(View.GONE);
				}
				else if(position == 2){
					mDisplayNameText.setText(getString(R.string.org_name));
					mDescText.setVisibility(View.VISIBLE);
					mDescEdit.setVisibility(View.VISIBLE);
				}
					
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		/*mFirstNameEdit = (EditText)findViewById(R.id.edit_first_name);		
		mFirstNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					mLoginCorrect = false;
					if (mFirstNameEdit.getText().length() <2) {
						mFirstNameEdit.setError(getString(R.string.error_not_enough_character));
					}
					else
					{
						mFirstNameEdit.setError(null);
					}
					if (mFirstNameEdit.getText().toString().contains(" ")) {
						mFirstNameEdit.setError(getString(R.string.error_space));
					}
					
					mDisplayNameEdit.setText(mFirstNameEdit.getText().toString()+" "+mLastNameEdit.getText().toString());
				}
			}
		});
		
		mLastNameEdit = (EditText)findViewById(R.id.edit_last_name);
		mLastNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					mLoginCorrect = false;
					if (mLastNameEdit.getText().length() <2) {
						mLastNameEdit.setError(getString(R.string.error_not_enough_character));
					}
					else
					{
						mLastNameEdit.setError(null);
					}
					if (mLastNameEdit.getText().toString().contains(" ")) {
						mLastNameEdit.setError(getString(R.string.error_space));
					}
					
					mDisplayNameEdit.setText(mFirstNameEdit.getText().toString()+" "+mLastNameEdit.getText().toString());
				}
			}
		});*/
		
		mLoginEdit = (EditText)findViewById(R.id.edit_login);	
		mLoginEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					if (mLoginEdit.getText().toString().contains(" ")) {
						mLoginEdit.setError(getString(R.string.error_space));
					}

			    	if (mLoginEdit.getText().length() <3) {
			    		mLoginEdit.setError(getString(R.string.error_not_enough_character));
					}
					checkUserExist();
					
					mDisplayNameEdit.setText(mLoginEdit.getText().toString());
				}
			}
		});

		mEmailEdit = (EditText)findViewById(R.id.edit_email);	
		mEmailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					if (mEmailEdit.getText().toString().contains(" ")) {
			    		mEmailEdit.setError(getString(R.string.error_space));
					}
					else if (mEmailEdit.getText().length() <3) {
			    		mEmailEdit.setError(getString(R.string.error_not_enough_character));
					}
			    	else if(!isEmailValid(mEmailEdit.getText().toString())){
			    			mEmailEdit.setError(getString(R.string.error_not_have_dog));
			    	}
					checkEmailExist();
				}
			}
		});
		
		
		mPassEdit = (EditText)findViewById(R.id.edit_pass);
		mPassEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					if (mPassEdit.getText().toString().contains(" ")) {
			    		mPassEdit.setError(getString(R.string.error_space));
					}
					if (mPassEdit.getText().length() <6) {
			    		mPassEdit.setError(getString(R.string.error_not_enough_character));
					}
					
					if(mPassEdit.getText().length() >=6 && mPassConfEdit.getText().length() >=6)
			    	{
			    		if(!mPassEdit.getText().toString().equals(mPassConfEdit.getText().toString()))
			    		{
			    			mPassConfEdit.setError(getString(R.string.error_not_equal_pass));
			    		}
			    	}
				}
			}
		});
		
		mPassConfEdit = (EditText)findViewById(R.id.edit_pass_conf);
		mPassConfEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					if (mPassConfEdit.getText().toString().contains(" ")) {
			    		mPassConfEdit.setError(getString(R.string.error_space));
					}
					if (mPassConfEdit.getText().length() <6) {
			    		mPassConfEdit.setError(getString(R.string.error_not_enough_character));
					}
					
					if(mPassEdit.getText().length() >=6 && mPassConfEdit.getText().length() >=6)
			    	{
			    		if(!mPassEdit.getText().toString().equals(mPassConfEdit.getText().toString()))
			    		{
			    			mPassConfEdit.setError(getString(R.string.error_not_equal_pass));
			    		}
			    	}
				}
			}
		});
		
		
		mDisplayNameText = (TextView)findViewById(R.id.display_name);
		mDisplayNameEdit = (EditText)findViewById(R.id.edit_display_name);
		
		mDescText = (TextView)findViewById(R.id.desc_text);
		mDescEdit =  (EditText)findViewById(R.id.edit_desc);
		
		/*mAvatar = (ImageButton)findViewById(R.id.avatar);
		mAvatar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});*/
		
		mPolicy = (CheckBox)findViewById(R.id.confirm_policy);
		mPolicy.setText(Html.fromHtml("Я принимаю <a href='http://webhearme.ru/policy/user_policy.html'>Условия использования</a>"
				+ " и соглашаюсь с политикой конфиденциальности"));
		mPolicy.setMovementMethod(LinkMovementMethod.getInstance());
		
		mRegBtn = (Button)findViewById(R.id.confirm_reg);
		mRegBtn.setOnClickListener(this);
	}
	
	public static boolean isEmailValid(String email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	public void checkPolicy(boolean state){
		mPolicy.setChecked(state);
	}
	
	public void errorRegister()
	{
		ErrorFragment fragment = new ErrorFragment(getString(R.string.login_error));
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void selectImage() {
		final CharSequence[] items = { getString(R.string.take_photo), 
									   getString(R.string.from_gallery),
									   getString(R.string.btn_cancel) };

		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setTitle("Аватар");
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
			            new CustomToast(RegisterActivity.this).showErrorToast(getString(R.string.error_crop_not_support));
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
						new CustomToast(RegisterActivity.this).showErrorToast(getString(R.string.error_crop_not_support));
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
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	mImageUploader.setCropWH(200);
		        	bmp = mImageUploader.cropImg(bmp);
		        	getDecodeCompleteState(bmp);
		        }
		        else{
		        	try{
		        		mImageUploader.setCropWH(200);
			        	mImageUploader.selectFile(data.getData());
		        	}catch(Exception e){
		        		new CustomToast(RegisterActivity.this).showErrorToast(getString(R.string.error_take_picture));
		        	}
		        	
		        	return;
		        }
				
			} else if (requestCode == SELECT_FILE) {

				Bundle extras = data.getExtras();
				Bitmap bmp = null;
		        if (extras != null) {
		        	bmp = extras.getParcelable("data");
		        	mImageUploader.setCropWH(200);
		        	bmp = mImageUploader.cropImg(bmp);
		        	getDecodeCompleteState(bmp);
			        return;
		        }
		        else{
		        	try{
			        	mImageUploader.setCropWH(200);
			        	mImageUploader.selectFile(data.getData());
		        	}
		        	catch(Exception e){
		        		new CustomToast(RegisterActivity.this).showErrorToast(getString(R.string.error_take_picture));
		        	}
		        	return;
		        }
		        
		           
		        
			}
		}
	}

	@Override
	public void onClick(View v) {
		registerUser();
	}
	
	private void registerUser(){
		
		String display_name = mDisplayNameEdit.getText().toString();
		String desc_text = mDescEdit.getText().toString();
		boolean no_error = true;
		int acc_type = 0;
		//mTextEditorWatcher.afterTextChanged(null);
		
		
		/*if(mFirstNameEdit.getError() != null && mFirstNameEdit.getError().length() > 0)
			no_error = false;
		if(mLastNameEdit.getError() != null && mLastNameEdit.getError().length() > 0)
			no_error = false;*/
		if(mEmailEdit.getError() != null && mEmailEdit.getError().length() > 0)
			no_error = false;
		if(mPassConfEdit.getError() != null && mPassConfEdit.getError().length() > 0)
			no_error = false;
		
		
		/*if(mFirstNameEdit.getText().toString().length() == 0){
			mFirstNameEdit.setError(getString(R.string.error_not_enough_character));
			no_error = false;
		}
		if(mLastNameEdit.getText().toString().length() == 0){
			mLastNameEdit.setError(getString(R.string.error_not_enough_character));
			no_error = false;
		}*/
		
		if(mPassConfEdit.getText().toString().length() == 0){
			mPassConfEdit.setError(getString(R.string.error_not_enough_character));
			no_error = false;
		}
			
		if(!mPolicy.isChecked()){
			no_error = false;
			mPolicy.setError("Соглашение не принято");
		}
		if(mEmailEdit.getText().toString().length() == 0){
			mEmailEdit.setError(getString(R.string.error_not_enough_character));
			mEmailEdit.requestFocus();
			no_error = false;
		}
		if(mLoginEdit.getText().toString().length() == 0){
			mLoginEdit.setError(getString(R.string.error_not_enough_character));
			no_error = false;
			mLoginEdit.requestFocus();
		}
		if(!mLoginCorrect){
			no_error = false;
			checkUserExist();
		}
		
		if(mHMAccType.getSelectedItemPosition() == 0){
			acc_type = 0;
			no_error = false;
			mAccTypeText.setError("Выберите тип аккаунта");
			mHMAccType.requestFocus();
		}
		else if(mHMAccType.getSelectedItemPosition() == 1)
			acc_type = 1;
		else if(mHMAccType.getSelectedItemPosition() == 2)
			acc_type = 2;
		
		if(no_error)
		{
			/*try {
				display_name = URLEncoder.encode(display_name,"UTF-8");
			   } catch (UnsupportedEncodingException e) {
			       e.printStackTrace();
			   } 		
			*/
			try{
				desc_text = URLEncoder.encode(desc_text,"UTF-8");
			}catch (UnsupportedEncodingException e) {
			       e.printStackTrace();
			} 	
			
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				//imm.hideSoftInputFromWindow(mFirstNameEdit.getWindowToken(), 0);
				//imm.hideSoftInputFromWindow(mLastNameEdit.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPassEdit.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPassConfEdit.getWindowToken(), 0);
				
			new AsyncTask<String, Void, Intent>() {

				@Override
				protected Intent doInBackground(String... params) {
					// TODO Auto-generated method stub
					HashMap<String, String> token_data;
					ServerAuth serverAuth =new ServerAuth();
					String access_token = null;
					Bundle data = new Bundle();
					try{
						token_data = serverAuth.userSignUp(
								null, 
								null,
								mLoginEdit.getText().toString(),
								mEmailEdit.getText().toString(), 
								mPassEdit.getText().toString(), 
								mHMAccType.getSelectedItemPosition(),
								mDisplayNameEdit.getText().toString(),
								mAvatarBitmap,
								mDescEdit.getText().toString(),
								AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
						
						if(token_data.get("error") == null){
							data.putBoolean(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
							data.putString(AccountManager.KEY_ACCOUNT_NAME, mLoginEdit.getText().toString());
		                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
		                    data.putString(AccountGeneral.KEY_SOCIAL_TYPE, "0");
		                    data.putString(AccountManager.KEY_AUTHTOKEN, token_data.get(AccountManager.KEY_AUTHTOKEN));
		                    data.putString(AccountGeneral.KEY_REFRESH_TOKEN, token_data.get(AccountGeneral.KEY_REFRESH_TOKEN));
		                    //data.putString(LoginActivity.PARAM_USER_PASS, mPassEdit.getText().toString());
		                    data.putString(AccountGeneral.KEY_IMAGE_URL, token_data.get(AccountGeneral.KEY_IMAGE_URL));
		                    data.putString(AccountGeneral.KEY_DISPLAY_NAME, mDisplayNameEdit.getText().toString());
		                    data.putString(AccountGeneral.KEY_DESC, mDescText.getText().toString());
						}
						else{
							data.putString(LoginActivity.KEY_ERROR_MESSAGE, "error");
						}
					}catch(Exception e){
						Log.e("hm", e.getMessage());
						data.putString(LoginActivity.KEY_ERROR_MESSAGE, "error");
					}
					
					final Intent res = new Intent();
	                res.putExtras(data);
	                return res;
				}

				@Override
				protected void onPostExecute(Intent intent) {
					// TODO Auto-generated method stub
					if (intent.hasExtra(LoginActivity.KEY_ERROR_MESSAGE)) {
						mRegisterProgress.setVisibility(View.GONE);
						mRegBtn.setVisibility(View.VISIBLE);
	                    new CustomToast(RegisterActivity.this).showErrorToast(getString(R.string.error_register));
	                } else {
	                    setResult(RESULT_OK, intent);
	                    finish();
	                }
				}
				
				
				
			}.execute();
			
			mRegisterProgress.setVisibility(View.VISIBLE);
			mRegBtn.setVisibility(View.GONE);
		}
		
	}
	
	private void checkUserExist(){
		
		if(mTaskLoginCheck == null)
			mTaskLoginCheck = new JSONParser(RegisterActivity.this, null);
		
		if(mTaskLoginCheck.getStatus() != Status.RUNNING){
			mTaskLoginCheck = new JSONParser(RegisterActivity.this, null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("url",  Urls.USER);
			params.put("tag", "check_user_on_exist");
			params.put("user_name", mLoginEdit.getText().toString());			

			
			mTaskLoginCheck.execute(params);
		}
	}
	
	private void checkEmailExist(){
		
		if(mTaskLoginCheck == null)
			mTaskLoginCheck = new JSONParser(RegisterActivity.this, null);
		
		if(mTaskLoginCheck.getStatus() != Status.RUNNING){
			mTaskLoginCheck = new JSONParser(RegisterActivity.this, null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("url",  Urls.USER);
			params.put("tag", "check_email_on_exist");
			params.put("email", mEmailEdit.getText().toString());			

			
			mTaskLoginCheck.execute(params);
		}
	}
	
	@Override
	public void getUserStateComplete(JSONObject obj) {
		// TODO Auto-generated method stub
		boolean error = false;
		int error_type = 0;
		try {
			error = obj.getBoolean("error");
			error_type = obj.getInt("error_type");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(error){
			CustomToast toast = new CustomToast(RegisterActivity.this);
			if(error_type == 1)
				toast.showErrorToast(getString(R.string.no_connection));
			else if(error_type == 2)
				toast.showErrorToast(getString(R.string.error_process_data));
			return;
		}
		UserFunctions userFunctions = new UserFunctions();
		try
		{
			String res_success = obj.getString(LoginKeys.KEY_SUCCESS);
			String res_error = obj.getString(LoginKeys.KEY_ERROR);
			if(Integer.parseInt(res_success) == 1)
			{
					DatabaseHandler db = new DatabaseHandler(getApplicationContext());
					JSONObject json_user = obj.getJSONObject("user");
					
					userFunctions.logoutUser(getApplicationContext());
					db.addUser(json_user.getString(LoginKeys.KEY_ID), 
							json_user.getString(LoginKeys.KEY_ACCOUNT_TYPE), 
							json_user.getString(LoginKeys.KEY_SOCIAL_TYPE),
							json_user.getString(LoginKeys.KEY_UID), 
							json_user.getString(LoginKeys.KEY_LOGIN), 
							json_user.getString(LoginKeys.KEY_EMAIL), 
							json_user.getString(LoginKeys.KEY_FULL_NAME), 
							json_user.getString(LoginKeys.KEY_SHOW_FULL_NAME),
							json_user.getString(LoginKeys.KEY_DESC_TEXT),
							"", 
							json_user.getString(LoginKeys.KEY_CREATE_DATE));
					
					
					//Intent MainActivity = new Intent(getApplicationContext(), MainControlBarActivity.class);
                    // Close all views before launching Dashboard
					//MainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					setResult(RESULT_OK);
					finish();
			}
			else if(Integer.parseInt(res_error) != 0)
			{
				ErrorFragment fragment = new ErrorFragment(obj.getString(LoginKeys.KEY_ERROR_MSG));
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
				if(Integer.parseInt(res_error) == 2)
					mEmailEdit.setError(obj.getString(LoginKeys.KEY_ERROR_MSG));
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub
		boolean error = false;
		int error_type = 0;
		try {
			error = obj.getBoolean("error");
			error_type = obj.getInt("error_type");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(error){
			CustomToast toast = new CustomToast(RegisterActivity.this);
			if(error_type == 1)
				toast.showErrorToast(getString(R.string.no_connection));
			else if(error_type == 2)
				toast.showErrorToast(getString(R.string.error_process_data));
			return;
		}
		
		try {
			String tag = obj.getString("tag");
			if(tag.equals("check_user_on_exist")){
				if(obj.getInt("is_user_exist") == 1){
					mLoginEdit.setError(getString(R.string.error_user_exist));
					mLoginEdit.requestFocus();
				}
				else
					mLoginCorrect = true;
			}
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			new CustomToast(RegisterActivity.this).showErrorToast("Ошибка");
			e.printStackTrace();
		}
	}

	@Override
	public void getDecodeCompleteState(Bitmap bm) {
		// TODO Auto-generated method stub
		mAvatarBitmap = bm;
		//mAvatar.setImageBitmap(bm);
	}
	

}
