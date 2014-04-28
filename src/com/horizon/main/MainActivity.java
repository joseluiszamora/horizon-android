package com.horizon.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.horizon.database.DatabaseHandlerUsers;
import com.horizon.database.User;
import com.horizon.dialogprogress.AlertDialogManager;
import com.horizon.webservice.InternetDetector;
import com.horizon.webservice.JSONParser;
import com.ruizmier.horizon.R;

public class MainActivity extends Activity {
	// Email, password edit text
    EditText txtUsername, txtPassword;
 
    // login button
    Button btnLogin;
    
    String username;
    String pass;
    
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    
	// Progress Dialog
    private ProgressDialog pDialog;

    DatabaseHandlerUsers db = new DatabaseHandlerUsers(MainActivity.this, "", null, 1);

    // Creating JSON Parser object
   	JSONParser jsonParser = new JSONParser();
   	
   	// internet object
 	final InternetDetector internet = new InternetDetector(this);
   	
 	@Override
 	public void onConfigurationChanged(Configuration newConfig) {
 	    super.onConfigurationChanged(newConfig);
 	}
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


	    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
	    int orientation = display.getRotation();

	    if(orientation==Surface.ROTATION_180)
	    {
	          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
	    }


		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//db.clearTable();
		//db.addUser(new User("venta", "directa", "demo123", "ventadirecta@horizon.com", "preventista", 1, 0));
		
		pDialog = new ProgressDialog(MainActivity.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Finalizando Transaccion...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		
		CheckIfExistAsyncDialog updateWork = new CheckIfExistAsyncDialog();
		updateWork.execute();
		

		// Text Fields
		txtUsername = (EditText) findViewById(R.id.editText1);
		txtPassword = (EditText) findViewById(R.id.editText2);

		// Login button
		btnLogin = (Button) findViewById(R.id.button1);
		
		
		
		// click btn login
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Get username, password from EditText
                username = txtUsername.getText().toString();
                pass = txtPassword.getText().toString();
 
                if (!username.trim().equals("")){
                	if (!pass.trim().equals("")){
                		pDialog = new ProgressDialog(MainActivity.this);
                		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                		pDialog.setMessage("Verificando Datos...");
                		pDialog.setCancelable(false);
                		pDialog.setMax(100);
                		
                		CheckIfExistWebAsyncDialog updateWork = new CheckIfExistWebAsyncDialog();
                		updateWork.execute();
                    }else
                    	Toast.makeText(MainActivity.this, "Introducir password", Toast.LENGTH_SHORT).show();
                }else
                	Toast.makeText(MainActivity.this, "Introducir mail", Toast.LENGTH_SHORT).show();
			}		
		});		
	}
	
	
	private String getUser() {
		final String mail = "";
		AlertDialog.Builder alertUser = new AlertDialog.Builder(MainActivity.this);
		alertUser.setTitle("Nuevo Usuario");
		alertUser.setMessage("Introducir Email :");                
		
		// Set an EditText view to get user input
		final EditText input = new EditText(MainActivity.this);
		alertUser.setView(input);
		  
		input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		
		input.setFilters(new InputFilter[] {
		  new InputFilter.LengthFilter(100), // maximum 100 characteres
		});
		
		
		alertUser.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    	 public void onClick(DialogInterface dialog, int whichButton) {  
		        String mail = input.getText().toString();
		        Log.d( "log_tag", "Pin Value : " + mail);
		        
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Verificando...");
				pDialog.setCancelable(false);
				pDialog.setMax(100);
				
				ArrayList<String> passing = new ArrayList<String>();
				passing.add(mail);
				MainAsyncMail updateWork = new MainAsyncMail();
				updateWork.execute(passing);
				
				
		         return;                  
		        }  
	      });  

		alertUser.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	         public void onClick(DialogInterface dialog, int which) {
	             // TODO Auto-generated method stub
	        	 String mail ="";
	             return;   
	         }
	     });
		alertUser.show();
		
		
		return mail;
	}
	
	// AsyncTask Finish Transaction
	private class CheckIfExistAsyncDialog extends AsyncTask<Void, Integer, Boolean> {
	    
    	@Override
    	protected Boolean doInBackground(Void... params) {    		
			
    		/*SQLiteDatabase checkDB = null;
		    try {
		        checkDB = SQLiteDatabase.openDatabase("horizon", null,
		                SQLiteDatabase.OPEN_READONLY);
		        checkDB.close();
		    } catch (SQLiteException e) {
		        // database doesn't exist yet.
		    }
		    return checkDB != null ? true : false;
		    */
			 // Database Users Management
		 	
			if (db.getUsersCount() > 0){
				return true;
			}else{
				return false;
			}
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
					
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		if(result) {
    			try {
    				Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        			startActivity(intent);
        			finish();
				} catch (Exception e) {
					finish();
				}
    		}else{
    			Toast.makeText(MainActivity.this, "Usuario nuevo", Toast.LENGTH_SHORT).show();
    		}
    		
    		pDialog.dismiss();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		//Toast.makeText(TransactionActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	

	// AsyncTask Finish Transaction
	private class CheckIfExistWebAsyncDialog extends AsyncTask<Void, Integer, String> {
	    
    	@Override
    	protected String doInBackground(Void... params) {    		
			
    		// Building Parameters
    		List<NameValuePair> paramsTransaction = new ArrayList<NameValuePair>();
    		paramsTransaction.add(new BasicNameValuePair("userMail", username));	    		
    		paramsTransaction.add(new BasicNameValuePair("userPassword", pass));
    		
    		// getting JSON string from URL
    		String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/check_if_user", "POST", paramsTransaction);
    		//String returnJson = jsonParser.makeHttpRequest("https://mariani.bo/pruebas/horizon/webservice/check_if_user", "POST", paramsTransaction);
    		//String returnJson = jsonParser.makeHttpRequest("http://www.ruizmier.com/systems/horizon/webservice/check_user_mail", "POST", paramsTransaction);
    		//String returnJson = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/webservice/check_if_user", "POST", paramsTransaction);
    		
    		Log.d("PRODUCTOS JSON SENDING: ", "> " + returnJson.trim());
    		/*try {		
    			Log.d("PRODUCTOS JSON: ", "> " + returnJson.trim());
    			if (returnJson.trim().equals("SAVED")){
    				Log.d("log_tag", "Transaccion creada");
    			}else{
    				Log.d("log_tag: ", "Fallo al crear la transaccion");
    			}
    		}
    		catch (Exception e) {	    			
    			Log.d("Products: ", "ERRROOOOOOOOOOOOOOR");
    		}*/
    		
    		return returnJson;
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
					
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(String result) {
    		pDialog.dismiss();
    		Log.d("YO RECIBI ESTO: ", "++++ " + result);
    		
    		// Check for internet connection
	        if (!internet.isConnectingToInternet()) {
	        	alert.showAlertDialog(MainActivity.this, "Error", "No se tiene acceso a internet", true);
	        }else{
	        	if(result.trim().equals("FAIL")) {
	      			 alert.showAlertDialog(MainActivity.this, "Error de Verificacion", "Hubo un problema al conectarse a los servicios Horizon, intentelo de nuevo", true);
	       		}else{
	       			if(result.trim().equals("MAIL")) {
	       			 alert.showAlertDialog(MainActivity.this, "Error de Verificacion", "Mail Incorrecto", true);
	       			}else{
	       				if(result.trim().equals("PASS")) {
	       	    			alert.showAlertDialog(MainActivity.this, "Error de Verificacion", "Password Incorrecto", true);
	       	    		}else{
	       	    			alert.showAlertDialog(MainActivity.this, "Error de Verificacion", "Error", true);
	       	    			//db.addUser(new User("venta", "directa", "demo123", "ventadirecta@horizon.com", "preventista", 1, 0));
	       	    			
	       	    			
	       	    			try {				
	       	    				JSONArray user = new JSONArray(result);
	       	     			
		       	     			if (user != null) {
		       	     				// looping through All albums
		       	     				for (int i = 0; i < user.length(); i++) {					
		       	     					JSONObject c = user.getJSONObject(i);
		
		       	     					// Storing each json item values in variable
		       	     					String code = c.getString("idUser");
		       	     				 	//String mail = c.getString("Email");
		       	     				 	String name = c.getString("Nombre");
		       	     				 	String lastname = c.getString("Apellido");
		       	     				 	//String pass = c.getString("Password");
		       	     				 	String profile = c.getString("NivelAcceso");
		       	     				
		       	     				 	db.clearTable();
		       	     				 	db.addUser(new User(name, lastname, pass, username, profile, 1, 0));
		       	     				 	
		       	     				 	// got to dashboard
		       	     				 	Intent intent = new Intent(MainActivity.this, AccountActivity.class);
		       	       	    			startActivity(intent);
		       	       	    			finish();
		       	     				}
		       	     			}else{
		       	     				Log.d("Customers: ", "null");
		       	     			}
		
		       	     		} catch (Exception e) {
		       	     			e.printStackTrace();
		       	     		}    
	       	    		}
	       			}
	       		}
	        }
    	}
    	
    	@Override
    	protected void onCancelled() {
    		//Toast.makeText(TransactionActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }
	
    
    private class MainAsyncMail extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
    	// Products JSONArray
		 JSONArray mail = null;
			
		// Creating JSON Parser object
		JSONParser jsonParser = new JSONParser();
		
		
		
    	protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
    		 ArrayList<String> result = new ArrayList<String>();            
             result.add("lol");
//        	get passed value
            ArrayList<String> value = passing[0]; 
            String codeCustomer = "";
            for (String s : value){
            	codeCustomer = s;
            }
            
            Log.d("log_tag", "VALUEPPPPPP:::: " + codeCustomer);

            /** VERIFY MAIL **/
            // Building Parameters
    		List<NameValuePair> paramsVerifyMail = new ArrayList<NameValuePair>();
    		paramsVerifyMail.add(new BasicNameValuePair("codeCustomer", codeCustomer));
    		// getting JSON string from URL
    		String jsonProducts = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/index.php/webservice/check_user_mail", "GET", paramsVerifyMail);
    		//String jsonProducts = jsonParser.makeHttpRequest("http://www.ruizmier.com/systems/horizon/webservice/check_user_mail", "GET", paramsVerifyMail);
    		//String jsonProducts = jsonParser.makeHttpRequest("http://mariani.bo/pruebas/horizon/webservice/check_user_mail", "GET", paramsVerifyMail);
    		//String jsonProducts = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/webservice/check_user_mail", "GET", paramsVerifyMail);
    		
    		Log.d("PRODUCTOS JSON: ", "> " + jsonProducts); // Check your log cat for JSON repons
    				
    		try {
    			Log.d("Products: ", jsonProducts);
    			result.add(jsonProducts);
    		}
    		catch (Exception e) {
    	       //Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
    			result.add("false");
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
					MainAsyncMail.this.cancel(true);
				}
			});
    		
    		pDialog.setProgress(0);
    		pDialog.show();
    	}
    	
    	@Override
    	protected void onPostExecute(ArrayList<String> result) {
    		String tmp = result.get(0);
    		    		
    		if (tmp.matches("FALSE")){
    			// Dialog Error found client
    			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
    			if(tmp.matches("TRUE")){
    				Toast.makeText(MainActivity.this, "USUARIO OK!", Toast.LENGTH_SHORT).show();
    			}
    		}
    		pDialog.dismiss();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		Toast.makeText(MainActivity.this, "Informacion Actualizada!", Toast.LENGTH_SHORT).show();
    	}
    }


	
}