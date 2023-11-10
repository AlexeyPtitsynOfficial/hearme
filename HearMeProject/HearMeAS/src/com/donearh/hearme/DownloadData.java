package com.donearh.hearme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.util.Log;

import com.donearh.hearme.datatypes.AreaData;
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
	private ArrayList<String> mSliderData;
	
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
				Log.e("JSON Parser", "Error parsing data " + e.toString());
				Log.e("Error pars", result);
				mDownloadError.clear();
			    mDownloadError.add("error");
			    return mDownloadError;
			}
			
			try {
	            // Получаем SUCCESS тег для проверки статуса ответа сервера
	            int success = json.getInt("success");
	
	            if (success == 1) 
	            {
	            	mAdListData = new ArrayList<AdListData>();
	            	JSONObject json_data=null;
	            	if(mLoadType == MAIN_DATA_UPDATE_CHECK){
	            		MainDatabaseHandler db = new MainDatabaseHandler(mContext);
	            		db.getWriteDatabase();
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
		            		updateCheckerData.add(data);
		            		
					    }
		            	int count = db.getUpdateCheckerRowCount();
	        			if(count != 0 && (count == updateCheckerData.size()))
	        			{
	        				ArrayList<UpdateCheckerData> old_data = db.getUpdateCheckerData();
	        				UpdateState state = new UpdateState();
	        				for(int i=0; i<count; i++){
		        				if(old_data.get(i).id.equals(updateCheckerData.get(i).id)){
		        					String oldDate = old_data.get(i).update_datetime.toString();
		        					String newDate = updateCheckerData.get(i).update_datetime.toString();
		        					if(!oldDate.equals(newDate)){
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
	        				mUpdateState.add(state);
	        			}
	        			else
	        			{
	        				db.resetTables();
	        				for(int i=0; i<updateCheckerData.size(); i++)
	        				{
	        					db.addUpdateCheckerData(updateCheckerData.get(i).id, 
	        							updateCheckerData.get(i).name, 
	        							updateCheckerData.get(i).update_datetime);
	        				}
	        				UpdateState state = new UpdateState();
	        				state.area = true;
	        				state.category = true;
	        				mUpdateState.add(state);
	        			}
	        			db.closeDB();
	            	}
	            	else if(mLoadType == MAIN_DATA_AREA)
			    	{
	            		MainDatabaseHandler db = new MainDatabaseHandler(mContext);
	            		db.getWriteDatabase();
		            	mJSONArray = json.getJSONArray("area_data");
		            	mAreaData = new ArrayList<AreaData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
		            		AreaData data = new AreaData();
		            		data.id = json_data.getInt("area_id");
		            		data.name = json_data.getString("area_name");
		            		mAreaData.add(data);
		            		db.addAreaData(data.id, data.name);
					    }
		            	db.closeDB();
			    	}
		            else if(mLoadType == MAIN_DATA_CATEGORY)
		            {
		            	MainDatabaseHandler db = new MainDatabaseHandler(mContext);
	            		db.getWriteDatabase();
		            	mJSONArray = json.getJSONArray("category_data");
		            	mCategoryData = new ArrayList<CategoryData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
		            		CategoryData data = new CategoryData();
				    		  data.Id = json_data.getInt("category_id");
				    		  data.Name = json_data.getString("category_name");
				    		  data.ParentId = json_data.getInt("parent_id");
				    		  data.hasChild = json_data.getInt("has_child");
				    		  data.left_color = json_data.getString("left_color");
				    		  data.right_color = json_data.getString("right_color");
				    		  if(data.Id == 1)
				    			  data.icon_img = R.drawable.cat_icon_1;
				  			if(data.Id == 72)
				  				data.icon_img = R.drawable.cat_icon_72;
				  			if(data.Id == 142)
				  				data.icon_img = R.drawable.cat_icon_142;
				  			if(data.Id == 283)
				  				data.icon_img = R.drawable.cat_icon_283;
				  			if(data.Id == 336)
				  				data.icon_img = R.drawable.cat_icon_336;
				  			if(data.Id == 395)
				  				data.icon_img = R.drawable.cat_icon_395;
				  			if(data.Id == 428)
				  				data.icon_img = R.drawable.cat_icon_428;
				  			if(data.Id == 461)
				  				data.icon_img = R.drawable.cat_icon_461;
				  			if(data.Id == 490)
				  				data.icon_img = R.drawable.cat_icon_490;
				  			if(data.Id == 550)
				  				data.icon_img = R.drawable.cat_icon_550;
				  			if(data.Id == 677)
				  				data.icon_img = R.drawable.cat_icon_677;
				  			if(data.Id == 678)
				  				data.icon_img = R.drawable.cat_icon_678;
				    		  mCategoryData.add(data);
				    		  db.addCategoryData(data);
					    }
		            	db.closeDB();
			    	}
	  		    	else if(mLoadType == LOAD_AD_LIST 
	  		    			  || mLoadType == GET_MORE_AD
	  		    			  || mLoadType == GET_MORE_AD_DETAILS)
	  		    	{
	  		    		mJSONArray = json.getJSONArray("ad_data");
	  		    		mAdListData = new ArrayList<AdListData>();
		            	for(int i=0;i<mJSONArray.length();i++)
					    {
		            		json_data = mJSONArray.getJSONObject(i);
	  		    		  	AdListData data = new AdListData();
	  		    		  	data.Id = json_data.getInt(AdKeys.AD_ID);
	  		    		  	try{
	  		    		  	data.User_id = json_data.getInt("user_id");
	  		    		  	data.user_name = json_data.getString("user_login");
	  		    		  	}catch(JSONException e1){
		  		    		  	data.User_id = -1;
								data.user_name = "";
	  		    		  	}
	  		    		  	data.area = json_data.getInt("ad_area");
	  		    		  	data.state = json_data.getInt("ad_state");
	  		    		  	data.Title = json_data.getString(AdKeys.AD_TITLE);
	  		    		  	data.Parent_cat_id = json_data.getInt("parent_id");
	  		    		  	data.Category_id = json_data.getInt(AdKeys.AD_CATEGORY);
	  		    		  	data.Category_name = json_data.getString("category_name");
	  		    		  	data.Cat_img_id = json_data.getString("image_id");
	  		    		  	data.Desc = json_data.getString(AdKeys.AD_DESC);
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
	  		    		  	//data.Add_time = json_data.getString(AdKeys.AD_ADD_TIME);
	  		    		  	//data.Add_date = json_data.getString(AdKeys.AD_ADD_DATE);
	  		    		  	data.user_image_url = json_data.getString(AdKeys.AD_IMG);
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
								Log.e("doni_info", "без изображения");
							}
				    		if(json_path_array != null)
					    		for(int j=0; j<json_path_array.length(); j++)
					    		{
					    			json_path = json_path_array.getJSONObject(j);
					    			data.files_urls.add(json_path.getString("file_path"));
					    		}
	  		    		  
	  		    		  mAdListData.add(data);
					    }
	  		    	  }
	  		    	  else if(mLoadType == GET_FAV_USERS_NAMES)
	  		    	  {
	  		    		  mFavUsersList.add(json_data.getString("user_login"));
	  		    	  }
	  		    	  else if(mLoadType == GET_SLIDER_URLS)
	  		    	  {
	  		    		  mJSONArray = json.getJSONArray("slider_data");
	  		    		  mSliderData = new ArrayList<String>();
	  		    		  for(int i=0;i<mJSONArray.length();i++)
	  		    		  {
	  		    			  json_data = mJSONArray.getJSONObject(i);
	  		    			  mSliderData.add(json_data.getString("image_url"));
	  		    		  }
	  		    	  }
	            }
			}
			catch (JSONException e) {
	            e.printStackTrace();
	            Log.e("doni_error", "SQL ERROR RESULT = " + result);
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
