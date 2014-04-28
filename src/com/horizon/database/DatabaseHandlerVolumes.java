package com.horizon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseHandlerVolumes extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_VOLUMES = "Volume";

	// Contacts Table Columns names
	private static final String KEY_ID = "idVolume";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_DESC = "Descripcion";	
		
	public DatabaseHandlerVolumes(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_VOLUME_TABLE = "CREATE TABLE " + TABLE_VOLUMES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_DESC + " TEXT" + ")";		
		db.execSQL(CREATE_VOLUME_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUMES);		
		// Create tables again
		onCreate(db);*/
	}
	
	public void CreateTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		String CREATE_VOLUME_TABLE = "CREATE TABLE " + TABLE_VOLUMES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_DESC + " TEXT" + ")";		
		db.execSQL(CREATE_VOLUME_TABLE);
		db.close();
	}
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUMES);
		// Create tables again
		onCreate(db);
		db.close();
	}
		
	/** All CRUD(Create, Read, Update, Delete) Operations **/
	
	// Adding new transaction
	public void addVolume(Volume volume) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(KEY_ID, volume.getID()); // Line Id
		values.put(KEY_ID_WEB, volume.getIDWeb()); // Line IdWeb
		values.put(KEY_DESC, volume.getDescription()); // Line Desc
		
		// Inserting Row
		db.insert(TABLE_VOLUMES, null, values);
		db.close(); // Closing database connection
	}
			
	// Getting All Volumes
	public List<Volume> getAllVolumes() {
		List<Volume> volumeList = new ArrayList<Volume>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_VOLUMES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Volume volume = new Volume();
				volume.setID(Integer.parseInt(cursor.getString(0)));
				volume.setIDWeb(Integer.parseInt(cursor.getString(1)));
				volume.setDescription(cursor.getString(2));
				// Adding Line to list
				volumeList.add(volume);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return volumeList;
	}	
	
	public List<Volume> getAllVolumesForLine(int Line) {
		SQLiteDatabase db = this.getWritableDatabase();
		List<Volume> volumeList = new ArrayList<Volume>();		
		String MY_QUERY = "SELECT * FROM Volume v, LineVolume lv, Lines l " +
				"WHERE v.idVolume = lv.idVolume AND l.idLine = lv.idLine" +
				" AND lv.idLine=?";			
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(Line)});
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {		
			do {
				Volume volume = new Volume();
				volume.setID(Integer.parseInt(cursor.getString(0)));
				volume.setIDWeb(Integer.parseInt(cursor.getString(1)));
				volume.setDescription(cursor.getString(2));
				// Adding Line to list
				volumeList.add(volume);
			} while (cursor.moveToNext());
		}else{		
			return null;
		}

		cursor.close();
		db.close();
		return volumeList;
	}
}