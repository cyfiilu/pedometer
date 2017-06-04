package com.iilu.fendou.dbs;

import android.content.Context;

public class RootDB {

    protected DBHelper mDBHelper = null;

    public RootDB(Context context) {
        mDBHelper = new DBHelper(context);
    }

}
