package com.horizon.database;

public class User {
	//private variables
	int _id;
	String _name;
	String _lastname;
	String _password;
	String _email;
	String _profile;
	int _uservalidate;
	int _loggedin;
	
	// Empty constructor
	public User(){
		
	}
	// constructor
	public User(int id, String name, String lastname, String pass, String mail, String profile, int validate, int login){
		this._id = id;
		this._name = name;
		this._lastname = lastname;
		this._password = pass;
		this._email = mail;
		this._profile = profile;
		this._uservalidate = validate;
		this._loggedin = login;
	}
	
	// constructor
	public User(String name, String lastname, String pass, String mail, String profile, int validate, int login){
		this._name = name;
		this._lastname = lastname;
		this._password = pass;
		this._email = mail;
		this._profile = profile;
		this._uservalidate = validate;
		this._loggedin = login;
	}
	
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting ID
	public void setID(int id){
		this._id = id;
	}
	
	// getting nombre
	public String getName(){
		return this._name;
	}
	
	// setting nombre
	public void setName(String name){
		this._name = name;
	}
	
	// getting apellido
	public String getLastName(){
		return this._lastname;
	}
	
	// setting apellido
	public void setLastName(String lastname){
		this._lastname = lastname;
	}
	
	// getting apellido
	public String getPassword(){
		return this._password;
	}
	
	// setting apellido
	public void setPassword(String pass){
		this._password = pass;
	}
	
	// getting email
	public String getEmail(){
		return this._email;
	}
	
	// setting email
	public void setEmail(String mail){
		this._email = mail;
	}
	
	// getting profile
	public String getProfile(){
		return this._profile;
	}
	
	// setting profile
	public void setProfile(String profile){
		this._profile = profile;
	}
		
	// getting User validate
	public int getUserValidate(){
		return this._uservalidate;
	}
	
	// setting User Validate
	public void setUserValidate(int uv){
		this._uservalidate = uv;
	}
	
	// getting Logged In
	public int getLoggedIn(){
		return this._loggedin;
	}
	
	// setting Logged In
	public void setLoggedIn(int li){
		this._loggedin = li;
	}
	
}
