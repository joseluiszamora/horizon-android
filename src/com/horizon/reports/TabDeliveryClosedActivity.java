package com.horizon.reports;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.Transaction;
import com.horizon.lists.listview.LastTransactionAdapter;
import com.ruizmier.horizon.R;

public class TabDeliveryClosedActivity extends Activity implements OnItemClickListener {
	// Selected Transaction
	String transactionCode;
	
	List<Transaction> rowItemsTransaction;
	LastTransactionAdapter adapterTransaction;
	Transaction transactionObject;
	Customer customer;
	Double totalPrice = 0.0;
	
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, '1');
	DatabaseHandlerTransactionDetail dbTransactionDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
	ListView listView;
	List<Transaction> rowItems;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list_last_transactions);
        
        
        // make listransaction transaction
	    listView = (ListView) findViewById(R.id.contentlistlasttransactions);	
	    listView.setOnItemClickListener(this);
	    rowItemsTransaction = dbTransactions.getAllTransactionsDeliveryConciliado();
	    
	    for (Transaction trans : rowItemsTransaction) {
	    	try {
	    		totalPrice += Double.valueOf(dbTransactionDetail.getTotalPriceTransaction(trans.getID()));
			} catch (Exception e) {
				totalPrice += 0.0;
			}
        }
	    
	    adapterTransaction = new LastTransactionAdapter(this, rowItemsTransaction);
	    listView.setAdapter(adapterTransaction);	    
	    
	    // Set TotalPriceTransaction
	    TextView txtTotalPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
	    txtTotalPrice.setText(String.format("%.2f", totalPrice));
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		transactionCode = (String) ((TextView) view.findViewById(R.id.transaction_id)).getText();
		Log.d("log_tag", "TRANSAccion click" + transactionCode);
		
		Intent intentViewTransaction = new Intent(TabDeliveryClosedActivity.this, DeliveryClosedInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("newTransactionCode", transactionCode);
		intentViewTransaction.putExtras(bundle);
		startActivity(intentViewTransaction);
		finish();
		
	}	  
}