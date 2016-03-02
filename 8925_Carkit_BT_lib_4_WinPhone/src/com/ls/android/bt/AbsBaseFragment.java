package com.ls.android.bt;

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
import com.ls.bt.contact.AbsBtContactNumFragment;
import com.ls.android.phone.R;

abstract public class AbsBaseFragment extends Fragment implements OnClickListener{
	private static final JLog LOG = new JLog("AbsBaseFragment", AbsBaseFragment.DEBUG, JLog.TYPE_DEBUG);
    public static final boolean DEBUG = true;
    protected  View view = null;  //布局对象
    private InputMethodManager mInputMethodManager; 
	/** BT控制对象 */
	protected BTFeature btDeviceFeature;
	/** BT设备对象 */
	protected BTController btDeviceControler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}
	protected void moveMenu(Menu menu)
	{
	//	menu.removeItem(R.id.menu_settings_match);
	    menu.removeItem(R.id.delBt);
	    menu.removeItem(R.id.menu_call_detail);
	    menu.removeItem(R.id.menu_search);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		//moveMenu(menu);
		LOG.print("fatherBaseFragment="+AbsBtContactNumFragment.editTextSearch);
		if(null!=AbsBtContactNumFragment.editTextSearch)
		{
			mInputMethodManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputMethodManager.hideSoftInputFromWindow(AbsBtContactNumFragment.editTextSearch.getWindowToken(), 0);
		}
		
	}
   public AbsBaseFragment()
   {
	   
   }
	/**
	 * 初始化给蓝牙设备发送命令的对象
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
		LOG.print("BASEonKeyDown="+keyCode);
		return true;
	}
}
