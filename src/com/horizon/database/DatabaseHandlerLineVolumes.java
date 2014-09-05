package com.horizon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseHandlerLineVolumes extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_LINEVOLUMES = "LineVolume";

	// Contacts Table Columns names
	private static final String KEY_ID = "idLineVolume";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_ID_LINE = "idLine";
	private static final String KEY_ID_VOLUME = "idVolume";
	private static final String KEY_DESC = "Descripcion";	
	
	public DatabaseHandlerLineVolumes(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_LINEVOLUMES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_ID_LINE + " INTEGER," + KEY_ID_VOLUME 
				+ " INTEGER," + KEY_DESC + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*		// // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
		// Create tables again
		onCreate(db);
		 */	
	}
	
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_LINEVOLUMES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_ID_LINE + " INTEGER," + KEY_ID_VOLUME 
				+ " INTEGER," + KEY_DESC + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINEVOLUMES);
		// Create tables again
		onCreate(db);
	}
		
	/** All CRUD(Create, Read, Update, Delete) Operations **/
			
	// Adding new transaction
	public void addLineVolume(LineVolume linevolume) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//Log.d("log_tag", " INSERTING >>>>>ID Web " + linevolume.getIDWeb() + " Line " + linevolume.getIDLine() 
		//		+ " Volume " + linevolume.getIDVolume()+ " Description " + linevolume.getDescription());
		
		//values.put(KEY_ID, linevolume.getID()); // LineVolume Id
		values.put(KEY_ID_WEB, linevolume.getIDWeb()); // LineVolume IdWeb
		values.put(KEY_ID_LINE, linevolume.getIDLine()); // LineVolume IdLine
		values.put(KEY_ID_VOLUME, linevolume.getIDVolume()); // LineVolume IdVolume
		values.put(KEY_DESC, linevolume.getDescription()); // LineVolume Desc
		// Inserting Row
		db.insert(TABLE_LINEVOLUMES, null, values);
		
		db.close(); // Closing database connection
	}
					
	// Getting All Products
	public List<LineVolume> getAllLineVolumes() {
		List<LineVolume> lineVolumeList = new ArrayList<LineVolume>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_LINEVOLUMES;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				LineVolume linevolume = new LineVolume();
				linevolume.setID(Integer.parseInt(cursor.getString(0)));
				linevolume.setIDWeb(Integer.parseInt(cursor.getString(1)));
				linevolume.setIDLine(Integer.parseInt(cursor.getString(2)));
				linevolume.setIDVolume(Integer.parseInt(cursor.getString(3)));
				linevolume.setDescription(cursor.getString(4));
				// Adding Line to list
				lineVolumeList.add(linevolume);
			} while (cursor.moveToNext());
		}
		return lineVolumeList;
	}
	
	
}