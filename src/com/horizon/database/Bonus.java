package com.horizon.database;

public class Bonus {
	//private variables
	int _id;
	String _type; //line or product
	int _idline_from;
	int _idproduct_from;
	int _quantity_from;
	int _idline_to;
	int _idproduct_to;
	int _quantity_to;
	String _status; // enabled disabled
	
	// Empty constructor
	public Bonus(){
	}
	
	// constructor
	public Bonus(int id, String type, int idlinefrom, int idproductfrom, int quantityfrom, int idlineto, int idproductto, int quantityto, String status){
		this._id = id;
		this._type = type;
		this._idline_from = idlinefrom;
		this._idproduct_from = idproductfrom;
		this._quantity_from = quantityfrom;
		this._idline_to = idlineto;
		this._idproduct_to = idproductto;
		this._quantity_to = quantityto;
		this._status = status;
	}
	
	// constructor
	public Bonus(String type, int idlinefrom, int idproductfrom, int quantityfrom, int idlineto, int idproductto, int quantityto, String status){
		this._type = type;
		this._idline_from = idlinefrom;
		this._idproduct_from = idproductfrom;
		this._quantity_from = quantityfrom;
		this._idline_to = idlineto;
		this._idproduct_to = idproductto;
		this._quantity_to = quantityto;
		this._status = status;
	}

	// getting ID
	public int getID(){
		return this._id;
	}
	// setting ID
	public void setID(int id){
		this._id = id;
	}

	// getting type
	public String getType(){
		return this._type;
	}
	// setting type
	public void setType(String type){
		this._type = type;
	}

	// getting IDLinefrom
	public int getIdLineFrom(){
		return this._idline_from;
	}
	// setting IDLinefrom
	public void setIdLineFrom(int idlinefrom){
		this._idline_from = idlinefrom;
	}
	// getting IDProductfrom
	public int getIdProductFrom(){
		return this._idproduct_from;
	}
	// setting IDProductfrom
	public void setIdProductFrom(int idproductfrom){
		this._idproduct_from = idproductfrom;
	}

	// getting IDQuantityfrom
	public int getQuantityFrom(){
		return this._quantity_from;
	}
	// setting IDQuantityfrom
	public void setQuantityFrom(int idquantityfrom){
		this._quantity_from = idquantityfrom;
	}

	// getting IDLineTo
	public int getIdLineTo(){
		return this._idline_to;
	}
	// setting IDLineTo
	public void setIdLineTo(int idlineto){
		this._idline_to = idlineto;
	}

	// getting IDProductTo
	public int getIdProductTo(){
		return this._idproduct_to;
	}
	// setting IDProductTo
	public void setIdProductTo(int idproductto){
		this._idproduct_to = idproductto;
	}

	// getting IDQuantityTo
	public int getQuantityTo(){
		return this._quantity_to;
	}
	// setting IDQuantityTo
	public void setQuantityTo(int quantityto){
		this._quantity_to = quantityto;
	}

	// getting status
	public String getStatus(){
		return this._status;
	}
	// setting status
	public void setStatus(String status){
		this._status = status;
	}
}