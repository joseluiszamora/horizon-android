package com.horizon.main;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.horizon.account.SessionManager;
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerGps;
import com.horizon.database.DatabaseHandlerLineVolumes;
import com.horizon.database.DatabaseHandlerLines;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.DatabaseHandlerTransactions;
import com.horizon.database.DatabaseHandlerUsers;
import com.horizon.database.DatabaseHandlerVolumes;
import com.horizon.database.User;
import com.horizon.dialogprogress.AlertDialogManager;
import com.ruizmier.horizon.R;

public class AccountActivity extends Activity {
	// Email, password edit text
    EditText txtUsername, txtPassword;
 
 // Progress Dialog
    private ProgressDialog pDialog;
    
    // login button
    Button btnLogin;
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Session Manager Class
    SessionManager session;
    
    // Database Users Management
	DatabaseHandlerUsers db = new DatabaseHandlerUsers(this, "", null, 1);
    
    // Databases Variables
    String dbname;
	String dbmail;
	String dbpass;
	String dbprofile;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_login);
		
		// Text Fields
		txtUsername = (EditText) findViewById(R.id.txtloginMail);
		txtPassword = (EditText) findViewById(R.id.txtloginPassword);

		// Login button
		btnLogin = (Button) findViewById(R.id.btnLogin);
       
		// Session Manager
        session = new SessionManager(getApplicationContext());
        
        if(session.isLoggedIn()){ // go to Dashboard
        	Intent intent = new Intent(this, DashboardActivity.class);
			startActivity(intent);
        }else{  // go to Login Page*/
        	
            
    	        // Deleting ALL Users
    			//db.clearTable();
    			//db.CreateTable();
    			// Inserting NEW User
    			//db.addUser(new User("venta", "directa", "demo123", "ventadirecta@horizon.com", "preventista", 1, 0));
    			//db.addUser(new User("Luis", "Candia", "demo123", "luis@horizon.com", "admin", 1, 0));
    			
    			//get user
    			List<User> user = db.getAllUsers();		
    			//final String count = String.valueOf(db.getUsersCount());
    			
    			// Reading all users
    	        for (User us : user) {
    	        	dbname = us.getName() + " " + us.getLastName();
    	        	dbmail = us.getEmail();
    	        	dbpass = us.getPassword();
    	        	dbprofile = us.getProfile();
    	        }
    	        
        }		

        // click btn login 
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//alert.showAlertDialog(AccountActivity.this, "Error de Logueo", "Count:: " + count+" Name:: " + dbname+" Mail:: " + dbmail+" Password:: " + dbpass+" Profile:: " + dbprofile, false);
							
				// Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
 
                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                	            	
                	if(username.equals(dbmail.trim()) && password.equals(dbpass.trim())){
						
						// Creating user login session
						// For testing i am stroing name, email as follow
						// Use user real data
						session.createLoginSession(dbname, dbmail);
						
						// Staring MainActivity
						Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
						startActivity(i);
						finish();
						
					}else{
						// username / password doesn't match
						alert.showAlertDialog(AccountActivity.this, "Error de Logueo", "Username o Password son incorrectos", false);
					}		
                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(AccountActivity.this, "Error de Logueo", "Porfavor ingrese Email y Password", true);
                }
			}	
		});
	}
	
	/** MENU **/
    // Initiating Menu XML file 
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu_login, menu);
        return true;
    }
           
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        switch (item.getItemId()){
        case R.id.menu_reset_user:           
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
            builder.setTitle("Atención");
            builder.setMessage("Esta operación borrara toda la información y operaciones pendientes, ¿está seguro?")
                       .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   
                    	pDialog = new ProgressDialog(AccountActivity.this);
                   		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   		pDialog.setMessage("Reseteando Informacion...");
                   		pDialog.setCancelable(false);
                   		pDialog.setMax(100);
                   		
                   		DropAllAsyncDialog updateWork = new DropAllAsyncDialog();
                   		updateWork.execute();

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
    
    
    
    
    
    
 // AsyncTask Finish Transaction
 	private class DropAllAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
 	    
     	@Override
     	protected Boolean doInBackground(Void... params) {    		
 			
     		DatabaseHandlerProducts dbProducts = new DatabaseHandlerProducts(AccountActivity.this, "", null, '1');
       		dbProducts.clearTable();
       		
       		final DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(AccountActivity.this, "", null, '1');
    		dbCustomers.clearTable();	
    		
    		DatabaseHandlerLines dbLine = new DatabaseHandlerLines(AccountActivity.this, "", null, '1');
    		dbLine.clearTable();
    		
    		DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(AccountActivity.this, "", null, '1');
    		dbVolume.clearTable();
    		    		
    		DatabaseHandlerLineVolumes dbLineVolume = new DatabaseHandlerLineVolumes(AccountActivity.this, "", null, '1');
    		dbLineVolume.clearTable();
    		
    		DatabaseHandlerTransactions dbTransactions = new DatabaseHandlerTransactions(AccountActivity.this, "", null, 1);
    		dbTransactions.clearTable();
    		
    		DatabaseHandlerTransactionDetail dbTransDetail = new DatabaseHandlerTransactionDetail(AccountActivity.this, "", null, '1');
    		dbTransDetail.clearTable();
    		
    		DatabaseHandlerUsers dbuser = new DatabaseHandlerUsers(AccountActivity.this, "", null, 1);
    		dbuser.clearTable();
    		
    		DatabaseHandlerGps dbGps = new DatabaseHandlerGps(AccountActivity.this, "", null, '1');
    		dbGps.CreateTable();
    		dbGps.clearTable();
 		 	 			
 			return true; 			
     	}
     	
     	@Override
     	protected void onProgressUpdate(Integer... values) {
     		int progreso = values[0].intValue();
     		
     		//pDialog.setProgress(progreso);
     	}
     	
     	@Override
     	protected void onPreExecute() {
     		
     		/*pDialog.setOnCancelListener(new OnCancelListener() {
 				@Override
 				public void onCancel(DialogInterface dialog) {
 					
 				}
 			});
     		
     		pDialog.setProgress(0);
     		pDialog.show();*/
     	}
     	
     	@Override
     	protected void onPostExecute(Boolean result) {
     		if(result) {
     			Log.d("log_tag", "finishedd");
     			
     			Intent intentNewTransaction = new Intent(AccountActivity.this, MainActivity.class);
    			startActivity(intentNewTransaction);
    			
     			//finish();
     		}else{
     			Toast.makeText(AccountActivity.this, "No se pudo completar la operacion", Toast.LENGTH_SHORT).show();
     		}
     		
     	//	pDialog.dismiss();
     	}
     	
     	@Override
     	protected void onCancelled() {
     		//Toast.makeText(TransactionActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
     	}
     }
}
