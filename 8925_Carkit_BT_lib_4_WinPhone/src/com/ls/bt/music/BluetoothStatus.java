package com.ls.bt.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bt.BTDefine;
import com.ls.bt.utils.BtHelper;

public class BluetoothStatus extends BroadcastReceiver {
	
	public BluetoothStatus() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {				
		String action = intent.getAction();	
		
		if(action.equals(BTDefine.ACTION_BT_MUSIC_PAUSE)) {
		    Log.d("BluetoothStatus", "****A2DP_AUDIO_RELEASE***** action = " + action);
		    BtHelper.mBlueToothPlayOrPasue = false;
		} else if(action.equals(BTDefine.ACTION_BT_MUSIC_PLAY)){
		    Log.d("BluetoothStatus", "****A2DP_AUDIO_ESTABLISH***** action = " + action);
		    BtHelper.mBlueToothPlayOrPasue = true;
		}
	}

}
