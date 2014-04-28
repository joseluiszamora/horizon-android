	package com.horizon.lists.listview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.DatabaseHandlerProducts;
import com.horizon.database.TransactionDetail;
import com.horizon.main.TransactionActivity;
import com.ruizmier.horizon.R;

public class TransactionListAdapter extends BaseAdapter {
	Context context;
    List<TransactionDetail> rowCustomers;
    
    public TransactionListAdapter(Context context, List<TransactionDetail> items) {
        this.context = context;
        this.rowCustomers = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {    	        
        TextView transactionDetailId;
        TextView transactionId;
        TextView productId;
        TextView txtproductName;
        TextView txtproductPrice;        
        TextView txtQuantity;
        TextView txttotalPrice;       
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_transaction_detail, null);
            holder = new ViewHolder();
            holder.transactionDetailId = (TextView) convertView.findViewById(R.id.transaction_id_detail);
            holder.transactionId = (TextView) convertView.findViewById(R.id.transaction_id);
            holder.productId = (TextView) convertView.findViewById(R.id.td_idproduct);
            holder.txtproductName = (TextView) convertView.findViewById(R.id.tdproduct);
            holder.txtproductPrice = (TextView) convertView.findViewById(R.id.infoClientPageName);
            holder.txttotalPrice = (TextView) convertView.findViewById(R.id.totalPriceTransactionModify);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.tdQuantity);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        TransactionDetail rowItem = (TransactionDetail) getItem(position);
        holder.transactionDetailId.setText(String.valueOf(rowItem.getID()));
        holder.transactionId.setText(String.valueOf(rowItem.getIdTransaction()));
        holder.productId.setText(String.valueOf(rowItem.getCodeProduct()));
        holder.txtproductName.setText(rowItem.getNameProduct());
        
        //holder.txtproductPrice.setText("Precio Unitario Bs " + round(rowItem.getPriceProduct(), 2, BigDecimal.ROUND_HALF_UP));
        holder.txtproductPrice.setText("Precio Unitario Bs " + String.format("%.2f", rowItem.getPriceProduct()));
        holder.txttotalPrice.setText(String.format("%.2f", rowItem.getPriceTotal()));
        holder.txtQuantity.setText(rowItem.getQuantity() + " unidades");
        return convertView;
    }
 
    public int getCount() {
        return rowCustomers.size();
    }
 
    public Object getItem(int position) {
        return rowCustomers.get(position);
    }
 
    public long getItemId(int position) {
        return rowCustomers.indexOf(getItem(position));
    }
    
    
    public static double round(double unrounded, int precision, int roundingMode) {
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
}