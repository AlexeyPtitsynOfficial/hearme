package com.donearh.hearme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class HMDialog extends DialogFragment{

	private String mTitle;
	private String mText;
	private TextView mMainText;
	@SuppressLint("ValidFragment")
	public HMDialog(String title, String text) {
		super();
		mTitle = title;
		mText = text;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.hm_dialog, container, false);
	    
	    setCancelable(false);
	    Button btn_ok = (Button)rootView.findViewById(R.id.btn_ok);
	    btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	    TextView title = (TextView)rootView.findViewById(R.id.title);
	    title.setText(mTitle);
	    mMainText = (TextView)rootView.findViewById(R.id.text);
	    mMainText.setText(mText);
	    
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
}
