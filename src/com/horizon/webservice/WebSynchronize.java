package com.horizon.webservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.Product;
import com.horizon.dialogprogress.AlertDialogManager;

public class WebSynchronize extends Activity {
	
	// Connection detector
    InternetDetector cd;
 
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // Creating JSON Parser object
 	JSONParser jsonParser = new JSONParser();
 
 	// 	Clients JSONArray
 	JSONArray clients = null;
 	
// 	Products JSONArray
 	JSONArray products = null;
 	
	public WebSynchronize (){		
	
	}
	
	public void updateProducts(DatabaseHandlerProducts databaseProducts){					
		// delete All Products
		databaseProducts.clearTable();		
					
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// getting JSON string from URL
		String jsonProducts = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/product/report_android", "GET", params);

		// Check your log cat for JSON reponse
		Log.d("PRODUCTOS JSON: ", "> " + jsonProducts);
				
		try {				
			products = new JSONArray(jsonProducts);
			
			if (products != null) {
				// looping through All albums
				for (int i = 0; i < products.length(); i++) {					
					JSONObject c = products.getJSONObject(i);

					// Storing each json item values in variable
					Long idProduct = c.getLong("idProduct");
				 	String Nombre = c.getString("Nombre");
				 	String PrecioUnitario = c.getString("PrecioUnit");
				 	String Descripcion = c.getString("Descripcion");
				 	
				 	Log.e("log_tag", idProduct + "---" + Nombre+ "---" + PrecioUnitario+ "---" + Descripcion);
				 	
				 	databaseProducts.addProduct(new Product(idProduct, 2, Nombre, Double.parseDouble(PrecioUnitario), Descripcion, "activo"));				 	
				}
			}else{
				Log.d("Albums: ", "null");
			}
		}
		catch (Exception e) {
	       Toast.makeText(getBaseContext(),"Error al conectar con el servidor. ",Toast.LENGTH_SHORT).show();
		}
	}
	

	public void updateClients(DatabaseHandlerCustomers dbCustomers) {
		pDialog = new ProgressDialog(WebSynchronize.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setMessage("Procesando...");
		pDialog.setCancelable(true);
		pDialog.setMax(100);
		
		/*// delete All Clients
		dbCustomers.clearTable();		
		
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// getting JSON string from URL
		String json = jsonParser.makeHttpRequest("http://www.mariani.bo/horizon/client/report_android", "GET",
				params);

		// Check your log cat for JSON reponse
		Log.d("Albums JSON: ", "> " + json);

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
				 	
				 	Log.e("log_tag", codeCustomer + "---" + NombreTienda+ "---" + NombreContacto+ "---" + Direccion
				 			+ "---" + Telefono+ "---" + TelfCelular+ "---");
				 	
				 	dbCustomers.addCustomer(new Customer(codeCustomer, NombreTienda, NombreContacto, Direccion, Telefono, TelfCelular, "activo"));				 	
				}
			}else{
				Log.d("Albums: ", "null");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}*/											
	}
	
	
	private void tareaLarga()
    {
    	try { 
    		Thread.sleep(1000); 
    	} catch(InterruptedException e) {}
    }
	
	class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			for(int i=1; i<=10; i++) {
				tareaLarga();
				
				publishProgress(i*10);
				
				if(isCancelled())
					break;
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
					MiTareaAsincronaDialog.this.cancel(true);
				}
			});
			
			pDialog.setProgress(0);
			pDialog.show();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
			{
				pDialog.dismiss();
				Toast.makeText(WebSynchronize.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onCancelled() {
			Toast.makeText(WebSynchronize.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
		}
	}
}
