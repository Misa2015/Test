package com.ls.bt.music;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.bt.music.AbsBtMusicFragment;

public class BTMusicMainActivity extends AbsBTMusicMainActivity{

	
	
	@Override
	protected View onCreateViewBtMusicLayout() {
	 
		View view = SkinUtil.inflater(this, "bt_music_main_layout",null);
    	return view;
	}

	@Override
	protected View onCreateViewActionbarMusicLayout() {
		View view = SkinUtil.inflater(this, "actionbar_music_layout",null);
    	return view;
	}

	@Override
	protected AbsBtMusicFragment onCreateFragmentBtMusic() {
		AbsBtMusicFragment btMusicFragment = new BtMusicFragment();
		return btMusicFragment;
	}

}
