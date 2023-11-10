package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.tags.CommonTags;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class UserDetailsActivity extends AppCompatActivity implements ConnectionCallbacks,OnConnectionFailedListener, GetJSONListener{
	
	CollapsingToolbarLayout collapsingToolbar;
	int mutedColor = R.attr.colorPrimary;
	
	private GoogleApiClient mGoogleApiClient;
	private AccountManager mAccountManager;
	
	private Integer mAccPos;
	private DatabaseHandler mDBUser;
	
	private FloatingActionButton mFavFab;

	private ImageView mAvatarImage;
	
	private HashMap<String, String> mTempParams = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mAccPos = getIntent().getExtras().getInt("acc_pos");
		
        Integer user_id = getIntent().getExtras().getInt("user_id");
        String display_name = getIntent().getExtras().getString("user_display_name");
        String image_url = getIntent().getExtras().getString("user_img_url");
        String desc_text = getIntent().getExtras().getString("desc_text");
        
        GoogleSignInOptions plusOptions = new GoogleSignInOptions.Builder().build();
				//.addActivityTypes(MomentUtil.ACTIONS)
        
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, plusOptions)
        //.addScope(new Scope(Scopes.PROFILE))
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
        
        mAccountManager = AccountManager.get(this);
        mDBUser = new DatabaseHandler(UserDetailsActivity.this);
        
        mFavFab = (FloatingActionButton)findViewById(R.id.fav_fab);
        mFavFab.setTag(user_id);
        mFavFab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mAccPos == -1){
					new CustomToast(UserDetailsActivity.this).show(getString(R.string.toast_need_auth));
					return;
				}
				if(mGoogleApiClient.isConnected() || mAccPos != -1){
					new AccessTokenActionsTask().execute(v);
				}
			}
		});
        
        for (Integer fav_user_id : mDBUser.getFavUsers()) {
        	if(user_id.equals(fav_user_id)){
        		mFavFab.setImageResource(R.drawable.icon_active_fav_user);
        		break;
        	}
		}

        
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(display_name);
        
        mAvatarImage = (ImageView)findViewById(R.id.avatar_image);

		Glide.with(this).load(image_url).into(mAvatarImage);
        
        TextView desc = (TextView)findViewById(R.id.desc_text);
        desc.setText(desc_text);
	}
	
	private class AccessTokenActionsTask extends AsyncTask<View, Void, String> {

		private String social_type;
		protected View view;
		
	    @Override
	    protected String doInBackground(View... params) {
	    	view = params[0];
	    	Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	    	social_type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = "";//Plus.AccountApi.getAccountName(mGoogleApiClient);
		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(UserDetailsActivity.this, account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, UserDetailsActivity.this, null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, UserDetailsActivity.this, null, null);
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
	    	
	        if (access_token != null) {
	        	if(access_token.equals("no_conn")){
		    		new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
		    		return;
		    	}
	        	String url;
	        	if(social_type.equals("1"))
	        		url = Urls.G_USER;
	        	else
	        		url = Urls.USER;
	        	clickAction(view, url, access_token);
	        	
	        } else {
	        	Intent intent = new Intent();
				intent.putExtra("error", "expired_token");
				return;
	        }
	    }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_details, menu);
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
	protected void onStart() {
	    super.onStart();

	    // Connect to Drive and Google+
	    //mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
	    super.onStop();
	    if (mGoogleApiClient.isConnected()) {
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
	
	private void clickAction(View v, String url, String access_token){
		JSONParser asyncPoster = new JSONParser(UserDetailsActivity.this, v);
		HashMap<String, String> params = new HashMap<String, String>();
		
		Integer user_id = (Integer)v.getTag();
		
		if(user_id == null){
			new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.error_not_guest_to_fav));
			return;
		}
		else if(user_id == 1){
			new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.error_not_yourself_to_fav));
			return;
		}
		
		params.put("url", url);
		params.put("access_token", access_token);
		params.put("fav_user_id", String.valueOf(user_id));

		boolean is_user_already_exist = false;
		for (Integer fav_user_id : mDBUser.getFavUsers()) {
			if (fav_user_id.equals(user_id)){
				is_user_already_exist = true;
				break;
			}
		}
		if(!is_user_already_exist)
		{
			params.put(CommonTags.TAG, CommonTags.FAV_USER_ADD);
			mTempParams.putAll(params);
			asyncPoster.execute(params);
		}
		else
		{
			params.put(CommonTags.TAG, CommonTags.FAV_USER_REMOVE);
			mTempParams.putAll(params);
			asyncPoster.execute(params);
		}
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub
		try {
			if(!obj.isNull("error")){
				if(!obj.getString("error").equals("no_conn"))
					new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
				else if(!obj.getString("error").equals("error_parse"))
					new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.error_load_data));
				else if(obj.getString("error").equals("expired_token")){
					/*new AsyncTask<Void, Void, HashMap<String, String>>() {

						@Override
						protected HashMap<String, String> doInBackground(Void... params) {
							// TODO Auto-generated method stub
							return Oauth2Util.getAuthToken(mTokenData.get(AccountGeneral.KEY_REFRESH_TOKEN));
						}

						@Override
						protected void onPostExecute(HashMap<String, String> result) {
							// TODO Auto-generated method stub
							if(result.get("error") != null && result.get("error").equals("expired_token")){
								mTempParams.clear();
								Intent intent = new Intent();
								intent.putExtra("error", "expired_token");
								setResult(MainControlBarActivity.EXPIRED_REFRESH_TOKEN, intent);
								return;
							}
							JSONParser parser = new JSONParser(UserDetailsActivity.this, null);	
							parser.execute(mTempParams);
							
							if(result.get(AccountGeneral.KEY_REFRESH_TOKEN) != null){
								Intent intent = new Intent();
								intent.putExtra(AccountManager.KEY_AUTHTOKEN, result.get(AccountManager.KEY_AUTHTOKEN));
								intent.putExtra(AccountGeneral.KEY_REFRESH_TOKEN, result.get(AccountGeneral.KEY_REFRESH_TOKEN));
								setResult(MainControlBarActivity.UPDATE_REFRESH_TOKEN, intent);
							}
							super.onPostExecute(result);
						}
					}.execute();*/
            	}
			}
			else if(obj.getBoolean("success")){
				String tag = obj.getString("tag");
				if(tag.equals(CommonTags.FAV_USER_ADD) || tag.equals(CommonTags.FAV_USER_REMOVE)){
					Integer user_id = (Integer)v.getTag();
					
					if(tag.equals(CommonTags.FAV_USER_ADD)){
						mDBUser.addFavUser(user_id);
						mFavFab.setImageResource(R.drawable.icon_active_fav_user);
					}
					else if(tag.equals(CommonTags.FAV_USER_REMOVE)){
						mDBUser.removeFavUser(user_id);
						mFavFab.setImageResource(R.drawable.icon_deactive_fav_user);
					}
				}
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			new CustomToast(UserDetailsActivity.this).showErrorToast(getString(R.string.error));
			e.printStackTrace();
		}
		
		mTempParams.clear();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
}
