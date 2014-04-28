package com.horizon.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.horizon.webservice.InternetDetector;
import com.horizon.webservice.JSONParser;
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
	
	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	// internet object
	final InternetDetector internet = new InternetDetector(this);
	
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
	String userMail;
	
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
        userMail = user.get(SessionManager.KEY_EMAIL);
        
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
		        
		bundle = getIntent().getExtras();
		// Get Bundle Transaction Code
		codeTransaction = Integer.parseInt(bundle.getString("codeTransaction"));
		
		Log.d("log_tag", "CODE>>>>>>>>** " + codeTransaction);
        
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(codeTransaction);
		Log.d("log_tag", "TO >>>>>>>>" + transactionObject.getCodeCustomer());				
		
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
		
		// Create Customer Object
		try {
			customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
			Log.d("log_tag", "CU NA >>>>>>>>" + customer.getName());
			
			txtClientName.setText(customer.getName());
	        txtClientAddress.setText(customer.getAddress());
		} catch (Exception e) {
			txtClientName.setText("Nombre de Cliente no disponible");
	        txtClientAddress.setText("Direccion de Cliente no disponible");
		}
        
        // set transaction price
        //setPrice();
		TextView txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		try {
        	totalPrice = dbTransDetail.getTotalPriceTransaction(codeTransaction);
    		//totalPrice += Double.valueOf(dbTransDetail.getTotalPriceTransactionDelivery(codeTransaction));
		} catch (Exception e) {
			totalPrice += 0.0;
		}
		txtBottomPrice.setText(String.format("%.2f", totalPrice));

		
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
	/*public void setPrice(){
		TextView txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		txtBottomPrice.setText(String.format("%.2f", totalPrice));
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
	*/
	
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
		builder.setTitle("Atención ");
		builder.setMessage("La Distribución será finalizada.").
		setPositiveButton("Aceptar ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/*pDialog = new ProgressDialog(DeliveryInfoActivity.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Finalizando Transaccion...");
				pDialog.setCancelable(false);
				pDialog.setMax(100);
				
				UpdateInfoAsyncDialog updateWork = new UpdateInfoAsyncDialog();
				updateWork.execute();*/
				
				
				
				
				
				transactionObject.setCoordinateFinish(latitude + " ; "+ longitude);
				
				//get Date, Hour Now
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String formattedDate = df.format(c.getTime());
				
				transactionObject.setTimeFinish(formattedDate);
				transactionObject.setStatus("entregado");
				dbTransactions.updateTransaction(transactionObject);
				Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
    			startActivity(i);
    			finish();
				
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
		Log.d("log_tag", "ID>>>>> : " + idTransDetail);
		selectedTransactionDetail = dbTransDetail.getTransactionDetail(idTransDetail);
		Log.d("log_tag", "ID>>>>> : " + selectedTransactionDetail);
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
    	
    	builder.setTitle("Seleccione la acción:");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opción elegida: " + items[item]);    	    	
    	    	if (item == 0){
    	    		selectedTransactionDetail.setStatus("entregado");
    	    		dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
    	    		update();
    	    	}
				if (item == 1) {
					selectedTransactionDetail.setStatus("por_entregar");
					dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
					update();
				}
				if (item == 2) {
					selectedTransactionDetail.setStatus("cancelado");
					dbTransDetail.updateTransactionDetail(selectedTransactionDetail);
					update();
				}
    	    }
    	});    	    	
    	return builder.create();
    }
	
	private void update() { // refresh listview
		/*rowItems = dbTransDetail.getAllTransactionDetailsForThisDelivery(codeTransaction);
	    adapter = new DeliveryDetailAdapter(DeliveryInfoActivity.this, rowItems);
	    adapter.notifyDataSetChanged();
	    listView.setAdapter(adapter);
	    */
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
					
			Log.d("log_tag", "ID>>>>>################ : " + transactionObject.getID());
			
			if(internet.isConnectingToInternet()){
				List<TransactionDetail> listTransactionDetail  = dbTransDetail.
	    				getAllTransactionDetailsForThisDelivery(transactionObject.getID());

	    		JSONObject objectAllTransactionsDetails = new JSONObject();
	    		
				//create json transaction details Main Object
	    		for(int i = 0; i < listTransactionDetail.size(); i++) {
	    			TransactionDetail trans = listTransactionDetail.get(i);
	    			Log.d("log_tag", "Anadiendo producto:::: " + trans.getNameProduct());
	    			
	    			JSONArray jsonArray = new JSONArray();
	    			jsonArray.put(trans.getIDWeb());
	    			jsonArray.put(trans.getStatus());
	                jsonArray.put(trans.getObs());
	                try {
	                	Log.d("log_tag", "Add product:::: ");
	                	objectAllTransactionsDetails.put("transaction"+i, jsonArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
	    		}
	    		Log.d("log_tag: ", "DDDDDDDDD");
	    		// create Main json Object for this transaction
	    		JSONObject objectTransaction = new JSONObject();
				try {
					Log.d("log_tag", "NNNNNNNNNNNNNNNNNNNN::: " + transactionObject.getCodeCustomer());
					objectTransaction.put("TransactionsArray", objectAllTransactionsDetails);
					objectTransaction.put("idWeb", transactionObject.getIDWeb());
					objectTransaction.put("coordinateStart", transactionObject.getCoordinateStart());
					objectTransaction.put("coordinateFinish", transactionObject.getCoordinateFinish());
					objectTransaction.put("timeStart", transactionObject.getTimeStart());
					objectTransaction.put("timeFinish", transactionObject.getTimeFinish());
					objectTransaction.put("obs", transactionObject.getObs());
					objectTransaction.put("status", transactionObject.getStatus());
					objectTransaction.put("userMail", userMail);
				} catch (JSONException e) {					
					e.printStackTrace();
					Log.d("log_tag: ", "GGGGGGGGGGGG");
				}
				
				// Building Parameters
	    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
	    		paramsTransaction.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	    		
	    		
	    		// getting JSON string from URL
	    		//String returnJson = jsonParser.makeHttpRequest("http://www.ruizmier.com/systems/horizon/webservice/update_transaction", "POST", paramsTransaction);
	    		//String returnJson = jsonParser.makeHttpRequest("http://mariani.bo/pruebas/horizon/webservice/update_transaction", "POST", paramsTransaction);
	    		//String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/webservice/update_transaction", "POST", paramsTransaction);
	    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/update_transaction", "POST", paramsTransaction);
	    		
	    		try {		
	    			Log.d("log_tag", "############# > " + returnJson.trim());
	    			if (returnJson.trim().equals("SAVED")){
	    				Log.d("log_tag", "Transaccion creada");
	    				// Close transaction
	    				transactionObject.setStatus("entregado_conciliado");
	    			}else{
	    				Log.d("log_tag: ", "Fallo al crear la transaccion");
	    			}
	    		}
	    		catch (Exception e) {
	    			Log.d("Products: ", "ERRROOOOOOOOOOOOOOR");
	    		}
			}else{
				transactionObject.setStatus("entregado");
				
			}
			dbTransactions.updateTransaction(transactionObject);
			
    		return true;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Log.i("log_tag", "conciliate delivery **********");
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
    			pDialog.dismiss();
    			
    			Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
    			startActivity(i);
    			finish();
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(DeliveryInfoActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	
}