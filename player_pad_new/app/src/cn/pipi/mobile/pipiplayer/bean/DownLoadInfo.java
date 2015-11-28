package cn.pipi.mobile.pipiplayer.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import cn.pipi.mobile.pipiplayer.poly.HttpGetProxy;


public class DownLoadInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private String downloadID;

    private String downloadName;

    private String downAddress;

    private String downloadImgPath;

    private String downloadSourceTag;// 来源 皮皮 爱奇艺...

    private int downloadPosition;// 当前是第几集

    private int DownloadState;

    private long downloadComputeSize;


    private long downloadTotalSize;

    private String downloadPath;

    private int downloadCount; // 总段数

    private int currentDownloadIndex; // 当前下载的是第几段

    private int downloadProgress;

    private int downloadSpeed;

    private String playListStr;

    private List<String> taskList;

    private boolean isEditState;// 是否编辑

    public boolean isEditState() {
        return isEditState;
    }
    private MovieInfo movieInfo;
    public void setMovieInfo(MovieInfo movieInfo){
        this.movieInfo=movieInfo;
    }
    public MovieInfo getMovieInfo(){
        return movieInfo;
    }
    private int resid;
    public void setSourceIcon(int resid){
        this.resid=resid;
    }
    public int getSourceIcon(){
        return resid;
    }
    public void setEditState(boolean isEditState) {
        this.isEditState = isEditState;
    }

    public long getDownloadComputeSize() {
        return downloadComputeSize;
    }

    public void setDownloadComputeSize(long downloadComputeSize) {
        this.downloadComputeSize = downloadComputeSize;
    }

    private HttpGetProxy proxy;//下载任务

    public HttpGetProxy getProxy() {
        return proxy;
    }

    public void setProxy(HttpGetProxy proxy) {
        this.proxy = proxy;
    }

    public List<String> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<String> taskList) {
        this.taskList = taskList;
    }

    public String getPlayListStr() {
        return playListStr;
    }

    public void setPlayListStr(String playListStr) {
        this.playListStr = playListStr;
    }

    public String getDownloadImgPath() {
        return downloadImgPath;
    }

    public void setDownloadImgPath(String downImg) {

        if (!TextUtils.isEmpty(downImg) && downImg.startsWith("http")) {
            this.downloadImgPath = downImg;
        } else if (downImg != null && downImg.startsWith("/upload/Image/")) {
            this.downloadImgPath = "http://img.pipi.cn/imgupload/" + downImg.substring(14);
        } else {
            this.downloadImgPath = "http://img.pipi.cn/movies/126X168/" + downImg;
        }
    }

    public String getDownloadSourceTag() {
        return downloadSourceTag;
    }

    public void setDownloadSourceTag(String downloadSourceTag) {
        this.downloadSourceTag = downloadSourceTag;
    }


    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }


    public int getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(int downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getCurrentDownloadIndex() {
        return currentDownloadIndex;
    }

    public void setCurrentDownloadIndex(int currentDownloadIndex) {
        this.currentDownloadIndex = currentDownloadIndex;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public long getDownloadTotalSize() {
        return downloadTotalSize;
    }

    public void setDownloadTotalSize(long downloadTotalSize) {
        this.downloadTotalSize = downloadTotalSize;
    }

    public int getDownloadState() {
        return DownloadState;
    }

    public void setDownloadState(int downloadState) {
        DownloadState = downloadState;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String downloadID) {
        this.downloadID = downloadID;
    }

    public String getDownAddress() {
        return downAddress;
    }

    public void setDownAddress(String downAddress) {
        this.downAddress = downAddress;
    }


}
