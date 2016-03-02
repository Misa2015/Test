package com.ls.android.phone;

import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.music.AbsBtMusicFragment;

public class BtMusicFragment extends AbsBtMusicFragment{

	@Override
	protected View onCreateViewBtMusic() {
		View view = SkinUtil.inflater(getActivity(), "bt_music_layout",null);
    	return view;
	}

	@Override
	protected void onChangePlayState(View view, boolean isPlaying) {
      if(isPlaying)
      {
    	  view.setBackgroundResource(R.drawable.music_pause_btn_selector);
      }
      else
      {
    	  view.setBackgroundResource(R.drawable.music_play_btn_selector);
      }
		
	}

}
