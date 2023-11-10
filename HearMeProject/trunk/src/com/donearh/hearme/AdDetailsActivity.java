package com.donearh.hearme;

import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.donearh.animations.DepthPageTransformer;
import com.donearh.hearme.DownloadData.DownloadDCompleteListener;
import com.donearh.hearme.InsertData.InsertCompleteListener;
import com.donearh.hearme.account.AccountGeneral;
import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.library.JSONParser.GetJSONListener;
import com.donearh.hearme.library.MainDatabaseHandler;
import com.donearh.imageloader.ImageFetcher;
import com.donearh.imageloader.ImageFetcherRounded;
import com.donearh.imageloader.ImageLoader;
import com.donearh.imageloader.ImageLoaderRounded;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;

public class AdDetailsActivity extends AppCompatActivity implements DownloadDCompleteListener,
																	InsertCompleteListener,
																	ConnectionCallbacks,
																	OnConnectionFailedListener,
																	GetJSONListener
{

	
	
	private String mUserId;
	private ArrayList<PlaceholderFragment> mFragArray = new ArrayList<AdDetailsActivity.PlaceholderFragment>();
	private static int mPos;
	private int mPage;
	private int mType;
	
	protected GoogleApiClient mGoogleApiClient;
	protected AccountManager mAccountManager;
	private Integer mAccPos;
	protected HashMap<String, String> mRecallParams = new HashMap<String, String>();
	
	private CollapsingToolbarLayout collapsingToolbar;
	int mutedColor = R.attr.colorPrimary;
	
	public static ArrayList<CategoryData> mCategoryData;
	public static ArrayList<AdListData> mAdListData;
	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	
	private boolean mNoConn = false;
	private Button mBtnRepeat; 
	private Animation mAnimFromRight;
	
	private static ShareActionProvider mShareActionProvider;
	
	private ImageLoader mImageLoader;
	private ImageLoaderRounded mImageLoaderRounded;
	private static ImageFetcher mImageFetcher;
	private static ImageFetcherRounded mImageFetcherRounded;
	private EditText mCommentEdit;
	private ImageButton mCommentBtn;
	private ProgressBar mCommentAddProgress;
	
	private Integer mAdCountInOnePage;
	public long mSelectedAreaId = -1;
	private String mMainSearchText;
	public ArrayList<Integer> mSelectedAdsId;
	public ArrayList<Integer> mSelectedUsersId;
	public ArrayList<Integer> mCatIDsList;
	private ArrayList<String> mFilterArray;
	private ArrayList<Integer> mOutCatIDsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("hm", "start "+getClass().getName());
		setContentView(R.layout.activity_ad_details);
		
		final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        PlusOptions plusOptions = new PlusOptions.Builder().addActivityTypes(MomentUtil.ACTIONS).build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
					        .addApi(Plus.API, plusOptions)
					        .addScope(new Scope(Scopes.PROFILE))
					        .addConnectionCallbacks(this)
					        .addOnConnectionFailedListener(this)
					        .build();
        
        mAccountManager = AccountManager.get(AdDetailsActivity.this);
        
        mAccPos = getIntent().getExtras().getInt("acc_pos");
		
		mImageLoader = new ImageLoader(AdDetailsActivity.this, ImageLoader.DIR_AD_DETAILS);
		mImageLoaderRounded = new ImageLoaderRounded(AdDetailsActivity.this, ImageLoaderRounded.DIR_USER_AVATAR);
		mImageFetcher = mImageLoader.mImageFetcher;
		mImageFetcherRounded = mImageLoaderRounded.mImageFetcherRounded;
		
        mUserId = getIntent().getStringExtra("user_id");
		mType = getIntent().getIntExtra("type", -1);
		mPos = getIntent().getIntExtra("ad_pos", -1);
		mPage = getIntent().getIntExtra("page", -1);
		mAdCountInOnePage = getIntent().getIntExtra("item_count", -1);
		
		MainDatabaseHandler db = new MainDatabaseHandler(getApplicationContext());
		mCategoryData = db.getCategoryData();
		mAdListData = (ArrayList<AdListData>)getIntent().getSerializableExtra("ad_data");
		
		mMainSearchText = getIntent().getStringExtra("mMainSearchText");
		mSelectedAreaId = getIntent().getLongExtra("mSelectedAreaId", -1);
		mSelectedAdsId = getIntent().getIntegerArrayListExtra("mSelectedAdsId");
		mSelectedUsersId = getIntent().getIntegerArrayListExtra("mSelectedUsers");
		mCatIDsList = getIntent().getIntegerArrayListExtra("mCatIDsList");
		mFilterArray = getIntent().getStringArrayListExtra("mFilterArray");
		mOutCatIDsList = getIntent().getIntegerArrayListExtra("mOutCatIDsList");
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(),
															getSupportFragmentManager());
		
		mAnimFromRight = AnimationUtils.loadAnimation(AdDetailsActivity.this, R.anim.move_from_right);
		mBtnRepeat = (Button)findViewById(R.id.btn_repeat);
		mBtnRepeat.setVisibility(View.GONE);
		mBtnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNoConn = false;
				mBtnRepeat.clearAnimation();
				mBtnRepeat.setVisibility(View.GONE);
				getAdList(DownloadData.GET_MORE_AD_DETAILS);
			}
		});
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setCurrentItem(mPos);
		
		mCommentEdit = (EditText)findViewById(R.id.comment_edit);
		mCommentBtn = (ImageButton)findViewById(R.id.comment_btn);
		mCommentBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			    
				if(mAccPos == -1 && !mGoogleApiClient.isConnected()){
					new CustomToast(AdDetailsActivity.this).show(getString(R.string.toast_need_auth));
					return;
				}
				
				mCommentAddProgress.setVisibility(View.VISIBLE);
				mCommentBtn.setVisibility(View.GONE);
				
				new AccessTokenActionsTask().execute(v);
			}
		});
		
		mCommentAddProgress = (ProgressBar)findViewById(R.id.comment_add_progress);

	}
	
	private class AccessTokenActionsTask extends AsyncTask<View, Void, String> {

		private String social_type;
		protected View view;
		
	    @Override
	    protected String doInBackground(View... params) {
	    	view = params[0];
	    	Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
	    	social_type = mAccountManager.getUserData(acc[mAccPos], AccountGeneral.KEY_SOCIAL_TYPE);
	    	if(social_type.equals("1")){
		        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
		        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		        String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";//"audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
		        try {
		            return GoogleAuthUtil.getToken(AdDetailsActivity.this, account, "oauth2:"+Scopes.PROFILE);
		        } catch (IOException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        } catch (GoogleAuthException e) {
		            Log.e("hm", "Error retrieving ID token.", e);
		            return null;
		        }
	    	}else if(social_type.equals("0")){
	    		AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, AdDetailsActivity.this, null, null);
	    		String access_token = null;
				 try {
	                    Bundle bnd = future.getResult();

	                    access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	                    if(access_token != null){
	   					 	boolean is_access = ServerAuth.checkAccess(access_token);
	   	                    if(!is_access){
	   	                    	mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, access_token);
	   	                    	future = mAccountManager.getAuthToken(acc[mAccPos], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, AdDetailsActivity.this, null, null);
	   	                    	bnd = future.getResult();
	   	                    	access_token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
	   	                    }
	   				 	}
	                } catch (Exception e) {
	                	return "no_conn";
	                }
				 
				return access_token;
	    	}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(String access_token) {
	    	if(access_token.equals("no_conn")){
	    		new CustomToast(AdDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
	    		return;
	    	}
	        if (access_token != null) {
	        	String url;
	        	if(social_type.equals("1"))
	        		url = Urls.G_USER;
	        	else
	        		url = Urls.USER;
	        	clickAction(view, url, access_token);
	        	
	        } else {
	        	Intent intent = new Intent();
				intent.putExtra("error", "expired_token");
				return;
	        }
	    }

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mImageLoader.destroy();
		mImageLoader = null;
		mFragArray.clear();
		mFragArray = null;
		
		mImageLoaderRounded.destroy();
		mImageFetcherRounded= null;
		mSectionsPagerAdapter = null;
		super.onDestroy();
	}

	@Override
	protected void onStart() {
	    super.onStart();

	    // Connect to Drive and Google+
	    mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
	    super.onStop();
	    if (mGoogleApiClient.isConnected()) {
	        mGoogleApiClient.disconnect();
	      }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mImageLoader.onPause();
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mImageLoader.onResume();
		super.onResume();
	}

	public void setMoreData(ArrayList<AdListData> data)
	{
		for(int i=0; i<data.size(); i++)
		{
			mAdListData.add(data.get(i));
		}
		
		mSectionsPagerAdapter.notifyDataSetChanged();
		//mContent.setVisibility(View.VISIBLE);
	}

	public void updateAdapter()
	{
		PlaceholderFragment fragment = mSectionsPagerAdapter.getCurrentFragment();
		fragment.loadData();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ad_details, menu);
		
		MenuItem share_item = menu.findItem(R.id.share_ad);
		mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(share_item);
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String title = "Объявление: " + mAdListData.get(mPos).Title;
		String desc = mAdListData.get(mPos).Desc + " - отправлено из приложения HearMe присоединяйся - http://play.google.com";
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, desc);
		mShareActionProvider.setShareIntent(shareIntent);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.share_ad) {
			
			//shareIntent.setType("image/*");
			//Uri uri = Uri.fromFile(new File(getFilesDir(), "foo.jpg"));
			//shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	        startActivity(Intent.createChooser(shareIntent, "Share via"));
	    }
	}

	
	public void getAdList(int secondtype){
		URLWithParams urlWithParams = new URLWithParams();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Integer start = 0;
		start = mPage * mAdCountInOnePage - mAdCountInOnePage;
		
		params.add(new BasicNameValuePair("tag", AdTags.get_ad_list_tag));
		params.add(new BasicNameValuePair("area_id", String.valueOf(mSelectedAreaId)));
		params.add(new BasicNameValuePair("start_page", start.toString()));
		params.add(new BasicNameValuePair("item_count", mAdCountInOnePage.toString()));
		
		for (Integer ad_id : mSelectedAdsId) {
			params.add(new BasicNameValuePair("ad_id_array[]", ad_id.toString()));
		}
		for (Integer user_id : mSelectedUsersId) {
			params.add(new BasicNameValuePair("users[]", user_id.toString()));
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
		urlWithParams.url = Urls.ADS;
		urlWithParams.nameValuePairs = params;
		
		DownloadData dd = new DownloadData(secondtype, this);
		dd.setParams(urlWithParams);
		dd.setListener(this);
		dd.execute();
	}
	
	private void clickAction(View v, String url, String access_token){
		String str = mCommentEdit.getText().toString().trim();
		String encode_Str="";
		if(str.length() == 0){
			new CustomToast(AdDetailsActivity.this).show(getString(R.string.hint_text));
			return;
		}
		if(!str.equals(""))
		{
			try {
				encode_Str = URLEncoder.encode(str,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCommentEdit.setText("");
			int pos =mSectionsPagerAdapter.getCurrentPos();
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("tag", AdTags.insert_ad_comment);
			params.put("ad_id", mAdListData.get(pos).Id.toString());
			params.put("access_token", access_token);
			params.put("comment_text", encode_Str);
			params.put("comment_rate", "0");
			
			params.put("url", url);
			mRecallParams.putAll(params);
			JSONParser parser = new JSONParser(AdDetailsActivity.this, null);
			parser.execute(params);
		}
	}
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private Context mContext;
		private PlaceholderFragment mCurrentFragment;
		public SectionsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			mContext = context;
		}

		public PlaceholderFragment getCurrentFragment() {
			if(mCurrentFragment != null && mNoConn)
				if(mAdListData.size()-1 == mCurrentFragment.mSecPos){
					if(mBtnRepeat.getVisibility() == View.GONE){
						mBtnRepeat.clearAnimation();
						mBtnRepeat.setVisibility(View.VISIBLE);
					}
				}
				else{
					if(mBtnRepeat.getVisibility() == View.VISIBLE){
						mBtnRepeat.clearAnimation();
						mBtnRepeat.setVisibility(View.GONE);
					}
				}
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
			Integer size = mAdListData.size();
			Integer check = position+1;
			if(size.equals(check))
			{
				mPage += 1;
				//mMainActivity.selectItem(DrawerListAdapter.AD_ALL, DownloadData.GET_MORE_AD_DETAILS);
				getAdList(DownloadData.GET_MORE_AD_DETAILS);
				
				return PlaceholderFragment.newInstance(mContext, mAdListData.size()-1);
			}
			else
			{
				return PlaceholderFragment.newInstance(mContext, position);
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return mAdListData.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			
			return "text";//mAdListData.get(position).Title;
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
		private static Context mContext;
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		private View mTopView;
		private View mBottomLoadCircle;
		
		public ListView mCommentsListView;
		public int mSecPos;
		
		public CommentsAdapter mCommentsAdapter;
		
		private class CatList{
			Integer id;
			String name;
		}
		private ArrayList<CatList> mCatNameList = new ArrayList<CatList>();
		
		private ArrayList<CommentData> mCommentData = new ArrayList<CommentData>();

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(Context context, int sectionNumber) {
			mContext = context;
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_ad_details,
					container, false);
			
			mTopView = LayoutInflater.from(getActivity()).inflate(R.layout.ad_info, null);
			mBottomLoadCircle = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_load_circle, null);
			mSecPos = getArguments().getInt(ARG_SECTION_NUMBER);
			mCommentsListView = (ListView)rootView.findViewById(R.id.comments_listview);
			mCommentsListView.addHeaderView(mTopView);
			mCommentsListView.addFooterView(mBottomLoadCircle);
			mCatNameList.clear();
			CatList list = new CatList();
			list.id = mAdListData.get(mSecPos).Category_id;
			list.name = mAdListData.get(mSecPos).Category_name;
			mCatNameList.add(list);
			if(!mAdListData.get(mSecPos).Parent_cat_id.equals(0)){
				for(int i=0; i<mCategoryData.size(); i++)
				{
					if(mCategoryData.get(i).Id.equals(mAdListData.get(mSecPos).Parent_cat_id))
					{
						createCatList(mCategoryData.get(i).Id);
						break;
					}
				}
			}
			LinearLayout cat_scroller = (LinearLayout)mTopView.findViewById(R.id.cat_scroller);
			cat_scroller.removeAllViews();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 0, 10, 0);
			for(int i=mCatNameList.size()-1; i>-1; i--)
			{
				Button cat_button = new Button(getActivity());
				cat_button.setTextSize(10);
				cat_button.setPadding(10, 10, 10, 10);
				cat_button.setLayoutParams(params);
				cat_button.setBackgroundResource(R.drawable.cat_gradient);
				cat_button.setTextColor(Color.WHITE);
				cat_button.setText(mCatNameList.get(i).name);
				cat_button.setTag(mCatNameList.get(i).id);
				cat_button.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Integer id = (Integer)v.getTag();
						for(int i=0; i<mCategoryData.size(); i++){
							if(mCategoryData.get(i).Id.equals(id)){
								Intent intent = new Intent();
								intent.putExtra("cat_pos", i);
								intent.putExtra("cat_id", id);
								getActivity().setResult(RESULT_OK, intent);
								getActivity().finish();
								break;
							}
						}
					}
				});
				cat_scroller.addView(cat_button);
			} 
			////////////TOP AD INFO
			LinearLayout adTitleBack = (LinearLayout)mTopView.findViewById(R.id.ad_title_back);
			if(mAdListData.get(mSecPos).cat_left_color != null)
				if(mAdListData.get(mSecPos).cat_left_color.length() != 0)
				{
					GradientDrawable g = new GradientDrawable(Orientation.TL_BR, new int[] { Color.parseColor(mAdListData.get(mSecPos).cat_left_color), 
																							Color.parseColor(mAdListData.get(mSecPos).cat_right_color)});
					g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
					g.setGradientCenter(0.0f, 0.5f);
					Resources r = getResources();
					float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
					g.setCornerRadius(px);
					if(Build.VERSION.SDK_INT <= 15)
						adTitleBack.setBackgroundDrawable(g);
					else
					{
						adTitleBack.setBackground(g);
					}
				}
			TextView adTitle = (TextView)mTopView.findViewById(R.id.ad_title);
			adTitle.setText(mAdListData.get(mSecPos).Title);
			
			ImageView user_image = (ImageView)mTopView.findViewById(R.id.user_img);
			
			TextView ad_text = (TextView)mTopView.findViewById(R.id.ad_text);
			ad_text.setText(mAdListData.get(mSecPos).Desc);
			
			View divider = (View)mTopView.findViewById(R.id.divider);
	    	 
			if(mAdListData.get(mSecPos).files_urls != null && mAdListData.get(mSecPos).files_urls.size() !=0){
				divider.setVisibility(View.VISIBLE);
		    	FileImageScroller imageList = (FileImageScroller)mTopView.findViewById(R.id.image_scroller);
		    	FilesAdapter adapter = new FilesAdapter();
		    	imageList.setAdapter(getActivity(), adapter);
			}
	    	 
	    	 
	    	Button email_btn = (Button)mTopView.findViewById(R.id.email_btn);
	    	Button call_btn = (Button)mTopView.findViewById(R.id.call_btn);
	    	Button share_btn = (Button)mTopView.findViewById(R.id.share_btn);
	    	 
	    	email_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL  , mAdListData.get(mSecPos).email);
					i.putExtra(Intent.EXTRA_SUBJECT, "Тема письма");
					i.putExtra(Intent.EXTRA_TEXT   , "Основной текст");
					try {
					    startActivity(Intent.createChooser(i, "Выберите приложение для отправки письма..."));
					} catch (android.content.ActivityNotFoundException ex) {
					    new CustomToast(getActivity()).showErrorToast(getString(R.string.error_not_found_email_app));
					}
				}
			});
	    	 
	    	call_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:+7"+mAdListData.get(mSecPos).phone_num));
					try {
						startActivity(intent);
					}catch(Exception e){
						new CustomToast(getActivity()).showErrorToast(getString(R.string.error_cannot_call));
					}
				}
			});
	    	
	    	mImageFetcherRounded.loadImage(mAdListData.get(mSecPos).user_image_url, user_image);
	    	 if(mAdListData.get(mSecPos).phone_num != null){
		    	 if(mAdListData.get(mSecPos).phone_num.length() == 0 || mAdListData.get(mSecPos).phone_num.equals("null"))
		    		 call_btn.setVisibility(View.GONE);
		    	 else
		    		 call_btn.setText("Позвонить: +7" +mAdListData.get(mSecPos).phone_num);
	    	 }else{
	    		 call_btn.setVisibility(View.GONE);
	    	 }
		     
	    	 
	    	 if(mAdListData.get(mSecPos).email != null){
	    		 if(mAdListData.get(mSecPos).email.length() == 0 || mAdListData.get(mSecPos).email.equals("null"))
	    			 email_btn.setVisibility(View.GONE);
	    		 else
	    			 email_btn.setText("Написать: " +mAdListData.get(mSecPos).email);
	    	 }else{
	    		 email_btn.setVisibility(View.GONE);
	    	 }
	    	 
	    	 share_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					String title = "Объявление: " + mAdListData.get(mPos).Title;
					String desc = mAdListData.get(mPos).Desc + " - отправлено из приложения HearMe присоединяйся - http://play.google.com";
					shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, desc);
					mShareActionProvider.setShareIntent(shareIntent);
					startActivity(Intent.createChooser(shareIntent, "Выберите назначение"));
				}
			});
	    	 
	    	 
	    	 if(mCommentsAdapter == null)
	    		 mCommentsAdapter = new CommentsAdapter(getActivity());
	    	 
	    	 mCommentData.clear();
	    	 mCommentsListView.setAdapter(mCommentsAdapter);
			
			loadData();
		    	 
			return rootView;
		}
		
		public void loadData()
		{
			URLWithParams urlWithParams = new URLWithParams();
			urlWithParams.url = Urls.ADS;
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", AdTags.get_ad_comments));
			params.add(new BasicNameValuePair("ad_id", mAdListData.get(mSecPos).Id.toString()));
			urlWithParams.nameValuePairs = params;
			
			DownloadComments dd = new DownloadComments();
			dd.execute(urlWithParams);
		}
		
		private void createCatList(Integer id){
			for(int i=0; i<mCategoryData.size(); i++)
			{
				if(mCategoryData.get(i).Id.equals(id))
				{
					CatList list = new CatList();
					list.id = mCategoryData.get(i).Id;
					list.name = mCategoryData.get(i).Name;
					mCatNameList.add(list);
					if(!mCategoryData.get(i).ParentId.equals(0))
					{
						createCatList(mCategoryData.get(i).ParentId);
					}
					break;
				}
			}
		}
		
		public class FilesAdapter extends BaseAdapter
		{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mAdListData.get(mSecPos).files_urls.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				ImageView imageView = null;
				if(convertView == null)
				{
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_hor_slider, null);
					imageView = (ImageView)convertView.findViewById(R.id.image);
					imageView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getActivity(), AdImagesActivity.class);
							intent.putStringArrayListExtra("image_urls", mAdListData.get(mSecPos).files_urls);
							intent.putExtra("image_pos", (Integer)v.getTag());
							getActivity().startActivity(intent);
						}
					});
					
					convertView.setTag(imageView);
				}
				else
				{
					imageView = (ImageView)convertView.getTag();
				}
				imageView.setTag(position);
				mImageFetcher.loadImage(mAdListData.get(mSecPos).files_urls.get(position), imageView);
				return convertView;
			}
			
		}
		
		private class CommentsAdapter extends BaseAdapter
		{
			private Context mContext;
			CharSequence dt = "";
			
			public CommentsAdapter(Context context) {
	            super();
	            mContext = context;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mCommentData.size();
			}
			
			private class ViewHolder
			{
				ImageView user_avatar;
				TextView user_name;
				TextView comment_text;
				TextView add_date;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder;
				if(convertView == null)
				{
					viewHolder = new ViewHolder();
					convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_comments, null);
					
					viewHolder.user_avatar = (ImageView)convertView.findViewById(R.id.user_avatar);
					viewHolder.user_name = (TextView)convertView.findViewById(R.id.user_name);
					viewHolder.comment_text = (TextView)convertView.findViewById(R.id.comment_text);
					viewHolder.add_date = (TextView)convertView.findViewById(R.id.add_date);
					
					convertView.setTag(viewHolder);
				}
				else
				{
					viewHolder = (ViewHolder)convertView.getTag();
				}
				
				mImageFetcherRounded.loadImage(mCommentData.get(position).user_image_url, viewHolder.user_avatar);
				
				viewHolder.user_name.setText(mCommentData.get(position).user_name);
				viewHolder.comment_text.setText(mCommentData.get(position).text);

				dt = DateUtils.getRelativeDateTimeString(getActivity(), 
						mCommentData.get(position).add_datetime, 
	    				DateUtils.MINUTE_IN_MILLIS, 
	    				DateUtils.WEEK_IN_MILLIS, 
	    				DateUtils.FORMAT_ABBREV_ALL);
				
				viewHolder.add_date.setText(dt);
				
				return convertView;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
		}
		
		private class DownloadComments extends AsyncTask<URLWithParams, Void, Object>
		{

			private JSONArray jArray;
			private String result = null;
			private InputStream is = null;
			private StringBuilder sb = null;
			
			@Override
			protected Object doInBackground(URLWithParams... params) {
				// TODO Auto-generated method stub
				//http post
				try
				{
				     HttpClient httpclient = new DefaultHttpClient();
				     String url = params[0].url;
				     HttpPost httppost = new HttpPost(url);
				     httppost.setEntity(new UrlEncodedFormEntity(params[0].nameValuePairs));
				     HttpResponse response = httpclient.execute(httppost);
				     HttpEntity entity = response.getEntity();
				     is = entity.getContent();
				}
				catch(Exception e)
				{
				   Log.e("doni_error", "Error in http connection"+e.toString());
				   return "no_internet";
				}
				
				//convert response to string
				try
				{
				  BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8));
				  sb = new StringBuilder();
				  sb.append(reader.readLine() + "\n");

				  String line="0";
				  while ((line = reader.readLine()) != null) 
				  {
				      sb.append(line + "\n");
				  }
				  is.close();
				  result=sb.toString();
				}
				catch(Exception e)
				{
					
				   Log.e("doni_error", "Error converting result "+e.toString());
				   return "sql_error";
				}
				
				JSONObject json = null;
				try {
					json = new JSONObject(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("JSON Parser", "Error parsing data " + e.toString());
					Log.e("Error pars", result);
					return "sql_error";
				}
				
				try
				{
					int success = json.getInt("success");
					
					if (success == 1) 
		            {
						mCommentData.clear();
				      JSONObject json_data=null;
				      jArray = json.getJSONArray("comment_data");
				      for(int i=0;i<jArray.length();i++)
				      {
				    	  json_data = jArray.getJSONObject(i);
				    	  
				    	  CommentData data = new CommentData();
				    	  data.id = json_data.getInt("comment_id");
				    	  data.ad_id = json_data.getInt("ad_id");
				    	  data.user_id = json_data.getInt("user_id");
				    	  
				    	  data.text = json_data.getString("comment_text");
				    	  
				    	  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
	  		    		  Date mDate = null;
						  try {
								mDate = sdf.parse(json_data.getString("comment_time")
												+" "+json_data.getString("comment_date"));
						  } catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						  }
						  
						  data.add_datetime = mDate.getTime();
				    	  data.rate = json_data.getInt("comment_rate");
				    	  mCommentData.add(data);
				      }
				      jArray = json.getJSONArray("users_list");
				      for (CommentData data : mCommentData) {
				    	  for(int i=0;i<jArray.length();i++){
				    		  json_data = jArray.getJSONObject(i);
				    		  if(data.user_id != null && data.user_id.equals(json_data.getInt("id"))){
				  		    		if(!json_data.isNull("display_name"))
				  		    			data.user_name = json_data.getString("display_name");
				  		    		if(!json_data.isNull(AdKeys.AD_IMG))
				  		    			data.user_image_url = json_data.getString(AdKeys.AD_IMG);
				  		    		break;
				    		  }
				    	  }
				      }
		            }
				      
				}
				catch(JSONException e1)
				{
				    return "sql_error";
				} 
				catch (ParseException e1) 
				{
							e1.printStackTrace();
				}
				jArray = null;
				result = null;
				is = null;
				sb=null;
				return "success";
			}

			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				if(result == "no_internet")
		    	{
					mCommentsListView.removeFooterView(mBottomLoadCircle);
		    		mCommentsAdapter.notifyDataSetChanged();
		    	}
		    	if(result == "sql_error")
		    	{
		    		mCommentsListView.removeFooterView(mBottomLoadCircle);
		    		mCommentsAdapter.notifyDataSetChanged();
		    	}
		    	else if(result == "success")
		    	{
		    		mCommentsListView.removeFooterView(mBottomLoadCircle);
		    		mCommentsAdapter.notifyDataSetChanged();
		    	}
			}
			
		}
	}

	@Override
	public void getDownloadDCompleteState(int loadType,
			ArrayList<? extends Object> ad_list) {
		ArrayList<String> checkConn = (ArrayList<String>)ad_list;
		if(checkConn.size() != 0){
			
			if(checkConn.get(0) == "no_connection"){
				mNoConn = true;
				new CustomToast(AdDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
				mBtnRepeat.setVisibility(View.VISIBLE);
				mBtnRepeat.startAnimation(mAnimFromRight);
				return;
			}
			else if(checkConn.get(0) == "error"){
				mNoConn = true;
				new CustomToast(AdDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
				mBtnRepeat.setVisibility(View.VISIBLE);
				mBtnRepeat.startAnimation(mAnimFromRight);
				return;
			}
		}
		
		ArrayList<AdListData> data_ad = (ArrayList<AdListData>)ad_list;
		setMoreData(data_ad);
	}

	@Override
	public void getInsertCompleteState(Object result) {
		// TODO Auto-generated method stub
		mSectionsPagerAdapter.getCurrentFragment().loadData();
	}



	@Override
	public void onRemoteCallComplete(JSONObject obj, View v) {
		// TODO Auto-generated method stub
		mCommentAddProgress.setVisibility(View.GONE);
		mCommentBtn.setVisibility(View.VISIBLE);
		try {
			if(!obj.isNull("error")){
				
				if(!obj.getString("error").equals("no_conn"))
					new CustomToast(AdDetailsActivity.this).showErrorToast(getString(R.string.no_connection));
				else if(!obj.getString("error").equals("error_parse"))
					new CustomToast(AdDetailsActivity.this).showErrorToast(getString(R.string.error_load_data));
				else if(obj.getString("error").equals("expired_token")){
					/*new AsyncTask<Void, Void, HashMap<String, String>>() {

						@Override
						protected HashMap<String, String> doInBackground(Void... params) {
							// TODO Auto-generated method stub
							return Oauth2Util.getAuthToken(mTokenData.get(AccountGeneral.KEY_REFRESH_TOKEN));
						}

						@Override
						protected void onPostExecute(HashMap<String, String> result) {
							// TODO Auto-generated method stub
							if(result.get("error") != null && result.get("error").equals("expired_token")){
								mRecallParams.clear();
								Intent intent = new Intent();
								intent.putExtra("error", "expired_token");
								setResult(MainControlBarActivity.EXPIRED_REFRESH_TOKEN, intent);
								return;
							}
							JSONParser parser = new JSONParser(AdDetailsActivity.this, null);	
							parser.execute(mRecallParams);
							
							super.onPostExecute(result);
						}
					}.execute();*/
				}
			}
			else if(!obj.isNull("success") && obj.getBoolean("success") == true){
				if(obj.getString("tag").equals(AdTags.insert_ad_comment))
					mSectionsPagerAdapter.getCurrentFragment().loadData();
			}
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mRecallParams.clear();
	}



	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

}
