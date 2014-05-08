package com.horizon.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Daily;
import com.horizon.database.DatabaseHandlerDaily;
import com.horizon.database.DatabaseHandlerPay;
import com.horizon.database.Pay;
import com.horizon.lists.listview.TransactionListAdapter;
import com.horizon.main.DashboardActivity;
import com.horizon.main.TransactionActivity;
import com.horizon.webservice.GPSTracker;
import com.ruizmier.horizon.R;

public class CobroListActivity extends Activity implements OnItemClickListener {
    // GPS latitude longitude
 	double latitude = 0.0;
    double longitude = 0.0;
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // Dialog options to preventa, venta directa
  	private static final int DIALOGO_ADDPAY = 1;
  	private static final int DIALOGO_DELETEPAY = 2;
  	
    DatabaseHandlerDaily db = new DatabaseHandlerDaily(this, "", null, '1');
    DatabaseHandlerPay dbpay = new DatabaseHandlerPay(this, "", null, '1');
    Daily daily = new Daily();
    
	// search functionality
	EditText edittext;
	String[] text = null;
	int textlength = 0;
	ArrayList<String> text_sort = new ArrayList<String>();
	
	List<Daily> rowItems;
	ListView listview;
	
	// pay
	int idDaily;
	
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
	    
	    try {
	    	edittext = (EditText) findViewById(R.id.textSearch);
			listview = (ListView) findViewById(R.id.contentlistclient);
			
			text = db.getAllNames();
			rowItems = db.getAll();
			Log.d("log_tag", "SELECT DAILIES:::::::: ITEMS::: " + rowItems);
			listview.setAdapter(new CustomAdapter(this, text, rowItems));		
			listview.setOnItemClickListener(this);

		} catch (Exception e) { }
		
	    // GPS TRACKER
		GPSTracker gps = new GPSTracker(CobroListActivity.this);
	    latitude = gps.getLatitude();
        longitude = gps.getLongitude();

		edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textlength = edittext.getText().length();
				text_sort.clear();
				
				for (int i = 0; i < text.length; i++) {
					if(text[i].toLowerCase().contains(edittext.getText().toString())){
						text_sort.add(text[i]);
						Log.d("log_tag", "Edit text " + text[i].toLowerCase() + "----------" +edittext.getText().toString());
					}
				}
				
				List<Daily> SearchRowItems = db.getSearch(edittext.getText().toString());
				listview.setAdapter(new CustomAdapter(CobroListActivity.this, text_sort, SearchRowItems));			   
			}
		});

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Intent intent = new Intent(CobroListActivity.this, DashboardActivity.class);
			startActivity(intent);
			finish();
         }
        return super.onKeyDown(keyCode, event);
    }

	public void showdialogQuantity(final int idTransactionx) {
		Log.d("log_tag", "%%%%%%%%%%%%%%%%%%%%%%%%%");
		final AlertDialog.Builder alert = new AlertDialog.Builder(CobroListActivity.this);
		alert.setTitle("Cantidad: ");  
		
		// Set an EditText view to get user input   
		final EditText input = new EditText(CobroListActivity.this);
		alert.setView(input);
		
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		input.setFilters(new InputFilter[] {
			// Maximum 5 characters.
			new InputFilter.LengthFilter(5),
		});

		alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {  
	     public void onClick(DialogInterface dialog, int whichButton) {  
	    	 if(!input.getText().toString().trim().equals("") && !input.getText().toString().trim().equals(null)){
	    		 Integer value = Integer.parseInt(input.getText().toString());
		         
		         if(value != 0){
		        	 Pay pago = new Pay(idTransactionx, String.valueOf(value), "1", "1");
		        	 dbpay.add(pago);
		        	 update();
	    		 }else{
					// insert error here
	    			Toast.makeText(CobroListActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
	    		 }
	    	 }else{
	    		Toast.makeText(CobroListActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
	    	 }
	    	 
	    	 return;                  
	        }  
	      });  
	
	     alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int which) {
	             // TODO Auto-generated method stub
	             return;   
	         }
	     });				
		
	    AlertDialog alertToShow = alert.create();
		alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		alertToShow.show();	
    }
	
	

	public void showDialogDelete(final int idTransactionx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(CobroListActivity.this);
        builder.setTitle("Atención");
        builder.setMessage("¿Está seguro de eliminar este pago?")
                   .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dbpay.delete(idTransactionx);
                	   update();
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
	
	
	
	
	/*
 
    private class StartNewTransactionDialog extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
		
    	protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
    		Log.d("log_tag", "******************** TRANSACTION BACKGROUND 1 ::::");
            ArrayList<String> result = new ArrayList<String>();            
            result.add("lol");
            Log.d("log_tag", "******************** TRANSACTION BACKGROUND 2 ::::");
//        	get passed value
            ArrayList<String> value = passing[0]; 
            String codeCustomer = "";
            for (String s : value){
            	codeCustomer = s;
            }
            Log.d("log_tag", "******************** TRANSACTION BACKGROUND 3 ::::");
    		try {
    			Log.d("log_tag", "******************** TRANSACTION BACKGROUND 4 ::::");
    			
    			customer = db.getCustomerByCode(codeCustomer);
    			if (customer != null){    				
    				Log.d("log_tag", " TRANSACTION CUSTOMER ::::" + customer.getCode());
        			result.add("NO_FAIL");
        	        
        			//get Date, Hour Now
        			Calendar c = Calendar.getInstance();
        			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        			String formattedDate = df.format(c.getTime());        			
        			Log.d("log_tag", "******************** TRANSACTION BACKGROUND 5 ::::");
        			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(ClientsPrestamoListActivity.this, "", null, 1);
        			
        			// Create New Transaction        			
        			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", codeCustomer, "", "pending", transactionTpye, "regular", 
        					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
        			
        			if(idNewTransaction != null && idNewTransaction != 0){
        				int codeTransaction = (int) (long) idNewTransaction;        				
        				result.add("CODE_OK");
        				result.add(String.valueOf(codeTransaction));
        			}else{
        				result.add("CODE_FAIL");
        				// insert error here
        				Log.d("log_tag", "Fucking code not match!!!!");
        			}
        			Log.d("log_tag", "******************** TRANSACTION BACKGROUND 6 ::::");
        		}else{
        			Log.d("log_tag", "******************** TRANSACTION BACKGROUND 7 ::::");
        			Log.d("log_tag", "TRANSACTION CUSTOMER :::: NO es un cliente valido");
        			result.add("EPIC_FAIL");
        		}
			} catch (Exception e) {
				Log.d("log_tag", "FAIL CODE");
				Log.d("log_tag", "******************** TRANSACTION BACKGROUND 8 ::::");
			}    		
    		Log.d("log_tag", "******************** TRANSACTION BACKGROUND 9 ::::");
            return result; //return result
        }

    	
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Log.d("log_tag", " TRANSACTION PRE EXECUTE::::");
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
    			Log.d("log_tag", "********************  EPIC_FAIL");
    			// Dialog Error found client
    			AlertDialog alertDialog = new AlertDialog.Builder(ClientsPrestamoListActivity.this).create();
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
    				Log.d("log_tag", "NO_FAIL");
    				if(result.get(2).matches("CODE_OK")){
    					Log.d("log_tag", "POST::::" + result.get(3));
    					Intent intentNewTransaction = new Intent(ClientsPrestamoListActivity.this, TransactionActivity.class);            	
    					// get Client Info
    	    			Bundle bundle = new Bundle();
    	    			bundle.putString("newTransactionCode", result.get(3));
    					intentNewTransaction.putExtras(bundle);
    	    			startActivity(intentNewTransaction);
    				}else{
    					Log.d("log_tag", "NO_FAIL else");
    					// Dialog Error found client
    	    			AlertDialog alertDialog = new AlertDialog.Builder(ClientsPrestamoListActivity.this).create();
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
    		Toast.makeText(ClientsPrestamoListActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }

    
    
    */
	
    class CustomAdapter extends BaseAdapter {
  	  String[] data_text;
  	  Context context;
  	  List<Daily> rowCustomers;

  	  CustomAdapter() { }

		CustomAdapter(Context context, String[] text, List<Daily> items) {
			data_text = text;
			this.rowCustomers = items;
		}

		CustomAdapter(Context context, ArrayList<String> text, List<Daily> items) {
			this.rowCustomers = items;
			data_text = new String[text.size()];
			for (int i = 0; i < text.size(); i++) {
				data_text[i] = text.get(i);
			}
		}
  	  
		public int getCount() {
			return data_text.length;
		}
		public String getItem(int position) { return null; }
		
		public Object getItemCustom(int position) {
			return rowCustomers.get(position);
		}
		public long getItemId(int position) {
			return rowCustomers.indexOf(getItem(position));
		}

  	  	/* private view holder class */
		private class ViewHolder {
			TextView cobroId;
			TextView txtName;
			TextView txtAddress;
			TextView txtVoucher;
			TextView txtAmmount;
			TextView txtSaldo;
			ImageView go;
		}
      
  	  	public View getView(int position, View convertView, ViewGroup parent) {
  	  		ViewHolder holder = null;
	  		if (convertView == null) {
	  			LayoutInflater inflater = getLayoutInflater();
	  			convertView = inflater.inflate(R.layout.row_cobros_list, parent, false);

  				holder = new ViewHolder();
  				holder.cobroId = (TextView) convertView.findViewById(R.id.id);
  				holder.txtName = (TextView) convertView.findViewById(R.id.tdIProduct);
  				holder.txtAddress = (TextView) convertView.findViewById(R.id.tdQuantity);
  				holder.txtVoucher = (TextView) convertView.findViewById(R.id.transactionCoordinate);
  				holder.txtAmmount = (TextView) convertView.findViewById(R.id.transactionDateTime);
		        holder.txtSaldo = (TextView) convertView.findViewById(R.id.ammount);
		        //holder.go = (ImageView) convertView.findViewById(R.id.imageView1);
		        holder.go = (ImageView) convertView.findViewById(R.id.imageViewCobroLIst);
		        
		        convertView.setTag(holder);
		    }
	  		else {
		    	 holder = (ViewHolder) convertView.getTag();
		    }
	  		Daily rowItem = (Daily) getItemCustom(position);

	  		holder.cobroId.setText(String.valueOf(rowItem.getID()));
			holder.txtName.setText(String.valueOf(rowItem.getCustomerName()));
			holder.txtAddress.setText(rowItem.getCustomerAddress());
			holder.txtVoucher.setText("Factura: " + rowItem.getVoucher());
			holder.txtAmmount.setText("Monto Total: " + rowItem.getAmmount() + " Bs.");
			
			Pay payed = dbpay.get_by_daily(rowItem.getID());
			Double paymont;
			Double saldo = 0.0;
			holder.go.setImageResource(0);
			
			try {
				paymont = Double.parseDouble(payed.getAmmount());
			} catch (Exception e) {
				paymont = Double.parseDouble("0.0");
			}
			
			if (rowItem.getSaldo().toString().equals("null")){
				if (paymont.equals(0.0)) {
					saldo = Double.parseDouble(rowItem.getAmmount());
					holder.go.setImageResource(R.drawable.ic_launcher);
				}else{
					saldo = Double.parseDouble(rowItem.getAmmount()) - paymont;
					holder.go.setImageResource(R.drawable.icook);
				}
			}else{ 
				Double submonto = Double.parseDouble(rowItem.getAmmount()) - Double.parseDouble(rowItem.getSaldo());
				if (paymont.equals(0.0)) {
					saldo = submonto;
					holder.go.setImageResource(R.drawable.ic_launcher);
				}else{
					saldo = submonto - paymont;
					holder.go.setImageResource(R.drawable.icook);
				}
			}
			holder.txtSaldo.setText("Saldo: " + String.format("%.2f", saldo) + " Bs.");
			
			return convertView;
  	  	}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		
		// create class object
		String id = (String) ((TextView) arg1.findViewById(R.id.id)).getText();
		idDaily = Integer.valueOf(id);
		
		Log.d("log_tag", "CLICKCKCKC ...._> " + idDaily);
		//showdialogQuantity(id);
		//showDialog(DIALOGO_ADDPAY);
		
		
		
		
		
		
		
		
		
		Pay payed = dbpay.get_by_daily(idDaily);
		Double paymont;
		
		try {
			paymont = Double.parseDouble(payed.getAmmount());
		} catch (Exception e) {
			paymont = Double.parseDouble("0.0");
		}
		

		if (paymont.equals(0.0)) {
			showDialog(DIALOGO_ADDPAY);
		}else{
			showDialog(DIALOGO_DELETEPAY);
		}
		
		
		//get Date, Hour Now
		/*Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String formattedDate = df.format(c.getTime());        			
		DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(CobroListActivity.this, "", null, 1);
		*/
		
		/*Intent intentNewTransaction = new Intent(CobroListActivity.this, PayActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("idDaily", id);
		intentNewTransaction.putExtras(bundle);
		startActivity(intentNewTransaction);
		finish();*/
	}
	
	private void update() { // refresh listview
	    rowItems = db.getAll();
		text = db.getAllNames();
		Log.d("log_tag", "SELECT DAILIES:::::::: ITEMS::: " + rowItems);
		listview.setAdapter(new CustomAdapter(this, text, rowItems));		
		listview.setOnItemClickListener(this);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.i("log_tag", "onCreateDialog:::::: " + id);
		Dialog dialogo = null;
		if (id == 1){
			dialogo = addPayDialog();
			return dialogo;
		}
		if (id == 2) {
			dialogo = deletePayDialog();
			return dialogo;
		}
		return dialogo;
    }
	
	private Dialog addPayDialog(){
    	final String[] items = {"Adicionar Pago"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Pagos");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	if (item == 0){
    	    		showdialogQuantity(idDaily);
    	    	}
    	    }
    	});    	    	
    	return builder.create();
    }
	
	private Dialog deletePayDialog(){
    	final String[] items = {"Eliminar Pago"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Pagos");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	if (item == 0){
    	    		showDialogDelete(idDaily);
    	    	}
    	    }
    	});    	    	
    	return builder.create();
    }
}