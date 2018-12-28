package com.example.asus.handbook.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.adapter.CommunityAdapter;
import com.example.asus.handbook.dataobject.Community;
import com.example.asus.handbook.dataobject.MyUser;
import com.example.asus.handbook.userdefined.DBOpenHelper;
import com.example.asus.handbook.userdefined.ImageManage;
import com.example.asus.handbook.userdefined.NetWorkUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class CommunityActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private RecyclerView view_community;
    private CommunityAdapter cAdapter;
    private List<String> values1;
    private List<String> values2;
    private List<String> values3;
    private List<String> values6;
    private List<byte []> values4;
    private List<byte []> values5;
    private SwipeRefreshLayout refreshLayout;
    private static String currentusername;
    private int symbol=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        currentusername = getIntent().getStringExtra("username");

        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.main:
                                Intent intent = new Intent(CommunityActivity.this, Main3Activity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.community:

                                break;
                            case R.id.find:
                                intent=new Intent(CommunityActivity.this, SearchingActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.me:
                                intent=new Intent(CommunityActivity.this, MeActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;


                        }
                        return true;
                    }
                });
        mNavigationView.setSelectedItemId(R.id.community);//根据具体情况调用
        //底部导航栏有几项就有几个Fragment

        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values3 = new ArrayList<>();
        values4 = new ArrayList<>();
        values5 = new ArrayList<>();
        values6 = new ArrayList<>();
        view_community = findViewById(R.id.view_community);
        view_community.setLayoutManager(new LinearLayoutManager(this));
        view_community.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        view_community.setLayoutManager(manager);


        refreshLayout = findViewById(R.id.refresh_community);
        refreshLayout.setColorSchemeResources(R.color.orange,R.color.blue,R.color.green);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                refreshLayout.setRefreshing(false);
            }
        });
        refresh();
    }

    private void refresh(){
        values1.clear();
        values2.clear();
        values3.clear();

        final ImageManage imageManage=new  ImageManage();
        DBOpenHelper helper = new DBOpenHelper(CommunityActivity.this,currentusername+"_2.db",null,1);
        final SQLiteDatabase db = helper.getWritableDatabase();


        NetWorkUtil netWorkUtil = new NetWorkUtil();
        boolean judge= netWorkUtil.isNetworkAvailable(CommunityActivity.this);
        if(judge) {
            BmobQuery<Community> query = new BmobQuery<>();
            Date curdate = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            ft.format(curdate);
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(curdate));
            query.findObjects(new FindListener<Community>() {
                @Override
                public void done(List<Community> list, BmobException e) {
                    if (e == null) {

                        for (int i = list.size() - 1; i >= 0; i--) {
                            values1.add(list.get(i).getStatecontent());
                            values2.add(list.get(i).getStateimage());
                            values3.add(list.get(i).getUsername());
                            Cursor c = db.query("community", new String[]{"content","imageURL","username"}, "content=? and username=? and imageURL=?", new String[]{list.get(i).getStatecontent(), list.get(i).getUsername(),list.get(i).getStateimage()}, null, null, null);
                            if (c.getCount()==0) {
                                //insert data
                                ContentValues values = new ContentValues();
                                values.put("content",list.get(i).getStatecontent());
                                values.put("image", imageManage.bitmabToBytes(list.get(i).getStateimage()));//图片转为二进制
                                values.put("username", list.get(i).getUsername());
                                values.put("imageURL", list.get(i).getStateimage());
                               // values.put("image1", imageManage.bitmabToBytes(list.get(j).getFigureimage()));

                                long rowid = db.insert("community",null,values);

                            }    c.close();

                        }

                    }
                }
            });
            BmobQuery<MyUser> query2 = new BmobQuery<>();
            query2.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < values3.size(); i++) {
                               values6.add(values3.get(i));
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getUsername().equalsIgnoreCase(values3.get(i))) {
                                    values3.set(i, list.get(j).getFigureimage());

                                    break;
                                }

                            }
                        }

                    }
                    db.close();
                    view_community.removeAllViews();
                    cAdapter = new CommunityAdapter(values1, values2, values3, values4, values5, R.layout.layout_communitycard, CommunityActivity.this, symbol);
                    view_community.setAdapter(cAdapter);
                }
            });


        }
        else{
            symbol = 1;
            int o = 0;
            Cursor c=null;
            while(true) {


                    c = db.query("community", new String[]{"content", "username", "image"}, null, null, null, null, null, String.valueOf(o) + ",1");
                    o++;
                    if (c.moveToNext()&&o<5) {
                        String content = c.getString(c.getColumnIndex("content"));
                        String username = c.getString(c.getColumnIndex("username"));

                        values1.add(content);
                        byte[] imgData,imgData1;
                        //将Blob数据转化为字节数组
                        imgData = c.getBlob(c.getColumnIndex("image"));

                        values4.add(imgData);

                        values3.add(username);
                    }
                    else {
                        break;
                    }
                c.close();
            }
/*
            o=0;
            while(true) {


                c = db.query("community", new String[]{"content", "username", "image"}, null, null, null, null, null, String.valueOf(o) + ",1");
                o++;
                if (c.moveToNext()&&o<5) {

                byte[] imgData1;
                    //将Blob数据转化为字节数组

                 imgData1 = c.getBlob(c.getColumnIndex("image1"));

                    values5.add(imgData1);

                } else {
                    break;
                }
                c.close();
            }
*/
            db.close();
            view_community.removeAllViews();
            cAdapter = new CommunityAdapter(values1, values2, values3, values4, values5, R.layout.layout_communitycard, CommunityActivity.this, symbol);
            view_community.setAdapter(cAdapter);
        }

    }



}
