package com.ls.android.phone;

import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.inout.AbsBtInComingCallFragment;

public class BtInComingCallFragment extends AbsBtInComingCallFragment{

	@Override
	protected View onCreateViewBtIncomingLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_in_coming_layout",null);
    	return view;
	}

}
