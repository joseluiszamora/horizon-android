package com.horizon.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.Transaction;
import com.horizon.reports.ClientsListActivity;
import com.horizon.webservice.GPSTracker;
import com.ruizmier.horizon.R;

public class DeliveryActivity extends Activity implements OnItemClickListener {
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
 // GPS latitude longitude
 	double latitude = 0.0;
    double longitude = 0.0;
    
	DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(this, "", null, '1');
	DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(this, "", null, '1');
	
	ListView listView;
	List<Customer> rowItems;
	List<Transaction> rowItemsTransaction;
	
	Transaction transactionObject;
	Customer customer;
	
	// search functionality
	EditText edittext;
	 ListView listview;

	 String[] deliverycodes = null;

	 int textlength = 0;

	 ArrayList<String> text_sort = new ArrayList<String>();
	 ArrayList<Integer> image_sort = new ArrayList<Integer>();
		
	 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.delivery);
	    
	    
	    // Session Manager
		SessionManager session = new SessionManager(getApplicationContext());
	    // get user name data from session
        HashMap<String, String> user = session.getUserDetails();
        // Set user name into action bar layout 
        String name = user.get(SessionManager.KEY_NAME);
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
        
        GPSTracker gps = new GPSTracker(DeliveryActivity.this);
	    
	    latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        
        edittext = (EditText) findViewById(R.id.textSearch);
		listview = (ListView) findViewById(R.id.contentlistdelivery);
		
		deliverycodes = dbTransactions.getAllCustomerDeliveryOrder();
		//final List<Customer> rowItems = db.getAllCustomers();
		
		rowItemsTransaction = dbTransactions.getAllTransactionsDelivery();
		
		CustomAdapter adapter = new CustomAdapter(this, deliverycodes, rowItemsTransaction);
	    //ProductsAdapter adapter = new ProductsAdapter(this, rowItems);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	    
	    
        
        edittext.addTextChangedListener(new TextWatcher() {

 		   public void afterTextChanged(Editable s) {

 		   }

 		   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

 		   }

 		   public void onTextChanged(CharSequence s, int start, int before, int count) {

 		    textlength = edittext.getText().length();
 		    text_sort.clear();
 		    image_sort.clear();

 		    for (int i = 0; i < deliverycodes.length; i++) {
 		     if (textlength <= deliverycodes[i].length()) {
 		      if (edittext.getText().toString().equalsIgnoreCase ( (String) deliverycodes[i].subSequence(0,textlength))) {
 		    	  text_sort.add(deliverycodes[i]);
 		    	  Log.d("log_tag", "Edit text " + edittext.getText().toString());
 		      }
 		     }
 		    } 		       
 		    
 		   // List<Customer> SearchRowItems = db.getSearchCustomers(edittext.getText().toString());
		    //listview.setAdapter(new CustomAdapter(ClientsListActivity.this, text_sort, SearchRowItems));

 		   	rowItemsTransaction = dbTransactions.getAllTransactionsDelivery();
 			CustomAdapter adapter = new CustomAdapter(DeliveryActivity.this, text_sort, rowItemsTransaction);
 			listview.setAdapter(adapter);
 		   }
 		  });
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Gps Object
		GPSTracker gps = new GPSTracker(DeliveryActivity.this);
		// check if GPS enabled
        if(gps.canGetLocation()){	 
        	// create class object
            String codeCustomer = (String) ((TextView) arg1.findViewById(R.id.transaction_id)).getText();           
            /*Log.d("log_tag", "click :):):):)" + custom_id);
            
            pDialog = new ProgressDialog(DeliveryActivity.this);
    		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		pDialog.setMessage("Actualizando...");
    		pDialog.setCancelable(false);
    		pDialog.setMax(100);
    		
    		ArrayList<String> passing = new ArrayList<String>();
    		passing.add(custom_id);
    		
    		StartNewDeliveryDialog updateWork = new StartNewDeliveryDialog();
    		updateWork.execute(passing);*/
            
            Transaction thisTransaction = dbTransactions.getTransaction(Integer.parseInt(codeCustomer));
            
            
            thisTransaction.setCoordinateStart(latitude + " ; "+ longitude);
            
            // update time Start
            Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String formattedDate = df.format(c.getTime());
			thisTransaction.setTimeStart(formattedDate);
			dbTransactions.updateTransaction(thisTransaction);
			
			Intent intentNewTransaction = new Intent(DeliveryActivity.this, DeliveryInfoActivity.class);
			// get Client Info
			Bundle bundle = new Bundle();
			bundle.putString("codeTransaction", codeCustomer);
			intentNewTransaction.putExtras(bundle);
			startActivity(intentNewTransaction);
			finish();
			
        }else{
        	// can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
	}
	
	

    private class StartNewDeliveryDialog extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
		
    	protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
            ArrayList<String> result = new ArrayList<String>();            
         
            
//        	get passed value
            ArrayList<String> value = passing[0]; 
            String codeCustomer = "";
            for (String s : value){
            	codeCustomer = s;
            }
            
            if(codeCustomer != null && Integer.parseInt(codeCustomer) != 0){

                // get object transaction
                Transaction thisTransaction = dbTransactions.getTransaction(Integer.parseInt(codeCustomer));
                
                
                thisTransaction.setCoordinateStart(latitude + " ; "+ longitude);
                
                // update time Start
                Calendar c = Calendar.getInstance();
    			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    			String formattedDate = df.format(c.getTime());
    			thisTransaction.setTimeStart(formattedDate);
    			dbTransactions.updateTransaction(thisTransaction);
    			
    			
				result.add(String.valueOf(thisTransaction.getID()));
			}else{
				result.add("CODE_FAIL");
				// insert error here
				Log.d("log_tag", "Fucking code not match!!!!");
			}
            
            return result; //return result
        }

    	
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Log.d("log_tag", "PRE EXECUTE::::");
    		pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					StartNewDeliveryDialog.this.cancel(true);
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(ArrayList<String> result) {
    		String code = result.get(0);
    		//Log.d("log_tag", "0 ::::" + result.get(0));
    		

			if(code != "CODE_FAIL"){
				Log.d("log_tag", "TRANS COOOOODE ::::" + code);
				Intent intentNewTransaction = new Intent(DeliveryActivity.this, DeliveryInfoActivity.class);
				// get Client Info
				Bundle bundle = new Bundle();
				bundle.putString("codeTransaction", code);
				intentNewTransaction.putExtras(bundle);
				startActivity(intentNewTransaction);
				finish();
			}else{
				AlertDialog alertDialog = new AlertDialog.Builder(DeliveryActivity.this).create();
    			alertDialog.setTitle("Error");
    			alertDialog.setMessage("Se produjo un error al iniciar la transaccion, favor intentelo de nuevo");
    			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        			// here you can add functions
        				dialog.cancel();
        			}
    			});
    			alertDialog.setIcon(R.drawable.exit_pressed);
    			alertDialog.show();
			}
    		
    		pDialog.dismiss();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(DeliveryActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }


    
    class CustomAdapter extends BaseAdapter {
  	  String[] data_text;
  	  Context context;
  	  List<Transaction> rowTransactions;

  	  CustomAdapter() {

  	  }

  	  CustomAdapter(Context context, String[] text, List<Transaction> items) {
		this.context = context;
		data_text = text;
		this.rowTransactions = items;
  	  }

  	  CustomAdapter(Context context, ArrayList<String> text, List<Transaction> items) {
  		this.context = context;
		this.rowTransactions = items;
  	   	data_text = new String[text.size()];

  	   for (int i = 0; i < text.size(); i++) {
  	    data_text[i] = text.get(i);
  	   }

  	  }
  	  
  	  
	  public int getCount() {
	   return data_text.length;
	  }
	
		@Override
		public Object getItem(int position) {
		    return rowTransactions.get(position);
		}
	  
	  	@Override
	    public long getItemId(int position) {
	        return rowTransactions.indexOf(getItem(position));
	    }
  

  	  /* private view holder class */
      private class ViewHolder {
    	  TextView transactionId;
          TextView txtClient;
          TextView txtAddress;
          TextView txtDateStart;
          TextView txtCoordinate;
      }
      
  	  public View getView(int position, View convertView, ViewGroup parent) {
  		  
  		ViewHolder holder = null;
  		if (convertView == null) {
  			LayoutInflater inflater = getLayoutInflater();
  			convertView = inflater.inflate(R.layout.row_last_transaction, parent, false);
  			
	        holder = new ViewHolder();
	        holder.transactionId = (TextView) convertView.findViewById(R.id.transaction_id);
            holder.txtClient = (TextView) convertView.findViewById(R.id.tdIProduct);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.tdQuantity);
            holder.txtDateStart = (TextView) convertView.findViewById(R.id.transactionDateTime);
            holder.txtCoordinate = (TextView) convertView.findViewById(R.id.transactionCoordinate);
	        convertView.setTag(holder);
	     }
	     else {
	         holder = (ViewHolder) convertView.getTag();
	     }
  		
  		Transaction rowItem = (Transaction) getItem(position);
        Log.d("log_tag", rowItem.getCodeCustomer());
        Customer customer = db.getCustomerByCode(rowItem.getCodeCustomer());
        try {
        	
            holder.txtClient.setText(rowItem.getID()+ "-" + rowItem.getCodeCustomer()+ "-" + customer.getName());
            holder.txtAddress.setText(customer.getAddress());
		} catch (Exception e) {
			holder.txtClient.setText(rowItem.getID()+ "-" + rowItem.getCodeCustomer()+ "- Nombre no disponible");
            holder.txtAddress.setText("Direccion no disponible");
		}
        holder.transactionId.setText(String.valueOf(rowItem.getID()));
        holder.txtDateStart.setText("Creado en: " + rowItem.getTimeStart());
        //holder.txtDateStart.setText("Creado en: " + rowItem.getStatus());
        
        String upperString = rowItem.getType().substring(0,1).toUpperCase() + rowItem.getType().substring(1);
        
        holder.txtCoordinate.setText("Tipo de Transaccion: " + upperString);
        
        //holder.transactionId.setText(String.valueOf(rowItem.getID()));
        //holder.txtClient.setText(String.valueOf(customer.getName()));
        //holder.txtAddress.setText(customer.getAddress());
        //holder.txtDateStart.setText(rowItem.getTimeStart());        
        //holder.txtCoordinate.setText(rowItem.getCoordinateStart());

		return convertView;

  	  }
  	 }
}