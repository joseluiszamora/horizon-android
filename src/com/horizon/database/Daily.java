package com.horizon.database;

public class Daily {
	//private variables
	int _id;
	int _id_web;
	int _id_transaction;
	int _id_customer;
	String _transaction_date;
	String _voucher;
	String _type;
	String _ammount;
	String _customer_code;
	String _customer_name;
	String _customer_address;
	String _status;
	
	// constructor
	public Daily(){}
	
	// constructor
	public Daily(int id, int idweb, int idtransaction, int idcustomer, String transactiondate, String voucher, 
			String type, String ammount, String customercode, String customername, String customeraddress, String status ){
		this._id = id;
		this._id_web= idweb;
		this._id_transaction= idtransaction;
		this._id_customer= idcustomer;
		this._transaction_date= transactiondate;
		this._voucher= voucher;
		this._type= type;
		this._ammount= ammount;
		this._customer_code= customercode;
		this._customer_name= customername;
		this._customer_address= customeraddress;
		this._status= status;
	}
	// constructor
	public Daily( int idweb, int idtransaction, int idcustomer, String transactiondate, String voucher, 
			String type, String ammount, String customercode, String customername, String customeraddress, String status ){
		this._id_web= idweb;
		this._id_transaction= idtransaction;
		this._id_customer= idcustomer;
		this._transaction_date= transactiondate;
		this._voucher= voucher;
		this._type= type;
		this._ammount= ammount;
		this._customer_code= customercode;
		this._customer_name= customername;
		this._customer_address= customeraddress;
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
	// getting ID Web
	public int getIDWeb(){
		return this._id_web;
	}
	// setting ID Web
	public void setIDWeb(int id){
		this._id_web = id;
	}
	// getting ID Transaction
	public int getIDTransaction(){
		return this._id_transaction;
	}
	// setting ID Transaction
	public void setIDTransaction(int id){
		this._id_transaction = id;
	}
	// getting ID Customer
	public int getIDCustomer(){
		return this._id_customer;
	}
	// setting ID Customer
	public void setIDCustomer(int id){
		this._id_customer = id;
	}
	// getting Transaction Date
	public String getTransactionDate(){
		return this._transaction_date;
	}
	// setting Transaction Date
	public void setTransactionDate(String transactiondate){
		this._transaction_date = transactiondate;
	}
	// getting Voucher
	public String getVoucher(){
		return this._voucher;
	}
	// setting Voucher
	public void setVoucher(String voucher){
		this._voucher = voucher;
	}
	// getting type
	public String getType(){
		return this._type;
	}
	// setting type
	public void setType(String type){
		this._type = type;
	}
	// getting ammount
	public String getAmmount(){
		return this._ammount;
	}
	// setting ammount
	public void setAmmount(String ammount){
		this._ammount = ammount;
	}
	// getting customer code
	public String getCustomerCode(){
		return this._customer_code;
	}
	// setting customer code
	public void setCustomerCode(String customercode){
		this._customer_code = customercode;
	}
	// getting Customer Name
	public String getCustomerName(){
		return this._customer_name;
	}
	// setting Customer Name
	public void setCustomerName(String customername){
		this._customer_name = customername;
	}
	
	// getting Customer Address
	public String getCustomerAddress(){
		return this._customer_address;
	}
	// setting Customer Address
	public void setCustomerAddress(String customeraddress){
		this._customer_address = customeraddress;
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