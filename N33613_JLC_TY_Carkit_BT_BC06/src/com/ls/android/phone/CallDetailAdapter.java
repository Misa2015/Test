package com.ls.android.phone;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.call.detail.AbsCallDetailAdapter;
import com.ls.bt.contact.Contact;

public class CallDetailAdapter extends AbsCallDetailAdapter{

	public CallDetailAdapter(Context context, int textViewResourceId,
			List<Contact> items, int callTypeIcon) {
		super(context, textViewResourceId, items, callTypeIcon);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View onCreteViewCallDetailItem() {
		View view = SkinUtil.inflater(context, "call_detail_item",null);
    	return view;
	}

}
