package com.donearh.hearme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class CustomImageScroller extends HorizontalScrollView{

	Context context;
	int prevIndex = 0;
	
	private AdAddActivity.ImagesAdapter mAdapter;
	private ViewGroup parent;

	public CustomImageScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setSmoothScrollingEnabled(true);

	}

	public void setAdapter(Context context, AdAddActivity.ImagesAdapter tAdapter) {

		mAdapter = tAdapter;
		try {
			fillViewWithAdapter(mAdapter);
		} catch (ZeroChildException e) {

			e.printStackTrace();
		}
	}

	public void addItem(AdAddActivity.ImagesAdapter tAdapter)
	{
		parent.addView(tAdapter.getView(tAdapter.getCount()-1, null, parent));
	}
	
	public void removeItem(int pos)
	{
		parent.removeViewAt(pos);
	}
	
	public void updateView(int pos)
	{
		parent.getChildAt(0).invalidate();
	}
	
	private void fillViewWithAdapter(AdAddActivity.ImagesAdapter mAdapter)
			throws ZeroChildException {
		if (getChildCount() == 0) {
			throw new ZeroChildException(
					"CenterLockHorizontalScrollView must have one child");
		}
		if (getChildCount() == 0 || mAdapter == null)
			return;

		parent = (ViewGroup) getChildAt(0);
		
		parent.removeAllViews();

		for (int i = 0; i < mAdapter.getCount(); i++) {
			parent.addView(mAdapter.getView(i, null, parent));
		}
	}

}
