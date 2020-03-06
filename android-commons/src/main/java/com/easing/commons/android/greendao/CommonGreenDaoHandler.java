package com.easing.commons.android.greendao;

import android.database.sqlite.SQLiteDatabase;

public class CommonGreenDaoHandler {

    public static final int version = 1;

    public static CommonGreenDaoSession session;

    static {
        initSession();
    }

    public static void initSession() {
        clearSession();
        CommonGreenDaoHelper helper = new CommonGreenDaoHelper(new CommonGreenDaoContext(), "common", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        session = new CommonGreenDaoMaster(db).newSession();
    }

    public static void clearSession() {
        if (session == null)
            return;
        session.clear();
        session = null;
    }


}

