package cn.pipi.mobile.pipiplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.db.PipiDBHelper;

public class DBHelperDao {

    private static DBHelperDao dbHelperDao;

    PipiDBHelper pipiDBHelper;

    public DBHelperDao(Context context) {
        pipiDBHelper = PipiDBHelper.getInstance(context);
    }

    public static synchronized DBHelperDao getDBHelperDaoInstace(Context context) {
        if (dbHelperDao == null) {
            dbHelperDao = new DBHelperDao(context);
        }
        return dbHelperDao;
    }

    /**
     * 是否初次插入到搜索历史中
     *
     * @param movieName
     * @return
     */
    public boolean isFirsttoInsertSearchHistory(String movieName) {
        boolean isTmp = true;
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();

            String sql = "select * from "
                    + PipiDBHelper.TABLENAME_SEARCHHISTORY
                    + " where MovieName=?";
            // if(!sqlitedb.isOpen()){
            // SQLiteDatabase.openDatabase(PipiDBHelper.dbName, null,
            // Context.MODE_PRIVATE);
            // }
            cursor = sqlitedb.rawQuery(sql, new String[]{movieName});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                isTmp = false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }
        return isTmp;
    }

    /**
     * 插入数据到搜索历史
     *
     * @param name
     */
    public synchronized void insertToSearchHistoryTable(String name) {
        boolean isFirst = isFirsttoInsertSearchHistory(name);
        SQLiteDatabase sqLiteDatabase = null;
        sqLiteDatabase = pipiDBHelper.getWritableDatabase();
        // if(!sqLiteDatabase.isOpen()){
        // SQLiteDatabase.openDatabase(PipiDBHelper.dbName, null,
        // Context.MODE_PRIVATE);
        // }
        if (isFirst) {
            ContentValues values = new ContentValues();
            values.put("MovieName", name);
            sqLiteDatabase.insert(PipiDBHelper.TABLENAME_SEARCHHISTORY, null,
                    values);
        }
        String sql = "select  *  from " + PipiDBHelper.TABLENAME_SEARCHHISTORY;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        int count = 12;
        if (cursor != null && cursor.getCount() != 0
                && cursor.getCount() > count) {
            while (cursor.moveToNext()) {
                String movieName = cursor.getString(cursor
                        .getColumnIndex("MovieName"));
                sqLiteDatabase.delete(PipiDBHelper.TABLENAME_SEARCHHISTORY,
                        " MovieName = ? ", new String[]{movieName});
                break;
            }
        }
    }

    /**
     * 清空搜索历史
     */
    public void clearSearchHistory() {
        SQLiteDatabase database = pipiDBHelper.getWritableDatabase();
        database.delete(PipiDBHelper.TABLENAME_SEARCHHISTORY, null, null);
    }

    /**
     * 获取搜索历史列表
     */
    public synchronized List<String> getSearchHistoryList() {
        List<String> list = new ArrayList<String>();
        String sql = "select  *  from " + PipiDBHelper.TABLENAME_SEARCHHISTORY
                + " order by _id  desc ";
        Cursor cursor = null;
        SQLiteDatabase database = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            // if(!database.isOpen()){
            // SQLiteDatabase.openDatabase(PipiDBHelper.dbName, null,
            // Context.MODE_PRIVATE);
            // }
            cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() != 0) {
                // database.beginTransaction();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor
                            .getColumnIndex("MovieName"));
                    list.add(name);
                }
                // database.setTransactionSuccessful();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null)
                database.close();
        }
        return list;
    }

    public synchronized List<DownTask> getActiveDownLoad() {
        List<DownTask> mDownLoadTaskList = new ArrayList<DownTask>();
        String sql = "select * from " + PipiDBHelper.TABLENAME_DOWNLOADTABLE;
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            cursor = database.rawQuery(sql, null);
            if (cursor == null || cursor.getCount() == 0)
                return mDownLoadTaskList;
            while (cursor.moveToNext()) {
                // 暂未加清晰度
                DownLoadInfo downLoadInfo = new DownLoadInfo();
                downLoadInfo.setDownloadID(cursor.getString(cursor
                        .getColumnIndex("sMovieID")));
                downLoadInfo.setDownloadName(cursor.getString(cursor
                        .getColumnIndex("sMovieName")));
                downLoadInfo.setDownloadSourceTag(cursor.getString(cursor
                        .getColumnIndex("MovieSourceTag")));
                downLoadInfo.setDownloadImgPath(cursor.getString(cursor
                        .getColumnIndex("sMovieImgUrl")));
                downLoadInfo.setDownAddress(cursor.getString(cursor
                        .getColumnIndex("sMovieUrl")));
                downLoadInfo.setDownloadPath(cursor.getString(cursor
                        .getColumnIndex("sMoviePath")));
                downLoadInfo.setPlayListStr(cursor.getString(cursor
                        .getColumnIndex("pipiPlayListStr")));
                downLoadInfo.setDownloadProgress(cursor.getInt(cursor
                        .getColumnIndex("sMoviePlayProgress")));
                downLoadInfo.setDownloadTotalSize(cursor.getInt(cursor
                        .getColumnIndex("sMovieSize")));
                downLoadInfo.setDownloadPosition(cursor.getInt(cursor
                        .getColumnIndex("currentPosition")));
                downLoadInfo.setCurrentDownloadIndex(cursor.getInt(cursor
                        .getColumnIndex("currentNum")));
                downLoadInfo.setDownloadCount(cursor.getInt(cursor
                        .getColumnIndex("totalNum")));
                downLoadInfo.setSourceIcon(cursor.getInt(cursor.getColumnIndex("sSourceIcon")));
                int state = cursor.getInt(cursor
                        .getColumnIndex("sMovieLoadState"));

                // System.out.println("state---"+state);
                if (state != 1) {
                    downLoadInfo.setDownloadState(DownTask.TASK_PAUSE_DOWNLOAD);
                } else {
                    downLoadInfo.setDownloadState(DownTask.TASK_FINISHED);
                }
                DownTask downTask = new DownTask(downLoadInfo);
                mDownLoadTaskList.add(downTask);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {

        }
        return mDownLoadTaskList;

    }

    /**
     * 是否初次插入到数据库中 根据影片id判断
     */
    public boolean isFirstToPlayHistoryTable(String movieID) {
        boolean tmp = true;
        if (TextUtils.isEmpty(movieID))
            return false; // 如果movieid 为空 则不插入数据库
        String sql = "select * from " + PipiDBHelper.TABLENAME_PLAYHISTORY
                + " where sMovieID =?";
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            cursor = database.rawQuery(sql, new String[]{movieID});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                tmp = false;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null)
                database.close();
        }
        return tmp;
    }

    public boolean isFirstToinsertFavouriteTable(String movieId) {
        boolean isFirst = true;
        if (TextUtils.isEmpty(movieId))
            return false;
        String sql = "select * from " + PipiDBHelper.TABLENAME_FAVOURITE
                + " where movieid =?";
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            cursor = database.rawQuery(sql, new String[]{movieId});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                isFirst = false;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null)
                database.close();
        }

        return isFirst;
    }

    /**
     * 插入数据到收藏表
     */
    public void insertDataToFavouriteTable(MovieInfo movieInfo) {
        if (movieInfo == null)
            return;
        SQLiteDatabase sqLiteDatabase = pipiDBHelper.getWritableDatabase();
        try {
            // movieid moviename movieposition movieimgpath moviegrade
            // moviesourcekey
            ContentValues contentValues = new ContentValues();
            contentValues.put("movieid", movieInfo.getMovieID());
            contentValues.put("moviename", movieInfo.getMovieName());
            contentValues.put("movieimgpath", movieInfo.getMovieImgPath());
            contentValues.put("moviegrade", movieInfo.getGrade());
            sqLiteDatabase.insert(PipiDBHelper.TABLENAME_FAVOURITE, null,
                    contentValues);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {

        }
    }

    /**
     * 根据影片 id 删除收藏表某条数据
     *
     * @param movieid
     */
    public void deleteFavouriteFromMovieid(String movieid) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.delete(PipiDBHelper.TABLENAME_FAVOURITE,
                    "movieid = ?", new String[]{movieid});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    /**
     * 获取搜索列表
     */
    public List<MovieInfo> getFavouriteList() {
        List<MovieInfo> list = new ArrayList<MovieInfo>();
        String sql = "select * from " + PipiDBHelper.TABLENAME_FAVOURITE;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor == null || cursor.getCount() == 0)
                return list;
            // movieid moviename movieposition movieimgpath moviegrade
            // moviesourcekey
            while (cursor.moveToNext()) {
                MovieInfo movieInfo = new MovieInfo();
                movieInfo.setMovieID(cursor.getString(cursor
                        .getColumnIndex("movieid")));
                movieInfo.setMovieName(cursor.getString(cursor
                        .getColumnIndex("moviename")));
                movieInfo.setMovieImgPath(cursor.getString(cursor
                        .getColumnIndex("movieimgpath")));
                movieInfo.setGrade(cursor.getString(cursor
                        .getColumnIndex("moviegrade")));
                list.add(movieInfo);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
        return list;
    }

    /**
     * 插入影片数据到播放历史列表
     */
    public void insertToPlayHistoryTable(HistoryBean historyBean) {
        if (historyBean == null)
            return;
        // 根据id判断是否之前以插入过数据库
        boolean isFirst = isFirstToPlayHistoryTable(historyBean.getMovieId());
        if (!isFirst)
            return;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("sMovieID", historyBean.getMovieId());
            contentValues.put("MovieName", historyBean.getMovieName());
            //
            Log.i("TAG999", "保存 第" + (historyBean.getPlayPosition() + 1) + "集");
            contentValues.put("currentPosition", historyBean.getPlayPosition());
            contentValues.put("MovieSourceTag", historyBean.getSourceTag());
            contentValues.put("MovieDownloadUrl", historyBean.getMovieUrl());
            contentValues.put("MoviePlayProgress", 0);
            contentValues.put("watchDate", historyBean.getWatchedDate());
            sqLiteDatabase.insert(PipiDBHelper.TABLENAME_PLAYHISTORY, null,
                    contentValues);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null)
                ;
            // sqLiteDatabase.close();
        }
    }

    public void updatePlayHistoryName(String movieID, HistoryBean historyBean) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("MovieName", historyBean.getMovieName());
            // contentValues.put("currentPosition",
            // historyBean.getPlayPosition());
            contentValues.put("MovieSourceTag", historyBean.getSourceTag());
            contentValues.put("watchDate", historyBean.getWatchedDate());
            contentValues.put("MovieDownloadUrl", historyBean.getMovieUrl());
            // contentValues.put("MoviePlayProgress", 0);
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_PLAYHISTORY,
                    contentValues, "sMovieID = ? ", new String[]{movieID});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            // if (sqLiteDatabase != null)
            // sqLiteDatabase.close();
        }
    }


    public void updatePlayHistoryPosition(String movieId, int position) {

        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("currentPosition", position);
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_PLAYHISTORY,
                    contentValues, "sMovieID = ? ", new String[]{movieId});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }


    public void updatePlayHistoryPlayProgress(String movieId, long time) {

        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("MoviePlayProgress", time);
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_PLAYHISTORY,
                    contentValues, "sMovieID = ? ", new String[]{movieId});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public void delPlayHistoryFromUrl(String movieUrl) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.delete(PipiDBHelper.TABLENAME_PLAYHISTORY,
                    "MovieDownloadUrl = ?", new String[]{movieUrl});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public int getPlayPositionFromPlayHistory(String movieId) {
        int playPosition = 0;
        if (isFirstToPlayHistoryTable(movieId)) {
            return playPosition;
        }
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String sql = "select currentPosition from "
                + PipiDBHelper.TABLENAME_PLAYHISTORY + " where sMovieID = ?";
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery(sql, new String[]{movieId});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                playPosition = cursor.getInt(cursor
                        .getColumnIndex("currentPosition"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
        return playPosition;
    }

    public long getPlayProgressFromPlayHistory(String movieId) {
        long progress = 0;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String sql = "select MoviePlayProgress from "
                + PipiDBHelper.TABLENAME_PLAYHISTORY + " where sMovieID = ?";
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery(sql, new String[]{movieId});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                progress = cursor.getLong(cursor
                        .getColumnIndex("MoviePlayProgress"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
        return progress;
    }

    public List<HistoryBean> getPlayHistoryList() {
        List<HistoryBean> list = new ArrayList<HistoryBean>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String sql = "select * from " + PipiDBHelper.TABLENAME_PLAYHISTORY;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    HistoryBean historyBean = new HistoryBean();
                    // MovieDownloadUrl MovieImgUrl MoviePlayProgress
                    historyBean.setMovieId(cursor.getString(cursor
                            .getColumnIndex("sMovieID")));
                    historyBean.setMovieName(cursor.getString(cursor
                            .getColumnIndex("MovieName")));
                    historyBean.setMovieUrl(cursor.getString(cursor
                            .getColumnIndex("MovieDownloadUrl")));
                    historyBean.setPlayPosition(cursor.getInt(cursor
                            .getColumnIndex("currentPosition")));
                    historyBean.setSourceTag(cursor.getString(cursor
                            .getColumnIndex("MovieSourceTag")));
                    historyBean.setWatchedDate(cursor.getString(cursor
                            .getColumnIndex("watchDate")));
                    historyBean.setWatchedTime(cursor.getLong(cursor
                            .getColumnIndex("MoviePlayProgress")));
                    list.add(0, historyBean);
                }
            }
        } catch (Exception e) {
            Log.d(AppConfig.Tag, "数据库获取播放历史列表异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }

        return list;
    }

    public void updateDownloadProgressFromUrl(String url, int progress) {
        String sql = "update " + PipiDBHelper.TABLENAME_DOWNLOADTABLE
                + " set sMoviePlayProgress =" + progress + " where sMovieUrl="
                + "'" + url + "'";
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(sql);
        } catch (Exception e) {
            System.out.println("更新下载进度异常");
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public void updateDownloadState(String url, int state) {

        String sql = "update " + PipiDBHelper.TABLENAME_DOWNLOADTABLE
                + " set sMovieLoadState =" + state + " where sMovieUrl=" + "'"
                + url + "'";
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(sql);
        } catch (Exception e) {
            System.out.println("更新下载进度异常");
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public void updateDownloadSizeAndPath(String url, long size, String path) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("sMovieSize", size);
            contentValues.put("sMoviePath", path);
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_DOWNLOADTABLE,
                    contentValues, "sMovieUrl= ?", new String[]{url});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public void updateDownloadSizeAndPath2(String url, String path) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("sMoviePath", path);
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_DOWNLOADTABLE,
                    contentValues, "sMovieUrl= ?", new String[]{url});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    public void updateDownloadCurrentIndexAndTotalCount(String url,
                                                        DownLoadInfo downLoadInfo) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("currentNum",
                    downLoadInfo.getCurrentDownloadIndex());
            contentValues.put("totalNum", downLoadInfo.getDownloadCount());
            sqLiteDatabase.update(PipiDBHelper.TABLENAME_DOWNLOADTABLE,
                    contentValues, "sMovieUrl= ?", new String[]{url});
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    /**
     * 判断是否下载列表中 是否已存在该记录 根据下载地址判断 是否已插入到表中
     */
    public boolean isFirstInsertToDownloadTable(String downloadUrl) {
        boolean tmp = true;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        String sql = "select *  from " + PipiDBHelper.TABLENAME_DOWNLOADTABLE
                + " where sMovieUrl=?";
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery(sql, new String[]{downloadUrl});
            if (cursor != null && cursor.getCount() != 0) {
                tmp = false; // 数据库中有该记录
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // sqLiteDatabase.close();
        }
        return tmp;
    }

    /**
     * 插入影片数据到下载表 数据库中
     */

    public void insertToDownloadTable(DownLoadInfo downLoadInfo) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("sMovieID", downLoadInfo.getDownloadID());
            contentValues.put("sMovieName", downLoadInfo.getDownloadName());
            contentValues.put("MovieSourceTag",
                    downLoadInfo.getDownloadSourceTag());
            contentValues.put("currentPosition",
                    downLoadInfo.getDownloadPosition());
            contentValues.put("sMovieLoadState",
                    downLoadInfo.getDownloadState());
            contentValues
                    .put("sMovieImgUrl", downLoadInfo.getDownloadImgPath());
            contentValues.put("sMovieUrl", downLoadInfo.getDownAddress());
            contentValues.put("sSourceIcon",downLoadInfo.getSourceIcon());
            sqLiteDatabase.insert(PipiDBHelper.TABLENAME_DOWNLOADTABLE, null,
                    contentValues);
        } catch (Exception e) {
            Log.d(AppConfig.Tag, "**插入数据到下载表异常**");
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }

    }

//    

    /**
     * 根据下载地址删除 下载表中某一条记录
     *
     * @param movieUrl
     */
    public void deleteDownloadTaskFromMovieURL(String movieUrl) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.delete(PipiDBHelper.TABLENAME_DOWNLOADTABLE,
                    "sMovieUrl=?", new String[]{movieUrl});
        } catch (Exception e) {
            System.out.println("下载表  删除Exception");
        } finally {
            if (sqLiteDatabase != null) {
                // sqLiteDatabase.close();
            }
        }
    }

    /**
     * 删除下载表中所有数据
     */
    public void delAllDownloadTableData() {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            String sql = "delete * from "
                    + PipiDBHelper.TABLENAME_DOWNLOADTABLE;
            sqLiteDatabase.execSQL(sql);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {

        }
    }

    //查找下载完成后本地保存路径
    public synchronized String getLocalByUrl(String sMovieurl) {
        // TODO Auto-generated method stub
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = pipiDBHelper.getReadableDatabase();

            String sql = "select * from "
                    + pipiDBHelper.TABLENAME_DOWNLOADTABLE
                    + " where sMovieUrl= '" + sMovieurl + "'";
            cursor = database.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                return cursor.getString(cursor.getColumnIndex("sMoviePath"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            cursor.close();
            database.close();
        }

        return null;
    }


    /**
     * 检测是否初次插入到数据库升级表中
     *
     * @return
     */
    public boolean isFirstInsertUpdataTable(String versionName) {
        boolean isfirst = true;
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();

            String sql = "select newver from "
                    + PipiDBHelper.TABLENAME_APPUPDATA + " where newver =?";
            cursor = sqlitedb.rawQuery(sql, new String[]{versionName});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                isfirst = false;
            }
            // if (cursor.moveToNext()) {
            // isStore = true;
            // }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }
        return isfirst;
    }

    /**
     * apk 升级插入数据
     */
    public void InsertAppUpdata(String apkName, String newVer, int finish,
                                int downloadSize) {
        if (!isFirstInsertUpdataTable(newVer))
            return;
        System.out.println("插入数据到 升级表");
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = pipiDBHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("apkName", apkName);
            values.put("newver", newVer);
            values.put("downloadedSize", downloadSize);
            values.put("finished", finish);
            values.put("filelength", 0);
            sqLiteDatabase.insert(PipiDBHelper.TABLENAME_APPUPDATA, null,
                    values);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }

    }

    public void delAppUpdata() {
        SQLiteDatabase database = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            database.delete(PipiDBHelper.TABLENAME_APPUPDATA, null, null);

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (database != null)
                database.close();
        }
    }

    public void delAppUpdata(String versionName) {
        SQLiteDatabase database = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            database.delete(PipiDBHelper.TABLENAME_APPUPDATA, "newver = ?",
                    new String[]{versionName});

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (database != null)
                database.close();
        }
    }

    public int getAppUpdataDownloadSize(String newver) {
        int tmp = 0;
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();

            String sql = "select downloadedSize from "
                    + PipiDBHelper.TABLENAME_APPUPDATA + " where newver =?";
            cursor = sqlitedb.rawQuery(sql, new String[]{newver});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                tmp = cursor.getInt(cursor.getColumnIndex("downloadedSize"));
            }
            // if (cursor.moveToNext()) {
            // isStore = true;
            // }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }

        return tmp;
    }

    public int getAppUpdataFileLength(String newver) {
        int tmp = 0;
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();

            String sql = "select filelength from "
                    + PipiDBHelper.TABLENAME_APPUPDATA + " where newver =?";
            cursor = sqlitedb.rawQuery(sql, new String[]{newver});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                tmp = cursor.getInt(cursor.getColumnIndex("filelength"));
            }
            // if (cursor.moveToNext()) {
            // isStore = true;
            // }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }

        return tmp;
    }

    public String getAppUpdataNewVer(String newver) {
        String tmp = "";
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();
            String sql = "select newver from "
                    + PipiDBHelper.TABLENAME_APPUPDATA + " where newver =?";
            cursor = sqlitedb.rawQuery(sql, new String[]{newver});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                tmp = cursor.getString(cursor.getColumnIndex("newver"));
            }
            // if (cursor.moveToNext()) {
            // isStore = true;
            // }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }

        return tmp;
    }

    public void updataApp(int downloadSize, int finish, String newver) {
        SQLiteDatabase database = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("downloadedSize", downloadSize);
            values.put("finished", finish);
            database.update(PipiDBHelper.TABLENAME_APPUPDATA, values,
                    "newver = ? ", new String[]{newver});
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public void updataAppFileLenghth(int fileLength, String newver) {
        SQLiteDatabase database = null;
        try {
            database = pipiDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("filelength", fileLength);
            database.update(PipiDBHelper.TABLENAME_APPUPDATA, values,
                    "newver = ? ", new String[]{newver});
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public boolean isFinishedAppUpdata(String newver) {
        boolean isFinish = true;
        Cursor cursor = null;
        SQLiteDatabase sqlitedb = null;
        try {
            sqlitedb = pipiDBHelper.getWritableDatabase();

            String sql = "select finished from "
                    + PipiDBHelper.TABLENAME_APPUPDATA + " where newver =?";
            cursor = sqlitedb.rawQuery(sql, new String[]{newver});
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToNext()) {
                int temp = cursor.getInt(cursor.getColumnIndex("finished"));
                if (temp != 1) {
                    isFinish = false;
                }
            } else {
                isFinish = false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqlitedb.close();
        }
        return isFinish;
    }

}
