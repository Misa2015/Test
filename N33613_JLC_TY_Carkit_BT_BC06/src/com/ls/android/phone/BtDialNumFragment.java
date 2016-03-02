package com.ls.android.phone;

import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.inout.AbsBtDialNumFragment;

public class BtDialNumFragment extends AbsBtDialNumFragment{

	@Override
	protected View onCreateViewBtDialNumLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_dianum_layout",null);
    	return view;
	}

	@Override
	protected void onChangeViewDialPhoneState(View view, int state) {
		if(state==1){
			view.setBackgroundResource(R.drawable.dia_phone_selector);
		}else {
			view.setBackgroundResource(R.drawable.call_off_selector);
		}
		
	}

}
