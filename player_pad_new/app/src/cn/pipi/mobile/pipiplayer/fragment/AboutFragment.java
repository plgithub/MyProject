package cn.pipi.mobile.pipiplayer.fragment;

import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.AppInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AboutFragment extends SetItemBaseFragment implements OnClickListener{
	
	private View view;
	private Context mContext;
	private Button backButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.about, null);
		widgetInit();
//		return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	
	public void widgetInit(){
		if(view==null)return ;
		backButton=(Button) view.findViewById(R.id.about_backbtn);
		backButton.setOnClickListener(this);
        TextView t1 = (TextView) view.findViewById(R.id.t1);
        TextView t2 = (TextView) view.findViewById(R.id.t2);
        t1.setText(AppInfo.getVersionName(mContext));
        t2.setText(AppInfo.getVersionCode(mContext)+"");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (hideActivityInterface != null) {
			hideActivityInterface.hideActivityView(false);
		}
	}
}
