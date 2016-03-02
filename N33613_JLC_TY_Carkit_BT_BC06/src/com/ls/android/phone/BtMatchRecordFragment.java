package com.ls.android.phone;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.match.AbsBtMatchRecordFragment;

public class BtMatchRecordFragment extends AbsBtMatchRecordFragment {

	@Override
	protected View onCreateViewBtMatchRecord() {
		View view = SkinUtil.inflater(getActivity(), "bt_match_record_layout",
				null);
		return view;
	}

	@Override
	protected View onCreateViewItemMatchRecord() {
		View view = SkinUtil.inflater(getActivity(), "matchrecord_item", null);
		return view;
	}

	@Override
	protected void onInflateMenuMatchRecord(MenuInflater inflater, Menu menu) {
		inflater.inflate(R.menu.menu_match_record, menu);
	}

	@Override
	protected void onChangeViewItemConnectedState(View view, boolean select) {
		if (select) {
			view.setBackgroundResource(R.drawable.bt_item_connected);
		} else {
			view.setBackgroundResource(R.drawable.bt_item_disconnected);
		}
	}

}
