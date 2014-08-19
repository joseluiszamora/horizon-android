package com.horizon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandlerBonus  extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "Horizon";
	// Contacts table name
	private static final String TABLE_BONUS = "Bonus";

	// Contacts Table Columns names
	private static final String KEY_ID = "idBonus";
	private static final String KEY_TYPE = "type";
	private static final String KEY_ID_LINE_FROM = "idlinefrom";
	private static final String KEY_ID_PRODUCT_FROM = "idproductfrom";
	private static final String KEY_QUANTITY_FROM = "quantityfrom";
	private static final String KEY_NAME_FROM = "namefrom";
	private static final String KEY_ID_LINE_TO = "idlineto";
	private static final String KEY_ID_PRODUCT_TO = "idproductto";
	private static final String KEY_QUANTITY_TO = "quantityto";
	private static final String KEY_NAME_TO = "nameto";
	private static final String KEY_STATUS = "status";
		
	public DatabaseHandlerBonus(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_BONUS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," + KEY_ID_LINE_FROM + " INTEGER," + KEY_ID_PRODUCT_FROM + " TEXT," 
				+ KEY_QUANTITY_FROM + " INTEGER," + KEY_NAME_FROM + " TEXT," + KEY_ID_LINE_TO + " INTEGER," + KEY_ID_PRODUCT_TO + " TEXT,"
				+ KEY_QUANTITY_TO + " INTEGER,"+ KEY_NAME_TO + " TEXT," + KEY_STATUS + " TEXT" + ")";		
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_TABLE = "CREATE TABLE " + TABLE_BONUS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," + KEY_ID_LINE_FROM + " INTEGER," + KEY_ID_PRODUCT_FROM + " TEXT," 
				+ KEY_QUANTITY_FROM + " INTEGER," + KEY_NAME_FROM + " TEXT," + KEY_ID_LINE_TO + " INTEGER," + KEY_ID_PRODUCT_TO + " TEXT,"
				+ KEY_QUANTITY_TO + " INTEGER,"+ KEY_NAME_TO + " TEXT," + KEY_STATUS + " TEXT" + ")";		
		db.execSQL(CREATE_TABLE);
	}
	
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BONUS);
		onCreate(db);
	}
			
	/** All CRUD(Create, Read, Update, Delete) Operations **/
		
	// Adding new
	public void addBonus(Bonus bonus) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, bonus.getType());
		values.put(KEY_ID_LINE_FROM, bonus.getIdLineFrom());
		values.put(KEY_ID_PRODUCT_FROM, bonus.getIdProductFrom());
		values.put(KEY_QUANTITY_FROM, bonus.getQuantityFrom());
		values.put(KEY_NAME_FROM, bonus.getNameFrom());
		values.put(KEY_ID_LINE_TO, bonus.getIdLineTo());
		values.put(KEY_ID_PRODUCT_TO, bonus.getIdProductTo());
		values.put(KEY_QUANTITY_TO, bonus.getQuantityTo());
		values.put(KEY_NAME_TO, bonus.getNameTo());
		values.put(KEY_STATUS, bonus.getStatus());
		// Inserting Row
		db.insert(TABLE_BONUS, null, values);
		db.close(); // Closing database connection
	}
	
	public String[] getAllNames() {
		int i = 0;
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_BONUS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		String [] customerList;  
		customerList = new String[cursor.getCount()];
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {				
				//customerList[i] = cursor.getString(2);
				customerList[i] = new String(cursor.getString(2));
				i++;
			} while (cursor.moveToNext());
		}		
		return customerList;
	}

	// Getting All Bonus
	public List<Bonus> getAllBonus() {
		List<Bonus> list = new ArrayList<Bonus>();
		String selectQuery = "SELECT  * FROM " + TABLE_BONUS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Bonus bonus = new Bonus();
				
				bonus.setID(Integer.parseInt(cursor.getString(0)));
				bonus.setType(cursor.getString(1));
				bonus.setIdLineFrom(Integer.parseInt(cursor.getString(2)));
				bonus.setIdProductFrom(cursor.getString(3));
				bonus.setQuantityFrom(Integer.parseInt(cursor.getString(4)));
				bonus.setNameFrom(cursor.getString(5));
				bonus.setIdLineTo(Integer.parseInt(cursor.getString(6)));
				bonus.setIdProductTo(cursor.getString(7));
				bonus.setQuantityTo(Integer.parseInt(cursor.getString(8)));
				bonus.setNameTo(cursor.getString(9));
				bonus.setStatus(cursor.getString(10));
				// Adding Line to list
				list.add(bonus);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	public List<Bonus> getAllSearch(String like) {
		List<Bonus> list = new ArrayList<Bonus>();		
		return list;
	}
	
	public List<Bonus> getBonusSearch(String id, String type) {// type: 'line', 'product'
		SQLiteDatabase db = this.getReadableDatabase();
		List<Bonus> list = new ArrayList<Bonus>();
		Cursor cursor;
		if (type == "line") {
			cursor = db.query(TABLE_BONUS, new String[] {KEY_ID, KEY_TYPE, KEY_ID_LINE_FROM, KEY_ID_PRODUCT_FROM, KEY_QUANTITY_FROM
					, KEY_NAME_FROM, KEY_ID_LINE_TO, KEY_ID_PRODUCT_TO, KEY_QUANTITY_TO, KEY_NAME_TO, KEY_STATUS}, 
					KEY_ID_LINE_FROM + "=?", new String[] { id }, null, null, null, null);	
		} else {
			cursor = db.query(TABLE_BONUS, new String[] {KEY_ID, KEY_TYPE, KEY_ID_LINE_FROM, KEY_ID_PRODUCT_FROM, KEY_QUANTITY_FROM
					, KEY_NAME_FROM, KEY_ID_LINE_TO, KEY_ID_PRODUCT_TO, KEY_QUANTITY_TO, KEY_NAME_TO, KEY_STATUS}, 
					KEY_ID_PRODUCT_FROM + "=?", new String[] { id }, null, null, null, null);
		}
		
		if (cursor.moveToFirst()) {
			do {
				Bonus bonus = new Bonus();
				
				bonus.setID(Integer.parseInt(cursor.getString(0)));
				bonus.setType(cursor.getString(1));
				bonus.setIdLineFrom(Integer.parseInt(cursor.getString(2)));
				bonus.setIdProductFrom(cursor.getString(3));
				bonus.setQuantityFrom(Integer.parseInt(cursor.getString(4)));
				bonus.setNameFrom(cursor.getString(5));
				bonus.setIdLineTo(Integer.parseInt(cursor.getString(6)));
				bonus.setIdProductTo(cursor.getString(7));
				bonus.setQuantityTo(Integer.parseInt(cursor.getString(8)));
				bonus.setNameTo(cursor.getString(9));
				bonus.setStatus(cursor.getString(10));
				// Adding contact to list
				list.add(bonus);
			} while (cursor.moveToNext());
		}
		
		return list;
	}
}
