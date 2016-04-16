package com.dao.dbHelpers;

import android.text.TextUtils;

import com.dao.base.BaseDBHelper;
import com.dao.generate.SearchRecord;
import com.dao.generate.SearchRecordDao;
import com.dao.listener.DBOperationListener;
import com.google.inject.Singleton;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by cwj on 16/4/16.
 * 搜索记录
 */
@Singleton
public class SearchRecordHelper extends BaseDBHelper<SearchRecord, Long> {

    public SearchRecordHelper() {
        super(getDaoSession().getSearchRecordDao(), SearchRecord.class);
    }

    /**
     * 获取最近使用的搜索记录
     */
    public void getRecentlyUsedAsync(DBOperationListener<SearchRecord> listener) {
        QueryBuilder<SearchRecord> builder = dao.queryBuilder();
        builder.orderDesc(SearchRecordDao.Properties.LastUseTime);//时间从最近到之前
        findByQueryAsync(builder.build(), listener);
    }

    /**
     * 添加搜索记录(有重复(单空格连接相等)的则更新最近使用时间)
     */
    public void addRecentlyUsed(String name) {
        String realName = getRealName(name);
        if (TextUtils.isEmpty(realName))//无搜索内容不添加
            return;
        QueryBuilder<SearchRecord> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SearchRecordDao.Properties.Name.eq(realName));
        List<SearchRecord> list = findByQuery(queryBuilder);
        if (list == null || list.size() <= 0) {//没有过记录,增加一条
            SearchRecord searchRecord = new SearchRecord(realName, System.currentTimeMillis());
            insertData(searchRecord);
        } else {//更新原有记录最近使用时间
            SearchRecord searchRecord = list.get(0);
            searchRecord.setLastUseTime(System.currentTimeMillis());
            updateData(searchRecord);
        }
    }

    private String getRealName(String name) {
        StringBuilder sb = new StringBuilder("");
        for (String str : name.split(" ")) {
            if (!TextUtils.isEmpty(str)) {
                sb.append(str).append(" ");
            }
        }
        return sb.toString();
    }
}
