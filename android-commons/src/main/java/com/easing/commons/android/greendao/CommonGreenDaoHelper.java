package com.easing.commons.android.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

public class CommonGreenDaoHelper extends CommonGreenDaoMaster.DevOpenHelper {

    private Context context;
    private String name;

    public CommonGreenDaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        this.context = context;
        this.name = name;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //默认删除所有表，并重建数据库表结构
        super.onUpgrade(db, oldVersion, newVersion);

        //如果需要保留旧数据，则手动升级，逐个版本升级表结构
        if (false) {
            int version = oldVersion;
            while (version != newVersion)
                upgradeTo(db, ++version);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //降级同升级同等处理，重建数据库
        super.onUpgrade(db, oldVersion, newVersion);
    }

    //逐个版本升级表结构
    //如需手动升级数据库，请覆写这个方法
    protected void upgradeTo(Database db, int version) {
        switch (version) {
            //版本10升级到版本11逻辑
            case 11:
                //更新表结构...
                break;

            //版本11升级到版本12逻辑
            case 12:
                //更新表结构...
                break;
        }
    }
}
