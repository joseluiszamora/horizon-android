package com.horizon.database;

public class LineVolume {
	//private variables
	int _id;
	int _idweb;
	int _idline;
	int _idvolume;
	String _desc;
	
	// Empty constructor
	public LineVolume(){
	}
	
	// constructor
	public LineVolume(int id, int idweb, int idline, int idvolume, String desc){
		this._id = id;
		this._idweb = idweb;
		this._idline = idline;
		this._idvolume = idvolume;
		this._desc = desc;
	}
	
	// constructor
	public LineVolume(int idweb, int idline, int idvolume, String desc){
		this._idweb = idweb;
		this._idline = idline;
		this._idvolume = idvolume;
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
	
	// getting IDLINE
	public int getIDLine(){
		return this._idline;
	}
	
	// setting IDLINE
	public void setIDLine(int id){
		this._idline = id;
	}
	
	// getting IDVOLUME
	public int getIDVolume(){
		return this._idvolume;
	}
	
	// setting IDVOLUME
	public void setIDVolume(int id){
		this._idvolume = id;
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