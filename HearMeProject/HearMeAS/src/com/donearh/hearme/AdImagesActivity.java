package com.donearh.hearme;

import java.util.ArrayList;
import java.util.Locale;

import com.donearh.animations.ZoomOutPageTransformer;
import com.donearh.hearme.AdDetailsActivity.PlaceholderFragment;
import com.donearh.imageloader.ImageFetcher;
import com.donearh.imageloader.ImageCache.ImageCacheParams;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AdImagesActivity extends ActionBarActivity {

	private static final String IMAGE_CACHE_DIR = "details_image";
	
	private static ImageFetcher mImageFetcher;
	
	private int mImagePos;
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	private static ArrayList<String> mImageList;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
		
		setContentView(R.layout.activity_ad_images);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        
        final int longest = (height > width ? height : width) / 2;

        ImageCacheParams cacheParams = new ImageCacheParams(AdImagesActivity.this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(AdImagesActivity.this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        
		mImageList = getIntent().getExtras().getStringArrayList("image_urls");
		mImagePos = getIntent().getExtras().getInt("image_pos");
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mViewPager.setCurrentItem(mImagePos);

	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mImageFetcher = null;
		mSectionsPagerAdapter = null;
		mImageList.clear();
		mImageList = null;
		mViewPager = null;
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ad_images, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private PlaceholderFragment mCurrentFragment;
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		public PlaceholderFragment getCurrentFragment() {
            return mCurrentFragment;
        }
		
		public int getCurrentPos()
		{
			return mCurrentFragment.mSecPos;
		}
		
		@Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((PlaceholderFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
		

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return mImageList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		public int mSecPos;
		
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			mSecPos = getArguments().getInt(ARG_SECTION_NUMBER);
			
			View rootView = inflater.inflate(R.layout.fragment_ad_images,
					container, false);

			ImageView imageV = (ImageView)rootView.findViewById(R.id.image);
			mImageFetcher.loadImage(mImageList.get(mSecPos), imageV);
			
			return rootView;
		}
	}

}
