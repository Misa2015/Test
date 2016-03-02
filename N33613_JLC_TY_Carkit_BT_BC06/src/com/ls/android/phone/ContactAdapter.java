package com.ls.android.phone;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.contact.AbsContactAdapter;
import com.ls.bt.contact.Contact;

public class ContactAdapter extends AbsContactAdapter{
    
	public ContactAdapter(Context context, int textViewResourceId,
			List<Contact> contactLists) {
		super(context, textViewResourceId, contactLists);
		Log.i("shen", "ContactAdapter ");
	}

	@Override
	protected View onCreateViewBtContactItem() {
		View view = SkinUtil.inflater(context, "bt_contact_item",null);
    	return view;
	}

}
