package com.horizon.reports;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.lists.listview.DeliveryDetailAdapter;
import com.horizon.lists.listview.TransactionListAdapter;
import com.horizon.main.DeliveryInfoActivity;
import com.ruizmier.horizon.R;

public class DeliveryClosedInfoActivity extends Activity {		
	
	// Database transaction class
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, 1);
	
	// Database transaction detail class
	DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
		
	// Database Product class
	DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(this, "", null, '1');
	
	
	ListView listView;
	List<TransactionDetail> rowItems;
	TransactionListAdapter adapter;
	Customer customer;
	Transaction transactionObject;
	Bundle bundle;
	Integer codeTransaction;
	Double totalPrice = 0.0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.history_last_transaction);
		
		// Session Manager
		SessionManager session = new SessionManager(getApplicationContext());
	    // get user name data from session
        HashMap<String, String> user = session.getUserDetails();
        // Set user name into action bar layout 
        String name = user.get(SessionManager.KEY_NAME);
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
        
        bundle = getIntent().getExtras();
		// Get Bundle Transaction Code
		codeTransaction = Integer.parseInt(bundle.getString("newTransactionCode"));
		
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(codeTransaction);
		
		// Create Customer Object
		customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
		
		// price
		//totalPrice = dbTransDetail.getTotalPriceTransaction(codeTransaction);
		 
		// Define TextViews
		TextView txtClientName = (TextView)findViewById(R.id.TransDetailInfoCustomName);
		TextView txtClientAddress = (TextView)findViewById(R.id.txtClientAddress);
		TextView txtTotalPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		
		txtClientName.setText(customer.getName());
        txtClientAddress.setText(customer.getAddress());  
        
        try {
        	totalPrice = dbTransDetail.getTotalPriceTransaction(codeTransaction);
    		//totalPrice += Double.valueOf(dbTransDetail.getTotalPriceTransactionDelivery(codeTransaction));
		} catch (Exception e) {
			totalPrice += 0.0;
		}
        txtTotalPrice.setText(String.format("%.2f", totalPrice));
        
        
        
        // make listransaction transaction
		listView = (ListView) findViewById(R.id.contentlisttransdetails);	
		
		Log.d("log_tag", "CodexxZZZZ " + codeTransaction);
 	    rowItems = dbTransDetail.getAllTransactionDetailsForThisDeliveryIDWEB(codeTransaction);
 	    
 	    
 	   
		//rowItems = dbTransDetail.getAllTransactionDetails();
 	    DeliveryDetailAdapter adapter = new DeliveryDetailAdapter(DeliveryClosedInfoActivity.this, rowItems);
 	    listView.setAdapter(adapter);
	}
	
	
	//** Pressed return button **// 
	
		public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	Intent intentViewTransaction = new Intent(DeliveryClosedInfoActivity.this, TabsHistoryActivity.class);
	    		startActivity(intentViewTransaction);
	    		finish();
	         }
	        return super.onKeyDown(keyCode, event);
	    }
}