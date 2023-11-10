package com.donearh.hearme.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.donearh.hearme.URLWithParams;

import android.os.AsyncTask;
import android.util.Log;

public class JSONParser extends AsyncTask<URLWithParams, Void, JSONObject>{
	
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	GetJSONListener getJSONListener;
	
	private List<NameValuePair> mUserData;
	
	public JSONParser(GetJSONListener listener)
	{
		this.getJSONListener = listener;
	}
	
	public interface GetJSONListener
	{
		public void onRemoteCallComplete(JSONObject obj);
	}
	
	/*public void getJSONFromUrl(String url, List<NameValuePair> params)
	{
		mUserData = params;
		new RegUserWorker().execute(url);
	}*/

	@Override
	protected JSONObject doInBackground(URLWithParams... params) {
		// TODO Auto-generated method stub
		return getJSON(params[0].url, params[0].nameValuePairs);
	}
	
	public static JSONObject getJSON(String url, List<NameValuePair> pairs)
	{
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		}
		catch(Exception e)
		{
		   Log.e("doni_error", "Error in http connection"+e.toString());
		}
		/*catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
		
		try
		{
			/*BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();
			Log.e("JSON", json);*/
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8));
				StringBuilder sb = new StringBuilder();
			  sb.append(reader.readLine() + "\n");

			  String line="0";
			  while ((line = reader.readLine()) != null) 
			  {
			      sb.append(line + "\n");
			  }
			  is.close();
			  json=sb.toString();
			  Log.e("JSON", json);
		}
		catch(Exception e)
		{
			Log.e("Buffer Error", "Error converting result " + e.toString());
			return null;
		}
		
		try
		{
			jObj = new JSONObject(json);
		}
		catch(JSONException e)
		{
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return null;
		}
		return jObj;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		if(result != null)
			getJSONListener.onRemoteCallComplete(result);
	}

}