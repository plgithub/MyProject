package cn.pipi.mobile.pipiplayer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.view.Window;

/**
 * Created by admin on 2015/11/5.
 */
public class DownloadInfoDialog extends AlertDialog {

    public DownloadInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    public DownloadInfoDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature((int)Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.slt_cnt_type);
    }
}
