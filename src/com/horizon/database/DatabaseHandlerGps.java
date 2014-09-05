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

public class DatabaseHandlerGps extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_GPS = "Gps";

	// Contacts Table Columns names
	private static final String KEY_ID = "idGps";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_DATE = "date";
	private static final String KEY_HOUR = "hour";
	
	public DatabaseHandlerGps(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_GPS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_DATE + " TEXT," + KEY_HOUR + " TEXT" + ")";		
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
	}
	
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_GPS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_DATE + " TEXT," + KEY_HOUR + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}
	
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);
		// Create tables again
		onCreate(db);
		db.close();
	}
			
	/** All CRUD(Create, Read, Update, Delete) Operations **/
		
	// Adding new transaction
	public void addGps(Gps gps) {		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(KEY_ID, line.getID()); // Line Id
		//Log.d("log_tag", gps.getLatitude() + " ## " + gps.getLongitude() + " ## " + gps.getDate() + " ## " + gps.getHour());
		values.put(KEY_LATITUDE, gps.getLatitude()); // Gps latitude
		values.put(KEY_LONGITUDE, gps.getLongitude()); // Gps longitude
		values.put(KEY_DATE, gps.getDate()); // Gps longitude
		values.put(KEY_HOUR, gps.getHour()); // Gps longitude
		// Inserting Row
		db.insert(TABLE_GPS, null, values);
		db.close(); // Closing database connection
	}
				
	// Getting All GPs
	public List<Gps> getAllGps() {
		List<Gps> gpsList = new ArrayList<Gps>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_GPS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Gps gps = new Gps();
				gps.setID(Integer.parseInt(cursor.getString(0)));
				gps.setLatitude(cursor.getString(1));
				gps.setLongitude(cursor.getString(2));
				gps.setDate(cursor.getString(3));
				gps.setHour(cursor.getString(4));				
				gpsList.add(gps);
			} while (cursor.moveToNext());
		}
		return gpsList;
	}
	
	// Deleting Gps
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GPS, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}
}