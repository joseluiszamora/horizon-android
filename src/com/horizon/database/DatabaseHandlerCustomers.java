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

public class DatabaseHandlerCustomers extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Customers table name
	private static final String TABLE_CUSTOMERS = "Customers";
	
	// Contacts Table Columns names
	private static final String KEY_ID = "idCustomer";
	private static final String KEY_CODE = "CodeCustomer";
	private static final String KEY_NAME = "NombreTienda";
	private static final String KEY_CO_NA = "NombreContacto";
	private static final String KEY_ADDRESS = "Direccion";
	private static final String KEY_PHONE = "Telefono";
	private static final String KEY_CELLPHONE = "TelCelular";
	private static final String KEY_STATUS = "Estado";

	public DatabaseHandlerCustomers(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_CODE + " TEXT," + KEY_NAME + " TEXT,"
			+ KEY_CO_NA + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_PHONE + " TEXT," + KEY_CELLPHONE + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_USERS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*		// // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
		// Create tables again
		onCreate(db);
		Log.d("log_tag", "Upgrade Base de Datos Customers");
*/
	}
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	*/
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
		// Create tables again
		onCreate(db);
	}
	
	
	// Deleting All transactions (OjO)
	public void deleteAllCustomers(String status) {
		SQLiteDatabase db = this.getWritableDatabase();	
		db.delete(TABLE_CUSTOMERS, null, null);
		db.close();
	}
		
	// Adding new customer
	public void addCustomer(Customer customer) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();		
		//values.put(KEY_ID, customer.getID()); // Customer code
		values.put(KEY_CODE, customer.getCode()); // Customer code
		values.put(KEY_NAME, customer.getName()); // Customer name
		values.put(KEY_CO_NA, customer.getContactName()); // Customer contact name
		values.put(KEY_ADDRESS, customer.getAddress()); // Customer address
		values.put(KEY_PHONE, customer.getPhone()); // Customer phone
		values.put(KEY_CELLPHONE, customer.getCellPhone()); // Customer cell phone
		values.put(KEY_STATUS, customer.getStatus()); // Customer status

		// Inserting Row
		db.insert(TABLE_CUSTOMERS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single product
	Customer getCustomer(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Customer customer = null;
		Cursor cursor = db.query(TABLE_CUSTOMERS, new String[] { KEY_ID, KEY_CODE,
				KEY_NAME, KEY_CO_NA, KEY_ADDRESS, KEY_PHONE, KEY_CELLPHONE, KEY_STATUS }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			customer = new Customer(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), 
					cursor.getString(4), cursor.getString(5), cursor.getString(6));
		}
		cursor.close();
		db.close();
		return customer;
	}
	
	public int isRealCustomer(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		int res = 0;
		Cursor cursor = db.query(TABLE_CUSTOMERS, new String[] { KEY_ID, KEY_CODE,
				KEY_NAME, KEY_CO_NA, KEY_ADDRESS, KEY_PHONE, KEY_CELLPHONE, KEY_STATUS }, KEY_CODE + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		if (cursor != null){
			res = cursor.getCount();
		}
		cursor.close();
		db.close();
		return res;
	}
	public Customer getCustomerByCode(String id) {		
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CUSTOMERS, new String[] { KEY_ID, KEY_CODE,
				KEY_NAME, KEY_CO_NA, KEY_ADDRESS, KEY_PHONE, KEY_CELLPHONE, KEY_STATUS }, KEY_CODE + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		Customer customer = null;
		if (cursor.moveToFirst()) {			
			customer = new Customer(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6), cursor.getString(7));
		}
		db.close();
		cursor.close();
		return customer;
	}
		
	// Getting All Customers
	public List<Customer> getAllCustomers() {
		List<Customer> customerList = new ArrayList<Customer>();

		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMERS + " ORDER BY " + KEY_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Customer customer = new Customer();
				customer.setID(Integer.parseInt(cursor.getString(0)));
				customer.setCode(cursor.getString(1));
				customer.setName(cursor.getString(2));
				customer.setContactName(cursor.getString(3));										
				customer.setAddress(cursor.getString(4));
				customer.setPhone(cursor.getString(5));
				customer.setCellPhone(cursor.getString(6));
				customer.setStatus(cursor.getString(7));
				// Adding contact to list
				customerList.add(customer);
			} while (cursor.moveToNext());
		}		
		// return contact list
		cursor.close();
		db.close();
		return customerList;
	}
	
	// Getting All Customers
	public List<Customer> getSearchCustomers(String like) {
		List<Customer> customerList = new ArrayList<Customer>();
	
		//Select All Query
		SQLiteDatabase db = this.getWritableDatabase();
		//Cursor cursor = db.rawQuery(selectQuery, null);
		Cursor cursor = db.query(TABLE_CUSTOMERS, new String[] {KEY_ID, KEY_CODE, KEY_NAME, KEY_CO_NA, KEY_ADDRESS
				, KEY_PHONE, KEY_CELLPHONE, KEY_STATUS}, 
				KEY_NAME + " LIKE '%"+like +"%'", null, null, null, KEY_NAME);
	
		//Log.d("log_tag", "Edit text 000>>>  " + like);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Customer customer = new Customer();
				customer.setID(Integer.parseInt(cursor.getString(0)));
				customer.setCode(cursor.getString(1));
			//	Log.d("log_tag", "Edit text Result>>>  " + cursor.getString(2));
				customer.setName(cursor.getString(2));
				customer.setContactName(cursor.getString(3));										
				customer.setAddress(cursor.getString(4));
				customer.setPhone(cursor.getString(5));
				customer.setCellPhone(cursor.getString(6));
				customer.setStatus(cursor.getString(7));
				// Adding contact to list
				customerList.add(customer);
			} while (cursor.moveToNext());
		}			
		// return contact list
		cursor.close();
		db.close();
		return customerList;
	}
		
	public String[] getAllCustomerNames() {
		int i = 0;
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMERS + " ORDER BY " + KEY_NAME;

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
		cursor.close();
		db.close();
		return customerList;
	}

	// Updating single customer
	public int updateCustomer(Customer customer) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CODE, customer.getCode()); // Customer code
		values.put(KEY_NAME, customer.getName()); // Customer name
		values.put(KEY_CO_NA, customer.getContactName()); // Customer contact name
		values.put(KEY_ADDRESS, customer.getAddress()); // Customer address
		values.put(KEY_PHONE, customer.getPhone()); // Customer phone
		values.put(KEY_CELLPHONE, customer.getCellPhone()); // Customer cell phone
		values.put(KEY_STATUS, customer.getStatus()); // Customer status

		// updating row
		return db.update(TABLE_CUSTOMERS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(customer.getID()) });
	}

	// Deleting single customer
	public void deleteUsr(Customer customer) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CUSTOMERS, KEY_ID + " = ?",
				new String[] { String.valueOf(customer.getID()) });
		db.close();
	}
		
	// Getting customer Count
	public int getCustomersCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CUSTOMERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// return count
		
		cursor.close();
		db.close();
		return cursor.getCount();
	}
}