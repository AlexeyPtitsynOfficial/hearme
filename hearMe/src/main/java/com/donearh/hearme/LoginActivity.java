package com.donearh.hearme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.account.ServerAuth;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.library.UserFunctions;
import com.donearh.hearme.library.UserFunctions.UserLoginListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;

public class LoginActivity extends AccountAuthenticatorActivity  implements UserLoginListener,
																ConnectionCallbacks, 
																OnConnectionFailedListener,
																GetJSONListener
{
	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    private final int REQ_SIGNUP = 1;
    public final static String PARAM_USER_PASS = "USER_PASS";

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	
	private ProgressDialog mConnectionProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private AccountManager mAccountManager;
    
    private boolean mIntentInProgress;
    
    private InputStream is = null;
    
    private String mAuthTokenType;
    
    private ConnectionResult mConnectionResult;
    
    private JSONParser mTaskLoginCheck;
    
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
	
	private SavedData mSavedData;
	private Integer mUserUid;
	private String mEncodeLogin;
	private String mEncodePass;
	
	private TextView mErrorText;
	private EditText mLoginEdit;
	private EditText mPassEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
        
		mSavedData = SavedData.getInstance(LoginActivity.this);
		
		GoogleSignInOptions plusOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
				//.addActivityTypes(MomentUtil.ACTIONS).build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(Auth.GOOGLE_SIGN_IN_API, plusOptions)
	        //.addScope(Plus.SCOPE_PLUS_LOGIN)
	        //.addScope(new Scope(Scopes.PROFILE))
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
		
		
		mAccountManager = AccountManager.get(this);
		
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
		if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
		
		mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Авторизация...");
		
		mErrorText = (TextView)findViewById(R.id.error_text);
		mLoginEdit = (EditText)findViewById(R.id.login_edit);
		mPassEdit = (EditText)findViewById(R.id.pass_edit);
		
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
				
				if(mLoginEdit.getError() != null)
					mLoginEdit.setError(null);
				if (mLoginEdit.getText().toString().contains(" ")) {
					mLoginEdit.setError(getString(R.string.error_space));
				}
			}
		});
		
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
				if(mPassEdit.getError() != null)
					mPassEdit.setError(null);
				/*if (mPassEdit.getText().toString().contains(" ")) {
		    		mPassEdit.setError(getString(R.string.error_space));
				}
				if (mPassEdit.getText().length() <6) {
		    		mPassEdit.setError(getString(R.string.error_not_enough_character));
				}*/
			}
		});
		SignInButton google_enter = (SignInButton)findViewById(R.id.sign_in_button);
		google_enter.setVisibility(View.GONE);
		google_enter.setScopes(plusOptions.getScopeArray());
		google_enter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new PolicyDialog().show(getSupportFragmentManager(), "");
				
				/*if(!mGoogleApiClient.isConnected()){
					if(mConnectionResult == null){
						mConnectionProgressDialog.show();
					}else{
						try{
							mConnectionResult.startResolutionForResult(LoginActivity.this, REQUEST_CODE_RESOLVE_ERR);
						}catch(SendIntentException e){
							mConnectionResult = null;
							mShouldResolve = true;
							mGoogleApiClient.connect();
						}
					}
				}*/
			}
		});
		Button tJoinButton = (Button)findViewById(R.id.join_btn);
		tJoinButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(mLoginEdit.getText().toString().trim().length() == 0)
				{
					mLoginEdit.setError(getString(R.string.error_not_enough_character));
					return;
				}
				if(mPassEdit.getText().toString().trim().length() == 0)
				{
					mPassEdit.setError(getString(R.string.error_not_enough_character));
					return;
				}
				
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mLoginEdit.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(mPassEdit.getWindowToken(), 0);
				// TODO Auto-generated method stub
				if(mLoginEdit.length() != 0 && mPassEdit.length() != 0)
				{					
					signIn();
					return;
					
				}
				
				
				
			}
		});
		
		Button tRegButton = (Button)findViewById(R.id.register_btn);
		tRegButton.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v) 
			{
				Intent sign_up = new Intent(LoginActivity.this, RegisterActivity.class);
				sign_up.putExtras(getIntent().getExtras());
				startActivityForResult(sign_up, REQ_SIGNUP);
			}
		});
	}
	
	public void startGoogleSign(){
		mShouldResolve = true;
		//mGoogleApiClient.connect();
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, MainControlBarActivity.RC_SIGN_IN);
		mConnectionProgressDialog.show();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	    if (!mIntentInProgress && result.hasResolution()) {
	        try {
	          mIntentInProgress = true;
	          startIntentSenderForResult(result.getResolution().getIntentSender(),
	        		  MainControlBarActivity.RC_SIGN_IN, null, 0, 0, 0);
	        } catch (SendIntentException e) {
	          // The intent was canceled before it was sent.  Return to the default
	          // state and attempt to connect to get an updated ConnectionResult.
	          mIntentInProgress = false;
	          //mGoogleApiClient.connect();
	        }
	      }
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle bundle) {
		// TODO Auto-generated method stub
		//Log.d("hm", "onConnected:" + bundle);
	    mShouldResolve = false;
	    
	    
	    // Show the signed-in UI
	    //new GetIdTokenTask().execute();
	   // finish();
	}
	
	
	private void signIn(){
		
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mLoginEdit.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mPassEdit.getWindowToken(), 0);
			
		mErrorText.setVisibility(View.GONE);
		final String login = mLoginEdit.getText().toString();
		final String pass = mPassEdit.getText().toString();
		
		final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
		
		if(login.length() == 0){
			mLoginEdit.setError(getString(R.string.error_enter_login));
		}
		if(pass.length() == 0){
			mPassEdit.setError(getString(R.string.error_enter_pass));
		}
		mConnectionProgressDialog.show();
		new AsyncTask<String, Void, Intent>() {

			@Override
			protected Intent doInBackground(String... params) {
				// TODO Auto-generated method stub
				ServerAuth serverAuth =new ServerAuth();
				String auth_token = null;
				HashMap<String, String> token_data = new HashMap<String, String>();
				
				Bundle data = new Bundle();
				try{
					token_data = serverAuth.userSignIn(login, pass, mAuthTokenType);
					data.putString(AccountManager.KEY_ACCOUNT_NAME, login);
					data.putString(AccountManager.KEY_AUTHTOKEN, token_data.get(AccountManager.KEY_AUTHTOKEN));
					data.putString(AccountGeneral.KEY_REFRESH_TOKEN, token_data.get(AccountGeneral.KEY_REFRESH_TOKEN));
					data = getUserData(data, token_data.get(AccountManager.KEY_AUTHTOKEN));
				}catch(Exception e){
					data.putString(KEY_ERROR_MESSAGE, e.getMessage());
				}
				
				final Intent res = new Intent();
                res.putExtras(data);
                return res;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				// TODO Auto-generated method stub
				
				if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
					mConnectionProgressDialog.dismiss();
					if(intent.getStringExtra(KEY_ERROR_MESSAGE).toString().equals("401"))
						mErrorText.setVisibility(View.VISIBLE);
					else
						new CustomToast(LoginActivity.this).showErrorToast(getString(R.string.no_connection));
                } else {
                    finishLogin(intent);
                }
			}
			
		}.execute();
	}
	
	private Bundle getUserData(Bundle data, String access_token) throws Exception {
		
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("tag", "sign_in");
	    params.put("access_token", access_token);
		
		String response = "";
		try{
			URL url = new URL(Urls.USER);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(params));
			writer.flush();
			writer.close();
			os.close();
			
			conn.connect();  
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                response= sb.toString();
                
                JSONObject json = null;
    			try {
    				json = new JSONObject(response);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    			}
    			
    			if(json.getInt("success") == 1){
    				JSONObject json_user = json.getJSONObject("user");
    				data.putString(AccountGeneral.KEY_HM_ACC_TYPE, json_user.getString(AccountGeneral.KEY_HM_ACC_TYPE));
    				data.putString(AccountGeneral.KEY_SOCIAL_TYPE, json_user.getString(AccountGeneral.KEY_SOCIAL_TYPE));
    				data.putString(AccountGeneral.KEY_DISPLAY_NAME, json_user.getString(AccountGeneral.KEY_DISPLAY_NAME));
    				data.putString(AccountGeneral.KEY_DESC, json_user.getString(AccountGeneral.KEY_DESC));
    				data.putString(AccountGeneral.KEY_IMAGE_URL, json_user.getString(AccountGeneral.KEY_IMAGE_URL));
    				
    				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
    				JSONObject json_data = null;
					try{
						JSONArray json_fav_users = json.getJSONArray("fav_users");
						for(int i=0; i<json_fav_users.length(); i++){
							json_data = json_fav_users.getJSONObject(i);
							db.addFavUser(json_data.getInt("favorite_user_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					
					try{
						JSONArray json_fav_ads = json.getJSONArray("fav_ads");
						for(int i=0; i<json_fav_ads.length(); i++){
							json_data = json_fav_ads.getJSONObject(i);
							db.addFavAd(json_data.getInt("ad_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
    			}
    			
    			return data;
    			
            }
            else {
                response="";

                throw new HttpException(responseCode+"");
            }
			
			//is = conn.getInputStream();
		}finally{
			if (is != null) {
	            is.close();
	        } 
		}
	}
	
	private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.login, menu);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("hm", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

	    if (requestCode == MainControlBarActivity.RC_SIGN_IN) {
	    	mIntentInProgress = false;
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
			mConnectionProgressDialog.dismiss();
	    }
	    
	    if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
	    
	}


	private void handleSignInResult(GoogleSignInResult result) {
		/*Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			new CheckkIdToken().execute(acct);
			mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
			updateUI(true);
		} else {
			// Signed out, show unauthenticated UI.
			updateUI(false);
		}*/
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
							json_user.getString(LoginKeys.KEY_ACCOUNT_TYPE),
							json_user.getString(LoginKeys.KEY_SOCIAL_TYPE),
							json_user.getString(LoginKeys.KEY_UID), 
							json_user.getString(LoginKeys.KEY_LOGIN),
							json_user.getString(LoginKeys.KEY_EMAIL), 
							json_user.getString(LoginKeys.KEY_FULL_NAME), 
							json_user.getString(LoginKeys.KEY_SHOW_FULL_NAME),
							json_user.getString(LoginKeys.KEY_DESC_TEXT), 
							json_user.getString(LoginKeys.KEY_IMAGE_URL), 
							json_user.getString(LoginKeys.KEY_CREATE_DATE));
					
					JSONObject json_data = null;
					try{
						JSONArray json_fav_users = obj.getJSONArray("fav_users");
						for(int i=0; i<json_fav_users.length(); i++){
							json_data = json_fav_users.getJSONObject(i);
							db.addFavUser(json_data.getInt("favorite_user_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					
					ArrayList<Integer> ad_array = new ArrayList<Integer>();
					try{
						JSONArray json_fav_ads = obj.getJSONArray("fav_ads");
						for(int i=0; i<json_fav_ads.length(); i++){
							json_data = json_fav_ads.getJSONObject(i);
							ad_array.add(json_data.getInt("ad_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					
					mSavedData.saveFavoriteAds(ad_array);
					
					setResult(MainControlBarActivity.CHECK_PROFILE);
					finish();
			}
			else if(Integer.parseInt(res_error) != 0)
			{
				mErrorText.setVisibility(View.VISIBLE);
				//ErrorFragment fragment = new ErrorFragment(obj.getString(LoginKeys.KEY_ERROR_MSG));
				//FragmentManager fragmentManager = getSupportFragmentManager();
				//fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	

	/*private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

	    @Override
	    protected String doInBackground(Void... params) {
	        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
	        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
	        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
	        try {
	            return GoogleAuthUtil.getToken(LoginActivity.this, account, "oauth2:"+Scopes.PROFILE);
	        } catch (IOException e) {
	            Log.e("hm", "Error retrieving ID token.", e);
	            return null;
	        } catch (GoogleAuthException e) {
	            Log.e("hm", "Error retrieving ID token.", e);
	            return null;
	        }
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        if (result != null) {
	        	new CheckkIdToken().execute(result);
	        } else {
	          // There was some error getting the ID Token
	          // ...
	        }
	    }

	}*/
	
	private class CheckkIdToken extends AsyncTask<GoogleSignInAccount, Void, Intent> {

		protected GoogleSignInAccount mAcc;
		private String result = null;
		private InputStream is = null;
		private StringBuilder sb = null;
		
		private String return_result ="0";
	    @Override
	    protected Intent doInBackground(GoogleSignInAccount... acc) {

			mAcc = acc[0];
	    	final Intent res = new Intent();
	    	
	    	HttpClient httpClient = new DefaultHttpClient();
        	HttpPost httpPost = new HttpPost(Urls.G_USER);
        	Bundle data = new Bundle();
        	try {
        	    List nameValuePairs = new ArrayList(1);
        	    nameValuePairs.add(new BasicNameValuePair("tag", "sign"));
        	    nameValuePairs.add(new BasicNameValuePair("id_token", mAcc.getIdToken()));
        	    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        	    HttpResponse response = httpClient.execute(httpPost);
        	    int statusCode = response.getStatusLine().getStatusCode();
        	    HttpEntity entity = response.getEntity();
        	    is = entity.getContent();
        	} catch (ClientProtocolException e) {
        	    Log.e("hm", "Error sending ID token to backend.", e);
        	} catch (IOException e) {
        	    Log.e("hm", "Error sending ID token to backend.", e);
        	}
        	
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
			   data.putString(KEY_ERROR_MESSAGE, e.getMessage());
			   res.putExtras(data);
	           return res;
			}
			
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("JSON Parser", "Error parsing data " + e.toString());
				//Log.e("Error pars", result);
				data.putString(KEY_ERROR_MESSAGE, e.getMessage());
				res.putExtras(data);
	            return res;
			}
			
			
			try {
	            // SUCCESS
	            int success = json.getInt("success");
	
	            if(json.getString("success").equals("1")){
	            	if (acc[0] != null){
	            		JSONObject json_user = json.getJSONObject("user");
	            		data.putString(AccountManager.KEY_ACCOUNT_NAME, mAcc.getGivenName());
						data.putString(AccountManager.KEY_AUTHTOKEN, mAcc.getIdToken());
		            	data.putString(AccountGeneral.KEY_HM_ACC_TYPE, "1");
		            	data.putString(AccountGeneral.KEY_SOCIAL_TYPE, "1");
						data.putString(AccountGeneral.KEY_DISPLAY_NAME, mAcc.getDisplayName());
						data.putString(AccountGeneral.KEY_DESC, "");
						data.putString(AccountGeneral.KEY_IMAGE_URL, json_user.getString(AccountGeneral.KEY_IMAGE_URL));
	            	}
	            }
	            if(json.getString("success").equals("2")){
		            JSONObject json_user = json.getJSONObject("user");
		            data.putString(AccountManager.KEY_ACCOUNT_NAME, mAcc.getGivenName());
					data.putString(AccountManager.KEY_AUTHTOKEN, mAcc.getIdToken());
		            data.putString(AccountGeneral.KEY_HM_ACC_TYPE, json_user.getString(AccountGeneral.KEY_HM_ACC_TYPE));
		            data.putString(AccountGeneral.KEY_SOCIAL_TYPE, "1");
					data.putString(AccountGeneral.KEY_DISPLAY_NAME, json_user.getString(AccountGeneral.KEY_DISPLAY_NAME));
					data.putString(AccountGeneral.KEY_DESC, json_user.getString(AccountGeneral.KEY_DESC));
					data.putString(AccountGeneral.KEY_IMAGE_URL, json_user.getString(AccountGeneral.KEY_IMAGE_URL));
					
					DatabaseHandler db = new DatabaseHandler(getApplicationContext());
    				JSONObject json_data = null;
					try{
						JSONArray json_fav_users = json.getJSONArray("fav_users");
						for(int i=0; i<json_fav_users.length(); i++){
							json_data = json_fav_users.getJSONObject(i);
							db.addFavUser(json_data.getInt("favorite_user_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					
					try{
						JSONArray json_fav_ads = json.getJSONArray("fav_ads");
						for(int i=0; i<json_fav_ads.length(); i++){
							json_data = json_fav_ads.getJSONObject(i);
							db.addFavAd(json_data.getInt("ad_id"));
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
	            }
	            
            }catch (JSONException e) {
	            e.printStackTrace();
	        }
			
			
            res.putExtras(data);
            return res;
	    }

		@Override
		protected void onPostExecute(Intent intent) {
			// TODO Auto-generated method stub
			if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
				if (mGoogleApiClient.isConnected() && mAcc != null) {
			        mGoogleApiClient.disconnect();
			    }
				mConnectionProgressDialog.dismiss();
			}
			else if (mAcc != null) {
					finishLogin(intent);
			}
		}
	    
	    
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub		
		try {
			if(!obj.isNull("error")){
				if(!obj.getString("error").equals("no_conn"))
					new CustomToast(LoginActivity.this).showErrorToast(getString(R.string.no_connection));
				else if(!obj.getString("error").equals("error_parse"))
					new CustomToast(LoginActivity.this).showErrorToast(getString(R.string.error_load_data));
			}else{
				String tag = obj.getString("tag");
				if(tag.equals("check_social_login_on_exist")){
					if(obj.getInt("is_user_exist") == 1){
						setResult(MainControlBarActivity.CHECK_PROFILE);
						finish();
					}
					else{
						/*UserFunctions user_func = new UserFunctions();
						user_func.setListener(this);
				        user_func.registerUser(currentPerson.getId(), 
				        						1,1,
				        						currentPerson.getNickname()
				        						, Plus.AccountApi.getAccountName(mGoogleApiClient)
				        						, null
				        						, currentPerson.getDisplayName()
				        						, null
				        						, null);*/
					}
				}
			}
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			new CustomToast(LoginActivity.this).showErrorToast(getString(R.string.error_unknown));
			e.printStackTrace();
		}
	}
	
	private void finishLogin(Intent intent){
		String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("hm", "> finishLogin > addAccountExplicitly");
            String access_token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            Bundle userData = new Bundle();
            userData.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, access_token);
            mAccountManager.setUserData(account, AccountGeneral.KEY_REFRESH_TOKEN, intent.getStringExtra(AccountGeneral.KEY_REFRESH_TOKEN));
            mAccountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, accountName);
            mAccountManager.setUserData(account, AccountGeneral.KEY_HM_ACC_TYPE, intent.getStringExtra(AccountGeneral.KEY_HM_ACC_TYPE));
            mAccountManager.setUserData(account, AccountGeneral.KEY_SOCIAL_TYPE, intent.getStringExtra(AccountGeneral.KEY_SOCIAL_TYPE));
            mAccountManager.setUserData(account, AccountGeneral.KEY_DISPLAY_NAME, intent.getStringExtra(AccountGeneral.KEY_DISPLAY_NAME));
            mAccountManager.setUserData(account, AccountGeneral.KEY_IMAGE_URL, intent.getStringExtra(AccountGeneral.KEY_IMAGE_URL));
            mAccountManager.setUserData(account, AccountGeneral.KEY_DESC, intent.getStringExtra(AccountGeneral.KEY_DESC));
        } else {
            Log.d("hm", "> finishLogin > setPassword");
            //mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
	}
}
