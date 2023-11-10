package com.donearh.imageloader;

import com.donearh.hearme.AdDetailsActivity;
import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.ProfileActivity;
import com.donearh.hearme.R;
import com.donearh.imageloader.ImageCache.ImageCacheParams;

import android.content.Context;

public class ImageLoaderRounded {
	
	public static final String DIR_USER_AVATAR = "user_avatar";
	public static final String DIR_AD_LIST = "ad_list";
	
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	public ImageFetcherRounded mImageFetcherRounded;
	private ImageCacheParams cacheParams;
	
	public ImageLoaderRounded(Context context, String dir){
		mImageThumbSize = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        
        cacheParams = new ImageCacheParams(context, dir);

        cacheParams.setMemCacheSizePercent(0.8f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcherRounded = new ImageFetcherRounded(context, mImageThumbSize);
        mImageFetcherRounded.setLoadingImage(R.drawable.ic_launcher);
        if(context instanceof MainControlBarActivity)
        	mImageFetcherRounded.addImageCache(((MainControlBarActivity) context).getSupportFragmentManager(), cacheParams);
        else if(context instanceof ProfileActivity)
        	mImageFetcherRounded.addImageCache(((ProfileActivity) context).getSupportFragmentManager(), cacheParams);
        else if(context instanceof AdDetailsActivity)
        	mImageFetcherRounded.addImageCache(((AdDetailsActivity) context).getSupportFragmentManager(), cacheParams);
	}
	
	public void destroy(){
		mImageFetcherRounded.closeCache();
		mImageFetcherRounded = null;
		cacheParams = null;
	}
	
	public void onPause(){
		mImageFetcherRounded.setPauseWork(false);
		mImageFetcherRounded.setExitTasksEarly(true);
		mImageFetcherRounded.flushCache();
	}
	
	public void onResume(){
		mImageFetcherRounded.setExitTasksEarly(false);
	}
}
