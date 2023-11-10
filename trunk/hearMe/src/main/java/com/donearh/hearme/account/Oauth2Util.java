package com.donearh.hearme.account;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.util.Log;

import com.donearh.hearme.Urls;
import com.donearh.hearme.util.IOStreamUtil;

public class Oauth2Util {
	
	public static HashMap<String, String> getAuthToken(String refresh_token){
		
		HashMap<String, String> token_data = new HashMap<String, String>();
		HashMap<String, String> params = new HashMap<String, String>();
		String result;
		
		params.put("grant_type", "refresh_token");
		params.put("client_id", "testclient");
	    params.put("client_secret", "testpass");
		params.put("refresh_token", refresh_token);
		try{
			URL url = new URL(Urls.TOKEN);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(IOStreamUtil.getPostDataString(params));
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
                JSONObject json = null;
    			try {
    				json = new JSONObject(result);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    			}
    			
    			token_data.put(AccountManager.KEY_AUTHTOKEN, json.getString("access_token"));
    			token_data.put(AccountGeneral.KEY_REFRESH_TOKEN, json.getString("refresh_token"));
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
            	
            	if(json.getString("error").equals("expired_token") || json.getString("error_description").equals("Invalid refresh token"))
            		result = "refresh_token_expired";
            		
            	token_data.put("error", result);
            	
            	return token_data;
                //throw new HttpException(responseCode+"");
            }
			
			//is = conn.getInputStream();
		}catch(UnsupportedEncodingException e){
			token_data.put("error", "invalid encoding");
			
		}catch (Exception e) {
			// TODO: handle exception
			token_data.put("error", "no conn");
		}
		
		
		return token_data;
	}
}
