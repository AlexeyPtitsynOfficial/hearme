package com.donearh.hearme;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

public class PolicyDialog extends DialogFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setCancelable(false);
		View rootView = inflater.inflate(R.layout.dialog_policy, container, false);
		
		getDialog().setTitle("Пользовательское соглашение");
		
		WebView webview = (WebView)rootView.findViewById(R.id.webview);

		webview.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				view.clearCache(true);
			}
		});
		webview.loadUrl("http://webhearme.ru/policy/user_policy.html");
		//webview.loadDataWithBaseURL("webhearme.ru/policy/user_policy.html", "", "text/html", "utf-8", "");
		
		Button btn_confirm = (Button)rootView.findViewById(R.id.confirm);
		btn_confirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(getActivity() instanceof LoginActivity)
					((LoginActivity)getActivity()).startGoogleSign();
				else if(getActivity() instanceof MainControlBarActivity) {
					((MainControlBarActivity) getActivity()).mSavedData.savePolicyState(false);
					((MainControlBarActivity) getActivity()).selectItem(DrawerListAdapter.AD_ADD, -1);
				}
				else if(getActivity() instanceof RegisterActivity){
					((RegisterActivity)getActivity()).checkPolicy(true);
				}
				dismiss();
			}
		});
		
		Button btn_cancel = (Button)rootView.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(getActivity() instanceof RegisterActivity){
					((RegisterActivity)getActivity()).checkPolicy(false);
					
				}
				dismiss();
				
			}
		});
		return rootView;
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

}
