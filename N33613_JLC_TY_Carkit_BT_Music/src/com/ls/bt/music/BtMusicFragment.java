package com.ls.bt.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.utils.log.JLog;
import com.android.utils.utils.SkinUtil;
import com.bt.BTController;
import com.bt.BTFeature;
import com.ls.bt.music.AbsBtMusicFragment;
import com.ls.bt.utils.Utils;
import com.nwd.kernel.utils.KernelConstant;
public class BtMusicFragment extends AbsBtMusicFragment{
	 private static final JLog LOG = new JLog("AbsBtMusicFragment", AbsBtMusicFragment.DEBUG, JLog.TYPE_DEBUG);
		public static final boolean DEBUG = true;
	@Override
	protected View onCreateViewBtMusic() {
		View view = SkinUtil.inflater(getActivity(), "bt_music_layout",null);
    	return view;
	}

	@Override
	protected void onChangePlayState(View view, boolean isPlaying) {
      if(isPlaying)
      {
    	  Utils.openSpeakerMusic(getActivity());
    	  LOG.print("music.sun.play");
    	  view.setBackgroundResource(R.drawable.music_pause_btn_selector);
      }
      else
      {
    	  Utils.closeSpeaker(getActivity());
    	  LOG.print("music.sun.pause");
    	  view.setBackgroundResource(R.drawable.music_play_btn_selector);
      }
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        try
        {
        	final BTFeature bt = btDeviceFeature;
        	//是否已经连接到A2DP
            if(!bt.isConnectA2DP())
            {
            	LOG.print("onResume");
                btStatus.setText(R.string.disconnect_successful);
            }else
            {
                btStatus.setText(R.string.bt_music_ok);
                
                //如果蓝牙音乐是暂停状态，则播放,isBTMusicPlaying音乐是否播放中
                if(!bt.isBTMusicPlaying())
                {
                	mIsPlaying = false;
                	mHandler.sendEmptyMessage(MESSAGE_PALY_OR_PAUSE);
                }
                //是否静音
                bt.setBtMusicMute(false);
                //发送播放音乐的广播
                Intent intent = new Intent(KernelConstant.ACTION_MCU_POWER_OFF);
                getActivity().sendBroadcast(intent);
            }
        }
        catch (Exception e)
        {
        	 e.printStackTrace();
        }
	}

	/*@Override
	protected void stopMusic() {
		Activity musicActivity = getActivity();
		musicActivity.finish();
		
	    Intent service = new Intent("com.ls.bt.music.BtMusic");
	    getActivity().stopService(service);
		
	}*/
	
}
