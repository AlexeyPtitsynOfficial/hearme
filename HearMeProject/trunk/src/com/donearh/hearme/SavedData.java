package com.donearh.hearme;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SavedData
{
	private static volatile SavedData instance;
	
	public static final String PREFS_NAME = "UserProfile";
	
	private static String FIRST_RUN = "first_run";
	private static String POLICY_UPDATE = "policy_update";
	private static String LOWER_BAR_OPEN = "lower_bar_open";
	private static String AREA_ID = "area_id";
	private static String MAIN_AREA_ID = "main_area_id";
	private static String LOWER_BAR_POS_ARRAY = "lower_bar_pos_array";
	private static String FAVORITE_ADS = "favorite_ads";
	private static String FAVORITE_USERS = "favorite_users";
	private static String SELECTED_CATS = "selected_cats";
	private static String OUT_CATS = "out_cats";
	private SharedPreferences mPrefs;
	
	
	private SavedData(Context context)
	{
		mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String text = "";
	}
	
	public static SavedData getInstance(Context context){
		if(instance == null){
			synchronized (SavedData.class) {
				if(instance == null)
					instance = new SavedData(context);
			}
		}
		return instance;
	}
	
	public void setFirstStart(boolean state){
		Editor editor = mPrefs.edit();
		editor.putBoolean(FIRST_RUN, state);
		editor.commit();
	}
	
	public boolean isFirstAppRun()
	{
		return mPrefs.getBoolean(FIRST_RUN, true);
		
	}
	
	public void savePolicyState(boolean state){
		Editor editor = mPrefs.edit();
		editor.putBoolean(POLICY_UPDATE, state);
		editor.commit();
	}
	
	public void saveLowerBarPos(boolean is_open)
	{
		Editor editor = mPrefs.edit();
		editor.putBoolean(LOWER_BAR_OPEN, is_open);
		editor.commit();
	}
	
	public void saveSelectedArea(int area_id)
	{
		Editor editor = mPrefs.edit();
		editor.putInt(AREA_ID, area_id);
		editor.commit();
	}
	
	public void saveSelectedAreaId(int area_id)
	{
		Editor editor = mPrefs.edit();
		editor.putInt(MAIN_AREA_ID, area_id);
		editor.commit();
	}
	
	public boolean getPolicyUpdate()
	{
		return mPrefs.getBoolean(POLICY_UPDATE, false);
	}
	
	public boolean getLowerBarPos()
	{
		return mPrefs.getBoolean(LOWER_BAR_OPEN, false);
	}
	
	public int getAreaId()
	{
		return mPrefs.getInt(AREA_ID, -1);
	}
	
	public int getMainAreaId()
	{
		return mPrefs.getInt(MAIN_AREA_ID, -1);
	}
		
	public void saveFavoriteAds(ArrayList<Integer> ads_id)
	{
		Editor editor = mPrefs.edit();
		try
		{
			editor.putString(FAVORITE_ADS, ObjectSerializer.serialize(ads_id));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public void saveSelectedCats(ArrayList<Integer> cats){
		Editor editor = mPrefs.edit();
		try
		{
			editor.putString(SELECTED_CATS, ObjectSerializer.serialize(cats));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public void saveOutCats(ArrayList<Integer> out_cats){
		Editor editor = mPrefs.edit();
		try
		{
			editor.putString(OUT_CATS, ObjectSerializer.serialize(out_cats));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public ArrayList<Integer> getUserFavoriteAds()
	{
		ArrayList<Integer> array = new ArrayList<Integer>();
		try
		{
			array = (ArrayList<Integer>) ObjectSerializer.deserialize(mPrefs.getString(FAVORITE_ADS, ObjectSerializer.serialize(new ArrayList<Integer>())));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return array;
	}
	
	public ArrayList<Integer> getSelectedCats(){
		ArrayList<Integer> tArray = new ArrayList<Integer>();
		try 
        {
			tArray = (ArrayList<Integer>) ObjectSerializer.deserialize(mPrefs.getString(SELECTED_CATS, ObjectSerializer.serialize(new ArrayList<Integer>())));
        	
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    	return tArray;
	}
	
	public ArrayList<Integer> getOutCats(){
		ArrayList<Integer> tArray = new ArrayList<Integer>();
		try 
        {
			tArray = (ArrayList<Integer>) ObjectSerializer.deserialize(mPrefs.getString(OUT_CATS, ObjectSerializer.serialize(new ArrayList<Integer>())));
        	
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    	return tArray;
	}
	
	public void logOut(){
		Editor editor = mPrefs.edit();
		editor.remove(FAVORITE_ADS);
		editor.remove(FAVORITE_USERS);
		editor.remove(SELECTED_CATS);
		editor.remove(OUT_CATS);
		editor.commit();
	}
	
	
	public void clearSelectCats(){
		Editor editor = mPrefs.edit();
		editor.remove(SELECTED_CATS);
		editor.commit();
	}
}
