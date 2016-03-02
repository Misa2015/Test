package com.ls.android.phone;

import android.view.View;

import com.android.utils.utils.SkinUtil;
import com.ls.android.bt.AbsBTMusicMainActivity;
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
