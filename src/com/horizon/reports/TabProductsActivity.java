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
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.Product;
import com.horizon.lists.listview.ProductsAdapter;
import com.horizon.reports.ClientsListActivity.CustomAdapter;
import com.ruizmier.horizon.R;

public class TabProductsActivity extends Activity implements OnItemClickListener {
/** Called when the activity is first created. */
	
	DatabaseHandlerProducts db = new DatabaseHandlerProducts(this, "", null, '1');
	ListView listView;
	List<Product> rowItems;
	
	
	// search functionality
	EditText edittext;
	 ListView listview;

	 String[] text = null;

	 int textlength = 0;

	 ArrayList<String> text_sort = new ArrayList<String>();
	 ArrayList<Integer> image_sort = new ArrayList<Integer>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_clients);
	    
	    listView = (ListView) findViewById(R.id.contentlistclient);
	    edittext = (EditText) findViewById(R.id.textSearch);
	    
	    // set all Customers List
	    text = db.getAllProductNames();
	    final List<Product> rowItemsProd = db.getAllProducts();
	    //listview.setAdapter(new CustomAdapterProducts(this, text, rowItems));
	    CustomAdapterProducts adapter = new CustomAdapterProducts(this, text, rowItemsProd);
	    //ProductsAdapter adapter = new ProductsAdapter(this, rowItems);
	    listView.setAdapter(adapter);
	    listView.setOnItemClickListener(this);
	    
		//listview.setAdapter(new CustomAdapter(this, text, rowItems));
	    
	    
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
	     if (textlength <= text[i].length()) {
	      if (edittext.getText().toString().equalsIgnoreCase ( (String) text[i].subSequence(0,textlength))) {
	    	  text_sort.add(text[i]);
	    	  Log.d("log_tag", "Edit text " + edittext.getText().toString());
	      }
	     }
	    }

	    List<Product> SearchRowItems = db.getAllSearchProducts(edittext.getText().toString());
	    
	    CustomAdapterProducts adapter = new CustomAdapterProducts(TabProductsActivity.this, text_sort, SearchRowItems);
	    listView.setAdapter(adapter);
	   }
	  });
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		String custom_name = (String) ((TextView) arg1.findViewById(R.id.customer_id)).getText();		
        
        Intent intentNewProduct = new Intent(this, ProductInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("productCode", custom_name);		
		intentNewProduct.putExtras(bundle);
		startActivity(intentNewProduct);
	}
	
	
	
	
	

	
    class CustomAdapterProducts extends BaseAdapter {

  	  String[] data_text;
  	  Context context;
  	  List<Product> rowCustomers;

  	CustomAdapterProducts() {

  	  }

  	CustomAdapterProducts(Context context, String[] text, List<Product> items) {
  		this.context = context;
  		data_text = text;
  		this.rowCustomers = items;
  	}

  	CustomAdapterProducts(Context context, ArrayList<String> text, List<Product> items) {
  		this.context = context;
  		this.rowCustomers = items;
  		data_text = new String[text.size()];

		for (int i = 0; i < text.size(); i++) {
			data_text[i] = text.get(i);
		}
  	 }
  	  
  	@Override
    public int getCount() {
  		//return data_text.length;
        return rowCustomers.size();
    }
 
    @Override
    public Object getItem(int position) {
        return rowCustomers.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowCustomers.indexOf(getItem(position));
    }
  	  
	public Object getItemCustom(int position) {
		return rowCustomers.get(position);
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
  			convertView = inflater.inflate(R.layout.row_clients, null);
  			
	        holder = new ViewHolder();
	        holder.customerId = (TextView) convertView.findViewById(R.id.customer_id);
	        holder.txtName = (TextView) convertView.findViewById(R.id.customerName);
	        holder.txtAddress = (TextView) convertView.findViewById(R.id.customerAddress);
	        convertView.setTag(holder);
	     }
	     else {
	         holder = (ViewHolder) convertView.getTag();
	     }
		
		Product rowItem = (Product) getItem(position);
        holder.customerId.setText(String.valueOf(rowItem.getIDWeb()));
        holder.txtName.setText(rowItem.getName());
        holder.txtAddress.setText(String.valueOf(rowItem.getIDWeb())); 
		
		return convertView;
  	  }
  }

}