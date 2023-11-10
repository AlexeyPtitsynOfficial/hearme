package com.donearh.hearme.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class JSONParser extends AsyncTask<HashMap<String, String>, Void, JSONObject>{
	
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	GetJSONListener getJSONListener;
	protected View mView;
	
	public JSONParser(GetJSONListener listener, View v)
	{
		this.getJSONListener = listener;
		this.mView = v;
	}
	
	public interface GetJSONListener
	{
		public void onRemoteCallComplete(JSONObject obj, View v);
	}
	
	/*public void getJSONFromUrl(String url, List<NameValuePair> params)
	{
		mUserData = params;
		new RegUserWorker().execute(url);
	}*/

	@Override
	protected JSONObject doInBackground(HashMap<String, String>... params) {
		// TODO Auto-generated method stub
		String url;
		url = params[0].get("url");
		params[0].remove("url");
		return getJSON(url, params[0]);
	}
	
	public static JSONObject getJSON(String url_text, HashMap<String, String> params)
	{
			
		try
		{
			URL url = new URL(url_text);
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
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
                /*String line = "";
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                	response+=line;
                }*/
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                json= sb.toString();
                
    			try {
    				jObj = new JSONObject(json);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    				jObj = new JSONObject();
    				   try {
    					   jObj.put("error", true);
    					   jObj.put("error_type", "error_parse");
    					} catch (JSONException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    					return jObj;
    			}
            }
            else {
                Log.e("doni_error", "Error in http connection");
                
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                json= sb.toString();
                
                try {
    				jObj = new JSONObject(json);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    				jObj = new JSONObject();
    				   try {
    					   jObj.put("error", true);
    					   jObj.put("error_type", "error_parse");
    					} catch (JSONException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    					return jObj;
    			}
     			return jObj;
            }
		}catch(UnsupportedEncodingException e){
			
		}catch (Exception e) {
			// TODO: handle exception
			Log.e("hm_error", e.getStackTrace().toString());
			jObj = new JSONObject();
			try {
				jObj.put("error", "no_conn");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		return jObj;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		if(getJSONListener != null)
			getJSONListener.onRemoteCallComplete(result, mView);
	}
	
	private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
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

}
