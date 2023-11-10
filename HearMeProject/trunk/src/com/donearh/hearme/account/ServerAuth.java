package com.donearh.hearme.account;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.donearh.hearme.Base64;
import com.donearh.hearme.Urls;
import com.donearh.hearme.tags.CommonTags;
import com.donearh.hearme.util.IOStreamUtil;



public class ServerAuth implements ServerAuthenticate{

	private InputStream is = null;
	
	@Override
	public String userSignUp(String name, String email, String pass,
			String authType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	public HashMap<String, String> userSignUp(String first_name, String last_name, String username, String email, String pass, Integer acc_type, String display_name, Bitmap image_file,
			String desc,
			String authType) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, String> result = new HashMap<String, String>();
		
		String response ="";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(CommonTags.TAG, CommonTags.SIGN_UP);
		
		//params.put("first_name", first_name);
		//params.put("last_name", last_name);
		params.put("username", username);
		params.put("email", email);
		params.put("password", pass);
		
		params.put(AccountGeneral.KEY_HM_ACC_TYPE, acc_type.toString());
		params.put(AccountGeneral.KEY_SOCIAL_TYPE, "0");
		params.put(AccountGeneral.KEY_DISPLAY_NAME, display_name);
		
		if(image_file != null){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			image_file.compress(CompressFormat.JPEG, 100, bos);
		    byte[] data = bos.toByteArray();
		    String file = Base64.encodeBytes(data);
			params.put("image_file", file);
		}
		//params.put(AccountGeneral.KEY_DESC, desc);
		
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
			writer.write(IOStreamUtil.getPostDataString(params));
			
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
                response= sb.toString();
                
                JSONObject json = null;
    			try {
    				json = new JSONObject(response);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				Log.e("JSON Parser", "Error parsing data " + e.toString());
    				//Log.e("Error pars", result);
    			}
    			int success = json.getInt("success");
    			if(success == 1){
    				result= userSignIn(username, pass,AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
    				if(!json.isNull("image_path"))
    					result.put(AccountGeneral.KEY_IMAGE_URL, json.getString("image_path"));
    			}
    			else
    				result.put("error", "error");
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
		
		return result;
	}
	
	

	public HashMap<String, String> userSignIn(String user, String pass, String authType)
			throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, String> token_data = new HashMap<String, String>();
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("grant_type", "password");
	    params.put("client_id", "hearme_client");
	    params.put("client_secret", "lampochka123");
		params.put("username", user);
		params.put("password", pass);
		
		String response = "";
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
    			
    			token_data.put(AccountManager.KEY_AUTHTOKEN, json.getString("access_token"));
    			token_data.put(AccountGeneral.KEY_REFRESH_TOKEN, json.getString("refresh_token"));
            }
            else {
            	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                response= sb.toString();

                throw new HttpException(responseCode+"");
            }
			
			//is = conn.getInputStream();
		}finally{
			if (is != null) {
	            is.close();
	        } 
		}
		
		
		return token_data;
	}

	private String getAccessToken(String user_name, String password) throws IOException, HttpException, JSONException{
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("grant_type", "password");
	    params.put("client_id", "hearme_client");
	    params.put("client_secret", "lampochka123");
		params.put("username", user_name);
		params.put("password", password);
		
		String response = "";
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
    			
    			response = json.getString("access_token");
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
		
		
		return response;
	}


	@Override
	public HashMap<String, String> getRefreshAccessToken(String refresh_token)
			throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, String> token_data = new HashMap<String, String>();
		HashMap<String, String> params = new HashMap<String, String>();
		String result;
		
		params.put("grant_type", "refresh_token");
		params.put("client_id", "hearme_client");
	    params.put("client_secret", "lampochka123");
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


	@Override
	public boolean checkAccess(String access_token)
			throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("access_token", access_token);
		
		String response = "";
		try{
			URL url = new URL(Urls.RESOURCES);
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
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				return true;
            }
            else {
            	return false;
            }
			
			//is = conn.getInputStream();
		}finally{
			if (is != null) {
	            is.close();
	        } 
		}
	}

	
}
