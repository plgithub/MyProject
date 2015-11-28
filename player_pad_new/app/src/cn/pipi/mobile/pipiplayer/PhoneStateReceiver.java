/*****************************************************************************
 * PhoneStateReceiver.java
 *****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package cn.pipi.mobile.pipiplayer;


import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)
           ||intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED))//卸载SD卡
        {
        	//如果当前使用的是卡路径 卡被拔出
//        	if(FileUtils.RootPath.contains(intent.getData().getPath())){
//        		DataUtil.getToast(context, context.getString(R.string.sdRemoved),2000);
//				try {
//					Thread.sleep(2000);
//					System.exit(0);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}
        }else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
                state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            LibVLC libVLC = LibVLC.getExistingInstance();
            if (libVLC != null && libVLC.isPlaying())
                libVLC.pause();
        }
    }

}
