package com.iilu.fendou.dbs.liteorm;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

public class LiteOrmDB {

    private static LiteOrm liteOrm;

    public static void createDb(Context context, String dbName) {
        DataBaseConfig config = new DataBaseConfig(context);
        // 数据库名，可设置存储路径。默认在内部存储位置databases文件夹下
        config.dbName =  dbName.toString().trim();
        config.debugged = true; // 是否打Log
        config.dbVersion = 1; // database Version
        config.onUpdateListener = null; // 升级

        liteOrm = LiteOrm.newCascadeInstance(config);
    }

    public static LiteOrm getLiteOrm() {
        return liteOrm;
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    public static <T> void insert(T t) {
        liteOrm.save(t);
    }

    /**
     * 插入所有记录
     *
     * @param list
     */
    public static <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public static <T> List<T> queryAll(Class<T> cla) {
        return liteOrm.query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    public static <T> List<T> queryByWhere(Class<T> cla, String field, Object[] value) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 查询 按照降序排列查询某几条记录
     *
     * @param cla
     * @param field
     * @param start
     * @param length
     * @return
     */
    public static <T> List<T> queryByDescOrder(Class<T> cla, String field, int start, int length) {
        return liteOrm.<T>query(new QueryBuilder(cla).appendOrderDescBy(field).limit(start, length));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public static <T> List<T> queryByWhereLength(Class<T> cla, String field, Object[] value, int start, int length) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 仅在以存在时更新
     */
    public static <T> void update(T t) {
        liteOrm.save(t);
    }


    public static <T> void updateALL(List<T> list) {
        liteOrm.update(list);
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     * @param cla
     * @param field
     * @param value
     */
    public static <T> void deleteWhere(Class<T> cla, String field, Object[] value) {
        liteOrm.delete(cla, WhereBuilder.create(cla).where(field + "=?", value));
    }

    /**
     * 删除所有
     *
     * @param cla
     */
    public static <T> void deleteAll(Class<T> cla) {
        liteOrm.deleteAll(cla);
    }
}
