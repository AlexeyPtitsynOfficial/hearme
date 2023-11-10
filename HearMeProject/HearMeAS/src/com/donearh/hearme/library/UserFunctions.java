package com.donearh.hearme.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.donearh.hearme.InsertDataFragment;
import com.donearh.hearme.R;
import com.donearh.hearme.URLWithParams;
import com.donearh.hearme.Urls;
import com.donearh.hearme.library.JSONParser.GetJSONListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserFunctions extends Fragment implements GetJSONListener{
	
	private JSONParser jsonParser;
	
	UserLoginListener mUserLoginListener;
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	private String mCircleText;
	
	public interface UserLoginListener
	{
		public void getUserStateComplete(JSONObject obj);
	}
	
	public UserFunctions()
	{
		
		//jsonParser = new JSONParser();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		mCircleText = getArguments().getString("circle_text");
		View rootView = inflater.inflate(R.layout.circle, container, false);
		
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.circle_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		layout.startAnimation(a1);
		
		View view = (View)rootView.findViewById(R.id.gr_back_circle);
		
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		view.startAnimation(a);
		
		TextView tv = (TextView)rootView.findViewById(R.id.circle_text);
		tv.setText(mCircleText);
	    
		return rootView;
	}

	public void setListener(UserLoginListener listener)
	{
		this.mUserLoginListener = listener;
	}
	
	public void loginUser(String login, String pass)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("user_login", login));
		params.add(new BasicNameValuePair("user_pass", pass));
		//jsonParser.getJSONFromUrl(mLoginURL, params);
		
		URLWithParams urlWithParams = new URLWithParams();
		urlWithParams.url = Urls.API + Urls.USER;
		urlWithParams.nameValuePairs = params;
		
		try {
            JSONParser asyncPoster = new JSONParser(this);
            asyncPoster.execute(urlWithParams);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public void registerUser(String login,
						String email,
						String pass,
						String full_name,
						String image_url)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("user_login", login));
		params.add(new BasicNameValuePair("user_email", email));
		params.add(new BasicNameValuePair("user_pass", pass));
		params.add(new BasicNameValuePair("user_full_name", full_name));
		params.add(new BasicNameValuePair("user_image_url", image_url));
		
		//jsonParser.getJSONFromUrl(mRegisterURL, params);
		URLWithParams urlWithParams = new URLWithParams();
		urlWithParams.url = Urls.API + Urls.USER;
		urlWithParams.nameValuePairs = params;
		
		try {
            JSONParser asyncPoster = new JSONParser(this);
            asyncPoster.execute(urlWithParams);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public boolean isUserLoggedIn(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count >0)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean logoutUser(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj) {
		// TODO Auto-generated method stub
		mUserLoginListener.getUserStateComplete(obj);
		getActivity().getSupportFragmentManager().beginTransaction().remove(UserFunctions.this).commit();
	}

	public void updateAvatar(Context context, String url){
		DatabaseHandler db = new DatabaseHandler(context);
		db.updateAvatar(url);
	}
}
