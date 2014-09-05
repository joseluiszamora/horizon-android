package com.horizon.database;

public class Gps {
	//private variables
		int _id;
		String _latitude;
		String _longitude;
		String _date;
		String _hour;
		
		// Empty constructor
		public Gps(){
		}
		
		// constructor
		public Gps(int id, String latitude, String longitude, String date, String hour){
			this._id = id;
			this._latitude = latitude;
			this._longitude = longitude;
			this._date = date;
			this._hour = hour;
		}
		
		// constructor
		public Gps(String latitude, String longitude, String date, String hour){
			this._latitude = latitude;
			this._longitude = longitude;
			this._date = date;
			this._hour = hour;
		}
		
		// getting ID
		public int getID(){
			return this._id;
		}
		
		// setting ID
		public void setID(int id){
			this._id = id;
		}
		
		// getting Latitude
		public String getLatitude(){
			return this._latitude;
		}
		
		// setting Latitude
		public void setLatitude(String latitude){
			this._latitude = latitude;
		}
			
		// getting Longitude
		public String getLongitude(){
			return this._longitude;
		}
		
		// setting Longitude
		public void setLongitude(String longitude){
			this._longitude = longitude;
		}
		
		// getting Date
		public String getDate(){
			return this._date;
		}
		
		// setting Date
		public void setDate(String date){
			this._date = date;
		}
		
		// getting Hour
		public String getHour(){
			return this._hour;
		}
		
		// setting Hour
		public void setHour(String hour){
			this._hour = hour;
		}
}