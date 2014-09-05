package com.horizon.reports;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerLineVolumes;
import com.horizon.database.DatabaseHandlerLines;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.DatabaseHandlerVolumes;
import com.horizon.database.MakeTransaction;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.lists.listview.TransactionListAdapter;
import com.ruizmier.horizon.R;

public class TransactionClosedInfoActivity extends Activity {		

	// Progress Dialog
    private ProgressDialog pDialog;
    
	// Object make transaction
	MakeTransaction transaction = new MakeTransaction();
	
	// Database transaction class
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, 1);
	
	// Database transaction detail class
	DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
	
	// Database Line class
	DatabaseHandlerLines dbLine = new DatabaseHandlerLines(this, "", null, '1');
	
	// Database Volume class
	DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(this, "", null, '1');
	
	// Database Product class
	DatabaseHandlerProducts dbProduct = new DatabaseHandlerProducts(this, "", null, '1');

	// Database Line Volume class
	DatabaseHandlerLineVolumes dbLineVolume = new DatabaseHandlerLineVolumes(this, "", null, '1');
	
	// Database Product class
	DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(this, "", null, '1');
	
	
	
	// GPS latitude longitude
 	double latitude = 0.0;
    double longitude = 0.0;
	
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
		setContentView(R.layout.transaction);
	    		
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
		Log.d("log_tag", "CODE>>>>>>>>" + codeTransaction);
        
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(codeTransaction);
		Log.d("log_tag", "TO >>>>>>>>" + transactionObject.getCodeCustomer());
		
		// Create Product Object
		customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
		Log.d("log_tag", "CU NA >>>>>>>>" + customer.getName());
		
		// make listransaction transaction
		listView = (ListView) findViewById(R.id.contentlisttransdetails);		    
	    rowItems = dbTransDetail.getAllTransactionDetailsForThisTransaction(codeTransaction);
	    adapter = new TransactionListAdapter(TransactionClosedInfoActivity.this, rowItems);
	    listView.setAdapter(adapter);
	    
	    // Define Buttons
		final Button btnadd = (Button)findViewById(R.id.btnConciliar);
		final Button btnSave = (Button)findViewById(R.id.btnSave);
				
		// Define TextViews
		TextView txtClientName = (TextView)findViewById(R.id.TransDetailInfoCustomName);
		TextView txtClientAddress = (TextView)findViewById(R.id.txtClientAddress);
		
		txtClientName.setText(customer.getName());
        txtClientAddress.setText(customer.getAddress());  
        
        // set transaction price
        setPrice();
		
	}	
	
	//** Total Price Management **//
	public void setPrice(){
		TextView txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		txtBottomPrice.setText(String.valueOf(totalPrice));
		Log.d("log_tag", "new price::: " + totalPrice);
	}
		
	//** Pressed return button **// 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	finish();
         }
        return super.onKeyDown(keyCode, event);
    }
}