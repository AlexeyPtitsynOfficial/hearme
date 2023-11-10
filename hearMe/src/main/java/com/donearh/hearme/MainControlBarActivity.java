package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchManager.OnCancelListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.donearh.hearme.DownloadData.DownloadDCompleteListener;
import com.donearh.hearme.DownloadFragment.DownloadCompleteListener;
import com.donearh.hearme.ExtendedSearchDialog.OnExtendedSearch;
import com.donearh.hearme.MySlidingPaneLayout.OnSlideDetectListener;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.datatypes.LowerBarData;
import com.donearh.hearme.datatypes.SliderData;
import com.donearh.hearme.datatypes.UpdateState;
import com.donearh.hearme.library.DatabaseHandler;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.hearme.library.SearchDatabase;
import com.donearh.hearme.library.UserFunctions;
import com.donearh.hearme.library.UserFunctions.UserLoginListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;

import android.view.animation.Transformation;

public class MainControlBarActivity extends AppCompatActivity implements AnimationListener,
																		OnRefreshListener,
																		UserLoginListener,
																		DownloadCompleteListener,
																		DownloadDCompleteListener, 
																		OnCancelListener,
																		OnSlideDetectListener,
																		OnExtendedSearch,
																		ConnectionCallbacks, 
																		OnConnectionFailedListener,
																		MyCatsRecyclerViewAdapter.OnCatChoosedListener
{

	static final int REGISER_USER = 0;
	static final int CHECK_PROFILE = 1;
	static final int GET_SELECT_CAT = 2;
	static final int PROFILE_ACTIVITY = 3;
	static final int AD_ADD = 4;
	static final int CAT_ACTIVITY = 5;
	static final int SETTINGS = 6;
	static final int LOWER_BAR_SETTINGS = 7;
	
	static final int EXPIRED_REFRESH_TOKEN = 8;
	static final int UPDATE_REFRESH_TOKEN = 9;
	
	public static final int RC_SIGN_IN = 101;
	static final String MAIN_PAGE = "main_page";
	
	public final static int LOOPS = 10; 
	public final static int FIRST_PAGE = 1;
	public final static float BIG_SCALE = 1.0f;
	public final static float SMALL_SCALE = 0.6f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	
	private static final String IMAGE_CACHE_DIR = "common_dir";
	
	FragmentManager mFragmentManager;
	private boolean mIntentInProgress;
	public boolean mAppStarted = false;
	
	protected Account mAccount;
	private boolean mAccExpired = false;
	Integer acc_async_num;
	private Integer mAccPos = -1;
	
	private ProgressDialog mConnectionProgressDialog;

	private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    
    private AccountManager mAccountManager;
    
    private TextView mFilterInfo;
	
	private SharedPreferences mSettingsPref;

	private DrawerListProfile mDrawerListProfile;
	public MainPage mMainPage;
	
	public MainPage getMainPage() {
		return mMainPage;
	}

	public DatabaseHandler mDBUser;
	
	public ArrayList<AdListData> mAdListData;
	
	private UserFunctions mUserFunctions;
	
	private StartAppFragment mStartAppFragment;
	public YesNoFragment mYesNoFragment;
	private FavoriteUsersFilter mFavoriteUsersFilter;
	
	private Spinner mAreaSpinner;
	private boolean mAreaNaviFirstHit = true;
	private boolean mFavUsersNaviFirstHit = true;
	
	private SpinnerAreaAdapter mAreaSpinnerAdapter;
	
	public DownloadFragment mDownloadFragment;
	private FragmentFilterState mFragmentFilterState;
	public LowerControlBar mLowerControlBar;
	
	private DownloadData mTaskUpdateChecker;
	private ArrayList<DownloadData> mDownloadDataTasks;
	
	public FrameLayout mLowerBarFrame;
	
	private LayoutInflater inflater;
	//private DrawerListAdapter mDrawerAdapter;
	
	public int mPrevType = -1;
	public int mLoadedType = -1;
	public int mType = -1;
	public int mSecondType = -1;
	public int mCurrentType = -1;
	
	boolean mIsListViewScroll = false;
	
	private SearchManager mSearchManager;
	private SearchView mSearchView;
	private MenuItem mSearchItem;
	private ShareActionProvider mShareActionProvider;
	 
	public SavedData mSavedData;
	private DrawerLayout mDrawerLayout;
	private ListView mMenuDrawerList;
	private DrawerListAdapter mMenuDrawerAdapter;
	private View mCatsDrawerView;
	private String mCatFilterText;
	private TextView mCatHeaderText;
	private RecyclerView mCatRecycleView;
	//private ListView mCatDrawer;
	private FloatingActionButton mBtnCatBack;
	private MyCatsRecyclerViewAdapter mMyCatsRecyclerViewAdapter;
	//private CategoryAdapter mCategoryAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ArrayList<Item> mItems;
	private ArrayList<Item> mCategoryItems;
	public ArrayList<LowerBarData> mLowerBarData;

	boolean mSlidingEnable = true;
	
	private String mMainSearchText;
	private ArrayList<Integer> mExtSearchFilterList = new ArrayList<Integer>();
	
	private FrameLayout mContentFrame;
	
	//Changing views
	private RelativeLayout mContentView;
    private ImageView mLoadingView;
    private int mShortAnimationDuration;
    
    //menu 
    private MenuItem mRefreshMenuItem; 
    
    private Integer mAdCountInOnePage = 30;
    
    private Integer mAddedAdId;
    
    private Integer mSelectedMainParentId = -1;
    
    private int mSliderCount;
	
    public void setMainParentId(int id){
    	mSelectedMainParentId = id;
    }
    public Integer getSelectedMainParentId() {
		return mSelectedMainParentId;
	}
    
    
    public void setSliderCount(int count){
    	mSliderCount = count;
    }
    
    public int getSliderCount(){
    	return mSliderCount;
    }

	public ArrayList<AreaData> mAreaData = new ArrayList<AreaData>();
	public ArrayList<CategoryData> mCategoryData = new ArrayList<CategoryData>();
	public ArrayList<SliderData> mSliderData = new ArrayList<SliderData>();
	
	public Integer mSelectedAreaId = -1;
	public ArrayList<Integer> mSelectedAdsId = new ArrayList<Integer>();
	public ArrayList<Integer> mSelectedUsers = new ArrayList<Integer>();
	public ArrayList<Integer> mCatIDsList = new ArrayList<Integer>();
	private ArrayList<String> mFilterArray = new ArrayList<String>();
	private ArrayList<Integer> mOutCatIDsList = new ArrayList<Integer>();
	
	private ArrayList<AreaIcons> mAreaIcons = new ArrayList<AreaIcons>();

	@Override
	public void onCatChoosed(String cat_tree_text, CategoryData cat) {
		//mFilterInfo.setText(cat_tree_text);
		mCatFilterText = cat_tree_text;
		mCatIDsList.clear();
		if(cat.id == -1){

		}
		else if(cat.has_child == 0)
			mCatIDsList.add(cat.id);
		else {
			for(int i=0; i<mCategoryData.size(); i++){
				if(cat.id.equals(mCategoryData.get(i).id)){
					createNeedCatTree(i, Integer.valueOf(cat.id));
					getMainParentId(i);
					break;
				}
			}
		}
		setMainParentId(-1);

		getMainPage().setCurrentSlide(1);
		getMainPage().getSliderPagerAdapterNoRotate().updateData(getSelectedMainParentId());
		selectItem(DrawerListAdapter.AD_ALL, -1);
	}

	public class AreaIcons{
		Integer id;
		Drawable image;
	}
	
	Animation mCatAddAnim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//savedInstanceState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onCreate(savedInstanceState);
		Log.i("hm", "start " + getClass().getName());
		setContentView(R.layout.activity_main_control_bar);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getSupportActionBar().hide();

		mAccountManager = AccountManager.get(this);

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.build();
		// ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				//.addScope(new Scope(Scopes.PROFILE))
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				//.addApi(AppIndex.API)
				.build();

		mFilterInfo = (TextView) findViewById(R.id.filter_info);
		mFilterInfo.setVisibility(View.GONE);

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

		mContentFrame = (FrameLayout) findViewById(R.id.content_frame);

		mSettingsPref = PreferenceManager.getDefaultSharedPreferences(this);
		mAdCountInOnePage = Integer.valueOf(mSettingsPref.getString(SettingsActivity.LOAD_ADS_NUM, "30"));

		mDBUser = new DatabaseHandler(MainControlBarActivity.this);

		mSearchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
		mSearchManager.setOnCancelListener(this);

		mSearchManager.setOnDismissListener(new SearchManager.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				int t;
				t = 0;
			}
		});

		mAdListData = new ArrayList<AdListData>();
		mUserFunctions = new UserFunctions();
		mUserFunctions.setListener(this);
		mSavedData = SavedData.getInstance(MainControlBarActivity.this);

		mCategoryData = new ArrayList<CategoryData>();

		mTitle = mDrawerTitle = getTitle();

		/*mSlidingPane = (MySlidingPaneLayout) findViewById(R.id.sliding_pane);
		mSlidingPane.setOnSlideDetectListener(this);
		mSlidingPane.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPanelOpened(View arg0) {
				// TODO Auto-generated method stub
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
				if (mFragmentFilterState.isFilterShowing() && !isFilterStateEmpty()) {
					mFragmentFilterState.hideFilter();
				}
			}

			@Override
			public void onPanelClosed(View arg0) {
				// TODO Auto-generated method stub
				if (mFragmentFilterState.isFilterShowing() && !isFilterStateEmpty())
					mFragmentFilterState.showFilter();
			}
		});*/

		Drawable img = getResources().getDrawable(R.drawable.ic_drawer);


		mCategoryItems = new ArrayList<Item>();
		mLowerBarData = new ArrayList<LowerBarData>();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//mCatDrawerList = (ListView) findViewById(R.id.left_drawer);

		mMenuDrawerList = (ListView)findViewById(R.id.menu_list);
		mMenuDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCatIDsList.clear();
				setMainParentId(-1);
				getMainPage().setCurrentSlide(1);
				getMainPage().getSliderPagerAdapterNoRotate().updateData(getSelectedMainParentId());
				selectItem(mItems.get(position).getMenuType(),-1);
			}
		});

		mCatsDrawerView = (RelativeLayout)findViewById(R.id.drawer_view);
		mCatHeaderText = (TextView) mCatsDrawerView.findViewById(R.id.header_text);
		mCatRecycleView = (RecyclerView)mCatsDrawerView.findViewById(R.id.cat_list);
		//mCatDrawer= (ListView) mCatsDrawerView.findViewById(R.id.cat_list);

		mBtnCatBack = (FloatingActionButton) mCatsDrawerView.findViewById(R.id.btn_back);
		mBtnCatBack.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				mMyCatsRecyclerViewAdapter.backPress();
			}
		});
		mBtnCatBack.setVisibility(View.GONE);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout,
				R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
			}

			public void onDrawerOpened(View drawerView) {

				getSupportActionBar().setTitle(mDrawerTitle);
				closeSearch();
				supportInvalidateOptionsMenu();
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			}

		};

		mDrawerToggle.setDrawerIndicatorEnabled(true);


		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mLowerBarFrame = (FrameLayout) findViewById(R.id.lower_control);
		mLowerBarFrame.setVisibility(View.GONE);
		if (mSavedData.getLowerBarPos()) {
			showLowerBar();
		}

		mFragmentManager = getSupportFragmentManager();
		mStartAppFragment = new StartAppFragment();
		mFragmentManager.beginTransaction().replace(R.id.start_app_frame, mStartAppFragment).commit();

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		mAreaSpinnerAdapter = new SpinnerAreaAdapter(this,
				R.layout.adapter_spinner_area,
				mAreaData);

		mAreaSpinner = (Spinner) findViewById(R.id.area_spinner);
		mAreaSpinner.setAdapter(mAreaSpinnerAdapter);
		mAreaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				// TODO Auto-generated method stub

				mSelectedAreaId = (int) id;
				mSavedData.saveSelectedAreaId(mSelectedAreaId);
				//selectItem(mType, -1);
				mCurrentType = DrawerListAdapter.AD_ALL;
				mMainPage.refreshTrigger(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		mSelectedAreaId = mSavedData.getMainAreaId();
		mCatIDsList = mSavedData.getSelectedCats();
		if(mCatIDsList.size() != 0)
			mCatFilterText = mSavedData.getSelectedCatsFilterText();
		mOutCatIDsList = mSavedData.getOutCats();

		if (mItems == null)
			mItems = new ArrayList<Item>();

		mFragmentFilterState = new FragmentFilterState();
		getSupportFragmentManager().beginTransaction().add(R.id.filter_state, mFragmentFilterState).commit();

		mDownloadDataTasks = (ArrayList<DownloadData>) getLastCustomNonConfigurationInstance();
		if (mDownloadDataTasks == null) {
			mDownloadDataTasks = new ArrayList<DownloadData>();
			initApp();
		} else {
			for (int i = 0; i < mDownloadDataTasks.size(); i++) {
				if (mDownloadDataTasks.get(i).isCancelled()) {
					mDownloadDataTasks.get(i).setListener(this);
					mDownloadDataTasks.get(i).execute();
				}
			}
		}


		mMainPage = new MainPage();

		Bundle bundle = new Bundle();
		mMainPage.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mMainPage, MAIN_PAGE).commit();


		mCatAddAnim = AnimationUtils.loadAnimation(
				MainControlBarActivity.this, R.anim.listview_top_to_down);
		mCatAddAnim.setDuration(500);
		mCatAddAnim.setAnimationListener(this);

		final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.btn_fab_add_ad);
		fab_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectItem(DrawerListAdapter.AD_ADD,-1);
			}
		});
	}
	
	private class AccessTokenActionsTask extends AsyncTask<String, Void, String> {

		private String social_type;
		private String type;
		protected View view;
	    @Override
	    protected String doInBackground(String... params) {
	    	type = params[0];
	    	social_type = mAccountManager.getUserData(mAccount, AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = "";//Plus.AccountApi.getAccountName(mGoogleApiClient);
		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(MainControlBarActivity.this, account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(mAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, MainControlBarActivity.this, null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(mAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, MainControlBarActivity.this, null, null);
	   	                    	bnd = future.getResult();
	   	                    	access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	   	                    }
	   				 	}
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
				return access_token;
	    	}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(String access_token) {
	        if (access_token != null) {
	        	String url;
	        	if(social_type.equals("1")){
	        		url = Urls.G_USER;
        		}else{
        			url = Urls.USER;
        		}
	        	if(type == "fav_users"){
					mMainPage.refreshTrigger(false);
	        		mFragmentFilterState.setAccessToken(url, access_token);
	        	}
	        	else if(type == "ad_my"){
					mMainPage.refreshTrigger(false);
	        		getAdList(DownloadData.LOAD_AD_LIST, access_token);

	        	}
	        	else if(type == "logout" || type == "remove_account"){
	        		
        			mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
        			mAccountManager.setUserData(mAccount, AccountGeneral.KEY_REFRESH_TOKEN, null);
        			
        			HashMap<String, String> params = new HashMap<String, String>();
        			params.put("url", Urls.USER);
        			params.put("tag", type);
        			params.put("access_token", access_token);
        			//params.put("refresh_token", mTokenData.get(AccountGeneral.KEY_REFRESH_TOKEN));
        			
        			new JSONParser(null, null).execute(params);
        			
        			mConnectionProgressDialog.dismiss();
        			logout();
	        	}
	        	//if(getCurrentMenuType() == DrawerListAdapter.AD_MY)
	        	//	getAdList(-1, access_token);
	        } else {
	          // There was some error getting the ID Token
	          // ...
	        }
	    }

	}
	
	public boolean isLogin(){
		if(mAccount != null)
			return true;
		else
			return false;
	}
	
	public int getUserPos(){
		return mAccPos;
	}
	@Override
	public void onPostCreate(Bundle savedInstanceState,
			PersistableBundle persistentState) {
		// TODO Auto-generated method stub
		mDrawerToggle.syncState();
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mDownloadDataTasks.clear();
		mDownloadDataTasks = null;
		
		mSelectedAdsId.clear();
		mSelectedUsers.clear();
		mSavedData = null;
		mSearchManager = null;
		
		FragmentManager fm = getSupportFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    fm.popBackStack();
		}
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mItems == null)
			mItems = new ArrayList<Item>();
		// Connect to Drive and Google+
		//mGoogleApiClient.connect();
		onConnectionFailed(null);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"MainControlBar Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.donearh.hearme/http/host/path")
		);
		AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"MainControlBar Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.donearh.hearme/http/host/path")
		);
		AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}*/
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		for(int i=0; i<mDownloadDataTasks.size(); i++)
        {
			mDownloadDataTasks.get(i).cancel(true);
        }
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		urlWithParams.url = Urls.COMMON;
		urlWithParams.nameValuePairs = params;
		
		DownloadData dd_task = new DownloadData(DownloadData.MAIN_DATA_UPDATE_CHECK, MainControlBarActivity.this);
		dd_task.setParams(urlWithParams);
		dd_task.setListener(this);
		mDownloadDataTasks.add(dd_task);
		mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
		
		URLWithParams urlWithParams3 = new URLWithParams();
		List<NameValuePair> params3 = new ArrayList<NameValuePair>();
		params3.add(new BasicNameValuePair("tag", AdTags.get_slider_urls));
		urlWithParams3.url = Urls.COMMON;
		urlWithParams3.nameValuePairs = params3;
		
		DownloadData dd_task2 = new DownloadData(DownloadData.GET_SLIDER_URLS, MainControlBarActivity.this);
		dd_task2.setParams(urlWithParams3);
		dd_task2.setListener(this);
		mDownloadDataTasks.add(dd_task2);
		mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
		
		//checkProfile();
	}
	
	public GoogleApiClient getApiClient(){
		return mGoogleApiClient;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		/*if (!mIntentInProgress && result.hasResolution()) {
		    try {
		      mIntentInProgress = true;
		      startIntentSenderForResult(result.getResolution().getIntentSender(),
		          RC_SIGN_IN, null, 0, 0, 0);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		  }*/
		if(mAccount == null)
			checkProfile();
	}
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		if(mAccount == null)
			checkProfile();
	}
	
	
	public void showLowerBar(){
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		layoutParams.setMargins(0, 0, 0, (int)px);
		mContentFrame.setLayoutParams(layoutParams);
		FragmentManager fragmentManager = getSupportFragmentManager();
		mLowerControlBar = new LowerControlBar();
		fragmentManager.beginTransaction().replace(R.id.lower_control, mLowerControlBar).commit();
		mLowerBarFrame.setVisibility(View.VISIBLE);
	}
	
	public void closeLowerBar(){
		if(mLowerControlBar != null)
		{
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(0, 0, 0, 0);
			mContentFrame.setLayoutParams(layoutParams);
			mLowerControlBar.closeAnim();
			mSavedData.saveLowerBarPos(false);
		}
	}
	public void setSliderData(ArrayList<String> data)
	{
		
	}
	
	public ArrayList<SliderData> getSliderData()
	{
		return mSliderData;
	}
	
	private Runnable openMenuDrawerRunnable() {
	    return new Runnable() {

	        @Override
	        public void run() {
	            mDrawerLayout.openDrawer(mMenuDrawerList);
	        }
	    };
	}

	private Runnable openCatsDrawerRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				mDrawerLayout.openDrawer(mCatsDrawerView);
			}
		};
	}
	
	public int getType()
	{
		return mType;
	}
	
	public int getLoadedType(){
		return mLoadedType;
	}
	
	public int getSecondType(){
		return mSecondType;
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
        	if(mDrawerLayout.isDrawerOpen(mCatsDrawerView))
        		mDrawerLayout.closeDrawer(mCatsDrawerView);
        	SearchDatabase db = new SearchDatabase(this);
        	mMainPage.mPage = 0;
        	//setType(DrawerListAdapter.SIMPLE_SEARCH);
        	String search_text = intent.getStringExtra(SearchManager.QUERY).trim();
        	
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
        	mMainPage.refreshTrigger(true);
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
		
		mDrawerLayout.closeDrawer(mCatsDrawerView);
		Intent intent = new Intent(MainControlBarActivity.this,LowerControlBarSettings.class);
		//intent.putExtra("items", mLowerBarItems);
		startActivityForResult(intent, LOWER_BAR_SETTINGS);
	}
	
	/*public Drawable getIconByMenuType(int type)
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
			icon = getResources().getDrawable(R.drawable.icon_cat_filter);
			break;
		case 5:
			icon = getResources().getDrawable(R.drawable.icon_fav_user);
		case 6:
			icon = getResources().getDrawable(R.drawable.icon_fav_ad);
			break;
		case 7:
			icon = getResources().getDrawable(R.drawable.icon_cat_filter);
			break;
		case 8:
			icon = getResources().getDrawable(R.drawable.icon_ext_search);
			break;
		case 9:
			icon = getResources().getDrawable(R.drawable.icon_exit);
			break;
		default:
			break;
		}
		return icon;
	}*/
	
	/*public void setSlidingEnable(boolean is_enable){
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
	}*/
	
	public void showFilterStateInfo(boolean show){
		
		boolean filter_not_exist = true;

		mFilterArray.clear();
		if(mCatIDsList.size() != 0){
			mFilterArray.add(mCatFilterText);
			filter_not_exist = false;
		}
		if(mSelectedAdsId.size() != 0){
			mFilterArray.add(getString(R.string.favorite_ads));
			filter_not_exist = false;
		}
		if(getCurrentMenuType() == DrawerListAdapter.AD_MY){
			mFilterArray.add(getString(R.string.my_ad));
			filter_not_exist = false;
		}
		else if(!mFragmentFilterState.isFilterShowing() && (mSelectedUsers.size() != 0 || getCurrentMenuType() == DrawerListAdapter.AD_FAVORITE_USERS)){
			mFilterArray.add(getString(R.string.favorite_users));
			filter_not_exist = false;
		}
		if(mMainSearchText != null){
			mFilterArray.add(getString(R.string.filter_search));
			filter_not_exist = false;
		}
		if(!filter_not_exist && show){
			mFilterInfo.setVisibility(View.VISIBLE);
			mFilterInfo.setText(mFilterArray.get(0));
			if(mSelectedUsers.size() != 0 && getCurrentMenuType() == DrawerListAdapter.AD_FAVORITE_USERS){
				mFragmentFilterState.openFilter(mFilterArray, mSelectedUsers);
				new AccessTokenActionsTask().execute("fav_users");
			}
		}
		if(!show && mFragmentFilterState != null){
			if(!filter_not_exist || !show){
				mFilterInfo.setVisibility(View.GONE);
				mFragmentFilterState.closeFilter();
			}
		}
	}
	
	public FragmentFilterState getFragmentFilterState (){
		return mFragmentFilterState;
	}
	
	public boolean isFilterStateEmpty(){
		
		if(mFilterArray == null)
			return true;
		if(mFilterArray.size() == 0)
			return true;
		else
			return false;
	}
	
	public boolean isFilterShowing(){
		if(mFragmentFilterState == null)
			return false;
		return mFragmentFilterState.isFilterShowing();
	}
	
	public List<Item> getItemList()
	{
		return mItems;
	}
	
	public Item getMenuItem(int index)
	{
		return mItems.get(index);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	if(MenuItemCompat.isActionViewExpanded(mSearchItem)){
	    		closeSearch();
	    		return true;
	    	}

			if(mDrawerLayout.isDrawerOpen(mCatsDrawerView))
				mDrawerLayout.closeDrawer(mCatsDrawerView);
			else if(mDrawerLayout.isDrawerOpen(mMenuDrawerList))
				mDrawerLayout.closeDrawer(mMenuDrawerList);
	    	else if(mYesNoFragment == null)
	    	{
		    	mYesNoFragment = new YesNoFragment();
		    	mYesNoFragment.setYesNoText(getString(R.string.are_u_sure_to_exit),
		    								getString(R.string.exit),
		    								getString(R.string.btn_cancel));
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
		//for(int i=0; i<mCategoryData.size(); i++)
		//{
			//if(mCategoryData.get(i).parent_id.equals(0))
				//createCategoryTree(mCategoryData.get(i), 0);
		//}

		//TreeViewAdapter adapter = new TreeViewAdapter(MainControlBarActivity.this, mTreeElements, MainControlBarActivity.this);
		//mCatDrawerList.setAdapter(adapter);
		mMyCatsRecyclerViewAdapter = new MyCatsRecyclerViewAdapter(MyCatsRecyclerViewAdapter.DRAWER_TYPE, mBtnCatBack, mCatHeaderText,getApplicationContext(), mCategoryData, this);
		//mCatDrawer.setAdapter(mCategoryAdapter);
		mCatRecycleView.setAdapter(mMyCatsRecyclerViewAdapter);
		//mDrawerAdapter.notifyDeataSetChanged();
	}
	
	/*public void createCategoryTree(CategoryData parent_data, int tree_level)
	{
		for(int i=0; i<mCategoryData.size(); i++)
		{
			TreeElement parent_el = null;
			//if has child
			if(parent_data.id.equals(mCategoryData.get(i).parent_id))
			{
				boolean parentExist = false; 
				for(int t=0; t<mParentElements.size(); t++) //Check parent on exist
				{
					if(mParentElements.get(t).getId().equals(parent_data.id.toString()))
					{
						parent_el = mParentElements.get(t);
						parentExist = true;
						break;
					}
				}
				if(!parentExist)
				{
					mParentElements.add(new TreeElement(parent_data.id.toString(),
							parent_data.icon_img,
							parent_data.name,
						false, 
						parent_data.has_child, null, 0, false));
					parent_el = mParentElements.get(mParentElements.size()-1);
					
				}
				//if main parent
				if(parent_data.parent_id.equals(0))
				{
					boolean exist = false;
					for(int j=0; j<mTreeElements.size(); j++)
					{
						if(parent_data.id.toString().equals(mTreeElements.get(j).getId()))
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
				TreeElement child_el = new TreeElement(mCategoryData.get(i).id.toString(),
						mCategoryData.get(i).icon_img,
						mCategoryData.get(i).name,
						true, 
						mCategoryData.get(i).has_child, parent_el, tree_level+1, false);
				if(!mCategoryData.get(i).has_child.equals(0))
				{
					mParentElements.add(child_el);
					createCategoryTree(mCategoryData.get(i), tree_level+1);
				}
			}
		}
	}*/
	
	public void createNeedCatTree(Integer pos, Integer id){
		
		mCatIDsList.add(id);
		if(mCategoryData.get(pos).has_child.equals(1)){
			for(int i=0; i<mCategoryData.size(); i++){
					if(mCategoryData.get(i).parent_id.equals(id))
					{
						createNeedCatTree(i, mCategoryData.get(i).id);
					}
			}
		}
	}
	
	public void getMainParentId(Integer pos){
		if(mCategoryData.size() <pos)
			return;
		if(mCategoryData.get(pos).parent_id.equals(0)){
			mSelectedMainParentId = mCategoryData.get(pos).id;
			return;
		}
		
		for (int i = 0; i < mCategoryData.size(); i++) {
			if(mCategoryData.get(pos).parent_id.equals(mCategoryData.get(i).id)){
				getMainParentId(i);
				break;
			}
		}
	}
	
	public UserFunctions getUserFunctions(){
		return mUserFunctions;
	}
	
	private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                	Bundle bnd = null;
                	if(future != null)
                		bnd = future.getResult();
                    Log.i("hm","Account was created");
                    
                   // Log.d("hm", "AddNewAccount Bundle is " + bnd);
                    if(bnd.getInt(AccountGeneral.KEY_HM_ACC_TYPE) == 0){
                    	checkProfile();
                    	openMenu();
                    }else
                    	mConnectionProgressDialog.show();
                } catch (Exception e) {
                	if(e != null){
	                    e.printStackTrace();
	                    if(e.getMessage() != null)
	                    	Log.i("hm",e.getMessage());
                	}
                }
            }
        }, null);
    }
	
	public void checkProfile()
	{
		final Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		if(acc.length == 0){
			createMenuItems();
			return;
		}
		/*if(mGoogleApiClient.isConnected() && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
			for (int i = 0; i < acc.length; i++) {
				mAccPos = i;
				String type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
				if(Plus.AccountApi.getAccountName(mGoogleApiClient).equals(acc[mAccPos].name)
					&& type != null && type.equals("1")){
					mAccount = acc[mAccPos];
					break;
				}
				mAccPos =-1;
			}
			if(mAccPos == -1){
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
    	        mGoogleApiClient.disconnect();
				logout();
			}
			else
				createMenuItems();
		}
		else*/
		{
			acc_async_num = acc.length;
			for (int i = 0; i < acc.length; i++) {
				String refresh_token = mAccountManager.getUserData(acc[i], AccountGeneral.KEY_REFRESH_TOKEN);
				
				if(refresh_token != null){
					mAccPos = i;
					mAccount = acc[mAccPos];
					break;
				}
			}
			
			createMenuItems();
		}
		
	}
	
	
	public void logout(){
		mSavedData.logOut();
		mMainPage.logOut();
		mDBUser.logout();
		mSelectedUsers.clear();
		showFilterStateInfo(false);
		
		mAccount = null;
		mAccPos = -1;
		checkProfile();
		mMainPage.updateAdapter();
		if(mLowerControlBar != null)
			mLowerControlBar.updateBar();
		mMainPage.refreshTrigger(true);
	}
	
	private void createMenuItems(){
		
		if(mItems == null)
			mItems = new ArrayList<Item>();
		mItems.clear();
		if(mAccount != null)
		{			
			/*if(mGoogleApiClient.isConnected() && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				HashMap<String, String> user = new HashMap<String, String>();
				user.put(AccountGeneral.KEY_DISPLAY_NAME, currentPerson.getDisplayName());
				user.put(AccountGeneral.KEY_IMAGE_URL, currentPerson.getImage().getUrl());
				mDrawerListProfile = new DrawerListProfile(MainControlBarActivity.this, user);
			}else{*/
				HashMap<String, String> user = new HashMap<String, String>();
				user.put(AccountGeneral.KEY_DISPLAY_NAME, mAccountManager.getUserData(mAccount, AccountGeneral.KEY_DISPLAY_NAME));
				user.put(AccountGeneral.KEY_IMAGE_URL, mAccountManager.getUserData(mAccount, AccountGeneral.KEY_IMAGE_URL));
				mDrawerListProfile = new DrawerListProfile(MainControlBarActivity.this, user);
			//}
			mItems.add(mDrawerListProfile);
			mItems.add(new DrawerListHeaders(inflater, getString(R.string.title_private)));
	        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_ad_my), R.drawable.icon_ad_my, getString(R.string.my_ad), DrawerListAdapter.AD_MY));
	        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_fav_user), R.drawable.icon_fav_user, getString(R.string.favorite_users), DrawerListAdapter.AD_FAVORITE_USERS));
	        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_fav_ad), R.drawable.icon_fav_ad, getString(R.string.favorite_ads), DrawerListAdapter.AD_FAVORITE));
		}
		else
		{
			mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_auth), 0, getString(R.string.authorization), DrawerListAdapter.PROFILE));
		}
		mItems.add(new DrawerListHeaders(inflater, getString(R.string.title_common)));
		mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_ad_add), R.drawable.icon_ad_add, getString(R.string.add_ad), DrawerListAdapter.AD_ADD));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_ad_all), R.drawable.icon_ad_all, getString(R.string.all_ad), DrawerListAdapter.AD_ALL));
		mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_cat_filter), R.drawable.icon_cat_filter, getString(R.string.cats), DrawerListAdapter.CATS_DRAWER));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_cat_filter), R.drawable.icon_cat_filter, getString(R.string.ad_cat_filter), DrawerListAdapter.AD_CAT_FILTER));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_ext_search), R.drawable.icon_ext_search, getString(R.string.ext_search), DrawerListAdapter.AD_EXT_SEARCH));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_settings), R.drawable.icon_settings, getString(R.string.title_activity_settings), DrawerListAdapter.SETTINGS));
        //mItems.add(new DrawerListItems(getApplicationContext(), inflater, getResources().getDrawable(R.drawable.icon_settings), R.drawable.icon_settings, getString(R.string.add_commercial), DrawerListAdapter.COMMERCIAL));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_share_to_friends), R.drawable.icon_share_to_friends, getString(R.string.tell_friends), DrawerListAdapter.TELL_FRIENDS));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_about), R.drawable.icon_about, getString(R.string.about_pr), DrawerListAdapter.ABOUT));
        mItems.add(new DrawerListItems(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_exit), R.drawable.icon_exit, getString(R.string.btn_exit), DrawerListAdapter.EXIT));
        mItems.add(new DrawerListLowerBar(MainControlBarActivity.this, inflater, getResources().getDrawable(R.drawable.icon_settings), R.drawable.icon_settings, getString(R.string.lower_bar), DrawerListAdapter.LOWER_BAR));
        
        mLowerBarData.clear();
        for (int i = 0; i < mItems.size(); i++) 
		{
			if((mItems.get(i).getViewType()
					!= DrawerListAdapter.RowType.HEADER_ITEM.ordinal()&&
							mItems.get(i).getViewType()
					!= DrawerListAdapter.RowType.PROFILE_ITEM.ordinal() &&
							mItems.get(i).getViewType()
					!= DrawerListAdapter.RowType.LOWERBAR.ordinal())
			&& (mItems.get(i).getMenuType() != DrawerListAdapter.PROFILE))
			{
				LowerBarData data = new LowerBarData();
        		data.menu_type = mItems.get(i).getMenuType();
        		data.icon_res = mItems.get(i).getIconId();
				mLowerBarData.add(data);
			}
		}
        
        MainDatabaseHandler db = new MainDatabaseHandler(getApplicationContext());
        if(mLowerBarData.size() != db.getLowerBarData().size()){
        	db.clearTableLowerBar();
 	        db.addLowerBarData(mLowerBarData);
        }
        else{
        	mLowerBarData = db.getLowerBarData();
        }
        if(mMenuDrawerList != null){
			mMenuDrawerAdapter = new DrawerListAdapter(MainControlBarActivity.this, mItems);
			mMenuDrawerList.setAdapter(mMenuDrawerAdapter);
			mMenuDrawerAdapter.notifyDataSetChanged();
        }

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
		MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0) {
				// TODO Auto-generated method stub
				//setType(DrawerListAdapter.AD_ALL);
				mMainSearchText = null;
				return true;
			}
		});
		mSearchView = (SearchView)MenuItemCompat.getActionView(mSearchItem);
		mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
		MenuItem share_item = menu.findItem(R.id.share_ad);
		mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(share_item);
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String title = "HearMe";
		String desc = "ыв - http://play.google.com";
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		shareIntent.putExtra(Intent.EXTRA_TEXT, desc);
		mShareActionProvider.setShareIntent(shareIntent);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mCatRecycleView);
		//menu.findItem(R.id.main_search).setVisible(!drawerOpen);
		if(!mAppStarted)
			return false;
		else
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
			//selectItem(mType, -1);
			mMainPage.refreshTrigger(true);
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
	
	public String getSearchText(){
		return mMainSearchText;
	}
	
	private void closeSearch(){
		if(mMainSearchText != null){
			if(mMainSearchText.trim() != ""){
				mMainSearchText = null;
				mSearchManager.stopSearch();
				MenuItemCompat.collapseActionView(mSearchItem);
			}
		}
		else if(mSearchItem != null){
			if(MenuItemCompat.isActionViewExpanded(mSearchItem))
				MenuItemCompat.collapseActionView(mSearchItem);
		}
		//setType(DrawerListAdapter.AD_ALL);
	}
	
	
	public void createAdDetailsActivity(int pos){
		Intent intent = new Intent(MainControlBarActivity.this, AdDetailsActivity.class);
		intent.putExtra("acc_pos", mAccPos);
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
		boolean close_catsDrawer = true;
		if(type == 0)
			return;
		if(!getSupportActionBar().isShowing()){
			getSupportActionBar().show();
		}
		
		if(mFavoriteUsersFilter != null){
			getSupportFragmentManager().beginTransaction().remove(mFavoriteUsersFilter).commit();
			mFavoriteUsersFilter = null;
		}

		closeSearch();
		mFragmentFilterState.closeFilter();

		if(mDownloadFragment != null){
			getSupportFragmentManager().beginTransaction().remove(mDownloadFragment).commit();
			mDownloadFragment = null;
		}
		int index = -1;
		for(int i=0; i<mItems.size(); i++){
			int tMenuType = mItems.get(i).getMenuType();
			if(tMenuType == type){
				index = i;
				break;
			}
		}
		
		mCurrentType = mItems.get(index).getMenuType();
		
		switch (mCurrentType) {
		case DrawerListAdapter.PROFILE:
			if(mAccount != null)
			{
				Intent intent = new Intent(MainControlBarActivity.this, ProfileActivity.class);
				intent.putExtra(AccountGeneral.KEY_ACC_POS, mAccPos);
				startActivityForResult(intent, PROFILE_ACTIVITY);
			}
			else{
				addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
			}
			break;
		case DrawerListAdapter.AD_ADD:
			if(mSavedData.getPolicyUpdate() && mAccount == null){
				new PolicyDialog().show(getSupportFragmentManager(), "");
			}
			else{
				if(getResources().getBoolean(R.bool.is_tablet)){
					AdAddDialog ad_add_dialog = new AdAddDialog();
					ad_add_dialog.show(getSupportFragmentManager(), "ad_add_dialog");
				}else{
					Intent ad_add_intent = new Intent(MainControlBarActivity.this, AdAddActivity.class);
					ad_add_intent.putExtra(AccountGeneral.KEY_ACC_POS, mAccPos);
					startActivityForResult(ad_add_intent, AD_ADD);
				}
			}
			break;
		case DrawerListAdapter.AD_MY:
			mMainPage.mPage = 0;
			mCatIDsList.clear();
			mSelectedAdsId.clear();
			mSelectedUsers.clear();
			
			showFilterStateInfo(true);
			if(mAccount != null)
				new AccessTokenActionsTask().execute("ad_my");
			break;
		case DrawerListAdapter.AD_FAVORITE_USERS:
			mSelectedUsers = mDBUser.getFavUsers();
			if(mSelectedUsers.size() == 0)
			{
				new CustomToast(MainControlBarActivity.this).show(getString(R.string.no_fav_users));
				mType = mPrevType;
				return;
			}
			mCatIDsList.clear();
			mSelectedAdsId.clear();
			showFilterStateInfo(true);
			
			//getAdList(secondtype);
			break;
		case DrawerListAdapter.AD_FAVORITE:
			mSelectedAdsId = mDBUser.getFavAds();
			if(mSelectedAdsId.size() == 0){
				new CustomToast(MainControlBarActivity.this).show(getString(R.string.no_fav_ads));
				mType = mPrevType;
				return;
			}
			mSelectedUsers.clear();
			mCatIDsList.clear();
			
			showFilterStateInfo(true);
			mMainPage.refreshTrigger(true);
			break;
		case DrawerListAdapter.AD_ALL:
			mSelectedAdsId.clear();
			mSelectedUsers.clear();
			mSavedData.clearSelectCats();
			if(mCatIDsList.size() != 0){
				showFilterStateInfo(true);
				mSavedData.saveSelectedCats(mCatFilterText, mCatIDsList);
			}
			else
				showFilterStateInfo(false);
			
			//if(getMainPage() != null)
			//	getMainPage().getSliderPagerAdapterNoRotate().updateData(getSelectedMainParentId());
			mMainPage.refreshTrigger(true);
			break;
		case DrawerListAdapter.CATS_DRAWER:
			close_catsDrawer = false;
			mDrawerLayout.openDrawer(mCatsDrawerView);
			break;
		case DrawerListAdapter.AD_CAT_FILTER:
			Intent cat_intent = new Intent(MainControlBarActivity.this, CategoryActivity.class);
			startActivityForResult(cat_intent, CAT_ACTIVITY);
			break;
		
		case DrawerListAdapter.LOWER_BAR:
			if(mSavedData.getLowerBarPos())
			{
				closeLowerBar();
			}
			else
			{
				showLowerBar();
				mSavedData.saveLowerBarPos(true);	
			}
			break;
		case DrawerListAdapter.AD_EXT_SEARCH:
			ExtendedSearchDialog dialog = new ExtendedSearchDialog();
			dialog.show(getSupportFragmentManager(), "ext_search");
			break;
		case DrawerListAdapter.SETTINGS:
			Intent intent = new Intent(MainControlBarActivity.this, SettingsActivity.class);
			startActivityForResult(intent, SETTINGS);
			break;
		case DrawerListAdapter.COMMERCIAL:
			new CustomToast(MainControlBarActivity.this).show(getString(R.string.soon));
			break;
		case DrawerListAdapter.TELL_FRIENDS:
			final String appPackageName = getPackageName();
			String app_url="https://play.google.com/store/apps/details?id=" + appPackageName;
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			String title = "HearMe";
			String desc = getString(R.string.tell_friends_text)+" "+app_url;
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HearMe");
			shareIntent.putExtra(Intent.EXTRA_TEXT, desc);
			mShareActionProvider.setShareIntent(shareIntent);
			startActivity(Intent.createChooser(shareIntent, "Выберите "));
			break;
		case DrawerListAdapter.REPORT:
			Intent report_intent = new Intent(MainControlBarActivity.this, UserReportActivity.class);
			startActivity(report_intent);
			break;
		case DrawerListAdapter.ABOUT:
			HMAboutDialog about = new HMAboutDialog();
			about.show(getSupportFragmentManager(), "about_dialog");
			break;
		case DrawerListAdapter.EXIT:
			finish();
			break;
		default:
			break;
		}		
		
		mPrevType = mType;
		mType = type;
		//mCatDrawerList.setItemChecked(index, true);
		setTitle(mItems.get(index).getMenuTitle());
		if(close_catsDrawer)
			mDrawerLayout.closeDrawer(mCatsDrawerView);
		mDrawerLayout.closeDrawer(mMenuDrawerList);
		
	}
	
	public int getCurrentMenuType(){
		return mCurrentType;
	}
	
	public void getAdList(int secondtype, String access_token){
		
		if(getCurrentMenuType() == DrawerListAdapter.AD_FAVORITE_USERS && mSelectedUsers.size()==0)
			return;
		
		mSecondType = secondtype;
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
		if(access_token != null){
			params.add(new BasicNameValuePair("access_token", access_token));
		}
		params.add(new BasicNameValuePair("area_id", String.valueOf(mSelectedAreaId)));
		params.add(new BasicNameValuePair("start_page", start.toString()));
		params.add(new BasicNameValuePair("item_count", mAdCountInOnePage.toString()));
		
		for (Integer ad_id : mSelectedAdsId) {
			params.add(new BasicNameValuePair("ad_id_array[]", ad_id.toString()));
		}
		
		for (Integer user_id : mSelectedUsers) {
			params.add(new BasicNameValuePair("users_id[]", user_id.toString()));
		}
			
		if(mMainSearchText != null)
			if(mMainSearchText.trim().length() != 0)
			{
				params.add(new BasicNameValuePair("search_text", mMainSearchText));
			}
		for(Integer filter : mExtSearchFilterList){
			params.add(new BasicNameValuePair("filter_list[]", filter.toString()));
		}
		for(int i=0; i<mCatIDsList.size(); i++){
			params.add(new BasicNameValuePair("cat_id_array[]", mCatIDsList.get(i).toString()));
		}
		for(Integer cat :mOutCatIDsList){
			params.add(new BasicNameValuePair("out_cats_id[]", cat.toString()));
		}
		if(access_token != null && mGoogleApiClient.isConnected())
			urlWithParams.url = Urls.G_USER;
		else
			urlWithParams.url = Urls.ADS;
		urlWithParams.nameValuePairs = params;
		//if(secondtype == DownloadData.GET_MORE_AD || secondtype == DownloadData.LOAD_AD_LIST){
			DownloadData dd = new DownloadData(secondtype, this);
			dd.setParams(urlWithParams);
			dd.setListener(this);
			mDownloadDataTasks.add(dd);
			mDownloadDataTasks.get(mDownloadDataTasks.size()-1).execute();
		//}
		/*else{
			createDownloadFragment(urlWithParams,
					getString(R.string.load_data), 
					DownloadFragment.LOAD_AD_LIST, 
					MainControlBarActivity.this);
		}*/
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
	
	public ArrayList<Integer> getSelectedUsers(){
		return mSelectedUsers;
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
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
	        mConnectionResult = null;
	        //mGoogleApiClient.connect();
	    }
		
		Fragment ad_add_fragment = (Fragment) getSupportFragmentManager().findFragmentByTag("ad_add_dialog");
		if(ad_add_fragment != null){
			ad_add_fragment.onActivityResult(requestCode, resultCode, data);
		}
		if(requestCode == AD_ADD){
			if(resultCode == RESULT_OK){
				mSelectedAreaId = data.getIntExtra("area_id", -1);
				Integer cat = data.getIntExtra("cat_id", -1);
				mMainPage.setAddedAdID(data.getIntExtra("ad_id", -1));
				
				
				for(int i=0; i<mCategoryData.size(); i++){
		    		if(cat.equals(mCategoryData.get(i).id)){
		    			createNeedCatTree(i, cat);
		    			break;
	 	    		}
		    	}
				if(mSelectedAreaId != -1){
					for (int i=0; i<mAreaData.size(); i++) {
						if(mAreaData.get(i).id == mSelectedAreaId){
							if(mAreaSpinner.getSelectedItemPosition() != i){
								mAreaSpinner.setSelection(i);
							}
							else
								mMainPage.refreshTrigger(true);
							break;
						}
					}
					
				}
				//getSupportActionBar().setListNavigationCallbacks(mAreaSpinnerAdapter, mCallbackArea);
				//selectItem(DrawerListAdapter.AD_ALL, -1);
			}
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
			else if(requestCode == PROFILE_ACTIVITY){
				if(resultCode == EXPIRED_REFRESH_TOKEN){
					logout();
					selectItem(DrawerListAdapter.PROFILE, -1);
				}
				else if(resultCode == ProfileActivity.GOOGLE_LOG_OUT){
					logout();
				}
				else if(resultCode == ProfileActivity.GOOGLE_REMOVE_ACCOUNT)
					logout();
				if(resultCode == ProfileActivity.LOG_OUT){
					new AccessTokenActionsTask().execute("logout");
					mConnectionProgressDialog.setMessage(getString(R.string.notif_logout));
					mConnectionProgressDialog.show();
				}
				else if(resultCode == ProfileActivity.REMOVE_ACCOUNT)
					new AccessTokenActionsTask().execute("remove_account");
			}
			else if(requestCode == CAT_ACTIVITY){
				if(resultCode == RESULT_OK){
					mOutCatIDsList = mSavedData.getOutCats();
				}
			}
			else if(requestCode == SETTINGS){
				mAdCountInOnePage = Integer.valueOf(mSettingsPref.getString(SettingsActivity.LOAD_ADS_NUM, "10"));
			}
			else if(requestCode == LOWER_BAR_SETTINGS){
				showLowerBar();
			}
		}
		else{
			if(requestCode == GET_SELECT_CAT){
				if(resultCode == RESULT_OK){
					mCatIDsList.clear();
					int cat_pos = data.getIntExtra("cat_pos", -1);
					int cat_id = data.getIntExtra("cat_id", -1);
					createNeedCatTree(cat_pos, cat_id);
					mSavedData.saveSelectedCats(mCatFilterText, mCatIDsList);
					selectItem(DrawerListAdapter.AD_ALL, -1);
				}
			}
			else if(requestCode == CAT_ACTIVITY){
				if(resultCode == RESULT_OK){
					mOutCatIDsList = mSavedData.getOutCats();
				}
			}
			else if(requestCode == PROFILE_ACTIVITY){
				if(resultCode == RESULT_OK){
					if(data.getBooleanExtra("upd_avatar", false)){
						String image_url = data.getStringExtra(AccountGeneral.KEY_IMAGE_URL);
						mAccountManager.setUserData(mAccount, AccountGeneral.KEY_IMAGE_URL, image_url);
						mDrawerListProfile.updateAvatar(image_url);
						openMenu();
					}
					if(data.getBooleanExtra("upd_name", false)){
						mDrawerListProfile.updateName(mDBUser.getUserDetails());
						openMenu();
					}
				}
				else if(resultCode == ProfileActivity.GOOGLE_LOG_OUT){
					logout();
				}
				else if(resultCode == ProfileActivity.LOG_OUT)
					new AccessTokenActionsTask().execute("logout");
			}
		}
	}
	
	@Override
	public void onExtendedSearch(ArrayList<Integer> filter_list, String search_text) {
		// TODO Auto-generated method stub
		
		try {
			mMainSearchText = URLEncoder.encode(search_text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mExtSearchFilterList = filter_list;
		//getAdList(-1, null);
		mMainPage.refreshTrigger(true);
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
		mDownloadFragment = null;
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
					//setMainPageData(mAdListData);
					return;
				}
				else if(checkConn.get(0) == "error"){
					new CustomToast(this).showErrorToast(getString(R.string.error_load_data));
					//setMainPageData(mAdListData);
					return;
				}
			mLoadedType = mType;
			mAdListData = (ArrayList<AdListData>)data_array;
			
			FragmentManager fragmentManager1 = getSupportFragmentManager();
			Fragment fragmment = fragmentManager1.findFragmentByTag(MAIN_PAGE);
			
			if(fragmment == null){
				mMainPage = new MainPage();
				
				Bundle bundle = new Bundle();
				mMainPage.setArguments(bundle);
				fragmentManager1.beginTransaction().replace(R.id.content_frame, mMainPage, MAIN_PAGE).commit();
			}
			else{
				if(mMainPage == null)
					mMainPage = (MainPage) fragmment;
				mMainPage.setParent(this);
			}
			mMainPage.updateAdapter();
		
	}

	@Override
	public void getDownloadDCompleteState(int loadType, ArrayList<? extends Object> data_array) {
		// TODO Auto-generated method stub
		if(mRefreshMenuItem != null){
			MenuItemCompat.getActionView(mRefreshMenuItem).clearAnimation();
			MenuItemCompat.setActionView(mRefreshMenuItem, null);
			mRefreshMenuItem = null;
		}
		if(mSavedData.isFirstAppRun())
			mStartAppFragment.setLoadInfo(getString(R.string.setting_app));
		ArrayList<String> checkConn = (ArrayList<String>)data_array;
		if(checkConn.size() != 0)
			if(checkConn.get(0) == "no_connection"){
				if(mSavedData.isFirstAppRun()){
					MainDatabaseHandler db = new MainDatabaseHandler(MainControlBarActivity.this);
	        		db.resetTables();
					mAreaData.clear();
					mCategoryData.clear();
					mSliderData.clear();
				}
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
				if(mSavedData.isFirstAppRun()){
					MainDatabaseHandler db = new MainDatabaseHandler(MainControlBarActivity.this);
	        		db.resetTables();
					mAreaData.clear();
					mCategoryData.clear();
					mSliderData.clear();
				}
				if(loadType == DownloadData.GET_MORE_AD){
					mMainPage.loadCircleState(MainPage.LOAD_CIRCLE_ERROR, null);
					return;
				}
				if(loadType == DownloadData.LOAD_AD_LIST){
					mMainPage.stopRefreshing();
					return;
				}
				if(!mAppStarted)
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
			if(update_state.get(0).info){
				new HMDialog(getString(R.string.info),update_state.get(0).text).show(getSupportFragmentManager(), "info_dialog");
			}
			if(update_state.get(0).policy_update){
				mSavedData.savePolicyState(true);
			}
			if(update_state.get(0).app){
				mStartAppFragment.updateApp();
				return;
			}
			if(update_state.get(0).access){
				mStartAppFragment.showInfo(update_state.get(0).text);
				return;
			}
			if(update_state.get(0).area || area_count == 0){
				db.clearTableArea();
				URLWithParams urlWithParams = new URLWithParams();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("tag", AdTags.get_area_data));
				urlWithParams.url = Urls.COMMON;
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
				urlWithParams2.url = Urls.COMMON;
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
			ArrayList<SliderData> slider_data = (ArrayList<SliderData>)data_array;
			mSliderData = slider_data;
			break;
		case DownloadData.GET_MORE_AD:
			ArrayList<AdListData> data_ad = (ArrayList<AdListData>)data_array;
			if(data_ad == null){
				mMainPage.loadCircleState(MainPage.LOAD_CIRCLE_SUCCESS, data_ad);
				return;
			}
			if(mMainPage != null)
				mMainPage.setMoreData(data_ad);
			cancelTask(loadType);
			return;
		case DownloadData.LOAD_AD_LIST:
			if(data_array == null){
				mMainPage.stopRefreshing();
				return;
			}
			mAdListData = (ArrayList<AdListData>)data_array;
			mMainPage.updateAdapter();
			showFilterStateInfo(true);
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
							//mAreaNaviFirstHit = false;
							mAreaSpinner.setSelection(i);
							break;
						}
					}
					
				}
				
				Collections.sort(mCategoryData, new CatComparator());
				createCatalogItems();
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

				mStartAppFragment.startClose();
				getSupportActionBar().show();
				
				if(mSavedData.isFirstAppRun()){
					mSavedData.setFirstStart(false);
					mSavedData.savePolicyState(true);
					new HMDialog(getString(R.string.welcome),getString(R.string.welcome_text)).show(getSupportFragmentManager(), "info_dialog");
					new Handler().postDelayed(openMenuDrawerRunnable(), 200);
				}else{
					new Handler().postDelayed(openCatsDrawerRunnable(), 200);
				}
				
				mAppStarted = true;
				
				//supportInvalidateOptionsMenu();
				//mAreaNaviFirstHit = false;
				
				//selectItem(DrawerListAdapter.AD_ALL, -1);
				mMainPage.setCatData(mCategoryData);
				
				
				if(mCatIDsList != null && mCatIDsList.size() != 0){
					for (int i = 0; i < mCategoryData.size(); i++) {
						if(mCategoryData.get(i).id.equals(mCatIDsList.get(0))){
							getMainParentId(i);
							break;
						}
					}
				}
				
				mMainPage.updateSlider();
				//mMainPage.refreshTrigger();
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
		new Handler().postDelayed(openMenuDrawerRunnable(), 200);
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
		//if(ev.getAction() != MotionEvent.ACTION_MOVE 
		//	|| ev.getAction() != MotionEvent.ACTION_DOWN)
		//	Log.i("test", String.valueOf(ev.getAction()));
		
		return true;
	}
	
	public DatabaseHandler getDBUser(){
		if(mDBUser == null)
			mDBUser = new DatabaseHandler(MainControlBarActivity.this);
		return mDBUser;
	}
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		//mGoogleApiClient.connect();
	}
	
}
