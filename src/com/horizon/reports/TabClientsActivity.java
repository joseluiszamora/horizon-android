package com.horizon.reports;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.horizon.database.Customer;
import com.horizon.database.DatabaseHandlerCustomers;
import com.ruizmier.horizon.R;

public class TabClientsActivity extends Activity implements OnItemClickListener {
	
	// search functionality
	EditText edittext;
	 ListView listview;
	
	 String[] text = null;
	
	 int textlength = 0;
	
	 ArrayList<String> text_sort = new ArrayList<String>();
	 ArrayList<Integer> image_sort = new ArrayList<Integer>();
	 	
	
	DatabaseHandlerCustomers db = new DatabaseHandlerCustomers(this, "", null, '1');
	ListView listView;
	List<Customer> rowItems;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_clients);
	    
	    
	    edittext = (EditText) findViewById(R.id.textSearch);
		listview = (ListView) findViewById(R.id.contentlistclient);
		
		// set all Customers List
		text = db.getAllCustomerNames();
		final List<Customer> rowItems = db.getAllCustomers();
		listview.setAdapter(new CustomAdapter(this, text, rowItems));		
		listview.setOnItemClickListener(this);
		
		
		
		edittext.addTextChangedListener(new TextWatcher() {

		   public void afterTextChanged(Editable s) {

		   }

		   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		   }

		   public void onTextChanged(CharSequence s, int start, int before, int count) {

		    textlength = edittext.getText().length();
		    text_sort.clear();
		    image_sort.clear();

		    for (int i = 0; i < text.length; i++) {
				if(text[i].toLowerCase().contains(edittext.getText().toString())){
					text_sort.add(text[i]);
					Log.d("log_tag", "Edit text " + text[i].toLowerCase() + "----------" +edittext.getText().toString());
				}
		    }
		    
		    List<Customer> SearchRowItems = db.getSearchCustomers(edittext.getText().toString());
		    listview.setAdapter(new CustomAdapter(TabClientsActivity.this, text_sort, SearchRowItems));		    

		   }
		});
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		String custom_id = (String) ((TextView) arg1.findViewById(R.id.customer_id)).getText();

        Log.d("log_tag", "lalalalall" + custom_id);
        
        Intent intentNewProduct = new Intent(this, ClientInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("customerCode", custom_id);
		intentNewProduct.putExtras(bundle);
		startActivity(intentNewProduct);
	}
	

	
    class CustomAdapter extends BaseAdapter {

  	  String[] data_text;
  	  Context context;
  	  List<Customer> rowCustomers;

  	  CustomAdapter() {

  	  }

  	  CustomAdapter(Context context, String[] text, List<Customer> items) {
  	   data_text = text;
  	   this.rowCustomers = items;
  	  }

  	  CustomAdapter(Context context, ArrayList<String> text, List<Customer> items) {
  		this.rowCustomers = items;
  	   data_text = new String[text.size()];

  	   for (int i = 0; i < text.size(); i++) {
  	    data_text[i] = text.get(i);
  	   }

  	  }
  	  
  	  
  	  public int getCount() {
  	   return data_text.length;
  	  }

  	  public String getItem(int position) {
  	   return null;
  	  }
  	  
  	  public Object getItemCustom(int position) {
	    return rowCustomers.get(position);
  	  }

  	  public long getItemId(int position) {
  		return rowCustomers.indexOf(getItem(position));
  	  }

  	  /* private view holder class */
      private class ViewHolder {
      	TextView customerId;
        TextView txtName;
        TextView txtAddress;
      }
      
  	  public View getView(int position, View convertView, ViewGroup parent) {
  		  
  		ViewHolder holder = null;
  		if (convertView == null) {
  			LayoutInflater inflater = getLayoutInflater();
  			convertView = inflater.inflate(R.layout.row_clients, parent, false);
  			
	        holder = new ViewHolder();
	        holder.customerId = (TextView) convertView.findViewById(R.id.customer_id);
	        holder.txtName = (TextView) convertView.findViewById(R.id.customerName);
	        holder.txtAddress = (TextView) convertView.findViewById(R.id.customerAddress);
	        convertView.setTag(holder);
	     }
	     else {
	         holder = (ViewHolder) convertView.getTag();
	     }
  		
  		Customer rowItem = (Customer) getItemCustom(position);	    
	     
  		holder.customerId.setText(String.valueOf(rowItem.getCode()));
		holder.txtName.setText(" ("+ rowItem.getCode() +" )"+ data_text[position]);
  		//holder.txtName.setText(rowItem.getName());
		holder.txtAddress.setText(rowItem.getAddress());
		
		return convertView;

  	  }
  	 }

	
}