package com.donearh.hearme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.donearh.hearme.keys.MainDataKeys;
import com.donearh.hearme.db.tables.MainDBStruct.*;

/**
 * Created by Donearh on 01.05.2016.
 */
public class MainDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "main_data";

    private static MainDBHelper sInstance;

    public static synchronized MainDBHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new MainDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public MainDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String SQL_CREATE_UPDATE_CHECKER = "CREATE TABLE " + UpdateChecker.TABLE_NAME + "("
                + UpdateChecker.ID + " INTEGER PRIMARY KEY,"
                + UpdateChecker.UPDATE_NAME + " TEXT,"
                + UpdateChecker.UPDATE_DATETIME + " DATETIME,"
                + UpdateChecker.INFO_TEXT + " TEXT,"
                + UpdateChecker.STATE + " TEXT"
                + ")";

        db.execSQL(SQL_CREATE_UPDATE_CHECKER);

        String SQL_CREATE_AREA_TABLE = "CREATE TABLE " + AreasTable.TABLE_NAME + "("
                + AreasTable.ID + " INTEGER PRIMARY KEY,"
                + AreasTable.NAME + " TEXT" + ")";
        db.execSQL(SQL_CREATE_AREA_TABLE);

        String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CatsTable.TABLE_NAME + "("
                + CatsTable.ID + " INTEGER PRIMARY KEY,"
                + CatsTable.NAME + " TEXT,"
                + CatsTable.PARENT_ID + " INTEGER,"
                + CatsTable.COLOR_ID + " INTEGER,"
                + CatsTable.HAS_CHILD + " INTEGER,"
                + CatsTable.LEFT_COLOR + " TEXT,"
                + CatsTable.RIGHT_COLOR + " TEXT,"
                + CatsTable.ICON + " TEXT" + ")";
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

        String SQL_CREATE_LOWER_BAR_TABLE ="CREATE TABLE " + LowerBarTable.TABLE_NAME + "("
                + LowerBarTable.MENU_TYPE + " INTEGER PRIMARY KEY,"
                + LowerBarTable.ICON_RES + " INTEGER,"
                + LowerBarTable.SORT_FIELD + " INTEGER"+ ")";
        db.execSQL(SQL_CREATE_LOWER_BAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + UpdateChecker.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AreasTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CatsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LowerBarTable.TABLE_NAME);
        onCreate(db);
    }
}
