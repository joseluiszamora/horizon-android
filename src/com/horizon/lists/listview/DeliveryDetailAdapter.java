	package com.horizon.lists.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.TransactionDetail;
import com.ruizmier.horizon.R;

public class DeliveryDetailAdapter extends BaseAdapter {
	Context context;
    List<TransactionDetail> rowCustomers;
    
    public DeliveryDetailAdapter(Context context, List<TransactionDetail> items) {
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
 
        TransactionDetail rowItem = (TransactionDetail) getItem(position);
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_delivery_detail, null);
            holder = new ViewHolder();
            
            if(rowItem.getStatus().trim().equals("cancelado")){
            	convertView.findViewById(R.id.deliveryBackgroundRow).setBackgroundColor(Color.RED);
            }
            if(rowItem.getStatus().trim().equals("entregado")){
            	convertView.findViewById(R.id.deliveryBackgroundRow).setBackgroundColor(Color.GREEN);
            }
            
            holder.transactionDetailId = (TextView) convertView.findViewById(R.id.transaction_id_detail);
            holder.transactionId = (TextView) convertView.findViewById(R.id.transaction_id);
            holder.productId = (TextView) convertView.findViewById(R.id.td_idproduct);
            holder.txtproductName = (TextView) convertView.findViewById(R.id.tdproduct);
            holder.txtproductPrice = (TextView) convertView.findViewById(R.id.infoClientPageName);
            holder.txttotalPrice = (TextView) convertView.findViewById(R.id.totalPriceTransactionModify);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.tdQuantity);
            convertView.setTag(holder);
        //}
        //else {
        //    holder = (ViewHolder) convertView.getTag();
        //}

        holder.transactionDetailId.setText(String.valueOf(rowItem.getID()));
        holder.transactionId.setText(String.valueOf(rowItem.getIdTransaction()));
        holder.productId.setText(String.valueOf(rowItem.getCodeProduct()));
        holder.txtproductName.setText(rowItem.getNameProduct());
        holder.txtproductPrice.setText("Precio Unitario (Bs): " + String.format("%.2f", rowItem.getPriceProduct()));
        holder.txttotalPrice.setText(String.format("%.2f", rowItem.getPriceTotal()));
        holder.txtQuantity.setText(rowItem.getQuantity() + " unidades");
        //holder.txtQuantity.setText(rowItem.getIdTransaction() + "---"+ rowItem.getIDWeb() + "---"+ rowItem.getID() + "---"+ rowItem.getStatus());
       
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
}