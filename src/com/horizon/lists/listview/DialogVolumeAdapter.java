package com.horizon.lists.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.Volume;
import com.ruizmier.horizon.R;

public class DialogVolumeAdapter extends BaseAdapter  {
	Context context;
    List<Volume> rowVolumes;
 
    public DialogVolumeAdapter(Context context, List<Volume> items) {
        this.context = context;
        this.rowVolumes = items;
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView volumeId;
        TextView txtDesc;        
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_row_lines, null);
            holder = new ViewHolder();
            holder.volumeId = (TextView) convertView.findViewById(R.id.line_id);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.line_desc);            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Volume rowItem = (Volume) getItem(position);
             
    	//DatabaseHandlerProducts dbProduct = new DatabaseHandlerProducts(context, "", null, '1');
        //List<Product> rowItems = dbProduct.getAllProductsForLineVolume(rowItem., transaction.getVolume());
    	
        holder.volumeId.setText(String.valueOf(rowItem.getIDWeb()));
        holder.txtDesc.setText(rowItem.getDescription());
 
        return convertView;
    }
 
    @Override
    public int getCount() {
        return rowVolumes.size();
    }
 
    @Override
    public Object getItem(int position) {
        return rowVolumes.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowVolumes.indexOf(getItem(position));
    }
}
