package com.donearh.hearme.library;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.donearh.hearme.CategoryData;
import com.donearh.hearme.datatypes.AreaData;
import com.donearh.hearme.datatypes.LowerBarData;
import com.donearh.hearme.datatypes.UpdateCheckerData;
import com.donearh.hearme.keys.MainDataKeys;

public class MainDatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "main_data";
	private static final String UPDATE_CHECKER_TABLE = "data_update_checker";
	private static final String AREA_TABLE = "area_table";
	private static final String CATEGORY_TABLE = "category_table";
	private static final String LOWER_BAR_TABLE = "lower_bar_table";
	
	SQLiteDatabase db;
	
	
	public MainDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_UPDATE_CHECKER_TABLE = "CREATE TABLE " + UPDATE_CHECKER_TABLE + "("
				+ MainDataKeys.UPDATE_DATA_ID + " INTEGER PRIMARY KEY,"
				+ MainDataKeys.UPDATE_DATA_NAME + " TEXT,"
				+ MainDataKeys.UPDATE_DATA_DATETIME + " DATETIME,"
				+ MainDataKeys.UPDATE_INFO_TEXT + " TEXT,"
				+ MainDataKeys.UPDATE_STATE + " TEXT"
				+ ")";
		db.execSQL(CREATE_UPDATE_CHECKER_TABLE);
		
		String CREATE_AREA_TABLE = "CREATE TABLE " + AREA_TABLE + "("
				+ MainDataKeys.AREA_ID + " INTEGER PRIMARY KEY,"
				+ MainDataKeys.AREA_NAME + " TEXT" + ")";
		db.execSQL(CREATE_AREA_TABLE);
				
		String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CATEGORY_TABLE + "("
				+ MainDataKeys.CATEGORY_ID + " INTEGER PRIMARY KEY," 
				+ MainDataKeys.CATEGORY_NAME + " TEXT,"
				+ MainDataKeys.CATEGORY_PARENT_ID + " INTEGER,"
				+ MainDataKeys.CATEGORY_COLOR_ID + " INTEGER,"
				+ MainDataKeys.CATEGORY_HAS_CHILD + " INTEGER,"
				+ MainDataKeys.CATEGORY_ICON + " TEXT" + ")";
		db.execSQL(CREATE_CATEGORY_TABLE);
		
		String CREATE_LOWER_BAR_TABLE ="CREATE TABLE " + LOWER_BAR_TABLE + "("
				+ MainDataKeys.MENU_TYPE + " INTEGER PRIMARY KEY,"
				+ MainDataKeys.ICON_RES + " INTEGER,"
				+ MainDataKeys.SORT_FIELD + " INTEGER"+ ")";
		db.execSQL(CREATE_LOWER_BAR_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + UPDATE_CHECKER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + AREA_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOWER_BAR_TABLE);
		onCreate(db);
	}
	
	public void closeDB()
	{
		if(db != null)
		{
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
			db = null;
		}
	}
	
	public void getWriteDatabase()
	{
		db = this.getWritableDatabase();
		db.beginTransaction();
	}
	
	public void addUpdateCheckerData(UpdateCheckerData data){
		
		ContentValues values = new ContentValues();
		values.put(MainDataKeys.UPDATE_DATA_ID, data.id);
		values.put(MainDataKeys.UPDATE_DATA_NAME, data.name);
		values.put(MainDataKeys.UPDATE_DATA_DATETIME, data.update_datetime);
		values.put(MainDataKeys.UPDATE_INFO_TEXT, data.info_text);
		values.put(MainDataKeys.UPDATE_STATE, data.state);
		db.insert(UPDATE_CHECKER_TABLE, null, values);
		
	}
	public void addAreaData(Integer area_id, String area_name)
	{
		ContentValues values = new ContentValues();
		values.put(MainDataKeys.AREA_ID, area_id);
		values.put(MainDataKeys.AREA_NAME, area_name);
		db.insert(AREA_TABLE, null, values);
	}
	
	public void addCategoryData(ArrayList<CategoryData> array_data)
	{
		for(CategoryData data : array_data){
			ContentValues values = new ContentValues();
			values.put(MainDataKeys.CATEGORY_ID, data.Id);
			values.put(MainDataKeys.CATEGORY_NAME, data.Name);
			values.put(MainDataKeys.CATEGORY_PARENT_ID, data.ParentId);
			values.put(MainDataKeys.CATEGORY_COLOR_ID, data.color_id);
			values.put(MainDataKeys.CATEGORY_HAS_CHILD, data.hasChild);
			values.put(MainDataKeys.CATEGORY_ICON, data.icon_img);
			db.insert(CATEGORY_TABLE, null, values);
		}
	}
	
	public void addLowerBarData(ArrayList<LowerBarData> data){
		db = this.getWritableDatabase();
		for(int i=0; i<data.size(); i++){
			ContentValues values = new ContentValues();
			values.put(MainDataKeys.MENU_TYPE, data.get(i).menu_type);
			values.put(MainDataKeys.ICON_RES, data.get(i).icon_res);
			values.put(MainDataKeys.SORT_FIELD, i);
			db.insert(LOWER_BAR_TABLE, null, values);
		}
		db.close();
	}
	
	public int getUpdateCheckerRowCount(){
		String countQuery = "SELECT * FROM " + UPDATE_CHECKER_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		cursor.close();
		
		return rowCount;
	}
	public int getAreaRowCount()
	{
		String countQuery = "SELECT * FROM " + AREA_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		
		return rowCount;
	}
	
	public int getCategoryRowCount()
	{
		String countQuery = "SELECT * FROM " + CATEGORY_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		
		return rowCount;
	}
	
	public ArrayList<UpdateCheckerData> getUpdateCheckerData(){
		
		String selectQuery = "SELECT * FROM " + UPDATE_CHECKER_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<UpdateCheckerData> updateData = new ArrayList<UpdateCheckerData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++){
			
			UpdateCheckerData data = new UpdateCheckerData();
			data.id = cursor.getInt(0);
			data.name = cursor.getString(1);
			data.update_datetime = cursor.getString(2);
			data.info_text = cursor.getString(3);
			data.state = cursor.getString(4);
			updateData.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		
		return updateData;
	}
	public ArrayList<AreaData> getAreaData()
	{
		String selectQuery = "SELECT * FROM " + AREA_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<AreaData> areaData = new ArrayList<AreaData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++)
		{
			AreaData data = new AreaData();
			data.id = cursor.getInt(0);
			data.name = cursor.getString(1);
			areaData.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		return areaData;
	}
	
	public ArrayList<CategoryData> getCategoryData()
	{
		String selectQuery = "SELECT * FROM " + CATEGORY_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++)
		{
			CategoryData data = new CategoryData();
			data.Id = cursor.getInt(0);
			data.Name = cursor.getString(1);
			data.ParentId = cursor.getInt(2);
			data.color_id = cursor.getInt(3);
			data.hasChild = cursor.getInt(4);
			data.icon_img = cursor.getString(5);
			categoryData.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		return categoryData;
	}
	
	public ArrayList<LowerBarData> getLowerBarData(){
		
		String selectQuery = "SELECT * FROM " + LOWER_BAR_TABLE + " ORDER BY "+ MainDataKeys.SORT_FIELD;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<LowerBarData> lowerBarData = new ArrayList<LowerBarData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++)
		{
			LowerBarData data = new LowerBarData();
			data.menu_type = cursor.getInt(0);
			data.icon_res = cursor.getInt(1);
			lowerBarData.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		return lowerBarData;
	}
	
	public void updateUpdateChecker(Integer id, String name, String datetime){
		
		getWritableDatabase();
		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put(MainDataKeys.UPDATE_DATA_NAME, name);
		args.put(MainDataKeys.UPDATE_DATA_DATETIME, datetime);
		db.update(UPDATE_CHECKER_TABLE, args, strFilter, null);
	}
	
	public void resetTables()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(UPDATE_CHECKER_TABLE, null, null);
		db.delete(AREA_TABLE, null, null);
		db.delete(CATEGORY_TABLE, null, null);
		db.delete(LOWER_BAR_TABLE, null, null);
	}
	
	public void clearTableUpdateChecker(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(UPDATE_CHECKER_TABLE, null, null);
		db.close();
	}
	public void clearTableArea(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(AREA_TABLE, null, null);
		db.close();
	}
	
	public void clearTableCategory(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(CATEGORY_TABLE, null, null);
		db.close();
	}
	
	public void clearTableLowerBar(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(LOWER_BAR_TABLE, null, null);
		db.close();
	}

}
