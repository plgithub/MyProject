package cn.pipi.mobile.pipiplayer;

import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class HdmiListener extends BroadcastReceiver {

    private static String HDMIINTENT = "android.intent.action.HDMI_PLUGGED";

    @Override
    public void onReceive(Context ctxt, Intent receivedIt) {
        String action = receivedIt.getAction();

        if (action.equals(HDMIINTENT)) {
            boolean state = receivedIt.getBooleanExtra("state", false);

            if (state == true) {
                Log.d("HDMIListner", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                Toast.makeText(ctxt, "HDMI Connected>>", Toast.LENGTH_LONG).show();    
            } else {
                Log.d("HDMIListner", "HDMI >>: Disconnected HDMI-TV");
                Toast.makeText(ctxt, "HDMI DisConnected>>", Toast.LENGTH_LONG).show();
            }
            
            LibVLC sVLC = LibVLC.getExistingInstance();
            if(sVLC != null)
            	sVLC.setEnableCloneScreen(state);
        }
    }
}