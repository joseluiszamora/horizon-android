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

public class DatabaseHandlerProducts extends SQLiteOpenHelper {
		
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Horizon";

	// Contacts table name
	private static final String TABLE_PRODUCTS = "Products";

	// Contacts Table Columns names
	private static final String KEY_ID = "idProduct";
	private static final String KEY_ID_WEB = "idWeb";
	private static final String KEY_ID_LI_VO = "idLineVolume";
	private static final String KEY_NAME = "Nombre";	
	private static final String KEY_PR_UN = "PrecioUnit";
	private static final String KEY_DESC = "Descripcion";
	private static final String KEY_STATUS = "Estado";
	
	public DatabaseHandlerProducts(Context context, String name, CursorFactory factory, int version) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_WEB + " LONG," + KEY_ID_LI_VO + " INTEGER," + KEY_NAME + " TEXT," 
				+ KEY_PR_UN + " DOUBLE," + KEY_DESC + " TEXT," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*		// // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		// Create tables again
		onCreate(db);
*/
	}
	
	/**
	* All CRUD(Create, Read, Update, Delete) Operations
	*/
	
	//Remake all the table, Risk!!!!
	public void clearTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		// Create tables again
		onCreate(db);
	}
	
	// Adding new product
	public void addProduct(Product product) {
		SQLiteDatabase db = this.getWritableDatabase();
	//	Log.d("log_tag", "NOMBRE::::::::::: " + product.getIDWeb());
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, product.getIDWeb()); // Product Id Web
		values.put(KEY_ID_LI_VO, product.getIDLineVolume()); // Product Line Volume
		values.put(KEY_NAME, product.getName()); // Product Nombre		
		values.put(KEY_PR_UN, product.getPrice()); // Product Precio Unitario
		values.put(KEY_DESC, product.getDescription()); // Product Descripcion
		values.put(KEY_STATUS, product.getEstado()); // Product Estado

		// Inserting Row
		db.insert(TABLE_PRODUCTS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single product
	public Product getProduct(String idweb) {
		Log.d("log_tag", "Recibi este ID:::" + idweb);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PRODUCTS, new String[] { KEY_ID,
				KEY_ID_WEB, KEY_ID_LI_VO, KEY_NAME, KEY_PR_UN, KEY_DESC, KEY_STATUS }, KEY_ID_WEB + "=?",
				new String[] { idweb }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			Log.d("log_tag", "Producto existente:::" );
			Log.d("log_tag", "Cursor 0:::" + cursor.getString(0));
			Log.d("log_tag", "Cursor 1:::" + cursor.getString(1));
			Log.d("log_tag", "Cursor 2:::" + cursor.getString(2));
			Log.d("log_tag", "Cursor 3:::" + cursor.getString(3));
			Log.d("log_tag", "Cursor 4:::" + cursor.getString(4));
			Log.d("log_tag", "Cursor 5:::" + cursor.getString(5));
			Log.d("log_tag", "Cursor 6:::" + cursor.getString(6));
			
			Product product = new Product(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)), cursor.getString(3), Double.parseDouble(cursor.getString(4)),
					cursor.getString(5), cursor.getString(6));
			return product;
		}else{
			Log.d("log_tag", "Epic Fail:::" + idweb);
			return null;
		}							
	}
	
	// Getting All Products
	public List<Product> getAllProducts() {
		List<Product> productList = new ArrayList<Product>();
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " ORDER BY " + KEY_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Product product = new Product();				
				product.setID(Integer.parseInt(cursor.getString(0)));
				product.setIDWeb(Long.parseLong(cursor.getString(1)));
				product.setIDLineVolume(Integer.parseInt(cursor.getString(2)));
				product.setName(cursor.getString(3));				
				product.setPrice(Double.parseDouble(cursor.getString(4)));
				product.setDescription(cursor.getString(5));
				product.setEstado(cursor.getString(6));
				
				// Adding contact to list
				productList.add(product);
			} while (cursor.moveToNext());
		}
		
		return productList;
	}

	
	public String[] getAllProductNames() {
		int i = 0;
		//Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " ORDER BY " + KEY_NAME;

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
	
	// Getting All Products For Search
		public List<Product> getAllSearchProducts(String like) {
			List<Product> productList = new ArrayList<Product>();

			SQLiteDatabase db = this.getWritableDatabase();
			
			//Cursor cursor = db.rawQuery(selectQuery, null);
			Cursor cursor = db.query(TABLE_PRODUCTS, new String[] {KEY_ID, KEY_ID_WEB, KEY_ID_LI_VO, KEY_NAME, KEY_PR_UN
					, KEY_DESC, KEY_STATUS, KEY_STATUS}, 
					KEY_NAME + " LIKE '%"+like +"%'", null, null, null, KEY_NAME);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Product product = new Product();				
					product.setID(Integer.parseInt(cursor.getString(0)));
					product.setIDWeb(Long.parseLong(cursor.getString(1)));
					product.setIDLineVolume(Integer.parseInt(cursor.getString(2)));
					product.setName(cursor.getString(3));				
					product.setPrice(Double.parseDouble(cursor.getString(4)));
					product.setDescription(cursor.getString(5));
					product.setEstado(cursor.getString(6));
					
					// Adding contact to list
					productList.add(product);
				} while (cursor.moveToNext());
			}
			
			return productList;
		}		
	
	// Getting All Products for this lineVolume
	public List<Product> getAllProductsForLineVolume(int line, int volume) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Product> productList = new ArrayList<Product>();
		Log.d("log_tag", "QUERY>> " + " Line " + line+ " Volume " + volume);
		String MY_QUERY = "SELECT * FROM Products P, LineVolume lv " +
						"WHERE lv.idLine=" +line+ " AND lv.idVolume=" + volume + " AND P.idLineVolume = lv.idWeb";
		Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});
		Log.d("log_tag", "QUERY>> " + MY_QUERY);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			Log.d("log_tag", "match :):):):)");
			do {
				Product product = new Product();				
				product.setID(Integer.parseInt(cursor.getString(0)));
				product.setIDWeb(Long.parseLong(cursor.getString(1)));
				product.setIDLineVolume(Integer.parseInt(cursor.getString(2)));
				product.setName(cursor.getString(3));				
				product.setPrice(Double.parseDouble(cursor.getString(4)));
				product.setDescription(cursor.getString(5));
				product.setEstado(cursor.getString(6));
				
				Log.d("log_tag", "PRODUCTOS:::::: code> " + cursor.getString(1) + " Name> "+ cursor.getString(3));
				// Adding contact to list
				productList.add(product);
			} while (cursor.moveToNext());
		}else{
			Log.d("log_tag", "No match :):):):)");
		}
		
		return productList;
	}
	
	// Updating single product
	public int updateProduct(Product product) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ID_WEB, product.getIDWeb()); // Product Id Web
		values.put(KEY_ID_LI_VO, product.getIDLineVolume()); // Product Line Volume
		values.put(KEY_NAME, product.getName()); // Product Nombre		
		values.put(KEY_PR_UN, product.getPrice()); // Product Precio Unitario
		values.put(KEY_DESC, product.getDescription()); // Product Descripcion
		values.put(KEY_STATUS, product.getEstado()); // Product Estado

		// updating row
		return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(product.getID()) });
	}

	// Deleting single product
	public void deleteProduct(Product product) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
				new String[] { String.valueOf(product.getID()) });
		db.close();
	}
	
	// Getting products Count
	public int getProductsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		// return count
		return cursor.getCount();
	}
}