package com.horizon.database;

public class Customer {
	//private variables
	int _id;
	String _code;
	String _name;
	String _contactname;
	String _address;
	String _phone;
	String _cellphone;
	String _status;
	int _rank;
	
	// Empty constructor
	public Customer(){
	}
	
	// constructor
	public Customer(int id, String code, String name, String contact, String address, String phone, String cell, String status, int rank){
		this._id = id;
		this._code = code;
		this._name = name;
		this._contactname = contact;
		this._address = address;
		this._phone = phone;
		this._cellphone = cell;
		this._status = status;
		this._rank = rank;
	}
	
	// constructor
	public Customer(String code, String name, String contact, String address, String phone, String cell, String status, int rank){
		this._code = code;
		this._name = name;			
		this._contactname = contact;
		this._address = address;
		this._phone = phone;
		this._cellphone = cell;
		this._status = status;
		this._rank = rank;
	}
	
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting ID
	public void setID(int id){
		this._id = id;
	}
	
	// getting code
	public String getCode(){
		return this._code;
	}
	
	// setting code
	public void setCode(String code){
		this._code = code;
	}
		
	// getting nombre
	public String getName(){
		return this._name;
	}
	
	// setting nombre
	public void setName(String name){
		this._name = name;
	}
	
	// getting contact name
	public String getContactName(){
		return this._contactname;
	}
	
	// setting contact name
	public void setContactName(String name){
		this._contactname = name;
	}
	
	// getting direccion
	public String getAddress(){
		return this._address;
	}
	
	// setting direccion
	public void setAddress(String address){
		this._address = address;
	}
	
	// getting phone
	public String getPhone(){
		return this._phone;
	}
	
	// setting phone
	public void setPhone(String phone){
		this._phone = phone;
	}
	
	// getting email
	public String getCellPhone(){
		return this._cellphone;
	}
	
	// setting email
	public void setCellPhone(String cell){
		this._cellphone = cell;
	}
	
	// getting Estado
	public String getStatus(){
		return this._status;
	}
	
	// setting estado
	public void setStatus(String status){
		this._status = status;
	}
	
	// getting Rango
	public int getRank(){
		return this._rank;
	}
	
	// setting Rango
	public void setRank(int rank){
		this._rank = rank;
	}
}