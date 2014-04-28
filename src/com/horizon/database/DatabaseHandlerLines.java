package com.horizon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandlerLines extends SQLiteOpenHelper {
	
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_LINES = "Lines";

	// Contacts Table Columns names
	private static final String KEY_ID = "idLine";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_DESC = "desc";	
		
	public DatabaseHandlerLines(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_LINES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_DESC + " TEXT" + ")";		
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
		Log.d("log_tag", "***************** create table");
		// Create tables again
		onCreate(db);*/
	}
	
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_LINES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " INTEGER," + KEY_DESC + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
		// Create tables again
		onCreate(db);
	}
		
	/** All CRUD(Create, Read, Update, Delete) Operations **/
	
	// Adding new transaction
	public void addLine(Line line) {
		Log.d("log_tag", "ADD LINE >" + line.getIDWeb());
		Log.d("log_tag", "ADD LINE >" + line.getDescription());
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(KEY_ID, line.getID()); // Line Id
		values.put(KEY_ID_WEB, line.getIDWeb()); // Line IdWeb
		values.put(KEY_DESC, line.getDescription()); // Line Desc
		// Inserting Row
		db.insert(TABLE_LINES, null, values);
		db.close(); // Closing database connection
	}
			
	// Getting All Products
	public List<Line> getAllLines() {
		List<Line> lineList = new ArrayList<Line>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_LINES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Line line = new Line();
				
				line.setID(Integer.parseInt(cursor.getString(0)));
				line.setIDWeb(Integer.parseInt(cursor.getString(1)));
				line.setDescription(cursor.getString(2));
				
				Log.d("log_tag", "GET LINES >" + cursor.getString(0) + " __ "+ cursor.getString(1) + " __ "+ cursor.getString(2));
				// Adding Line to list
				lineList.add(line);
			} while (cursor.moveToNext());
		}
		return lineList;
	}
}