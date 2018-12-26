package com.example.asus.handbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.dataobject.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "c8f692e7b196b64defe8ddfd16ebe4e7");
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.button4);//跳转到登录
        btn.setOnClickListener(listener);
        Button btn1=(Button)findViewById(R.id.button5);//注册按钮的响应事件
        btn1.setOnClickListener(listener1);



    }
    //实现监听器1：按键1，跳到第2张
    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };
    Button.OnClickListener listener1 = new Button.OnClickListener() {
        public void onClick(View v) {
            //获取EditText实例对象

            EditText email1 = (EditText)findViewById(R.id.textInputEditText);
            String email=email1.getText().toString();
            EditText password3= (EditText)findViewById(R.id.editText);
            String password1=password3.getText().toString();
            EditText password4= (EditText)findViewById(R.id.editText2);
            String password2=password4.getText().toString();
            Switch wlan=(Switch)findViewById(R.id.switch1);

            if (email.length() == 0||password1.length() == 0||password2.length() == 0) {
                //弹出提示
                Toast.makeText(getApplicationContext(), "请填写全部内容", Toast.LENGTH_SHORT).show();
            }
            else{
                if(password1.equals(password2)){
                    MyUser bu = new MyUser();
                    bu.setPassword(password1);

                    if(email.indexOf("@")>0) {
                        bu.setEmail(email);
                        bu.setUsername(email);
                    }
                    else{
                        bu.setMobilePhoneNumber(email);
                        bu.setUsername(email);
                    }
                    bu.setMoney(0.0);
                    if(wlan.isChecked()){
                        bu.setSex('F');
                    }
                    else{
                        bu.setSex('M');
                    }
                    bu.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser user, BmobException e) {
                        if(e==null){
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        }else{
                            System.out.println(e);
                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                        }}
                });
            }
                else{
                    Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
