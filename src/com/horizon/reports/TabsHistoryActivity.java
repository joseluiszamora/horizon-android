package com.horizon.reports;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.ruizmier.horizon.R;

@SuppressWarnings("deprecation")
public class TabsHistoryActivity extends TabActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.tab_lists);
		
		TabHost tabHost = getTabHost();
        
        // Tab for Last Transactions
        TabSpec transspec = tabHost.newTabSpec("Ultimas Transacciones Realizadas");
     // setting Title and Icon for the Tab
        transspec.setIndicator("Ultimas Transacciones", getResources().getDrawable(android.R.drawable.ic_menu_today));
        Intent transactionIntent = new Intent(this, TabTransactionsClosedActivity.class);
        transspec.setContent(transactionIntent);
        
        // Tab for Last Delivery
        TabSpec deliveryspec = tabHost.newTabSpec("Ultimas Entregas realizadas");
        deliveryspec.setIndicator("Ultimas Entregas", getResources().getDrawable(android.R.drawable.ic_menu_slideshow));
        Intent deliveryIntent = new Intent(this, TabDeliveryClosedActivity.class);
        deliveryspec.setContent(deliveryIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(transspec); // Adding Ultimas Transacciones tab
        tabHost.addTab(deliveryspec); // Adding Ultimas Entregas tab
	}
}