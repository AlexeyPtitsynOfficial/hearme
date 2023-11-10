package com.donearh.hearme;

import android.content.Context;
import android.webkit.WebView;

public class MyWebView extends WebView{

	int t;
	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		
		if(getContentHeight() > 0)
		{
			t = 0;
		}
	}

}
