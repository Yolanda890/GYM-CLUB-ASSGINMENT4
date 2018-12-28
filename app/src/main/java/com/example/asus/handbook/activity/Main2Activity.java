package com.example.asus.handbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.dataobject.MyUser;
import com.example.asus.handbook.userdefined.DBOpenHelper;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Main2Activity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btn=findViewById(R.id.button4);
        btn.setOnClickListener(listener);
        Button btn1=findViewById(R.id.button5);
        btn1.setOnClickListener(listener1);
    }
    //实现监听器1：按键1，跳到第2张
    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
            Main2Activity.this.finish();
        }
    };
    //实现监听器1：按键1，跳到第2张
    Button.OnClickListener listener1 = new Button.OnClickListener() {
        public void onClick(View v) {
            EditText email1 = (EditText)findViewById(R.id.textInputEditText);
            email=email1.getText().toString();
            EditText password3= (EditText)findViewById(R.id.editText);
            String password1=password3.getText().toString();

            if (email.length() == 0||password1.length() == 0) {
                //弹出提示
                Toast.makeText(getApplicationContext(), "请填写全部内容", Toast.LENGTH_SHORT).show();
            }
            else {
                MyUser bu2 = new MyUser();

                bu2.setUsername(email);

                bu2.setPassword(password1);
                bu2.login(new SaveListener<MyUser>() {

                    @Override
                    public void done(MyUser user, BmobException e) {
                        if(e==null){
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                           //DBOpenHelper helper = new DBOpenHelper(Main2Activity.this,"me.db",null,1);
                          //  SQLiteDatabase db=new SQLiteDatabase("me.db");
                           //  helper.onCreate(db);
                          //  final SQLiteDatabase db = helper.getWritableDatabase();
                            //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                            //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                            Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
                            intent.putExtra("username",email);
                            startActivity(intent);
                            startActivity(intent);
                            Main2Activity.this.finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }
    };

}
