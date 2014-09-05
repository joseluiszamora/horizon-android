package com.horizon.database;

public class Pay {
	//private variables
	int _id;
	int _id_daily;
	int _id_transaction;
	int _id_customer;
	String _voucher;
	String _ammount;
	String _date;
	String _status;

	// constructor
	public Pay(){}
	
	// constructor
	public Pay(int id, int iddaily, int idtransaction, int idcustomer, String voucher, String ammount, String date, String status ){
		this._id = id;
		this._id_daily = iddaily;
		this._id_transaction = idtransaction;
		this._id_customer = idcustomer;
		this._voucher = voucher;
		this._ammount= ammount;
		this._date= date;
		this._status= status;
	}
	// constructor
	public Pay( int iddaily, int idtransaction, int idcustomer, String voucher, String ammount, String date, String status ){
		this._id_daily = iddaily;
		this._id_transaction = idtransaction;
		this._id_customer = idcustomer;
		this._voucher = voucher;
		this._ammount= ammount;
		this._date= date;
		this._status= status;
	}
	// getting ID
	public int getID(){
		return this._id;
	}
	// setting ID
	public void setID(int id){
		this._id = id;
	}
	// getting ID Daily
	public int getIdDaily(){
		return this._id_daily;
	}
	// setting ID Daily
	public void setIdDaily(int id){
		this._id_daily = id;
	}
	// setting ID Transaction
	public int getIdTransaction(){
		return this._id_transaction;
	}
	// setting ID Transaction
	public void setIdTransaction(int idtransaction){
		this._id_transaction = idtransaction;
	}
	// setting ID Customer
	public int getIdCustomer(){
		return this._id_customer;
	}
	// setting ID Customer
	public void setIdCustomer(int idcustomer){
		this._id_customer = idcustomer;
	}
	// getting voucher
	public String getVoucher(){
		return this._voucher;
	}
	// setting voucher
	public void setVoucher(String voucher){
		this._voucher = voucher;
	}
	// getting ammount
	public String getAmmount(){
		return this._ammount;
	}
	// setting ammount
	public void setAmmount(String ammount){
		this._ammount = ammount;
	}
	// getting date
	public String getDate(){
		return this._date;
	}
	// setting date
	public void setDate(String date){
		this._date = date;
	}
	// getting Estado
	public String getStatus(){
		return this._status;
	}
	// setting estado
	public void setStatus(String status){
		this._status = status;
	}	
}