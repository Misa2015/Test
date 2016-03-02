package com.ls.bt.call.detail;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ls.bt.contact.Contact;
import com.ls.android.phone.R;
 
abstract public class AbsCallDetailAdapter extends ArrayAdapter<Contact> {

    private List<Contact> items;
    protected Context context;
    private int idCallType = 0;
    public AbsCallDetailAdapter(Context context, int textViewResourceId, List<Contact> items,int callTypeIcon) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
        this.idCallType = callTypeIcon;
    }
    public String getSelectPhoneNum(int i){
        if(i>=0 && items!=null && i<items.size()){
            return items.get(i).getNumber();
        }
        return "";
    }
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Log.i("cmd", "执行到了Adapter");
		
        View view = convertView;
        if (view == null) {
            view = onCreteViewCallDetailItem();
        }

        Contact item = items.get(position);
        
        Log.i("cmd", "Adapter显示  name="+item.getName()+"   number="+item.getNumber());
        
        if (item!= null) {
            // My layout has only one TextView
            TextView nameView = (TextView) view.findViewById(R.id.bluetooth_call_name);
            TextView numberView = (TextView) view.findViewById(R.id.bluetooth_call_number);
            ImageView ivCallType = (ImageView) view.findViewById(R.id.bluetooth_call_type);
            ivCallType.setBackgroundResource(idCallType);
            if (nameView != null) {
                // do whatever you want with your string and long
                nameView.setText(item.getName());
            }
            if (numberView != null) {
                // do whatever you want with your string and long
                numberView.setText(item.getNumber());
            }
         }

        return view;
    }
    /**
     * 获取联系人列表项的视图
     * @param inflater
     * @return
     */
    abstract protected View onCreteViewCallDetailItem();
}