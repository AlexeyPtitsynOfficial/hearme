package com.donearh.hearme;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.donearh.hearme.datatypes.FavUsersData;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.tags.CommonTags;

public class FragmentFilterState extends Fragment implements AdapterView.OnItemSelectedListener,
GetJSONListener,
AnimationListener{

	private Spinner mSpinner;
	private ProgressBar mProgressBar;
	private SpinnerFavUsersAdapter mFavAdapter;
	private ArrayList<Integer> mFavUsersId;
	private ArrayList<FavUsersData> mFavUsersData;

	private Animation hideAnim;
	private Animation openAnim;
	
	private LinearLayout main_layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_filter_state, container, false);
		
		main_layout = (LinearLayout)rootView.findViewById(R.id.main_layout);
		main_layout.setVisibility(View.GONE);
		openAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.open_filter);
		
		hideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.close_filter);
		hideAnim.setAnimationListener(this);
		
		mSpinner = (Spinner)rootView.findViewById(R.id.filter_spinner);	
		mSpinner.setOnItemSelectedListener(this);
		
		mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress);
		
		mFavUsersData = new ArrayList<FavUsersData>();
		return rootView;
	}
	
	public void openFilter(ArrayList<String> filter_array, ArrayList<? extends Object> spinner_data){

		if(spinner_data != null){
			mFavUsersId = (ArrayList<Integer>)spinner_data;
			if(mFavUsersId.size() != 0){
				mProgressBar.setVisibility(View.VISIBLE);
			}else{
				SpinnerArrayAdapter adapter2 = new SpinnerArrayAdapter(getActivity(), R.layout.adapter_spinner, (ArrayList<String>)spinner_data);
				mSpinner.setAdapter(adapter2);
			}	
		}
		else{ 
			if(mSpinner.getVisibility() == View.VISIBLE){
				mSpinner.setVisibility(View.GONE);
			}
			if(mFavAdapter != null){
				mFavAdapter = null;
			}
		}
		
		showFilter();
	}
	
	public void setAccessToken(String url, String access_token){
		if(mFavUsersId.size() != 0){
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("url", url);
			params.put("tag", CommonTags.GET_FAV_USERS);
			params.put("access_token", access_token);
			
			JSONParser parser = new JSONParser(this, null);
			parser.execute(params);
		}
	}
	
	public void closeFilter(){
		mFavUsersData.clear();
		main_layout.setVisibility(View.GONE);
	}
	
	
	public void showFilter(){
		if(main_layout.getAnimation() != openAnim && ((mFavUsersData !=  null && mFavUsersData.size() != 0) && !isFilterShowing())){
			if(main_layout.getVisibility() == View.GONE)
				main_layout.setVisibility(View.VISIBLE);
			main_layout.startAnimation(openAnim);
			
			if(mFavAdapter != null && mFavAdapter.getData().size() != 0)
				mSpinner.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void hideFilter(){
		main_layout.startAnimation(hideAnim);
	}
	
	public boolean isFilterShowing(){
		
		if(main_layout.getVisibility() == View.VISIBLE)
			return true;
		else
			return false;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animation == hideAnim){
			main_layout.setVisibility(View.GONE);
			if(mSpinner.getVisibility() == View.VISIBLE){
				mSpinner.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mFavUsersId.clear();
		if(position == 0){
			mFavUsersData.addAll(mFavAdapter.getData());
			for (FavUsersData fav_user : mFavAdapter.getData()) {
				if(fav_user.id != null)
					mFavUsersId.add(fav_user.id);
			}
		}
		else{
			mFavUsersId.add(mFavAdapter.getData().get(position).id);
		}
		((MainControlBarActivity)getActivity()).mMainPage.refreshTrigger(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub
		mFavUsersData.clear();
		mProgressBar.setVisibility(View.GONE);
		
		try {
			if(!obj.isNull("success") && obj.getBoolean("success") == true){
				JSONArray json_array = obj.getJSONArray("users_list");
				for(int i=0; i<json_array.length(); i++) {
					JSONObject data = json_array.getJSONObject(i);
					FavUsersData fav_user = new FavUsersData();
					fav_user.id = data.getInt("id");
					fav_user.name = data.getString("display_name");
					fav_user.avatar_url = data.getString("image_url");
					mFavUsersData.add(fav_user);
				}
				main_layout.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.VISIBLE);
				mFavAdapter = new SpinnerFavUsersAdapter(getActivity(), 
						R.layout.adapter_spinner_area, mFavUsersData);
				mSpinner.setAdapter(mFavAdapter);
				mSpinner.setSelection(0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
