package com.easing.commons.android.preference;

import com.easing.commons.android.data.JSON;
import com.easing.commons.android.greendao.CommonGreenDaoHandler;

import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import lombok.Data;

@Data
public class Preference {

    @Id
    public String key;
    public String value;

    public Preference(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static <T> void set(String key, T value) {
        if (value == null) return;
        PreferenceDao dao = CommonGreenDaoHandler.session.getPreferenceDao();
        Preference preference = new Preference(key, JSON.stringfy(value));
        dao.insertOrReplace(preference);
    }

    public static <T> T get(String key, Class<T> clazz) {
        PreferenceDao dao = CommonGreenDaoHandler.session.getPreferenceDao();
        List<Preference> records = dao.queryRaw("where key = ?", key);
        return records.isEmpty() ? null : JSON.parse(records.get(0).value, clazz);
    }

    public static <T> T get(String key, Class<T> clazz, T defaultValue) {
        PreferenceDao dao = CommonGreenDaoHandler.session.getPreferenceDao();
        List<Preference> records = dao.queryRaw("where key = ?", key);
        if (records.isEmpty()) {
            Preference.set(key, defaultValue);
            return defaultValue;
        }
        return JSON.parse(records.get(0).value, clazz);
    }


}


