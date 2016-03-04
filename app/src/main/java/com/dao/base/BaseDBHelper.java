package com.dao.base;

import com.dao.generate.DaoSession;
import com.dao.inter.AsyncDBInterface;
import com.dao.inter.SyncDBInterface;
import com.dao.listener.DBOperationListener;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;

/**
 * Created by cwj on 16/2/16.
 * Dao的处理基类,提供基础的增删改查方法
 * 可以直接new使用,如果没用特殊方法需要定义时
 */
public class BaseDBHelper<T, K> implements SyncDBInterface<T, K>, AsyncDBInterface<T> {

    protected AbstractDao<T, K> dao;
    protected Class<T> clazz;

    public BaseDBHelper(AbstractDao<T, K> dao, Class<T> clazz) {
        this.dao = dao;
        this.clazz = clazz;
    }

    /**
     * 获得到单例的session对象
     */
    protected static DaoSession getDaoSession() {
        return DaoManager.getDaoSession();
    }

    @Override
    public void insertDataAsync(List<T> data, DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.insertInTx(clazz, data);
    }

    @Override
    public void deleteDataAsync(List<T> data, DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.deleteInTx(clazz, data);
    }

    @Override
    public void deleteAllAsync(DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.deleteAll(clazz);
    }

    @Override
    public void updateDataAsync(List<T> data, DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.updateInTx(clazz, data);
    }

    @Override
    public void findAllAsync(DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.loadAll(clazz);
    }

    @Override
    public void findByQueryAsync(Query<T> query, DBOperationListener<T> listener) {
        AsyncSession asyncSession = getDaoSession().startAsyncSession();
        setListener(asyncSession, listener);
        asyncSession.queryList(query);
    }

    @SuppressWarnings("unchecked")
    private void setListener(AsyncSession asyncSession, final DBOperationListener<T> listener) {
        asyncSession.setListenerMainThread(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                List<T> result;
                try {
                    result = (List<T>) operation.getResult();//只获取正确的List集合
                } catch (Exception e) {
                    result = null;
                }
                if (listener != null)
                    listener.onGetResult(result);
            }
        });
    }

    @Override
    public void insertData(T data) {
        dao.insert(data);
    }

    @Override
    public void insertData(List<T> data) {
        dao.insertInTx(data);
    }

    @Override
    public void deleteData(T data) {
        dao.delete(data);
    }

    @Override
    public void deleteData(List<T> data) {
        dao.deleteInTx(data);
    }

    @Override
    public void deleteAll() {
        dao.deleteAll();
    }

    @Override
    public void updateData(T data) {
        dao.update(data);
    }

    @Override
    public void updateData(List<T> data) {
        dao.updateInTx(data);
    }

    @Override
    public T find(K key) {
        return dao.load(key);
    }

    @Override
    public List<T> findAll() {
        return dao.loadAll();
    }

    @Override
    public long getCount() {
        return dao.count();
    }

    @Override
    public boolean isEmpty() {
        return getCount() <= 0;
    }

}
