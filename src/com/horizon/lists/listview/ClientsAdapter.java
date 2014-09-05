package com.horizon.lists.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.Customer;
import com.ruizmier.horizon.R;

public class ClientsAdapter extends BaseAdapter {
    Context context;
    List<Customer> rowCustomers;
 
    public ClientsAdapter(Context context, List<Customer> items) {
        this.context = context;
        this.rowCustomers = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView customerId;
        TextView txtName;
        TextView txtAddress;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_clients, null);
            holder = new ViewHolder();
            holder.customerId = (TextView) convertView.findViewById(R.id.customer_id);
            holder.txtName = (TextView) convertView.findViewById(R.id.customerName);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.customerAddress);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        Customer rowItem = (Customer) getItem(position);
        holder.customerId.setText("" + rowItem.getCode());
        holder.txtName.setText(rowItem.getName());
        holder.txtAddress.setText(rowItem.getAddress());        
 
        return convertView;
    }
 
    @Override
    public int getCount() {
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
}