package cn.pipi.mobile.pipiplayer;

import android.app.Application;

public class PipiApplication extends Application{
	
	
	private static PipiApplication pipiApplication;
	
	@Override
	public void onCreate() {
		super.onCreate();
		pipiApplication=this;
	}
	
	public static synchronized PipiApplication getinstance(){
		
		return pipiApplication;
	}
	

}
