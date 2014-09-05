package com.horizon.database;

public class Volume {
	//private variables
	int _id;
	int _idweb;
	String _desc;
	
	// Empty constructor
	public Volume(){
	}
	
	// constructor
	public Volume(int id, int idweb, String desc){
		this._id = id;
		this._idweb = idweb;
		this._desc = desc;
	}
	
	// constructor
	public Volume(int idweb, String desc){
		this._idweb = idweb;
		this._desc = desc;
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
	public int getIDWeb(){
		return this._idweb;
	}
	
	// setting IDWEB
	public void setIDWeb(int id){
		this._idweb = id;
	}
		
	// getting description
	public String getDescription(){
		return this._desc;
	}
	
	// setting description
	public void setDescription(String desc){
		this._desc = desc;
	}
}