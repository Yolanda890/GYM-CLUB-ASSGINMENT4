package com.example.asus.handbook.userdefined;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context,String name, CursorFactory factory,int version){
        super(context, name, factory, version);
    }

    @Override
    //首次创建数据库的时候调用，一般可以执行建库，建表的操作
    //Sqlite没有单独的布尔存储类型，它使用INTEGER作为存储类型，0为false，1为true
    public void onCreate(SQLiteDatabase db){
        //user table
        db.execSQL("create table if not exists user(username integer primary key autoincrement," +
                "image BLOB)"

        );//用户
        db.execSQL("create table if not exists community(content text," +
                "image BLOB,"+
                "image1 BLOB,"+
                "imageURL text,"+
                "username text)");//社区
        db.execSQL("create table if not exists coach(coachname text," +
                "coachimage BLOB,"+
                "coachtype text,"+
                "coachnumber text,"+
                "coachemail text)"
                );//教练
        db.execSQL("create table if not exists course(coursename text," +
                "coursetype text,"+
                "courseimage BOLB,"+
                "coachname text,"+
                "firstimage BOLB)");//课程
        db.execSQL("create table if not exists sc(username text," +
                "coursename text)");//选课

    }

    @Override//当数据库的版本发生变化时，会自动执行
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

}
