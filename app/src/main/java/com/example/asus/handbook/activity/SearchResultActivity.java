package com.example.asus.handbook.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.adapter.SearchAdapter;
import com.example.asus.handbook.dataobject.Coach;
import com.example.asus.handbook.dataobject.Course;
import com.example.asus.handbook.userdefined.DBOpenHelper;
import com.example.asus.handbook.userdefined.ImageManage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchResultActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private RecyclerView view_course;
    private SearchAdapter sAdapter;
    private String type;
    private List<String> values1;
    private List<String> values2;
    private List<byte[]> values3;
    private ImageButton searchButton;
    private Spinner spinner;
    private EditText editText;
    private SwipeRefreshLayout refreshLayout;
    private static String currentusername;
    private int symbol=0;
    private ImageManage imageManage ;
    DBOpenHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //隐藏软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        currentusername = getIntent().getStringExtra("username");

        /* 设置底部菜单栏 */
        navigationView = findViewById(R.id.navigation);
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(2).setChecked(true);

       imageManage=new  ImageManage();
       helper = new DBOpenHelper(SearchResultActivity .this,"me.db",null,1);
       db = helper.getWritableDatabase();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch(item.getItemId()) {
                            case R.id.main:
                                intent=new Intent(SearchResultActivity.this, Main3Activity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.community:
                                intent=new Intent(SearchResultActivity.this, CommunityActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.find:
                                intent=new Intent(SearchResultActivity.this, SearchingActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.me:
                                intent=new Intent(SearchResultActivity.this, MeActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                }
        );

        //获取搜索内容
        Intent intent = getIntent();
        String searchType = intent.getStringExtra("searchType");
        String searchInput = intent.getStringExtra("searchInput");

        view_course = findViewById(R.id.view_course);
        view_course.setLayoutManager(new LinearLayoutManager(this));
        view_course.setItemAnimator(new DefaultItemAnimator());

        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values3 = new ArrayList<>();

        switchType(searchType,searchInput);

        searchButton = findViewById(R.id.imageButton);
        spinner = findViewById(R.id.spinner);
        editText = findViewById(R.id.editText);
        editText.setText(searchInput);
        if(searchType.equalsIgnoreCase("所有")){
            spinner.setSelection(0,true);
        }
        else if(searchType.equalsIgnoreCase("教程")){
            spinner.setSelection(1,true);
        }
        else if(searchType.equalsIgnoreCase("教练")){
            spinner.setSelection(2,true);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchType = spinner.getSelectedItem().toString();
                String searchInput = editText.getText().toString();
                if(searchInput.length() == 0){
                    Toast ts = Toast.makeText(SearchResultActivity.this,getString(getResources().getIdentifier("stringSearchingNull", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show();
                    return;
                }
                values1.clear();
                values2.clear();
                switchType(searchType,searchInput);
            }

        });

        refreshLayout = findViewById(R.id.refresh_search_result);
        refreshLayout.setColorSchemeResources(R.color.orange,R.color.blue,R.color.green);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String searchType = spinner.getSelectedItem().toString();
                String searchInput = editText.getText().toString();
                values1.clear();
                values2.clear();
                switchType(searchType,searchInput);
                refreshLayout.setRefreshing(false);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    private void switchType(String searchType,String searchInput){
        if(searchType.equalsIgnoreCase("所有")){
            searchAll(searchInput);
        }
        else if(searchType.equalsIgnoreCase("课程")){
            searchCourse(searchInput);
        }
        else if(searchType.equalsIgnoreCase("教练")){
            searchCoach(searchInput);
        }
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void searchCourse(final String searchInput){

        BmobQuery<Course> query=new BmobQuery<Course>();
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < list.size();i++){
                        if(searchInput.contains("课程")|| list.get(i).getName().contains(searchInput) || list.get(i).getType().contains(searchInput)){
                            values1.add(list.get(i).getName());
                            values2.add(list.get(i).getImage());
                        }
                    }

                }
                else{
                    symbol=1;
                    Cursor c=db.query("course",new String[]{"coursename","courseimage","coursetype"},null,null,null,null,null);
                    while(!c.moveToNext()) {
                        String coachname = c.getString(c.getColumnIndex("coursename"));
                        String coachtype = c.getString(c.getColumnIndex("coursetype"));
                        if (searchInput.contains("课程") || coachname.contains(searchInput) || coachtype.contains(searchInput)) {


                            values1.add(coachname);
                            byte[] imgData = null;
                            //将Blob数据转化为字节数组
                            imgData = c.getBlob(c.getColumnIndex("courseimage"));
                            values3.add(imgData);
                        }
                        c.close();
                    }db.close();
                }
                if(values1.size()!=0){
                    view_course.removeAllViews();

                    /* 加参数 */
                    sAdapter = new SearchAdapter("课程",currentusername,values1,values2,values3, R.layout.layout_coursecard, SearchResultActivity.this,symbol);
                    view_course.setAdapter(sAdapter);
                }
                else{
                    System.out.println("error");
                    view_course.removeAllViews();
                    Toast ts = Toast.makeText(SearchResultActivity.this,getResources().getString(getResources().getIdentifier("stringSearchingFail", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show() ;
                }

            }
        });
    }

    private void searchCoach(final String searchInput){
        BmobQuery<Coach> query=new BmobQuery<Coach>();
        query.findObjects(new FindListener<Coach>() {
            @Override
            public void done(List<Coach> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < list.size();i++){
                        if(searchInput.contains("教练") || list.get(i).getCName().contains(searchInput)){
                            values1.add(list.get(i).getCName());
                            values2.add(list.get(i).getImage());
                        }
                    }

                }
                else {

                        symbol = 1;
                        Cursor c = db.query("coach", new String[]{"coachname", "coachimage"}, null, null, null, null, null);
                        while (!c.moveToNext()) {
                            String coachname = c.getString(c.getColumnIndex("coachname"));
                            if (searchInput.contains("教练") ||coachname.contains(searchInput)) {


                                values1.add(coachname);
                                byte[] imgData = null;
                                //将Blob数据转化为字节数组
                                imgData = c.getBlob(c.getColumnIndex("coachimage"));
                                values3.add(imgData);
                            }
                        }
                        c.close();
                        db.close();

                }
                if(values1.size()!=0){
                    view_course.removeAllViews();

                    /* 加参数 */
                    sAdapter = new SearchAdapter("教练",currentusername,values1,values2,values3, R.layout.layout_coursecard, SearchResultActivity.this,symbol);
                    view_course.setAdapter(sAdapter);
                }
                else{
                    System.out.println("error");
                    view_course.removeAllViews();
                    Toast ts = Toast.makeText(SearchResultActivity.this,getResources().getString(getResources().getIdentifier("stringSearchingFail", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show() ;
                }

            }
        });

    }

    private void searchAll(String searchInput){
        final String s_i = searchInput;
        BmobQuery<Course> query=new BmobQuery<Course>();
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < list.size();i++){
                        if(s_i.contains("所有") || s_i.contains("课程")|| list.get(i).getName().contains(s_i)|| list.get(i).getType().contains(s_i)){
                            values1.add(list.get(i).getName());
                            values2.add(list.get(i).getImage());
                        }
                    }

                }
                else{
                    symbol=1;
                    Cursor c=db.query("course",new String[]{"coursename","courseimage","coursetype"},null,null,null,null,null);
                    while(!c.moveToNext()) {
                        String coachname = c.getString(c.getColumnIndex("coursename"));
                        String coachtype = c.getString(c.getColumnIndex("coursetype"));
                        if(s_i.contains("所有") || s_i.contains("课程")|| coachname.contains(s_i)|| coachtype.contains(s_i)){


                            values1.add(coachname);
                            byte[] imgData = null;
                            //将Blob数据转化为字节数组
                            imgData = c.getBlob(c.getColumnIndex("courseimage"));
                            values3.add(imgData);
                        }
                        c.close();
                    }db.close();
                }
                if(values1.size()!=0){
                    view_course.removeAllViews();

                    /* 加参数 */
                    sAdapter = new SearchAdapter("课程",currentusername,values1,values2,values3, R.layout.layout_coursecard, SearchResultActivity.this,symbol);
                    view_course.setAdapter(sAdapter);
                }
                else{
                    System.out.println("course not found");
                    view_course.removeAllViews();
                }
            }
        });

        BmobQuery<Coach> query2=new BmobQuery<Coach>();
        query2.findObjects(new FindListener<Coach>() {
            @Override
            public void done(List<Coach> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < list.size();i++){
                        if(s_i.contains("所有") || s_i.contains("教练") || list.get(i).getCName().contains(s_i)){
                            values1.add(list.get(i).getCName());
                            values2.add(list.get(i).getImage());
                        }
                    }
                }
                else{
                    symbol = 1;
                    Cursor c = db.query("coach", new String[]{"coachname", "coachimage"}, null, null, null, null, null);
                    while (!c.moveToNext()) {
                        String coachname = c.getString(c.getColumnIndex("coachname"));
                        if(s_i.contains("所有") || s_i.contains("教练") || coachname.contains(s_i)){


                            values1.add(coachname);
                            byte[] imgData = null;
                            //将Blob数据转化为字节数组
                            imgData = c.getBlob(c.getColumnIndex("coachimage"));
                            values3.add(imgData);
                        }
                    }
                    c.close();
                    db.close();


                }
                if(values1.size()!=0){
                    view_course.removeAllViews();

                    /* 加参数 */
                    sAdapter = new SearchAdapter("教练",currentusername,values1,values2,values3, R.layout.layout_coursecard, SearchResultActivity.this,symbol);
                    view_course.setAdapter(sAdapter);
                }
                else{
                    System.out.println("coach not found");
                }

            }
        });
    }

}
