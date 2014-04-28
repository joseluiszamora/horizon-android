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

public class DatabaseHandlerUsers extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_USERS = "Users";
	
	// Contacts Table Columns names
	private static final String KEY_ID = "idUser";
	private static final String KEY_NAME = "Nombre";
	private static final String KEY_LA_NA = "Apellido";
	private static final String KEY_PASS = "Password";
	private static final String KEY_MAIL = "Email";
	private static final String KEY_PROFILE = "Profile";
	private static final String KEY_US_VA = "UserValidate";
	private static final String KEY_LO_IN = "LoggedIn";
	
	public DatabaseHandlerUsers(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
			+ KEY_LA_NA + " TEXT," + KEY_PASS + " TEXT," + KEY_MAIL + " TEXT," + KEY_PROFILE + " TEXT," + KEY_US_VA + " INTEGER," + KEY_LO_IN + " INTEGER" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		// Create tables again
		onCreate(db);*/
	}
	
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_LA_NA + " TEXT," + KEY_PASS + " TEXT," + KEY_MAIL + " TEXT," + KEY_PROFILE + " TEXT," + KEY_US_VA + " INTEGER," + KEY_LO_IN + " INTEGER" + ")";
			db.execSQL(CREATE_PRODUCTS_TABLE);
	
	}

	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		// Create tables again
		onCreate(db);
		db.close();
	}
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	*/
	
	// Adding new product
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, user.getName()); // User name
		values.put(KEY_LA_NA, user.getLastName()); // User last name
		values.put(KEY_PASS, user.getPassword()); // User pass
		values.put(KEY_MAIL, user.getEmail()); // User mail
		values.put(KEY_PROFILE, user.getProfile()); // User profile
		values.put(KEY_US_VA, user.getUserValidate()); // User User Validate
		values.put(KEY_LO_IN, user.getLoggedIn()); // User Logged In

		// Inserting Row
		db.insert(TABLE_USERS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single User
	User getUser(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
				KEY_NAME, KEY_LA_NA, KEY_PASS, KEY_MAIL, KEY_PROFILE, KEY_US_VA, KEY_LO_IN }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		User user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 
				Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7))); 
		return user;
	}
	
	User getUniqueUser() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
				KEY_NAME, KEY_LA_NA, KEY_PASS, KEY_MAIL, KEY_PROFILE, KEY_US_VA, KEY_LO_IN },null, null, null, null, null, "LIMIT 1");
		if (cursor != null)
			cursor.moveToFirst();
		
		User user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 
				Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7))); 
		return user;
	}
	
	
	
	// Getting All Products
	public List<User> getAllUsers() {
		List<User> userList = new ArrayList<User>();

		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_USERS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				User user = new User();
				user.setID(Integer.parseInt(cursor.getString(0)));
				user.setName(cursor.getString(1));
				user.setLastName(cursor.getString(2));										
				user.setPassword(cursor.getString(3));
				user.setEmail(cursor.getString(4));
				user.setProfile(cursor.getString(5));
				user.setUserValidate(Integer.parseInt(cursor.getString(6)));
				user.setLoggedIn(Integer.parseInt(cursor.getString(7)));
				// Adding contact to list
				userList.add(user);
			} while (cursor.moveToNext());
		}
		// return contact list
		return userList;
	}

	// Updating single product
	public int updateUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, user.getName()); // Product name
		values.put(KEY_LA_NA, user.getLastName()); // Product last name
		values.put(KEY_PASS, user.getPassword()); // Product pass
		values.put(KEY_MAIL, user.getEmail()); // Product mail
		values.put(KEY_PROFILE, user.getProfile()); // Product profile
		values.put(KEY_US_VA, user.getUserValidate()); // User Validate
		values.put(KEY_LO_IN, user.getLoggedIn()); // Logged In

		// updating row
		return db.update(TABLE_USERS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(user.getID()) });
	}

	// Deleting single product
	public void deleteUsr(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USERS, KEY_ID + " = ?",
				new String[] { String.valueOf(user.getID()) });
		db.close();
	}
	
	// Getting products Count
	public int getUsersCount() {
		int Count = 0;
		String countQuery = "SELECT  * FROM " + TABLE_USERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		Count = cursor.getCount();
		db.close();
		return Count;		
	}
}