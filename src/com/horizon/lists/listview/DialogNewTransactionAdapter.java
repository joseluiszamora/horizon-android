package com.horizon.lists.listview;

import java.util.List;

import com.horizon.database.Customer;
import com.horizon.database.Product;
import com.ruizmier.horizon.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogNewTransactionAdapter extends BaseAdapter {

	Context context;
    List<Product> rowProducts;
 
    public DialogNewTransactionAdapter(Context context, List<Product> items) {
        this.context = context;
        this.rowProducts = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView customerId;
        TextView txtName;        
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_dialog_new_transaction, null);
            holder = new ViewHolder();
            holder.customerId = (TextView) convertView.findViewById(R.id.newTransactionItemId);
            holder.txtName = (TextView) convertView.findViewById(R.id.newTransactionItemId);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        Customer rowItem = (Customer) getItem(position);
        holder.customerId.setText("" + rowItem.getCode());
        holder.txtName.setText(rowItem.getName());        
 
        return convertView;
	}
}