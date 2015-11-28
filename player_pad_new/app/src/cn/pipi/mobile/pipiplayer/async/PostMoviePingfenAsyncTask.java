package cn.pipi.mobile.pipiplayer.async;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.pipi.mobile.pipiplayer.util.HandlerUtil;
import cn.pipi.mobile.pipiplayer.util.HttpConnection;
import cn.pipi.mobile.pipiplayer.util.PipiPlayerConstant;

/**
 * Created by admin on 2015/10/28.
 */
public class PostMoviePingfenAsyncTask extends AsyncTask<String, Void, String> {

    private boolean isNetwork;
    private Handler handler;

    public PostMoviePingfenAsyncTask(Handler handler){
        this.handler=handler;
        if (HandlerUtil.isConnect()) {
            isNetwork = true;
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String info = null;
        if (isNetwork) {
//			http://user.pipi.cn/action/markCommit.jsp?movId=250151&score=2&d=1444901174665
            String httpURL = "http://user.pipi.cn/action/markCommit.jsp?movId=" + params[0] + "&score=" + params[1] + "&d=";
            java.util.Date dateNow = new java.util.Date(System.currentTimeMillis());
            httpURL += String.valueOf(dateNow.getTime());

            info = HttpConnection.requestGet(httpURL);
            Log.i("PostMoviePingfenAsyncT", info);
        }
        return info;
    }

    @Override
    protected void onPostExecute(String result) {
        if(!isNetwork || handler == null){
            return;
        }
        //refresh pingfen resutl
        Message message = handler.obtainMessage();
        message.what = PipiPlayerConstant.EXEC_NORMOL;
        message.arg1 = 2;
        message.obj = null;
        handler.sendMessageDelayed(message, 500);
    }
}
