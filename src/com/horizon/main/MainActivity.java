package com.horizon.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

import com.horizon.account.SessionManager;
import com.horizon.dialogprogress.AlertDialogManager;
import com.horizon.webservice.InternetDetector;
import com.ruizmier.horizon.R;

public class MainActivity extends Activity {

    // login button
    Button btnLogin;
    
    String username;
    String mail;
    
    Bundle bundle;
    
    // Session Manager Class
    SessionManager session;
    
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // internet object
 	final InternetDetector internet = new InternetDetector(this);
   	
 	@Override
 	public void onConfigurationChanged(Configuration newConfig) {
 	    super.onConfigurationChanged(newConfig);
 	}
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {// exist bundle
			bundle = getIntent().getExtras();
			// Get session vars
	        username = bundle.getString("user");
	        mail = bundle.getString("mail");
	        
			// Session Manager
	        session = new SessionManager(getApplicationContext());
	        
	        //session.logoutUser();
	        session.createLoginSession(username, mail);
	        
	        // go to Dashboard
			Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
			startActivity(i);
			finish();
		} catch (Exception e) {//no exist bundle, go to menu app
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Atención");
            builder.setMessage("Para utilizar esta aplicación debe ingresar mediante la aplicación Menu horizon, contactese con su administrador")
	               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
					finish();
	               }
	           });
            AlertDialog alert = builder.create();            
            alert.show();
		}
	};
	
	//** Pressed return button **//
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	finish();
         }
        return super.onKeyDown(keyCode, event);
    }
}