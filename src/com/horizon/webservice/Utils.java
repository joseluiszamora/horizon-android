package com.horizon.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;

import com.horizon.account.SessionManager;
import com.horizon.reports.CobroListActivity;

public class Utils {
	private Context _context;
	// Session Manager Class
	public SessionManager session;	
	// Session user
	HashMap<String, String> user;
	
	// internet
    InternetDetector internet;
    
    // GPS latitude longitude
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
	
	
	
    public Utils(Context context){
        this._context = context;
        this.setSession();
        // internet
        internet = new InternetDetector(this._context);
        // gps
		gps = new GPSTracker(this._context);
		latitude = gps.getLatitude();
		longitude = gps.getLongitude();
    }
 
    /** DATE AND TIME **/
    // get format Date
    public String getDate(String format){
    	Calendar c = Calendar.getInstance();
    	SimpleDateFormat df = new SimpleDateFormat(format);
    	String formattedDate = df.format(c.getTime());
    	
    	return formattedDate;
    }
    
    /** URL **/
    // get URL WEBSERVICE
    public String url() {
		return "http://www.mariani.bo/horizon-sc/index.php/webservice/";
	}
    
    /** SESSION **/
    public void setSession(){
		// Session Manager
	    session = new SessionManager(this._context);
	    // get user name data from session
	    user = session.getUserDetails();
    }
    
    public void setActionBar(){
    	//TextView actionBarClient = (this._context)(TextView)findViewById(R.id.actionBarClientName);
        //actionBarClient.setText(name);
    }
    
    public String getSessionName() {
    	return user.get(session.KEY_NAME);
	}
    
    public String getSessionMail() {
    	return user.get(session.KEY_EMAIL);
	}
    
    /** internet **/
    public Boolean checkInternet() {
    	return internet.isConnectingToInternet();
	}
    
    
    /** GPS **/
    public double getLatitude() {
    	latitude = gps.getLatitude();
    	return gps.getLatitude();
	}
    
    public double getLongitude() {
    	longitude = gps.getLongitude();
    	return gps.getLongitude();
	}
}