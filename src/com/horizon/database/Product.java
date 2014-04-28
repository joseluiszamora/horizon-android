package com.horizon.database;

public class Product {
	//private variables
	int _id;
	long _idweb;
	int _linevolume;
	String _name;	
	Double _price;
	String _desc;
	String _status;
	
	// Empty constructor
	public Product(){
		
	}
	// constructor
	public Product(int id, long idweb, int linevolume, String name, Double price, String desc, String status){
		this._id = id;
		this._idweb = idweb;
		this._linevolume = linevolume;
		this._name = name;
		this._price = price;
		this._desc = desc;
		this._status = status;
	}
	
	// constructor
	public Product(long idweb, int linevolume, String name, Double price, String desc, String status){
		this._idweb = idweb;
		this._linevolume = linevolume;
		this._name = name;
		this._price = price;
		this._desc = desc;
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
	
	// getting IDWEB
	public long getIDWeb(){
		return this._idweb;
	}
	
	// setting IDWEB
	public void setIDWeb(long id){
		this._idweb = id;
	}
	
	// getting IDLINEVOLMUE
	public int getIDLineVolume(){
		return this._linevolume;
	}
	
	// setting IDLINEVOLMUE
	public void setIDLineVolume(int id){
		this._linevolume = id;
	}
		
	// getting nombre
	public String getName(){
		return this._name;
	}
	
	// setting nombre
	public void setName(String name){
		this._name = name;
	}
	
	// getting Unidad de Medida
	public String getPrice(){
		return Double.toString(this._price);
	}
	
	// setting Unidad de Medida
	public void setPrice(Double price){
		this._price = price;
	}
	
	// getting descripcion
	public String getDescription(){
		return this._desc;
	}
	
	// setting descripcion
	public void setDescription(String desc){
		this._desc = desc;
	}
	
	// getting estado
	public String getEstado(){
		return this._status;
	}
	
	// setting estado
	public void setEstado(String status){
		this._status = status;
	}
}
