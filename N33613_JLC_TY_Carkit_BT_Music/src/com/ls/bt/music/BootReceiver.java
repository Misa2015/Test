package com.ls.bt.music;

import com.ls.bt.utils.BtMusicService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class BootReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			
			Intent server = new Intent(context,BtMusicService.class);
			context.startService(server);
			
		}
	}
}
