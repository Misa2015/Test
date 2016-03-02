package com.ls.android.phone;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.call.detail.AbsCallDetailAdapter;
import com.ls.bt.contact.Contact;

public class BtCallDetailFragment extends AbsBtCallDetailFragment{

	@Override
	protected View onCreateViewCallDetailLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_call_detail_layout",null);
    	return view;
	}

	@Override
	protected void onInflateMenuCallDetail(MenuInflater inflater, Menu menu) {
		inflater.inflate(R.menu.menu_call_detail, menu);
	}

	@Override
	protected AbsCallDetailAdapter onCreateAdapter(Context context,
			int textViewResourceId, List<Contact> items, int callTypeIcon) {
		
		return new CallDetailAdapter(context, textViewResourceId, items,callTypeIcon);
	}

}  
