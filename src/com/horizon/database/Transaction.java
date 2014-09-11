package com.horizon.database;

public class Transaction {
	//private variables
	int _id;
	String _idweb;
	String _codecustomer;
	String _obs;
	String _status;
	String _type;
	String _prestamo; // 1=si, 0=no
	String _clienttype;
	String _timestart;
	String _timefinish;
	String _coordinatestart;
	String _coordinatefinish;
	String _voucher;
	
	// Empty constructor
	public Transaction() {}
	// constructor
	public Transaction(int id, String idweb, String codecustom, String obs, String status, String type, String prestamo, String clienttype, String timestart, String timefinish, String coordstart, String coordfinish, String voucher){
		this._id = id;
		this._idweb = idweb;
		this._codecustomer = codecustom;
		this._obs = obs;
		this._status = status;
		this._type = type;
		this._prestamo = prestamo;
		this._clienttype = clienttype;
		this._timestart = timestart;
		this._timefinish = timefinish;
		this._coordinatestart = coordstart;
		this._coordinatefinish = coordfinish;
		this._voucher = voucher;
	}
	
	// constructor
	public Transaction(String idweb, String codecustom, String obs, String status, String type, String prestamo, String clienttype, String timestart, String timefinish, String coordstart, String coordfinish, String voucher){
		this._idweb = idweb;
		this._codecustomer = codecustom;
		this._obs = obs;
		this._status = status;
		this._type = type;
		this._prestamo = prestamo;
		this._clienttype = clienttype;
		this._timestart = timestart;
		this._timefinish = timefinish;
		this._coordinatestart = coordstart;
		this._coordinatefinish = coordfinish;
		this._voucher = voucher;
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
		return this._idweb;
	}
	
	// setting IDWeb
	public void setIDWeb(String id){
		this._idweb = id;
	}
		
	// getting CodeCustomer
	public String getCodeCustomer(){
		return this._codecustomer;
	}
	
	// setting CodeCustomer
	public void setCodeCustomer(String id){
		this._codecustomer = id;
	}
	
	
	// getting Observations
	public String getObs(){
		return this._obs;
	}
	
	// setting Observations
	public void setObs(String obs){
		this._obs = obs;
	}
	
	// getting Status
	public String getStatus(){
		return this._status;
	}
	
	// setting Status
	public void setStatus(String stat){
		this._status = stat;
	}
	
	// getting Type
	public String getType(){
		return this._type;
	}
	
	// setting Type
	public void setType(String stat){
		this._type = stat;
	}
	
	// getting Prestamo
	public String getPrestamo(){
		return this._prestamo;
	}
	
	// setting Prestamo
	public void setPrestamo(String prestamo){
		this._prestamo = prestamo;
	}
	
	// getting Client Type
	public String getClientType(){
		return this._clienttype;
	}
	
	// setting Client Type
	public void setClientType(String stat){
		this._clienttype = stat;
	}
	
	// getting Time Start
	public String getTimeStart(){
		return this._timestart;
	}
	
	// setting Time Start
	public void setTimeStart(String date){
		this._timestart = date;
	}
	
	// getting Time Finish
	public String getTimeFinish(){
		return this._timefinish;
	}
	
	// setting Time Finish
	public void setTimeFinish(String date){
		this._timefinish = date;
	}
	
	// getting Coordinate Start
	public String getCoordinateStart(){
		return this._coordinatestart;
	}
	
	// setting Coordinate Start
	public void setCoordinateStart(String cs){
		this._coordinatestart = cs;
	}
	
	// getting Coordinate Finish
	public String getCoordinateFinish(){
		return this._coordinatefinish;
	}
	// setting Coordinate Finish
	public void setCoordinateFinish(String cs){
		this._coordinatefinish = cs;
	}
	
	// getting Voucher
	public String getVoucher(){
		return this._voucher;
	}
	// setting Voucher
	public void setVoucher(String voucher){
		this._voucher = voucher;
	}
}