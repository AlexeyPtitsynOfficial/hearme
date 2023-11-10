package com.donearh.hearme;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

public class AdAddDialog extends DialogFragment
{
	static int AD_ADD_DIALOG = 1;
	
	private SavedData mSavedData;
	private AdAddFragment mAdAddFragment;
	
	public interface OnActivityResult2 {
		public void onActivityResult2(int requestCode, int resultCode, Intent data);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		getDialog().setTitle(R.string.title_activity_ad_add);
		View rootView = inflater.inflate(R.layout.ad_add_frame, container, false);
		
		mSavedData = SavedData.getInstance(getActivity());
		Bundle bundle = new Bundle();
		bundle.putInt("id_main_area", mSavedData.getMainAreaId());
		
		mAdAddFragment = new AdAddFragment();
		mAdAddFragment.setArguments(bundle);
		
		getChildFragmentManager().beginTransaction().add(R.id.content_frame, mAdAddFragment).commit();
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stubsuper.onDestroy();
		mAdAddFragment = null;
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
		super.onResume();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		getDialog().getWindow().setLayout((6 * width)/7, LayoutParams.WRAP_CONTENT);
    }


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		mAdAddFragment.onActivityResult(requestCode, resultCode, data);
    }
	
}
