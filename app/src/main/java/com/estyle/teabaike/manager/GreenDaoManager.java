package com.estyle.teabaike.manager;

import android.content.Context;

import com.estyle.teabaike.bean.CollectionBean;
import com.estyle.teabaike.bean.CollectionBeanDao;
import com.estyle.teabaike.bean.ContentBean;
import com.estyle.teabaike.bean.DaoMaster;
import com.estyle.teabaike.bean.DaoSession;
import com.estyle.teabaike.bean.TempCollectionBean;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.List;

public class GreenDaoManager {

    public static final String DB_NAME = "tea_baike.db";

    private DaoSession daoSession;


    public GreenDaoManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    // 收藏文章
    public void collectData(ContentBean.DataBean data) {
        CollectionBeanDao collectionDao = daoSession.getCollectionBeanDao();
        Query<CollectionBean> query = collectionDao.queryBuilder()
                .where(CollectionBeanDao.Properties.Id.eq(data.getId()))
                .build();
        if (query.unique() == null) {
            CollectionBean collection = new CollectionBean(Long.parseLong(data.getId()),
                    System.currentTimeMillis(),
                    data.getTitle(),
                    data.getSource(),
                    data.getCreate_time(),
                    data.getAuthor());
            collectionDao.insert(collection);
        }
    }

    // 查询收藏的全部文章
    public List<CollectionBean> queryCollectionDatas() {
        return daoSession
                .getCollectionBeanDao()
                .queryBuilder()
                .orderDesc(CollectionBeanDao.Properties.CurrentTimeMillis)
                .build()
                .list();
    }

    // 删除收藏的文章
    public void deleteCollectionData(List<TempCollectionBean> tempList) {
        for (TempCollectionBean tempCollection : tempList) {
            daoSession.getCollectionBeanDao().delete(tempCollection.getCollection());
        }
        tempList.clear();
    }

}
