package com.horizon.delivery;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.horizon.reports.TabClientsActivity;
import com.horizon.reports.TabLastTransactionsActivity;
import com.horizon.reports.TabProductsActivity;
import com.ruizmier.horizon.R;

public class DeliveryReport extends TabActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.tab_lists);
		
		TabHost tabHost = getTabHost();
        
        // Tab for Last Transactions
        TabSpec transspec = tabHost.newTabSpec("Entregas Pendientes");
        transspec.setIndicator("Ultimas Transacciones", getResources().getDrawable(android.R.drawable.ic_menu_preferences));
        Intent transactionIntent = new Intent(this, TabLastTransactionsActivity.class);
        transspec.setContent(transactionIntent);
        
        // Tab for Productos
        TabSpec productspec = tabHost.newTabSpec("Productos");
        productspec.setIndicator("Productos", getResources().getDrawable(android.R.drawable.ic_menu_my_calendar));
        Intent productsIntent = new Intent(this, TabProductsActivity.class);
        productspec.setContent(productsIntent);
        
        // Tab for Clients
        TabSpec clientspec = tabHost.newTabSpec("Clientes");
        clientspec.setIndicator("Clientes", getResources().getDrawable(android.R.drawable.ic_dialog_map));
        Intent clientsIntent = new Intent(this, TabClientsActivity.class);
        clientspec.setContent(clientsIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(transspec); // Adding Ultimas Transacciones tab
        tabHost.addTab(clientspec); // Adding Clientes tab
	}
}
