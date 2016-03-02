package com.ls.bt.bc03;

import com.android.utils.log.JLog;
import com.bt.BTConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if(Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
            Intent startServiceIntent = new Intent(BTConstant.ACTION_SERVICE);
            context.startService(startServiceIntent);
        }
    }
}
