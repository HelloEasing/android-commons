package com.easing.commons.android.greendao;

import com.easing.commons.android.preference.Preference;
import com.easing.commons.android.preference.PreferenceDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;


public class CommonGreenDaoSession extends AbstractDaoSession {

    private final DaoConfig preferenceDaoConfig;

    private final PreferenceDao preferenceDao;

    public CommonGreenDaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        preferenceDaoConfig = daoConfigMap.get(PreferenceDao.class).clone();
        preferenceDaoConfig.initIdentityScope(type);

        preferenceDao = new PreferenceDao(preferenceDaoConfig, this);

        registerDao(Preference.class, preferenceDao);
    }

    public void clear() {
        preferenceDaoConfig.clearIdentityScope();
    }

    public PreferenceDao getPreferenceDao() {
        return preferenceDao;
    }

}
