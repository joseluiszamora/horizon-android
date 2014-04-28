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

import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.Transaction;
import com.horizon.lists.listview.historyTransactionAdapter;
import com.ruizmier.horizon.R;

public class TabTransactionsClosedActivity extends Activity implements OnItemClickListener {			
	// Selected Transaction
	String transactionCode;
	
	List<Transaction> rowItemsTransaction;
	historyTransactionAdapter adapterTransaction;
	ListView listView;
	Double totalPrice = 0.0;
	
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, '1');
	// Database transaction detail class
	DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list_last_transactions);                
        
	    // make listransaction transaction
	    listView = (ListView) findViewById(R.id.contentlistlasttransactions);
	    listView.setOnItemClickListener(this);
	    rowItemsTransaction = dbTransactions.getAllTransactionsClosed();
	   
	    for (Transaction trans : rowItemsTransaction) {
	    	try {
	    		totalPrice += Double.valueOf(dbTransDetail.getTotalPriceTransaction(trans.getID()));
			} catch (Exception e) {
				totalPrice += 0.0;
			}
        }
	    
	    adapterTransaction = new historyTransactionAdapter(this, rowItemsTransaction);
	    listView.setAdapter(adapterTransaction);

	    // Set TotalPriceTransaction
	    TextView txtTotalPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		
	    txtTotalPrice.setText(String.format("%.2f", totalPrice));
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		transactionCode = (String) ((TextView) view.findViewById(R.id.transaction_id)).getText();
		
		Intent intentViewTransaction = new Intent(TabTransactionsClosedActivity.this, HistoryTransactionClosedInfo.class);
		Bundle bundle = new Bundle();
		bundle.putString("newTransactionCode", transactionCode);
		intentViewTransaction.putExtras(bundle);
		startActivity(intentViewTransaction);
		finish();
	}		
}