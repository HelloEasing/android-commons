package com.easing.commons.android.greendao;

import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.io.Files;

import java.io.File;

public class CommonGreenDaoContext extends ContextWrapper {

    public CommonGreenDaoContext() {
        super(CommonApplication.ctx);
    }

    @Override
    public File getDatabasePath(String name) {
        String path = Files.getAndroidExternalFile("app/" + CommonApplication.projectName + "/common/" + name + ".db");
        return new File(path);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler handler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }
}
