/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.pipi.mobile.pipiplayer.util;

import android.os.Handler;
import android.util.Log;

/**
 *
 * @author admin
 */
public class UITimer
{
    private int m_interval = 0;//执行时间间隔微秒
    private Handler m_handler = null;//句柄
    private Runnable m_startEvent = null;//启动事件
    private boolean m_bRun = false;//运行状态
    boolean m_bPausing = false;//暂停
    boolean m_bPasued = false;//重起
    OnUITimer m_timeEvent;

    public UITimer(int interval, OnUITimer tiemEvent) {
        m_handler = new Handler();
        m_startEvent = null;
        m_bRun = false;
        m_bPausing = false;
        m_bPasued = false;
        m_interval = interval;
        m_timeEvent = tiemEvent;
        setOnTickHandler(tiemEvent);
    }

    public void setOnTickHandler(OnUITimer tiemEvent) {
        if (null == tiemEvent) {
            return;
        }
        m_timeEvent = tiemEvent;
        m_startEvent = new Runnable() {

            public void run() {
                if (m_bPausing)//暂停中，完成暂停动作
                {
                    m_bPasued = true;
                    return;
                }
                m_handler.postDelayed(m_startEvent, m_interval);//在前面调用，以支持在，定时事件中停止自身
                m_timeEvent.onTimer();
            }
        };
    }

    public void updateInterval(int interval) {
        stop();
        m_interval = interval;
    }

    public boolean start() {
        if (m_bRun) {
            return true;
        }
        if (null == m_timeEvent) {
            return false;
        }
        m_bRun = true;
        m_bPausing = false;
        m_handler.postDelayed(m_startEvent, 0);
        return true;
    }

    public boolean start(long mills) {
        if (m_bRun) {
            return true;
        }
        if (null == m_timeEvent) {
            return false;
        }
        m_bRun = true;
        m_bPausing = false;
        m_handler.postDelayed(m_startEvent, mills);
        return true;
    }

    public void pause() {
        if (m_bPausing) {
            return;
        }
        m_bPausing = true;
    }

    public void restart() {
        start();
        if (!m_bPausing) {
            return;
        }
        m_bPausing = false;
        if (!m_bPasued) {
            return;
        }
        m_bPasued = false;
        m_handler.postDelayed(m_startEvent, 0);
        return;
    }

    public void stop() {
        if (!m_bRun) {
            return;
        }
        m_bRun = false;
        m_bPausing = false;
        m_handler.removeCallbacks(m_startEvent);
    }

    public static abstract class OnUITimer {

        public abstract void onTimer();
    }
}
