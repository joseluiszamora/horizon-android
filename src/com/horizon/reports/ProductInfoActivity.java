package com.horizon.reports;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.horizon.account.SessionManager;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.Product;
import com.ruizmier.horizon.R;

public class ProductInfoActivity extends Activity{
	DatabaseHandlerProducts db = new DatabaseHandlerProducts(this, "", null, '1');
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info_product);
	 	  
	    // Session Manager
		SessionManager session = new SessionManager(getApplicationContext());
	    // get user name data from session
        HashMap<String, String> user = session.getUserDetails();
        // Set user name into action bar layout 
        String name = user.get(SessionManager.KEY_NAME);
        TextView actionBarClient = (TextView)findViewById(R.id.actionBarClientName);
        actionBarClient.setText(name);
        
        Bundle bundle = getIntent().getExtras();
	    String productCode = bundle.getString("productCode");
	    
	    // get this product
	    Product product = db.getProduct(productCode);
	    
	    // Define TextViews
 		TextView InfoProductName = (TextView)findViewById(R.id.infoClientPageName);
 		TextView InfoProductCode = (TextView)findViewById(R.id.infoClientAddress);
 		TextView InfoProductPrice = (TextView)findViewById(R.id.infoClientContact);
 		TextView InfoProductDesc = (TextView)findViewById(R.id.infoClientPhone);
 				
 		InfoProductName.setText(product.getName());
 		InfoProductCode.setText(String.valueOf(product.getIDWeb()));
 		
 		InfoProductPrice.setText("Bs " + String.format("%.2f", Double.parseDouble(product.getPrice())));
 		InfoProductDesc.setText(product.getDescription());
	}
}