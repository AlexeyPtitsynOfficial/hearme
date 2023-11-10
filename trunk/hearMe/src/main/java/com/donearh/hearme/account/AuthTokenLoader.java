package com.donearh.hearme.account;

import java.io.BufferedWriter;
import java.io.IOException;
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
import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.donearh.hearme.Urls;
import com.donearh.hearme.util.IOStreamUtil;

public class AuthTokenLoader extends AsyncTaskLoader<HashMap<String, String>>{

	protected String mRefreshToken;
	
	private HashMap<String, String> mTokenData = new HashMap<String, String>();
	public AuthTokenLoader(Context context, String refresh_token) {
		super(context);
		// TODO Auto-generated constructor stub
		mRefreshToken = refresh_token;
	}

		
	public static HashMap<String, String> getAuthToken(Context context, String refresh_token) throws IOException {
		try {
            return new AuthTokenLoader(context, refresh_token).getAuthToken();
        } catch (IOException e) {
            Log.e(AuthTokenLoader.class.getSimpleName(), e.getMessage(), e);
        }
        return null;
	}
	
	@Override
    protected void onStartLoading() {
        if (TextUtils.isEmpty(mTokenData.get(AccountManager.KEY_AUTHTOKEN))) {
            forceLoad();
        } else {
            deliverResult(mTokenData);
        }
    }
	
	 @Override
    public void deliverResult(HashMap<String, String> data) {
	 mTokenData = data;
        super.deliverResult(data);
    }
	
	
	private HashMap<String, String> getAuthToken() throws IOException {
		HashMap<String, String> token_data = new HashMap<String, String>();
		HashMap<String, String> params = new HashMap<String, String>();
		String result;
		
		params.put("refresh_token", mRefreshToken);
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
            	
            	if(json.getString("error").equals("expired_token"))
            		result = "expired_token";
            		
                throw new HttpException(responseCode+"");
            }
			
			//is = conn.getInputStream();
		}catch(UnsupportedEncodingException e){
			result = "error";
			
		}catch (Exception e) {
			// TODO: handle exception
			result = "error";
		}
		
		
		return token_data;
    }
	
	@Override
	public HashMap<String, String> loadInBackground() {
		// TODO Auto-generated method stub
		try {
            return getAuthToken();
        } catch (IOException e) {
            Log.e(AuthTokenLoader.class.getSimpleName(), e.getMessage(), e);
        }
        return null;
	}
	
}
