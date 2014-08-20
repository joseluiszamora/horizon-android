package com.horizon.reports;

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
import android.util.Log;
import android.view.KeyEvent;
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
import com.horizon.main.DashboardActivity;
import com.horizon.main.TransactionActivity;
import com.horizon.reports.TabClientsActivity.CustomAdapter;
import com.horizon.webservice.GPSTracker;
import com.ruizmier.horizon.R;

public class ClientsListActivity extends Activity implements OnItemClickListener {
	
	// Progress Dialog
    private ProgressDialog pDialog;
    // GPS latitude longitude
 	double latitude = 0.0;
    double longitude = 0.0;
    
	DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(this, "", null, '1');
	List<Customer> rowItems;
	
	// Check new transaction type
	String transactionTpye = null;
	
	// search functionality
	EditText edittext;
	ListView listview;

	String[] text = null;
	int textlength = 0;

	ArrayList<String> text_sort = new ArrayList<String>();
	ArrayList<Integer> image_sort = new ArrayList<Integer>();
	
	Customer customer = new Customer();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.client_list);
	    
	    // Session Manager
	    SessionManager session = new SessionManager(getApplicationContext());
	    // get user name data from session
	    HashMap<String, String> user = session.getUserDetails();
	    // Set user name into action bar layout 
	    String name = user.get(SessionManager.KEY_NAME);
	    TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
	    actionBarClient.setText(name);
	    
	     
	    // get bundle transaction type info
	    Bundle bundle = getIntent().getExtras();
		// Get Bundle Transaction Code
	    transactionTpye = bundle.getString("transactionType");
	    
	    try {
	    	edittext = (EditText) findViewById(R.id.textSearch);
			listview = (ListView) findViewById(R.id.contentlistclient);
			
			text = db.getAllCustomerNames();
			final List<Customer> rowItems = db.getAllCustomers();
			listview.setAdapter(new CustomAdapter(this, text, rowItems));		
			listview.setOnItemClickListener(this);

		} catch (Exception e) {
			Toast.makeText(ClientsListActivity.this, "No se pudo listar correctamente la lista de clientes, favor intentelo de nuevo", Toast.LENGTH_SHORT).show();
		}
		
	    // GPS TRACKER
		GPSTracker gps = new GPSTracker(ClientsListActivity.this);
	    
	    latitude = gps.getLatitude();
        longitude = gps.getLongitude();

	  edittext.addTextChangedListener(new TextWatcher() {
	
	   public void afterTextChanged(Editable s) { }
	
	   public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	   public void onTextChanged(CharSequence s, int start, int before, int count) {
	
	    textlength = edittext.getText().length();
	    text_sort.clear();
	    image_sort.clear();
	
	    for (int i = 0; i < text.length; i++) {
			if(text[i].toLowerCase().contains(edittext.getText().toString())){
				text_sort.add(text[i]);
				Log.d("log_tag", "Edit text " + text[i].toLowerCase() + "----------" +edittext.getText().toString());
			}
	    }
	
	    List<Customer> SearchRowItems = db.getSearchCustomers(edittext.getText().toString());
	    listview.setAdapter(new CustomAdapter(ClientsListActivity.this, text_sort, SearchRowItems));			   
	   }
	  });
	}
	
	//** Pressed return button **// 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Intent intent = new Intent(ClientsListActivity.this, DashboardActivity.class);
			startActivity(intent);
			finish();
         }
        return super.onKeyDown(keyCode, event);
    }
		
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		
		// create class object
        String custom_id = (String) ((TextView) arg1.findViewById(R.id.customer_id)).getText();
        
        Log.d("log_tag", "******************** CUSTOM ID----> " + custom_id);
        
        
        /*pDialog = new ProgressDialog(ClientsListActivity.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Cliente...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		
		ArrayList<String> passing = new ArrayList<String>();
		passing.add(custom_id);
		
		StartNewTransactionDialog updateWork = new StartNewTransactionDialog();
		updateWork.execute(passing);*/
        
      //get Date, Hour Now
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(ClientsListActivity.this, "", null, 1);
		
		// Create New Transaction        			
		Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", custom_id, "", "pending", transactionTpye, "0", "regular", 
				formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));

		String codeTransaction = String.valueOf(idNewTransaction);
		
		
		Intent intentNewTransaction = new Intent(ClientsListActivity.this, TransactionActivity.class);            	
		// get Client Info
		Bundle bundle = new Bundle();
		bundle.putString("newTransactionCode", codeTransaction);
		intentNewTransaction.putExtras(bundle);
		startActivity(intentNewTransaction);
		finish();
	}
		 
    private class StartNewTransactionDialog extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
		
    	protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
            ArrayList<String> result = new ArrayList<String>();            
            result.add("lol");
            // get passed value
            ArrayList<String> value = passing[0]; 
            String codeCustomer = "";
            for (String s : value){
            	codeCustomer = s;
            }
    		try {
    			customer = db.getCustomerByCode(codeCustomer);
    			if (customer != null){
        			result.add("NO_FAIL");
        	        
        			//get Date, Hour Now
        			Calendar c = Calendar.getInstance();
        			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        			String formattedDate = df.format(c.getTime());        			
        			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(ClientsListActivity.this, "", null, 1);
        			
        			// Create New Transaction        			
        			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", codeCustomer, "", "pending", transactionTpye, "0", "regular", 
        					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
        			
        			if(idNewTransaction != null && idNewTransaction != 0){
        				int codeTransaction = (int) (long) idNewTransaction;        				
        				result.add("CODE_OK");
        				result.add(String.valueOf(codeTransaction));
        			}else{
        				result.add("CODE_FAIL");
        			}
        		}else{
        			result.add("EPIC_FAIL");
        		}
			} catch (Exception e) {
				Log.d("log_tag", "FAIL CODE");
			}    		
            return result;
        }

    	
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					StartNewTransactionDialog.this.cancel(true);
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(ArrayList<String> result) {
    		String tmp = result.get(1);
    		if (tmp.matches("EPIC_FAIL")){
    			// Dialog Error found client
    			AlertDialog alertDialog = new AlertDialog.Builder(ClientsListActivity.this).create();
    			alertDialog.setTitle("Error");
    			alertDialog.setMessage("Cliente no Registrado");
    			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        			// here you can add functions
        				dialog.cancel();
        			}
    			});
    			alertDialog.setIcon(R.drawable.exit_pressed);
    			alertDialog.show();
    		}else{
    			if(tmp.matches("NO_FAIL")){
    				if(result.get(2).matches("CODE_OK")){
    					Intent intentNewTransaction = new Intent(ClientsListActivity.this, TransactionActivity.class);            	
    					// get Client Info
    	    			Bundle bundle = new Bundle();
    	    			bundle.putString("newTransactionCode", result.get(3));
    					intentNewTransaction.putExtras(bundle);
    	    			startActivity(intentNewTransaction);
    				}else{
    					// Dialog Error found client
    	    			AlertDialog alertDialog = new AlertDialog.Builder(ClientsListActivity.this).create();
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
    			}
    		}
    		pDialog.dismiss();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(ClientsListActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }

    class CustomAdapter extends BaseAdapter {

  	  String[] data_text;
  	  Context context;
  	  List<Customer> rowCustomers;

  	  CustomAdapter() {

  	  }

  	  CustomAdapter(Context context, String[] text, List<Customer> items) {
  	   data_text = text;
  	   this.rowCustomers = items;
  	  }

  	  CustomAdapter(Context context, ArrayList<String> text, List<Customer> items) {
  		this.rowCustomers = items;
  	   data_text = new String[text.size()];

  	   for (int i = 0; i < text.size(); i++) {
  	    data_text[i] = text.get(i);
  	   }

  	  }
  	  
  	  
  	  public int getCount() {
  	   return data_text.length;
  	  }

  	  public String getItem(int position) {
  	   return null;
  	  }
  	  
  	  public Object getItemCustom(int position) {
	    return rowCustomers.get(position);
  	  }

  	  public long getItemId(int position) {
  		return rowCustomers.indexOf(getItem(position));
  	  }

  	  /* private view holder class */
      private class ViewHolder {
      	TextView customerId;
        TextView txtName;
        TextView txtAddress;
      }
      
  	  public View getView(int position, View convertView, ViewGroup parent) {
  		  
  		ViewHolder holder = null;
  		if (convertView == null) {
  			LayoutInflater inflater = getLayoutInflater();
  			convertView = inflater.inflate(R.layout.row_clients, parent, false);
  			
	        holder = new ViewHolder();
	        holder.customerId = (TextView) convertView.findViewById(R.id.customer_id);
	        holder.txtName = (TextView) convertView.findViewById(R.id.customerName);
	        holder.txtAddress = (TextView) convertView.findViewById(R.id.customerAddress);
	        convertView.setTag(holder);
	     }
	     else {
	         holder = (ViewHolder) convertView.getTag();
	     }
  		
  		Customer rowItem = (Customer) getItemCustom(position);	    
	     
  		holder.customerId.setText(String.valueOf(rowItem.getCode()));
		holder.txtName.setText(data_text[position]);
		holder.txtAddress.setText(rowItem.getAddress());
		
		return convertView;

  	  }
  	 }
}