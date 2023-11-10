package com.donearh.hearme;

import com.donearh.hearme.AdDetailsActivity.PlaceholderFragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class FileImageScroller extends HorizontalScrollView{

	Context context;
	int prevIndex = 0;
	
	private PlaceholderFragment.FilesAdapter mAdapter;
	private ViewGroup parent;

	public FileImageScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setSmoothScrollingEnabled(true);

	}

	public void setAdapter(Context context, PlaceholderFragment.FilesAdapter tAdapter) {

		mAdapter = tAdapter;
		try {
			fillViewWithAdapter(mAdapter);
		} catch (ZeroChildException e) {

			e.printStackTrace();
		}
	}

	public void addItem(PlaceholderFragment.FilesAdapter tAdapter)
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
	
	private void fillViewWithAdapter(PlaceholderFragment.FilesAdapter mAdapter)
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
