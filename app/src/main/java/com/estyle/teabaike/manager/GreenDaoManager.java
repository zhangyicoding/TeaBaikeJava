package com.estyle.teabaike.manager;

import android.content.Context;

import com.estyle.teabaike.bean.ContentDataBean;
import com.estyle.teabaike.bean.ContentDataBeanDao;
import com.estyle.teabaike.bean.DaoMaster;
import com.estyle.teabaike.bean.DaoSession;
import com.estyle.teabaike.bean.TempCollectionBean;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.List;

public class GreenDaoManager {

    private static final String DB_NAME = "tea_baike.db";

    private DaoSession mDaoSession;

    public GreenDaoManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    // 收藏文章
    public void collectData(ContentDataBean data) {
        ContentDataBeanDao dao = mDaoSession.getContentDataBeanDao();
        Query<ContentDataBean> query = dao.queryBuilder()
                .where(ContentDataBeanDao.Properties.Id.eq(data.getId()))
                .build();
        if (query.unique() == null) {
            data.setCurrentTimeMillis(System.currentTimeMillis());
            dao.insert(data);
        }
    }

    // 查询收藏的全部文章
    public List<ContentDataBean> queryCollectionDatas() {
        return mDaoSession
                .getContentDataBeanDao()
                .queryBuilder()
                .orderDesc(ContentDataBeanDao.Properties.CurrentTimeMillis)
                .build()
                .list();
    }

    //    // 通过id查询收藏的文章
    public ContentDataBean queryCollectionDataById(long id) {
        return mDaoSession
                .getContentDataBeanDao()
                .queryBuilder()
                .where(ContentDataBeanDao.Properties.Id.eq(id))
                .build()
                .unique();
    }

    // 删除收藏的文章
    public void deleteCollectionData(List<TempCollectionBean> tempList) {
        for (TempCollectionBean tempCollection : tempList) {
            mDaoSession.getContentDataBeanDao().delete(tempCollection.getCollection());
        }
        tempList.clear();
    }

}
