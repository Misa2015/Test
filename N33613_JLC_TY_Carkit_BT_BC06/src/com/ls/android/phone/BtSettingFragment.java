package com.ls.android.phone;

import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.setting.AbsBtSettingFragment;

public class BtSettingFragment extends AbsBtSettingFragment{

	@Override
	protected View onCreateViewPWD() {
		View view = SkinUtil.inflater(getActivity(), "setting_password_edit",null);
    	return view;
	}

	@Override
	protected View onCreateViewSettingEdit() {
		View view = SkinUtil.inflater(getActivity(), "setting_edit",null);
    	return view;
	}

	@Override
	protected View onCreatViewSettingLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_setting_layout",null);
    	return view;
	}

}
