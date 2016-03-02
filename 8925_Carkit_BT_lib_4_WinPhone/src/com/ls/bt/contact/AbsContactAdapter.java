package com.ls.bt.contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.ls.android.phone.R;
 
abstract public class AbsContactAdapter extends ArrayAdapter<Contact> implements Filterable{

	public static final boolean DEBUG = true;
    private List<Contact> items;
    protected Context context;
     
    public AbsContactAdapter(Context context, int textViewResourceId, List<Contact> contactLists) {
        super(context, textViewResourceId, contactLists);
        this.context = context;
        //数据回调
        this.items = contactLists;
    }
    
    public String getSelectPhoneNum(int i){
        if(i>=0 && items!=null && i<items.size()){
            return items.get(i).getNumber();
        }
        return "";
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
        	view=onCreateViewBtContactItem();
        }
        
        Contact item = items.get(position);
        if (item!= null) {
        	
        	Log.i("cont", "数据回调到Adapter显示    name="+item.getName()+"  number="+item.getNumber());
        	 
            TextView nameView = (TextView) view.findViewById(R.id.bt_company);
            TextView numberView = (TextView) view.findViewById(R.id.bt_teleNum);
            if (nameView != null) {
                nameView.setText(item.getName());
            }
            if (numberView != null) {
                numberView.setText(item.getNumber());
            }
        }
        return view;
    }
    protected abstract View onCreateViewBtContactItem();
}