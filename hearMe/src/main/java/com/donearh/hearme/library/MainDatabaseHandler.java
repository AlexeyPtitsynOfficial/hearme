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
import com.donearh.hearme.db.MainDBHelper;
import com.donearh.hearme.db.tables.MainDBStruct.*;
import com.donearh.hearme.keys.MainDataKeys;

public class MainDatabaseHandler {

	private static MainDBHelper mDBHelper;
	public MainDatabaseHandler(Context context) {
		mDBHelper = MainDBHelper.getInstance(context);
	}


	public synchronized static SQLiteDatabase getDB() {
		return mDBHelper.getWritableDatabase();
	}

	public void closeDB(SQLiteDatabase db)
	{
		if(db != null)
		{
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
			db = null;
		}
	}
	
	public void addUpdateCheckerData(UpdateCheckerData data){
		SQLiteDatabase db = getDB();
		ContentValues values = new ContentValues();
		values.put(UpdateChecker.ID, data.id);
		values.put(UpdateChecker.UPDATE_NAME, data.name);
		values.put(UpdateChecker.UPDATE_DATETIME, data.update_datetime);
		values.put(UpdateChecker.INFO_TEXT, data.info_text);
		values.put(UpdateChecker.STATE, data.state);
		db.insert(UpdateChecker.TABLE_NAME, null, values);
	}
	public void addAreaData(ArrayList<AreaData> array_data)
	{
		SQLiteDatabase db = getDB();
		db.beginTransaction();
		for(AreaData data : array_data) {
			ContentValues values = new ContentValues();
			values.put(AreasTable.ID, data.id);
			values.put(AreasTable.NAME, data.name);
			db.insert(AreasTable.TABLE_NAME, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void addCategoryData(ArrayList<CategoryData> array_data)
	{
		SQLiteDatabase db = getDB();
		db.beginTransaction();
		for(CategoryData data : array_data){
			ContentValues values = new ContentValues();
			values.put(CatsTable.ID, data.id);
			values.put(CatsTable.NAME, data.name);
			values.put(CatsTable.PARENT_ID, data.parent_id);
			values.put(CatsTable.COLOR_ID, data.color_id);
			values.put(CatsTable.HAS_CHILD, data.has_child);
			values.put(CatsTable.LEFT_COLOR, data.left_color);
			values.put(CatsTable.RIGHT_COLOR, data.right_color);
			values.put(CatsTable.ICON, data.icon_img);
			db.insert(CatsTable.TABLE_NAME, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void addLowerBarData(ArrayList<LowerBarData> array_data){
		SQLiteDatabase db = getDB();
		db.beginTransaction();
		for(LowerBarData data : array_data){
			ContentValues values = new ContentValues();
			values.put(LowerBarTable.MENU_TYPE, data.menu_type);
			values.put(LowerBarTable.ICON_RES, data.icon_res);
			values.put(LowerBarTable.SORT_FIELD, 0);
			db.insert(LowerBarTable.TABLE_NAME, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public int getUpdateCheckerRowCount(){
		String countQuery = "SELECT * FROM " + UpdateChecker.TABLE_NAME;
		SQLiteDatabase db = getDB();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		cursor.close();
		return rowCount;
	}
	public int getAreaRowCount()
	{
		String countQuery = "SELECT * FROM " + AreasTable.TABLE_NAME;
		SQLiteDatabase db = getDB();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		cursor.close();
		return rowCount;
	}
	
	public int getCategoryRowCount()
	{
		String countQuery = "SELECT * FROM " + CatsTable.TABLE_NAME;
		SQLiteDatabase db = getDB();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		cursor.close();
		return rowCount;
	}
	
	public ArrayList<UpdateCheckerData> getUpdateCheckerData(){
		
		String selectQuery = "SELECT * FROM " + UpdateChecker.TABLE_NAME;
		SQLiteDatabase db = getDB();
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
		String selectQuery = "SELECT * FROM " + AreasTable.TABLE_NAME;
		SQLiteDatabase db = getDB();
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
		return areaData;
	}
	
	public ArrayList<CategoryData> getCategoryData()
	{
		String selectQuery = "SELECT * FROM " + CatsTable.TABLE_NAME;
		SQLiteDatabase db = getDB();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
		cursor.moveToFirst();
		
		for(int i=0; i<cursor.getCount(); i++)
		{
			CategoryData data = new CategoryData();
			data.id = cursor.getInt(0);
			data.name = cursor.getString(1);
			data.parent_id = cursor.getInt(2);
			data.color_id = cursor.getInt(3);
			data.has_child = cursor.getInt(4);
			data.left_color = cursor.getString(5);
			data.right_color = cursor.getString(6);
			data.icon_img = cursor.getString(7);
			categoryData.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return categoryData;
	}
	
	public ArrayList<LowerBarData> getLowerBarData(){

		SQLiteDatabase db = getDB();
		String selectQuery = "SELECT * FROM " + LowerBarTable.TABLE_NAME + " ORDER BY "+ LowerBarTable.SORT_FIELD;
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
		return lowerBarData;
	}
	
	public void updateUpdateChecker(Integer id, String name, String datetime){

		SQLiteDatabase db = getDB();
		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put(UpdateChecker.UPDATE_NAME, name);
		args.put(UpdateChecker.UPDATE_DATETIME, datetime);
		db.update(UpdateChecker.TABLE_NAME, args, strFilter, null);
	}
	
	public void resetTables()
	{
		SQLiteDatabase db = getDB();
		db.delete(UpdateChecker.TABLE_NAME, null, null);
		db.delete(AreasTable.TABLE_NAME, null, null);
		db.delete(CatsTable.TABLE_NAME, null, null);
		db.delete(LowerBarTable.TABLE_NAME, null, null);
	}
	
	public void clearTableUpdateChecker(){
		SQLiteDatabase db = getDB();
		db.delete(UpdateChecker.TABLE_NAME, null, null);
	}
	public void clearTableArea(){
		SQLiteDatabase db = getDB();
		db.delete(AreasTable.TABLE_NAME, null, null);
	}
	
	public void clearTableCategory(){
		SQLiteDatabase db = getDB();
		db.delete(CatsTable.TABLE_NAME, null, null);
	}
	
	public void clearTableLowerBar(){
		SQLiteDatabase db = getDB();
		db.delete(LowerBarTable.TABLE_NAME, null, null);
	}

}
