package com.ls.android.phone;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;

import com.android.utils.utils.SkinUtil;
import com.ls.android.phone.R;
import com.ls.bt.inout.AbsBtCallOnLingFragment;

public class BtCallOnLingFragment extends AbsBtCallOnLingFragment{

	@Override
	protected View onCreateViewBtCallOnLineDialog() {
		View view = SkinUtil.inflater(getActivity(), "bt_call_on_line_dialog",null);
    	return view;
	}

	@Override
	protected Dialog onCreateDialogFromDialogStyle() {
		Dialog a = new Dialog(getActivity(),R.style.DialogStyle);
		LayoutParams param = a.getWindow().getAttributes();
        param.width = 600;
        param.height = 600;
        a.getWindow().setAttributes(param);
		return a;
	}

	@Override
	protected View onCreateViewCallOnLineLayout() {
		View view = SkinUtil.inflater(getActivity(), "bt_call_on_line_layout",null);
    	return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
 
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("wuguisheng", "BtCallOnLingFragment onStop");
 
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("wuguisheng", "BtCallOnLingFragment onDestroy");
 
	}

}
