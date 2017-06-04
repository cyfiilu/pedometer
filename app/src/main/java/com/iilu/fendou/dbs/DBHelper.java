package com.iilu.fendou.dbs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iilu.fendou.configs.DBConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * SQLite 数据库操作公共类
 *
 * @version 1.1.0
 * @since JDK1.5/Android2.1
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    /**
     * DBHelper
     *
     * @param context 调用Context实例
     */
    public DBHelper(Context context) {
        super(context, DBConfig.DB_NAME, null, DBConfig.DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSchema(db, "schema.sql");
    }

    /**
     * 读取数据库文件（.sql），并执行sql语句
     */
    private void executeSchema(SQLiteDatabase db, String schemaName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(mContext.getAssets().open("schema" + File.separator + schemaName)));
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (buffer.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 数据库升级，每次升级读取相应的升级文件
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库不升级
        if (newVersion <= oldVersion) return;
        int count = newVersion - oldVersion;
        for (int i = 0; i < count; i++) {
            // 依次执行updatei_i+1文件 由1更新到2 [1-2]，2更新到3 [2-3]
            String schemaName = "update" + (oldVersion + i) + "_" + (oldVersion + i + 1) + ".sql";
            executeSchema(db, schemaName);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
