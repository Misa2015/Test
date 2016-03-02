package com.ls.bt.music;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.android.utils.log.JLog;
import com.bt.BTController;
import com.bt.BTFeature;

@SuppressLint("NewApi")
abstract public class AbsBaseFragment extends Fragment implements OnClickListener{
	
    public static final boolean DEBUG = true;
    protected  View view = null; 
    private InputMethodManager mInputMethodManager; 
	/** BT控制对象 */
	protected BTFeature btDeviceFeature;
	/** BT设备对象 */
	protected BTController btDeviceControler;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}
	protected void moveMenu(Menu menu)
	{
	    menu.removeItem(R.id.delBt);
	    menu.removeItem(R.id.menu_call_detail);
	    menu.removeItem(R.id.menu_search);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//moveMenu(menu);
	}
   public AbsBaseFragment()
   {
	   
   }
	/**
	 * 初始化给蓝牙设备发送命令的对象
	 * 
	 */
	protected void completeDeviceConnectService(BTController cbtController) {
		btDeviceControler = cbtController;
		btDeviceFeature = btDeviceControler.getFeature();
	}
	@Override
	public void onClick(View v) {
		if(null==btDeviceFeature)return;
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
}
