package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.donearh.hearme.DownloadData.DownloadDCompleteListener;
import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.ObservableListView.OnDetectScrollListener;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.datatypes.GeneralUserData;
import com.donearh.hearme.datatypes.SliderData;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.tags.CommonTags;
import com.donearh.imageloader.ImageFromResource;
import com.donearh.imageslider.AdapterImageSlider;
import com.donearh.imageslider.MyPagerAdapterNoRotate;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter.ExpandCollapseListener;

@SuppressLint({ "JavascriptInterface", "NewApi" })
public class MainPage extends Fragment implements AdapterView.OnItemClickListener, 
													OnRefreshListener,
													OnScrollListener,
													DownloadDCompleteListener,
													OnDetectScrollListener,
													InsertCompleteListener,
													GetJSONListener
{	

    
	public static String LOAD_CIRCLE_SUCCESS = "success";
	public static String LOAD_CIRCLE_ERROR = "error";
	//private AdData mAdData;
	private MyExpandableListItemAdapter mExpandableListItemAdapter;
	
	private AccountManager mAccountManager;
	private SavedData mSavedData;
	private View mRootView;
	
	private HashMap<String, String> mTempParams = new HashMap<String, String>();

	private ImageFromResource mImageFromRes;
	private MainControlBarActivity mMainControlBarActivity;
	public ArrayList<CategoryData> mCategoryData;
	
	//IMAGE SLIDER
	//public MyPagerAdapter mSliderPagerAdapter;
	public MyPagerAdapterNoRotate mSliderPagerAdapterNoRotate;
	public MyPagerAdapterNoRotate getSliderPagerAdapterNoRotate() {
		return mSliderPagerAdapterNoRotate;
	}

	private int mActionBarHeight;
	private boolean mPageMoved = false;
	private static final long ANIM_VIEWPAGER_DELAY = 2000;
	public ViewPager mImageSliderViewPager;
	private AdapterImageSlider mSectionsAdapter;
	public static ArrayList<SliderData> mSliderData;
	private Handler mAutoImageSlider;
	private int currentSlide;
	
	private RelativeLayout mBottomCircle;
	private LinearLayout mBottomCircleError;
	private LinearLayout mEmptyListView;
	
	//private ArrayList<AdListData> mAdListData;
	private ArrayList<WebView> mWebViewArray;
	private ArrayList<Integer> mWebViewHeight;
	//private AdAdapter mAdAdapter;
	private ObservableListView mListView;
	
	private int mInc = 0;
	private ProgressBar mProgressBar = null;
	
	public int mPage = 0;
	private Integer mAddedAdID = -1;
	boolean mDo = true;
	boolean mUpdate = true;
	
	boolean mLoadData = false;
	private WebView mWebView;
	private static int webviewContentWidth = 0;
	
	SwipeRefreshLayout swipeLayout;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	
	private Integer mLoadType;
	private View image_slider;
	
	public void setCurrentSlide(int num){
		currentSlide = num;
	}
	
	public MainPage()
	{	
		mWebViewArray = new ArrayList<WebView>();
		mWebViewHeight = new ArrayList<Integer>();
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mMainControlBarActivity = (MainControlBarActivity)activity;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mAccountManager = AccountManager.get(getActivity());
        mSavedData = SavedData.getInstance(getActivity());
        mImageFromRes = new ImageFromResource(getActivity());
        mCategoryData = mMainControlBarActivity.mCategoryData;
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		super.onCreateView(inflater, container, saveInstanceState);
		//IMAGE SLIDER	
		Log.i("hm", "create main page view");
		if(mSavedData == null)
			mSavedData = SavedData.getInstance(getActivity());
		
		image_slider = LayoutInflater.from(getActivity()).inflate(R.layout.main_image_slider, null);
		mImageSliderViewPager = (ViewPager) image_slider.findViewById(R.id.image_slider);

		if(mSliderPagerAdapterNoRotate == null)
			mSliderPagerAdapterNoRotate = new MyPagerAdapterNoRotate(getActivity(),
											 	getChildFragmentManager());

		mImageSliderViewPager.setAdapter(mSliderPagerAdapterNoRotate);
		mImageSliderViewPager.setOnPageChangeListener(mSliderPagerAdapterNoRotate);
		currentSlide = MainControlBarActivity.FIRST_PAGE;
		mImageSliderViewPager.setCurrentItem(currentSlide);
		mImageSliderViewPager.setOffscreenPageLimit(3);
		
		int width = container.getMeasuredWidth();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.pagermargin), getResources().getDisplayMetrics());
		mImageSliderViewPager.setPageMargin(-(int) (px));
		//mImageSliderViewPager.setPageMargin(Integer.parseInt(getString(R.string.pagermargin)));
		mAutoImageSlider = new Handler();
		image_slider.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int t=0;
				t=0;
			}
		});
		mImageSliderViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				//mMainControlBarActivity.setNotSlidableZone(image_slider.getMeasuredHeight());
				
				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					mImageSliderViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				   else
					   mImageSliderViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		/*mSectionsAdapter = new AdapterImageSlider(getActivity(), getActivity().getSupportFragmentManager());
		mSectionsAdapter.setData(mSliderData);
		View image_slider = LayoutInflater.from(getActivity()).inflate(R.layout.main_image_slider, null);	
		final float scale = getResources().getDisplayMetrics().density;
		mImageSliderViewPager = (ViewPager) image_slider.findViewById(R.id.image_slider);
		mImageSliderViewPager.setAdapter(mSectionsAdapter);
		mImageSliderViewPager.setPageMargin((int) (-50 * scale + 0.5f));
		mImageSliderViewPager.setOffscreenPageLimit(3);
		mImageSliderViewPager.setHorizontalFadingEdgeEnabled(true);
		mImageSliderViewPager.setFadingEdgeLength(30);
		mAutoImageSlider = new Handler();
		currentSlide = mImageSliderViewPager.getCurrentItem();*/
		
		TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getActivity().getResources().getDisplayMetrics());
        }
		
		View circleView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_load_circle, null);
		mBottomCircle = (RelativeLayout)circleView.findViewById(R.id.circle_layout);
		mBottomCircleError = (LinearLayout)circleView.findViewById(R.id.error_layout);	
		mBottomCircleError.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMainControlBarActivity.getAdList(DownloadData.GET_MORE_AD, null);
			}
		});
		
		mEmptyListView = (LinearLayout)circleView.findViewById(R.id.empty_list);	
		mEmptyListView.setVisibility(View.GONE);
		mRootView = inflater.inflate(R.layout.main_page_fragment, container, false);
		//mAdAdapter = new AdAdapter(getActivity());
		
		swipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_layout);
		//swipeLayout.setProgressViewOffset(false, 0, 200);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeColors(R.color.light_blue, 
	            R.color.night_green,
	            R.color.light_green, 
	            R.color.night_blue);
		mListView = (ObservableListView) mRootView.findViewById(R.id.listview);
		
		swipeLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
			@Override
			public void onSwipeTop() {
		        Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
		    }
			@Override
		    public void onSwipeRight() {
		        Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
		    }
			@Override
		    public void onSwipeLeft() {
		        Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
		    }
			@Override
		    public void onSwipeBottom() {
		        Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
		    }
		});
		
		mExpandableListItemAdapter = new MyExpandableListItemAdapter(getActivity());
		mExpandableListItemAdapter.setLimit(1);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mExpandableListItemAdapter);
		//swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setOnDetectScrollListener(this);
		
		mListView.addHeaderView(image_slider);
		mListView.addFooterView(circleView);
		loadCircleState(LOAD_CIRCLE_SUCCESS, mMainControlBarActivity.mAdListData);
		mListView.setAdapter(swingBottomInAnimationAdapter);

		LayoutParams lparams = new LayoutParams(
				   LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		
		/*View v = (View)circleView.findViewById(R.id.gr_back_circle);
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		v.startAnimation(a);*/
		
	      mWebView = new WebView(mMainControlBarActivity);
	      mWebView.setLayoutParams(lparams);
	      mWebView.getSettings().setJavaScriptEnabled(true);
	      mWebView.setSaveEnabled(true);
	      mWebView.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
	      mWebView.setWebViewClient(new WebViewClient() {
	          @Override
	          public void onPageFinished(WebView view, String url) {
	          	mWebView.loadUrl("javascript:window.HTMLOUT.getContentHeight(document.getElementsByTagName('html')[0].scrollHeight);");
	          }
	      });
	      if(mMainControlBarActivity.mAdListData != null)
	      for(int i=0; i<mMainControlBarActivity.mAdListData.size(); i++)
	      {
	    	  
	    	  String text = "<html><body>"
	  	            + "<p align=\"justify\">"
	  	             + mMainControlBarActivity.mAdListData.get(i).Desc
	  	            + "</p> "
	  	             + "</body></html>";
	    	  mWebViewArray.add(mWebView);
	    	  mWebViewArray.get(i).loadDataWithBaseURL("", text, "text/html", "utf-8", "");
	      }
			
	      //mMainControlBarActivity.showFilterStateInfo(true);
	      
	    updateAdapter();
		return mRootView;
		
	}
	
	
	
	 @Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mRootView = null;
		mSliderPagerAdapterNoRotate = null;
		mAutoImageSlider = null;
	}

	 

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
	}
	
	public void setMargin(){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.pagermargin), getResources().getDisplayMetrics());
		mImageSliderViewPager.setPageMargin(-(int) (px));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.pagermargin), getResources().getDisplayMetrics());
		mImageSliderViewPager.setPageMargin(-(int) (px));
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        //Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        //Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
	    }
	}

	private Runnable animateViewPager = new Runnable() {
	        public void run() {
	        	
	            if (!mSliderPagerAdapterNoRotate.isSliderMove() && mImageSliderViewPager.isShown()) {          	
	            	if (currentSlide >= mSliderPagerAdapterNoRotate.getCount()-1) {
	            		currentSlide = 1;
	                }
	            	mImageSliderViewPager.setCurrentItem(currentSlide++, true);
	            	mAutoImageSlider.removeCallbacks(animateViewPager);
	                mAutoImageSlider.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
	            }
	            else{
	            	mAutoImageSlider.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
	            }
	        }
	    };
	
	class JavaScriptInterface {
        public void getContentHeight(String value) {
            if (value != null) {
            	
            	mWebViewHeight.add(Integer.parseInt(value));
            	//mInc += 1;
            	//if(mInc == mAdListData.size())
            	//	mExpandableListItemAdapter.notifyDataSetChanged();
            }
        }
    }
	
	private class AccessTokenActionsTask extends AsyncTask<View, Void, String> {

		private String social_type;
		protected View view;
		
	    @Override
	    protected String doInBackground(View... params) {
	    	view = params[0];
	    	int acc_pos = mMainControlBarActivity.getUserPos();
	    	Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	    	social_type = mAccountManager.getUserData(acc[acc_pos], AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = "";//Plus.AccountApi.getAccountName(mMainControlBarActivity.getApiClient());
		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(getActivity(), account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(acc[acc_pos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, getActivity(), null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   					 	Log.e("hearme", "is access:"+String.valueOf(is_access));
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(acc[acc_pos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, getActivity(), null, null);
	   	                    	bnd = future.getResult();
	   	                    	access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	   	                    }
	   				 	}
	                } catch (Exception e) {
	                	e.printStackTrace();
	                	Log.e("hm", "access error "+e.toString());
	                	return "no_conn";
	                }
				 
				return access_token;
	    	}
	    	
	    	//new CustomToast(getActivity()).showErrorToast("фыв: "+String.valueOf(social_type));
	    	//new CustomToast(getActivity()).showErrorToast("фыв: "+String.valueOf(acc_pos));
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(String access_token) {
	        if (access_token != null) {
	        	if(access_token.equals("no_conn")){
		    		new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
		    		return;
		    	}
	        	String url;
	        	if(social_type.equals("1"))
	        		url = Urls.G_USER;
	        	else
	        		url = Urls.USER;
	        	mExpandableListItemAdapter.clickAction(view, url, access_token);
	        	
	        } else {
	        	Intent intent = new Intent();
				intent.putExtra("error", "expired_token");
				new CustomToast(getActivity()).showErrorToast(getString(R.string.error_expired_token));
				return;
	        }
	    }

	}
	
	public void setParent(MainControlBarActivity parent){
		mMainControlBarActivity = parent;
	}
	
	public void setRefreshedData(ArrayList<AdListData> data)
	{
		mMainControlBarActivity.mAdListData = data;
		mExpandableListItemAdapter.notifyDataSetChanged();
		//swingBottomInAnimationAdapter.reset();
		loadCircleState(LOAD_CIRCLE_SUCCESS, data);
		mListView.setAdapter(swingBottomInAnimationAdapter);
		mLoadData = false;
	}
	
	public void setMoreData(ArrayList<AdListData> data)
	{
		//for(int i=0; i<data.size(); i++)
		//{
			mMainControlBarActivity.mAdListData.addAll(data);
		//}
		loadCircleState(LOAD_CIRCLE_SUCCESS, data);
		
		mExpandableListItemAdapter.notifyDataSetChanged();
		if(data.size() != 0 && data.size() == mMainControlBarActivity.getAdCountInList())
			mLoadData = false;
		else
			mLoadData = true;
		//mListView.removeFooterView(mBottomLoadCircle);
	}
	
	public void updateAdapter(){
		if(mExpandableListItemAdapter != null){
			if(swipeLayout != null)
				if(swipeLayout.isRefreshing())
					stopRefreshing();
			
			mPage = 1;
			if(mMainControlBarActivity.getAdArray().size() != 0 && mMainControlBarActivity.getAdArray().size() == mMainControlBarActivity.getAdCountInList())
				mLoadData = false;
			else
				mLoadData = true;
			
			loadCircleState(LOAD_CIRCLE_SUCCESS, mMainControlBarActivity.getAdArray());
			
			
				mExpandableListItemAdapter.notifyDataSetChanged();
				swingBottomInAnimationAdapter.reset();
				mListView.setSelection(0);
		}
	}
	
	public void setAddedAdID(Integer id){
		mAddedAdID = id;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        if(mExpandableListItemAdapter != null)
        	mExpandableListItemAdapter.notifyDataSetChanged();
        
        //Image slider
        mAutoImageSlider.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }
	
	@Override
    public void onPause() {
        super.onPause();
        
        //Image slider
        if (mAutoImageSlider != null) {
        	mAutoImageSlider.removeCallbacks(animateViewPager);
        }
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        mSliderPagerAdapterNoRotate = null;
        mImageSliderViewPager = null;

		mAutoImageSlider = null;
    }
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		//mMainControlBarActivity.selectItem(DrawerListAdapter.AD_ALL, DownloadData.LOAD_AD_LIST);
		mMainControlBarActivity.getAdList(DownloadData.LOAD_AD_LIST, null);
		//loadData(DownloadData.LOAD_AD_LIST);
	}

	public void refreshTrigger(boolean refresh){

		if(!swipeLayout.isRefreshing())
			swipeLayout.post(new Runnable() {
				@Override public void run() {
					swipeLayout.setRefreshing(true);
				}
				});

		if(refresh)
			onRefresh();
	}
	
	public void stopRefreshing()
	{
		swipeLayout.setRefreshing(false);
	}
	
	public void loadCircleState(String load_state, ArrayList<AdListData> data)
	{
		if(mEmptyListView  == null || mBottomCircle  == null ||mBottomCircleError == null )
			return;
		mEmptyListView.setVisibility(View.GONE);
		if(load_state == LOAD_CIRCLE_ERROR){
			mBottomCircle.setVisibility(View.GONE);
			mBottomCircleError.setVisibility(View.VISIBLE);
		}	
		else{
			mBottomCircle.setVisibility(View.VISIBLE);
			mBottomCircleError.setVisibility(View.GONE);
		}
		if(data != null)
		{
			int count = mMainControlBarActivity.getAdCountInList();
			if(data.size() < count)
			{
				mBottomCircle.setVisibility(View.GONE);
				if(data.size() == 0 && mExpandableListItemAdapter.getCount() == 0){
					mEmptyListView.setVisibility(View.VISIBLE);
					emptyListViewInfo();
				}
			}
		}
		else{
			mBottomCircle.setVisibility(View.GONE);
			if(mListView.getCount() ==0){
				mEmptyListView.setVisibility(View.VISIBLE);
				emptyListViewInfo();
			}
		}
		
	}
	
	private void emptyListViewInfo(){
		ImageView image = (ImageView)mEmptyListView.findViewById(R.id.empty_image);
		TextView text = (TextView)mEmptyListView.findViewById(R.id.empty_text);
		
		if(mMainControlBarActivity.getSearchText() != null){
			image.setImageResource(R.drawable.sticker_search);
			text.setText(getString(R.string.error_empty_search));
			return;
		}
		switch (mMainControlBarActivity.getCurrentMenuType()) {
		case DrawerListAdapter.AD_ALL:
			image.setImageResource(R.drawable.sticker_cat);
			text.setText(getString(R.string.empty_list_view));
			break;
		case DrawerListAdapter.AD_MY:
			image.setImageResource(R.drawable.sticker_my_ad);
			text.setText(getString(R.string.user_ad_list_empty));
			break;
		case DrawerListAdapter.AD_FAVORITE_USERS:
			String str_users = getString(R.string.fav_users_list_empty);
			if(mMainControlBarActivity.getDBUser().getFavUsers().size() != 0
				&& mSavedData.getMainAreaId() != - 1){
				str_users = getString(R.string.fav_users_empty_in_area);
			}
			
			image.setImageResource(R.drawable.sticker_fav_user);
			text.setText(str_users);
			break;
		case DrawerListAdapter.AD_FAVORITE:
			String str_ads = getString(R.string.fav_ads_list_empty);
			if(mSavedData.getUserFavoriteAds().size() != 0
				&& mSavedData.getMainAreaId() != - 1){
				str_ads = getString(R.string.fav_ads_empty_in_area);
			}
			image.setImageResource(R.drawable.sticker_fav_ad);
			text.setText(str_ads);
			break;
		default:
			break;
		}
	}
	
	public void enableLoadCircle()
	{
		//mBottomLoadCircle.setVisibility(View.VISIBLE);
	}
	
	public void logOut(){
		mExpandableListItemAdapter.notifyDataSetChanged();
	}
	
	private class MyExpandableListItemAdapter extends ExpandableListItemAdapter<Integer> implements OnClickListener,
																									ExpandCollapseListener,
																									AnimationListener,
																									GetJSONListener
	{
		
        private final Context mContext;
        ArrayList<String> mCatNameList = new ArrayList<String>();
        private String mEncodeCommentText;
        
        private int mOldExpandedPos = 0;
        
        private Animation mAnimFade;
        
        public MyExpandableListItemAdapter(Context context) {
            super(context, R.layout.fragment_main_page_card, R.id.card_title, R.id.card_content);
            mContext = context;
            
            setExpandCollapseListener(this);
            
            mAnimFade = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_wth_delay);
            mAnimFade.setAnimationListener(this);
        }

        @Override
        public int getCount() {
            // Size + number of columns for top empty row
        	if(mMainControlBarActivity.mAdListData != null)
        		return mMainControlBarActivity.mAdListData.size();
        	else
        		return 0;
        }
        
        @Override
        public Integer getItem(int position) {
            return mMainControlBarActivity.mAdListData.get(position).Id;
        }

        @Override
        public long getItemId(int position) {
        	
            return mMainControlBarActivity.mAdListData.get(position).Id;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
        
        @Override
        public View getView(int position, View convertView,
				ViewGroup parent) {
        	convertView = super.getView(position, convertView, parent);
        	LinearLayout card = (LinearLayout)convertView.findViewById(R.id.card);
        	/*if(mMainControlBarActivity.mAdListData.get(position).state == 2)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_rich));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 2)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_rich));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 3)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_free));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 4)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_common));*/
        	/*LinearLayout card = null;
        	if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_main_page_card, null);
				card = (LinearLayout)convertView.findViewById(R.id.card);
				convertView.setTag(card);
        	}
        	else{
        		card = (LinearLayout)convertView.getTag();
        	}
        	
        	card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_free));*/
        	
        	return convertView;
        }

        private class titleViewHolder
        {
			ImageView ad_img;
        	ImageView imageView;
        	ImageView area_icon;
        	LinearLayout category_back;
        	TextView user_name;
        	TextView title;
        	TextView ad_category;
        	TextView desc_text;
        	TextView datetime;
        	ImageButton btn_ad_details;
        	LinearLayout ad_add_notif;
        }
        
        private void updateView(int position){
            View v = mListView.getChildAt(position - 
                mListView.getFirstVisiblePosition()+1);

            if(v == null)
               return;

            TextView ad_cat_text = (TextView)v.findViewById(R.id.ad_category);
            if(ad_cat_text != null)
            if(isExpanded(position)){
            	ad_cat_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			}
			else if(ad_cat_text.getEllipsize() == TextUtils.TruncateAt.MARQUEE){
				ad_cat_text.setEllipsize(TextUtils.TruncateAt.END);
			}
        }
        
        @SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public View getTitleView(int position, View convertView,
				ViewGroup parent) {
			titleViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.main_page_title_adapter, null);
				/*TitleCreateTask task = new TitleCreateTask();
				task.setConvertView(convertView);
				task.execute(position);*/
				viewHolder = new titleViewHolder();
				viewHolder.user_name = (TextView)convertView.findViewById(R.id.user_name);
				viewHolder.ad_img = (RecyclingImageView)convertView.findViewById(R.id.ad_img);
				viewHolder.imageView = (RecyclingImageView)convertView.findViewById(R.id.user_img);
				viewHolder.area_icon = (RecyclingImageView)convertView.findViewById(R.id.area_icon);
				viewHolder.title = (TextView)convertView.findViewById(R.id.title);
				viewHolder.category_back = (LinearLayout)convertView.findViewById(R.id.ad_category_back);
				viewHolder.ad_category = (TextView)convertView.findViewById(R.id.ad_category);
				
				viewHolder.desc_text = (TextView)convertView.findViewById(R.id.desc_text);
				viewHolder.datetime = (TextView)convertView.findViewById(R.id.ad_datetime);
				
				viewHolder.btn_ad_details = (ImageButton)convertView.findViewById(R.id.ad_details_btn);
				viewHolder.ad_add_notif = (LinearLayout)convertView.findViewById(R.id.ad_add_notif);
				viewHolder.ad_img.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Integer pos = (Integer)v.getTag(R.id.ad_img);

						Intent intent = new Intent(getActivity(), AdImagesActivity.class);
						intent.putStringArrayListExtra("image_urls", mMainControlBarActivity.mAdListData.get(pos).files_urls);
						intent.putExtra("image_pos", pos);
						getActivity().startActivity(intent);
					}
				});

				viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						GeneralUserData data = (GeneralUserData)v.getTag(R.id.user_img);
						
						if(data.id == null){
							
							CustomToast toast = new CustomToast(getActivity());
							toast.show(getString(R.string.no_auth_user));
							return;
						}
						Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
						intent.putExtra("acc_pos", mMainControlBarActivity.getUserPos());
						intent.putExtra("user_id", data.id);
						intent.putExtra("acc_type", data.acc_type);
						intent.putExtra("user_display_name", data.name);
						intent.putExtra("desc_text", data.desc_text);
						intent.putExtra("user_img_url", data.img_url);
						startActivity(intent);
					}
				});
				viewHolder.btn_ad_details.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int pos = (Integer)v.getTag();
						mMainControlBarActivity.createAdDetailsActivity(pos);
					}
				});
				
				viewHolder.ad_category.setSelected(true);
				convertView.setTag(viewHolder);
				convertView.setId(position);
			}
			else
			{
				viewHolder = (titleViewHolder)convertView.getTag();
			}
			
			if(mMainControlBarActivity.mAdListData.get(position).user_id == null){
				if(!mMainControlBarActivity.mAdListData.get(position).guest_name.isEmpty())
					viewHolder.user_name.setText(mMainControlBarActivity.mAdListData.get(position).guest_name);
				else
					viewHolder.user_name.setText(getString(R.string.guest));
			}
			else
				viewHolder.user_name.setText(mMainControlBarActivity.mAdListData.get(position).display_name);
			
			if(mMainControlBarActivity.mAdListData.get(position).Id.equals(mAddedAdID)){
				mAddedAdID = -1;
				viewHolder.ad_add_notif.setVisibility(View.VISIBLE);
				viewHolder.ad_add_notif.startAnimation(mAnimFade);
			}
			
			GeneralUserData data = new GeneralUserData();
			data.id = mMainControlBarActivity.mAdListData.get(position).user_id;
			data.acc_type = mMainControlBarActivity.mAdListData.get(position).acc_type;
			data.img_url = mMainControlBarActivity.mAdListData.get(position).user_image_url;
			data.name = mMainControlBarActivity.mAdListData.get(position).display_name;
			viewHolder.imageView.setTag(R.id.user_img, data);
			
			viewHolder.title.setText(mMainControlBarActivity.mAdListData.get(position).Title);
			viewHolder.btn_ad_details.setTag(position);
			viewHolder.ad_img.setTag(R.id.ad_img,position);
			
			if(viewHolder.ad_category.getEllipsize() == TextUtils.TruncateAt.MARQUEE)
				updateView(position);
			
			/*if(mMainControlBarActivity.mAdListData.get(position).state == 1)
				viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
			else if(mMainControlBarActivity.mAdListData.get(position).state == 2)
				viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
			else if(mMainControlBarActivity.mAdListData.get(position).state == 3)
				viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_free);
			else if(mMainControlBarActivity.mAdListData.get(position).state == 4)
				viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad);*/


			String ad_img_url = "";
			if(mMainControlBarActivity.mAdListData.get(position).files_urls != null && mMainControlBarActivity.mAdListData.get(position).files_urls.size() != 0)
				ad_img_url = mMainControlBarActivity.mAdListData.get(position).files_urls.get(0);

			Glide.with(mMainControlBarActivity)
					.load(ad_img_url)
					.asBitmap()
					.centerCrop()
					.placeholder(R.drawable.load_icon)
					.into(new BitmapImageViewTarget(viewHolder.ad_img) {
				@Override
				protected void setResource(Bitmap resource) {
					RoundedBitmapDrawable circularBitmapDrawable =
							RoundedBitmapDrawableFactory.create(mMainControlBarActivity.getResources(), resource);
					circularBitmapDrawable.setCircular(true);
					view.setImageDrawable(circularBitmapDrawable);
				}
			});

			String user_image_url = "";
			if(mMainControlBarActivity.mAdListData.get(position).user_image_url != null)
				user_image_url = mMainControlBarActivity.mAdListData.get(position).user_image_url;

			Glide.with(mMainControlBarActivity)
					.load(user_image_url)
					.asBitmap()
					.centerCrop()
					.placeholder(R.drawable.icon_no_avatar)
					.into(new BitmapImageViewTarget(viewHolder.imageView) {
						@Override
						protected void setResource(Bitmap resource) {
							RoundedBitmapDrawable circularBitmapDrawable =
									RoundedBitmapDrawableFactory.create(mMainControlBarActivity.getResources(), resource);
							circularBitmapDrawable.setCircular(true);
							view.setImageDrawable(circularBitmapDrawable);
						}
					});

			
			for (int i = 0; i < mMainControlBarActivity.mAreaData.size(); i++) {
				if(mMainControlBarActivity.mAreaData.get(i).id.equals(mMainControlBarActivity.mAdListData.get(position).area)){
					viewHolder.area_icon.setImageResource(mMainControlBarActivity.mAreaData.get(i).icon_res);
					break;
				}
			}

			TitleTask task = new TitleTask();
			task.setHolder(viewHolder);
			task.execute(position);
    		viewHolder.desc_text.setText(mMainControlBarActivity.mAdListData.get(position).Desc);
	    		
	        return convertView;
		}
        
        private class TitleTask extends AsyncTask<Integer, Void, Void>{

        	String category_text = "";
        	CharSequence dt = "";
        	titleViewHolder mViewHolder;
        	GradientDrawable g;
        	int mPos;
        	
        	public void setHolder(titleViewHolder holder){
        		mViewHolder = holder;
        	}
			@Override
			protected Void doInBackground(Integer... pos) {
				// TODO Auto-generated method stub
				try{
					mPos = pos[0];
					if(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color != null)
						if(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color.length() != 0)
						{
							g = new GradientDrawable(Orientation.TL_BR, new int[] { Color.parseColor(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color), Color.parseColor(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color)});
							g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
							g.setGradientCenter(0.0f, 0.45f);
							
							Resources r = getResources();
							float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
							g.setCornerRadius(px);
							
						}
					mCatNameList.clear();
		    		if(!mMainControlBarActivity.mAdListData.get(pos[0]).Parent_cat_id.equals(0)){
						for(int i=0; i<mCategoryData.size(); i++)
						{
							if(mCategoryData.get(i).id.equals(mMainControlBarActivity.mAdListData.get(pos[0]).Parent_cat_id))
							{
								createCatList(mCategoryData.get(i).id);
								break;
							}
						}
					}
		    		
		    		for(int i=mCatNameList.size()-1; i>-1; i--)
					{
		    			category_text += mCatNameList.get(i) + " > ";
					}
		    		category_text += mMainControlBarActivity.mAdListData.get(pos[0]).Category_name;
		    		
		    		dt = DateUtils.getRelativeDateTimeString(getActivity(), 
		    				mMainControlBarActivity.mAdListData.get(pos[0]).add_datetime, 
		    				DateUtils.MINUTE_IN_MILLIS, 
		    				DateUtils.WEEK_IN_MILLIS, 
		    				DateUtils.FORMAT_ABBREV_ALL);
				}
	    		catch(Exception e){
	    			return null;
	    		}
				return null;
			}

			@Override
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				try{
					if(Build.VERSION.SDK_INT <= 15)
						mViewHolder.category_back.setBackgroundDrawable(g);
					else
					{
						mViewHolder.category_back.setBackground(g);
					}
					
					if(mViewHolder.ad_category.getEllipsize() == TextUtils.TruncateAt.MARQUEE)
						updateView(mPos);
					mViewHolder.ad_category.setText(category_text);
					mViewHolder.datetime.setText(dt);
				}catch(Exception e){
					
				}
				super.onPostExecute(result);
			}
			
			
        	
        }
        
        
        
        private void createCatList(Integer id){
			for(int i=0; i<mCategoryData.size(); i++)
			{
				if(mCategoryData.get(i).id.equals(id))
				{
					mCatNameList.add(mCategoryData.get(i).name);
					if(!mCategoryData.get(i).parent_id.equals(0))
					{
						createCatList(mCategoryData.get(i).parent_id);
					}
					break;
				}
			}
		}
		
		

		private class ContentViewHolder
		{
			TextView text;
			TextView content_text;
			ImageView favorite_img;
			ImageView favorite_user_img;
			RelativeLayout btn_fav_ad;
			RelativeLayout mFavorite_user;
			RelativeLayout layout;
			RelativeLayout btn_ad_details;
			TextView comment_count;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getContentView(int position, View convertView,
				ViewGroup parent) {
				
				ContentViewHolder viewHolder;
				if(convertView == null)
				{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.main_page_adapter, null);		
					
					viewHolder = new ContentViewHolder();
					viewHolder.content_text = (TextView)convertView.findViewById(R.id.content_text);

					viewHolder.layout = (RelativeLayout)convertView.findViewById(R.id.layout);
					viewHolder.favorite_img = (ImageView)convertView.findViewById(R.id.favorite_ad_img);
					viewHolder.favorite_user_img = (ImageView)convertView.findViewById(R.id.favorite_user_img);
					
					viewHolder.btn_fav_ad = (RelativeLayout)convertView.findViewById(R.id.favorite_ad_btn);
					viewHolder.mFavorite_user = (RelativeLayout)convertView.findViewById(R.id.favorite_user_btn);
					viewHolder.btn_ad_details = (RelativeLayout)convertView.findViewById(R.id.btn_ad_details);
					viewHolder.comment_count = (TextView) convertView.findViewById(R.id.comment_count);

					viewHolder.btn_fav_ad.setOnClickListener(MyExpandableListItemAdapter.this);
					viewHolder.mFavorite_user.setOnClickListener(MyExpandableListItemAdapter.this);
					viewHolder.btn_ad_details.setOnClickListener(MyExpandableListItemAdapter.this);

					convertView.setTag(viewHolder);
				}
				else
				{
					viewHolder = (ContentViewHolder)convertView.getTag();

				}
				
				viewHolder.favorite_user_img.setTag(mMainControlBarActivity.mAdListData.get(position).user_name);
				viewHolder.btn_fav_ad.setTag(mMainControlBarActivity.mAdListData.get(position).Id);
				
				viewHolder.mFavorite_user.setTag(mMainControlBarActivity.mAdListData.get(position).user_id);
				
				ContentTask task = new ContentTask();
				task.setHolder(viewHolder);
				task.execute(position);
				
				viewHolder.content_text.setText(mMainControlBarActivity.mAdListData.get(position).Desc);

				viewHolder.btn_ad_details.setTag(position);

				if(mMainControlBarActivity.mAdListData.get(position).comment_count != 0) {
					viewHolder.comment_count.setVisibility(View.VISIBLE);
					viewHolder.comment_count.setText(mMainControlBarActivity.mAdListData.get(position).comment_count.toString());
				}
				else
					viewHolder.comment_count.setVisibility(View.GONE);

	            return convertView;
		}
		
		private class ContentTask extends AsyncTask<Integer, Void, Void>{

			Drawable fav_ad_icon;
			Drawable fav_user_icon;
			ContentViewHolder mViewHolder;
			
			public void setHolder(ContentViewHolder holder){
	    		mViewHolder = holder;
	    	}
			@Override
			protected Void doInBackground(Integer... pos) {
				// TODO Auto-generated method stub
				try{
					fav_ad_icon = getActivity().getResources().getDrawable(R.drawable.icon_deactive_fav_ad);
					fav_user_icon = getActivity().getResources().getDrawable(R.drawable.icon_deactive_fav_user);
					for(Integer fav_ad : mMainControlBarActivity.getDBUser().getFavAds())
					{
						if(fav_ad.equals(mMainControlBarActivity.mAdListData.get(pos[0]).Id))
						{
							fav_ad_icon = getActivity().getResources().getDrawable(R.drawable.icon_active_fav_ad);
							break;
						}
					}
					for(Integer fav_user_id : mMainControlBarActivity.getDBUser().getFavUsers())
					{
						if(fav_user_id.equals(mMainControlBarActivity.mAdListData.get(pos[0]).user_id))
						{
							fav_user_icon = getActivity().getResources().getDrawable(R.drawable.icon_active_fav_user);
							break;
						}
					}
				}catch(Exception e){
					return null;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				try{
				if(fav_ad_icon != null)
					mViewHolder.favorite_img.setImageDrawable(fav_ad_icon);
				if(fav_user_icon != null)
					mViewHolder.favorite_user_img.setImageDrawable(fav_user_icon);
				}catch(Exception e){
					
				}
				super.onPostExecute(result);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!mMainControlBarActivity.isLogin()){
				new CustomToast(getActivity()).show(getString(R.string.toast_need_auth));
				mMainControlBarActivity.openMenu();
				return;
			}
			if(mMainControlBarActivity.getApiClient().isConnected() || mMainControlBarActivity.isLogin()){
				
				switch (v.getId()) {
				case R.id.favorite_ad_btn:
					ImageView img = (ImageView)v.findViewById(R.id.favorite_ad_img);
					ProgressBar progress = (ProgressBar)v.findViewById(R.id.progress_fav_ad);
					img.setVisibility(View.GONE);
					progress.setVisibility(View.VISIBLE);
					break;
				case R.id.favorite_user_btn:
					ImageView img2 = (ImageView)v.findViewById(R.id.favorite_user_img);
					ProgressBar progress2 = (ProgressBar)v.findViewById(R.id.progress_fav_user);
					img2.setVisibility(View.GONE);
					progress2.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
				new AccessTokenActionsTask().execute(v);
				
			}
		}
		
		
		public void clickAction(View v, String url, String access_token){
			
			JSONParser asyncPoster = new JSONParser(MyExpandableListItemAdapter.this, v);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("url", url);
			params.put("access_token", access_token);
			
			switch (v.getId()) {
			case R.id.favorite_ad_btn:
				
				Integer fav_ad_id = (Integer)v.getTag();			
				params.put("ad_id", String.valueOf(v.getTag()));
				
				boolean is_ad_already_exist = false;
				for (Integer fav_ad : mMainControlBarActivity.getDBUser().getFavAds()) {
					if (fav_ad.equals(fav_ad_id)){
						is_ad_already_exist = true;
						break;
					}
				}
				if(!is_ad_already_exist)
				{
					params.put(CommonTags.TAG, CommonTags.FAV_AD_ADD);
					mTempParams.putAll(params);
					asyncPoster.execute(params);
				}
				else
				{
					params.put(CommonTags.TAG, CommonTags.FAV_AD_REMOVE);
					mTempParams.putAll(params);
					asyncPoster.execute(params);
				}
				break;
			case R.id.favorite_user_btn:
				
				Integer fav_user_id = (Integer)v.getTag();
				
				if(fav_user_id == null){
					new CustomToast(getActivity()).showErrorToast(getString(R.string.error_not_guest_to_fav));
					return;
				}
				else if(fav_user_id == 1){
					new CustomToast(getActivity()).showErrorToast(getString(R.string.error_not_yourself_to_fav));
					return;
				}
				
				params.put("fav_user_id", String.valueOf(fav_user_id));
				
				boolean is_user_already_exist = false;
				for (Integer user_id : mMainControlBarActivity.getDBUser().getFavUsers()) {
					if (fav_user_id.equals(user_id)){
						is_user_already_exist = true;
						break;
					}
				}
				if(!is_user_already_exist)
				{
					params.put(CommonTags.TAG, CommonTags.FAV_USER_ADD);
					mTempParams.putAll(params);
					asyncPoster.execute(params);
				}
				else
				{
					params.put(CommonTags.TAG, CommonTags.FAV_USER_REMOVE);
					mTempParams.putAll(params);
					asyncPoster.execute(params);
				}
				
				break;
				case R.id.btn_ad_details:
					int pos = (Integer)v.getTag();
					mMainControlBarActivity.createAdDetailsActivity(pos);
					break;
			default:
				break;
			}
		}

		@Override
		public void onItemCollapsed(int position) {
			// TODO Auto-generated method stub
			updateView(position);
		}

		@Override
		public void onItemExpanded(int position) {
			// TODO Auto-generated method stub
			updateView(position);
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRemoteCallComplete(JSONObject obj, View v) {
			// TODO Auto-generated method stub
			try {
				if(!obj.isNull("error")){
					if(!obj.getString("error").equals("no_conn"))
						new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
					else if(!obj.getString("error").equals("error_parse"))
						new CustomToast(getActivity()).showErrorToast(getString(R.string.error_load_data));
					else if(obj.getString("error").equals("expired_token")){
	            	}
				}
				else if(obj.getBoolean("success")){
					String tag = obj.getString("tag");
					if(tag.equals(AdTags.insert_ad_comment)){
						new CustomToast(getActivity()).show(getString(R.string.success_comment));
					}
					if(tag.equals(CommonTags.FAV_USER_ADD) || tag.equals(CommonTags.FAV_USER_REMOVE)){
						ImageView img = (ImageView)v.findViewById(R.id.favorite_user_img);
						Integer user_id = (Integer)v.getTag();
						
						if(tag.equals(CommonTags.FAV_USER_ADD)){
							mMainControlBarActivity.getDBUser().addFavUser(user_id);
							img.setImageResource(R.drawable.icon_active_fav_user);
						}
						else if(tag.equals(CommonTags.FAV_USER_REMOVE)){
							mMainControlBarActivity.getDBUser().removeFavUser(user_id);
							
							img.setImageResource(R.drawable.icon_deactive_fav_user);
							//mMainControlBarActivity.mAdListData.clear();
							if(mMainControlBarActivity.getType() == DrawerListAdapter.AD_FAVORITE_USERS){
								for (int i = 0; i < mMainControlBarActivity.getSelectedUsers().size(); i++) {
									if(mMainControlBarActivity.getSelectedUsers().get(i).equals(user_id)){
										mMainControlBarActivity.getSelectedUsers().remove(i);
										i -= 1;
			 						}
								}
								mMainControlBarActivity.showFilterStateInfo(true);
								mLoadData = true;
								mPage = 0;
								
								if(mMainControlBarActivity.getCurrentMenuType() == DrawerListAdapter.AD_FAVORITE_USERS && mMainControlBarActivity.getSelectedUsers().size() == 0){
									mMainControlBarActivity.mAdListData.clear();
									loadCircleState(LOAD_CIRCLE_SUCCESS, mMainControlBarActivity.mAdListData);
									mExpandableListItemAdapter.notifyDataSetChanged();
								}
							}
						}
						
						for(int i=mListView.getFirstVisiblePosition(); i<=mListView.getLastVisiblePosition(); i++)
						{
							if(mListView.getChildAt(i) != null)
							{
								RelativeLayout fav_btn = (RelativeLayout)mListView.getChildAt(i).findViewById(R.id.favorite_user_btn);
								ImageView fav_img = (ImageView)mListView.getChildAt(i).findViewById(R.id.favorite_user_img);
								
								if(fav_btn != null)
								{
									Integer fav_users_id2 = (Integer)fav_btn.getTag();
									fav_img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_deactive_fav_user));
									
									for(Integer fav_user_id : mMainControlBarActivity.getDBUser().getFavUsers())
									{
										if(fav_user_id.equals(fav_users_id2))
										{
											fav_img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_active_fav_user));
											break;
										}
									}
								}
							}
						}
						
						
					}
					else if(tag.equals(CommonTags.FAV_AD_ADD) || tag.equals(CommonTags.FAV_AD_REMOVE)){
						Integer fav_ad_id = (Integer)v.getTag();
						
						ImageView img = (ImageView)v.findViewById(R.id.favorite_ad_img);
						
						if(tag.equals(CommonTags.FAV_AD_ADD)){
							mMainControlBarActivity.getDBUser().addFavAd(fav_ad_id);
							img.setImageResource(R.drawable.icon_active_fav_ad);
						}
						else if(tag.equals(CommonTags.FAV_AD_REMOVE)){
							mMainControlBarActivity.getDBUser().removeFavAd(fav_ad_id);
							img.setImageResource(R.drawable.icon_deactive_fav_ad);
							
							if(mMainControlBarActivity.getType() == DrawerListAdapter.AD_FAVORITE){
								for (int i = 0; i < mMainControlBarActivity.mAdListData.size(); i++) {
									if(mMainControlBarActivity.mAdListData.get(i).Id.equals(fav_ad_id)){
										mMainControlBarActivity.mAdListData.remove(i);
										mExpandableListItemAdapter.notifyDataSetChanged();
										loadCircleState(LOAD_CIRCLE_SUCCESS, mMainControlBarActivity.mAdListData);
										break;
									}
								}
							}
						}
						
					}
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				new CustomToast(getActivity()).showErrorToast(getString(R.string.error_unknown));
				e.printStackTrace();
			}
			
			if(v != null){
				switch (v.getId()) {
				case R.id.favorite_ad_btn:
					ImageView img = (ImageView)v.findViewById(R.id.favorite_ad_img);
					ProgressBar progress = (ProgressBar)v.findViewById(R.id.progress_fav_ad);
					img.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
					break;
				case R.id.favorite_user_btn:
					ImageView img2 = (ImageView)v.findViewById(R.id.favorite_user_img);
					ProgressBar progress2 = (ProgressBar)v.findViewById(R.id.progress_fav_user);
					img2.setVisibility(View.VISIBLE);
					progress2.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
			mTempParams.clear();
		}

    }
	
	public void setCatData(ArrayList<CategoryData> data){
    	mCategoryData = data;
    }
	
	public void updateSlider(){
		mSliderPagerAdapterNoRotate.updateData(mMainControlBarActivity.getSelectedMainParentId());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
			mMainControlBarActivity.setListViewState(true);
        }
		if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
			mMainControlBarActivity.setListViewState(true);
		}
		if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
			mMainControlBarActivity.setListViewState(false);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		
		if(loadMore && totalItemCount != 0 && !mLoadData)
		{
			
			mLoadData = true;
			mPage +=1;
			//loadData(DownloadData.GET_MORE_AD);
			mMainControlBarActivity.getAdList(DownloadData.GET_MORE_AD, null);
		}
	}

	@Override
	public void getDownloadDCompleteState(int loadType, ArrayList<? extends Object> data_array) {
		// TODO Auto-generated method stub
		switch (loadType) {
		case DownloadData.LOAD_AD_LIST:
			ArrayList<AdListData> data = (ArrayList<AdListData>)data_array;
			setRefreshedData(data);
			stopRefreshing();
			break;
		case DownloadData.GET_MORE_AD:
			ArrayList<AdListData> data_ad = (ArrayList<AdListData>)data_array;
			setMoreData(data_ad);
			break;

		default:
			break;
		}
	}

	@Override
	public void onUpScrolling(int offset_y) {
		// TODO Auto-generated method stub
		/*mMainControlBarActivity.setSlidingPaneOffsetY(image_slider.getTop());
        if(offset_y < mActionBarHeight*-1)
        {	
        	if(mMainControlBarActivity.isSlidingPaneOpen()){
        		if(mMainControlBarActivity.isFilterShowing())
        			mMainControlBarActivity.getFragmentFilterState().hideFilter();
				if(!mMainControlBarActivity.getSupportActionBar().isShowing())
					mMainControlBarActivity.getSupportActionBar().show();
			}
        	else if(!mMainControlBarActivity.getSupportActionBar().isShowing()){
        		if(!mMainControlBarActivity.isFilterShowing())
        			mMainControlBarActivity.getFragmentFilterState().showFilter();
				mMainControlBarActivity.getSupportActionBar().show();
			}
        }*/
	}

	@Override
	public void onDownScrolling(int offset_y) {
		// TODO Auto-generated method stub
		/*mMainControlBarActivity.setSlidingPaneOffsetY(image_slider.getTop());
		if(offset_y < mActionBarHeight*-1)
        {	
			if(mMainControlBarActivity.getSupportActionBar().isShowing()){
				if(mMainControlBarActivity.isFilterShowing())
					mMainControlBarActivity.getFragmentFilterState().hideFilter();
				mMainControlBarActivity.getSupportActionBar().hide();
			}
        }*/
	}

	@Override
	public void getInsertCompleteState(Object result) {
		// TODO Auto-generated method stub
		if(result == "no_connection"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		else if(result == "error"){
			new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
			return;
		}
		else if(result == "expired_token"){
			
			new AsyncTask<Void, Void, HashMap<String, String>>() {

				@Override
				protected HashMap<String, String> doInBackground(Void... params) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				protected void onPostExecute(HashMap<String, String> result) {
					// TODO Auto-generated method stub
					if(result.get("error") != null && result.get("error").equals("expired_token")){
						Intent intent = new Intent();
						intent.putExtra("error", "expired_token");
						logOut();
						mMainControlBarActivity.selectItem(DrawerListAdapter.PROFILE, -1);
						return;
					}
						
					
					super.onPostExecute(result);
				}
			}.execute();
			return;
		}
	}

	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub
		try {
			if(!obj.isNull("error")){
				if(!obj.getString("error").equals("no_conn"))
					new CustomToast(getActivity()).showErrorToast(getString(R.string.no_connection));
				else if(!obj.getString("error").equals("error_parse"))
					new CustomToast(getActivity()).showErrorToast(getString(R.string.error_load_data));
				else if(obj.getString("error").equals("expired_token")){
					new AsyncTask<Void, Void, HashMap<String, String>>() {

						@Override
						protected HashMap<String, String> doInBackground(Void... params) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						protected void onPostExecute(HashMap<String, String> result) {
							// TODO Auto-generated method stub
							if(result.get("error") != null && result.get("error").equals("expired_token")){
								Intent intent = new Intent();
								intent.putExtra("error", "expired_token");
								logOut();
								mMainControlBarActivity.selectItem(DrawerListAdapter.PROFILE, -1);
								return;
							}
							recallAction();	
							
							super.onPostExecute(result);
						}
					}.execute();
            	}
				return;
			}
			else if(!obj.isNull("tag")){
				if(!obj.isNull("success") && obj.getBoolean("success") == true){
					if(obj.getString("tag").equals(AdTags.insert_ad_comment))
						new CustomToast(getActivity()).show(getString(R.string.success_comment));
				}	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recallAction(){
		
	}
}
