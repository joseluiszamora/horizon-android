package com.horizon.reports;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.ruizmier.horizon.R;

@SuppressWarnings("deprecation")
public class TabsReportActivity extends TabActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.tab_lists);
		
		TabHost tabHost = getTabHost();
        
        // Tab for Last Transactions
        TabSpec transspec = tabHost.newTabSpec("Transacciones");
     // setting Title and Icon for the Tab
        transspec.setIndicator("Transacciones", getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));
        Intent transactionIntent = new Intent(this, TabLastTransactionsActivity.class);
        transspec.setContent(transactionIntent);
        
        // Tab for Productos
        TabSpec productspec = tabHost.newTabSpec("Productos");
        productspec.setIndicator("Productos", getResources().getDrawable(android.R.drawable.ic_menu_sort_alphabetically));
        Intent productsIntent = new Intent(this, TabProductsActivity.class);
        productspec.setContent(productsIntent);
        
        // Tab for Clients
        TabSpec clientspec = tabHost.newTabSpec("Clientes");
        clientspec.setIndicator("Clientes", getResources().getDrawable(android.R.drawable.ic_menu_myplaces));
        Intent clientsIntent = new Intent(this, TabClientsActivity.class);
        clientspec.setContent(clientsIntent);
        
        // Tab for Last Delivery
        TabSpec deliveryspec = tabHost.newTabSpec("Entregas");
        deliveryspec.setIndicator("Entregas", getResources().getDrawable(android.R.drawable.ic_menu_agenda	));
        Intent deliveryIntent = new Intent(this, TabLastDeliveryActivity.class);
        deliveryspec.setContent(deliveryIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(transspec); // Adding Ultimas Transacciones tab
        tabHost.addTab(deliveryspec); // Adding Ultimas Entregas tab
        tabHost.addTab(clientspec); // Adding Clientes tab
        tabHost.addTab(productspec); // Adding Productos tab
	}
}