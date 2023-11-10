package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donearh.hearme.DownloadData.DownloadDCompleteListener;
import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.ObservableListView.OnDetectScrollListener;
import com.donearh.hearme.datatypes.FavUsersData;
import com.donearh.imageloader.ImageFromResource;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;
import com.donearh.imageslider.AdapterImageSlider;
import com.donearh.imageslider.MyPagerAdapterNoRotate;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter.ExpandCollapseListener;


@SuppressLint({ "JavascriptInterface", "NewApi" })
public class MainPage extends Fragment implements AdapterView.OnItemClickListener, 
													OnRefreshListener,
													OnScrollListener,
													DownloadDCompleteListener,
													OnDetectScrollListener,
													InsertCompleteListener
{	

    
	public static String LOAD_CIRCLE_SUCCESS = "success";
	public static String LOAD_CIRCLE_ERROR = "error";
	//private AdData mAdData;
	private MyExpandableListItemAdapter mExpandableListItemAdapter;
	
	private View mRootView;
	
	private ImageLoaderRounded mImageLoaderRounded;
	private ImageFromResource mImageFromRes;
	private MainControlBarActivity mMainControlBarActivity;
	public ArrayList<CategoryData> mCategoryData;
	
	//IMAGE SLIDER
	//public MyPagerAdapter mSliderPagerAdapter;
	public MyPagerAdapterNoRotate mSliderPagerAdapterNoRotate;
	private int mActionBarHeight;
	private boolean mPageMoved = false;
	private static final long ANIM_VIEWPAGER_DELAY = 2000;
	public ViewPager mImageSliderViewPager;
	private AdapterImageSlider mSectionsAdapter;
	public static ArrayList<String> mSliderData;
	private Handler mAutoImageSlider;
	private int currentSlide;
	
	private RelativeLayout mBottomCircle;
	private LinearLayout mBottomCircleError;
	private LinearLayout mEmptyListView;
	
	//private ArrayList<AdListData> mAdListData;
	private ArrayList<WebView> mWebViewArray;
	private ArrayList<Integer> mWebViewHeight;
	
	private ArrayList<Integer> mFavoriteAds;
	private ArrayList<FavUsersData> mFavoriteUsers;
	//private AdAdapter mAdAdapter;
	private ObservableListView mListView;
	
	private int mInc = 0;
	private ProgressBar mProgressBar = null;
	
	public int mPage = 0;
	
	boolean mDo = true;
	boolean mUpdate = true;
	
	boolean mLoadData = false;
	private WebView mWebView;
	private static int webviewContentWidth = 0;
	
	SwipeRefreshLayout swipeLayout;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	
	private Integer mLoadType;
	private View image_slider;
	
	public MainPage()
	{	
		mWebViewArray = new ArrayList<WebView>();
		mWebViewHeight = new ArrayList<Integer>();
		
		mFavoriteAds = new ArrayList<Integer>();
		mFavoriteUsers = new ArrayList<FavUsersData>();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mMainControlBarActivity = (MainControlBarActivity)getActivity();
        
        mImageLoaderRounded = new ImageLoaderRounded(getActivity(), ImageLoader.DIR_USER_AVATAR);
        mImageFromRes = new ImageFromResource(getActivity());
        mFavoriteAds = ((MainControlBarActivity)getActivity()).mSavedData.getUserFavoriteAds();
        mFavoriteUsers = mMainControlBarActivity.getDBUser().getFavUsers();
        mSliderData = ((MainControlBarActivity)getActivity()).getSliderData();
        mCategoryData = mMainControlBarActivity.mCategoryData;
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		super.onCreateView(inflater, container, saveInstanceState);
		//IMAGE SLIDER	
		
		image_slider = LayoutInflater.from(getActivity()).inflate(R.layout.main_image_slider, null);
		mImageSliderViewPager = (ViewPager) image_slider.findViewById(R.id.image_slider);

		mSliderPagerAdapterNoRotate = new MyPagerAdapterNoRotate(getActivity(),
											 	getActivity().getSupportFragmentManager());
		mImageSliderViewPager.setAdapter(mSliderPagerAdapterNoRotate);
		mImageSliderViewPager.setOnPageChangeListener(mSliderPagerAdapterNoRotate);
		currentSlide = MainControlBarActivity.FIRST_PAGE;
		mImageSliderViewPager.setCurrentItem(currentSlide);
		mImageSliderViewPager.setOffscreenPageLimit(3);
		mImageSliderViewPager.setPageMargin(Integer.parseInt(getString(R.string.pagermargin)));
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
				mMainControlBarActivity.setNotSlidableZone(image_slider.getMeasuredHeight());
				
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
				mMainControlBarActivity.getAdList(DownloadData.GET_MORE_AD);
			}
		});
		
		mEmptyListView = (LinearLayout)circleView.findViewById(R.id.empty_list);	
		mEmptyListView.setVisibility(View.GONE);
		mRootView = inflater.inflate(R.layout.main_page_fragment, container, false);
		//mAdAdapter = new AdAdapter(getActivity());
		
		swipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_layout);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.light_blue, 
	            R.color.night_green,
	            R.color.light_green, 
	            R.color.night_blue);
		
		mListView = (ObservableListView) mRootView.findViewById(R.id.listview);
		
		mExpandableListItemAdapter = new MyExpandableListItemAdapter(getActivity());
		mExpandableListItemAdapter.setLimit(1);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mExpandableListItemAdapter);
		//swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setOnDetectScrollListener(this);
		//mListView.addFooterView(mBottomLoadCircle);
		
		mListView.addHeaderView(image_slider);
		mListView.addFooterView(circleView);
		loadCircleState(LOAD_CIRCLE_SUCCESS, mMainControlBarActivity.mAdListData);
		mListView.setAdapter(swingBottomInAnimationAdapter);

		LayoutParams lparams = new LayoutParams(
				   LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		
		View v = (View)circleView.findViewById(R.id.gr_back_circle);
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		v.startAnimation(a);
		
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
		
      mMainControlBarActivity.showFilterStateInfo(true);
		return mRootView;
		
	}
	
	
	
	 @Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mRootView = null;
	}



	private Runnable animateViewPager = new Runnable() {
	        public void run() {
	        	
	            if (!mSliderPagerAdapterNoRotate.isSliderMove() && mImageSliderViewPager.isShown()) {          	
	            	if (currentSlide == mSliderData.size()-1) {
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
	
	public void setParent(MainControlBarActivity parent){
		mMainControlBarActivity = parent;
	}
	
	public void setData(ArrayList<AdListData> data)
	{
		if(swipeLayout != null)
			if(swipeLayout.isRefreshing())
				stopRefreshing();
		//mMainControlBarActivity.mAdListData = data;
		mPage = 1;
		mLoadData = false;
		if(mExpandableListItemAdapter != null){
			mExpandableListItemAdapter.notifyDataSetChanged();
			swingBottomInAnimationAdapter.reset();
			mListView.setSelection(0);
		}
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
		for(int i=0; i<data.size(); i++)
		{
			mMainControlBarActivity.mAdListData.add(data.get(i));
		}
		loadCircleState(LOAD_CIRCLE_SUCCESS, data);
		mExpandableListItemAdapter.notifyDataSetChanged();
		if(data.size() != 0)
			mLoadData = false;
		else
			mLoadData = true;
		//mListView.removeFooterView(mBottomLoadCircle);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageLoaderRounded.onResume();
        if(mExpandableListItemAdapter != null)
        	mExpandableListItemAdapter.notifyDataSetChanged();
        
        //Image slider
        mAutoImageSlider.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }
	
	@Override
    public void onPause() {
        super.onPause();
        mImageLoaderRounded.onPause();
        
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
        
        mImageLoaderRounded.destroy();
		mAutoImageSlider = null;
    }
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		//mMainControlBarActivity.selectItem(DrawerListAdapter.AD_ALL, DownloadData.LOAD_AD_LIST);
		mMainControlBarActivity.getAdList(-1);
		//loadData(DownloadData.LOAD_AD_LIST);
	}
	
	public void stopRefreshing()
	{
		swipeLayout.setRefreshing(false);
	}
	
	public void loadCircleState(String load_state, ArrayList<AdListData> data)
	{
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
				if(data.size() == 0 && mExpandableListItemAdapter.getCount() == 0)
					mEmptyListView.setVisibility(View.VISIBLE);
			}
		}
		else{
			mBottomCircle.setVisibility(View.GONE);
			if(mListView.getCount() ==0){
				mEmptyListView.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	public void enableLoadCircle()
	{
		//mBottomLoadCircle.setVisibility(View.VISIBLE);
	}
	
	public void logOut(){
		mFavoriteAds.clear();
		mFavoriteUsers.clear();
		mExpandableListItemAdapter.notifyDataSetChanged();
	}
	
	private class MyExpandableListItemAdapter extends ExpandableListItemAdapter<Integer> implements OnClickListener,
																									OnFocusChangeListener,
																									ExpandCollapseListener
	{
		
        private final Context mContext;
        ArrayList<String> mCatNameList = new ArrayList<String>();
        private String mEncodeCommentText;
        
        private int mOldExpandedPos = 0;
        
        public MyExpandableListItemAdapter(Context context) {
            super(context, R.layout.fragment_main_page_card, R.id.card_title, R.id.card_content);
            mContext = context;
            
            setExpandCollapseListener(this);
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
        	if(mMainControlBarActivity.mAdListData.get(position).state == 2)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_rich));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 2)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_rich));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 3)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_free));
        	else if(mMainControlBarActivity.mAdListData.get(position).state == 4)
        		card.setBackground(getActivity().getResources().getDrawable(R.drawable.card_common));
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
        	ImageView imageView;
        	ImageView area_icon;
        	LinearLayout category_back;
        	TextView title;
        	TextView ad_category;
        	TextView desc_text;
        	TextView datetime;
        	Button btn_ad_details;
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
				TitleCreateTask task = new TitleCreateTask();
				task.setConvertView(convertView);
				task.execute(position);
			}
			else
			{
				viewHolder = (titleViewHolder)convertView.getTag();
				
				viewHolder.title.setText(mMainControlBarActivity.mAdListData.get(position).Title);
				viewHolder.btn_ad_details.setTag(position);
				
				if(viewHolder.ad_category.getEllipsize() == TextUtils.TruncateAt.MARQUEE)
					updateView(position);
				
				if(mMainControlBarActivity.mAdListData.get(position).state == 1)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
				else if(mMainControlBarActivity.mAdListData.get(position).state == 2)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
				else if(mMainControlBarActivity.mAdListData.get(position).state == 3)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_free);
				else if(mMainControlBarActivity.mAdListData.get(position).state == 4)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad);
	    		
				mImageLoaderRounded.mImageFetcherRounded.loadImage(mMainControlBarActivity.mAdListData.get(position).user_image_url, viewHolder.imageView);
				mImageFromRes.loadBitmap(mMainControlBarActivity.mAreaData.get(mMainControlBarActivity.mAdListData.get(position).area).icon_res, viewHolder.area_icon);
				
				TitleTask task = new TitleTask();
				task.setHolder(viewHolder);
				task.execute(position);
	    		viewHolder.desc_text.setText(mMainControlBarActivity.mAdListData.get(position).Desc);
			}
	    		
	        return convertView;
		}
        
        private class TitleCreateTask extends AsyncTask<Integer, Void, Void>{

        	View convertView;
        	titleViewHolder viewHolder;
        	private int mPos;
        	private Drawable full_ad;
        	
        	
        	public void setConvertView(View view){
        		convertView = view;
        	}
			@Override
			protected Void doInBackground(Integer... pos) {
				// TODO Auto-generated method stub
				mPos = pos[0];
				viewHolder = new titleViewHolder();
				viewHolder.imageView = (RecyclingImageView)convertView.findViewById(R.id.user_img);
				viewHolder.area_icon = (RecyclingImageView)convertView.findViewById(R.id.area_icon);
				viewHolder.title = (TextView)convertView.findViewById(R.id.title);
				viewHolder.category_back = (LinearLayout)convertView.findViewById(R.id.ad_category_back);
				viewHolder.ad_category = (TextView)convertView.findViewById(R.id.ad_category);
				
				viewHolder.desc_text = (TextView)convertView.findViewById(R.id.desc_text);
				viewHolder.datetime = (TextView)convertView.findViewById(R.id.ad_datetime);
				
				viewHolder.btn_ad_details = (Button)convertView.findViewById(R.id.ad_details_btn);
				viewHolder.btn_ad_details.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int pos = (Integer)v.getTag();
						mMainControlBarActivity.createAdDetailsActivity(pos);
					}
				});
				
				
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				viewHolder.ad_category.setSelected(true);
				convertView.setTag(viewHolder);
				convertView.setId(mPos);
				
				viewHolder.title.setText(mMainControlBarActivity.mAdListData.get(mPos).Title);
				viewHolder.btn_ad_details.setTag(mPos);
				
				if(mMainControlBarActivity.mAdListData.get(mPos).state == 1)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
				else if(mMainControlBarActivity.mAdListData.get(mPos).state == 2)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_rich);
				else if(mMainControlBarActivity.mAdListData.get(mPos).state == 3)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad_free);
				else if(mMainControlBarActivity.mAdListData.get(mPos).state == 4)
					viewHolder.btn_ad_details.setBackgroundResource(R.drawable.selector_circle_full_ad);
				
				mImageLoaderRounded.mImageFetcherRounded.loadImage(mMainControlBarActivity.mAdListData.get(mPos).user_image_url, viewHolder.imageView);
				mImageFromRes.loadBitmap(mMainControlBarActivity.mAreaData.get(mMainControlBarActivity.mAdListData.get(mPos).area).icon_res, viewHolder.area_icon);
				
				viewHolder.desc_text.setText(mMainControlBarActivity.mAdListData.get(mPos).Desc);
				
				TitleTask task = new TitleTask();
				task.setHolder(viewHolder);
				task.execute(mPos);
	    		
			}
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
				mPos = pos[0];
				if(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color != null)
					if(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color.length() != 0)
					{
						g = new GradientDrawable(Orientation.TL_BR, new int[] { Color.parseColor(mMainControlBarActivity.mAdListData.get(pos[0]).cat_left_color), Color.parseColor(mMainControlBarActivity.mAdListData.get(pos[0]).cat_right_color)});
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
						if(mCategoryData.get(i).Id.equals(mMainControlBarActivity.mAdListData.get(pos[0]).Parent_cat_id))
						{
							createCatList(mCategoryData.get(i).Id);
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
				return null;
			}

			@Override
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
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
				
				super.onPostExecute(result);
			}
			
			
        	
        }
        
        
        private void createCatList(Integer id){
			for(int i=0; i<mCategoryData.size(); i++)
			{
				if(mCategoryData.get(i).Id.equals(id))
				{
					mCatNameList.add(mCategoryData.get(i).Name);
					if(!mCategoryData.get(i).ParentId.equals(0))
					{
						createCatList(mCategoryData.get(i).ParentId);
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
			LinearLayout mFavorite;
			LinearLayout mFavorite_user;
			RelativeLayout layout;
			EditText comment_text;
			Button btn_add_comment;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getContentView(int position, View convertView,
				ViewGroup parent) {
				
				ContentViewHolder viewHolder;
				if(convertView == null)
				{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.main_page_adapter, null);		
					
					ContentCreateTask content_task = new ContentCreateTask();
					content_task.setConvertView(convertView);
					content_task.execute(position);
					
					//TaskHelper.execute(content_task);
				}
				else
				{
					viewHolder = (ContentViewHolder)convertView.getTag();
					
					viewHolder.favorite_user_img.setTag(mMainControlBarActivity.mAdListData.get(position).user_name);
					viewHolder.mFavorite.setTag(mMainControlBarActivity.mAdListData.get(position).Id);
					
					FavUsersData fav_user = new FavUsersData();
					fav_user.id = mMainControlBarActivity.mAdListData.get(position).User_id;
					fav_user.name = mMainControlBarActivity.mAdListData.get(position).user_name;
					fav_user.avatar_url = mMainControlBarActivity.mAdListData.get(position).user_image_url;
					viewHolder.mFavorite_user.setTag(fav_user);
					
					viewHolder.comment_text.setTag(position);
					viewHolder.btn_add_comment.setTag(viewHolder.comment_text);
					
					viewHolder.comment_text.setTag(R.string.fav_ad, viewHolder.mFavorite);
					viewHolder.comment_text.setTag(R.string.fav_user, viewHolder.mFavorite_user);
					
					ContentTask task = new ContentTask();
					task.setHolder(viewHolder);
					task.execute(position);
					
					viewHolder.content_text.setText(mMainControlBarActivity.mAdListData.get(position).Desc);
		    		/*String text = "<html><body>"
		    	            + "<p align=\"justify\">"
		    	             + mMainControlBarActivity.mAdListData.get(position).Desc
		    	            + "</p> "
		    	            + "<img src='dsa' >"
		    	             + "</body></html>";

		    		viewHolder.content_text.loadDataWithBaseURL("", text, "text/html", "utf-8", "");*/
				}

	            return convertView;
		}
		
		private class ContentCreateTask extends AsyncTask<Integer, Void, Void>{

			private View convertView;
			private ContentViewHolder viewHolder;
			private int mPos;
			
			public void setConvertView(View view){
        		convertView = view;
        	}
			@Override
			protected Void doInBackground(Integer... pos) {
				// TODO Auto-generated method stub
				mPos = pos[0];
				
				viewHolder = new ContentViewHolder();
				viewHolder.content_text = (TextView)convertView.findViewById(R.id.content_text);

				viewHolder.layout = (RelativeLayout)convertView.findViewById(R.id.layout);
				viewHolder.favorite_img = (ImageView)convertView.findViewById(R.id.favorite_ad_img);
				viewHolder.favorite_user_img = (ImageView)convertView.findViewById(R.id.favorite_user_img);
				
				viewHolder.mFavorite = (LinearLayout)convertView.findViewById(R.id.favorite_ad_btn);
				viewHolder.mFavorite_user = (LinearLayout)convertView.findViewById(R.id.favorite_user_btn);
				viewHolder.mFavorite.setOnClickListener(MyExpandableListItemAdapter.this);
				viewHolder.mFavorite_user.setOnClickListener(MyExpandableListItemAdapter.this);
				viewHolder.comment_text = (EditText)convertView.findViewById(R.id.edit_add_comment);
				
				viewHolder.comment_text.setOnFocusChangeListener(MyExpandableListItemAdapter.this);
				viewHolder.btn_add_comment = (Button)convertView.findViewById(R.id.btn_add_comment);
				viewHolder.btn_add_comment.setOnClickListener(MyExpandableListItemAdapter.this);
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				//viewHolder.webview.setPictureListener(MyExpandableListItemAdapter.this);
				
				viewHolder.mFavorite.setTag(mMainControlBarActivity.mAdListData.get(mPos).Id);
				
				FavUsersData fav_user = new FavUsersData();
				fav_user.id = mMainControlBarActivity.mAdListData.get(mPos).User_id;
				fav_user.name = mMainControlBarActivity.mAdListData.get(mPos).user_name;
				fav_user.avatar_url = mMainControlBarActivity.mAdListData.get(mPos).user_image_url;
				viewHolder.mFavorite_user.setTag(fav_user);
				
				convertView.setId(mMainControlBarActivity.mAdListData.get(mPos).Id);
				convertView.setTag(viewHolder);
				
				viewHolder.comment_text.setTag(mPos);
				viewHolder.btn_add_comment.setTag(viewHolder.comment_text);
				
				viewHolder.comment_text.setTag(R.string.fav_ad, viewHolder.mFavorite);
				viewHolder.comment_text.setTag(R.string.fav_user, viewHolder.mFavorite_user);
				
				ContentTask task = new ContentTask();
				task.setHolder(viewHolder);
				task.execute(mPos);
				
				viewHolder.content_text.setText(mMainControlBarActivity.mAdListData.get(mPos).Desc);
	    		/*String text = "<html><body>"
	    	            + "<p align=\"justify\">"
	    	             + mMainControlBarActivity.mAdListData.get(mPos).Desc
	    	            + "</p> "
	    	            + "<img src='dsa' >"
	    	             + "</body></html>";*/

	    		//viewHolder.webview.loadDataWithBaseURL("", text, "text/html", "utf-8", "");
			}
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
				fav_ad_icon = getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_false);
				fav_user_icon = getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_users_false);
				for(Integer fav_ad : mFavoriteAds)
				{
					if(fav_ad.equals(mMainControlBarActivity.mAdListData.get(pos[0]).Id))
					{
						fav_ad_icon = getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_add);
						break;
					}
				}
				for(FavUsersData fav_user : mFavoriteUsers)
				{
					if(fav_user.id.equals(mMainControlBarActivity.mAdListData.get(pos[0]).User_id))
					{
						fav_user_icon = getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_users_add);
						break;
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				if(fav_ad_icon != null)
					mViewHolder.favorite_img.setImageDrawable(fav_ad_icon);
				if(fav_user_icon != null)
					mViewHolder.favorite_user_img.setImageDrawable(fav_user_icon);
				super.onPostExecute(result);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!mMainControlBarActivity.getUserFunctions().isUserLoggedIn(getActivity())){
				new CustomToast(getActivity()).show(getString(R.string.toast_need_auth));
				mMainControlBarActivity.openMenu();
				return;
			}
			switch (v.getId()) {
			case R.id.favorite_ad_btn:
				Integer fav_ad_id = (Integer)v.getTag();
				ImageView img = (ImageView)v.findViewById(R.id.favorite_ad_img);
				InsertData insertData = new InsertData();
				
				URLWithParams urlWithParams = new URLWithParams();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("user_id", mMainControlBarActivity.mUserData.get(LoginKeys.KEY_ID)));
				params.add(new BasicNameValuePair("ad_id", String.valueOf(v.getTag())));
				
				boolean is_ad_already_exist = false;
				for (Integer fav_ad : mFavoriteAds) {
					if (fav_ad.equals(fav_ad_id)){
						is_ad_already_exist = true;
						break;
					}
				}
				if(!is_ad_already_exist)
				{
					params.add(new BasicNameValuePair("tag", "add_fav_ad"));
					img.setImageResource(R.drawable.icon_ad_favorite_add);
					
					urlWithParams.url = Urls.API + Urls.USER;
					urlWithParams.nameValuePairs = params;
					insertData.execute(urlWithParams);
					
					mFavoriteAds.add((Integer)v.getTag());
					((MainControlBarActivity)getActivity()).mSavedData.saveFavoriteAds(mFavoriteAds);
				}
				else
				{
					params.add(new BasicNameValuePair("tag", "remove_fav_ad"));
					img.setImageResource(R.drawable.icon_ad_favorite_false);
					
					urlWithParams.url = Urls.API + Urls.USER;
					urlWithParams.nameValuePairs = params;
					insertData.execute(urlWithParams);
					for(int i=0; i<mFavoriteAds.size(); i++)
					{
						if(mFavoriteAds.get(i).equals(fav_ad_id))
						{
							mFavoriteAds.remove(i);
							break;
						}
					}
					((MainControlBarActivity)getActivity()).mSavedData.saveFavoriteAds(mFavoriteAds);
				}
				break;
			case R.id.favorite_user_btn:
				
				FavUsersData fav_users = (FavUsersData)v.getTag();
				
				ImageView img2 = (ImageView)v.findViewById(R.id.favorite_user_img);
				InsertData insertData2 = new InsertData();
				
				URLWithParams urlWithParams2 = new URLWithParams();
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("user_id", mMainControlBarActivity.mUserData.get(LoginKeys.KEY_ID)));
				params2.add(new BasicNameValuePair("fav_user_id", String.valueOf(v.getId())));
				
				boolean is_user_already_exist = false;
				for (FavUsersData fav_user : mFavoriteUsers) {
					if (fav_user.id.equals(fav_users.id)){
						is_user_already_exist = true;
						break;
					}
				}
				if(!is_user_already_exist)
				{
					params2.add(new BasicNameValuePair("tag", "add_fav_user"));
					img2.setImageResource(R.drawable.icon_ad_favorite_users_add);
					
					urlWithParams2.url = Urls.API + Urls.USER;
					urlWithParams2.nameValuePairs = params2;
					
					insertData2.execute(urlWithParams2);

					mFavoriteUsers.add(fav_users);
					
					mMainControlBarActivity.getDBUser().addFavUser(fav_users);
				}
				else
				{
					params2.add(new BasicNameValuePair("tag", "remove_fav_user"));
					img2.setImageResource(R.drawable.icon_ad_favorite_users_false);
					
					urlWithParams2.url = Urls.API + Urls.USER;
					urlWithParams2.nameValuePairs = params2;
					
					insertData2.execute(urlWithParams2);
					for(int i=0; i<mFavoriteUsers.size(); i++)
					{
						if(mFavoriteUsers.get(i).id.equals(fav_users.id))
						{
							mFavoriteUsers.remove(i);
							break;
						}
					}
					mMainControlBarActivity.getDBUser().removeFavUser(fav_users.id);
				}
				
				for(int i=mListView.getFirstVisiblePosition(); i<=mListView.getLastVisiblePosition(); i++)
				{
					if(mListView.getChildAt(i) != null)
					{
						LinearLayout fav_btn = (LinearLayout)mListView.getChildAt(i).findViewById(R.id.favorite_user_btn);
						ImageView fav_img = (ImageView)mListView.getChildAt(i).findViewById(R.id.favorite_user_img);
						
						if(fav_btn != null)
						{
							FavUsersData fav_users2 = (FavUsersData)fav_btn.getTag();
							fav_img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_users_false));
							for(int j=0; j<mFavoriteUsers.size(); j++)
							{
								if(mFavoriteUsers.get(j).id.equals(fav_users2.id))
								{
									fav_img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_ad_favorite_users_add));
									break;
								}
							}
						}
					}
				}
				break;
			case R.id.btn_add_comment:
				EditText comment_text = (EditText)v.getTag();
				comment_text.clearFocus();
				Integer pos = (Integer)comment_text.getTag();
				String str = comment_text.getText().toString();
				if(!str.equals(""))
				{
					try {
						mEncodeCommentText = URLEncoder.encode(str,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = mEncodeCommentText;
					comment_text.setText("");
											
					URLWithParams urlWithParams3 = new URLWithParams();
					List<NameValuePair> params3 = new ArrayList<NameValuePair>();
					params3.add(new BasicNameValuePair("tag", AdTags.insert_ad_comment));
					params3.add(new BasicNameValuePair("ad_id", mMainControlBarActivity.mAdListData.get(pos).Id.toString()));
					params3.add(new BasicNameValuePair("user_id", mMainControlBarActivity.mUserData.get(LoginKeys.KEY_ID)));
					params3.add(new BasicNameValuePair("comment_text", mEncodeCommentText));
					params3.add(new BasicNameValuePair("comment_rate", "0"));
					
					urlWithParams3.url = Urls.API + Urls.ADS;
					urlWithParams3.nameValuePairs = params3;
					
					InsertData insert = new InsertData();
					insert.execute(urlWithParams3);
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.edit_add_comment:
				LinearLayout fav_ad = (LinearLayout)v.getTag(R.string.fav_ad);
				LinearLayout fav_user = (LinearLayout)v.getTag(R.string.fav_user);
				if(fav_ad.getVisibility() == View.VISIBLE)
				{
					fav_ad.setVisibility(View.GONE);
					fav_user.setVisibility(View.GONE);
				}
				else
				{
					fav_ad.setVisibility(View.VISIBLE);
					fav_user.setVisibility(View.VISIBLE);
				}
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
			mImageLoaderRounded.mImageFetcherRounded.setPauseWork(true);
        } else {
        	mImageLoaderRounded.mImageFetcherRounded.setPauseWork(false);
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
			mMainControlBarActivity.getAdList(DownloadData.GET_MORE_AD);
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
		mMainControlBarActivity.setSlidingPaneOffsetY(image_slider.getTop());
        if(offset_y < mActionBarHeight*-1)
        {	
        	if(mMainControlBarActivity.isSlidingPaneOpen()){
				mMainControlBarActivity.showFilterStateInfo(false);
				if(!mMainControlBarActivity.getSupportActionBar().isShowing())
					mMainControlBarActivity.getSupportActionBar().show();
			}
        	else if(!mMainControlBarActivity.getSupportActionBar().isShowing()){
				mMainControlBarActivity.showFilterStateInfo(true);
				mMainControlBarActivity.getSupportActionBar().show();
			}
        }
	}

	@Override
	public void onDownScrolling(int offset_y) {
		// TODO Auto-generated method stub
		mMainControlBarActivity.setSlidingPaneOffsetY(image_slider.getTop());
		if(offset_y < mActionBarHeight*-1)
        {	
			if(mMainControlBarActivity.getSupportActionBar().isShowing()){
				mMainControlBarActivity.showFilterStateInfo(false);
				mMainControlBarActivity.getSupportActionBar().hide();
			}
        }
	}

	@Override
	public void getInsertCompleteState() {
		// TODO Auto-generated method stub
		
	}
	
}
