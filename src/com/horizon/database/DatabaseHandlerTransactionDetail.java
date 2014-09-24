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

public class DatabaseHandlerTransactionDetail extends SQLiteOpenHelper{
	// All Static variables
	
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Transaction table name
	private static final String TABLE_TRANSACTION = "detailTransactions";

	// Contacts Table Columns names
	private static final String KEY_ID = "idDetalTransaction";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_ID_TR = "idTransaction";
	private static final String KEY_PR = "codeProduct";
	private static final String KEY_NA_PR = "nameProduct";
	private static final String KEY_PR_PR = "priceProduct";
	private static final String KEY_QUANTITY = "cantidad";
	private static final String KEY_STATUS = "status";
	private static final String KEY_PR_TO = "priceTotal";
	private static final String KEY_OBS = "obs";
	private static final String KEY_TYPE = "type";
	private static final String KEY_LINE = "codeLine";
			
	public DatabaseHandlerTransactionDetail(Context context, String name,CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_ID_TR + " INTEGER," + KEY_PR + " TEXT," + KEY_NA_PR + " TEXT," + KEY_PR_PR + " DOUBLE,"
				+ KEY_QUANTITY + " INTEGER," + KEY_STATUS + " TEXT," + KEY_PR_TO + " DOUBLE," + KEY_OBS + " TEXT," + KEY_TYPE + " TEXT," + KEY_LINE + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
		// Create tables again
		onCreate(db);
	}
	public void CreateTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " TEXT," + KEY_ID_TR + " INTEGER," + KEY_PR + " TEXT," + KEY_NA_PR + " TEXT," + KEY_PR_PR + " DOUBLE,"
				+ KEY_QUANTITY + " INTEGER," + KEY_STATUS + " TEXT," + KEY_PR_TO + " DOUBLE," + KEY_OBS + " TEXT," + KEY_TYPE + " TEXT," + KEY_LINE + " TEXT" + ")";
		db.execSQL(CREATE_TRANSACTION_TABLE);		
	}
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getWritableDatabase();
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
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	* 	
	*/
					
	// Adding new transaction	
	public void addTransactionDetail(TransactionDetail transactionDetail) {		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, transactionDetail.getIDWeb()); // Transaction Id
		values.put(KEY_ID_TR, transactionDetail.getIdTransaction()); // Transaction id
		values.put(KEY_PR, transactionDetail.getCodeProduct()); // Product
		values.put(KEY_NA_PR, transactionDetail.getNameProduct()); // Name Product
		values.put(KEY_PR_PR, transactionDetail.getPriceProduct()); // Price Product
		values.put(KEY_QUANTITY, transactionDetail.getQuantity()); // Transaction quantity
		values.put(KEY_STATUS, transactionDetail.getStatus()); // Transaction Status
		values.put(KEY_PR_TO, transactionDetail.getPriceTotal()); // Transaction Total Price
		values.put(KEY_OBS, transactionDetail.getObs()); // Transaction Obs
		values.put(KEY_TYPE, transactionDetail.getType()); // Transaction Obs
		values.put(KEY_LINE, transactionDetail.getCodeLine()); // Transaction Line

		db.insert(TABLE_TRANSACTION, null, values);		
		db.close(); // Closing database connection
	}
	
	// Verify is already exist detail transaction product for this 
	public TransactionDetail getTransactionDetailIfExist(int idtransaction, String codeproduct) {	
		SQLiteDatabase db = this.getReadableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM detailTransactions td " +
				"WHERE td.idTransaction = ? AND td.codeProduct=? AND td.status=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idtransaction), String.valueOf(codeproduct), "creado"});

		if (cursor != null && cursor.moveToFirst()){
			TransactionDetail transactionDetail = new TransactionDetail(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
					Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), 
					Double.valueOf(cursor.getString(5)) , Integer.parseInt(cursor.getString(6)),
					cursor.getString(7), Double.valueOf(cursor.getString(8)), cursor.getString(9), cursor.getString(10), cursor.getString(11));
			return transactionDetail;
		}else{		
			return null;
		}							
	}
	
	// Verify is already exist detail transaction product Bonus 
	public TransactionDetail getTransactionDetailIfExistBonus(int idtransaction, String codeproduct) {	
		SQLiteDatabase db = this.getReadableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM detailTransactions td " +
				"WHERE td.idTransaction = ? AND td.codeProduct=? AND td.status=? AND td.type=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idtransaction), String.valueOf(codeproduct), "creado", "bonus"});
		
		if (cursor != null && cursor.moveToFirst()){
			TransactionDetail transactionDetail = new TransactionDetail(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
					Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), 
					Double.valueOf(cursor.getString(5)) , Integer.parseInt(cursor.getString(6)),
					cursor.getString(7), Double.valueOf(cursor.getString(8)), cursor.getString(9), cursor.getString(10), cursor.getString(11));
			return transactionDetail;
		}else{		
			return null;
		}							
	}
	
	// Getting All Transactions
	public List<TransactionDetail> getAllTransactionDetails() {
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list			
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	// Getting All Transactions
	public List<TransactionDetail> getAllTransactionDetailsForThisTransaction(int idTransaction) {
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM Transactions t, detailTransactions td " +
		//		"WHERE t.idTransaction = td.idTransaction AND t.estado = 'creado' AND td.status = 'creado' " +
				"WHERE t.idTransaction = td.idTransaction " +
				" AND t.idTransaction=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	
	// Getting All Transactions
	public List<TransactionDetail> getAllTransactionDetailsForThisTransactionPending(int idTransaction) {
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.estado = 'pending' AND td.status = 'creado' " +
		//		"WHERE t.idTransaction = td.idTransaction " +
				" AND t.idTransaction=?";			
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	// Getting All Transactions NORMAL (no bonus)
	public List<TransactionDetail> getAllTransactionDetailsForThisTransactionPendingNoBonus(int idTransaction) {
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.estado = 'pending' AND td.status = 'creado' AND td.type = 'normal' " +
		//		"WHERE t.idTransaction = td.idTransaction " +
				" AND t.idTransaction=?";			
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	
	// Getting All Transactions
	public List<TransactionDetail> getAllTransactionDetailsForThisTransactionSelected(int idTransaction, String value) {
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.estado = '"+ value +"' AND td.status = '"+ value +"' " +
				" AND t.idTransaction=?";			
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	
	// Getting All Transactions
	public List<TransactionDetail> getAllTransactionDetailsForThisDelivery(int idTransaction) {	
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_TR, KEY_PR, KEY_NA_PR, KEY_PR_PR, KEY_QUANTITY, KEY_STATUS, KEY_PR_TO, KEY_OBS, KEY_TYPE, KEY_LINE}, KEY_ID_TR + "=?",
				new String[] { String.valueOf(idTransaction) }, null, null, null, null);	
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));
				// Adding contact to list
				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	/*TABLE_TRANSACTION = "detailTransactions";

	// Contacts Table Columns names
	private static final String KEY_ID = "idDetalTransaction";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_ID_TR = "idTransaction";
	private static final String KEY_PR = "codeProduct";
	private static final String KEY_NA_PR = "nameProduct";
	private static final String KEY_PR_PR = "priceProduct";
	private static final String KEY_QUANTITY = "cantidad";
	private static final String KEY_STATUS = "status";
	private static final String KEY_PR_TO = "priceTotal";
	private static final String KEY_OBS = "obs";
	*/
	
	// Getting All Transactions
		public List<TransactionDetail> getAllTransactionDetailsForThisDeliveryIDWEB(int idTransaction) {
			List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
			SQLiteDatabase db = this.getWritableDatabase();
			
			Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
			KEY_ID_WEB, KEY_ID_TR, KEY_PR, KEY_NA_PR, KEY_PR_PR, KEY_QUANTITY, KEY_STATUS, KEY_PR_TO, KEY_OBS, KEY_TYPE, KEY_LINE}, KEY_ID_TR + "=?",
			new String[] { String.valueOf(idTransaction) }, null, null, null, null);	
			
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					TransactionDetail transactionDetail = new TransactionDetail();
					transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
					transactionDetail.setIDWeb(cursor.getString(1));
					transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
					transactionDetail.setCodeProduct(cursor.getString(3));
					transactionDetail.setNameProduct(cursor.getString(4));
					transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
					transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
					transactionDetail.setStatus(cursor.getString(7));
					transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
					transactionDetail.setObs(cursor.getString(9));
					transactionDetail.setType(cursor.getString(10));
					transactionDetail.setCodeLine(cursor.getString(11));
					// Adding contact to list
					transactionDetailList.add(transactionDetail);
				} while (cursor.moveToNext());
			}
			return transactionDetailList;
		}
		
	
	// Getting single product
	public TransactionDetail getTransactionDetail(String id) {	
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_TR, KEY_PR, KEY_NA_PR, KEY_PR_PR, KEY_QUANTITY, KEY_STATUS, KEY_PR_TO, KEY_OBS, KEY_TYPE, KEY_LINE}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);		
		if (cursor != null && cursor.moveToFirst()){
			TransactionDetail transactionDetail = new TransactionDetail(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
					Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), 
					Double.valueOf(cursor.getString(5)) , Integer.parseInt(cursor.getString(6)),
					cursor.getString(7), Double.valueOf(cursor.getString(8)), cursor.getString(9), cursor.getString(10), cursor.getString(11));
			return transactionDetail;
		}else{		
			return null;
		}							
	}
	
	// Getting All Bonus for this product
	public List<TransactionDetail> getAllBonusForThisProduct(String idTransaction, String idProduct) {	
		List<TransactionDetail> transactionDetailList = new ArrayList<TransactionDetail>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		/*Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_TR, KEY_PR, KEY_NA_PR, KEY_PR_PR, KEY_QUANTITY, KEY_STATUS, KEY_PR_TO, KEY_OBS, KEY_TYPE}, KEY_ID_TR + "=?",
				new String[] { String.valueOf(idTransaction) }, null, null, null, null);	
		*/
		String MY_QUERY = "SELECT td.idDetalTransaction, td.idWeb, td.idTransaction, td.codeProduct, " +
				"td.nameProduct, td.priceProduct, td.cantidad, td.status, td.priceTotal, td.obs, td.type, td.codeLine FROM detailTransactions td " +
				"WHERE td.idTransaction = '"+ idTransaction +"' AND td.codeProduct = '"+ idProduct +"'  AND td.status = 'creado' ";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});
		
		if (cursor.moveToFirst()) {
			do {
				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setID(Integer.parseInt(cursor.getString(0)));
				transactionDetail.setIDWeb(cursor.getString(1));
				transactionDetail.setIdTransaction(Integer.parseInt(cursor.getString(2)));
				transactionDetail.setCodeProduct(cursor.getString(3));
				transactionDetail.setNameProduct(cursor.getString(4));
				transactionDetail.setPriceProduct(Double.parseDouble(cursor.getString(5)));
				transactionDetail.setQuantity(Integer.parseInt(cursor.getString(6)));
				transactionDetail.setStatus(cursor.getString(7));
				transactionDetail.setPriceTotal(Double.parseDouble(cursor.getString(8)));
				transactionDetail.setObs(cursor.getString(9));
				transactionDetail.setType(cursor.getString(10));
				transactionDetail.setCodeLine(cursor.getString(11));

				transactionDetailList.add(transactionDetail);
			} while (cursor.moveToNext());
		}
		return transactionDetailList;
	}
	
	// Updating single Transaction Detail
	public int updateTransactionDetail(TransactionDetail transactiondetail) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_ID_WEB, transactiondetail.getIDWeb()); // Transaction Id
		values.put(KEY_ID_TR, transactiondetail.getIdTransaction()); // Transaction id
		values.put(KEY_PR, transactiondetail.getCodeProduct()); // Product
		values.put(KEY_NA_PR, transactiondetail.getNameProduct()); // Name Product
		values.put(KEY_PR_PR, transactiondetail.getPriceProduct()); // Price Product
		values.put(KEY_QUANTITY, transactiondetail.getQuantity()); // Transaction quantity
		values.put(KEY_STATUS, transactiondetail.getStatus()); // Transaction Status
		values.put(KEY_PR_TO, transactiondetail.getPriceTotal()); // Transaction Total Price
		values.put(KEY_OBS, transactiondetail.getObs()); // Transaction Obs
		values.put(KEY_TYPE, transactiondetail.getType()); // Transaction type
		values.put(KEY_LINE, transactiondetail.getCodeLine()); // Transaction LINE

		// updating row
		return db.update(TABLE_TRANSACTION, values, KEY_ID + " = ?",
				new String[] { String.valueOf(transactiondetail.getID()) });
	}
	
	// Updating single Transaction Detail
		public int updateAllTransactionDetailsDelivery(int codeTransaction, String value) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			
			values.put(KEY_STATUS, value); // Transaction Status
			// updating row
			return db.update(TABLE_TRANSACTION, values, KEY_ID_TR + " = ?",
					new String[] { String.valueOf(codeTransaction) });			
		}
	
	// Deleting single product
	public void deleteTransactionDetail( int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRANSACTION, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}
	
	// Deleting All Bonus
	public void deleteTransactionDetailBonus( int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRANSACTION, KEY_ID_TR + "=" + id + " and " + KEY_TYPE + "=" + "'bonus'", null);
		db.close();
	}
	// Deleting All Bonus by product
	public void deleteTransactionDetailBonusByProduct( String codeProduct) {
		Log.d("log_tag", "DELETING TRANSACTION BY CODE PRODUCT*********::::::::::::::: " + codeProduct);
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRANSACTION, KEY_PR + "=" + codeProduct + " and " + KEY_TYPE + "=" + "'bonus'", null);
		db.close();
	}
	
	// Deleting All transactions (OjO)
	public void deleteAllTransactions(String status) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_TRANSACTION, null, null);
		} catch (Exception e) {
			// Create tables again
			onCreate(db);
		}
		db.close();
	}
	
	
	// Deleting single transaction
	public void deleteTransactionsFor(String status) {
		SQLiteDatabase db = this.getWritableDatabase();		
		db.delete(TABLE_TRANSACTION, KEY_STATUS + " = ?",
				new String[] { String.valueOf(status) });
		db.close();
	}
	
	// Deleting all transactions details for this transaction
	public void closeAllTransactionDetailForThisTransaction( int idtransaction) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRANSACTION, KEY_ID_TR + " = ?",
				new String[] { String.valueOf(idtransaction) });
		db.close();
	}
	
	public Double getTotalPriceTransaction(int idTransaction){
		SQLiteDatabase db = this.getReadableDatabase();
		Double price = 0.0;
		
		String MY_QUERY = "SELECT SUM(td.priceTotal) FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.idTransaction=?";			
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		//Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(2)});
		
		if (cursor.moveToFirst()) {
			if(!cursor.getString(0).equals(null))
				price += Double.valueOf(cursor.getString(0));
		}
		db.close();
		return price;
	}
	
	public Double getTotalPriceTransactionDelivery(int idTransaction){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Double price = 0.0;
		
		String MY_QUERY = "SELECT SUM(td.priceTotal) FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.idTransaction=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(idTransaction)});
		
		if (cursor.moveToFirst()) {		
			price += Double.valueOf(cursor.getString(0));
		}
		return price;
		
	}	
	
		
	public Double getTotalPriceAllTransactions(String statusTrans, String statusTransDetail){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Double price = 0.0;
		
		String MY_QUERY = "SELECT SUM(td.priceTotal) FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.estado=? AND td.status=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{statusTrans, statusTransDetail});
		
		if (cursor.moveToFirst()) {
			price += Double.valueOf(cursor.getString(0));
		}
		return price;
	}
	
	public Double getTotalPriceAllTransactions(String statusTrans){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Double price = 0.0;
		
		String MY_QUERY = "SELECT SUM(td.priceTotal) FROM Transactions t, detailTransactions td " +
				"WHERE t.idTransaction = td.idTransaction AND t.estado=?";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{statusTrans});
		
		if (cursor.moveToFirst()) {
			price += Double.valueOf(cursor.getString(0));
		}
		return price;
	}
}