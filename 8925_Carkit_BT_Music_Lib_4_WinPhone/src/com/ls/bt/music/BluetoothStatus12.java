package com.ls.bt.music;

import com.bt.BTDefine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothStatus12 extends BroadcastReceiver {
	
	public BluetoothStatus12() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {				
		String action = intent.getAction();	
		
		if(action.equals(BTDefine.ACTION_BT_MUSIC_PAUSE)) {
			BTMusicHelper.mBlueToothPlayOrPasue = false;
		} else if(action.equals(BTDefine.ACTION_BT_MUSIC_PLAY)){
		    BTMusicHelper.mBlueToothPlayOrPasue = true;
		}
	}
}
