package com.horizon.database;

public class TransactionDetail {
	//private variables
	int _id;
	String _idWeb;
	int _idTransaction;
	String _codeProduct;
	String _nameProduct;
	Double _priceProduct;
	int _quantity;
	String _status;
	Double _priceTotal;
	String _obs;
	String _type; //normal, bonus
	String _codeLine;
	
	// Empty constructor
	public TransactionDetail() {	
	}
	// constructor
	public TransactionDetail(int id, String idWeb, int idtransaction, String codeProduct, String nameProduct, 
			Double singleprice, int quantity, String status, Double totalprice, String obs, String type, String codeline){
		this._id = id;
		this._idWeb = idWeb;
		this._idTransaction = idtransaction;
		this._codeProduct = codeProduct;
		this._nameProduct = nameProduct;
		this._priceProduct = singleprice;
		this._quantity = quantity;
		this._status = status;
		this._priceTotal = totalprice;
		this._obs = obs;
		this._type = type;
		this._codeLine = codeline;
	}
	
	// constructor
	public TransactionDetail(String idWeb, int idtransaction, String codeProduct, String nameProduct, 
			Double singleprice, int quantity, String status, Double totalprice, String obs, String type, String codeline){		
		this._idWeb = idWeb;
		this._idTransaction = idtransaction;
		this._codeProduct = codeProduct;
		this._nameProduct = nameProduct;
		this._priceProduct = singleprice;
		this._quantity = quantity;
		this._status = status;
		this._priceTotal = totalprice;
		this._obs = obs;
		this._type = type;
		this._codeLine = codeline;
	}
	
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting ID
	public void setID(int id){
		this._id = id;
	}
	
	// getting IDWeb
	public String getIDWeb(){
		return this._idWeb;
	}
	
	// setting IDWeb
	public void setIDWeb(String id){
		this._idWeb = id;
	}
	
	// getting CodeCustomer
	public int getIdTransaction(){
		return this._idTransaction;
	}
	
	// setting CodeCustomer
	public void setIdTransaction(int id){
		this._idTransaction = id;
	}
	
	// getting Code Product
	public String getCodeProduct(){
		return this._codeProduct;
	}
	
	// setting Code Product
	public void setCodeProduct(String cp){
		this._codeProduct = cp;
	}
	
	// getting Name Product
	public String getNameProduct(){
		return this._nameProduct;
	}
	
	// setting Name Product
	public void setNameProduct(String nameproduct){
		this._nameProduct = nameproduct;
	}		
	
	// getting Price Product
	public Double getPriceProduct(){
		return this._priceProduct;
	}
	
	// setting Price Product
	public void setPriceProduct(Double priceproduct){
		this._priceProduct = priceproduct;
	}	
	
	// getting Status
	public String getStatus(){
		return this._status;
	}
	
	// setting Status
	public void setStatus(String stat){
		this._status = stat;
	}
		
	// getting Quantity
	public int getQuantity(){
		return this._quantity;
	}
	
	// setting Quantity
	public void setQuantity(int qua){
		this._quantity = qua;
	}	
	
	// getting Price Total
	public Double getPriceTotal(){
		return this._priceTotal;
	}
	
	// setting Price Total
	public void setPriceTotal(Double price){
		this._priceTotal = price;
	}
	
	// getting Observations
	public String getObs(){
		return this._obs;
	}
	
	// setting Observations
	public void setObs(String obs){
		this._obs = obs;
	}
	
	// getting Type
	public String getType(){
		return this._type;
	}
	
	// setting Type
	public void setType(String type){
		this._type = type;
	}
	
	// getting Code Line
	public String getCodeLine(){
		return this._codeLine;
	}
	// setting Code Line
	public void setCodeLine(String codeline){
		this._codeLine = codeline;
	}
}