package com.example.asus.handbook.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.adapter.MyAdapter;
import com.example.asus.handbook.dataobject.Coach;
import com.example.asus.handbook.dataobject.Course;
import com.example.asus.handbook.userdefined.DBOpenHelper;
import com.example.asus.handbook.userdefined.ImageManage;
import com.example.asus.handbook.userdefined.NetWorkUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CoachActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private RecyclerView view_teaching;
    private MyAdapter tAdapter;
    private static String currentusername;
    private List<String> values1,values2,values3,values4;
    private List<byte[]> values5;
    private ImageView coachFigure;
    private TextView coachName, coachType;
    private static String coachname;
    private int symbol=0;

    private TelephonyManager mTelephonyManager;
    ImageManage imageManage=null;
    DBOpenHelper helper;
    SQLiteDatabase db=null;
    NetWorkUtil netWorkUtil = new NetWorkUtil();
    boolean judge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        currentusername = getIntent().getStringExtra("username");
        coachname = getIntent().getStringExtra("coachname");

        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigationView.setSelectedItemId(R.id.find);//根据具体情况调用
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.main:
                                Intent intent = new Intent(CoachActivity.this, Main3Activity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.community:
                                intent=new Intent(CoachActivity.this, CommunityActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.find:
                                intent=new Intent(CoachActivity.this, SearchingActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.me:
                                intent=new Intent(CoachActivity.this, MeActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });

        coachFigure = findViewById(R.id.imageView);
        coachName = findViewById(R.id.textView);
        coachType = findViewById(R.id.textView2);
        imageManage=new  ImageManage();
        helper = new DBOpenHelper(CoachActivity.this,currentusername +"_2.db",null,1);
        db = helper.getWritableDatabase();
        judge = netWorkUtil.isNetworkAvailable(CoachActivity.this);
        if(judge) {

            BmobQuery<Coach> query = new BmobQuery<Coach>();
            query.addWhereEqualTo("coachname", this.coachname);
            query.findObjects(new FindListener<Coach>() {
                @Override
                public void done(List<Coach> list, BmobException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            Picasso.with(CoachActivity.this).load(list.get(0).getImage()).into(coachFigure);
                            coachName.setText(list.get(0).getCName());
                            coachType.setText(list.get(0).getCoachtype());
                        } else {
                            System.out.println("error");
                            Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("showCoachFail", "string", getPackageName())), Toast.LENGTH_LONG);
                            ts.show();


                        }
                    }
                }
            });
        }else {
            Cursor c = db.query("coach", new String[]{"coachname", "coachtype", "coachimage"}, "coachname=?", new String[]{coachname}, null, null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex("coachname"));
                    coachName.setText(name);
                    String type = c.getString(c.getColumnIndex("coachtype"));
                    coachType.setText(type);

                    byte[] imgData = null;
                    //将Blob数据转化为字节数组
                    imgData = c.getBlob(c.getColumnIndex("coachimage"));
                    ImageManage manage = new ImageManage();
                    Bitmap bitmap = manage.getBitmapFromByte(imgData);
                    coachFigure.setImageBitmap(bitmap);
                }

            } else {
                System.out.println("error");
                Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("showCoachFail", "string", getPackageName())), Toast.LENGTH_LONG);
                ts.show();
            }
            c.close();

        }




        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values5 = new ArrayList<>();

        view_teaching = findViewById(R.id.view_teaching);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);// 将“教授课程”列表排列置为横向。
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view_teaching.setLayoutManager(linearLayoutManager);
        view_teaching.setItemAnimator(new DefaultItemAnimator());

        judge = netWorkUtil.isNetworkAvailable(CoachActivity.this);
        if(judge) {
            BmobQuery<Course> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("coachname", coachname);
            query2.findObjects(new FindListener<Course>() {
                @Override
                public void done(List<Course> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            values1.add(list.get(i).getName());
                            values2.add(list.get(i).getImage());


                        }
                        if (values1.size() != 0 && values2.size() != 0) {
                            /* 加两个参数 */
                            tAdapter = new MyAdapter("课程", currentusername, values1, values2, values5, R.layout.layout_mycoursecard, CoachActivity.this, symbol);
                            view_teaching.setAdapter(tAdapter);
                        }
                    }
                }
            });
        }else {
            symbol = 1;
            Cursor c = db.query("course", new String[]{"coursename", "courseimage"}, null, null, null, null, null);
            while (c.moveToNext()) {
                String coachname = c.getString(c.getColumnIndex("coursename"));

                values1.add(coachname);
                byte[] imgData = null;
                //将Blob数据转化为字节数组
                imgData = c.getBlob(c.getColumnIndex("courseimage"));
                values5.add(imgData);
            }
            c.close();

            if (values1.size() != 0 && values5.size() != 0) {
                /* 加两个参数 */
                tAdapter = new MyAdapter("课程", currentusername, values1, values2, values5, R.layout.layout_mycoursecard, CoachActivity.this, symbol);
                view_teaching.setAdapter(tAdapter);
            }
        }

        values3 = new ArrayList<>();
        values4 = new ArrayList<>();
        view_teaching = findViewById(R.id.view_teaching);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);// 将“我的课程”列表排列置为横向。
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        view_teaching.setLayoutManager(linearLayoutManager2);
        view_teaching.setItemAnimator(new DefaultItemAnimator());

        judge = netWorkUtil.isNetworkAvailable(CoachActivity.this);
        if(judge) {
            BmobQuery<Course> query3 = new BmobQuery<>();
            query3.addWhereEqualTo("coachname", this.coachname);
            query3.findObjects(new FindListener<Course>() {
                @Override
                public void done(List<Course> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            values3.add(list.get(i).getName());
                            values4.add(list.get(i).getImage());
                            tAdapter = new MyAdapter("课程", currentusername, values3, values4, values5, R.layout.layout_mycoursecard, CoachActivity.this, symbol);
                            view_teaching.setAdapter(tAdapter);
                        }
                    }
                }
            });
        }
        else {
            symbol = 1;
            Cursor c = db.query("course", new String[]{"coursename", "courseimage"}, null, null, null, null, null);
            while (c.moveToNext()) {
                String coachname = c.getString(c.getColumnIndex("coursename"));

                values3.add(coachname);
                byte[] imgData = null;
                //将Blob数据转化为字节数组
                imgData = c.getBlob(c.getColumnIndex("courseimage"));
                values5.add(imgData);
                tAdapter = new MyAdapter("课程", currentusername, values3, values4, values5, R.layout.layout_mycoursecard, CoachActivity.this, symbol);
                view_teaching.setAdapter(tAdapter);
            }
            c.close();
            db.close();
        }


    }

    // 致电教练
    public void makeCall(View view) {

        BmobQuery<Coach> query=new BmobQuery<Coach>();
        query.addWhereEqualTo("coachname",coachname);
        query.findObjects(new FindListener<Coach>() {
            @Override
            public void done(List<Coach> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        String phoneNo = list.get(0).getCoachnumber();
                        Cursor c = db.query("coach",null,null,null,null,null,null);
                           if(c!=null && c.getCount() >= 1){
                              ContentValues cv = new ContentValues();
                                cv.put("coachnumber", phoneNo);
                                 String[] args = {phoneNo};
                                   long rowid = db.update("coach", cv, "coachnumber=?",args);

                              }
                              c.close();
                              db.close();
                        String dial = "tel:" + phoneNo;
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    } else {
                        Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("getNumberFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show();
                    }
                } else {
                    Cursor c = db.query("coach", new String[]{"coachnumber"}, "coachname=?", new String[]{coachname}, null, null, null);
                    if (c != null) {
                        while (c.moveToNext()) {

                            String phoneNo= c.getString(c.getColumnIndex("coachnumber"));
                            String dial = "tel:" + phoneNo;
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                        }

                    } else {
                        System.out.println("error");
                        Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("getNumberFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show();
                    }
                    c.close();db.close();
                }

            }
        });
        mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
    }

    // 传电邮给教练
    public void sendMail(View view) {
        View d_view = LayoutInflater.from(CoachActivity.this).inflate(R.layout.layout_mailbox, null);
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(CoachActivity.this);
        dialog.setView(d_view);

        final TextView emailAddress = d_view.findViewById(R.id.textView);

        final EditText mailContent = d_view.findViewById(R.id.editText2);
        final EditText mailTitle = d_view.findViewById(R.id.editText);

        BmobQuery<Coach> query=new BmobQuery<Coach>();
        query.addWhereEqualTo("coachname",coachname);
        query.findObjects(new FindListener<Coach>() {
            @Override
            public void done(List<Coach> list, BmobException e) {
                if(e == null){
                    if(list.size() != 0){
                        emailAddress.setText("发送给："+coachName.getText().toString()+"("+list.get(0).getCoachemail()+")");
                        Cursor c = db.query("coach",null,null,null,null,null,null);
                        if(c!=null && c.getCount() >= 1){
                            ContentValues cv = new ContentValues();
                            cv.put("coachemail",list.get(0).getCoachemail());
                            String[] args = {list.get(0).getCoachemail()};
                            long rowid = db.update("coach", cv, "coachnumber=?",args);

                        }
                        c.close();
                        db.close();
                    }
                    else{
                        Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("showMailAddressFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show() ;
                    }
                }
                else{
                    Cursor c = db.query("coach", new String[]{"coachemail"}, "coachname=?", new String[]{coachname}, null, null, null);
                    if (c != null) {
                        while (c.moveToNext()) {

                            String email= c.getString(c.getColumnIndex("coachemail"));
                            emailAddress.setText("发送给："+coachName.getText().toString()+"("+email+")");
                        }

                    } else {
                        System.out.println("error");
                        Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("showMailAddressFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show();
                    }
                    c.close();db.close();
                }

            }
        });

        dialog.setTitle(getResources().getString(getResources().getIdentifier("dialogTitle", "string", getPackageName())));
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        dialog.setPositiveButton("发送邮件",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mailTitle.getText().toString().equalsIgnoreCase("") ||
                                mailContent.getText().toString().equalsIgnoreCase("") ){
                            Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("mailInputNull", "string", getPackageName())), Toast.LENGTH_LONG);
                            ts.show() ;
                        }
                        else{
                            BmobQuery<Coach> query=new BmobQuery<Coach>();
                            query.addWhereEqualTo("coachname",coachname);
                            query.findObjects(new FindListener<Coach>() {
                                @Override
                                public void done(List<Coach> list, BmobException e) {
                                    if(e == null){
                                        if(list.size() != 0){
                                            sendemail(list.get(0).getCoachemail());

                                        }
                                        else{
                                            Cursor c = db.query("coach", new String[]{"coachemail"}, "coachname=?", new String[]{coachname}, null, null, null);
                                            if (c != null) {
                                                String email= c.getString(c.getColumnIndex("coachemail"));
                                                sendemail(email);
                                            }
                                            else {
                                                Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("sendMailFail", "string", getPackageName())), Toast.LENGTH_LONG);
                                                ts.show() ;
                                            }

                                        }
                                    }
                                    else{
                                        System.out.println("error");
                                        Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("sendMailFail", "string", getPackageName())), Toast.LENGTH_LONG);
                                        ts.show() ;
                                    }

                                }
                            });
                        }
                    }
                });

        dialog.show();


    }

    // 传短信给教练
    public void sendMSG(View view) {

        BmobQuery<Coach> query=new BmobQuery<Coach>();
        query.addWhereEqualTo("coachname",coachname);
        query.findObjects(new FindListener<Coach>() {
            @Override
            public void done(List<Coach> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        String phone = list.get(0).getCoachnumber();//电话号码
                        Uri uri = Uri.parse("smsto:+86"+phone);
                        Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intentMessage);
                    } else {
                        Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("getNumberFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show();
                    }
                } else {
                    Cursor c = db.query("coach", new String[]{"coachnumber"}, "coachname=?", new String[]{coachname}, null, null, null);
                    if (c != null) {
                        while (c.moveToNext()) {

                            String phone= c.getString(c.getColumnIndex("coachnumber"));
                            Uri uri = Uri.parse("smsto:+86"+phone);
                            Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intentMessage);
                        }

                    } else {
                        System.out.println("error");
                        Toast ts = Toast.makeText(CoachActivity.this, getResources().getString(getResources().getIdentifier("getNumberFail", "string", getPackageName())), Toast.LENGTH_LONG);
                        ts.show();
                    }
                    c.close();db.close();
                }

            }
        });

    }
    public void sendemail(String to){
        /* 发送邮件 */
        // 收件人电子邮箱

        View d_view = LayoutInflater.from(CoachActivity.this).inflate(R.layout.layout_mailbox, null);
        final EditText mailTitle = d_view.findViewById(R.id.editText);
        final EditText mailContent = d_view.findViewById(R.id.editText2);


        // 发件人电子邮箱
        String from = "yuzhou992999406@163.com";
        ;
        // 获取系统属性
        Properties properties = new Properties();

        // 设置邮件服务器
        properties.setProperty("mail.transport.protocol", "SMTP");
        properties.setProperty("mail.smtp.host", "smtp.163.com");
        properties.setProperty("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.timeout", "1000");

        // 获取默认session对象
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        // 登陆邮件发送服务器的用户名和密码
                        return new PasswordAuthentication("yuzhou992999406@163.com", "abc837477816");
                    }
                });

        session.setDebug(true);

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // 添加抄送
            InternetAddress address = new InternetAddress(from);
            message.setFrom(address);
            message.addRecipient(Message.RecipientType.CC, address);

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject(mailTitle.getText().toString());

            // 设置消息体
            message.setText(mailContent.getText().toString());

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.163.com", 25, from, "abc837477816");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            // 发送成功
            Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("sendMailSuccess", "string", getPackageName())), Toast.LENGTH_LONG);
            ts.show() ;

        }
        catch(Exception e2){
            e2.printStackTrace();
            Toast ts = Toast.makeText(CoachActivity.this,getResources().getString(getResources().getIdentifier("sendMailFail", "string", getPackageName())), Toast.LENGTH_LONG);
            ts.show() ;
        }
    }
}
