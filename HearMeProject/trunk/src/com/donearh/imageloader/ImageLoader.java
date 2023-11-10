package com.donearh.imageloader;

import android.content.Context;

import com.donearh.hearme.AdDetailsActivity;
import com.donearh.hearme.MainControlBarActivity;
import com.donearh.hearme.ProfileActivity;
import com.donearh.hearme.R;
import com.donearh.imageloader.ImageCache.ImageCacheParams;

public class ImageLoader {
	
	public static final String DIR_USER_AVATAR = "user_avatar";
	public static final String DIR_AD_LIST = "ad_list";
	public static final String DIR_AD_DETAILS = "ad_details_list";
	
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	public ImageFetcher mImageFetcher;
	private ImageCacheParams cacheParams;
	
	public ImageLoader(Context context, String dir){
		mImageThumbSize = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        
        cacheParams = new ImageCacheParams(context, dir);

        cacheParams.setMemCacheSizePercent(0.8f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(context, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.icon_load);
        if(context instanceof MainControlBarActivity)
        	mImageFetcher.addImageCache(((MainControlBarActivity) context).getSupportFragmentManager(), cacheParams);
        else if(context instanceof ProfileActivity)
        	mImageFetcher.addImageCache(((ProfileActivity) context).getSupportFragmentManager(), cacheParams);
        else if(context instanceof AdDetailsActivity)
        	mImageFetcher.addImageCache(((AdDetailsActivity) context).getSupportFragmentManager(), cacheParams);
	}
	
	public void destroy(){
		mImageFetcher.closeCache();
        mImageFetcher = null;
		cacheParams = null;
	}
	
	public void onPause(){
		mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
	}
	
	public void onResume(){
		mImageFetcher.setExitTasksEarly(false);
	}
}
