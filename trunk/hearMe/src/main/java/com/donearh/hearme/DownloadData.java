package com.donearh.hearme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.datatypes.SliderData;
import com.donearh.hearme.datatypes.UpdateCheckerData;
import com.donearh.hearme.datatypes.UpdateState;
import com.donearh.hearme.library.MainDatabaseHandler;

public class DownloadData extends AsyncTask<Void, Void, ArrayList<? extends Object>>
{
	
	private static String get_ad_list_tag = "get_ad_list";
	
	static final int MAIN_DATA_UPDATE_CHECK = 0;
	static final int MAIN_DATA_AREA = 1;
	static final int MAIN_DATA_CATEGORY = 2;
	static final int CHECK_USER = 3;
	static final int LOAD_AD_LIST = 4;
	static final int GET_FAV_USERS_NAMES = 5;
	static final int GET_MORE_AD = 6;
	static final int GET_MORE_AD_DETAILS = 7;
	static final int GET_SLIDER_URLS = 8;
	static final int MAIN_SEARCH = 9;
	
	private int mLoadType;
	private JSONArray mJSONArray;
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb = null;
	
	private ArrayList<String> mDownloadError = new ArrayList<String>();
	
	private URLWithParams mParams;
	
	private ArrayList<AdListData> mAdListData;
	private ArrayList<UpdateState> mUpdateState;
	private ArrayList<AreaData> mAreaData;
	private ArrayList<CategoryData> mCategoryData;
	private ArrayList<SliderData> mSliderData;
	
	private ArrayList<String> mFavUsersList;
	private ArrayList<Integer> mCatIcons;
	
	DownloadDCompleteListener mDownloadDCompleteListener;
	
	private Context mContext;
	
	public interface DownloadDCompleteListener
	{
		public void getDownloadDCompleteState(int loadType, ArrayList<? extends Object> ad_list);
	}
	
	public void setListener(DownloadDCompleteListener listener)
	{
		this.mDownloadDCompleteListener = listener;
	}
	
	protected DownloadData(int type, Context context)
	{
		mContext = context;
		mLoadType = type;
	}
	
	public void unlink(){
		this.mDownloadDCompleteListener = null;
	}
	
	public void setCatIcons(ArrayList<Integer> icons){
		
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	public void setParams(URLWithParams params){
		mParams = params;
	}
	public int getLoadType(){
		return mLoadType;
	}
	
	public URLWithParams getParams(){
		return mParams;
	}
	
	@Override
	protected ArrayList<? extends Object> doInBackground(Void... params)
	{
		if(isCancelled())
		{
			return null;
		}
		else{
		//http post
			try
			{
			     HttpClient httpclient = new DefaultHttpClient();
			     String url = mParams.url;
			     HttpPost httppost = new HttpPost(url);
			     httppost.setEntity(new UrlEncodedFormEntity(mParams.nameValuePairs));
			     HttpResponse response = httpclient.execute(httppost);
			     HttpEntity entity = response.getEntity();
			     is = entity.getContent();
			}
			catch(Exception e)
			{
			   Log.e("doni_error", "Error in http connection"+e.toString());
			   mDownloadError.clear();
			   mDownloadError.add("no_connection");
			   return mDownloadError;
			}
			
			//convert response to string
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
			   mDownloadError.clear();
			   mDownloadError.add("error");
			   return mDownloadError;
			}
			
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("JSON Parser", " json:"+String.valueOf(mLoadType)+"олд:" + e.toString());
				//Log.e("Error pars", result);
				mDownloadError.clear();
			    mDownloadError.add("error");
			    return mDownloadError;
			}
			
			try {
	            // SUCCESS
	            int success = json.getInt("success");
	
	            if (success == 1) 
	            {
					MainDatabaseHandler db = new MainDatabaseHandler(mContext);
	            	mAdListData = new ArrayList<AdListData>();
	            	JSONObject json_data=null;
	            	if(mLoadType == MAIN_DATA_UPDATE_CHECK){

		            	mJSONArray = json.getJSONArray("update_checker");
		            	ArrayList<UpdateCheckerData> updateCheckerData = new ArrayList<UpdateCheckerData>();
		            	mUpdateState = new ArrayList<UpdateState>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
		            		UpdateCheckerData data = new UpdateCheckerData();
		            		data.id = json_data.getInt("id");
		            		data.name = json_data.getString("name");
		            		data.update_datetime = json_data.getString("update_datetime");
		            		data.info_text = json_data.getString("info_text");
		            		data.state = json_data.getString("state");
		            		updateCheckerData.add(data);
		            		
					    }
		            	UpdateState state = new UpdateState();
		            	
		            	for(int i=0; i<updateCheckerData.size(); i++){
		            		
			            	if(updateCheckerData.get(i).name.equals("application")){
	    						PackageInfo pInfo = null;
								try {
									pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
									String version = pInfo.versionName;
	        						if(!version.equals(updateCheckerData.get(i).state)){
	        							state.app = true;
	        							mUpdateState.add(state);
	        							return mUpdateState;
	        						}
								} catch (NameNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	    					}
			            	else if(updateCheckerData.get(i).name.equals("access")){
			            		if(updateCheckerData.get(i).state.equals("1")){
			            			state.access = true;
			            			state.text = updateCheckerData.get(i).info_text;
			            			mUpdateState.add(state);
			            			return mUpdateState;
			            		}
			            	}
        				}
		            	int count = db.getUpdateCheckerRowCount();
	        			if(count != 0 && (count == updateCheckerData.size()))
	        			{
	        				ArrayList<UpdateCheckerData> old_data = db.getUpdateCheckerData();
	        				
	        				for(int i=0; i<count; i++){
	        					
		        				if(old_data.get(i).id.equals(updateCheckerData.get(i).id)){
		        					String oldDate = old_data.get(i).update_datetime.toString();
		        					String newDate = updateCheckerData.get(i).update_datetime.toString();
		        					if(!oldDate.equals(newDate)){
		        						if(updateCheckerData.get(i).name.equals("info")){
			        						state.info = true;
			        						state.text = updateCheckerData.get(i).info_text;
			        					}
		        						if(updateCheckerData.get(i).name.equals("policy_update")){
			        						state.policy_update = true;
			        					}
			        					if(updateCheckerData.get(i).name.equals("application")){
			        						state.app = true;
			        						
			        					}
			        					db.updateUpdateChecker(updateCheckerData.get(i).id, updateCheckerData.get(i).name, updateCheckerData.get(i).update_datetime);
			        					if(updateCheckerData.get(i).name.equals("area")){
			        						state.area = true;
			        					}
			        					else if(updateCheckerData.get(i).name.equals("category")){
			        						state.category = true;
			        					}
			        				}
		        					
		        				}
	        				}
	        				//state.app = true;
	        				mUpdateState.add(state);
	        			}
	        			else
	        			{
	        				db.resetTables();
	        				for(int i=0; i<updateCheckerData.size(); i++)
	        				{
	        					db.addUpdateCheckerData(updateCheckerData.get(i));
	        				}
	        				state.area = true;
	        				state.category = true;
	        				mUpdateState.add(state);
	        			}
	            	}
	            	else if(mLoadType == MAIN_DATA_AREA)
			    	{
		            	mJSONArray = json.getJSONArray("area_data");
		            	mAreaData = new ArrayList<AreaData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
		            		AreaData data = new AreaData();
		            		data.id = json_data.getInt("area_id");
		            		data.name = json_data.getString("area_name");
		            		mAreaData.add(data);

					    }
						db.addAreaData(mAreaData);
			    	}
		            else if(mLoadType == MAIN_DATA_CATEGORY)
		            {
		            	mJSONArray = json.getJSONArray("category_data");
		            	mCategoryData = new ArrayList<CategoryData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
		            		CategoryData data = new CategoryData();
				    		  data.id = json_data.getInt("category_id");
				    		  data.name = json_data.getString("category_name");
				    		  data.parent_id = json_data.getInt("parent_id");
				    		  data.has_child = json_data.getInt("has_child");
							  if(!json_data.isNull("left_color"))
				    		  	data.left_color = json_data.getString("left_color");
							  if(!json_data.isNull("right_color"))
				    		  	data.right_color = json_data.getString("right_color");
				    		  data.icon_img = json_data.getString("icon_url");
				    		  mCategoryData.add(data);
				    		  
					    }
		            	Collections.sort(mCategoryData, new CatComparator());
		            	db.addCategoryData(mCategoryData);
			    	}
	  		    	else if(mLoadType == LOAD_AD_LIST 
	  		    			  || mLoadType == GET_MORE_AD
	  		    			  || mLoadType == GET_MORE_AD_DETAILS
	  		    			  || mLoadType == MAIN_SEARCH)
	  		    	{
	  		    		mJSONArray = json.getJSONArray("ad_data");
	  		    		mAdListData = new ArrayList<AdListData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
	  		    		  	AdListData data = new AdListData();
	  		    		  	data.Id = json_data.getInt(AdKeys.AD_ID);
	  		    		  	
	  		    		  	if(!json_data.isNull("user_id"))
								data.user_id = json_data.getInt("user_id");
	  		    		  	if(!json_data.isNull("username"))
								data.user_name = json_data.getString("username");
	  		    		  	else
	  		    		  		data.guest_name = json_data.getString("ad_guest_name");
	  		    		  	  		  	
	  		    		  	data.area = json_data.getInt("ad_area");
	  		    		  	data.state = json_data.getInt("ad_state");
	  		    		  	data.Title = json_data.getString(AdKeys.AD_TITLE);
	  		    		  	data.Parent_cat_id = json_data.getInt("parent_id");
	  		    		  	data.Category_id = json_data.getInt(AdKeys.AD_CATEGORY);
	  		    		  	data.Category_name = json_data.getString("category_name");
	  		    		  	data.Cat_img_id = json_data.getString("image_id");
	  		    		  	data.Desc = json_data.getString(AdKeys.AD_DESC);
							data.comment_count = json_data.getInt(AdKeys.AD_COMMENT_COUNT);
	  		    		  	if(!json_data.isNull("ad_phone_num"))
	  		    		  		data.phone_num = json_data.getString("ad_phone_num");
	  		    		  	if(!json_data.isNull("ad_email"))
	  		    		  		data.email = json_data.getString("ad_email");
		  		    		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
	  		    		    Date mDate = null;
							try {
								mDate = sdf.parse(json_data.getString(AdKeys.AD_ADD_TIME)
												+" "+json_data.getString(AdKeys.AD_ADD_DATE));
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							data.add_datetime = mDate.getTime();

	  		    		  	data.cat_left_color = json_data.getString("left_color");
				    		data.cat_right_color = json_data.getString("right_color");
	  		    		  	
	  		    		  	JSONObject json_path = null;
				    		JSONArray json_path_array = null;
				    		try
				    		{
				    			json_path_array = json.getJSONArray("ad_files_path_" + data.Id);
				    		}
				    		catch(JSONException e1)
							{
								Log.e("doni_info", "without image");
							}
				    		if(json_path_array != null)
					    		for(int j=0; j<json_path_array.length(); j++)
					    		{
					    			json_path = json_path_array.getJSONObject(j);
					    			data.files_urls.add(json_path.getString("file_path"));
					    		}
	  		    		  
	  		    		  mAdListData.add(data);
					    }
		            	
		            	mJSONArray = json.getJSONArray("users_list");
		            	
		            	for (AdListData ad : mAdListData) {
		            		for(int i=0;i<mJSONArray.length();i++){
			            		json_data = mJSONArray.getJSONObject(i);
			            		if(ad.user_id != null && ad.user_id.equals(json_data.getInt("id"))){
			            			
			            			if(!json_data.isNull("username"))
			            				ad.user_name = json_data.getString("username");
			  		    		  	else
			  		    		  		ad.guest_name = json_data.getString("ad_guest_name");
				  		    		if(!json_data.isNull("display_name"))
				  		    			ad.display_name = json_data.getString("display_name");
				  		    		if(!json_data.isNull("username"))
				  		    			ad.user_name = json_data.getString("username");
				  		    		if(!json_data.isNull(AdKeys.AD_IMG))
				  		    			ad.user_image_url = json_data.getString(AdKeys.AD_IMG);
				  		    		break;
			            		}
			            	}
						}
		            	
		            	
	  		    	  }
	  		    	  else if(mLoadType == GET_FAV_USERS_NAMES)
	  		    	  {
	  		    		  mFavUsersList.add(json_data.getString("user_login"));
	  		    	  }
	  		    	  else if(mLoadType == GET_SLIDER_URLS)
	  		    	  {
	  		    		  mJSONArray = json.getJSONArray("slider_data");
	  		    		  mSliderData = new ArrayList<SliderData>();
	  		    		  
	  		    		  for(int i=0;i<mJSONArray.length();i++)
	  		    		  {
	  		    			  json_data = mJSONArray.getJSONObject(i);
	  		    			  SliderData data = new SliderData();
	  		    			  data.cat_id = json_data.getInt("cat_id");
	  		    			  data.url = json_data.getString("image_url");
	  		    			  data.content_url = json_data.getString("content_url");
	  		    			  mSliderData.add(data);
	  		    		  }
	  		    	  }
	            }
			}
			catch (JSONException e) {
	            e.printStackTrace();
	            //Log.e("doni_error", "SQL ERROR RESULT = " + result);
	            mDownloadError.clear();
			    mDownloadError.add("error");
			    return mDownloadError;
	        }
			mJSONArray = null;
			result = null;
			is = null;
			sb=null;
			
			switch (mLoadType) {
			case MAIN_DATA_UPDATE_CHECK:
				return mUpdateState;
			case MAIN_DATA_AREA:
				return mAreaData;
			case MAIN_DATA_CATEGORY:
				return mCategoryData;
			case GET_SLIDER_URLS:
				return mSliderData;
			case GET_MORE_AD:
				return mAdListData;
			case GET_MORE_AD_DETAILS:
				return mAdListData;
			case LOAD_AD_LIST:
				return mAdListData;
			case MAIN_SEARCH:
				return mAdListData;
			default:
				mDownloadError.clear();
				mDownloadError.add("error");
				return mDownloadError;
			}
		
		}
		
	}
	
	protected void onPostExecute(ArrayList<? extends Object> result) {
		
			mDownloadDCompleteListener.getDownloadDCompleteState(mLoadType, result);
    }
	
	public static <T extends Object> void foo(ArrayList<T> l) {
        T firstObj = l.get(0);

        if (firstObj != null) {
            if (firstObj instanceof DownloadData) {
                System.out.println("yay.. I am list of test obj " + l);
            } else {
                System.out.println("m just another list.. :( " + l);
            }
        }
	}

}
