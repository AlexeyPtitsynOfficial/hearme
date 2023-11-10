package com.donearh.hearme;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.donearh.hearme.util.IOStreamUtil;

public class InsertData extends AsyncTask<HashMap<String, ArrayList<String>>, Void, Object>{
	
	public static final int ADD_COMMENT = 1;
	
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb = null;
	
	InsertCompleteListener mInsertCompleteListener;
	
	public interface InsertCompleteListener
	{
		public void getInsertCompleteState(Object result);
	}
	
	public void setListener(InsertCompleteListener listener)
	{
		this.mInsertCompleteListener = listener;
	}
	
	public InsertData()
	{
	}
	
	@Override
	protected Object doInBackground(HashMap<String, ArrayList<String>>... params) 
	{
		// TODO Auto-generated method stub
		//http post
		String url_text;
		url_text = params[0].get("url").get(0);
		params[0].remove("url");
		try{
			///url_text = Urls.API+"oauth2/controllers/resource.php";
			URL url = new URL(url_text);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(params[0]));
			writer.flush();
			writer.close();
			os.close();
			
			//conn.connect();  
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				
				/*BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                sb.append(br.readLine());
                br.close();
                result= sb.toString();*/
				result = IOStreamUtil.streamToString(conn.getInputStream());
                /*JSONObject json = null;
    			try {
    				json = new JSONObject(result);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    			}*/
    			
            }
            else {
            	result = IOStreamUtil.streamToString(conn.getErrorStream());
            	JSONObject json = null;
            	try {
    				json = new JSONObject(result);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    			}
            	
            	if(json.getString("error").equals("expired_token")){
            		return result = "expired_token";
            	}
            		
                throw new HttpException(responseCode+"");
            }
			
			//is = conn.getInputStream();
		}catch(UnsupportedEncodingException e){
			result = "error";
			
		}catch (Exception e) {
			// TODO: handle exception
			result = "error";
		}
		
		return result;
	}
	
	protected void onPostExecute(Object result) {
		if(mInsertCompleteListener != null)
			mInsertCompleteListener.getInsertCompleteState(result);
    }
	
	private String getPostDataString(HashMap<String, ArrayList<String>> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, ArrayList<String>> entry : params.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
            	if (first)
                    first = false;
                else
                    result.append("&");
            	result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue().get(i), "UTF-8"));
			}
            
        }

        return result.toString();
    }
}
