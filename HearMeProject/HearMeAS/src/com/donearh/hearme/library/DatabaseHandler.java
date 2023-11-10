package com.donearh.hearme.library;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.donearh.hearme.datatypes.FavUsersData;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "android_api";
	private static final String TABLE_LOGIN = "user_login_data";
	private static final String TABLE_FAV_USERS = "fav_users";
	
	private String USER_ID = "user_id";
	private String USER_UID = "user_uid";
	private String USER_LOGIN = "user_login";
	private String USER_EMAIL = "user_email";
	private String USER_FULL_NAME = "user_full_name";
	private String USER_IMAGE_URL = "user_image_url";
	private String USER_CREATE_DATE = "user_create_date";
	
	private String FAV_ID = "id";
	private String FAV_NAME = "name";
	private String FAV_AVATAR_URL = "avatar_url";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
				+ USER_ID + " INTEGER PRIMARY KEY,"
				+ USER_UID + " TEXT," 
				+ USER_LOGIN + " TEXT,"
				+ USER_EMAIL + " TEXT UNIQUE,"
				+ USER_FULL_NAME + " TEXT,"
				+ USER_IMAGE_URL + " TEXT,"
				+ USER_CREATE_DATE + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
		
		String CREATE_FAV_USERS_TABLE = "CREATE TABLE " + TABLE_FAV_USERS + "("
				+ FAV_ID + " INTEGER PRIMARY KEY,"
				+ FAV_NAME + " TEXT,"
				+ FAV_AVATAR_URL + " TEXT" + ")";
		db.execSQL(CREATE_FAV_USERS_TABLE);
				
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_USERS);
		onCreate(db);
	}
	
	public void addUser(String id,
			String uid,
			String login,
			String email,
			String full_name,
			String image_url,
			String create_date)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(USER_ID, id);
		values.put(USER_UID, uid);
		values.put(USER_LOGIN, login);
		values.put(USER_EMAIL, email);
		values.put(USER_FULL_NAME, full_name);
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
			user.put("user_uid", cursor.getString(1));
			user.put("user_login", cursor.getString(2));
			user.put("user_email", cursor.getString(3));
			user.put("user_full_name", cursor.getString(4));
			user.put("user_image_url", cursor.getString(5));
			user.put("user_create_date", cursor.getString(6));
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
	}
	
	public void addFavUser(FavUsersData fav_user){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FAV_ID, fav_user.id);
		values.put(FAV_NAME, fav_user.name);
		values.put(FAV_AVATAR_URL, fav_user.avatar_url);
		
		db.insert(TABLE_FAV_USERS, null, values);
		db.close();
	}
	
	public void removeFavUser(Integer id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_FAV_USERS, "id = "+String.valueOf(id), null);
		db.close();
	}
	
	public ArrayList<FavUsersData> getFavUsers(){
		String selectQuery = "SELECT * FROM " + TABLE_FAV_USERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<FavUsersData> fav_users = new ArrayList<FavUsersData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++){
			
			FavUsersData data = new FavUsersData();
			data.id = cursor.getInt(0);
			data.name = cursor.getString(1);
			data.avatar_url = cursor.getString(2);
			fav_users.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		
		return fav_users;
	}
}
