package com.donearh.hearme.library;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "android_api";
	private static final String TABLE_LOGIN = "user_login_data";
	private static final String TABLE_FAV_USERS = "fav_users";
	private static final String TABLE_FAV_ADS = "fav_ads";
	
	private String USER_ID = "user_id";
	private String ACCOUNT_TYPE = "account_type";
	private String SOCIAL_TYPE = "social_type";
	private String USER_UID = "user_uid";
	private String USER_LOGIN = "user_login";
	private String USER_EMAIL = "user_email";
	private String USER_FULL_NAME = "user_full_name";
	private String SHOW_FULL_NAME = "show_full_name";
	private String DESC_TEXT = "desc_text";
	private String USER_IMAGE_URL = "user_image_url";
	private String USER_CREATE_DATE = "user_create_date";
	
	private String FAV_ID = "id";
	private String FAV_NAME = "name";
	private String FAV_AVATAR_URL = "avatar_url";
	
	private String ID = "id";
	private String AD_ID = "ad_id";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
				+ USER_ID + " INTEGER PRIMARY KEY,"
				+ ACCOUNT_TYPE + " INTEGER,"
				+ SOCIAL_TYPE + " INTEGER,"
				+ USER_UID + " TEXT," 
				+ USER_LOGIN + " TEXT,"
				+ USER_EMAIL + " TEXT UNIQUE,"
				+ USER_FULL_NAME + " TEXT,"
				+ SHOW_FULL_NAME + " INTEGER,"
				+ DESC_TEXT + " TEXT,"
				+ USER_IMAGE_URL + " TEXT,"
				+ USER_CREATE_DATE + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
		
		String CREATE_FAV_USERS_TABLE = "CREATE TABLE " + TABLE_FAV_USERS + "("
				+ FAV_ID + " INTEGER PRIMARY KEY,"
				+ FAV_NAME + " TEXT,"
				+ FAV_AVATAR_URL + " TEXT" + ")";
		db.execSQL(CREATE_FAV_USERS_TABLE);
				
		String CREATE_FAV_ADS_TABLE = "CREATE TABLE " + TABLE_FAV_ADS + "("
				+ AD_ID + " INTEGER PRIMARY KEY" + ")";
		db.execSQL(CREATE_FAV_ADS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_ADS);
		onCreate(db);
	}
	
	public void addUser(String id,
			String acc_type,
			String social_type,
			String uid,
			String login,
			String email,
			String full_name,
			String show_full_name,
			String desc_text,
			String image_url,
			String create_date)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(USER_ID, id);
		values.put(ACCOUNT_TYPE, acc_type);
		values.put(SOCIAL_TYPE, social_type);
		values.put(USER_UID, uid);
		values.put(USER_LOGIN, login);
		values.put(USER_EMAIL, email);
		values.put(USER_FULL_NAME, full_name);
		values.put(SHOW_FULL_NAME, show_full_name);
		values.put(DESC_TEXT, desc_text);
		values.put(USER_IMAGE_URL, image_url);
		values.put(USER_CREATE_DATE, create_date);
		
		db.insert(TABLE_LOGIN, null, values);
		db.close();
	}
	
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		if(cursor.getCount() > 0)
		{
			user.put("user_id", cursor.getString(0));
			user.put("account_type", cursor.getString(1));
			user.put("user_uid", cursor.getString(2));
			user.put("user_login", cursor.getString(3));
			user.put("user_email", cursor.getString(4));
			user.put("user_full_name", cursor.getString(5));
			user.put("show_full_name", cursor.getString(6));
			user.put("desc_text", cursor.getString(7));
			user.put("user_image_url", cursor.getString(8));
			user.put("user_create_date", cursor.getString(9));
		}
		cursor.close();
		db.close();
		
		return user;
	}
	
	public int getRowCount()
	{
		String countQuery = "SELECT * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		
		return rowCount;
	}
	
	public void resetTables()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_LOGIN, null, null);
		db.delete(TABLE_FAV_USERS, null, null);
		db.close();
	}
	
	public void updateAvatar(String url){
		SQLiteDatabase db = this.getWritableDatabase();
		
		//String strFilter = "id=" + id;
		
		ContentValues values = new ContentValues();
		values.put(USER_IMAGE_URL, url);
		
		db.update(TABLE_LOGIN, values, null, null);
		db.close();
	}
	
	public void updateNameState(int state){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(SHOW_FULL_NAME, state);
		
		db.update(TABLE_LOGIN, values, null, null);
		db.close();
	}
	
	public void addFavUser(Integer user_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FAV_ID, user_id);
		
		db.insert(TABLE_FAV_USERS, null, values);
		db.close();
	}
	
	public void removeFavUser(Integer id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_FAV_USERS, FAV_ID+" = "+String.valueOf(id), null);
		db.close();
	}
	
	public ArrayList<Integer> getFavUsers(){
		String selectQuery = "SELECT * FROM " + TABLE_FAV_USERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<Integer> fav_users = new ArrayList<Integer>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++){
			fav_users.add(cursor.getInt(0));
			cursor.moveToNext();
		}
		cursor.close();
		
		return fav_users;
	}
	
	public void addFavAd(Integer ad_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(AD_ID, ad_id);
		
		db.insert(TABLE_FAV_ADS, null, values);
		db.close();
	}
	
	public void removeFavAd(Integer ad_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_FAV_ADS, AD_ID+" = "+String.valueOf(ad_id), null);
		db.close();
	}
	
	public ArrayList<Integer> getFavAds(){
		String selectQuery = "SELECT * FROM " + TABLE_FAV_ADS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<Integer> fav_ads = new ArrayList<Integer>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++){
			fav_ads.add(cursor.getInt(0));
			cursor.moveToNext();
		}
		cursor.close();
		
		return fav_ads;
	}
	
	public void logout(){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_FAV_ADS, null, null);
		db.delete(TABLE_FAV_USERS, null, null);
		db.close();
	}
}
