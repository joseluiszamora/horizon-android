package com.horizon.main;

import java.math.BigDecimal;
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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.horizon.webservice.GPSTracker;
import com.horizon.webservice.InternetDetector;
import com.horizon.webservice.JSONParser;
import com.ruizmier.horizon.R;

public class TransactionActivity extends Activity implements OnItemClickListener {		

	// Progress Dialog
    private ProgressDialog pDialog;
    
	// Object make transaction
	MakeTransaction transaction = new MakeTransaction();
	
	// internet object
	final InternetDetector internet = new InternetDetector(this);
	
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
	
	
	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
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
	String clientType;
	
	TextView txtBottomPrice;
	String userMail;
	
	// Dialog options to preventa, venta directa
	private static final int DIALOGO_TRANSACTION_0 = 1;	
	//notify Icon
	private static final int NOTIF_ALERTA_ID = 1;
	
	// android unique code
	String uuid;
	
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
        userMail = user.get(SessionManager.KEY_EMAIL);
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
		        
		bundle = getIntent().getExtras();
		// Get Bundle Transaction Code
		codeTransaction = Integer.parseInt(bundle.getString("newTransactionCode"));
		Log.d("log_tag", "Codigo detransaccion ----------------->> " + codeTransaction);
		
		// Create Transaction Object
		transactionObject = dbTransactions.getTransaction(codeTransaction);
		Log.d("log_tag", "Codigo de Cliente ----------------->> " + transactionObject.getCodeCustomer());
		Log.d("log_tag", "Estado de la transaccion  ----------------->> " + transactionObject.getStatus());
				
		// Define TextViews
		TextView txtClientName = (TextView)findViewById(R.id.TransDetailInfoCustomName);
		TextView txtClientAddress = (TextView)findViewById(R.id.txtClientAddress);
		txtBottomPrice = (TextView)findViewById(R.id.totalPriceTransactionModify);
		
		if(transactionObject.getClientType().trim().equals("temporal")){
			txtClientName.setText(name);
		    txtClientAddress.setText("Transaccion temporal");
		}else{
			customer = dbCustomers.getCustomerByCode(transactionObject.getCodeCustomer());
			txtClientName.setText( "(" + customer.getCode() + ") "+ customer.getName());
		    txtClientAddress.setText(customer.getAddress());
		}
		
		// make listransaction transaction
		listView = (ListView) findViewById(R.id.contentlisttransdetails);	
	    listView.setOnItemClickListener(this);
	    rowItems = dbTransDetail.getAllTransactionDetailsForThisTransactionPending(codeTransaction);
	    adapter = new TransactionListAdapter(TransactionActivity.this, rowItems);
	    listView.setAdapter(adapter);
	    
	    // Define Buttons
		final Button btnadd = (Button)findViewById(R.id.btnConciliar);
		final Button btnSave = (Button)findViewById(R.id.btnSave);
        
        
		uuid = Secure.getString(TransactionActivity.this.getContentResolver(),Secure.ANDROID_ID);
        

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
	public static double round(double unrounded, int precision, int roundingMode) {
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
	
	
	public void setPrice(double price){
		txtBottomPrice.setText(String.format("%.2f", totalPrice));
	}
	
	public Double getPrice(){
		return totalPrice;
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
        	closeTransaction();
         }
        return super.onKeyDown(keyCode, event);
    }

	private void closeTransaction(){
		GPSTracker gps = new GPSTracker(TransactionActivity.this);
	    latitude = gps.getLatitude();
        longitude = gps.getLongitude();
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Atenci�n");
		builder.setMessage("La Transacci�n ser� finalizada.").
		setPositiveButton("Aceptar ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(getPrice() <= 0.0){
					AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
					builder.setTitle("Atenci�n");
					builder.setMessage("Esta transacci�n ser� almacenada como Transacci�n 0.").
					setPositiveButton("Aceptar ", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							transactionObject.setType("transaccion_0");
							showDialog(DIALOGO_TRANSACTION_0);
						}
					});
			        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                 dialog.dismiss();
		               }
		           });
			       AlertDialog alert = builder.create();
			       alert.show();
				}else{
					saveTransactionStatus();
				}
			}
		});
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
               }
           }); 
        AlertDialog alert = builder.create();            
        alert.show();
        
		/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Atencion");
		builder.setMessage("La Transaccion sera finalizada.").
		setPositiveButton("Ok ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				pDialog = new ProgressDialog(TransactionActivity.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Finalizando Transaccion...");
				pDialog.setCancelable(false);
				pDialog.setMax(100);
				//Toast.makeText(TransactionActivity.this, "GPS ON", Toast.LENGTH_SHORT).show();
				
				UpdateInfoAsyncDialog updateWork = new UpdateInfoAsyncDialog();
				updateWork.execute();
			}
		});
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
                 //Toast.makeText(TransactionActivity.this, "I'm Still Here", Toast.LENGTH_SHORT).show();
               }
           }); 
        AlertDialog alert = builder.create();            
        alert.show();*/
	}

	private void saveTransactionStatus(){
		transactionObject.setCoordinateFinish(latitude + " ; "+ longitude);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		transactionObject.setTimeFinish(formattedDate);
		transactionObject.setStatus("creado");
		dbTransactions.updateTransaction(transactionObject);
		
		Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
		startActivity(i);
		finish();
		
		/*pDialog = new ProgressDialog(TransactionActivity.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Finalizando Transaccion...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		
		UpdateInfoAsyncDialog updateWork = new UpdateInfoAsyncDialog();
		updateWork.execute();*/
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialogo = transactionTypeSelectionDialog();
		return dialogo;
    }
	// set Transaction 0 message
	private Dialog transactionTypeSelectionDialog(){
    	final String[] items = {"Ning�n pedido", "Negocio cerrado"};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("�Porque no se agreg� productos? ");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opci�n elegida: " + items[item]);
    	        transactionObject.setType("transaccion_0");
    	    	if (item == 0)
    	    		transactionObject.setObs("Ningun Pedido");
				if (item == 1) 
					transactionObject.setObs("Negocio Cerrado");
				Log.d("log_tag", "actualizado estado");
				showDialog(DIALOGO_TRANSACTION_0);
				
				saveTransactionStatus();
				
    	    }
    	});    	    	
    	return builder.create();
    }
	
	public void showdialogLines() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Linea:");
	    
	    final ListView modeList = new ListView(TransactionActivity.this);
	    List<Line> rowItemsLines = dbLine.getAllLines();

		DialogLineAdapter modeAdapter = new DialogLineAdapter(TransactionActivity.this, rowItemsLines);	    
	     
	     modeList.setAdapter(modeAdapter);
		
	     modeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	     builder.setView(modeList);
	     
		final Dialog dialog = builder.create();
		
		dialog.show();
		
		
		modeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	    	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    		 String line_id = (String) ((TextView) view.findViewById(R.id.line_id)).getText();

	    		 Log.d("log_tag", " LINE ID:  > " + line_id);
	    		 if(line_id != null && Integer.parseInt(line_id) != 0){
	    			 transaction.setLine(Integer.parseInt(line_id));
		    		 showdialogVolumes();
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
		
	public void showdialogVolumes() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Volumen:");
	     
	    final ListView modeList = new ListView(TransactionActivity.this);
	    
	    Log.d("log_tag", " SHOW VOLUMES BY LINE:  > " + transaction.getLine());
		List<Volume> rowItems = dbVolume.getAllVolumesForLine(transaction.getLine());
		DialogVolumeAdapter modeAdapter = new DialogVolumeAdapter(TransactionActivity.this, rowItems);	    
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
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
	
	public void showdialogProducts() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TransactionActivity.this, R.style.CustomDialogTheme));
	    builder.setTitle("Producto:");
	     
	    final ListView modeList = new ListView(TransactionActivity.this);
		List<Product> rowItems = dbProduct.getAllProductsForLineVolume(transaction.getLine(), transaction.getVolume());
		DialogProductAdapter modeAdapter = new DialogProductAdapter(TransactionActivity.this, rowItems);	
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
	    		 }
	    		 dialog.cancel();
	    	 }
	     });
    }
	
	public void showdialogQuantity() {	
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(TransactionActivity.this);
		alert.setTitle("Cantidad: ");  
		
		// Set an EditText view to get user input   
		final EditText input = new EditText(TransactionActivity.this);
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
		        	 transaction.setQuantity(value);
	    			 createNewTransactionDetail(transaction);
	    		 }else{
					// insert error here
	    			Toast.makeText(TransactionActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
	    		 }
	    	 }else{
	    		Toast.makeText(TransactionActivity.this, "Debe introducir una cantidad valida", Toast.LENGTH_SHORT).show();
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
	
	private void createNewTransactionDetail(MakeTransaction newtransaction){
		// verify again
		if (transaction.getIdTransaction() != 0 && transaction.getCodeProduct() != null && transaction.getQuantity() != 0){
			
			// verify if this transactions already have this product
			final TransactionDetail getTransDetail = dbTransDetail.getTransactionDetailIfExist(transaction.getIdTransaction(), transaction.getCodeProduct());
			final Product  getproduct = dbProduct.getProduct(transaction.getCodeProduct());
			final Double unitPrice = Double.parseDouble(getproduct.getPrice());
			
			if(getTransDetail != null){ // this product already exist in the list
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Atenci�n");
				builder.setMessage("El producto ya existe en la lista, �desea incrementar la cantidad?").
				setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						 Double totalPrice = (double) ((unitPrice * transaction.getQuantity()) + getTransDetail.getPriceTotal());
						int Quantity = (int) (transaction.getQuantity() + getTransDetail.getQuantity());
						
						// Edit Transaction Detail
						dbTransDetail.updateTransactionDetail(new TransactionDetail(getTransDetail.getID(), null, transaction.getIdTransaction(), transaction.getCodeProduct(),
								getproduct.getName(), unitPrice, Quantity, "creado", totalPrice, null));
						
						addPrice((double) ((unitPrice * transaction.getQuantity())));
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
				
				
			}else{ // product new
				Double totalPrice = (double) (unitPrice * transaction.getQuantity());
				// Add new Transaction Detail
				dbTransDetail.addTransactionDetail(new TransactionDetail(null, transaction.getIdTransaction(),  transaction.getCodeProduct(), 
						getproduct.getName(), unitPrice, transaction.getQuantity(), "creado", totalPrice, "ninguna"));
				
				addPrice(totalPrice);
			}
	        update();
		}
		else{
			Toast.makeText(TransactionActivity.this,"Error al agregar el producto", Toast.LENGTH_SHORT).show();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final String custom_id_trans_details = (String) ((TextView) arg1.findViewById(R.id.transaction_id_detail)).getText();
		final String product_name_trans_details = (String) ((TextView) arg1.findViewById(R.id.tdproduct)).getText();
		final String total_price_trans_details = (String) ((TextView) arg1.findViewById(R.id.totalPriceTransactionModify)).getText();
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("�Est� seguro de eliminar el producto: "+product_name_trans_details+"?")
	           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dbTransDetail.deleteTransactionDetail(Integer.parseInt(custom_id_trans_details));
	        	   lessPrice(Double.parseDouble(total_price_trans_details));
	        	   System.out.println("OK CLICKED");
	        	   update();
	           }
	       });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
               }
           });
 
        AlertDialog alert = builder.create();
        alert.setTitle("Atenci�n");
        alert.show();
    }
	
	private void update() { // refresh listview
		rowItems = dbTransDetail.getAllTransactionDetailsForThisTransactionPending(codeTransaction);
	    adapter = new TransactionListAdapter(TransactionActivity.this, rowItems);
	    adapter.notifyDataSetChanged();
	    listView.setAdapter(adapter);
	}
	
	// AsyncTask Finish Transaction
	private class UpdateInfoAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		if (!internet.isConnectingToInternet()) {
    			transactionObject.setStatus("creado");
	        }
    		// set GPS
			transactionObject.setCoordinateFinish(latitude + " ; "+ longitude);
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String formattedDate = df.format(c.getTime());
			transactionObject.setTimeFinish(formattedDate);
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
    		//conciliate.cancel(true);
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
    		pDialog.dismiss();
    		if(result) {
    			if (!internet.isConnectingToInternet()) {
    				transactionObject.setStatus("creado");
    				Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        			startActivity(i);
        			finish();		         
		        }else{
	    			pDialog = new ProgressDialog(TransactionActivity.this);
					pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pDialog.setMessage("Finalizando Transaccion...");
					pDialog.setCancelable(false);
					pDialog.setMax(100);
					
					SaveTransactionDialog conciliateWork = new SaveTransactionDialog();
					conciliateWork.execute();
		        }
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    		//Toast.makeText(TransactionActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	
	
	// AsyncTask Send Transaccion Web 
	private class SaveTransactionDialog extends AsyncTask<Void, Integer, Boolean> {
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		/*
    		Transaction transaction = dbTransactions.getTransaction(codeTransaction);
    		List<TransactionDetail> listTransactionDetail  = dbTransDetail.
    				getAllTransactionDetailsForThisTransaction(codeTransaction);

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
    		*/
    		// getting JSON string from URL
    		/*String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/save_transaction", "POST", paramsTransaction);
    		try {		
    			if (returnJson.trim().equals("SAVED")){
    				// Close transaction
    				transactionObject.setStatus("conciliado");
    				dbTransactions.updateTransaction(transactionObject);
    				dbTransDetail.updateAllTransactionDetailsDelivery(transactionObject.getID(), "conciliado");
    			}else{
    				transactionObject.setStatus("creado");
    				Log.d("log_tag: ", "Fallo al crear la transaccion");
    			}
    		}
    		catch (Exception e) {	    			
    			Log.d("Products: ", "ERRROOOOOOOOOOOOOOR");
    		}
    		*/
    		transactionObject.setStatus("creado");
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
    			Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
    			startActivity(i);
    			pDialog.dismiss();
    			finish();
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(TransactionActivity.this, "Transaccion ", Toast.LENGTH_SHORT).show();
    	}
    }
}