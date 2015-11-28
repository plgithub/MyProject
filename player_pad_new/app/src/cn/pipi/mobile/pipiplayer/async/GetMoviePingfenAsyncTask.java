package cn.pipi.mobile.pipiplayer.async;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;

import cn.pipi.mobile.pipiplayer.util.HandlerUtil;
import cn.pipi.mobile.pipiplayer.util.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;

/**
 * Created by admin on 2015/10/28.
 */
public	class GetMoviePingfenAsyncTask extends AsyncTask<String, Void, ArrayList<Float>> {
    private boolean isNetWork;
    private Handler handler;
    private int	mMovieID;

    public GetMoviePingfenAsyncTask(Handler handler, int movieID) {
        this.handler=handler;
        this.mMovieID = movieID;
        if(HandlerUtil.isConnect()){
            isNetWork=true;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<Float> result) {
        if(!isNetWork){
            HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NONETWORK, (int)1, null);
            return;
        }
        if(result == null){
            HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN, (int)1, null);
        }else {
            HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.EXEC_NORMOL, (int)1, result);
        }
    }

    @Override
    protected void onCancelled() {}

    protected ArrayList<Float> doInBackground(String... params){
        if (isNetWork) {
            return XMLPullParseUtil.getMoviePingfen(handler, mMovieID);
        }
        return null;
    }
}
