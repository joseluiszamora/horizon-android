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

import com.horizon.database.Bonus;
import com.horizon.database.DatabaseHandlerBonus;
import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.Product;
import com.ruizmier.horizon.R;

public class TabLastBonusActivity extends Activity implements OnItemClickListener {
	
	DatabaseHandlerBonus db = new DatabaseHandlerBonus(this, "", null, '1');
	ListView listView;
	List<Product> rowItems;
	
	// search functionality
	EditText edittext;
	 ListView listview;

	 String[] text = null;

	 int textlength = 0;

	 ArrayList<String> text_sort = new ArrayList<String>();
	 ArrayList<Integer> image_sort = new ArrayList<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_bonus);
	    
	    listView = (ListView) findViewById(R.id.contentlistclientbonusx);
	    
	    // set all Customers List
	    text = db.getAllNames();
	    final List<Bonus> rowItemsProd = db.getAllBonus();
	    //listview.setAdapter(new CustomAdapterProducts(this, text, rowItems));
	    CustomAdapterProducts adapter = new CustomAdapterProducts(this, text, rowItemsProd);
	    //ProductsAdapter adapter = new ProductsAdapter(this, rowItems);
	    listView.setAdapter(adapter);
	    listView.setOnItemClickListener(this);
	    
		//listview.setAdapter(new CustomAdapter(this, text, rowItems));
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
	
	
	
	
	

	
    class CustomAdapterProducts extends BaseAdapter {

  	  String[] data_text;
  	  Context context;
  	  List<Bonus> rowCustomers;

  	CustomAdapterProducts() {

  	  }

  	CustomAdapterProducts(Context context, String[] text, List<Bonus> items) {
  		this.context = context;
  		data_text = text;
  		this.rowCustomers = items;
  	}

  	CustomAdapterProducts(Context context, ArrayList<String> text, List<Bonus> items) {
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
          TextView title1;
          TextView subTitle1;
          TextView title2;
          TextView subTitle2;
      }
      
  	  public View getView(int position, View convertView, ViewGroup parent) {
  		  
  		ViewHolder holder = null;
  		if (convertView == null) {
  			LayoutInflater inflater = getLayoutInflater();
  			convertView = inflater.inflate(R.layout.row_bonus, null);
  			
	        holder = new ViewHolder();
	        holder.title1 = (TextView) convertView.findViewById(R.id.customerName);
	        holder.subTitle1 = (TextView) convertView.findViewById(R.id.customerAddress);
	        holder.title2 = (TextView) convertView.findViewById(R.id.textView1);
	        holder.subTitle2 = (TextView) convertView.findViewById(R.id.textView2);
	        convertView.setTag(holder);
	     }
	     else {
	         holder = (ViewHolder) convertView.getTag();
	     }
  		
		Bonus rowItem = (Bonus) getItem(position);
        
		if (rowItem.getType().equals("P")) {
			holder.title1.setText("Producto: " + rowItem.getNameFrom());
		}else{
			holder.title1.setText("Linea: " + rowItem.getNameFrom());
		}
		
		holder.subTitle1.setText("Cantidad: " + rowItem.getQuantityFrom()+"");
		holder.title2.setText("Bonificación: " + rowItem.getNameTo());
		holder.subTitle2.setText("Cantidad: " + rowItem.getQuantityTo()+"");

		return convertView;
  	  }
  }

}