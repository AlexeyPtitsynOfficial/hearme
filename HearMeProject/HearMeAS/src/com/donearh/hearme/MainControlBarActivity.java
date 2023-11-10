package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.SearchManager;
import android.app.SearchManager.OnCancelListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.donearh.hearme.DownloadData.DownloadDCompleteListener;
import com.donearh.hearme.DownloadFragment.DownloadCompleteListener;
import com.donearh.hearme.MySlidingPaneLayout.OnSlideDetectListener;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.datatypes.FavUsersData;
import com.donearh.hearme.datatypes.UpdateState;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.hearme.library.SearchDatabase;
import com.donearh.hearme.library.UserFunctions;
import com.donearh.hearme.library.UserFunctions.UserLoginListener;
import com.donearh.imageloader.ImageCache.ImageCacheParams;
import com.donearh.imageloader.ImageFetcherBack;

public class MainControlBarActivity extends ActionBarActivity implements AnimationListener,
																		OnRefreshListener,
																		UserLoginListener,
																		DownloadCompleteListener,
																		DownloadDCompleteListener, 
																		OnCancelListener,
																		OnSlideDetectListener
{

	static final int REGISER_USER = 0;
	static final int CHECK_PROFILE = 1;
	static final int GET_SELECT_CAT = 2;
	static final int UPDATE_AVATAR = 3;
	static final int AD_ADD = 4;
	static final int CAT_ACTIVITY = 5;
	
	static final String MAIN_PAGE = "main_page";
	
	public final static int LOOPS = 10; 
	public final static int FIRST_PAGE = 1;
	public final static float BIG_SCALE = 1.0f;
	public final static float SMALL_SCALE = 0.6f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
	
	private static final String IMAGE_CACHE_DIR = "common_dir";
	
	FragmentManager mFragmentManager;
	
	private FragmentMenu mMenuFragment;
	private DrawerListProfile mDrawerListProfile;
	public MainPage mMainPage;
	
	private DatabaseHandler mDBUser;
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	public ImageFetcherBack mImageFetcherBack;
	private ImageCacheParams cacheParams;
	
	public ArrayList<AdListData> mAdListData;
	
	private UserFunctions mUserFunctions;
	
	private StartAppFragment mStartAppFragment;
	public YesNoFragment mYesNoFragment;
	private FavoriteUsersFilter mFavoriteUsersFilter;
	
	private SpinnerAreaAdapter mAreaSpinnerAdapter;
	private OnNavigationListener mCallbackArea;
	private SpinnerFavUsersAdapter mSpinnerFavUsersAdapter;
	private OnNavigationListener mCallbackFavUsers;
	protected int mNavType = 1; 
	
	public DownloadFragment mDownloadFragment;
	private FragmentFilterState mFragmentFilterState;
	public LowerControlBar mLowerControlBar;
	
	private DownloadData mTaskUpdateChecker;
	private ArrayList<DownloadData> mDownloadDataTasks;
	
	public FrameLayout mLowerBarFrame;
	
	private LayoutInflater inflater;
	//private DrawerListAdapter mDrawerAdapter;
	public TreeViewAdapter mTreeViewAdapter;
	
	public int mType = -1;
	public int mCurrentType = -1;
	
	boolean mIsListViewScroll = false;
	
	private SearchManager mSearchManager;
	private SearchView mSearchView;
	private MenuItem mSearchItem;
	 
	public SavedData mSavedData;
	private DrawerLayout mDrawerLayout;
	private ListView mCatDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ArrayList<Item> mItems;
	private ArrayList<Item> mCategoryItems;
	public ArrayList<Item> mLowerBarItems;
	
	ArrayList<TreeElementI> mTreeElements = new ArrayList<TreeElementI>();
	ArrayList<TreeElement> mParentElements = new ArrayList<TreeElement>();
	boolean mSlidingEnable = true;
	private MySlidingPaneLayout mSlidingPane;
	
	private String mUserLogin = "";
	private String mUserPass = "";
	
	private String mMainSearchText;
	
	//Changing views
	private RelativeLayout mContentView;
    private ImageView mLoadingView;
    private int mShortAnimationDuration;
    
    //menu 
    private MenuItem mRefreshMenuItem; 
    
    private Integer mAdCountInOnePage = 30;
	
    public ArrayList<AreaData> mAreaData = new ArrayList<AreaData>();
	public ArrayList<CategoryData> mCategoryData = new ArrayList<CategoryData>();
	public ArrayList<String> mSliderData = new ArrayList<String>();
	
	public long mSelectedAreaId = -1;
	public ArrayList<Integer> mSelectedAdsId = new ArrayList<Integer>();
	public ArrayList<FavUsersData> mSelectedUsers = new ArrayList<FavUsersData>();
	public ArrayList<Integer> mCatIDsList = new ArrayList<Integer>();
	private ArrayList<String> mFilterArray = new ArrayList<String>();
	private ArrayList<Integer> mOutCatIDsList = new ArrayList<Integer>();
	
	HashMap<String, String> mUserData;
	
	private ArrayList<AreaIcons> mAreaIcons = new ArrayList<MainControlBarActivity.AreaIcons>();
	
	public class AreaIcons{
		Integer id;
		Drawable image;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//savedInstanceState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onCreate(savedInstanceState);
		
		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getSupportActionBar().hide();
		
		setContentView(R.layout.activity_main_control_bar);
		
		mDBUser = new DatabaseHandler(MainControlBarActivity.this);
		
		mSearchManager = (SearchManager)this.getSystemService(Context.SEARCH_SERVICE);
		mSearchManager.setOnCancelListener(this);
		
		mSearchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				int t;
				t=0;
			}
		});
		
		mAdListData = new ArrayList<AdListData>();
		mUserFunctions = new UserFunctions();
		mUserFunctions.setListener(this);
		mSavedData = new SavedData(MainControlBarActivity.this);
		
		mCategoryData = new ArrayList<CategoryData>();
		
		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        
        cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcherBack = new ImageFetcherBack(this, mImageThumbSize);
        mImageFetcherBack.setLoadingImage(R.drawable.load_image);
        mImageFetcherBack.addImageCache(this.getSupportFragmentManager(), cacheParams);
		
		mTitle = mDrawerTitle = getTitle();
		
		mSlidingPane = (MySlidingPaneLayout)findViewById(R.id.sliding_pane);
		mSlidingPane.setOnSlideDetectListener(this);
		Drawable img = getResources().getDrawable(R.drawable.ic_drawer);
		mItems = new ArrayList<Item>();
		mCategoryItems = new ArrayList<Item>();
		mLowerBarItems = new ArrayList<Item>();
		
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mCatDrawerList = (ListView)findViewById(R.id.left_drawer);	
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		//mDrawerAdapter = new DrawerListAdapter(getApplicationContext(), mCategoryItems);
		//mTreeViewAdapter = new TreeViewAdapter(getApplicationContext(), mCategoryItems, MainControlBarActivity.this);
		//mDrawerList.setAdapter(mDrawerAdapter);
		checkProfile();
		
		//mCatDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout, 
				R.drawable.ic_drawer,
				R.string.drawer_open, 
				R.string.drawer_close)
		{
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
				if(!getSupportActionBar().isShowing()){
					showFilterStateInfo(true);
					getSupportActionBar().show();
				}
            }

            public void onDrawerOpened(View drawerView) {
            	getSupportActionBar().setTitle(mDrawerTitle);
            	supportInvalidateOptionsMenu();
            	closeSearch();
            	if(getSupportActionBar().isShowing()){
        			showFilterStateInfo(false);
        			getSupportActionBar().hide();
        		}
            }
			
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mLowerBarFrame = (FrameLayout)findViewById(R.id.lower_control);
		mLowerBarFrame.setVisibility(View.GONE);
		if(mSavedData.getLowerBarPos())
		{
			FragmentManager fragmentManager = getSupportFragmentManager();
			mLowerControlBar = new LowerControlBar();
			fragmentManager.beginTransaction().replace(R.id.lower_control, mLowerControlBar).commit();
			mLowerBarFrame.setVisibility(View.VISIBLE);
		}
		
		mFragmentManager = getSupportFragmentManager();
		mStartAppFragment = new StartAppFragment();
		mFragmentManager.beginTransaction().replace(R.id.start_app_frame, mStartAppFragment).commit();
		
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		mAreaSpinnerAdapter = new SpinnerAreaAdapter(this, 
														R.layout.adapter_spinner_area, 
														mAreaData);
		
		mCallbackArea = new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				// TODO Auto-generated method stub
				mSelectedAreaId = itemId;
				mSavedData.saveSelectedAreaId(mSelectedAreaId);
				selectItem(DrawerListAdapter.AD_ALL, -1);
				return true;
			}
		};
		getSupportActionBar().setListNavigationCallbacks(mAreaSpinnerAdapter, mCallbackArea);
		
		mSpinnerFavUsersAdapter = new SpinnerFavUsersAdapter(this, 
															R.layout.adapter_spinner_area, null);
		mCallbackFavUsers = new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				// TODO Auto-generated method stubposition
				mSelectedUsers = mDBUser.getFavUsers();
				if(position == 0){
					getAdList(-1);
				}
				else{
					FavUsersData fav_user = new FavUsersData();
					fav_user.id =mSelectedUsers.get(position-1).id;
					fav_user.name = mSelectedUsers.get(position-1).name;
					
					mSelectedUsers.clear();			
					mSelectedUsers.add(fav_user);
					getAdList(-1);
				}
				return false;
			}
		};
		
		mSelectedAreaId = mSavedData.getMainAreaId();
		
		mMenuFragment = new FragmentMenu();
		
		mMenuFragment.setData(mItems);
		
		getSupportFragmentManager().beginTransaction().add(R.id.menu_frame, mMenuFragment, "menu_frame").commit();
		
		mDownloadDataTasks = (ArrayList<DownloadData>)getLastCustomNonConfigurationInstance();
		if(mDownloadDataTasks == null){
			mDownloadDataTasks = new ArrayList<DownloadData>();
			initApp();
		}else{
			for(int i=0; i<mDownloadDataTasks.size(); i++)
	        {
	        	if(mDownloadDataTasks.get(i).isCancelled())
	        	{
	        		mDownloadDataTasks.get(i).setListener(this);
	        		mDownloadDataTasks.get(i).execute();
	        	}
	        }
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImageFetcherBack.closeCache();
		mImageFetcherBack = null;
		cacheParams = null;
		
		mDownloadDataTasks.clear();
		mDownloadDataTasks = null;
	}

	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mImageFetcherBack.setPauseWork(false);
		mImageFetcherBack.setExitTasksEarly(true);
		mImageFetcherBack.flushCache();
		
		for(int i=0; i<mDownloadDataTasks.size(); i++)
        {
			mDownloadDataTasks.get(i).cancel(true);
        }
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mImageFetcherBack.setExitTasksEarly(false);
		ArrayList<DownloadData> tasks_array = new ArrayList<DownloadData>();
		for(int i=0; i<mDownloadDataTasks.size(); i++)
        {
        	if(mDownloadDataTasks.get(i).isCancelled())
        	{
        		DownloadData dd_task = new DownloadData(mDownloadDataTasks.get(i).getLoadType(), MainControlBarActivity.this);
        		dd_task.setListener(this);
        		dd_task.setParams(mDownloadDataTasks.get(i).getParams());
        		tasks_array.add(dd_task);
        	}
        }
		
		if(tasks_array.size() != 0){
			mDownloadDataTasks.clear();
			mDownloadDataTasks = null;
			mDownloadDataTasks = tasks_array;
		}
		for (DownloadData ddTask : mDownloadDataTasks) {
			if(ddTask.getStatus() != Status.RUNNING)
				ddTask.execute();
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		// TODO Auto-generated method stub
		for(int i=0; i<mDownloadDataTasks.size(); i++)
        {
			mDownloadDataTasks.get(i).unlink();
        }
		return super.onRetainCustomNonConfigurationInstance();
	}



	public void initApp(){
		
		URLWithParams urlWithParams = new URLWithParams();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", AdTags.get_update_check_data));
		urlWithParams.url = Urls.API + Urls.COMMON;
		urlWithParams.nameValuePairs = params;
		
		DownloadData dd_task = new DownloadData(DownloadData.MAIN_DATA_UPDATE_CHECK, MainControlBarActivity.this);
		dd_task.setParams(urlWithParams);
		dd_task.setListener(this);
		mDownloadDataTasks.add(dd_task);
		mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
		
		URLWithParams urlWithParams3 = new URLWithParams();
		List<NameValuePair> params3 = new ArrayList<NameValuePair>();
		params3.add(new BasicNameValuePair("tag", AdTags.get_slider_urls));
		urlWithParams3.url = Urls.API + Urls.COMMON;
		urlWithParams3.nameValuePairs = params3;
		
		DownloadData dd_task2 = new DownloadData(DownloadData.GET_SLIDER_URLS, MainControlBarActivity.this);
		dd_task2.setParams(urlWithParams3);
		dd_task2.setListener(this);
		mDownloadDataTasks.add(dd_task2);
		mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
	}
	
	public void setSliderData(ArrayList<String> data)
	{
		
	}
	
	public ArrayList<String> getSliderData()
	{
		return mSliderData;
	}
	
	private Runnable openDrawerRunnable() {
	    return new Runnable() {

	        @Override
	        public void run() {
	        	mSlidingPane.openPane();
	            //mDrawerLayout.openDrawer(Gravity.LEFT);
	        }
	    };
	}
	
	public void setType(int type)
	{
		mType = type;
	}
	
	public int getType()
	{
		return mType;
	}
	
	public int getAdCountInList()
	{
		return mAdCountInOnePage;
	}
	
	private void setAreasIcons(){
		
		for (AreaData area : mAreaData) {
			
			if(area.id == 0)
				area.icon_res = R.drawable.area_0;
			if(area.id == 1)
				area.icon_res = R.drawable.area_1;
			if(area.id == 2)
				area.icon_res = R.drawable.area_2;
			if(area.id == 3)
				area.icon_res = R.drawable.area_3;
			if(area.id == 4)
				area.icon_res = R.drawable.area_4;
			if(area.id == 5)
				area.icon_res = R.drawable.area_5;
			if(area.id == 6)
				area.icon_res = R.drawable.area_6;
			if(area.id == 7)
				area.icon_res = R.drawable.area_7;
			if(area.id == 8)
				area.icon_res = R.drawable.area_8;
			if(area.id == 9)
				area.icon_res = R.drawable.area_9;
			if(area.id == 10)
				area.icon_res = R.drawable.area_10;
			if(area.id == 11)
				area.icon_res = R.drawable.area_11;
			if(area.id == 12)
				area.icon_res = R.drawable.area_12;
			if(area.id == 13)
				area.icon_res = R.drawable.area_13;
			if(area.id == 14)
				area.icon_res = R.drawable.area_14;
			if(area.id == 15)
				area.icon_res = R.drawable.area_15;
			if(area.id == 16)
				area.icon_res = R.drawable.area_16;
			if(area.id == 17)
				area.icon_res = R.drawable.area_17;
			if(area.id == 18)
				area.icon_res = R.drawable.area_18;
			if(area.id == 19)
				area.icon_res = R.drawable.area_19;
			if(area.id == 20)
				area.icon_res = R.drawable.area_20;
			if(area.id == 21)
				area.icon_res = R.drawable.area_21;
			if(area.id == 22)
				area.icon_res = R.drawable.area_22;
			if(area.id == 23)
				area.icon_res = R.drawable.area_23;
			if(area.id == 24)
				area.icon_res = R.drawable.area_24;
			if(area.id == 25)
				area.icon_res = R.drawable.area_25;
			if(area.id == 26)
				area.icon_res = R.drawable.area_26;
			if(area.id == 27)
				area.icon_res = R.drawable.area_27;
			if(area.id == 28)
				area.icon_res = R.drawable.area_28;
			if(area.id == 29)
				area.icon_res = R.drawable.area_29;
			if(area.id == 30)
				area.icon_res = R.drawable.area_30;
			if(area.id == 31)
				area.icon_res = R.drawable.area_31;
			if(area.id == 32)
				area.icon_res = R.drawable.area_32;
			if(area.id == 33)
				area.icon_res = R.drawable.area_33;
			if(area.id == 34)
				area.icon_res = R.drawable.area_34;
			if(area.id == 35)
				area.icon_res = R.drawable.area_35;
		}
		
	}
	
	protected void setCatsIcons(){
		for (CategoryData cat : mCategoryData) {
			if(cat.Id == 1)
				cat.icon_img = R.drawable.cat_icon_1;
			if(cat.Id == 72)
				cat.icon_img = R.drawable.cat_icon_72;
			if(cat.Id == 142)
				cat.icon_img = R.drawable.cat_icon_142;
			if(cat.Id == 283)
				cat.icon_img = R.drawable.cat_icon_283;
			if(cat.Id == 336)
				cat.icon_img = R.drawable.cat_icon_336;
			if(cat.Id == 395)
				cat.icon_img = R.drawable.cat_icon_395;
			if(cat.Id == 428)
				cat.icon_img = R.drawable.cat_icon_428;
			if(cat.Id == 461)
				cat.icon_img = R.drawable.cat_icon_461;
			if(cat.Id == 490)
				cat.icon_img = R.drawable.cat_icon_490;
			if(cat.Id == 550)
				cat.icon_img = R.drawable.cat_icon_550;
			if(cat.Id == 677)
				cat.icon_img = R.drawable.cat_icon_677;
			if(cat.Id == 678)
				cat.icon_img = R.drawable.cat_icon_678;
		}
	}
	
	public String getMainSearchText()
	{
		return mMainSearchText;
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
		mLoadingView.setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        //setIntent(intent);
        handleIntent(intent);
    }
	
	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	if(mDrawerLayout.isDrawerOpen(mCatDrawerList))
        		mDrawerLayout.closeDrawer(mCatDrawerList);
        	SearchDatabase db = new SearchDatabase(this);
        	mMainPage.mPage = 0;
        	setType(DrawerListAdapter.SIMPLE_SEARCH);
        	String search_text = intent.getStringExtra(SearchManager.QUERY);
        	
        	Cursor c = db.getWordMatches(search_text, null);
        	
        	SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(search_text, null);
            mSearchView.setQuery(search_text, false);
            
        	try {
				mMainSearchText = URLEncoder.encode(search_text,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//selectItem(DrawerListAdapter.AD_ALL, DownloadFragment.MAIN_SEARCH);
        	getAdList(DownloadFragment.MAIN_SEARCH);
        	showFilterStateInfo(true);
        }
 
    }
	
	public void createDownloadFragment(URLWithParams params, String text, int type, Context context)
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		boolean null_check = false;
		if(mDownloadFragment == null)
		{
			mDownloadFragment = new DownloadFragment();
			null_check = true;
		}
		mDownloadFragment.setListener(this);
		mDownloadFragment.startDownload(params,
				text, 
				type, 
				context);
		if(null_check)
			fragmentManager.beginTransaction().replace(R.id.info_frame, mDownloadFragment).commitAllowingStateLoss();
	}
	
	public void createLowerBarSettingsFragment()
	{
		mDrawerLayout.closeDrawer(mCatDrawerList);
		LowerControlBarSettings fragment = new LowerControlBarSettings();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
	}
	
	public void setSlidingEnable(boolean is_enable){
		if(mSlidingPane.getEnable() != is_enable)
		mSlidingPane.setEnable(is_enable);
	}
	
	public void setNotSlidableZone(int zone){
		mSlidingPane.setNotSlidableZone(zone);
	}
	
	public void setSlidingPaneOffsetY(int offset){
		mSlidingPane.setOffsetY(offset);
	}
	
	public boolean isSlidingPaneOpen(){
		return mSlidingPane.isOpen();
	}
	
	public void showFilterStateInfo(boolean show){
		
		boolean is_full_empty = true;

		mFilterArray.clear();
		if(mCatIDsList.size() != 0){
			mFilterArray.add(getString(R.string.filter_category));
			is_full_empty = false;
		}
		if(mSelectedAdsId.size() != 0){
			mFilterArray.add(getString(R.string.filter_fav_ads));
			is_full_empty = false;
		}
		if(mSelectedUsers.size() != 0){
			mFilterArray.add(getString(R.string.filter_fav_users));
			is_full_empty = false;
		}
		if(mMainSearchText != null){
			mFilterArray.add(getString(R.string.filter_search));
			is_full_empty = false;
		}
		if(!is_full_empty && show){
			if(mFragmentFilterState == null){
				mFragmentFilterState = new FragmentFilterState();
				Bundle args = new Bundle();
				args.putStringArrayList("filter_array", mFilterArray);
				mFragmentFilterState.setArguments(args);
				getSupportFragmentManager().beginTransaction().add(R.id.filter_state, mFragmentFilterState).commit();
			}
			else{
				mFragmentFilterState.setFilter(mFilterArray);
			}
		}
		if(!show && mFragmentFilterState != null){
			if(!is_full_empty)
				mFragmentFilterState.hideFilter();
			else{
			getSupportFragmentManager().beginTransaction().remove(mFragmentFilterState).commit();
			mFragmentFilterState = null;
			}
		}
	}
	
	public List<Item> getItemList()
	{
		return mItems;
	}
	
	public Item getMenuItem(int index)
	{
		return mItems.get(index);
	}
	
	public Drawable getIconByMenuType(int type)
	{
		Drawable icon = null;
		switch (type) {
		case 1:
			icon = getResources().getDrawable(R.drawable.icon_ad_add);
			break;
		case 2:
			icon = getResources().getDrawable(R.drawable.icon_ad_my);
			break;
		case 3:
			icon = getResources().getDrawable(R.drawable.icon_ad_my);
			break;
		case 4:
			icon = getResources().getDrawable(R.drawable.icon_ad_category);
			break;
		case 5:
			icon = getResources().getDrawable(R.drawable.icon_ad_favorite_users);
		case 6:
			icon = getResources().getDrawable(R.drawable.icon_ad_favorite_ads);
			break;
		case 7:
			icon = getResources().getDrawable(R.drawable.icon_ad_filtr);
			break;
		case 8:
			icon = getResources().getDrawable(R.drawable.icon_ad_search);
			break;
		case 9:
			icon = getResources().getDrawable(R.drawable.icon_exit);
			break;
		default:
			break;
		}
		return icon;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	closeSearch();
	    	
	    	if(mYesNoFragment == null)
	    	{
		    	mYesNoFragment = new YesNoFragment();
		    	mYesNoFragment.setYesNoText(getString(R.string.exit_confirm), 
		    								getString(R.string.btn_yes), 
		    								getString(R.string.btn_no));
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(android.R.id.content, mYesNoFragment).commit();
		    }
	    	else
	    	{
	    		mYesNoFragment.closeAnim();
	    		mYesNoFragment = null;
	    	}
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	public void connectFailed()
	{
		ErrorFragment fragment = new ErrorFragment(getString(R.string.no_connection));
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
	}
	
	public void createCatalogItems()
	{
		mCategoryItems.clear();
		for(int i=0; i<mCategoryData.size(); i++)
		{
			if(mCategoryData.get(i).ParentId.equals(0))
				createCategoryTree(mCategoryData.get(i), 0);
		}
		TreeViewAdapter adapter = new TreeViewAdapter(MainControlBarActivity.this, mTreeElements, MainControlBarActivity.this);
		mCatDrawerList.setAdapter(adapter);
		//mDrawerAdapter.notifyDataSetChanged();
	}
	
	public void createCategoryTree(CategoryData parent_data, int tree_level)
	{
		for(int i=0; i<mCategoryData.size(); i++)
		{
			TreeElement parent_el = null;
			//if has child
			if(parent_data.Id.equals(mCategoryData.get(i).ParentId))
			{
				boolean parentExist = false; 
				for(int t=0; t<mParentElements.size(); t++)
				{
					if(mParentElements.get(t).getId().equals(parent_data.Id.toString()))
					{
						parent_el = mParentElements.get(t);
						parentExist = true;
						break;
					}
				}
				if(!parentExist)
				{
					mParentElements.add(new TreeElement(parent_data.Id.toString(),
							parent_data.icon_img,
							parent_data.Name, 
						false, 
						parent_data.hasChild, null, 0, false));
					parent_el = mParentElements.get(mParentElements.size()-1);
					
				}
				//if main parent
				if(parent_data.ParentId.equals(0))
				{
					boolean exist = false;
					for(int j=0; j<mTreeElements.size(); j++)
					{
						if(parent_data.Id.toString().equals(mTreeElements.get(j).getId()))
						{
							exist = true;
							break;
						}
					}
					if(!exist)
					{
						mTreeElements.add(parent_el);
					}
				}
				//add child
				TreeElement child_el = new TreeElement(mCategoryData.get(i).Id.toString(), 
						mCategoryData.get(i).icon_img,
						mCategoryData.get(i).Name, 
						true, 
						mCategoryData.get(i).hasChild, parent_el, tree_level+1, false);
				if(!mCategoryData.get(i).hasChild.equals(0))
				{
					mParentElements.add(child_el);
					createCategoryTree(mCategoryData.get(i), tree_level+1);
				}
			}
		}
	}
	
	public void createNeedCatTree(Integer pos, Integer id){
		
		mCatIDsList.add(id);
		if(mCategoryData.get(pos).hasChild.equals(1)){
			for(int i=0; i<mCategoryData.size(); i++){
					if(mCategoryData.get(i).ParentId.equals(id))
					{
						createNeedCatTree(i, mCategoryData.get(i).Id);
					}
			}
		}
	}
	
	
	public UserFunctions getUserFunctions(){
		return mUserFunctions;
	}
	
	public void checkProfile()
	{
		Drawable img = getResources().getDrawable(R.drawable.ic_drawer);
		mItems.clear();
		if(mUserFunctions.isUserLoggedIn(getApplicationContext()))
		{
			mUserData = mDBUser.getUserDetails();
			
			mDrawerListProfile = new DrawerListProfile(MainControlBarActivity.this, inflater, mUserData.get(LoginKeys.KEY_LOGIN), mUserData.get(LoginKeys.KEY_IMAGE_URL));
			mItems.add(mDrawerListProfile);
			mItems.add(new DrawerListHeaders(inflater, getString(R.string.title_private)));
	        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_my), getString(R.string.my_ad), DrawerListAdapter.AD_MY));
	        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_favorite_users), getString(R.string.ad_favorite_users), DrawerListAdapter.AD_FAVORITE_USERS));
	        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_favorite_ads), getString(R.string.ad_favorite_ads), DrawerListAdapter.AD_FAVORITE));
	        
	        /*ArrayList<Integer> array = mSavedData.getUserFavoriteAds();
	        if(array.size() == 0)
	        {
		        createDownloadFragment(getString(R.string.server_address)
		        		+"get_favorite_ads.php?user_id=" + mSavedData.getUserId(), 
		        		getString(R.string.load_data), 
		        		DownloadFragment.GET_FAVORITE_ADS, 
		        		MainControlBarActivity.this);
	        }
	        array = mSavedData.getUserFavoriteUsers();
	        if(array.size() == 0)
	        {
	        	createDownloadFragment(getString(R.string.server_address)
		        		+"get_favorite_users.php?user_id=" + mSavedData.getUserId(), 
		        		getString(R.string.load_data), 
		        		DownloadFragment.GET_FAVORITE_USERS, 
		        		MainControlBarActivity.this);
	        }*/
		}
		else
		{
			mItems.add(new DrawerListItems(getApplicationContext(), inflater, img, getString(R.string.user_enter), DrawerListAdapter.PROFILE));
		}
		mItems.add(new DrawerListHeaders(inflater, getString(R.string.title_common)));
		mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_add), getString(R.string.add_ad), DrawerListAdapter.AD_ADD));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_my), getString(R.string.all_ad), DrawerListAdapter.AD_ALL));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_category), getString(R.string.ad_category), DrawerListAdapter.AD_CATEGORY));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_filtr), getString(R.string.ad_filtr), DrawerListAdapter.AD_FILTR));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_search), getString(R.string.ad_search), DrawerListAdapter.AD_SEARCH));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_ad_search), getString(R.string.title_activity_settings), DrawerListAdapter.SETTINGS));
        mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_exit), getString(R.string.btn_exit), DrawerListAdapter.EXIT));
        mItems.add(new DrawerListLowerBar(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_gear), getString(R.string.lower_bar), DrawerListAdapter.LOWER_BAR));
        
        if(mSavedData.getLowerBarArrayPos().size() != 0)
        {
        	mLowerBarItems.clear();
        	ArrayList<Integer> array = mSavedData.getLowerBarArrayPos();
        	for(int i=0; i<array.size(); i++)
        	{
        		mLowerBarItems.add(getItemByType(array.get(i)));
        	}
        }
        else
        {
        	mLowerBarItems.clear();
	        for (int i = 0; i < mItems.size(); i++) 
			{
				if(mItems.get(i).getViewType()
						!= DrawerListAdapter.RowType.HEADER_ITEM.ordinal()&&
								mItems.get(i).getViewType()
						!= DrawerListAdapter.RowType.PROFILE_ITEM.ordinal() &&
								mItems.get(i).getViewType()
						!= DrawerListAdapter.RowType.LOWERBAR.ordinal())
				{
					mLowerBarItems.add(mItems.get(i));
				}
			}
	        
	        ArrayList<Integer> array_type = new ArrayList<Integer>();
	        
	        for(int i=0; i<mLowerBarItems.size(); i++)
	        {
	        	array_type.add(mLowerBarItems.get(i).getMenuType());
	        }
	        mSavedData.saveLowerBarArrayPos(array_type);
        }
        mMenuFragment = (FragmentMenu)getSupportFragmentManager().findFragmentByTag("menu_frame");
        if(mMenuFragment != null)
        	mMenuFragment.getAdapter().notifyDataSetChanged();
	}
	
	private Item getItemByType(int MenuType)
	{
		Item item = null;
		for(int i=0; i<mItems.size(); i++)
		{
			int tMenuType = mItems.get(i).getMenuType();
			if(tMenuType == MenuType)
			{
				item = mItems.get(i);
				break;
			}
		}
		return item;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_control_bar, menu);
		
		mSearchItem = menu.findItem(R.id.main_search);
		mSearchView = (SearchView)MenuItemCompat.getActionView(mSearchItem);
		mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mCatDrawerList);
		//menu.findItem(R.id.main_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(mYesNoFragment != null)
		{
			mYesNoFragment.closeAnim();
		}
		if(mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		
		switch(item.getItemId())
		{
		case R.id.main_search:
			return true;
		case R.id.action_refresh:
			mRefreshMenuItem = item;
			MenuItemCompat.setActionView(mRefreshMenuItem, R.layout.action_progressbar);
			//MenuItemCompat.expandActionView(mRefreshMenuItem);
			selectItem(DrawerListAdapter.AD_ALL, -1);
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(MainControlBarActivity.this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			selectItem(mItems.get(position).getMenuType(), -1);
		}
		
	}
	
	private void closeSearch(){
		if(mMainSearchText != null){
			if(mMainSearchText.trim() != ""){
				mMainSearchText = null;
				mSearchManager.stopSearch();
				MenuItemCompat.collapseActionView(mSearchItem);
			}
		}
		else if(MenuItemCompat.isActionViewExpanded(mSearchItem)){
			MenuItemCompat.collapseActionView(mSearchItem);
		}
	}
	
	
	public void createAdDetailsActivity(int pos){
		Intent intent = new Intent(MainControlBarActivity.this, AdDetailsActivity.class);
		if(mUserData != null)
			intent.putExtra("user_id", mUserData.get(LoginKeys.KEY_ID));
		intent.putExtra("type", mType);
		intent.putExtra("ad_pos", pos);
		intent.putExtra("page", mMainPage.mPage);
		intent.putExtra("item_count", mAdCountInOnePage);
		intent.putExtra("ad_data", mAdListData);
		
		intent.putExtra("mMainSearchText", mMainSearchText);
		intent.putExtra("mSelectedAreaId", mSelectedAreaId);
		intent.putExtra("mSelectedAdsId", mSelectedAdsId);
		intent.putExtra("mSelectedUsers", mSelectedUsers);
		intent.putExtra("mCatIDsList", mCatIDsList);
		intent.putExtra("mFilterArray", mFilterArray);
		intent.putExtra("mOutCatIDsList", mOutCatIDsList);
		startActivityForResult(intent, MainControlBarActivity.GET_SELECT_CAT);
	}
	
	public void selectItem(int type, int secondtype) 
	{
		if(type == 0)
			return;
		if(!getSupportActionBar().isShowing()){
			getSupportActionBar().show();
		}
		mType = type;
		if(mFavoriteUsersFilter != null)
		{
			getSupportFragmentManager().beginTransaction().remove(mFavoriteUsersFilter).commit();
			mFavoriteUsersFilter = null;
		}

		closeSearch();
		// TODO Auto-generated method stub
		//Fragment fragment = new PlanetFragment();
		//Bundle args = new Bundle();
		//args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		//fragment.setArguments(args);
		if(mDownloadFragment != null)
		{
			getSupportFragmentManager().beginTransaction().remove(mDownloadFragment).commit();
			mDownloadFragment = null;
		}
		int index = -1;
		for(int i=0; i<mItems.size(); i++)
		{
			int tMenuType = mItems.get(i).getMenuType();
			if(tMenuType == type)
			{
				index = i;
				break;
			}
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if(mNavType == 2){
			mNavType = 1;
			getSupportActionBar().setListNavigationCallbacks(mAreaSpinnerAdapter, mCallbackArea);
		}
		
		mCurrentType = mItems.get(index).getMenuType();
		
		switch (mCurrentType) {
		case DrawerListAdapter.PROFILE:
			if(mUserFunctions.isUserLoggedIn(getApplicationContext()))
			{
				Intent intent = new Intent(MainControlBarActivity.this, ProfileActivity.class);
				startActivityForResult(intent, UPDATE_AVATAR);
			}
			else
			{
				Intent intent = new Intent(MainControlBarActivity.this, LoginActivity.class);
				startActivityForResult(intent, REGISER_USER);
			}
			break;
		case DrawerListAdapter.AD_ADD:
			Intent ad_add_intent = new Intent(MainControlBarActivity.this, AdAddActivity.class);
			startActivityForResult(ad_add_intent, AD_ADD);
			break;
		case DrawerListAdapter.AD_MY:
			mMainPage.mPage = 0;
			mCatIDsList.clear();
			mSelectedAdsId.clear();
			mSelectedUsers.clear();
			
			FavUsersData user = new FavUsersData();
			user.id = Integer.valueOf(mUserData.get(LoginKeys.KEY_ID));
			user.name = "";
			mSelectedUsers.add(user);
			showFilterStateInfo(true);
			getAdList(secondtype);
			break;
		case DrawerListAdapter.AD_FAVORITE_USERS:
			mSelectedUsers = mDBUser.getFavUsers();
			if(mSelectedUsers.size() == 0)
			{
				new CustomToast(MainControlBarActivity.this).show("У вас нет избранных пользователей");
				return;
			}
			mNavType = 2;
			mCatIDsList.clear();
			mSelectedAdsId.clear();
			showFilterStateInfo(true);
			
			mSpinnerFavUsersAdapter.setData(mSelectedUsers);
			getSupportActionBar().setListNavigationCallbacks(mSpinnerFavUsersAdapter, mCallbackFavUsers);
			getAdList(secondtype);
			break;
		case DrawerListAdapter.AD_FAVORITE:
			mSelectedAdsId = mSavedData.getUserFavoriteAds();
			if(mSelectedAdsId.size() == 0){
				new CustomToast(MainControlBarActivity.this).show("У вас нет избранных объявлений");
				return;
			}
			mSelectedUsers.clear();
			mCatIDsList.clear();
			
			showFilterStateInfo(true);
			getAdList(secondtype);
			break;
		case DrawerListAdapter.AD_ALL:
			
			mSelectedAdsId.clear();
			mSelectedUsers.clear();
			if(mCatIDsList.size() != 0)
				showFilterStateInfo(true);
			else
				showFilterStateInfo(false);
			getAdList(secondtype);
			break;
		case DrawerListAdapter.AD_CATEGORY:
			//CategoryFragment fragment = new CategoryFragment();
			//fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			Intent cat_intent = new Intent(MainControlBarActivity.this, CategoryActivity.class);
			startActivityForResult(cat_intent, CAT_ACTIVITY);
			break;
		
		case DrawerListAdapter.LOWER_BAR:
			if(mSavedData.getLowerBarPos())
			{
				if(mLowerControlBar != null)
				{
					mLowerControlBar.closeAnim();
					mSavedData.saveLowerBarPos(false);
				}
			}
			else
			{
				mLowerBarFrame.setVisibility(View.VISIBLE);
				mLowerControlBar = new LowerControlBar();
				fragmentManager.beginTransaction().replace(R.id.lower_control, mLowerControlBar).commit();
				mSavedData.saveLowerBarPos(true);
				
			}
			break;
		case DrawerListAdapter.SETTINGS:
			Intent intent = new Intent(MainControlBarActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		case DrawerListAdapter.EXIT:
			finish();
			break;
		default:
			break;
		}		
		
		//mCatDrawerList.setItemChecked(index, true);
		setTitle(mItems.get(index).getMenuTitle());
		mDrawerLayout.closeDrawer(mCatDrawerList);
		mSlidingPane.closePane();
		
	}
	
	public void getAdList(int secondtype){
		if(secondtype == -1 || secondtype == DownloadData.LOAD_AD_LIST)
		{
			if(mMainPage != null)
				mMainPage.mPage = 0;
		}
		URLWithParams urlWithParams = new URLWithParams();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Integer start = 0;
		if(mMainPage != null)
			if(mMainPage.mPage != 0)
			{
				start = mMainPage.mPage * mAdCountInOnePage - mAdCountInOnePage;
			}
		params.add(new BasicNameValuePair("tag", AdTags.get_ad_list_tag));
		params.add(new BasicNameValuePair("area_id", String.valueOf(mSelectedAreaId)));
		params.add(new BasicNameValuePair("start_page", start.toString()));
		params.add(new BasicNameValuePair("item_count", mAdCountInOnePage.toString()));
		
		for (Integer ad_id : mSelectedAdsId) {
			params.add(new BasicNameValuePair("ad_id_array[]", ad_id.toString()));
		}
		for (FavUsersData user : mSelectedUsers) {
			params.add(new BasicNameValuePair("users_id[]", user.id.toString()));
		}
			
		if(mMainSearchText != null)
			if(mMainSearchText.trim().toString() != "")
			{
				params.add(new BasicNameValuePair("search_text", mMainSearchText));
			}
		for(int i=0; i<mCatIDsList.size(); i++){
			params.add(new BasicNameValuePair("cat_id_array[]", mCatIDsList.get(i).toString()));
		}
		for(Integer cat :mOutCatIDsList){
			params.add(new BasicNameValuePair("out_cats_id[]", cat.toString()));
		}
		urlWithParams.url = Urls.API + Urls.ADS;
		urlWithParams.nameValuePairs = params;
		if(secondtype == DownloadData.GET_MORE_AD || secondtype == DownloadData.LOAD_AD_LIST){
			DownloadData dd = new DownloadData(secondtype, this);
			dd.setParams(urlWithParams);
			dd.setListener(this);
			mDownloadDataTasks.add(dd);
			mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
		}
		else{
			createDownloadFragment(urlWithParams,
					getString(R.string.load_data), 
					DownloadFragment.LOAD_AD_LIST, 
					MainControlBarActivity.this);
		}
	}
	
	public ArrayList<AdListData> getAdArray(){
		return mAdListData;
	}
	
	public void setAdArray(ArrayList<AdListData> data){
		mAdListData = data;
	}
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
		super.onActivityResult(resultCode, resultCode, data);
		android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) getSupportFragmentManager().findFragmentByTag("ad_add");
	      if(fragment != null){
	            fragment.onActivityResult(requestCode, resultCode, data);
	      }
		if( data == null)
		{
			if (requestCode == REGISER_USER) {
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		        	Intent intent = new Intent(MainControlBarActivity.this, RegisterActivity.class);
					startActivityForResult(intent, CHECK_PROFILE);
		        }
		        else if(resultCode == CHECK_PROFILE)
		        {
			        	new Thread(new Runnable() {
			                @Override
			                public void run() {
			                  // Then bump it back onto the UI thread
			                  runOnUiThread(new Runnable() {
			                    @Override
			                    public void run() {
			                    	checkProfile();
			                    }
			                  });
			                }
			              }).start();
		        }
		    }
			else if (requestCode == CHECK_PROFILE) {
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		        	new Thread(new Runnable() {
		                @Override
		                public void run() {
		                  // Then bump it back onto the UI thread
		                  runOnUiThread(new Runnable() {
		                    @Override
		                    public void run() {
		                    	checkProfile();
		                    }
		                  });
		                }
		              }).start();
		        }
		    }
			else if(requestCode == AD_ADD){
				if(resultCode == RESULT_OK)
					selectItem(DrawerListAdapter.AD_ALL, -1);
			}
			else if(requestCode == UPDATE_AVATAR){
				if(resultCode == RESULT_OK){
					mDrawerListProfile.updateAvatar(mDBUser.getUserDetails().get(LoginKeys.KEY_IMAGE_URL));
				}
				else if(resultCode == ProfileActivity.LOG_OUT){
					mSavedData.logOut();
					mMainPage.logOut();
					checkProfile();
					selectItem(DrawerListAdapter.AD_ALL, -1);
				}
			}
		}
		else{
			if(requestCode == GET_SELECT_CAT){
				if(resultCode == RESULT_OK){
					mCatIDsList.clear();
					int cat_pos = data.getIntExtra("cat_pos", -1);
					int cat_id = data.getIntExtra("cat_id", -1);
					createNeedCatTree(cat_pos, cat_id);
					selectItem(DrawerListAdapter.AD_ALL, -1);
				}
			}
			else if(requestCode == CAT_ACTIVITY){
				if(resultCode == RESULT_OK){
					mOutCatIDsList =  data.getIntegerArrayListExtra("cats_id");
				}
			}
		}
	}
	
	
	public void setMainPageData(ArrayList<AdListData> data)
	{
		FragmentManager fragmentManager1 = getSupportFragmentManager();
		android.support.v4.app.Fragment fragmment = fragmentManager1.findFragmentByTag(MAIN_PAGE);
		
		if(fragmment == null){
			mMainPage = new MainPage();
			fragmentManager1.beginTransaction().replace(R.id.content_frame, mMainPage, MAIN_PAGE).commit();
		}
		else{
			if(mMainPage == null)
				mMainPage = (MainPage) fragmment;
			mMainPage.setParent(this);
		}
		mAdListData = data;
		mMainPage.setData(data);
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}

	public void setNull()
	{
		getSupportFragmentManager().beginTransaction().remove(mDownloadFragment).commit();
		mDownloadFragment = null;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void getUserStateComplete(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDownloadCompleteState(ArrayList<? extends Object> data_array) {
		// TODO Auto-generated method stub

			if(mRefreshMenuItem != null){
				MenuItemCompat.getActionView(mRefreshMenuItem).clearAnimation();
				MenuItemCompat.setActionView(mRefreshMenuItem, null);
				mRefreshMenuItem = null;
			}
			ArrayList<String> checkConn = (ArrayList<String>)data_array;
			if(checkConn.size() != 0)
				if(checkConn.get(0) == "no_connection"){
					new CustomToast(this).showErrorToast(getString(R.string.no_connection));
					setMainPageData(mAdListData);
					return;
				}
				else if(checkConn.get(0) == "error"){
					new CustomToast(this).showErrorToast(getString(R.string.error_load_data));
					setMainPageData(mAdListData);
					return;
				}
			ArrayList<AdListData> data = (ArrayList<AdListData>)data_array;
			setMainPageData(data);
		
	}

	@Override
	public void getDownloadDCompleteState(int loadType, ArrayList<? extends Object> data_array) {
		// TODO Auto-generated method stub
		ArrayList<String> checkConn = (ArrayList<String>)data_array;
		if(checkConn.size() != 0)
			if(checkConn.get(0) == "no_connection"){
				if(loadType == DownloadData.GET_MORE_AD){
					mMainPage.loadCircleState(MainPage.LOAD_CIRCLE_ERROR, null);
					return;
				}
				mStartAppFragment.startError(getString(R.string.no_connection));
				for(int i=0; i<mDownloadDataTasks.size(); i++)
		        {
					mDownloadDataTasks.get(i).cancel(true);
		        }
				mDownloadDataTasks.clear();
				return;
			}
			else if(checkConn.get(0) == "error"){
				if(loadType == DownloadData.GET_MORE_AD){
					mMainPage.loadCircleState(MainPage.LOAD_CIRCLE_ERROR, null);
					return;
				}
				mStartAppFragment.startError(getString(R.string.error_load_data));
				for(int i=0; i<mDownloadDataTasks.size(); i++)
		        {
					mDownloadDataTasks.get(i).cancel(true);
		        }
				mDownloadDataTasks.clear();
				return;
			}
		switch (loadType) {
		case DownloadData.MAIN_DATA_UPDATE_CHECK:
			ArrayList<UpdateState> update_state = (ArrayList<UpdateState>)data_array;
			MainDatabaseHandler db = new MainDatabaseHandler(getApplicationContext());
			int area_count = db.getAreaRowCount();
			int _cat_count = db.getCategoryRowCount();
			if(update_state.get(0).area || area_count == 0){
				db.clearTableArea();
				URLWithParams urlWithParams = new URLWithParams();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("tag", AdTags.get_area_data));
				urlWithParams.url = Urls.API + Urls.COMMON;
				urlWithParams.nameValuePairs = params;
				
				DownloadData dd_task = new DownloadData(DownloadData.MAIN_DATA_AREA, MainControlBarActivity.this);
				dd_task.setParams(urlWithParams);
				mDownloadDataTasks.add(dd_task);
				mDownloadDataTasks.get(mDownloadDataTasks.size()-1).setListener(this);
				mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
			}
			else
			{
				mAreaData = db.getAreaData();
				setAreasIcons();
			}
			if(update_state.get(0).category || _cat_count == 0){
				db.clearTableCategory();
				URLWithParams urlWithParams2 = new URLWithParams();
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("tag", AdTags.get_category_data));
				urlWithParams2.url = Urls.API + Urls.COMMON;
				urlWithParams2.nameValuePairs = params2;
				
				DownloadData dd_task = new DownloadData(DownloadData.MAIN_DATA_CATEGORY, MainControlBarActivity.this);
				dd_task.setParams(urlWithParams2);
				mDownloadDataTasks.add(dd_task);
				mDownloadDataTasks.get(mDownloadDataTasks.size()-1).setListener(this);
				mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
			}
			else
			{
				mCategoryData = db.getCategoryData();
				//setCatsIcons();
			}
			db.closeDB();
			break;
		case DownloadData.MAIN_DATA_AREA:
			ArrayList<AreaData> area_data = (ArrayList<AreaData>)data_array;
			mAreaData = area_data;
			setAreasIcons();
			break;
		case DownloadData.MAIN_DATA_CATEGORY:
			ArrayList<CategoryData> category_data = (ArrayList<CategoryData>)data_array;
			mCategoryData = category_data;
			//setCatsIcons();
			break;
		case DownloadData.GET_SLIDER_URLS:
			ArrayList<String> slider_data = (ArrayList<String>)data_array;
			mSliderData = slider_data;
			break;
		case DownloadData.GET_MORE_AD:
			
			ArrayList<AdListData> data_ad = (ArrayList<AdListData>)data_array;
			if(data_ad == null){
				mMainPage.loadCircleState(MainPage.LOAD_CIRCLE_SUCCESS, data_ad);
				return;
			}
			mMainPage.setMoreData(data_ad);
			cancelTask(loadType);
			return;
		case DownloadData.LOAD_AD_LIST:
			if(data_array == null){
				mMainPage.stopRefreshing();
				return;
			}
			ArrayList<AdListData> data = (ArrayList<AdListData>)data_array;
			mMainPage.setRefreshedData(data);
			mMainPage.stopRefreshing();
			cancelTask(loadType);
			return;
		default:
			break;
		}
		
    	cancelTask(loadType);
    	
		if(mAreaData != null
			&& mCategoryData != null
			&& mSliderData != null)
		{
			if(mAreaData.size() != 0 && mCategoryData.size() != 0 && mSliderData.size() != 0){
				mAreaSpinnerAdapter.setData(mAreaData);
				mAreaSpinnerAdapter.notifyDataSetChanged();
				if(mSelectedAreaId != -1){
					for (int i=0; i<mAreaData.size(); i++) {
						if(mAreaData.get(i).id == mSelectedAreaId){
							getSupportActionBar().setSelectedNavigationItem(i);
							break;
						}
					}
					
				}
				createCatalogItems();
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mStartAppFragment.startClose();
				getSupportActionBar().show();
				
				if(mSavedData.isFirstAppRun()){
					new HMDialog(MainControlBarActivity.this).show();
					new Handler().postDelayed(openDrawerRunnable(), 200);
				}
			}
		}
		
	}
	
	private void cancelTask(int loadType){
		for(int i=0; i<mDownloadDataTasks.size(); i++)
    	{
    		if(mDownloadDataTasks.get(i).getLoadType() == loadType)
    			mDownloadDataTasks.remove(i);
    	}
	}
	
	public void openMenu(){
		new Handler().postDelayed(openDrawerRunnable(), 200);
	}



	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		int t;
		t=0;
	}


	public void setListViewState(boolean state){
		mIsListViewScroll = state;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public DatabaseHandler getDBUser(){
		return mDBUser;
	}
}
