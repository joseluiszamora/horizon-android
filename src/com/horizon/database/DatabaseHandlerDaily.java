package com.horizon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandlerDaily extends SQLiteOpenHelper{
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "Horizon";
	// Transaction table name
	private static final String TABLE_TRANSACTION = "Daily";
	// Contacts Table Columns names
	
	private static final String KEY_ID = "id";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_ID_TRANSACTION = "idTransaction";
	private static final String KEY_ID_CUSTOMER = "idCustomer";
	private static final String KEY_TRANSACTION_DATE = "transactionDate";
	private static final String KEY_VOUCHER = "voucher";
	private static final String KEY_TYPE = "type";
	private static final String KEY_AMMOUNT = "ammount";
	private static final String KEY_CUSTOMER_CODE = "customerCode";
	private static final String KEY_CUSTOMER_NAME = "customerName";
	private static final String KEY_CUSTOMER_ADDRESS = "customerAddress";
	private static final String KEY_STATUS = "status";
	
	public DatabaseHandlerDaily(Context context, String name,CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_ID_TRANSACTION + " TEXT," + KEY_ID_CUSTOMER + " TEXT,"
				+ KEY_TRANSACTION_DATE + " TEXT," + KEY_VOUCHER + " TEXT," + KEY_TYPE + " TEXT," + KEY_AMMOUNT + " TEXT," 
				+ KEY_CUSTOMER_CODE + " TEXT," + KEY_CUSTOMER_NAME + " TEXT," + KEY_CUSTOMER_ADDRESS + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_ID_TRANSACTION + " TEXT," + KEY_ID_CUSTOMER + " TEXT,"
				+ KEY_TRANSACTION_DATE + " TEXT," + KEY_VOUCHER + " TEXT," + KEY_TYPE + " TEXT," + KEY_AMMOUNT + " TEXT," 
				+ KEY_CUSTOMER_CODE + " TEXT," + KEY_CUSTOMER_NAME + " TEXT," + KEY_CUSTOMER_ADDRESS + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}
	
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
		// Create tables again
		onCreate(db);
	}
	
	public void truncateTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("TRUNCATE TABLE IF EXISTS " + TABLE_TRANSACTION);
		// Create tables again
		onCreate(db);
	}
	
	// Deleting All (OjO)
	public void deleteAll(String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_TRANSACTION, null, null);
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
	public Long add(Daily daily) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, daily.getIDWeb());
		values.put(KEY_ID_TRANSACTION, daily.getIDTransaction());
		values.put(KEY_ID_CUSTOMER, daily.getIDCustomer());
		values.put(KEY_TRANSACTION_DATE, daily.getTransactionDate());
		values.put(KEY_VOUCHER, daily.getVoucher());
		values.put(KEY_TYPE, daily.getType());
		values.put(KEY_AMMOUNT, daily.getAmmount());
		values.put(KEY_CUSTOMER_CODE, daily.getCustomerCode());
		values.put(KEY_CUSTOMER_NAME, daily.getCustomerName());
		values.put(KEY_CUSTOMER_ADDRESS, daily.getCustomerAddress());
		values.put(KEY_STATUS, daily.getStatus());
		long id = db.insert(TABLE_TRANSACTION, null, values);
		db.close();
		
		return id;
	}
	// Getting All 
	public List<Daily> getAll() {
		List<Daily> list = new ArrayList<Daily>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + "";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Daily daily = new Daily();
				daily.setID(Integer.parseInt(cursor.getString(0)));
				daily.setIDWeb(Integer.parseInt(cursor.getString(1)));
				daily.setIDTransaction(Integer.parseInt(cursor.getString(2)));
				daily.setIDCustomer(Integer.parseInt(cursor.getString(3)));
				daily.setTransactionDate(cursor.getString(4));
				daily.setVoucher(cursor.getString(5));
				daily.setType(cursor.getString(6));
				daily.setAmmount(cursor.getString(7));
				daily.setCustomerCode(cursor.getString(8));
				daily.setCustomerName(cursor.getString(9));
				daily.setCustomerAddress(cursor.getString(10));
				daily.setStatus(cursor.getString(11));			
				// Adding to list
				list.add(daily);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return list;
	}

	// Getting single
	public Daily get(int id) {	
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_TRANSACTION, KEY_ID_CUSTOMER, KEY_TRANSACTION_DATE, KEY_VOUCHER, KEY_TYPE, KEY_AMMOUNT, KEY_CUSTOMER_CODE, 
				KEY_CUSTOMER_NAME, KEY_CUSTOMER_ADDRESS, KEY_STATUS }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			Daily daily = new Daily(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), 
					Integer.parseInt(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), 
					cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));			
			return daily;
		}else{
			cursor.close();
			db.close();
			return null;
		}			
	}
	
	// Getting single by idweb
	public Daily getByIdweb(int id) {		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_TRANSACTION, KEY_ID_CUSTOMER, KEY_TRANSACTION_DATE, KEY_VOUCHER, KEY_TYPE, KEY_AMMOUNT, KEY_CUSTOMER_CODE, 
				KEY_CUSTOMER_NAME, KEY_CUSTOMER_ADDRESS, KEY_STATUS }, KEY_ID_WEB + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){			
			Daily daily = new Daily(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), 
					Integer.parseInt(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), 
					cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
			cursor.close();
			db.close();
			return daily;
		}else{
			db.close();		
			return null;
		}							
	}
	
	// Updating single
	public int update(Daily daily) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, daily.getIDWeb());
		values.put(KEY_ID_TRANSACTION, daily.getIDTransaction());
		values.put(KEY_ID_CUSTOMER, daily.getIDCustomer());
		values.put(KEY_TRANSACTION_DATE, daily.getTransactionDate());
		values.put(KEY_VOUCHER, daily.getVoucher());
		values.put(KEY_TYPE, daily.getType());
		values.put(KEY_AMMOUNT, daily.getAmmount());
		values.put(KEY_CUSTOMER_CODE, daily.getCustomerCode());
		values.put(KEY_CUSTOMER_NAME, daily.getCustomerName());
		values.put(KEY_CUSTOMER_ADDRESS, daily.getCustomerAddress());
		values.put(KEY_STATUS, daily.getStatus());

		// updating row
		return db.update(TABLE_TRANSACTION, values, KEY_ID + " = ?",
		new String[] { String.valueOf(daily.getID()) });
	}

	// Deleting single
	public void deleteTransaction(int codetransaction) {
		SQLiteDatabase db = this.getWritableDatabase();	
		db.delete(TABLE_TRANSACTION, KEY_ID + " = ?",
				new String[] { String.valueOf(codetransaction) });
		db.close();
	}
}
