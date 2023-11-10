package com.donearh.hearme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InsertDataFragment extends Fragment
{
	private JSONArray mJSONArray;
	
	private InputStream is = null;
	private StringBuilder sb = null;
	
	static final int AD_ADD = 0;
	static final int REG_USER = 1;

	private String mCircleText;
	private int mLoadType;
	
	private InsertCompleteListener mInsertCompleteListener;
	
	public interface InsertCompleteListener
	{
		public void getInsertCompleteState();
	}
	
	public InsertDataFragment()
	{
		
	}
	
	public void setListener(InsertCompleteListener listener)
	{
		this.mInsertCompleteListener = listener;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
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
	
	public void startInsert(URLWithParams params, int type, String text)
	{
		mLoadType = type;
		mCircleText = text;
		new InsertData().execute(params);
	}
	
	private class InsertData extends AsyncTask<URLWithParams, Void, Object>
	{
		private String result = null;
		
		@Override
		protected Object doInBackground(URLWithParams... params) {
			// TODO Auto-generated method stub
			//http post
			try
			{
			     HttpClient httpclient = new DefaultHttpClient();
			     String url = params[0].url;
			     HttpPost httppost = new HttpPost(url);
			     httppost.setEntity(new UrlEncodedFormEntity(params[0].nameValuePairs));
			     HttpResponse response = httpclient.execute(httppost);
			     HttpEntity entity = response.getEntity();
			     //entity.getContent();
			     is = entity.getContent();
			}
			catch(Exception e)
			{
			   Log.e("doni_error", "Error in http connection"+e.toString());
			   return "no_internet";
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
			   return null;
			}
			
			return "success";
		}
		
		protected void onPostExecute(Object result) {
	        // Pass the result data back to the main activity
	    	mInsertCompleteListener.getInsertCompleteState();
	    }
	}

}
