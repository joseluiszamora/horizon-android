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

public class DatabaseHandlerTransactions extends SQLiteOpenHelper {
	// All Static variables
	
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Transaction table name
	private static final String TABLE_TRANSACTION = "Transactions";

	// Contacts Table Columns names
	private static final String KEY_ID = "idTransaction";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_CO_CU = "codeCustomer";
	private static final String KEY_OBS = "obs";
	private static final String KEY_STATUS = "estado";
	private static final String KEY_TYPE = "type"; // venta_directa, preventa
	private static final String KEY_PRESTAMO = "prestamo"; // 1:si, o:no
	private static final String KEY_CLIENT_TYPE = "clientType"; // temporal, regular
	private static final String KEY_DA_ST = "fechaHoraInicio";
	private static final String KEY_DA_FI = "fechaHoraFin";
	private static final String KEY_CO_ST = "coordenadaInicio";
	private static final String KEY_CO_FI = "coordenadaFin";
	
	public DatabaseHandlerTransactions(Context context, String name,CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_CO_CU + " TEXT," + KEY_OBS + " TEXT,"
				+ KEY_STATUS + " TEXT," + KEY_TYPE + " TEXT," + KEY_PRESTAMO + " TEXT," + KEY_CLIENT_TYPE + " TEXT," + KEY_DA_ST + " TEXT," + KEY_DA_FI + " TEXT," + KEY_CO_ST + " TEXT," + KEY_CO_FI + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*		// // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		// Create tables again
		onCreate(db);
		 */
	}
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_CO_CU + " TEXT," + KEY_OBS + " TEXT,"
				+ KEY_STATUS + " TEXT," + KEY_TYPE + " TEXT," + KEY_PRESTAMO + " TEXT," + KEY_CLIENT_TYPE + " TEXT," + KEY_DA_ST + " TEXT," + KEY_DA_FI + " TEXT," + KEY_CO_ST + " TEXT," + KEY_CO_FI + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);
	}
	
	//Remake all the table, Risk!!!!
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
	
	// Deleting All transactions (OjO)
	public void deleteAllTransactions(String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_TRANSACTION, null, null);
		} catch (Exception e) {
			Log.d("log_tag", "No existe la tabla transactions");
			onCreate(db);
		}
		db.close();
	}
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	 * @return 
	*/
	
	// Adding new transaction	
	public Long addTransaction(Transaction transaction) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, transaction.getIDWeb()); // Transaction Id
		values.put(KEY_CO_CU, transaction.getCodeCustomer()); // Transaction code customer
		values.put(KEY_OBS, transaction.getObs()); // Transaction obs
		values.put(KEY_STATUS, transaction.getStatus()); // Transaction status
		values.put(KEY_TYPE, transaction.getType()); // Transaction type
		values.put(KEY_PRESTAMO, transaction.getPrestamo()); // is prestamo?
		values.put(KEY_CLIENT_TYPE, transaction.getClientType()); // Transaction type
		values.put(KEY_DA_ST, transaction.getTimeStart()); // Transaction time start
		values.put(KEY_DA_FI, transaction.getTimeFinish()); // Transaction time finish
		values.put(KEY_CO_ST, transaction.getCoordinateStart()); // Transaction coordinate start
		values.put(KEY_CO_FI, transaction.getCoordinateFinish()); // Transaction coordinate finish

		// Inserting Row
		long id = db.insert(TABLE_TRANSACTION, null, values);
		//db.insert(TABLE_TRANSACTION, null, values);
		db.close(); // Closing database connection
		
		return id;
	}	
	
	// Getting All Transactions created status = creado .... transaction section
	public List<Transaction> getAllTransactions() {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'creado'";
		//String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + "";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));
				
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return transactionList;
	}
	
	//public int countTransactions(String status){
	//	return 0;
	//}
	
	
	// Getting All Transactions created status = conciliado .... transaction section
	public List<Transaction> getAllTransactionsClosed() {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'conciliado'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));
							
								
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transactionList;
	}
	
	// Getting All TransactionsDelivery
	public List<Transaction> getAllTransactionsDelivery() {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'por_entregar'" + " ORDER BY " + KEY_ID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));					
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transactionList;
	}
	

	// Getting All Customers
	public List<Transaction> getSearchCustomers(String like) {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] {KEY_ID, KEY_ID_WEB, KEY_CO_CU, KEY_OBS, KEY_STATUS
				, KEY_TYPE, KEY_CLIENT_TYPE, KEY_DA_ST, KEY_DA_FI, KEY_CO_ST, KEY_CO_FI}, 
				KEY_CO_CU + " LIKE '"+like +"%'", null, null, null, KEY_CO_CU);
	
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));					
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return transactionList;
	}
		
	
	public String[] getAllCustomerDeliveryOrder() {
		int i = 0;
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'por_entregar' " + " ORDER BY " + KEY_ID;

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
	
	// Getting All TransactionsDelivery = entregado
	public List<Transaction> getAllTransactionsDeliveryFinished() {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'entregado'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));						
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return transactionList;
	}
	
	
	// Getting All TransactionsDelivery = entregado
	public List<Transaction> getAllTransactionsDeliveryConciliado() {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_STATUS + "= 'entregado_conciliado'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				
				transaction.setID(Integer.parseInt(cursor.getString(0)));
				transaction.setIDWeb(cursor.getString(1));
				transaction.setCodeCustomer(cursor.getString(2));
				transaction.setObs(cursor.getString(3));
				transaction.setStatus(cursor.getString(4));
				transaction.setType(cursor.getString(5));
				transaction.setPrestamo(cursor.getString(6));
				transaction.setClientType(cursor.getString(7));
				transaction.setTimeStart(cursor.getString(8));
				transaction.setTimeFinish(cursor.getString(9));
				transaction.setCoordinateStart(cursor.getString(10));
				transaction.setCoordinateFinish(cursor.getString(11));								
				// Adding contact to list
				transactionList.add(transaction);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return transactionList;
	}
	
	// Getting single product
	public Transaction getTransaction(int id) {	
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_CO_CU, KEY_OBS, KEY_STATUS, KEY_TYPE, KEY_PRESTAMO, KEY_CLIENT_TYPE, KEY_DA_ST, KEY_DA_FI, KEY_CO_ST, KEY_CO_FI }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			
			//Log.d("log_tag", cursor.getString(0)+" - "+cursor.getString(1)+" - "+cursor.getString(2)+" - "+cursor.getString(3)+" - "+
		//cursor.getString(4)+" - "+ cursor.getString(5)+" - "+cursor.getString(6)+" - "+cursor.getString(7)+" - "+cursor.getString(8)+" - "+
			//		cursor.getString(9)+" - "+cursor.getString(10)+" - "+cursor.getString(11));
			
			Transaction transaction = new Transaction(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
					cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));			
			return transaction;
		}else{
			cursor.close();
			db.close();	
			return null;
		}							
	}
	
	
	// Getting single product
	public Transaction getTransactionByIdweb(int id) {		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_CO_CU, KEY_OBS, KEY_STATUS, KEY_TYPE, KEY_PRESTAMO, KEY_CLIENT_TYPE, KEY_DA_ST, KEY_DA_FI, KEY_CO_ST, KEY_CO_FI }, KEY_ID_WEB + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){			
			Transaction transaction = new Transaction(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
					cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 
					cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
			cursor.close();
			db.close();
			return transaction;
		}else{
			db.close();		
			return null;
		}							
	}
	
	// Updating single product
	public int updateTransaction(Transaction transaction) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, transaction.getIDWeb()); // Transaction code customer
		values.put(KEY_CO_CU, transaction.getCodeCustomer()); // Transaction code customer
		values.put(KEY_OBS, transaction.getObs()); // Transaction obs
		values.put(KEY_STATUS, transaction.getStatus()); // Transaction status
		values.put(KEY_TYPE, transaction.getType()); // Transaction type
		values.put(KEY_PRESTAMO, transaction.getPrestamo()); // if prestamo?
		values.put(KEY_CLIENT_TYPE, transaction.getClientType()); // Transaction type
		values.put(KEY_DA_ST, transaction.getTimeStart()); // Transaction time start
		values.put(KEY_DA_FI, transaction.getTimeFinish()); // Transaction time finish
		values.put(KEY_CO_ST, transaction.getCoordinateStart()); // Transaction coordinate start
		values.put(KEY_CO_FI, transaction.getCoordinateFinish()); // Transaction coordinate finish

		// updating row
		return db.update(TABLE_TRANSACTION, values, KEY_ID + " = ?",
		new String[] { String.valueOf(transaction.getID()) });
	}

	// Deleting single transaction
	public void deleteTransaction(int codetransaction) {
		SQLiteDatabase db = this.getWritableDatabase();	
		db.delete(TABLE_TRANSACTION, KEY_ID + " = ?",
				new String[] { String.valueOf(codetransaction) });
		db.close();
	}
	
	// Deleting single transaction
	public void deleteTransactionsFor(String status) {
		SQLiteDatabase db = this.getWritableDatabase();		
		db.delete(TABLE_TRANSACTION, KEY_STATUS + " = ?",
				new String[] { status });
		db.close();
	}
}
