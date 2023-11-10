package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.library.UserFunctions.UserLoginListener;
import com.donearh.hearme.library.UserFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class RegisterActivity extends ActionBarActivity implements OnClickListener,
																	UserLoginListener
{
	
	private Context mContext;
	
	private Long mUserUid;
	
	private String mLogin;
	private String mEncodeFullName;
	private String mEncodePass;
	
	private EditText mLoginEdit;
	private EditText mPassEdit;
	private EditText mPassConfEdit;
	private EditText mEmailEdit;
	private EditText mFullNameEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mLoginEdit = (EditText)findViewById(R.id.edit_login);
		mLoginEdit.addTextChangedListener(new TextWatcher() {
			
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
				
				if (mLoginEdit.getText().length() <4) {
					mLoginEdit.setError(getString(R.string.error_not_enough_character));
				}
				else
				{
					mLoginEdit.setError(null);
				}
				if (mLoginEdit.getText().toString().contains(" ")) {
					mLoginEdit.setError(getString(R.string.error_space));
				}
			}
		});
		
		mPassEdit = (EditText)findViewById(R.id.edit_pass);
		mPassEdit.addTextChangedListener(new TextWatcher() {
			
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
		});
		
		mPassConfEdit = (EditText)findViewById(R.id.edit_pass_conf);
		mPassConfEdit.addTextChangedListener(new TextWatcher() {
			
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
		});
		
		mEmailEdit = (EditText)findViewById(R.id.edit_email);
		mEmailEdit.addTextChangedListener(new TextWatcher() {
			
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
				if (mEmailEdit.getText().toString().contains(" ")) {
		    		mEmailEdit.setError(getString(R.string.error_space));
				}

		    	if (mEmailEdit.getText().length() <3) {
		    		mEmailEdit.setError(getString(R.string.error_not_enough_character));
				}
		    	else
		    	{
		    		if (!mEmailEdit.getText().toString().contains("@")) {
			    		mEmailEdit.setError(getString(R.string.error_not_have_dog));
					}
		    	}
			}
		});
		
		mFullNameEdit = (EditText)findViewById(R.id.edit_full_name);
		
		Button tRegBtn = (Button)findViewById(R.id.confirm_reg);
		tRegBtn.setOnClickListener(this);
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
		getMenuInflater().inflate(R.menu.register, menu);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String login = mLoginEdit.getText().toString();
		String tPass = mPassEdit.getText().toString();
		String tPassConf = mPassConfEdit.getText().toString();
		String email = mEmailEdit.getText().toString();
		String full_name = mFullNameEdit.getText().toString();
		
		boolean no_error = true;
		//mTextEditorWatcher.afterTextChanged(null);
		
		if(mLoginEdit.getError() != null)
		{
			if(mLoginEdit.getError().length() > 0)
				no_error = false;
		}
		if(mPassConfEdit.getError() != null)
		{
			if(mPassConfEdit.getError().length() > 0)
				no_error = false;
		}
		if(mEmailEdit.getError() != null)
		{
			if(mEmailEdit.getError().length() > 0)
				no_error = false;
		}
			
		if(no_error)
		{
			try {
				full_name = URLEncoder.encode(full_name,"UTF-8");
			   } catch (UnsupportedEncodingException e) {
			       e.printStackTrace();
			   } 			
			
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mLoginEdit.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPassEdit.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(mPassConfEdit.getWindowToken(), 0);
			
				UserFunctions fragment = new UserFunctions();
				fragment.setListener(this);
				Bundle args = new Bundle();
				args.putString("circle_text", getString(R.string.register));
				fragment.setArguments(args);
				fragment.registerUser(login, 
						email, 
						tPass, 
						full_name, 
						"");
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
		}
		else
		{
			ErrorFragment fragment = new ErrorFragment("Неправильные данные");
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
		}
		
	}

	@Override
	public void getUserStateComplete(JSONObject obj) {
		// TODO Auto-generated method stub
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
							json_user.getString(LoginKeys.KEY_UID), 
							json_user.getString(LoginKeys.KEY_LOGIN), 
							json_user.getString(LoginKeys.KEY_EMAIL), 
							json_user.getString(LoginKeys.KEY_FULL_NAME), 
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
					mLoginEdit.setError(obj.getString(LoginKeys.KEY_ERROR_MSG));
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

}
