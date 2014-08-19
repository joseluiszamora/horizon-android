package com.horizon.lists.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.horizon.database.DatabaseHandlerVolumes;
import com.horizon.database.Line;
import com.horizon.database.Volume;
import com.horizon.main.TransactionActivity;
import com.ruizmier.horizon.R;

public class DialogLineAdapter extends BaseAdapter {
    Context context;
    List<Line> rowLines;
 
    public DialogLineAdapter(Context context, List<Line> items) {
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
 
        try {
        	Line rowItem = (Line) getItem(position);
            // Database Volume class
        	//DatabaseHandlerVolumes dbVolume = new DatabaseHandlerVolumes(context, "", null, '1');
        	//Log.d("log_tag", "LINE IDWEB >>>" + rowItem.getIDWeb());
        	//Log.d("log_tag", "LINE DESC >>>" + rowItem.getDescription());
            
        	//List<Volume> rowItems = dbVolume.getAllVolumesForLine(rowItem.getIDWeb());
        	//Log.d("log_tag", "LINE CANTIDAD >>>" + rowItems.size());
        	
            holder.customerId.setText(String.valueOf(rowItem.getIDWeb()));
            //holder.txtDesc.setText(rowItem.getDescription() + "  (" + rowItems.size() + ")");
            holder.txtDesc.setText(rowItem.getDescription());
            
		} catch (Exception e) {
			//holder.customerId.setText("id no disponible");
            //holder.txtDesc.setText("descripcion");
		}
 
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