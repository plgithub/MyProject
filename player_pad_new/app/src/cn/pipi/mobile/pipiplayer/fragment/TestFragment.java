package cn.pipi.mobile.pipiplayer.fragment;

import cn.pipi.mobile.pipiplayer.hd.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends BaseFragment{

	@Override
	public void widgetInit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		View view=inflater.inflate(R.layout.testfragment, null);
		return  view;
	}

}
