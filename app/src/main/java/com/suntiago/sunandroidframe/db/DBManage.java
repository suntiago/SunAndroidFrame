package com.suntiago.sunandroidframe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.suntiago.sunandroidframe.entity.User;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.database.DaoConfig;

import java.util.List;

/**
 * Created by yu.zai on 2016/2/22.
 */
public class DBManage {
    private static KJDB mDb;
    private static Context mContext;
    final static int dbVersion = 1;
    public static void init(Context context) {
        mContext = context;
        DaoConfig d =new DaoConfig();
        d.setContext(context);
        d.setDbName("sun");
        d.setDbUpdateListener(new KJDB.DbUpdateListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
        d.setDbVersion(dbVersion);
        d.setDebug(true);
        mDb= KJDB.create(d);
    }

    public static void dd() {
        User user = new User(); //warn: The ugc must have id field or @ID annotate
        mDb.save(user);
    }

    public static void createTable(Class entrity) {
        mDb.save(entrity);
    }

    public static void createTable(List<? extends Object> entities) {
        mDb.save(entities);
    }

    public static boolean createTableBindId(Object entity) {
        return mDb.saveBindId(entity);
    }

}
