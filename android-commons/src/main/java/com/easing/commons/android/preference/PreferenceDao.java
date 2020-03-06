package com.easing.commons.android.preference;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.easing.commons.android.greendao.CommonGreenDaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;


public class PreferenceDao extends AbstractDao<Preference, String> {

    public static final String TABLENAME = "PREFERENCE";


    public static class Properties {
        public final static Property Key = new Property(0, String.class, "key", true, "KEY");
        public final static Property Value = new Property(1, String.class, "value", false, "VALUE");
    }


    public PreferenceDao(DaoConfig config) {
        super(config);
    }

    public PreferenceDao(DaoConfig config, CommonGreenDaoSession daoSession) {
        super(config, daoSession);
    }


    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"PREFERENCE\" (" +
                           "\"KEY\" TEXT PRIMARY KEY NOT NULL ," +
                           "\"VALUE\" TEXT);");
    }


    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PREFERENCE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Preference entity) {
        stmt.clearBindings();

        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(1, key);
        }

        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(2, value);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Preference entity) {
        stmt.clearBindings();

        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(1, key);
        }

        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(2, value);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    @Override
    public Preference readEntity(Cursor cursor, int offset) {
        Preference entity = new Preference(
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1)
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Preference entity, int offset) {
        entity.setKey(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setValue(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
    }

    @Override
    protected final String updateKeyAfterInsert(Preference entity, long rowId) {
        return entity.getKey();
    }

    @Override
    public String getKey(Preference entity) {
        if (entity != null) {
            return entity.getKey();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Preference entity) {
        return entity.getKey() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
