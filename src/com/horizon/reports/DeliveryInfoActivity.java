package com.horizon.reports;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.MakeTransaction;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.lists.listview.DeliveryDetailAdapter;
import com.horizon.webservice.GPSTracker;
import com.ruizmier.horizon.R;

public class DeliveryInfoActivity extends Activity implements OnItemClickListener {		

	// Dialog
	private static final int DIALOGO_SELECCION = 3;
	// Selected Transaction
	String transactionCode;
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
	// Object make transaction
	MakeTransaction transaction = new MakeTransaction();
	
	// Database transaction class
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, 1);
	
	// Database transaction detail class
	DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
		
	// Database Product class
	DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(this, "", null, '1');
	
	
	
	// GPS latitude longitude
 	double latitude = 0.0;
    double longitude = 0.0;
	
	ListView listView;
	List<TransactionDetail> rowItems;
	
	TransactionDetail selectedTransactionDetail;// transdetail selectedd for edit (cancel, cofirm)
	
	DeliveryDetailAdapter adapter;
	Customer customer;
	Transaction transactionObject;
	Bundle bundle;
	Integer codeTransaction;
	Double totalPrice = 0.0;
	
	String idTransDetail = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.delivery_info);
	    		
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
		codeTransaction = Integer.parseInt(bundle.getString("transaction_id"));
		//String codeT = bundle.getString("transaction_id");
		
		Log.d("log_tag", "CODE>>>>>>>+++++++++++++++= " + codeTransaction);
        
		// Create Transaction Object
		transactionObject = dbTransactions.getTransactionByIdweb(codeTransaction);
		Log.d("log_tag", "TO >>>>>>>>" + transactionObject.getCodeCustomer());
		
		// Create Product Object
		customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
		Log.d("log_tag", "CU NA >>>>>>>>" + customer.getName());
		
		// gps
		GPSTracker gps = new GPSTracker(DeliveryInfoActivity.this);
		latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        
		// make listransaction transaction
		listView = (ListView) findViewById(R.id.contentlisttransdetails);	
	    listView.setOnItemClickListener(this);
	    
	    Log.d("log_tag", "Transaction >>>>>>>>" + codeTransaction);
	    rowItems = dbTransDetail.getAllTransactionDetailsForThisDelivery(codeTransaction);
	    adapter = new DeliveryDetailAdapter(DeliveryInfoActivity.this, rowItems);
	    listView.setAdapter(adapter);
	    
	    // Define Buttons
		
		final Button btnSave = (Button)findViewById(R.id.btnSave);
		final Button btnDeliveryAll = (Button)findViewById(R.id.deliveryAll);
		final Button btnCancelAll = (Button)findViewById(R.id.cancelAll);
				
		// Define TextViews
		TextView txtClientName = (TextView)findViewById(R.id.TransDetailInfoCustomName);
		TextView txtClientAddress = (TextView)findViewById(R.id.txtClientAddress);
		
		txtClientName.setText(customer.getName());
        txtClientAddress.setText(customer.getAddress());  
        
        // set transaction price
        setPrice();

		
		// btn Saves
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeTransaction();
			}		
		});		
		
		btnDeliveryAll.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {				
				deliveryAll();
			}		
		});
		
		btnCancelAll.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {				
				cancelAll();
			}		
		});
	}	
	
	//** Total Price Management **//
	public void setPrice(){
		TextView txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		txtBottomPrice.setText(String.valueOf(totalPrice));
		Log.d("log_tag", "new price::: " + totalPrice);
	}
	
	public void addPrice(double addprice){
		totalPrice += addprice;
		setPrice();
	}
	public void lessPrice(double addprice){
		totalPrice -= addprice;
		setPrice();
	}
	
	
	//** Pressed return button **// 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	 //Toast.makeText(TransactionActivity.this, "bye bye", Toast.LENGTH_SHORT).show();
        	closeTransaction();
         }
        return super.onKeyDown(keyCode, event);
    }

	private void cancelAll() {
		dbTransDetail.updateAllTransactionDetailsDelivery(codeTransaction, "cancelado");
		update();
	}
	private void deliveryAll() {		
		dbTransDetail.updateAllTransactionDetailsDelivery(codeTransaction, "entregado");
		update();
	}
	
	
	private void closeTransaction(){
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Atención");
		builder.setMessage("La Distribución será finalizada.").
		setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				pDialog = new ProgressDialog(DeliveryInfoActivity.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Finalizando Transacción...");
				pDialog.setCancelable(false);
				pDialog.setMax(100);
				
				UpdateInfoAsyncDialog updateWork = new UpdateInfoAsyncDialog();
				updateWork.execute();
			}
		});
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
               }
           }); 
        AlertDialog alert = builder.create();            
        alert.show();
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		idTransDetail = (String) ((TextView) arg1.findViewById(R.id.transaction_id_detail)).getText();
		//final String product_name_trans_details = (String) ((TextView) arg1.findViewById(R.id.tdproduct)).getText();
		//final String total_price_trans_details = (String) ((TextView) arg1.findViewById(R.id.totalPriceTransactionModify)).getText();
		
		selectedTransactionDetail = dbTransDetail.getTransactionDetail(idTransDetail);
		
		showDialog(DIALOGO_SELECCION);
    }
	
	// Select Dialog Create Interface
	@Override
    protected Dialog onCreateDialog(int id) {    	
    	Dialog dialogo = makeSelectionDialog();
    	return dialogo;
    }
    
    private Dialog makeSelectionDialog(){
    	final String[] items = {"Confirmar Entrega", "Entrega Pendiente", "Cancelar Entrega"};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setTitle("Seleccione la acci�n");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opci�n elegida: " + items[item]);    	    	
    	    	if (item == 0){
    	    		selectedTransactionDetail.setStatus("entregado");
    	    		dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
    	    		update();
    	    		
    	    		Toast.makeText(DeliveryInfoActivity.this, "GPS ON 0000" + idTransDetail, Toast.LENGTH_SHORT).show();
    	    	}
				if (item == 1) {
					selectedTransactionDetail.setStatus("por_entregar");
					dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
					update();
					
					Toast.makeText(DeliveryInfoActivity.this, "GPS ON !!!" + idTransDetail, Toast.LENGTH_SHORT).show();
				}
				if (item == 2) {
					selectedTransactionDetail.setStatus("cancelado");
					dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
					update();
					
					Toast.makeText(DeliveryInfoActivity.this, "GPS ON !!!" + idTransDetail, Toast.LENGTH_SHORT).show();
				}
    	    }
    	});    	    	
    	return builder.create();
    }
	
	private void update() { // refresh listview
		rowItems = dbTransDetail.getAllTransactionDetailsForThisDelivery(codeTransaction);
	    adapter = new DeliveryDetailAdapter(DeliveryInfoActivity.this, rowItems);
	    adapter.notifyDataSetChanged();
	    listView.setAdapter(adapter);
	}
	
	// AsyncTask Finish Transaction
	private class UpdateInfoAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
	    
    	@Override
    	protected Boolean doInBackground(Void... params) {    		
    		// set GPS
			transactionObject.setCoordinateFinish(latitude + " ; "+ longitude);
			
			Log.i("COORDINATE:::::::::::::::: ", "Latitude  => " + latitude + "Longitude => " + longitude );
			//get Date, Hour Now
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String formattedDate = df.format(c.getTime());
			Log.i("TIME:::: ", "Date  => " + formattedDate);
			
			transactionObject.setTimeFinish(formattedDate);
			transactionObject.setStatus("entregado");
			dbTransactions.updateTransaction(transactionObject);
			
			Intent i = new Intent(getApplicationContext(), TabsReportActivity.class);
			startActivity(i);
			finish();
			
    		return true;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					UpdateInfoAsyncDialog.this.cancel(true);
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		if(result) {
    			Intent i = new Intent(getApplicationContext(), TabsReportActivity.class);
    			startActivity(i);
    			pDialog.dismiss();
    			finish();
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(DeliveryInfoActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	
}