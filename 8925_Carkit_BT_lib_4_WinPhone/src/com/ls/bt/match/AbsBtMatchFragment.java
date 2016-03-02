package com.ls.bt.match;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.utils.log.JLog;
import com.bt.BTController;
import com.bt.BTDefine;
import com.ls.android.bt.AbsBaseFragment;
import com.ls.android.bt.BtBroadcastReceiver;
import com.ls.android.phone.R;

abstract public class AbsBtMatchFragment extends AbsBaseFragment{
	 private static final JLog LOG = new JLog("BtMatchFragment", AbsBtMatchFragment.DEBUG, JLog.TYPE_DEBUG);
     public static final boolean DEBUG = true;
	protected static final String TAG = "AbsBtMatchFragment";
	private ImageView mainimageView;
	private LinearLayout linear;
	private TextView mapTextView, mapPassword;
	private TextView matchDeviceName, matchDevicePassword;
	public final static String BLUE_GET_DEVICES = BTDefine.ACTION_GET_PAIR_DEVICE_RECORD;
	public final static String BLUE_CHANGE_DEVICES = BTDefine.ACTION_BT_CONNECTION_CHANGE;
	//private Animation am ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BLUE_CHANGE_DEVICES);
		getActivity().registerReceiver(mMatchStateReceiver, filter);
	}
	
	BroadcastReceiver mMatchStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BLUE_CHANGE_DEVICES.equals(action)) {
				int state=intent.getIntExtra(BTDefine.EXTRA_BT_CONNECTION_EVENT, 0);
				//onChangeBtConnectState(imageView_to,imageView_phone,state);
				if(state ==1)
				{
					//imageView.clearAnimation();
					imageView_to.setVisibility(View.VISIBLE);
					imageView_phone.setVisibility(View.VISIBLE);
				}
				else
				{
					imageView_to.setVisibility(View.GONE);
					imageView_phone.setVisibility(View.GONE);
					//imageView.setAnimation(am);
					//am.startNow();
				}
			}
		}

	};
	private ImageView imageView_to;
	private ImageView imageView_phone;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		LOG.print("currentstatus="+BtBroadcastReceiver.BT_PHONE_STATUS);
	}
	

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mMatchStateReceiver);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=onCreateViewBtMatch();
		//imageView = (ImageView) view.findViewById(R.id.imageview_mapping);
		mainimageView = (ImageView) view.findViewById(R.id.main_imageView);
		linear = (LinearLayout) view.findViewById(R.id.matchLinearLayout);
		mapTextView = (TextView) view.findViewById(R.id.map_username);
		mapPassword = (TextView) view.findViewById(R.id.map_password);
		 matchDeviceName = (TextView) view.findViewById(R.id.tv_match_device_name);
		 matchDevicePassword = (TextView) view.findViewById(R.id.tv_match_device_password);
		 
		 imageView_to = (ImageView) view.findViewById(R.id.main_imageView_to);
		 imageView_phone = (ImageView) view.findViewById(R.id.main_imageView_phone);
		 
		 
		 
	/*	am = onCreateAnimationTip();
		LinearInterpolator lin = new LinearInterpolator();
		am.setInterpolator(lin);*/
		//imageView.setAnimation(am);

		return view;
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		setShowHideConnAnimation();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//如果更改语言可能会报空 modify by yangmaolin 20140328
		if(null!=matchDeviceName)
		{
			matchDeviceName.setText(R.string.mapping_device_name);
			matchDevicePassword.setText(R.string.mapping_device_password);
		}
	}
	
	@Override
	public void completeDeviceConnectService(BTController cbtController) {
		// TODO Auto-generated method stub
		super.completeDeviceConnectService(cbtController);
		setDeviceName();
		setShowHideConnAnimation();
		if(view != null){
			imageView_to.setVisibility(View.VISIBLE);
			imageView_phone.setVisibility(View.VISIBLE);
		}
		
	}
	/**显示隐藏动画*/
	private void setShowHideConnAnimation()
	{
		if(null!=btDeviceFeature)
		{
			setDeviceName();
			if(view != null)
			{
				view.requestFocus();
				try
		        {
					LOG.print("isConnectHFP="+btDeviceFeature.isConnectHFP());
				    if(btDeviceFeature.isConnectHFP())
					{
						//onChangeBtConnectState(imageView,1);
						//imageView.clearAnimation();
						imageView_to.setVisibility(View.VISIBLE);
						imageView_phone.setVisibility(View.VISIBLE);
					}else{
						//onChangeBtConnectState(imageView,0);
						//imageView.setAnimation(am);
						imageView_to.setVisibility(View.GONE);
						imageView_phone.setVisibility(View.GONE);
						//am.startNow();
					}
			    	btDeviceFeature.setPairable(true);
		        }catch (RemoteException e){
		        	e.printStackTrace();
		        }
			}
		}
	}
	private void setDeviceName()
	{
		if (btDeviceFeature != null) {
			try {
			    if(mapTextView != null)
			    {
			        mapTextView.setText(btDeviceFeature.getLocalDeviceName());
			    }
			    if(mapPassword != null)
			    {
			        mapPassword.setText(btDeviceFeature.getPinCode());
			    }
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract View onCreateViewBtMatch();
	protected abstract Animation onCreateAnimationTip();
	//protected abstract void onChangeBtConnectState(View imageView_to,ImageView imageView_phone,int state);
}
