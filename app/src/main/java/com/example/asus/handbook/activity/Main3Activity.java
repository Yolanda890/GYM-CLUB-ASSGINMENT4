package com.example.asus.handbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.adapter.MyAdapter;
import com.example.asus.handbook.dataobject.Course;
import com.example.asus.handbook.dataobject.SC;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Main3Activity extends AppCompatActivity {
    //users
    private TextView textDay,textMonthYear,myText;
    private BottomNavigationView mNavigationView;
    private RecyclerView view_myCourse;
    private MyAdapter mAdapter;
    private static String currentusername;
    private List<String> values1,values2;
    private ImageButton searchButton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        //隐藏软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        currentusername = getIntent().getStringExtra("username");


        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                            switch (item.getItemId()) {
                                case R.id.main:
                                    break;
                                case R.id.community:
                                    intent=new Intent(Main3Activity.this, CommunityActivity.class);
                                    intent.putExtra("username",currentusername);
                                    startActivity(intent);
                                    break;
                                case R.id.find:
                                    intent=new Intent(Main3Activity.this, SearchingActivity.class);
                                    intent.putExtra("username",currentusername);
                                    startActivity(intent);
                                    break;
                                case R.id.me:
                                    intent=new Intent(Main3Activity.this, MeActivity.class);
                                    intent.putExtra("username",currentusername);
                                    startActivity(intent);
                                    break;
                            }
                    return true;}
                });
        mNavigationView.setSelectedItemId(R.id.add);//根据具体情况调用

        textDay=findViewById(R.id.textDay);
        String day = String.format("%te", System.currentTimeMillis());
        textDay.setText(day);
        textMonthYear=findViewById(R.id.textMonthYear);
        String month = String.format( Locale.US, "%tb", System.currentTimeMillis() );
        String year1 = String.format("%tC", System.currentTimeMillis());
        String year2 = String.format("%ty", System.currentTimeMillis());
        textMonthYear.setText(month+"."+year1+year2);

        values1 = new ArrayList<>();
        values2 = new ArrayList<>();

        myText = findViewById(R.id.textView4);

        view_myCourse = findViewById(R.id.view_myCourse);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);// 将“我的课程”列表排列置为横向。
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view_myCourse.setLayoutManager(linearLayoutManager);
        view_myCourse.setItemAnimator(new DefaultItemAnimator());
        BmobQuery<SC> query = new BmobQuery<>();
        query.addWhereEqualTo("username",currentusername);
        query.findObjects(new FindListener<SC>() {
            @Override
            public void done(List<SC> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < list.size();i++){
                        values1.add(list.get(i).getCoursename());
                    }
                }
                else{
                    System.out.println("error");
                    Toast ts = Toast.makeText(Main3Activity.this,getResources().getString(getResources().getIdentifier("stringShowMCFail", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show() ;
                }
            }
        });
        BmobQuery<Course> query2 = new BmobQuery<>();
        query2.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if(e == null){
                    for(int i = 0;i < values1.size();i++){
                        for(int j = 0;j < list.size();j++){
                            if(list.get(j).getName().equalsIgnoreCase(values1.get(i))){
                                values2.add(list.get(j).getImage());
                                break;
                            }
                        }
                    }
                    if(values1.size() != 0 && values2.size() != 0){
                        ViewGroup.LayoutParams layoutParams = myText.getLayoutParams();
                        layoutParams.height = 0;
                        myText.setLayoutParams(layoutParams);
                        /* 加两个参数 */
                        mAdapter = new MyAdapter("课程",currentusername,values1,values2, R.layout.layout_mycoursecard, Main3Activity.this);
                        view_myCourse.setAdapter(mAdapter);
                    }

                }
                else{
                    System.out.println("error");
                    Toast ts = Toast.makeText(Main3Activity.this,getResources().getString(getResources().getIdentifier("stringShowMCFail", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show() ;
                }
            }
        });


        searchButton = findViewById(R.id.imageButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchInput = editText.getText().toString();
                if(searchInput.length() == 0){
                    Toast ts = Toast.makeText(Main3Activity.this,getString(getResources().getIdentifier("stringSearchNull", "string", getPackageName())), Toast.LENGTH_LONG);
                    ts.show();
                    return;
                }
                Intent intent = new Intent(Main3Activity.this, SearchResultActivity.class);

                /* 加1个传入参数 */
                intent.putExtra("username",currentusername);

                intent.putExtra("searchType", "所有");
                intent.putExtra("searchInput", searchInput);
                startActivity(intent);
            }
        });
        editText = findViewById(R.id.editText);

    }
    public void openMap(View view) {
        // 跳转
        Intent intent=new Intent(Main3Activity.this, MapActivity.class);
        intent.putExtra("username",currentusername);
        startActivity(intent);
    }

}

