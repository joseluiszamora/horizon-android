package com.horizon.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;

public class StartNewTransaction extends Activity {
		
	
	public Bundle Start (Context context,  String clientcode) {
		
		// verify if the client exist in the Database
		DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(context, "", null, '1');
		Customer customer = new Customer();
		customer = db.getCustomerByCode(clientcode);
		
		if (customer == null){
			Log.d("log_tag", "NO es un cliente valido");
			return null;
		}else{
			Log.d("log_tag", "cliente Habilitado");
			
			// get Client Info
			Bundle bundle = new Bundle();
			bundle.putString("clientCode", customer.getName());
			bundle.putString("clientName", customer.getContactName());
			bundle.putString("clientAddress", customer.getAddress());
			bundle.putString("dateTime", getDateTime());
			return bundle;	     
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getDateTime(){
		//get Date, Hour Now
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		Log.i("TIME:::: ", "Date  => " + formattedDate);
		
		return formattedDate;
	}		
}