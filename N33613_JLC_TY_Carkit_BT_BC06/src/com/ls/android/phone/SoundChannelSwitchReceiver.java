package com.ls.android.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ls.bt.call.detail.AbsBtCallDetailFragment;
import com.ls.bt.utils.Utils;
import com.nwd.kernel.utils.KernelUtils;

public class SoundChannelSwitchReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		if(KernelUtils.isHaveBtVolume(context.getContentResolver())) //判断蓝牙是否有声
		{
			Utils.switchSystemSound();
		}else 
		{
			Utils.switchBtSound();
		}
	}

}
