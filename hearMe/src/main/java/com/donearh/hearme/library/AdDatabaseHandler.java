package com.donearh.hearme.library;

import java.util.HashMap;

import org.json.JSONObject;

import com.donearh.hearme.AdKeys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdDatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "android_api";
	private static final String AD_LIST_TABLE = "ad_list";
	
	SQLiteDatabase db;
	
	
	public AdDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_AD_LIST_TABLE = "CREATE TABLE " + AD_LIST_TABLE + "("
				+ AdKeys.AD_ID + " INTEGER PRIMARY KEY,"
				+ AdKeys.AD_AREA + " INTEGER," 
				+ AdKeys.AD_TITLE + " TEXT,"
				+ AdKeys.AD_DESC + " TEXT,"
				+ AdKeys.AD_IMG + " TEXT,"
				+ AdKeys.AD_CATEGORY + " INTEGER,"
				+ AdKeys.USER_ID + " INTEGER,"
				+ AdKeys.AD_ADD_TIME + " TEXT,"
				+ AdKeys.AD_ADD_DATE + " TEXT" + ")";
		db.execSQL(CREATE_AD_LIST_TABLE);
				
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + AD_LIST_TABLE);
		
		onCreate(db);
	}
	
	public void closeDB()
	{
		db.close();
		db = null;
	}
	
	public void addAdList(Integer uid,
						String area,
						String title,
						String desc,
						String image_url,
						String catefory,
						Integer user_id,
						String create_time,
						String create_date)
	{
		db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(AdKeys.AD_ID, uid);
		values.put(AdKeys.AD_AREA, area);
		values.put(AdKeys.AD_TITLE, title);
		values.put(AdKeys.AD_DESC, desc);
		values.put(AdKeys.AD_IMG, image_url);
		values.put(AdKeys.AD_CATEGORY, catefory);
		values.put(AdKeys.USER_ID, user_id);
		values.put(AdKeys.AD_ADD_TIME, create_time);
		values.put(AdKeys.AD_ADD_DATE, create_date);
		
		db.insert(AD_LIST_TABLE, null, values);
		
	}
	
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + AD_LIST_TABLE;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		if(cursor.getCount() > 0)
		{
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
		String countQuery = "SELECT * FROM " + AD_LIST_TABLE;
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
		
		db.delete(AD_LIST_TABLE, null, null);
		db.close();
	}
	

}
