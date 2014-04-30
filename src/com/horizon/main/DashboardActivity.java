package com.horizon.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.Daily;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerDaily;
import com.horizon.database.DatabaseHandlerGps;
import com.horizon.database.DatabaseHandlerLineVolumes;
import com.horizon.database.DatabaseHandlerLines;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.DatabaseHandlerUsers;
import com.horizon.database.DatabaseHandlerVolumes;
import com.horizon.database.Gps;
import com.horizon.database.Line;
import com.horizon.database.LineVolume;
import com.horizon.database.Product;
import com.horizon.database.Transaction;
import com.horizon.database.TransactionDetail;
import com.horizon.database.Volume;
import com.horizon.reports.ClientsListActivity;
import com.horizon.reports.ClientsPrestamoListActivity;
import com.horizon.reports.CobroListActivity;
import com.horizon.reports.TabsHistoryActivity;
import com.horizon.reports.TabsReportActivity;
import com.horizon.webservice.GPSTracker;
import com.horizon.webservice.InternetDetector;
import com.horizon.webservice.JSONParser;
import com.ruizmier.horizon.R;

public class DashboardActivity extends Activity{
 	
	// Progress Dialog
    private ProgressDialog pDialog;
    
    // Creating JSON Parser object
 	JSONParser jsonParser = new JSONParser();
    
 	// Dialog options to preventa, venta directa
 	private static final int DIALOGO_PREVENTA = 1;
 	
	// Dialog options to start transaction
	private static final int DIALOGO_SELECCION = 2;
	
	// Dialog options prestamos
	private static final int DIALOGO_PRESTAMO = 3;

	// Dialog options cobros
	private static final int DIALOGO_COBRO = 4;
		
	// Session Manager Class
	SessionManager session;	
	
	// CHeck if the user exist, get GPS data, time
	StartNewTransaction nt = new StartNewTransaction();
	
	// GPS latitude longitude
	double latitude = 0.0;
    double longitude = 0.0;

    // Check new transaction type
 	String transactionTpye = null;

 	// set usermail
    String userMail;
    
    // internet object
 	InternetDetector internet;
 	
 	// notify Icon
 	private static final int NOTIF_ALERTA_ID = 1;
 	
 	// Conciliate Asynk
 	AsynkConciliateAll conciliate; 	
 	
 	DatabaseHandlerTransactions dbTransactions;			
	// Database transaction detail class
	DatabaseHandlerTransactionDetail dbTransactionDetail;
	
	DatabaseHandlerGps GpsObj;
	
	// android unique code
	String uuid; 
	
	// Gps Object
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		
		// declare buttons 
		final Button logout = (Button)findViewById(R.id.btnLogout);
		final Button btnviewlist = (Button)findViewById(R.id.btnViewList);
		final Button newtransaction = (Button)findViewById(R.id.btnNewTransaction);
		final Button update = (Button)findViewById(R.id.btnUpdate);
		final Button delivery = (Button)findViewById(R.id.btn_delivery);
		final Button history = (Button)findViewById(R.id.btn_history);
				
		// Session Manager
	    session = new SessionManager(getApplicationContext());
	    // get user name data from session
        HashMap<String, String> user = session.getUserDetails();
        // Set user name into action bar layout 
        String name = user.get(SessionManager.KEY_NAME);
        // set usermail
        userMail = user.get(SessionManager.KEY_EMAIL);
                
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
        
        internet = new InternetDetector(getApplicationContext());
        gps = new GPSTracker(DashboardActivity.this);
        
        
        
        dbTransactions = new DatabaseHandlerTransactions(getApplicationContext(), "", null, 1);
    	dbTransactionDetail = new DatabaseHandlerTransactionDetail(getApplicationContext(), "", null, '1');
    	GpsObj = new DatabaseHandlerGps(getApplicationContext(), "", null, 1);
    	uuid = Secure.getString(DashboardActivity.this.getContentResolver(),Secure.ANDROID_ID);
    	
    	conciliate = new AsynkConciliateAll();
        // Conciliar las pendientes cada 10 minutos
        if (!internet.isConnectingToInternet()) {
        	// message
			Toast toast = Toast.makeText(DashboardActivity.this, "No hay conexión a Internet", Toast.LENGTH_SHORT);
			toast.show();		         
        }else{
        	try {
        		conciliate.execute();
			} catch (Exception e) {
				// message
				Toast toast = Toast.makeText(DashboardActivity.this, "No se puede realizar la conciliacion", Toast.LENGTH_SHORT);
				toast.show();
			}
        }
                
        // rescue all transactions 
        /*List<Transaction> allTransactions = dbTransactions.getAllTransactions();
		
		for (Transaction thisTransaction : allTransactions){
			Transaction transaction = dbTransactions.getTransaction(thisTransaction.getID());
			
			transaction.setStatus("conciliado");
			dbTransactions.updateTransaction(transaction);
			dbTransactionDetail.updateAllTransactionDetailsDelivery(transaction.getID(), "conciliado");
		}*/
        
        
		// click Logout Button
		logout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
	            builder.setTitle("Atención");
	            builder.setMessage("¿Está seguro de salir de Horizon?")
	                       .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
							session.logoutUser();
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
		});
				
		// click view list
		btnviewlist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DashboardActivity.this, TabsReportActivity.class);
				startActivity(intent);				
			}		
		});
		
		// click history list
		history.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DashboardActivity.this, TabsHistoryActivity.class);
				startActivity(intent);				
			}
		});

		// click View Deliveries
		delivery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DashboardActivity.this, DeliveryActivity.class);
				startActivity(intent);				
			}		
		});
		
		// click new transaction
		newtransaction.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// check if GPS enabled
    	        if(gps.canGetLocation()){
    	        	showDialog(DIALOGO_PREVENTA);
    	        	latitude = gps.getLatitude();
        	        longitude = gps.getLongitude();
        	        
    	        }else{
    	        	// can't get location
    	            // GPS or Network is not enabled
    	            // Ask user to enable GPS/network in settings
    	            gps.showSettingsAlert();
    	        }
			}		
		});
		
		// click btn Update
		update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {		
				// Check for internet connection
		        if (!internet.isConnectingToInternet()) {
		        	// message
					Toast toast = Toast.makeText(DashboardActivity.this, "No hay conexión a Internet", Toast.LENGTH_SHORT);
	    			toast.show();		         
		        }else{
		        	AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
		            builder.setTitle("Atención");
		            builder.setMessage("Esta operación borrara cierta información, ¿está seguro?")
		                       .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		                       public void onClick(DialogInterface dialog, int id) {
		                    	   	pDialog = new ProgressDialog(DashboardActivity.this);
			       					pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			       					pDialog.setMessage("Actualizando...");
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
		        	//conciliate.cancel(true);
		        }
	    	}
		});
	
	}
	
	public void callAsynchronousTask() {
	    final Handler handler = new Handler();
	    Timer timer = new Timer();
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                    try {
	                    	AsynkConciliateAll asynk = new AsynkConciliateAll();
	                        //PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask();
	                        // PerformBackgroundTask this class is the class that extends AsynchTask 
	                    	asynk.execute();
	                    } catch (Exception e) {
	                        // TODO Auto-generated catch block
	                    }
	                }
	            });
	        }
	    };
	    timer.schedule(doAsynchronousTask, 0, 50000); //execute in every 50000 ms
	}
	
	 // Initiating Menu XML file 
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.dashboard_menu, menu);
        return true;
    }
           
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        switch (item.getItemId()){
        case R.id.menu_update:           
        	
    		InternetDetector internet = new InternetDetector(getApplicationContext());
        	 // Check for internet connection
	        if (!internet.isConnectingToInternet()) {
	        	// message
				Toast toast = Toast.makeText(DashboardActivity.this, "No hay conexión a Internet", Toast.LENGTH_SHORT);
    			toast.show();		         
	        }else{
	        	pDialog = new ProgressDialog(DashboardActivity.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Actualizando...");
				pDialog.setCancelable(false);
				pDialog.setMax(100);
				
				UpdateInfoAsyncDialog updateWork = new UpdateInfoAsyncDialog();
				updateWork.execute();
	        }
            return true;                    
 
        case R.id.menu_logout:
        	AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Atención ");
            builder.setMessage("Esta seguro de Salir de Horizon")
                       .setPositiveButton("Aceptar ", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
						session.logoutUser();
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
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }  
			
	//** Pressed return button **//
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Atención ");
            builder.setMessage("Esta seguro de Salir de Horizon")
                       .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
						session.logoutUser();
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
        return super.onKeyDown(keyCode, event);
    }
	
	// Select Dialog Create Interface
	@Override
    protected Dialog onCreateDialog(int id) {
		Log.i("log_tag", "onCreateDialog:::::: " + id);
		Dialog dialogo = null;
		if (id == 1){
			dialogo = transactionTypeSelectionDialog();
			return dialogo;
		}
		if (id == 2) {
			dialogo = makeSelectionDialog();
			return dialogo;
		}
		if (id == 3) {
			dialogo = prestamoDialog();
			return dialogo;
		}
		if (id == 4) {
			dialogo = cobroDialog();
			return dialogo;
		}
		return dialogo;
    }
		
	private Dialog transactionTypeSelectionDialog(){
    	final String[] items = {"Prestamo", "Cobro", "Preventa", "Venta Directa"};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Nueva Transacción");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opción elegidaXXXXX: " + items[item]);
    	    	if (item == 0){
    	    		Log.i("log_tag", "PRESTAMO ");
        	    	transactionTpye = "prestamo";
    				showDialog(DIALOGO_PRESTAMO);
    	    	}
				if (item == 1){
					Log.i("log_tag", "COBROOO " );
					showDialog(DIALOGO_COBRO);
				}
				if (item == 2){
					transactionTpye = "preventa";
					showDialog(DIALOGO_SELECCION);
				}
				if (item == 3){
					transactionTpye = "venta_directa";
					showDialog(DIALOGO_SELECCION);
				}
    	    }
    	});    	    	
    	return builder.create();
    }
	
    private Dialog makeSelectionDialog(){
    	final String[] items = {"Buscar en la lista", "Introducir Código", "Escanear  Código", "Transacción Temporal"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Selección de Cliente");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opción elegida: " + items[item]);    	    	
    	    	if (item == 0){
    	    		if(transactionTpye == "prestamo"){
    	    			Intent intent = new Intent(DashboardActivity.this, ClientsPrestamoListActivity.class);
    	    			Bundle bundle = new Bundle();
        				bundle.putString("transactionType", transactionTpye);
        				intent.putExtras(bundle);
        				startActivity(intent);
    	    		}else{
    	    			Intent intent = new Intent(DashboardActivity.this, ClientsListActivity.class);
    	    			Bundle bundle = new Bundle();
        				bundle.putString("transactionType", transactionTpye);
        				intent.putExtras(bundle);
        				startActivity(intent);
    	    		}
    				finish();
    	    	}
				if (item == 1) {
					 AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
					 alert.setTitle("Nueva Transacción");  
					 alert.setMessage("Ingresar Código de Cliente :");                

					  // Set an EditText view to get user input
					  final EditText input = new EditText(DashboardActivity.this);
					  alert.setView(input);
					  
					  input.setInputType(InputType.TYPE_CLASS_NUMBER);

					  input.setFilters(new InputFilter[] {
					    new InputFilter.LengthFilter(20), // maximum 20 characteres
					  });

					     alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {  
					     @SuppressWarnings("unchecked")
						public void onClick(DialogInterface dialog, int whichButton) {  
					        String value = input.getText().toString();
					        //verify if is a valid client
					        DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
					        if (dbCustomers.isRealCustomer(value) > 0){
					        	/*pDialog = new ProgressDialog(DashboardActivity.this);
								pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								pDialog.setMessage("Iniciando Nueva Transaccion...");
								pDialog.setCancelable(false);
								pDialog.setMax(100);
								
								ArrayList<String> passing = new ArrayList<String>();
								passing.add(value);
								
								StartNewTransactionDialog updateWork = new StartNewTransactionDialog();
								updateWork.execute(passing);*/
					        	//conciliate.cancel(true);
			    				
			            		Customer customer = new Customer();
			            		customer = dbCustomers.getCustomerByCode(value);
			            		if (customer != null){
			            			Calendar c = Calendar.getInstance();
			            			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			            			String formattedDate = df.format(c.getTime());        			
			            			
			            			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
			            			gps.getLocation();
			                	    latitude = gps.getLatitude();
			                        longitude = gps.getLongitude();
			            			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
			            			
			            			// Create New Transaction
			            			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", value, " ", "pending", transactionTpye, "regular", 
			            					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
			            			
				    				
				    				Intent intentNewTransaction = new Intent(DashboardActivity.this, TransactionActivity.class);            	
			    	    			Bundle bundle = new Bundle();
			    	    			bundle.putString("newTransactionCode", idNewTransaction.toString());
			    					intentNewTransaction.putExtras(bundle);
			    	    			startActivity(intentNewTransaction);
			    	    			finish();
			            		}else{        			
			            			Toast toast3 = Toast.makeText(DashboardActivity.this, "Error al iniciar la transaccion, favor intentelo de nuevo", Toast.LENGTH_SHORT);
					    			toast3.show();
			            		}
					        }else{
					        	Toast toast3 = Toast.makeText(DashboardActivity.this, "Cliente NO registrado", Toast.LENGTH_SHORT);
				    			toast3.show();
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
				if (item == 2){	
					try {
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		    			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		    			startActivityForResult(intent, 0);
					} catch (Exception e) {
						Toast toast3 = Toast.makeText(DashboardActivity.this, "NO tiene instalado un servicio para lectura de QRCode", Toast.LENGTH_SHORT);
		    			toast3.show();
					}
				}
				if (item == 3){					
					//get Date, Hour Now
	    			Calendar c = Calendar.getInstance();
	    			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    			String formattedDate = df.format(c.getTime());
	    			Log.i("TIME:::: ", "Date  => " + formattedDate);
	    			
	    			// get gps 
	    			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
					latitude = gps.getLatitude();
        	        longitude = gps.getLongitude();
	    			
	    			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
	    			
	    			// Create New Transaction
	    			Long idNewTransaction = dbTransactions.addTransaction(new Transaction(null, null, null, "pending", 
	    					transactionTpye, "temporal", formattedDate, "", latitude + " ; "+ longitude, "0.0"));
	    			
	    			if(idNewTransaction != null && idNewTransaction != 0){
	    				// cancel conciliate
	    				//conciliate.cancel(true);
	    				String codeTransaction = String.valueOf(idNewTransaction);

    					Intent intentNewTransaction = new Intent(DashboardActivity.this, TransactionActivity.class);            	
    					// get Client Info
    	    			Bundle bundle = new Bundle();
    	    			bundle.putString("newTransactionCode", codeTransaction);
    					intentNewTransaction.putExtras(bundle);
    	    			startActivity(intentNewTransaction);
	    				
	    			}else{
	    				Log.d("log_tag", "Fucking code not match!!!!");
	    			}
				}
    	    }
    	});    	    	
    	return builder.create();
    }
    
    private Dialog prestamoDialog() {
    	final String[] items = {"Buscar en la lista", "Introducir Código", "Escanear  Código"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Selección de Cliente");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opción elegida: " + items[item]);    	    	
    	    	if (item == 0){
    	    		if(transactionTpye == "prestamo"){
    	    			Intent intent = new Intent(DashboardActivity.this, ClientsPrestamoListActivity.class);
    	    			Bundle bundle = new Bundle();
        				bundle.putString("transactionType", transactionTpye);
        				intent.putExtras(bundle);
        				startActivity(intent);
    	    		}else{
    	    			Intent intent = new Intent(DashboardActivity.this, ClientsListActivity.class);
    	    			Bundle bundle = new Bundle();
        				bundle.putString("transactionType", transactionTpye);
        				intent.putExtras(bundle);
        				startActivity(intent);
    	    		}
    				finish();
    	    	}
				if (item == 1) {
					 AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
					 alert.setTitle("Nueva Transacción");  
					 alert.setMessage("Ingresar Código de Cliente :");                

					  // Set an EditText view to get user input
					  final EditText input = new EditText(DashboardActivity.this);
					  alert.setView(input);
					  
					  input.setInputType(InputType.TYPE_CLASS_NUMBER);

					  input.setFilters(new InputFilter[] {
					    new InputFilter.LengthFilter(20), // maximum 20 characteres
					  });

					     alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {  
					     @SuppressWarnings("unchecked")
						public void onClick(DialogInterface dialog, int whichButton) {  
					        String value = input.getText().toString();
					        Log.d( "log_tag", "Pin Value : " + value);
					        //verify if is a valid client
					        DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
					        if (dbCustomers.isRealCustomer(value) > 0){
			            		Customer customer = new Customer();
			            		customer = dbCustomers.getCustomerByCode(value);
			            		if (customer != null){
			            			Calendar c = Calendar.getInstance();
			            			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			            			String formattedDate = df.format(c.getTime());        			
			            			
			            			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
			            			gps.getLocation();
			                	    latitude = gps.getLatitude();
			                        longitude = gps.getLongitude();
			            			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
			            			
			            			// Create New Transaction
			            			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", value, " ", "pending", transactionTpye, "regular", 
			            					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
			            			
				    				
				    				Intent intentNewTransaction = new Intent(DashboardActivity.this, TransactionActivity.class);            	
			    	    			Bundle bundle = new Bundle();
			    	    			bundle.putString("newTransactionCode", idNewTransaction.toString());
			    					intentNewTransaction.putExtras(bundle);
			    	    			startActivity(intentNewTransaction);
			    	    			finish();
			            		}else{        			
			            			Toast toast3 = Toast.makeText(DashboardActivity.this, "Error al iniciar la transaccion, favor intentelo de nuevo", Toast.LENGTH_SHORT);
					    			toast3.show();
			            		}
					        }else{
					        	Toast toast3 = Toast.makeText(DashboardActivity.this, "Cliente NO registrado", Toast.LENGTH_SHORT);
				    			toast3.show();
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
				if (item == 2){	
					try {
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		    			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		    			startActivityForResult(intent, 0);
					} catch (Exception e) {
						Toast toast3 = Toast.makeText(DashboardActivity.this, "NO tiene instalado un servicio para lectura de QRCode", Toast.LENGTH_SHORT);
		    			toast3.show();
					}
				}
    	    }
    	});    	    	
    	return builder.create();
    }
	private Dialog cobroDialog() {
		final String[] items = {"Buscar en la lista", "Introducir Numero de Factura"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Selección de Cobro");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("log_tag", "Opción elegida: " + items[item]);    	    	
    	    	if (item == 0){
    	    		Intent intent = new Intent(DashboardActivity.this, CobroListActivity.class);
	    			Bundle bundle = new Bundle();
    				bundle.putString("transactionType", transactionTpye);
    				intent.putExtras(bundle);
    				startActivity(intent);
    				finish();
    	    	}
				if (item == 1) {
					AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
					alert.setTitle("Nuevo Cobro");  
					alert.setMessage("Ingresar Numero de Factura :");                
					
					// Set an EditText view to get user input
					final EditText input = new EditText(DashboardActivity.this);
					alert.setView(input);
					
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setFilters(new InputFilter[] {
							new InputFilter.LengthFilter(20), // maximum 20 characteres
					});

					alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {  
						@SuppressWarnings("unchecked")
						public void onClick(DialogInterface dialog, int whichButton) {  
							String value = input.getText().toString();
							Log.d( "log_tag", "Pin Value : " + value);
							return;
						}
					});
					
					alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
					AlertDialog alertToShow = alert.create();
					alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					alertToShow.show();

	    	        
				}
				if (item == 2){	
					try {
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		    			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		    			startActivityForResult(intent, 0);
					} catch (Exception e) {
						Toast toast3 = Toast.makeText(DashboardActivity.this, "NO tiene instalado un servicio para lectura de QRCode", Toast.LENGTH_SHORT);
		    			toast3.show();
					}
				}
    	    }
    	});    	    	
    	return builder.create();
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (requestCode == 0) {
    		if (resultCode == RESULT_OK) {
	            String codeScan = intent.getStringExtra("SCAN_RESULT").toString();
	            
	            Log.i("log_tag", "Opción elegida: " + codeScan);   
 
	            //verify if is a valid client
		        DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
		        if (dbCustomers.isRealCustomer(codeScan) > 0){
		        	/*pDialog = new ProgressDialog(DashboardActivity.this);
					pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pDialog.setMessage("Iniciando Transaccion...");
					pDialog.setCancelable(false);
					pDialog.setMax(100);
					
					ArrayList<String> passing = new ArrayList<String>();
					passing.add(codeScan);
					StartNewTransactionDialog updateWork = new StartNewTransactionDialog();
					updateWork.execute(passing);*/
    				Customer customer = new Customer();
            		customer = dbCustomers.getCustomerByCode(codeScan);
            		if (customer != null){
            			Calendar c = Calendar.getInstance();
            			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            			String formattedDate = df.format(c.getTime());        			
            			
            			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
            			gps.getLocation();
                	    latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
            			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
            			
            			// Create New Transaction
            			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", codeScan, " ", "pending", transactionTpye, "regular", 
            					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
            			
	    				
	    				Intent intentNewTransaction = new Intent(DashboardActivity.this, TransactionActivity.class);            	
    	    			Bundle bundle = new Bundle();
    	    			bundle.putString("newTransactionCode", idNewTransaction.toString());
    					intentNewTransaction.putExtras(bundle);
    	    			startActivity(intentNewTransaction);
    	    			finish();
            		}else{        			
            			Toast toast3 = Toast.makeText(DashboardActivity.this, "Error al iniciar la transaccion, favor intentelo de nuevo", Toast.LENGTH_SHORT);
		    			toast3.show();
            		}
		        }else{
		        	Toast toast3 = Toast.makeText(DashboardActivity.this, "Cliente NO registrado", Toast.LENGTH_SHORT);
	    			toast3.show();
		        }		
	            
    		} else if (resultCode == RESULT_CANCELED) {
    			Toast toast3 = Toast.makeText(this, "No se puede reconocer el Código", Toast.LENGTH_SHORT);
    			toast3.show();
    		}
    	}
    }
    
    private class UpdateInfoAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
		// Clients JSONArray
		JSONArray clients = null;
		// Products JSONArray
		JSONArray products = null;
		// Daily JSONArray
		JSONArray daily = null;
			
		// Creating JSON Parser object
		JSONParser jsonParser = new JSONParser();
		
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		/** UPDATE DATABASE PRODUCTS  **/
    		final DatabaseHandlerProducts dbProducts = new DatabaseHandlerProducts(DashboardActivity.this, "", null, '1');
    		// delete All Products
    		dbProducts.clearTable();
    		// Building Parameters
    		List<NameValuePair> paramsProducts = new ArrayList<NameValuePair>();
    		// getting JSON string from URL
    		String jsonProducts = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_products", "GET", paramsProducts);
    		
    		Log.d("log_tag", "PRODUCTOS JSON: > " + jsonProducts); // Check your log cat for JSON reponse

    		try {				
    			products = new JSONArray(jsonProducts);
    			if (products != null) {
    				// looping through All albums
    				for (int i = 0; i < products.length(); i++) {					
    					JSONObject c = products.getJSONObject(i);
    					// Storing each json item values in variable
    					Long idProduct = c.getLong("idProduct");
    				 	String Nombre = c.getString("Nombre");
    				 	String LineVolume = c.getString("idLineVolume");
    				 	String PrecioUnitario = c.getString("PrecioUnit");
    				 	String Descripcion = c.getString("Descripcion");
    				 	
    				 	Log.e("log_tag", idProduct + "---" + Nombre+ "---" + LineVolume+ "---" +PrecioUnitario+ "---" + Descripcion);
    				 	dbProducts.addProduct(new Product(idProduct, Integer.parseInt(LineVolume), Nombre, Double.parseDouble(PrecioUnitario), Descripcion, "activo"));				 	
    				}
    			}else{
    				Log.d("Products: ", "null");
    			}
    		}
    		catch (Exception e) {}

    		/** UPDATE DATABASE DAILY **/
    		final DatabaseHandlerDaily dbDaily = new DatabaseHandlerDaily(DashboardActivity.this, "", null, '1');
    		// delete All
    		dbDaily.clearTable();
    		// Building Parameters
    		List<NameValuePair> paramsDaily = new ArrayList<NameValuePair>();
    		JSONObject objectDaily = new JSONObject();
    		try {
    			objectDaily.put("userMail", userMail);
			} catch (JSONException e1) {
				Log.d("log_tag","mail from the user failll :(");
			}
    		paramsDaily.add(new BasicNameValuePair("codeCustomer", objectDaily.toString()));
    		
    		// getting JSON string from URL
    		String jsonDaily = jsonParser.makeHttpRequest
    				("http://www.mariani.bo/horizon-sc/index.php/webservice/get_daily", "POST", paramsDaily);
    		
    		Log.d("log_tag", "PRODUCTOS JSON: > " + jsonDaily);
    		
    		try {				
    			daily = new JSONArray(jsonDaily);
    			if (daily != null) {
    				// looping through All
    				for (int i = 0; i < daily.length(); i++) {					
    					JSONObject c = daily.getJSONObject(i);
    					// Storing each json item values in variable
    					String idweb = c.getString("iddiario");
    					String idTransaction = c.getString("idTransaction");
    					String idCustomer = c.getString("idCustomer");
    					String fechaTransaction = c.getString("FechaTransaction");
    				 	String voucher = c.getString("NumVoucher");
    				 	String type = c.getString("Type");
    				 	String ammount = c.getString("Monto");
    				 	String pagado = c.getString("pagado");
    				 	String code = c.getString("code");
    				 	String custname = c.getString("custname");
    				 	String custaddress = c.getString("custaddress");
    				 	String status = c.getString("Estado");
    				 	
    				 	Log.d("log_tag", "NEW DAILY: > " + idweb);
    				 	
    				 	dbDaily.add(new Daily(Integer.parseInt(idweb), Integer.parseInt(idTransaction), Integer.parseInt(idCustomer), 
    				 			fechaTransaction, voucher, type, ammount, pagado, code, custname, custaddress, status));
    				}
    			}else{
    				Log.d("log_tag", "null");
    			}
    		}
    		catch (Exception e) { }
    		
    		
    		/** UPDATE DATABASE CUSTOMERS **/ 
    		final DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
    		// delete All Clients
    		dbCustomers.clearTable();		
    		//dbCustomers.deleteAllCustomers("all");
    		
    		// Building Parameters
    		List<NameValuePair> paramsCustomers = new ArrayList<NameValuePair>();

    		//userMail
    		JSONObject objectTransaction = new JSONObject();
    		
    		try {
				objectTransaction.put("userMail", userMail);
			} catch (JSONException e1) {
				Log.d("log_tag","mail from the user failll :(");
			}
    		
    		paramsCustomers.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	
    		
    		Log.d("log_tag", ">**  " + paramsCustomers);
    		
    		// getting JSON string from URL
    		String json = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_customers", "POST", paramsCustomers);
    		
    		// Check your log cat for JSON reponse
    		Log.d("log_tag", "CUSTOMERS RECEIBED > " + json);

    		try {				
    			clients = new JSONArray(json);
    			
    			if (clients != null) {
    				// looping through All albums
    				for (int i = 0; i < clients.length(); i++) {					
    					JSONObject c = clients.getJSONObject(i);

    					// Storing each json item values in variable
    					String codeCustomer = c.getString("CodeCustomer");
    				 	String NombreTienda = c.getString("NombreTienda");
    				 	String NombreContacto = c.getString("NombreContacto");
    				 	String Direccion = c.getString("Direccion");
    				 	String Telefono = c.getString("Telefono");
    				 	String TelfCelular = c.getString("TelfCelular");
    				 	String rank = c.getString("rank");
    				 	
    				 	Log.e("log_tag", codeCustomer + "---" + NombreTienda+ "---" + NombreContacto+ "---" + Direccion
    				 			+ "---" + Telefono+ "---" + TelfCelular+ "---");
    				 	
    				 	
    				 	dbCustomers.addCustomer(new Customer(codeCustomer, NombreTienda, NombreContacto, Direccion, Telefono, TelfCelular, "activo", Integer.parseInt(rank) ));				 	
    				}
    			}else{
    				Log.d("Customers: ", "null");
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    		}    		    		    		
    		
    		/** UPDATE DATABASE LINES **/
    		DatabaseHandlerLines dbLine = new DatabaseHandlerLines(DashboardActivity.this, "", null, '1');
    		dbLine.clearTable();
    		// Building Parameters
    		List<NameValuePair> paramsLines = new ArrayList<NameValuePair>();
    		String jsonLines = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_lines", "GET", paramsLines);
			
    		Log.d("log_tag", "JSON LINES >" + jsonLines);
    		
    		try {				
    			products = new JSONArray(jsonLines);
    			if (products != null) {
    				// looping through All albums
    				for (int i = 0; i < products.length(); i++) {					
    					JSONObject c = products.getJSONObject(i);
    					// Storing each json item values in variable
    					String idLine = c.getString("idLine");
    				 	String Desc = c.getString("Descripcion");
    				 	
    				 	Log.e("log_tag", "Construyendo Lineas > " + idLine + "---" + Desc+ "---");
    				 	
    				 	dbLine.addLine(new Line(Integer.parseInt(idLine), Desc));
    				}
    			}else{
    				Log.d("Lines: ", "null");
    			}
    		}
    		catch (Exception e) {
    	       Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
    		}

    		/** UPDATE DATABASE VOLUMES **/
    		
    		// Database Volume class
    		DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(DashboardActivity.this, "", null, '1');
    		dbVolume.clearTable();
    		
    		// Building Parameters
    		List<NameValuePair> paramsVolumes = new ArrayList<NameValuePair>();
    		// getting JSON string from URL
    		String jsonVolumes = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_volumes", "GET", paramsVolumes);
    		
    		try {				
    			products = new JSONArray(jsonVolumes);
    			if (products != null) {
    				// looping through All albums
    				for (int i = 0; i < products.length(); i++) {					
    					JSONObject c = products.getJSONObject(i);
    					// Storing each json item values in variable
    					String idVolume = c.getString("idVolume");
    				 	String Desc = c.getString("Descripcion");
    				 	
    				 	Log.e("log_tag", idVolume + "---" + Desc+ "---");
    				 	dbVolume.addVolume(new Volume(Integer.parseInt(idVolume), Desc));
    				}
    			}else{
    				Log.d("Lines: ", "null");
    			}
    		}
    		catch (Exception e) {
    	       Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
    		}    		    		

    		/** UPDATE DATABASE LINEVOLUMES **/   		
    		// Database Line Volume class
    		DatabaseHandlerLineVolumes dbLineVolume = new DatabaseHandlerLineVolumes(DashboardActivity.this, "", null, '1');
    		dbLineVolume.clearTable();
    		
    		// Building Parameters
    		List<NameValuePair> paramsLineVolumes = new ArrayList<NameValuePair>();
    		// getting JSON string from URL
    		String jsonLineVolumes = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_linevolumes", "GET", paramsLineVolumes);
    		
    		//Log.d("PRODUCTOS JSON: ", "> " + jsonProducts); // Check your log cat for JSON reponse
    				
    		try {				
    			products = new JSONArray(jsonLineVolumes);
    			if (products != null) {
    				// looping through All albums
    				for (int i = 0; i < products.length(); i++) {					
    					JSONObject c = products.getJSONObject(i);
    					// Storing each json item values in variable
    					String idWeb = c.getString("idLineVolume");
    				 	String idLine = c.getString("idLine");
    				 	String idVolume = c.getString("idVolume");
    				 	
    				 	Log.e("log_tag", idWeb + "---" + idLine+ "---" + idVolume);
    				 	dbLineVolume.addLineVolume(new LineVolume(Integer.parseInt(idWeb), Integer.parseInt(idLine), Integer.parseInt(idVolume), ""));
    				}
    			}else{
    				Log.d("LineVolumes: ", "null");
    			}
    		}
    		catch (Exception e) {
    	       Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
    		}

    		/** UPDATE DATABASE TRANSACTIONS **/
	   		// Database transaction class
    		DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
    		// Database transaction detail class
    		DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(DashboardActivity.this, "", null, '1');
    			
    		
    		// limpiar las tablas
    		//dbTransactions.clearTable();
    		//dbTransDetail.clearTable();
    		dbTransactions.deleteAllTransactions("all");
    		dbTransDetail.deleteAllTransactions("all");
    		
    		/** UPDATE DATABASE TRANSACTIONS DELIVERY  **/
    		JSONObject objectT = new JSONObject();
    		
    		try {
    			objectT.put("userMail", userMail);
			} catch (JSONException e1) {
				Log.d("log_tag","mail from the user failll :(");
			}
    		
    		List<NameValuePair> paramsTrans = new ArrayList<NameValuePair>();
    		paramsTrans.add(new BasicNameValuePair("codeCustomer", objectT.toString()));	
    		
    		Log.d("log_tag", ">*****  " + paramsTrans);

    		// getting JSON string from URL
    		String jsonTrans = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/get_transactions_for_this_user", "POST", paramsTrans);
    		
    		
    		Log.d("log_tag", "TRANSACCIONES PENDIENTES:  > " + jsonTrans); // Check your log cat for JSON reponse
    		try {				
    			JSONArray transactions = new JSONArray(jsonTrans);
    			if (transactions != null && transactions.length() >0) {
    				Log.d("log_tag", "SI EXISTEN TRANSACCIONES PENDIENTES *** "); // Check your log cat for JSON reponse
    				// looping through All albums
    				for (int i = 0; i < transactions.length(); i++) {	
    					Log.d("log_tag", "Transaccion YEAAA::::");
    					JSONObject c = transactions.getJSONObject(i);
    					// Storing each json item values in variable
    					String idWeb = c.getString("idTransaction");
    					String customer = c.getString("customer");

    				 	JSONArray transactionList = c.getJSONArray("transactionsList");
    				 	int id_trans = (int) (long) dbTransactions.addTransaction(new Transaction(idWeb, customer, " ", "por_entregar", "preventa", "regular", "", "", "0.0", "0.0"));
    				 	
    				 	for (int j = 0; j < transactionList.length(); j++) {	
        					Log.d("log_tag", "Transaccion YEAAA::::");
        					JSONObject t = transactionList.getJSONObject(j);
        					// Storing each json item values in variable
        					
        					String idDetailTransaction = t.getString("idDetailTransaction");
        					int idTransaction = t.getInt("idTransaction");
        				 	String idProduct = t.getString("idProduct");
        				 	String productName = t.getString("productName");
        				 	double precio = t.getDouble("precio");
        				 	int Cantidad = t.getInt("Cantidad");
        				 	double priceTotal = t.getDouble("priceTotal");
        				 	
        				 	Log.e("log_tag", "TRANSACCIONES " + idDetailTransaction + "---" +idTransaction
        				 			+ "---" + idProduct + "---" + productName+ "---" + precio + "---" + Cantidad+ "---" + priceTotal);
        				 	
        				 	dbTransDetail.addTransactionDetail(new TransactionDetail(idDetailTransaction, id_trans,  idProduct, 
        				 			productName, precio, Cantidad, "por_entregar", priceTotal, "ninguna"));
        				}
    				}
    			}else{
    				Log.d("log_tag", "NOOO EXISTEN TRANSACCIONES PENDIENTES *** "); // Check your log cat for JSON reponse
    			}
    		}
    		catch (Exception e) {
    			Log.d("LineVolumes: ", "null");
    		}
    		
			/** UPDATE DATABASE GPS **/
    		DatabaseHandlerGps dbGps = new DatabaseHandlerGps(DashboardActivity.this, "", null, '1');
    		dbGps.clearTable();
    		
    		return true;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		conciliate.cancel(true);
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
    			Toast.makeText(DashboardActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    		}
    		conciliate = new AsynkConciliateAll();
    		conciliate.execute();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(DashboardActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
          
    private class StartNewTransactionDialog extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
		
    	protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
            ArrayList<String> result = new ArrayList<String>();            
            result.add("lol");
            
//        	get passed value
            ArrayList<String> value = passing[0]; 
            String codeCustomer = "";
            for (String s : value){
            	codeCustomer = s;
            }           
            try {
            	DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
        		Customer customer = new Customer();
        		customer = db.getCustomerByCode(codeCustomer);
        		if (customer != null){
        			result.add("NO_FAIL");
        	        
        			//get Date, Hour Now
        			Calendar c = Calendar.getInstance();
        			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        			String formattedDate = df.format(c.getTime());
        			Log.i("TIME:::: ", "Date  => " + formattedDate);
        			
        			DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(DashboardActivity.this, "", null, 1);
        			
        			// Create New Transaction
        			Long idNewTransaction = dbTransactions.addTransaction(new Transaction("", codeCustomer, " ", "pending", transactionTpye, "regular", 
        					formattedDate, formattedDate, latitude + " ; "+ longitude, latitude + " ; "+ longitude));        			
        			
        			if(idNewTransaction != null && idNewTransaction != 0){
        				int codeTransaction = (int) (long) idNewTransaction;        			
        				result.add("CODE_OK");
        				result.add(String.valueOf(codeTransaction));
        			}else{
        				result.add("CODE_FAIL");
        				// insert error here        			
        			}
        		}else{        			
        			result.add("EPIC_FAIL");
        		}
			} catch (Exception e) {				
			}
            
            
            
            return result; //return result
        }

    	
    	protected void onProgressUpdate(Integer... values) {
    		int progreso = values[0].intValue();
    		
    		pDialog.setProgress(progreso);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		// Gps Object
			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
    		if(gps.canGetLocation()){    		
	        	latitude = gps.getLatitude();
    	        longitude = gps.getLongitude();    	            	      
        		pDialog.setOnCancelListener(new OnCancelListener() {
    				@Override
    				public void onCancel(DialogInterface dialog) {
    					StartNewTransactionDialog.this.cancel(true);
    				}
    			});
        		
        		pDialog.setProgress(0);
        		pDialog.show();
    	        
	        }else{
	        	// can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings	        
	        	gps.showSettingsAlert();
	            //Toast.makeText(DashboardActivity.this, "Necesitas Activar el GPS para ralizar la transaccion", Toast.LENGTH_SHORT).show();
	        }
    	}
    	
    	@Override
    	protected void onPostExecute(ArrayList<String> result) {
    		String tmp = result.get(1);    		
    		if (tmp.matches("EPIC_FAIL")){
    			// Dialog Error found client
    			AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity.this).create();
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
    					Intent intentNewTransaction = new Intent(DashboardActivity.this, TransactionActivity.class);            	
    					// get Client Info
    	    			Bundle bundle = new Bundle();
    	    			bundle.putString("newTransactionCode", result.get(3));
    					intentNewTransaction.putExtras(bundle);
    	    			startActivity(intentNewTransaction);
    				}else{
    					// Dialog Error found client
    	    			AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity.this).create();
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
    		Toast.makeText(DashboardActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
          
    private class AsynkConciliateAll extends AsyncTask<Void, Integer, Boolean> {
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		Log.d("log_tag", "---------------  Iniciando Conciliacion automatica  doInBackground GPS -----------------");
    		// Verificar si el usuario ahun esta activo
    		Boolean checkifvalide = true;    		
    		if (internet.isConnectingToInternet()) {
    			try {
    				/** Conciliate all the GPS Position **/
    				List<Gps> allGps = null;
    				try {
    					allGps = GpsObj.getAllGps();
					} catch (Exception e) {
						// TODO: handle exception
					}
    				    			
    				if(!allGps.isEmpty() && allGps != null){    				
    					for (Gps thisGps : allGps) {
							JSONObject objectTransactionGPS = new JSONObject();
							try {
								Log.d("log_tag", userMail + " -- " + thisGps.getLatitude() + " -- " + thisGps.getLongitude() 
										+ " -- " + thisGps.getDate() + " -- " + thisGps.getHour());
								objectTransactionGPS.put("userMail", userMail);
								objectTransactionGPS.put("coordinate", thisGps.getLatitude() + " " + thisGps.getLongitude());
								objectTransactionGPS.put("date", thisGps.getDate());
								objectTransactionGPS.put("hour", thisGps.getHour());
								objectTransactionGPS.put("idphone", uuid);
							} catch (JSONException e) {					
								e.printStackTrace();
							}
				
				    		// Building Parameters
				    		List<NameValuePair> paramsTransactionGPS = new ArrayList<NameValuePair>();
				    		paramsTransactionGPS.add(new BasicNameValuePair("codeCustomer", objectTransactionGPS.toString()));
				    			    		
				    		// getting JSON string from URL
				    		String returnJsonGPS = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/trackGPS", "POST", paramsTransactionGPS);				    		
				    		Log.d("log_tag", "GPS CONCILIADO -----> " + returnJsonGPS);
				    		if (returnJsonGPS.trim().equals("ok")){
				    			GpsObj.delete(thisGps.getID());
				    			
				    			
				    			Log.d("log_tag", "GPS CONCILIADO");
			    			}else{
			    				Log.d("log_tag", "FALLO AL CONCILIAR GPS");
			    			}
				        }
    				}    				
				} catch (Exception e) {					
				}
    			
    			
    			Boolean sw = true;
    			Log.d("log_tag", "---------------  Iniciando Conciliacion automatica  doInBackground Transactions -----------------");
				/** Conciliate all the transactions **/
				List<Transaction> allTransactions = null;
				try {
					allTransactions = dbTransactions.getAllTransactions();
				} catch (Exception e) {
					allTransactions = null;
				}
				
				Log.d("log_tag", "All Transactions ------------------------------->" + allTransactions);
				
				//if(!allTransactions.isEmpty() && allTransactions != null){
				if(allTransactions != null){
					Log.d("log_tag", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SI EXISTEN TRANSACCIONES");
					try {
				        for (Transaction thisTransaction : allTransactions) {
				        	Log.d("log_tag", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SI EXISTEN TRANSACCIONES 2");
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
								
								/*// Close transaction
			    				Log.d("log_tag", "Transaccion guardada para: " + transaction.getCodeCustomer());
			    				transaction.setStatus("conciliado");
			    				dbTransactions.updateTransaction(transaction);
			    				dbTransactionDetail.updateAllTransactionDetailsDelivery(transaction.getID(), "conciliado");
			    				*/
								
					    		// Building Parameters
					    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
					    		paramsTransaction.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	    		
					    		
					    		// getting JSON string from URL
					    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/save_transaction", "POST", paramsTransaction);
					    		
					    		Log.d("log_tag", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + returnJson.trim());
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
					    		catch (Exception e) {}	    	
							} catch (Exception e) {}
				        }	        	       
					} catch (Exception e) {
						Log.d("log_tag: ", "Base de Datos nula");
					}
				}else{
					Log.d("log_tag", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> NO HAY TRANSACCIONES");
				}
				
				Log.d("log_tag", "---------------  Iniciando Conciliacion automatica  doInBackground Deliveries -----------------");
				/** Conciliate all the deliveries **/
				
				List<Transaction> allTransactionsFinish = null;
				try {
					allTransactionsFinish = dbTransactions.getAllTransactionsDeliveryFinished();
				} catch (Exception e) {
					// TODO: handle exception
				}
				Log.d("log_tag: ", "DELIVERIES  --------------->" + allTransactionsFinish);
				//if(!allTransactionsFinish.isEmpty() && allTransactionsFinish != null){
				if(allTransactionsFinish != null){
					try {
				        for (Transaction thisTransaction : allTransactionsFinish) {
				    		Transaction transaction = dbTransactions.getTransaction(thisTransaction.getID());
				    		List<TransactionDetail> listTransactionDetail  = dbTransactionDetail.
				    				getAllTransactionDetailsForThisDelivery(thisTransaction.getID());
			
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
								Log.d("log_tag", "NNNNNNNNNNNNNNNNNNNN::: " + transaction.getCodeCustomer());
								objectTransaction.put("TransactionsArray", objectAllTransactionsDetails);
								objectTransaction.put("idWeb", transaction.getIDWeb());
								objectTransaction.put("coordinateStart", transaction.getCoordinateStart());
								objectTransaction.put("coordinateFinish", transaction.getCoordinateFinish());
								objectTransaction.put("timeStart", transaction.getTimeStart());
								objectTransaction.put("timeFinish", transaction.getTimeFinish());
								objectTransaction.put("obs", transaction.getObs());
								objectTransaction.put("status", transaction.getStatus());
								objectTransaction.put("userMail", userMail);
							} catch (JSONException e) {					
								e.printStackTrace();
								Log.d("log_tag: ", "GGGGGGGGGGGG");
							}								
							
				    		// Building Parameters
				    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
				    		paramsTransaction.add(new BasicNameValuePair("codeCustomer", objectTransaction.toString()));	    		
				    		
				    		// getting JSON string from URL
				    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/update_transaction", "POST", paramsTransaction);
				    		
				    		try {
				    			Log.d("PRODUCTOS JSON: ", "> " + returnJson.trim());
				    			if (returnJson.trim().equals("SAVED")){
				    				Log.d("log_tag", "Transaccion creada");
				    				// Close transaction
				    				transaction.setStatus("entregado_conciliado");
				    				dbTransactions.updateTransaction(transaction);
				    			}else{
				    				Log.d("log_tag: ", "Fallo al crear la transaccion");
				    				sw = false;
				    			}
				    		}
				    		catch (Exception e) {
				    			sw = false;
				    		}
				        }
					} catch (Exception e) {
						Log.d("log_tag: ", "Base de Datos nula");
					}
				
					Log.d("log_tag: ", "DELIVERY -_- ");
				}
				
				Log.d("log_tag", "BACKGROUND 5 ::::::");
				
				
				
				/** Check if the user is valid **/ 
    			JSONObject objectCheckUser = new JSONObject();
    			try {
    				objectCheckUser.put("userMail", userMail);
    			} catch (JSONException e) {					
    				e.printStackTrace();
    			}
    			try {
    				// Building Parameters
            		List<NameValuePair> paramsCheckUser = new ArrayList<NameValuePair>();
            		paramsCheckUser.add(new BasicNameValuePair("codeCustomer", objectCheckUser.toString()));
            			    		
            		// getting JSON string from URL
            		String returnJsonCheckUser = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/check_if_is_valid_user", "POST", paramsCheckUser);
            			    	
            		Log.d("log_tag", "BACKGROUND 6 :::::: " + returnJsonCheckUser.trim());
            		
            		try {
            			Log.d("PRODUCTOS JSON CHECK USER   : ", "> " + returnJsonCheckUser.trim());
            			if (returnJsonCheckUser.trim().equals("FALSE")){	    			
            	    		checkifvalide = false;
            				//finish();
            			}
            			Log.d("log_tag", "BACKGROUND 7 ::::::");
            		}
            		catch (Exception e) {
            			Log.d("log_tag", "BACKGROUND 8 ::::::");
            		}
				} catch (Exception e) {
					Log.d("log_tag: ", "ERROR verifing user");
				}
    		}else{
    			Log.d("log_tag: ", "NO HAY INTERNET FUUUUUU");
    		}
    		
    		tareaSemiLarga();
	        
	        String ns = Context.NOTIFICATION_SERVICE;
    	    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
    	    nMgr.cancel(NOTIF_ALERTA_ID);
    	    
	        tareaLarga();
	        Log.d("log_tag", "BACKGROUND 9 ::::::");
	        Log.d("log_tag", ":::::: " + checkifvalide);
	        Log.d("log_tag", "BACKGROUND 10 ::::::");
		    return checkifvalide;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Log.d("log_tag", "PREEXECUTE ::::::");
    		/*if (internet.isConnectingToInternet()){
    			
    	        HashMap<String, String> user = session.getUserDetails();
    		    String userMail;
    		    session = new SessionManager(getApplicationContext());
    	        userMail = user.get(SessionManager.KEY_EMAIL);
    			
    			
    			
    			// Check if the user is valid 
    			JSONObject objectCheckUser = new JSONObject();
    			try {
    				objectCheckUser.put("userMail", userMail);
    			} catch (JSONException e) {					
    				e.printStackTrace();
    			}

        		// Building Parameters
        		List<NameValuePair> paramsCheckUser = new ArrayList<NameValuePair>();
        		paramsCheckUser.add(new BasicNameValuePair("codeCustomer", objectCheckUser.toString()));
        			    		
        		// getting JSON string from URL
        		String returnJsonCheckUser = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon-sc/index.php/webservice/check_if_is_valid_user", "POST", paramsCheckUser);
        			    	
        		Log.d("log_tag", ";( > " + returnJsonCheckUser.trim());
        		
        		try {
        			Log.d("PRODUCTOS JSON CHECK USER   : ", "> " + returnJsonCheckUser.trim());
        			if (returnJsonCheckUser.trim().equals("FALSE")){	    			
        				session.logoutUser();
        				dbTransactions.clearTable();
        		    	dbTransactionDetail.clearTable();
        	    		
        	    		DatabaseHandlerUsers dbuser = new DatabaseHandlerUsers(DashboardActivity.this, "", null, 1);
        	    		dbuser.clearTable();
        	    		
        	    		DatabaseHandlerProducts dbProducts = new DatabaseHandlerProducts(DashboardActivity.this, "", null, '1');
        	       		dbProducts.clearTable();
        	       		
        	       		final DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
        	    		dbCustomers.clearTable();	
        	    		
        	    		DatabaseHandlerLines dbLine = new DatabaseHandlerLines(DashboardActivity.this, "", null, '1');
        	    		dbLine.clearTable();
        	    		
        	    		DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(DashboardActivity.this, "", null, '1');
        	    		dbVolume.clearTable();
        	    		    		
        	    		DatabaseHandlerLineVolumes dbLineVolume = new DatabaseHandlerLineVolumes(DashboardActivity.this, "", null, '1');
        	    		dbLineVolume.clearTable();
        	    		
        				finish();
        			}
        		}
        		catch (Exception e) {}
    		}
    		*/
    		//Obtenemos una referencia al servicio de notificaciones
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager notManager = 
				(NotificationManager) getSystemService(ns);
			
			//Configuramos la notificación
			int icono = android.R.drawable.stat_notify_sync;
			CharSequence textoEstado = "Conciliando";
			long hora = System.currentTimeMillis();

			Notification notif = new Notification(icono, textoEstado, hora);
			
			//Configuramos el Intent
			Context contexto = getApplicationContext();
			CharSequence titulo = "Horizon";
			CharSequence descripcion = "Autoconciliación en proceso.";
			
			Intent notIntent = new Intent(contexto, 
					DashboardActivity.class);
			
			PendingIntent contIntent = PendingIntent.getActivity(
					contexto, 0, notIntent, 0);

			notif.setLatestEventInfo(contexto, titulo, descripcion, contIntent);
			
			//AutoCancel: cuando se pulsa la notificaión ésta desaparece
			notif.flags |= Notification.FLAG_AUTO_CANCEL;
			
			//Enviar notificación
			notManager.notify(NOTIF_ALERTA_ID, notif);
			
			}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		Log.d("log_tag", "POST   :::::: " + result);
    		
    		/** Save GPS Position **/
    		
    		Calendar c = Calendar.getInstance();
    		
    		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
			
			String formattedDate = date.format(c.getTime());
			String formattedHour = hour.format(c.getTime());
			
			final GPSTracker gps = new GPSTracker(DashboardActivity.this);
			gps.getLocation();
    	    latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            
            
            Log.d("log_tag", ":::: latitude  => " + latitude);
			Log.d("log_tag", ":::: longitude  => " + longitude);
			Log.d("log_tag", ":::: date  => " + formattedDate);
			Log.d("log_tag", ":::: hour  => " + formattedHour);
			try {
				GpsObj.addGps(new Gps(String.valueOf(latitude), String.valueOf(longitude), formattedDate, formattedHour));
			} catch (Exception e) {
				Log.d("log_tag", "ERRRO AL GENERAR MAPS");
			}
    		
		 	Log.d("log_tag", "RESULT >>>>> " + result);
		 	
    		if (result){
        		try {
            		// start Conciliate All Loop
                    AsynkConciliateAll conciliate = new AsynkConciliateAll();
        			conciliate.execute();
        			Log.d("log_tag", "ERROR3333333333333333333");
    			} catch (Exception e) {
    				Log.d("log_tag", "ERROR4444444444444444444");
    			}
    		}else{
    			Log.d("log_tag", "ERROR555555");
    			session.logoutUser();
				dbTransactions.clearTable();
		    	dbTransactionDetail.clearTable();
	    		
	    		DatabaseHandlerUsers dbuser = new DatabaseHandlerUsers(DashboardActivity.this, "", null, 1);
	    		dbuser.clearTable();
	    		
	    		DatabaseHandlerProducts dbProducts = new DatabaseHandlerProducts(DashboardActivity.this, "", null, '1');
	       		dbProducts.clearTable();
	       		
	       		final DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(DashboardActivity.this, "", null, '1');
	    		dbCustomers.clearTable();	
	    		
	    		DatabaseHandlerLines dbLine = new DatabaseHandlerLines(DashboardActivity.this, "", null, '1');
	    		dbLine.clearTable();
	    		
	    		DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(DashboardActivity.this, "", null, '1');
	    		dbVolume.clearTable();
	    		    		
	    		DatabaseHandlerLineVolumes dbLineVolume = new DatabaseHandlerLineVolumes(DashboardActivity.this, "", null, '1');
	    		dbLineVolume.clearTable();
	    		finish();
    		}
    	}
    	
    	@Override
    	protected void onCancelled() {
    	}
    }

	private void tareaLarga() {
		try { 
			//Thread.sleep(50000);
			Thread.sleep(100000);
		} catch(InterruptedException e) {}
	}
	
	private void tareaSemiLarga(){
		try { 
			Thread.sleep(10000); 
		} catch(InterruptedException e) {}
	}
}