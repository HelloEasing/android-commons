package com.easing.commons.android.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.preference.PreferenceDao;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


public class CommonGreenDaoMaster extends AbstractDaoMaster {

    public static void createAllTables(Database db, boolean ifNotExists) {
        PreferenceDao.createTable(db, ifNotExists);
    }


    public static void dropAllTables(Database db, boolean ifExists) {
        PreferenceDao.dropTable(db, ifExists);
    }


    public static CommonGreenDaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        CommonGreenDaoMaster daoMaster = new CommonGreenDaoMaster(db);
        return daoMaster.newSession();
    }

    public CommonGreenDaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public CommonGreenDaoMaster(Database db) {
        super(db, CommonGreenDaoHandler.version);
        registerDaoClass(PreferenceDao.class);
    }

    public CommonGreenDaoSession newSession() {
        return new CommonGreenDaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public CommonGreenDaoSession newSession(IdentityScopeType type) {
        return new CommonGreenDaoSession(db, type, daoConfigMap);
    }


    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, CommonGreenDaoHandler.version);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, CommonGreenDaoHandler.version);
        }

        @Override
        public void onCreate(Database db) {
            Console.info("GreenDAO Create Tables");
            createAllTables(db, false);
        }
    }


    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Console.info("GreenDAO Upgrade");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
