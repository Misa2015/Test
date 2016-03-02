package com.ls.android.phone;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.contact.AbsContactAdapter;
import com.ls.bt.contact.Contact;

public class BtContactNumFragment extends AbsBtContactNumFragment{

	@Override
	protected View onCreateViewBtContactLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_contact_layout",null);
    	return view;
	}

	@Override
	protected void onInflateMenuContact(MenuInflater inflater, Menu menu) {
		inflater.inflate(R.menu.menu_contact, menu);
	}

	/**
	 * 聯繫人列表的Adapter
	 */
	@Override
	protected AbsContactAdapter onCreateAdapter(Context context,
			int textViewResourceId, List<Contact> contactLists) {
		return new ContactAdapter(getActivity(), 0, contactLists);
	}

}
