package com.ls.android.phone;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ls.bt.service.AbsBTContactService;

public class BtContactService extends AbsBTContactService{

	@Override
	public IBinder onBind(Intent intent) {
		return super.onBind(intent);
	}
	
	@Override
	public void onCreate() {
		
		
		
		super.onCreate();
	
		Log.i("book", "BtContactService onCreate........");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
