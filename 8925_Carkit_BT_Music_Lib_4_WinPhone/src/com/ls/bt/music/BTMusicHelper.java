package com.ls.bt.music;

import android.content.Context;
import android.media.AudioManager;

import com.nwd.kernel.source.SourceConstant;
import com.nwd.kernel.utils.KernelUtils;

public class BTMusicHelper {
    public static boolean mBlueToothPlayOrPasue = false;
    /**
     * 通知MCU蓝牙音乐是否有声音
     * 由于蓝牙音乐只需要这一条协议,所以这里使用测试用的方法来发送协议数据.
     * @param isPlay
     */
    public static final void notifyIsBTMusicPlay(Context context,boolean isPlay)
    {
//        byte[] protocal = KernelProtocal.generateNullProtocal(4, KernelProtocal.TYPE_BT, (byte) 0x06);
//        int offset = KernelProtocal.getProtocalDataStartOffset(protocal);
//        protocal[offset] = (byte) (isPlay ? 1 : 0);
//        KernelProtocal.calCheckSumAndWriteEndOfData(protocal);
//        Intent intent = new Intent(KernelConstant.ACTION_EMU_SEND_DATA_TO_MCU);
//        intent.putExtra(KernelConstant.EXTRA_PROTOCAL, protocal);
//        context.sendBroadcast(intent);
    	AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
    	if (isPlay) {
	    	int ret = audioManager.requestAudioFocus(null,
	                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);  
	        if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {  
	            android.util.Log.i("BtMusic", "====request audio focus fail. " + ret);  
	        }  
    	}
    	else
    	{
    		audioManager.abandonAudioFocus(null); 
    	}
    	audioManager = null;
    }
    
    /**
     * 应用程序启动
     * @param context
     */
    public static final void appStart(Context context)
    {
//        Intent intent = new Intent(KernelConstant.ACTION_MCU_PUBLIC_KEY_VALUE);
//        intent.putExtra(KernelConstant.EXTRA_KEY_VALUE, KernelConstant.KEYVALUE.KEY_BT_MUSIC);
//        context.sendBroadcast(intent);
        KernelUtils.appStart(context, SourceConstant.APPID_BT_MUSIC);
    }
}
