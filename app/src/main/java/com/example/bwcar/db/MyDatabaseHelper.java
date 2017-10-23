package com.example.bwcar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bwcar.R;
import com.example.bwcar.util.Utility;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper{

    public static final String CREATE_BUTTON = "create table Button("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"position integer,"
            +"message integer)";

    public static final String NAME_KEY = "name";


    private Context mContext;
    private static SQLiteDatabase mDatabase;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BUTTON);
        Utility.showToast(mContext, mContext.getResources().getString(R.string.create_database_success));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists Button");
        onCreate(sqLiteDatabase);
    }
}
