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
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donearh.hearme.library.DatabaseHandler;

public class DownloadFragment extends Fragment implements AnimationListener
{
	
	static final int LOAD_AD_LIST = 2;
	static final int LOAD_CATEGORY = 4;
	static final int GET_FAVORITE_ADS = 5;
	static final int GET_FAVORITE_USERS = 6;
	static final int MAIN_SEARCH = 7;
	
	private DownloadD mDownloadData;
	private Animation animStartScale;
	
	private JSONArray mJSONArray;
	
	private View mCircle;
	private View mBackCircle;
	
	private String mCircleText;
	
	private Context mContext;
	private int mLoadType;
	private JSONArray jArray;
	
	private InputStream is = null;
	private StringBuilder sb = null;
	
	private ArrayList<DDTask> mDownloadDatas = new ArrayList<DDTask>();
	private ArrayList<AdListData> mAdListData;
	
	private ArrayList<Integer> mIntegerArray;
	
	private ArrayList<String> mDownloadError = new ArrayList<String>();
	
	private URLWithParams mUrlWithParams;
	DownloadCompleteListener mDownloadCompleteListener;
	
	private class DDTask{
		URLWithParams url_params;
		DownloadD task;
		int type;
	}
	
	public interface DownloadCompleteListener
	{
		public void getDownloadCompleteState(ArrayList<? extends Object> data);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mUrlWithParams = new URLWithParams();
		super.onCreate(savedInstanceState);
	}
	
	public void setListener(DownloadCompleteListener listener)
	{
		this.mDownloadCompleteListener = listener;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		//mCircleText = getArguments().getString("circle_text");
		View rootView = inflater.inflate(R.layout.circle, container, false);
		
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.circle_layout);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		layout.startAnimation(a1);
		
		mBackCircle = (View)rootView.findViewById(R.id.gr_back_circle);
		mCircle = (View)rootView.findViewById(R.id.gr_circle);
		
		animStartScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_start_anim);
		animStartScale.setAnimationListener(this);
		mBackCircle.startAnimation(animStartScale);
		mCircle.startAnimation(animStartScale);
		
		TextView tv = (TextView)rootView.findViewById(R.id.circle_text);
		tv.setText(mCircleText);
	    
		return rootView;
	}
	
	@Override
    public void onResume() {
        super.onResume();

        ArrayList<DDTask> task_array = new ArrayList<DDTask>();
        for(int i=0; i<mDownloadDatas.size(); i++)
        {
        	if(mDownloadDatas.get(i).task.isCancelled())
        	{
        		//mDownloadDatas.get(i).execute(mArrayUrl.get(i));
        		DDTask dd_task = new DDTask();
        		dd_task.task = new DownloadD();
        		dd_task.url_params = mDownloadDatas.get(i).url_params;
        		task_array.add(dd_task);
        	}
        }
        
        if(task_array.size() != 0){
        	mDownloadDatas.clear();
        	mDownloadDatas = null;
        	mDownloadDatas = task_array;
        }
        
        for(int i=0; i<mDownloadDatas.size(); i++){
        	if(mDownloadDatas.get(i).task.getStatus() != Status.RUNNING){
	        	mDownloadDatas.get(i).task.setIndex(i);
	        	mDownloadDatas.get(i).task.setType(mDownloadDatas.get(i).type);
	        	mDownloadDatas.get(i).task.execute(mDownloadDatas.get(i).url_params);
        	}
        }
    }
	
	@Override
    public void onPause() {
        super.onPause();
       //mDownloadData.cancel(true);
        for(int i=0; i<mDownloadDatas.size(); i++)
        {
        	mDownloadDatas.get(i).task.cancel(true);
        }
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        //mDownloadData = null;
        mDownloadDatas.clear();
        mDownloadDatas = null;
        
        //mAdListData.clear();
        //mAdListData = null;
       // mLongArray.clear();
       // mLongArray = null;
    }
	
	public boolean ResetTable(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	
	public void startDownload(URLWithParams params, String text, int type, Context context)
	{
		mCircleText = text;
		mLoadType = type;
		
		//mDownloadData = new DownloadD();
		//mDownloadData.execute(mUrl);
		DDTask dd_task = new DDTask();
		dd_task.task = new DownloadD();
		dd_task.url_params = params;
		dd_task.type = type;
		mDownloadDatas.add(dd_task);
		
		int index = mDownloadDatas.size()-1;
		mDownloadDatas.get(index).task.setIndex(index);
		mDownloadDatas.get(index).task.setType(type);
		mDownloadDatas.get(index).task.execute(params);
	}
	private class DownloadD extends AsyncTask<URLWithParams, Void, ArrayList<? extends Object>>
	{
		private int mIndex;
		private int mType;
		private String result = null;
		
		public void setIndex(int ind)
		{
			mIndex = ind;
		}
		
		public void setType(int type)
		{
			mType = type;
		}
		
		public int getIndex()
		{
			return mIndex;
		}
		@Override
		protected ArrayList<? extends Object> doInBackground(URLWithParams... params) {
			// TODO Auto-generated method stub
			if(isCancelled())
			{
				return null;
			}
			else
			{
			//http post
			try
			{
			     HttpClient httpclient = new DefaultHttpClient();
			     String url = params[0].url;
			     HttpPost httppost = new HttpPost(url);
			     httppost.setEntity(new UrlEncodedFormEntity(params[0].nameValuePairs));
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
			
			//paring data
			mIntegerArray = new ArrayList<Integer>();
			
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("JSON Parser", "Error parsing data " + e.toString());
				//Log.e("Error pars", result);
				mDownloadError.clear();
				mDownloadError.add("error");
				return mDownloadError;
			}
			try
			{			
				int success = json.getInt("success");
				if(success == 1)
				{
					mAdListData = new ArrayList<AdListData>();
					JSONObject json_data = null;
					mJSONArray = json.getJSONArray("ad_data");
					for(int i=0; i<mJSONArray.length(); i++)
					{
						json_data = mJSONArray.getJSONObject(i);
						AdListData data = new AdListData();
						data.Id = json_data.getInt(AdKeys.AD_ID);

						if(!json_data.isNull("user_id"))
							data.user_id = json_data.getInt("user_id");
						if(!json_data.isNull("account_type"))
							data.acc_type = json_data.getInt("account_type");
						if(!json_data.isNull("username"))
							data.user_name = json_data.getString("username");
						else
							data.guest_name = json_data.getString("ad_guest_name");
						if(!json_data.isNull("display_name"))
							data.display_name = json_data.getString("display_name");
						if(!json_data.isNull(AdKeys.AD_IMG))
							data.user_image_url = json_data.getString(AdKeys.AD_IMG);
						
			    		data.area = json_data.getInt("ad_area");
			    		data.state = json_data.getInt("ad_state");
			    		data.Title = json_data.getString(AdKeys.AD_TITLE);
			    		data.Parent_cat_id = json_data.getInt("parent_id");
			    		data.Category_id = json_data.getInt(AdKeys.AD_CATEGORY);
			    		data.Category_name = json_data.getString("category_name");
			    		data.Cat_img_id = json_data.getString("image_id");
			    		data.Desc = json_data.getString(AdKeys.AD_DESC);
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
			    		
			    		//data.Add_time = json_data.getString(AdKeys.AD_ADD_TIME);
			    		//data.Add_date = json_data.getString(AdKeys.AD_ADD_DATE);
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
				}
			}
			catch(JSONException e1)
			{
				//Log.e("doni_error", "SQL ERROR RESULT = " + result);
				mDownloadError.clear();
				mDownloadError.add("error");
				return mDownloadError;
			} 
			catch (ParseException e1) 
			{
				e1.printStackTrace();
				mDownloadError.clear();
				mDownloadError.add("error");
				return mDownloadError;
			}
			
			jArray = null;
			result = null;
			is = null;
			sb=null;
			return mAdListData;
			}
		}
		protected void onPostExecute(ArrayList<? extends Object> result) {
	        // Pass the result data back to the main activity
			mDownloadCompleteListener.getDownloadCompleteState(result);
			if(mDownloadDatas != null){
				for(int i=0; i<mDownloadDatas.size(); i++)
		    	{
		    		int idx = mDownloadDatas.get(i).task.getIndex();
		    		if(idx > mIndex)
		    			mDownloadDatas.get(i).task.setIndex(idx - 1);
		    	}
		    	mDownloadDatas.remove(mIndex);
			}
	    	if(mDownloadDatas == null || mDownloadDatas.size() == 0)
	    	{
	    		if(getActivity() != null)
	    		{
	    			((MainControlBarActivity)getActivity()).setNull();
	    		}
	    	}
	    	
		}
		
		@Override
	    protected void onCancelled() {
	      super.onCancelled();
	      Log.i("doni", "cancel");
		}
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == animStartScale) 
		{
			//Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
			//mBackCircle.startAnimation(a);
		}
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
}
