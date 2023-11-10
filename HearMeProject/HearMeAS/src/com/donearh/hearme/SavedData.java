package com.donearh.hearme;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SavedData
{

	public static final String PREFS_NAME = "UserProfile";
	
	private static String FIRST_RUN = "first_run";
	private static String LOWER_BAR_OPEN = "lower_bar_open";
	private static String AREA_ID = "area_id";
	private static String MAIN_AREA_ID = "main_area_id";
	private static String LOWER_BAR_POS_ARRAY = "lower_bar_pos_array";
	private static String FAVORITE_ADS = "favorite_ads";
	private static String FAVORITE_USERS = "favorite_users";
	private SharedPreferences mPrefs;
	
	public SavedData(Context context)
	{
		mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String text = "";
	}
	
	public boolean isFirstAppRun()
	{
		if(mPrefs.getBoolean(FIRST_RUN, true))
		{
			Editor editor = mPrefs.edit();
			editor.putBoolean(FIRST_RUN, false);
			editor.commit();
			
			return true;
		}
		else
		{
			return false;
		}
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
	
	public void saveSelectedAreaId(long area_id)
	{
		Editor editor = mPrefs.edit();
		editor.putLong(MAIN_AREA_ID, area_id);
		editor.commit();
	}
	
	public boolean getLowerBarPos()
	{
		return mPrefs.getBoolean(LOWER_BAR_OPEN, false);
	}
	
	public int getAreaId()
	{
		return mPrefs.getInt(AREA_ID, -1);
	}
	
	public long getMainAreaId()
	{
		return mPrefs.getLong(MAIN_AREA_ID, -1);
	}
	
	public void saveLowerBarArrayPos(ArrayList<Integer> menu_type)
	{
		Editor editor = mPrefs.edit();
        try {	
            editor.putString(LOWER_BAR_POS_ARRAY, ObjectSerializer.serialize(menu_type));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
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
	
	public ArrayList<Integer> getLowerBarArrayPos()
	{
		ArrayList<Integer> tArray = new ArrayList<Integer>();
		try 
        {
			tArray = (ArrayList<Integer>) ObjectSerializer.deserialize(mPrefs.getString(LOWER_BAR_POS_ARRAY, ObjectSerializer.serialize(new ArrayList<Integer>())));
        	
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
		editor.commit();
	}
}
