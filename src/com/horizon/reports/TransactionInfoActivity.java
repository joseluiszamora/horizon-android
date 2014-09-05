package com.horizon.reports;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerLineVolumes;
import com.horizon.database.DatabaseHandlerLines;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.DatabaseHandlerVolumes;
import com.horizon.database.Line;
import com.horizon.database.MakeTransaction;
import com.horizon.database.Product;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.database.Volume;
import com.horizon.lists.listview.DialogLineAdapter;
import com.horizon.lists.listview.DialogProductAdapter;
import com.horizon.lists.listview.DialogVolumeAdapter;
import com.horizon.lists.listview.TransactionListAdapter;
import com.horizon.main.DashboardActivity;
import com.horizon.main.TransactionActivity;
import com.horizon.webservice.GPSTracker;
import com.ruizmier.horizon.R;

public class TransactionInfoActivity extends Activity implements OnItemClickListener{
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
	
	// Database Product class
	DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(this, "", null, '1');
	
	// GPS Object
	final GPSTracker gps = new GPSTracker(TransactionInfoActivity.this);
	
	ListView listView;
	List<TransactionDetail> rowItems;
	TransactionListAdapter adapter;
	Customer customer;
	Transaction transactionObject;
	Bundle bundle;
	Integer codeTransaction;
	Double totalPrice;
	
	TextView txtBottomPrice;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.edit_transaction);
		
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
		Log.d("log_tag", "CODE>>>>>>>>" + codeTransaction);
        
		
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(codeTransaction);
		Log.d("log_tag", "TO >>>>>>>>" + transactionObject.getCodeCustomer());
		
		// define price
		try {
			totalPrice = dbTransDetail.getTotalPriceTransaction(transactionObject.getID());
		} catch (Exception e) {
			totalPrice = 0.00;
		}
		
		// Define TextViews
		TextView txtClientName = (TextView)findViewById(R.id.TransDetailInfoCustomName);
		TextView txtClientAddress = (TextView)findViewById(R.id.txtClientAddress);
		txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		
		if(transactionObject.getClientType().equals("temporal")){
			Log.d("log_tag", "CODE IS >>>>>>>> 0000");
			txtClientName.setText(name);
		    txtClientAddress.setText("Transaccion temporal");
		}else{
			Log.d("log_tag", "CODE NO IS >>>>>>>> " + transactionObject.getCodeCustomer());
			// Create Product Object
			customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
			Log.d("log_tag", "CU NA >>>>>>>>" + customer.getName());
			
			txtClientName.setText(customer.getName());
		    txtClientAddress.setText(customer.getAddress());
		}
		
		// make listransaction transaction
		listView = (ListView) findViewById(R.id.contentlisttransdetails);	
	    listView.setOnItemClickListener(this);
	    rowItems = dbTransDetail.getAllTransactionDetailsForThisTransaction(codeTransaction);
	    adapter = new TransactionListAdapter(TransactionInfoActivity.this, rowItems);
	    listView.setAdapter(adapter);
	    
	    // Define Buttons
		final Button btnadd = (Button)findViewById(R.id.btnConciliar);
		final Button btnSave = (Button)findViewById(R.id.btnSave);
        
        // set transaction price
        setPrice(totalPrice);

		//btn Add Product
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				transaction.start(codeTransaction);
				showdialogLines();
			}
		});
		
		// btn Saves
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeTransaction();
			}		
		});		
	}	
	
	//** Total Price Management **//
	public void setPrice(double price){
		txtBottomPrice.setText(String.format("%.2f", price));
		Log.d("log_tag", "new price::: " + totalPrice);
	}
	
	public void addPrice(double addprice){
		totalPrice += addprice;
		setPrice(totalPrice);
	}
	public void lessPrice(double addprice){
		totalPrice -= addprice;
		setPrice(totalPrice);
	}
	
	//** Pressed return button **// 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	 //Toast.makeText(TransactionActivity.this, "bye bye", Toast.LENGTH_SHORT).show();
        	closeTransaction();
         }
        return super.onKeyDown(keyCode, event);
    }

	private void closeTransaction(){
		Intent intent = new Intent(TransactionInfoActivity.this, TabsReportActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void showdialogLines() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionInfoActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Linea:");
	    
	    final ListView modeList = new ListView(TransactionInfoActivity.this);
	    List<Line> rowItemsLines = dbLine.getAllLines();
		DialogLineAdapter modeAdapter = new DialogLineAdapter(this, rowItemsLines);	    
	     
	     modeList.setAdapter(modeAdapter);
		
	     modeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	     builder.setView(modeList);
	     
		final Dialog dialog = builder.create();
		dialog.show();
		
		
		modeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    		 String line_id = (String) ((TextView) view.findViewById(R.id.line_id)).getText();
	    		 
	    		 if(line_id != null && Integer.parseInt(line_id) != 0){
	    			 transaction.setLine(Integer.parseInt(line_id));	    			    		
		    		 
		    		 showdialogVolumes();
	    		 }else{
    				// insert error here
	    			 Toast.makeText(TransactionInfoActivity.this, "Error: line incorrecto", Toast.LENGTH_SHORT).show();
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
		
	public void showdialogVolumes() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionInfoActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Volumen:");
	     
	    final ListView modeList = new ListView(TransactionInfoActivity.this);
	    	   
		List<Volume> rowItems = dbVolume.getAllVolumesForLine(transaction.getLine());
		
		DialogVolumeAdapter modeAdapter = new DialogVolumeAdapter(this, rowItems);	    
		 
	     modeList.setAdapter(modeAdapter);
	    
	     modeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	     builder.setView(modeList);
	     
		final Dialog dialog = builder.create(); 
		dialog.show();
		
		
		modeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    		 String volume_id = (String) ((TextView) view.findViewById(R.id.line_id)).getText();
	    		 
	    		 
	    		 if(volume_id != null && Integer.parseInt(volume_id) != 0){
	    			 transaction.setVolume(Integer.parseInt(volume_id));
		    		 
	    			 showdialogProducts();
	    		 }else{
    				// insert error here
	    			 Toast.makeText(TransactionInfoActivity.this, "Error: Volumn Incorrecto", Toast.LENGTH_SHORT).show();
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
	
	public void showdialogProducts() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionInfoActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Producto:");
	     
	    final ListView modeList = new ListView(TransactionInfoActivity.this);
		List<Product> rowItems = dbProduct.getAllProductsForLineVolume(transaction.getLine(), transaction.getVolume());
		DialogProductAdapter modeAdapter = new DialogProductAdapter(this, rowItems);	
	    modeList.setAdapter(modeAdapter);
	    
	    modeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    builder.setView(modeList);
	     
		final Dialog dialog = builder.create(); 
		dialog.show();
		
		modeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    		 String product_id = (String) ((TextView) view.findViewById(R.id.line_id)).getText();
	    		 Log.d("log_tag", "ID...._> " +product_id);
	    		 if(product_id != null && Long.parseLong(product_id) != 0){
	    			 transaction.setCodeProduct(product_id);
		    		 
	    			 showdialogQuantity();
	    		 }else{
    				// insert error here
	    			 Toast.makeText(TransactionInfoActivity.this, "Error: producto incorrecto", Toast.LENGTH_SHORT).show();
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
	
	public void showdialogQuantity() {		
		 AlertDialog.Builder alert = new AlertDialog.Builder(TransactionInfoActivity.this);
		 alert.setTitle("Cantidad: ");  

		  // Set an EditText view to get user input   
		  final EditText input = new EditText(TransactionInfoActivity.this);
		  alert.setView(input);
		  
		  input.setInputType(InputType.TYPE_CLASS_NUMBER);

		  input.setFilters(new InputFilter[] {
		    // Maximum 5 characters.
		    new InputFilter.LengthFilter(5),
		  });
		  		  		  
		  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

	     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	     public void onClick(DialogInterface dialog, int whichButton) {  
	    	 if(!input.getText().toString().trim().equals("") && !input.getText().toString().trim().equals(null)){
	    		 Integer value = Integer.parseInt(input.getText().toString());
		         
		         if(value != 0){
		        	 transaction.setQuantity(value);
	    			 createNewTransactionDetail(transaction);
	    		 }else{
					// insert error here
	    			 Toast.makeText(TransactionInfoActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
	    		 }
	    	 }else{
	    		 Toast.makeText(TransactionInfoActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
	    	 }
	    	 
	    	 return;                  
	        }  
	      });  
	
	     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int which) {
	             // TODO Auto-generated method stub
	             return;   
	         }
	     });
	    AlertDialog alertToShow = alert.create();
		alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		alertToShow.show();	
   }
	
	private void createNewTransactionDetail(MakeTransaction newtransaction){
		// verify again
		if (transaction.getIdTransaction() != 0 && transaction.getCodeProduct() != null && transaction.getQuantity() != 0){
			// verify if this transactions already have this product
			TransactionDetail getTransDetail = dbTransDetail.getTransactionDetailIfExist(transaction.getIdTransaction(), transaction.getCodeProduct());
			Product  getproduct = dbProduct.getProduct(transaction.getCodeProduct());
			Double unitPrice = Double.parseDouble(getproduct.getPrice());
			
			if(getTransDetail != null){ // this product already exist in the list
				Double totalPrice = (double) ((unitPrice * transaction.getQuantity()) + getTransDetail.getPriceTotal());
				int Quantity = (int) (transaction.getQuantity() + getTransDetail.getQuantity());
				
				// Edit Transaction Detail
				dbTransDetail.updateTransactionDetail(new TransactionDetail(getTransDetail.getID(), null, transaction.getIdTransaction(), transaction.getCodeProduct(),
						getproduct.getName(), unitPrice, Quantity, "creado", totalPrice, null, null));
				
				addPrice((double) ((unitPrice * transaction.getQuantity())));
			}else{ // product new
				Double totalPrice = (double) (unitPrice * transaction.getQuantity());
				// Add new Transaction Detail
				dbTransDetail.addTransactionDetail(new TransactionDetail(null, transaction.getIdTransaction(),  transaction.getCodeProduct(), 
						getproduct.getName(), unitPrice, transaction.getQuantity(), "creado", totalPrice, "ninguna", "normal"));
				
				addPrice(totalPrice);
			}
			
	        update();
		}
		else{
			Toast.makeText(TransactionInfoActivity.this,"Error al agregar el producto", Toast.LENGTH_SHORT).show();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final String custom_id_trans_details = (String) ((TextView) arg1.findViewById(R.id.transaction_id_detail)).getText();
		final String product_name_trans_details = (String) ((TextView) arg1.findViewById(R.id.tdproduct)).getText();
		final String total_price_trans_details = (String) ((TextView) arg1.findViewById(R.id.totalPriceTransactionModify)).getText();
		
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Esta seguro de Eliminar el producto: "+product_name_trans_details+" ?")
	           .setPositiveButton("OK ", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dbTransDetail.deleteTransactionDetail(Integer.parseInt(custom_id_trans_details));
	        	   lessPrice(Double.parseDouble(total_price_trans_details));
	        	   System.out.println("OK CLICKED");
	        	   update();
	           }
	       });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
     //            listDialog.cancel();
               }
           });
 
        AlertDialog alert = builder.create();
        alert.setTitle("Atencion");
        alert.show();
    }
	
	private void update() { // refresh listview
		rowItems = dbTransDetail.getAllTransactionDetailsForThisTransaction(codeTransaction);
	    adapter = new TransactionListAdapter(TransactionInfoActivity.this, rowItems);
	    adapter.notifyDataSetChanged();
	    listView.setAdapter(adapter);
	}
	
	// AsyncTask Finish Transaction
	private class UpdateInfoAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
	    
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		// set GPS
			transactionObject.setCoordinateFinish(gps.getLatitude() + " ; "+ gps.getLongitude());
			//get Date, Hour Now
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String formattedDate = df.format(c.getTime());
			Log.i("TIME:::: ", "Date  => " + formattedDate);
			
			transactionObject.setTimeFinish(formattedDate);
			
			dbTransactions.updateTransaction(transactionObject);
			
			Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
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
    			//Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
    			//startActivity(i);
    			pDialog.dismiss();
    			//finish();
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(TransactionInfoActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	
}