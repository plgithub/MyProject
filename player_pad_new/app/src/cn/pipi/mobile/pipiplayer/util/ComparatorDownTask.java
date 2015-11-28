package cn.pipi.mobile.pipiplayer.util;

import java.util.Comparator;

import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;

public class ComparatorDownTask implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        DownLoadInfo info1 = ((DownTask) arg0).getDownLoadInfo();
        DownLoadInfo info2 = ((DownTask) arg1).getDownLoadInfo();
        int flag = 0;
        if (info1 != null && info2 != null) {
            if(info1.getDownloadName()!=null&&info2.getDownloadName()!=null)
            flag = info1.getDownloadName().compareTo(info2.getDownloadName());
            if (flag == 0) {
                return (String.valueOf(info1.getDownloadPosition())).compareTo(String.valueOf(info2.getDownloadPosition()));
            }
        }

        return flag;
    }

}
