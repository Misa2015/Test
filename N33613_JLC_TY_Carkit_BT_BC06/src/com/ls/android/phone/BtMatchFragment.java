package com.ls.android.phone;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.match.AbsBtMatchFragment;

public class BtMatchFragment extends AbsBtMatchFragment{

	@Override
	protected View onCreateViewBtMatch() {
		View view = SkinUtil.inflater(getActivity(), "bt_match_layout",null);
    	return view;
	}

	@Override
	protected Animation onCreateAnimationTip() {
		return AnimationUtils.loadAnimation(getActivity(), R.anim.tip);
	}

	protected void onChangeBtConnectState(View arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
 
	/*@Override
	protected void onChangeBtConnectState(View view, int state) {
		if(state ==1)
		{
			view.setBackgroundResource(R.drawable.bt_connected);
		}else
		{
			view.setBackgroundResource(R.drawable.map_rotate);
		}
	}*/

}
