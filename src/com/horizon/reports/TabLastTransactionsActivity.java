package com.horizon.reports;

import java.util.ArrayList;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.lists.listview.LastTransactionAdapter;
import com.horizon.webservice.InternetDetector;
import com.horizon.webservice.JSONParser;
import com.ruizmier.horizon.R;

public class TabLastTransactionsActivity extends Activity implements OnItemClickListener {
	
	// internet object
	final InternetDetector internet = new InternetDetector(this);
			
	// Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
 		
	// Session Manager Class
	SessionManager session;	
	
	String userMail;
	
	// Dialog
	private static final int DIALOGO_SELECCION = 3;
	// Selected Transaction
	String transactionCode;
	
	List<Transaction> rowItemsTransaction;
	LastTransactionAdapter adapterTransaction;
	Transaction transactionObject;
	Customer customer;
	
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, '1');
	DatabaseHandlerTransactionDetail dbTransactionDetail = new DatabaseHandlerTransactionDetail(this, "", null, '1');
	ListView listView;
	List<Transaction> rowItems;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_lasttransactions);
        
        // Session Manager Setting Mail
	    session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userMail = user.get(SessionManager.KEY_EMAIL);
        
        
        // make listransaction transaction
	    listView = (ListView) findViewById(R.id.contentlistlasttransactions);	
	    listView.setOnItemClickListener(this);
	    rowItemsTransaction = dbTransactions.getAllTransactions();
	    adapterTransaction = new LastTransactionAdapter(this, rowItemsTransaction);
	    listView.setAdapter(adapterTransaction);
	       
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		transactionCode = (String) ((TextView) view.findViewById(R.id.transaction_id)).getText();
		
		//Log.d("log_tag", "///////// " + transactionCode);
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(Integer.parseInt(transactionCode));
		
		Log.d("log_tag", "TO >>>>>>>>" + transactionObject.getCodeCustomer());
		
		showDialog(DIALOGO_SELECCION);
		
	}	
	
	// Select Dialog Create Interface
		@Override
	    protected Dialog onCreateDialog(int id) {    	
	    	Dialog dialogo = makeSelectionDialog();
	    	return dialogo;
	    }
	    
	    private Dialog makeSelectionDialog(){
	    	final String[] items = {"Ver / Editar", "Conciliar", "Eliminar"};
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	builder.setTitle("Seleccione la acci�n");
	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	        Log.i("log_tag", "Opci�n elegida: " + items[item]);    	    	
	    	    	if (item == 0){
	    	    		if (!transactionObject.getType().equals("transaccion_0")){
	    	    			Intent intentViewTransaction = new Intent(TabLastTransactionsActivity.this, TransactionInfoActivity.class);
		    	    		Bundle bundle = new Bundle();
		    	    		bundle.putString("transaction_id", transactionCode);
		    	    		intentViewTransaction.putExtras(bundle);
		    	    		startActivity(intentViewTransaction);
		    	    		finish();
	    	    		}else{
	    	    			Toast.makeText(TabLastTransactionsActivity.this, "Esta Transaccion no tiene nigun producto", Toast.LENGTH_SHORT).show();
	    	    		}
	    	    	}
					if (item == 1) {
						// Check for internet connection
				        if (!internet.isConnectingToInternet()) {
				        	// message
							Toast toast = Toast.makeText(TabLastTransactionsActivity.this, "No hay conexi�n a Internet", Toast.LENGTH_SHORT);
			    			toast.show();		         
				        }else{
				        	pDialog = new ProgressDialog(TabLastTransactionsActivity.this);
							pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							pDialog.setMessage("Conciliando...");
							pDialog.setCancelable(false);
							pDialog.setMax(100);
							
							SaveTransactionDialog updateTransaction = new SaveTransactionDialog();
							updateTransaction.execute();
				        }
					}
					if (item == 2) {
						deleteTransaction(transactionCode);
					}
	    	    }
	    	});    	    	
	    	return builder.create();
	    }
	    
	    private void deleteTransaction(final String id_customer){
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("�Est� seguro de eliminar esta Transacci�n? ")
		           .setPositiveButton("Aceptar ", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dbTransactions.deleteTransaction(Integer.parseInt(id_customer));
		        	   dbTransactionDetail.closeAllTransactionDetailForThisTransaction(Integer.parseInt(id_customer));
		        	   update();
		           }
		       });
	        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                 dialog.dismiss();
	     //            listDialog.cancel();
	               }
	           });
	 
	        AlertDialog alert = builder.create();
	        alert.setTitle("Atenci�n");
	        alert.show();
	    }
	    
	    private void update() { // refresh listview
	    	rowItemsTransaction = dbTransactions.getAllTransactions();	  
	 	    adapterTransaction = new LastTransactionAdapter(this, rowItemsTransaction);
	 	    adapterTransaction.notifyDataSetChanged();
	 	    listView.setAdapter(adapterTransaction);
		}
	    
/** MENU **/
	    // Initiating Menu XML file 
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu){
	        MenuInflater menuInflater = getMenuInflater();
	        menuInflater.inflate(R.layout.menu_lasttransaction, menu);
	        return true;
	    }
	           
	    /**
	     * Event Handling for Individual menu item selected
	     * Identify single menu item by it's id
	     * */
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	 
	        switch (item.getItemId()){
	        case R.id.menu_concilar_todo:           
	        	
	    		InternetDetector internet = new InternetDetector(getApplicationContext());
	        	 // Check for internet connection
		        if (!internet.isConnectingToInternet()) {
		        	// message
					Toast toast = Toast.makeText(TabLastTransactionsActivity.this, "No hay conexi�n a Internet", Toast.LENGTH_SHORT);
	    			toast.show();		         
		        }else{
		        	// Check for internet connection
					if (!internet.isConnectingToInternet()) {
			        	// message
						Toast toast = Toast.makeText(TabLastTransactionsActivity.this, "No hay conexi�n a Internet", Toast.LENGTH_SHORT);
		    			toast.show();		         
			        }else{
			        	pDialog = new ProgressDialog(TabLastTransactionsActivity.this);
						pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pDialog.setMessage("Conciliando todas las operaciones...");
						pDialog.setCancelable(false);
						pDialog.setMax(100);
						
						SaveAllTransactionDialog updateAllTransaction = new SaveAllTransactionDialog();
						updateAllTransaction.execute();
			        }
		        }
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }  
	    
	    
	    
	    
	    // AsyncTask Send Transaccion Web 
		private class SaveTransactionDialog extends AsyncTask<Void, Integer, Boolean> {
	    	@Override
	    	protected Boolean doInBackground(Void... params) {
	    		Transaction transaction = dbTransactions.getTransaction(Integer.parseInt(transactionCode));
	    		List<TransactionDetail> listTransactionDetail  = dbTransactionDetail.
	    				getAllTransactionDetailsForThisTransaction(Integer.parseInt(transactionCode));

	    		JSONObject objectAllTransactionsDetails = new JSONObject();
	    		
				//create json transaction details Main Object
	    		for(int i = 0; i < listTransactionDetail.size(); i++) {
	    			TransactionDetail trans = listTransactionDetail.get(i);
	    			Log.d("log_tag", "Anadiendo producto:::: " + trans.getNameProduct());
	    			
	    			JSONArray jsonArray = new JSONArray();
	                jsonArray.put(trans.getCodeProduct());
	                jsonArray.put(trans.getQuantity());
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
					Log.d("log_tag", "NNNNNNNNNNNNNNNNNNNN::: " + transaction.getCodeCustomer());
					objectTransaction.put("TransactionsArray", objectAllTransactionsDetails);
					objectTransaction.put("codeCustomer", transaction.getCodeCustomer());
					objectTransaction.put("transactionType", transaction.getType());
					objectTransaction.put("clientType", transaction.getClientType());
					objectTransaction.put("coordinateStart", transaction.getCoordinateStart());
					objectTransaction.put("coordinateFinish", transaction.getCoordinateFinish());
					objectTransaction.put("timeStart", transaction.getTimeStart());
					objectTransaction.put("timeFinish", transaction.getTimeFinish());
					objectTransaction.put("obs", transaction.getObs());
					objectTransaction.put("userMail", userMail);
				} catch (JSONException e) {					
					e.printStackTrace();
				}

	    		// Building Parameters
	    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
	    		paramsTransaction.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	    		
	    		
	    		// getting JSON string from URL
	    		//String returnJson = jsonParser.makeHttpRequest("http://www.ruizmier.com/systems/horizon/webservice/save_transaction", "POST", paramsTransaction);
	    		//String returnJson = jsonParser.makeHttpRequest("http://mariani.bo/pruebas/horizon/webservice/save_transaction", "POST", paramsTransaction);
	    		//String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/webservice/save_transaction", "POST", paramsTransaction);
	    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/save_transaction", "POST", paramsTransaction);
	    		
	    		Log.d("log_tag: ", "> " + returnJson.trim());
	    		try {		
	    			Log.d("PRODUCTOS JSON: ", "> " + returnJson.trim());
	    			if (returnJson.trim().equals("SAVED")){
	    				Log.d("log_tag", "Transaccion creada");
	    				
	    				// Close transaction
	    				transaction.setStatus("conciliado");
	    				dbTransactions.updateTransaction(transaction);
	    				dbTransactionDetail.updateAllTransactionDetailsDelivery(transaction.getID(), "conciliado");
	    				
	    				//Toast.makeText(TabLastTransactionsActivity.this, "Transaccion conciliada correctamente!", Toast.LENGTH_SHORT).show();
	    			}else{
	    				Log.d("log_tag: ", "Fallo al crear la transaccion");
	    				//Toast.makeText(TabLastTransactionsActivity.this, "Hubo un problema al conciliar la transaccion, " +
	    				//		"porfavor intentelo de nuevo!", Toast.LENGTH_SHORT).show();
	    			}
	    		}
	    		catch (Exception e) {	    			
	    			Log.d("Products: ", "ERRROOOOOOOOOOOOOOR");
	    	       //Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
	    		}

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
						SaveTransactionDialog.this.cancel(true);
					}
				});
	    		
	    		pDialog.setProgress(0);
	    		pDialog.show();
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Boolean result) {
	    		if(result) {
	    			//Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
	    			//startActivity(i);
	    			pDialog.dismiss();
	    			//finish();
	    			update();
	    		}
	    	}
	    	
	    	@Override
	    	protected void onCancelled() {
	    		Toast.makeText(TabLastTransactionsActivity.this, "Transaccion ", Toast.LENGTH_SHORT).show();
	    	}
	    }
		
		
	    // AsyncTask Send ALL Transaccions to the Web 
		private class SaveAllTransactionDialog extends AsyncTask<Void, Integer, Boolean> {
	    	@Override
	    	protected Boolean doInBackground(Void... params) {
	    		
	    		Boolean sw = true;
	    		
	    		List<Transaction> allTransactions = dbTransactions.getAllTransactions();
	    		
    	        for (Transaction thisTransaction : allTransactions) {
    	        	
    	        	try {
    	        		Transaction transaction = dbTransactions.getTransaction(thisTransaction.getID());
    		    		List<TransactionDetail> listTransactionDetail  = dbTransactionDetail.
    		    				getAllTransactionDetailsForThisTransaction(thisTransaction.getID());

    		    		JSONObject objectAllTransactionsDetails = new JSONObject();

    		    		
    		    		//create json transaction details Main Object
    		    		for(int i = 0; i < listTransactionDetail.size(); i++) {
    		    			TransactionDetail trans = listTransactionDetail.get(i);		    			
    		    			
    		    			JSONArray jsonArray = new JSONArray();
    		                jsonArray.put(trans.getCodeProduct());
    		                jsonArray.put(trans.getQuantity());
    		                jsonArray.put(trans.getObs());
    		                try {
    		                	objectAllTransactionsDetails.put("transaction"+i, jsonArray);
    						} catch (JSONException e) {
    							e.printStackTrace();
    						}
    		    		}
    		    		
    		    		// create Main json Object for this transaction
    		    		JSONObject objectTransaction = new JSONObject();
    					try {
    						objectTransaction.put("TransactionsArray", objectAllTransactionsDetails);
    						objectTransaction.put("codeCustomer", transaction.getCodeCustomer());
    						objectTransaction.put("transactionType", transaction.getType());
    						objectTransaction.put("clientType", transaction.getClientType());
    						objectTransaction.put("coordinateStart", transaction.getCoordinateStart());
    						objectTransaction.put("coordinateFinish", transaction.getCoordinateFinish());
    						objectTransaction.put("timeStart", transaction.getTimeStart());
    						objectTransaction.put("timeFinish", transaction.getTimeFinish());
    						objectTransaction.put("obs", transaction.getObs());
    						objectTransaction.put("userMail", userMail);
    					} catch (JSONException e) {					
    						e.printStackTrace();
    					}								
    					
    		    		// Building Parameters
    		    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
    		    		paramsTransaction.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	    		
    		    		
    		    		// getting JSON string from URL
    		    		//String returnJson = jsonParser.makeHttpRequest("http://www.ruizmier.com/systems/horizon/webservice/save_transaction", "POST", paramsTransaction);
    		    		//String returnJson = jsonParser.makeHttpRequest("http://mariani.bo/pruebas/horizon/webservice/save_transaction", "POST", paramsTransaction);
    		    		//String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/webservice/save_transaction", "POST", paramsTransaction);
    		    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/save_transaction", "POST", paramsTransaction);
    		    		
    		    		Log.d("PRODUCTOS JSON SENDING: ", "> " + returnJson.trim());
    		    		try {		
    		    			Log.d("PRODUCTOS JSON: ", "> " + returnJson.trim());
    		    			if (returnJson.trim().equals("SAVED")){
    		    				Log.d("log_tag", "Transaccion Conciliada correctamente");
    		    				
    		    				// Close transaction
    		    				Log.d("log_tag", "Transaccion guardada para: " + transaction.getCodeCustomer());
    		    				transaction.setStatus("conciliado");
    		    				dbTransactions.updateTransaction(transaction);
    		    				dbTransactionDetail.updateAllTransactionDetailsDelivery(transaction.getID(), "conciliado");
    		    			}else{
    		    				transaction.setStatus("creado");
    		    				Log.d("log_tag: ", "Fallo al crear la transaccion");
    		    				sw = false;
    		    			}
    		    		}
    		    		catch (Exception e) {
    		    			sw = false;
    		    			Log.d("log_tag: ", "ERRROOOOOOOOOOOOOOR");
    		    		}	    	
					} catch (Exception e) {
						sw = false;
					}
    	        }

	    		return sw;
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
						SaveAllTransactionDialog.this.cancel(true);
					}
				});
	    		
	    		pDialog.setProgress(0);
	    		pDialog.show();
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Boolean result) {
	    		pDialog.dismiss();
	    		if(result) {
	    			Toast.makeText(TabLastTransactionsActivity.this, "Transacciones Conciliadas Correctamente", Toast.LENGTH_SHORT).show();
	    			update();
	    		}else{
	    			Toast.makeText(TabLastTransactionsActivity.this, "Error al conciliar Todas las transacciones, favor intentar de nuevo", Toast.LENGTH_SHORT).show();
	    		}
	    	}
	    	
	    	@Override
	    	protected void onCancelled() {
	    		Toast.makeText(TabLastTransactionsActivity.this, "Transaccion ", Toast.LENGTH_SHORT).show();
	    	}
	    }

}