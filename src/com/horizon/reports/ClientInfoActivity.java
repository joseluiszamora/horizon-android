package com.horizon.reports;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.horizon.account.SessionManager;
import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.ruizmier.horizon.R;

public class ClientInfoActivity extends Activity {
	
	//TextView customName;
    //TextView customAddress;
    //TextView customEncargado;
    //TextView customPhone;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info_client);
	    
	    DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(this, "", null, '1');
	    Bundle bundle = getIntent().getExtras();
	    Customer custom = db.getCustomerByCode(bundle.getString("customerCode"));	    
	    
	    
	    // Session Manager
		SessionManager session = new SessionManager(getApplicationContext());
	    // get user name data from session
        HashMap<String, String> user = session.getUserDetails();
        // Set user name into action bar layout 
        String name = user.get(SessionManager.KEY_NAME);
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
	    
	    
	    // Define TextViews
 		TextView InfoClientCustomName = (TextView)findViewById(R.id.infoClientPageName);
 		TextView InfoClientCustomAddress = (TextView)findViewById(R.id.infoClientAddress);
 		TextView InfoClientCustomEncargado = (TextView)findViewById(R.id.infoClientContact);
 		TextView InfoClientCustomPhone = (TextView)findViewById(R.id.infoClientPhone);
 		TextView InfoClientCustomCode = (TextView)findViewById(R.id.infoClientCode);
 		
 		InfoClientCustomName.setText(custom.getName());
 		InfoClientCustomAddress.setText(custom.getAddress()); 
 		InfoClientCustomEncargado.setText(custom.getContactName());
 		InfoClientCustomPhone.setText(custom.getPhone() + " - " + custom.getCellPhone());
 		InfoClientCustomCode.setText(custom.getCode());
         
	}
}
