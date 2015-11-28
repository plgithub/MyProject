package cn.pipi.mobile.pipiplayer.bean;

import java.util.List;

import cn.pipi.mobile.pipiplayer.DownTask;

/**
 * Created by admin on 2015/11/12.
 */
public class DeleteTaskBean {
    private int taskId;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    private List<DownTask> delList;
    private boolean canDelete=false;

    public List<DownTask> getDelList() {
        return delList;
    }

    public void setDelList(List<DownTask> delList) {
        this.delList = delList;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
}
