package com.donearh.hearme;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

	private LinearLayout mLayout;
	private TextView mText;
	private Toast mToast;
	public CustomToast(Context context){
		View layout = LayoutInflater.from(context).inflate(R.layout.toast, null);

		mLayout = (LinearLayout) layout.findViewById(R.id.toast_layout);
		mText = (TextView) layout.findViewById(R.id.toast_text);

		mToast = new Toast(context);
		//mToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setView(layout);
		
	}
	
	public void show(String text){
		mText.setText(text);
		mToast.show();
	}
	public void showErrorToast(String text){
		mLayout.setBackgroundResource(R.drawable.toast_error);
		mText.setText(text);
		mToast.show();
	}

}
