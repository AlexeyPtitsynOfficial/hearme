package com.donearh.hearme;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class InsertData extends AsyncTask<URLWithParams, Void, Object>{
	
	public static final int ADD_COMMENT = 1;
	private Long mUserID;
	private Long mAdID;
	
	InsertCompleteListener mInsertCompleteListener;
	
	public interface InsertCompleteListener
	{
		public void getInsertCompleteState();
	}
	
	public void setListener(InsertCompleteListener listener)
	{
		this.mInsertCompleteListener = listener;
	}
	
	public InsertData()
	{
	}
	
	@Override
	protected Object doInBackground(URLWithParams... params) 
	{
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
		     entity.getContent();
		}
		catch(Exception e)
		{
		   Log.e("doni_error", "Error in http connection"+e.toString());
		   return "no_internet";
		}
		
		return "success";
	}
	
	protected void onPostExecute(Object result) {
        // Pass the result data back to the main activity
		if(mInsertCompleteListener != null)
			mInsertCompleteListener.getInsertCompleteState();
    }
}
