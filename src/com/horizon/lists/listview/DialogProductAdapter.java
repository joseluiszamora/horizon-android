package com.horizon.lists.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.Product;
import com.ruizmier.horizon.R;

public class DialogProductAdapter extends BaseAdapter {
    Context context;
    List<Product> rowLines;
 
    public DialogProductAdapter(Context context, List<Product> items) {
        this.context = context;
        this.rowLines = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView customerId;
        TextView txtDesc;        
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_row_lines, null);
            holder = new ViewHolder();
            holder.customerId = (TextView) convertView.findViewById(R.id.line_id);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.line_desc);            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        Product rowItem = (Product) getItem(position);
        holder.customerId.setText(String.valueOf(rowItem.getIDWeb()));
        holder.txtDesc.setText(rowItem.getName());
 
        return convertView;
    }
 
    @Override
    public int getCount() {
        return rowLines.size();
    }
 
    @Override
    public Object getItem(int position) {
        return rowLines.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowLines.indexOf(getItem(position));
    }
}