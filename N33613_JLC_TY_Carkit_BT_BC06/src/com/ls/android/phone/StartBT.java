package com.ls.android.phone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;

import com.android.utils.utils.SkinUtil;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBTMainActivity;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.bt.inout.AbsBtCallOnLingFragment;
import com.ls.bt.inout.AbsBtDialNumFragment;
import com.ls.bt.inout.AbsBtInComingCallFragment;
import com.ls.bt.match.AbsBtMatchFragment;
import com.ls.bt.match.AbsBtMatchRecordFragment;
import com.ls.bt.music.AbsBtMusicFragment;
import com.ls.bt.setting.AbsBtSettingFragment;
import com.ls.bt.utils.BtHelper;

public class StartBT extends AbsBTMainActivity{
	
	@Override
	protected View onCreateViewMainLayout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		View view = SkinUtil.inflater(this, "bt_main_layout",null);
    	return view;
	}
	
	@Override
	protected View onCreateViewActionbarLayout(int position) {
		View view=null;
		switch (position) {
		case TAB_INDEX_CALL_DETAIL:
			view = SkinUtil.inflater(this, "actionbar_calldeail_layout",null);
			break;
		case TAB_INDEX_CONTACT:
			view = SkinUtil.inflater(this, "actionbar_contact_layout",null);
			break;
		case TAB_INDEX_DIAL_NUM:
			view = SkinUtil.inflater(this, "actionbar_dianum_layout",null);
			break;			
		case TAB_INDEX_MATCH:
			view = SkinUtil.inflater(this, "actionbar_match_layout",null);
				break;
		case TAB_INDEX_SETTINGS:
			view = SkinUtil.inflater(this, "actionbar_setting_layout",null);
				break;
		}
		return view;
	}

	/**
	 * 连接
	 */
	@Override
	protected AbsBtMatchFragment onCreateFragmentBtMatch() {
		AbsBtMatchFragment fragment = new BtMatchFragment();
        return fragment;
	}

	/**
	 * 蓝牙拨号界面
	 */
	@Override
	protected AbsBtDialNumFragment onCreateFragmentBtDialNum() {
		AbsBtDialNumFragment fragment = new BtDialNumFragment();
        return fragment;
	}

	/**
	 * 蓝牙联系人界面
	 */
	@Override
	protected AbsBtContactNumFragment onCreateFragmentBtContactNum() {
		AbsBtContactNumFragment fragment = new BtContactNumFragment();
        return fragment;
	}

	/**
	 * 蓝牙呼叫日志界面
	 */
	@Override
	protected AbsBtCallDetailFragment onCreateFragmentBtCallDetail() {
		AbsBtCallDetailFragment fragment = new BtCallDetailFragment();
        return fragment;
	}

	/**
	 * 蓝牙音乐界面
	 */
	@Override
	protected AbsBtMusicFragment onCreateFragmentBtMusic() {
		AbsBtMusicFragment fragment = new BtMusicFragment();
        return fragment;
	}

	/**
	 * 蓝牙设置界面
	 */
	@Override
	protected AbsBtSettingFragment onCreateFragmentBtSetting() {
		AbsBtSettingFragment fragment = new BtSettingFragment();
        return fragment;
	}

/*	@Override
	protected AbsBtMatchRecordFragment onCreateFragmentBtMatchRecord() {
		AbsBtMatchRecordFragment fragment = new BtMatchRecordFragment();
        return fragment;
	}*/

	/**
	 * 蓝牙来电界面
	 */
	@Override
	protected AbsBtCallOnLingFragment onCreateFragmentBtCallOnLine() {
		AbsBtCallOnLingFragment fragment = new BtCallOnLingFragment();
        return fragment;
	}

	
	@Override
	protected AbsBtInComingCallFragment onCreateFragmentBtInComingCall() {
		AbsBtInComingCallFragment fragment = new BtInComingCallFragment();
        return fragment;
	}

}
