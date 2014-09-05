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
import com.horizon.database.DatabaseHandlerCustomers;
import com.horizon.database.DatabaseHandlerTransactionDetail;
import com.horizon.database.Transaction;
import com.ruizmier.horizon.R;

public class LastTransactionAdapter extends BaseAdapter {
	Context context;
    List<Transaction> rowTransactions;   
    	
 
    public LastTransactionAdapter(Context context, List<Transaction> items) {
        this.context = context;
        this.rowTransactions = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView transactionId;
        TextView txtClient;
        TextView txtAddress;
        TextView txtDateStart;
        TextView txtCoordinate;        
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_last_transaction, null);
            holder = new ViewHolder();
            holder.transactionId = (TextView) convertView.findViewById(R.id.transaction_id);
            holder.txtClient = (TextView) convertView.findViewById(R.id.tdIProduct);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.tdQuantity);
            holder.txtDateStart = (TextView) convertView.findViewById(R.id.transactionDateTime);
            holder.txtCoordinate = (TextView) convertView.findViewById(R.id.transactionCoordinate);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        Transaction rowItem = (Transaction) getItem(position);
    	
        if(rowItem.getClientType().equals("temporal")){
        	holder.txtClient.setText(rowItem.getID()+ "- Temporal");
            holder.transactionId.setText(String.valueOf(rowItem.getID()));
            holder.txtAddress.setText("Transaccion Temporal");
            //holder.txtDateStart.setText("Creado en: " + rowItem.getTimeStart());
            holder.txtDateStart.setText("Creado en: " + rowItem.getStatus());
            holder.txtCoordinate.setText("Tipo de Transaccion: Transaccion Temporal");
        }else{
        	final DatabaseHandlerCustomers dbCustomers = new DatabaseHandlerCustomers(context, "", null, 1);
        	try {
        		Customer customer = dbCustomers.getCustomerByCode(rowItem.getCodeCustomer());
                holder.txtClient.setText(rowItem.getID()+ "-" + rowItem.getCodeCustomer()+ "-" + customer.getName());
                holder.txtAddress.setText(customer.getAddress());
			} catch (Exception e) {
				holder.txtClient.setText(rowItem.getID()+ "-" + rowItem.getCodeCustomer()+ "- Nombre no disponible");
                holder.txtAddress.setText("Direccion no disponible");
			}
            
            holder.transactionId.setText(String.valueOf(rowItem.getID()));
            holder.txtDateStart.setText("Creado en: " + rowItem.getTimeStart());
            //holder.txtDateStart.setText("Creado en: " + rowItem.getStatus());
            //holder.txtCoordinate.setText("Tipo de Transaccion: " + rowItem.getType());
            
            if(rowItem.getType().equals("venta_directa"))
            	holder.txtCoordinate.setText("Tipo de Transaccion: Venta Directa");
            if(rowItem.getType().equals("preventa"))
            	holder.txtCoordinate.setText("Tipo de Transaccion: Preventa");
            if(rowItem.getType().equals("transaccion_0"))
            	holder.txtCoordinate.setText("Tipo de Transaccion: Transaccion 0");
        }
        return convertView;
    }
 
    @Override
    public int getCount() {
        return rowTransactions.size();
    }
 
    @Override
    public Object getItem(int position) {
        return rowTransactions.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowTransactions.indexOf(getItem(position));
    }
}
