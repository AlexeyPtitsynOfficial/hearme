package com.donearh.hearme.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.donearh.hearme.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class SearchDatabase {
	
	private static final String TAG = "DictionaryDatabase";

    //The columns we'll include in the dictionary table
    public static final String COL_WORD = "WORD";
    public static final String COL_DEFINITION = "DEFINITION";

    private static final String DATABASE_NAME = "DICTIONARY";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;
    
	private DatabaseOpenHelper mDatabaseOpenHelper;
	
	public SearchDatabase(Context context){
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);
	}
	
	private static class DatabaseOpenHelper extends SQLiteOpenHelper{
		
		private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                    "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                    " USING fts3 (" +
                    COL_WORD + ", " +
                    COL_DEFINITION + ")";

		public DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadDictionary();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
		}
		
		private void loadDictionary() {
	        new Thread(new Runnable() {
	            public void run() {
	                try {
	                    loadWords();
	                } catch (IOException e) {
	                    throw new RuntimeException(e);
	                }
	            }
	        }).start();
	    }
		
		private void loadWords() throws IOException {
		    final Resources resources = mHelperContext.getResources();
		    InputStream inputStream = resources.openRawResource(R.raw.definitions);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		    try {
		        String line;
		        while ((line = reader.readLine()) != null) {
		            String[] strings = TextUtils.split(line, "-");
		            if (strings.length < 2) continue;
		            long id = addWord(strings[0].trim(), strings[1].trim());
		            if (id < 0) {
		                Log.e(TAG, "unable to add word: " + strings[0].trim());
		            }
		        }
		    } finally {
		        reader.close();
		    }
		}
		
		public long addWord(String word, String definition) {
		    ContentValues initialValues = new ContentValues();
		    initialValues.put(COL_WORD, word);
		    initialValues.put(COL_DEFINITION, definition);

		    return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
		}
	}

	public Cursor getWordMatches(String query, String[] columns) {
	    String selection = COL_WORD + " MATCH ?";
	    String[] selectionArgs = new String[] {query+"*"};

	    return query(selection, selectionArgs, columns);
	}

	private Cursor query(String selection, String[] selectionArgs, String[] columns) {
	    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	    builder.setTables(FTS_VIRTUAL_TABLE);

	    Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
	            columns, selection, selectionArgs, null, null, null);

	    if (cursor == null) {
	        return null;
	    } else if (!cursor.moveToFirst()) {
	        cursor.close();
	        return null;
	    }
	    return cursor;
	}
}
