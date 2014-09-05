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

public class DatabaseHandlerPay extends SQLiteOpenHelper{
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "Horizon";
	// Transaction table name
	private static final String TABLE_NAME = "Pay";
	// Contacts Table Columns names
	
	private static final String KEY_ID = "id";
	private static final String KEY_ID_DAILY = "idDaily";
	private static final String KEY_ID_TRANSACTION = "idTransaction";
	private static final String KEY_ID_CUSTOMER = "idCustomer";
	private static final String KEY_VOUCHER = "voucher";
	private static final String KEY_AMMOUNT = "ammount";
	private static final String KEY_DATE = "date";
	private static final String KEY_STATUS = "status";
	
	public DatabaseHandlerPay(Context context, String name,CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_DAILY + " TEXT," + KEY_ID_TRANSACTION + " TEXT," + KEY_ID_CUSTOMER + " TEXT," + KEY_VOUCHER + " TEXT," + KEY_AMMOUNT + " TEXT," + KEY_DATE + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_DAILY + " TEXT," + KEY_ID_TRANSACTION + " TEXT," + KEY_ID_CUSTOMER + " TEXT," + KEY_VOUCHER + " TEXT," + KEY_AMMOUNT + " TEXT," + KEY_DATE + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}
	
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}
	
	public void truncateTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("TRUNCATE TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}
	
	// Deleting All (OjO)
	public void deleteAll(String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, null, null);
		} catch (Exception e) {
			onCreate(db);
		}
		db.close();
	}
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	 * @return 
	*/
	
	// Adding new	
	public int add(Pay pay) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_DAILY, pay.getIdDaily());
		values.put(KEY_ID_TRANSACTION, pay.getIdTransaction());
		values.put(KEY_ID_CUSTOMER, pay.getIdCustomer());
		values.put(KEY_VOUCHER, pay.getVoucher());
		values.put(KEY_AMMOUNT, pay.getAmmount());
		values.put(KEY_DATE, pay.getDate());
		values.put(KEY_STATUS, pay.getStatus());
		int id = Integer.parseInt(String.valueOf(db.insert(TABLE_NAME, null, values)));
		db.close();
		
		return id;
	}
	// Getting All 
	public List<Pay> getAll() {
		List<Pay> list = new ArrayList<Pay>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_STATUS + "='1'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Pay pay = new Pay();
				Log.d("log_tag", ">>>>>>>>>>>>>>>>>>>>>>>>>> JALANDO PAGO DESDE LA DB " + cursor.getString(0));
				
				pay.setID(Integer.parseInt(cursor.getString(0)));
				pay.setIdDaily(Integer.parseInt(cursor.getString(1)));
				pay.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				pay.setIdCustomer(Integer.parseInt(cursor.getString(3)));
				pay.setVoucher(cursor.getString(4));
				pay.setAmmount(cursor.getString(5));
				pay.setDate(cursor.getString(6));
				pay.setStatus(cursor.getString(7));
				// Adding to list
				list.add(pay);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return list;
	}

	// Getting single
	public Pay get(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
				KEY_ID_DAILY, KEY_ID_TRANSACTION, KEY_ID_CUSTOMER, KEY_VOUCHER, KEY_AMMOUNT, KEY_DATE, KEY_STATUS }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			Pay pay = new Pay(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), 
					Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4), 
					cursor.getString(5), cursor.getString(6), cursor.getString(7));
			return pay;
		}else{
			cursor.close();
			db.close();
			return null;
		}			
	}
	
	// Getting single by daily
	public Pay get_by_daily(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
				KEY_ID_DAILY, KEY_ID_TRANSACTION, KEY_ID_CUSTOMER, KEY_VOUCHER, KEY_AMMOUNT, KEY_DATE, KEY_STATUS }, KEY_ID_DAILY + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			Pay pay = new Pay(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), 
					Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4), 
					cursor.getString(5), cursor.getString(6), cursor.getString(7));
			return pay;
		}else{
			cursor.close();
			db.close();
			return null;
		}			
	}
	
	public String[] getAllNames() {
		int i = 0;
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_AMMOUNT;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		String [] list;  
		list = new String[cursor.getCount()];
		if (cursor.moveToFirst()) {
			do {
				list[i] = new String(cursor.getString(2));
				i++;
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return list;
	}
	
	// Getting by id Daily
	public Pay getByIdDaily(int id) {	
		Log.d("log_tag", "GET BY ID DAILY >> " + id);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
				KEY_ID_DAILY, KEY_ID_TRANSACTION, KEY_ID_CUSTOMER, KEY_VOUCHER, KEY_AMMOUNT, KEY_DATE, KEY_STATUS }, KEY_ID_DAILY + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){			
			Pay pay = new Pay(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), 
					Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4), 
					cursor.getString(5), cursor.getString(6), cursor.getString(7));
			cursor.close();
			db.close();
			return pay;
		}else{
			cursor.close();
			db.close();		
			return null;
		}							
	}
	
	// Updating single
	public int update(Pay pay) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_DAILY, pay.getIdDaily());
		values.put(KEY_ID_TRANSACTION, pay.getIdTransaction());
		values.put(KEY_ID_CUSTOMER, pay.getIdCustomer());
		values.put(KEY_VOUCHER, pay.getVoucher());
		values.put(KEY_AMMOUNT, pay.getAmmount());
		values.put(KEY_DATE, pay.getDate());
		values.put(KEY_STATUS, pay.getStatus());
		
		// updating row
		return db.update(TABLE_NAME, values, KEY_ID + " = ?",
		new String[] { String.valueOf(pay.getID()) });
	}

	// Deleting single
	public void delete(int id) {
		Log.d("log_tag", "++++++++DELETING PAGOooooooooooooooo " + id);
		SQLiteDatabase db = this.getWritableDatabase();	
		//db.delete(TABLE_NAME, KEY_ID_DAILY + " = ?", new String[] { String.valueOf(id) });
		
		db.delete(TABLE_NAME, KEY_ID_DAILY + "='" + id + "'", null);
		db.close();
	}
}