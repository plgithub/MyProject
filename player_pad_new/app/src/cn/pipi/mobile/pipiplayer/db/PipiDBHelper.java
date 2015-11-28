package cn.pipi.mobile.pipiplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PipiDBHelper extends SQLiteOpenHelper {

	public static String dbName = "pipiplayer.db";
	// 数据库version
	private static int dbVersion = 5;

	private static PipiDBHelper pipiDBHelper;

	public final static String TABLENAME_SEARCHHISTORY = "pipiplayer_searchhistoryinfo";

	public final static String TABLENAME_DOWNLOADTABLE = "pipiplayer_storeinfo";

	public static final String TABLENAME_PLAYHISTORY = "pipiplayer_playhistoryinfo";
	// 收藏
	public static final String TABLENAME_FAVOURITE = "pipiplayer_favouriteinfo";
	
	public static final String TABLENAME_APPUPDATA = "download_info";

	// 搜索历史
	// 字段 名字
	private final String CREATE_SEARCHHISTORY_TABLE = "create table "
			+ TABLENAME_SEARCHHISTORY
			+ "(_id integer primary key autoincrement,MovieName Text " + ")";

	// 下载记录
	// 影片id 名字 来源 图片地址 下载地址 播放进度 下载状态 路径 大小 当前集数
	private final String CREATE_DOWNLOAD_TABLE = "create table "
			+ TABLENAME_DOWNLOADTABLE
			+ "(_id integer primary key autoincrement,sMovieID text,sMovieName Text, MovieSourceTag text  ,sMovieImgUrl Text ,sMovieUrl text, sMoviePlayProgress integer,sMovieStoreState  Integer,sMovieLoadState  Integer ,sMoviePath Text, sMovieSize integer ,currentPosition integer , pipiPlayListStr text,currentNum integer ,totalNum integer , moviedefinition text,sSourceIcon integer"
			+ ")";
	// 播放历史
	private final String CREATE_PLAYHISTORY_TABLE = "create table "
			+ TABLENAME_PLAYHISTORY
			+ "(_id integer primary key autoincrement ,sMovieID text, MovieName text ,MovieSourceTag text , MovieDownloadUrl text, MovieImgUrl text,MoviePlayProgress long , state integer ,sMoviePath Text ,currentPosition integer ,pipiPlayListStr text,watchDate text"
			+ "" + ")";

	// 收藏
	// movieid  moviename  movieposition  movieimgpath moviegrade moviesourcekey
	private final String CREATE_FAVOURITE_TABLE = "create table "
			+ TABLENAME_FAVOURITE
			+ "(_id integer primary key autoincrement ,movieid text,moviename text,movieposition text,movieimgpath text,moviegrade text ,moviesourcekey text"
			+ ")";
	
	// app 升级表
		private final String CREATE_APPUPDATA_TABLE = "create table "
				+ TABLENAME_APPUPDATA
				+ "(_id integer primary key autoincrement,apkName text, newver text , downloadedSize integer , finished  integer , filelength integer "
				+ ")";

	public PipiDBHelper(Context context) {
		super(context, dbName, null, dbVersion);
	}

	public PipiDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static synchronized PipiDBHelper getInstance(Context context) {
		if (pipiDBHelper == null) {
			pipiDBHelper = new PipiDBHelper(context);
		}
		return pipiDBHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建搜索历史表
		db.execSQL(CREATE_SEARCHHISTORY_TABLE);
		// 创建下载表
		db.execSQL(CREATE_DOWNLOAD_TABLE);
		// 创建播放历史表
		db.execSQL(CREATE_PLAYHISTORY_TABLE);
		// 创建收藏表
		db.execSQL(CREATE_FAVOURITE_TABLE);
		// 创建升级表
		db.execSQL(CREATE_APPUPDATA_TABLE);
		
	}

	public void deleteDatabase(Context context) {
		context.deleteDatabase(dbName);
	}

	/**
	 * 升级下载表
	 * 
	 * @param db
	 * @param newVersion
	 */
	private void onUpgradeDownloadTable(SQLiteDatabase db, int newVersion) {
		try {
			// 1, Rename table.
			db.beginTransaction();
			String tempTableName = TABLENAME_DOWNLOADTABLE + "_temp";
			String renameSql = "ALTER TABLE " + TABLENAME_DOWNLOADTABLE
					+ " RENAME TO " + tempTableName;
			db.execSQL(renameSql);
			// 2, Create table.
			db.execSQL(CREATE_DOWNLOAD_TABLE);
			// 3, Load data
			String loaddataSql = "INSERT INTO "
					+ TABLENAME_DOWNLOADTABLE
					+ " ("
					+ "sMovieID,sMovieName,sMovieImgUrl,sMovieUrl,sMoviePlayProgress,sMovieStoreState,sMovieLoadState,sMoviePath,sMovieSize,currentPosition,pipiPlayListStr"
					+ ") "
					+ " SELECT "
					+ "sMovieID,sMovieName,sMovieImgUrl,sMovieUrl,sMoviePlayProgress,sMovieStoreState,sMovieLoadState,sMoviePath,sMovieSize,currentPosition,pipiPlayListStr"
					+ " FROM " + tempTableName;
			db.execSQL(loaddataSql);
			// 4, Drop the temporary table.
			String dropTmpTable = "DROP TABLE IF EXISTS " + tempTableName;
			db.execSQL(dropTmpTable);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("---数据库升级失败---");
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 升级播放历史表
	 * 
	 * @param db
	 * @param newVersion
	 */
	private void onUpgradePlayHistoryTable(SQLiteDatabase db, int newVersion) {
		try {
			// 1, Rename table.
			db.beginTransaction();
			String tempTableName = TABLENAME_PLAYHISTORY + "_temp";
			String renameSql = "ALTER TABLE " + TABLENAME_PLAYHISTORY
					+ " RENAME TO " + tempTableName;
			db.execSQL(renameSql);
			// 2, Create table.
			db.execSQL(CREATE_PLAYHISTORY_TABLE);
			// 3, Load data
			// MovieName text ,MovieSourceTag text , MovieDownloadUrl text,
			// MovieImgUrl text,MoviePlayProgress int , state integer
			// ,sMoviePath Text ,currentPosition integer ,pipiPlayListStr
			// text,watchDate text
			String loaddataSql = "INSERT INTO "
					+ TABLENAME_PLAYHISTORY
					+ " ("
					+ "sMovieID,MovieName,MovieSourceTag,MovieDownloadUrl,MovieImgUrl,MoviePlayProgress,state,sMoviePath,currentPosition,pipiPlayListStr"
					+ ") "
					+ " SELECT "
					+ "sMovieID,sMovieName,MovieSourceTag,MovieDownloadUrl,MovieImgUrl,MoviePlayProgress,state,sMoviePath,currentPosition,pipiPlayListStr"
					+ " FROM " + tempTableName;
			db.execSQL(loaddataSql);
			// 4, Drop the temporary table.
			String dropTmpTable = "DROP TABLE IF EXISTS " + tempTableName;
			db.execSQL(dropTmpTable);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("---数据库升级失败---");
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("----自动执行数据库升级方法----onUpgrade");
		// 1, Rename table.
		// 2, Create table.
		// 3, Load data
		// 4, Drop the temporary table.
		onUpgradeDownloadTable(db, newVersion);
		// onUpgradePlayHistoryTable(db, newVersion);
	}

}
