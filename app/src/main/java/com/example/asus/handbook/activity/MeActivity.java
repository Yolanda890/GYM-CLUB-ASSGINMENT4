package com.example.asus.handbook.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.handbook.R;
import com.example.asus.handbook.dataobject.MyUser;
import com.example.asus.handbook.userdefined.CircleTransform;
import com.example.asus.handbook.userdefined.DBOpenHelper;
import com.example.asus.handbook.userdefined.ImageManage;
import com.example.asus.handbook.userdefined.NetWorkUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MeActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private static String currentusername;
    private ImageView figure;
    private TextView username;
    private File mFile;
   private ImageView iv_avator;
    private Bitmap mBitmap;

    String path = "";

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    public static final int CUT_PHOTO = 3;
    public static final int CAMERA_OK = 1;
    Uri photoOutputUri;

    //用户信息 CAMERA_OK = 1;
    //    Uri photoOutputUri;
    //Uri photoOutputUri;
    //    //
    //    //用户信息
    BmobIMUserInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_me);

        currentusername = getIntent().getStringExtra("username");

        navigationView = findViewById(R.id.navigation);
        TextView btn = findViewById(R.id.textView8);
        btn.setOnClickListener(listener1);
        iv_avator=findViewById(R.id.image);
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(3).setChecked(true);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch(item.getItemId()) {
                            case R.id.main:
                                intent=new Intent(MeActivity.this, Main3Activity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.community:
                                intent=new Intent(MeActivity.this, CommunityActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.find:
                                intent=new Intent(MeActivity.this, SearchingActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                break;
                            case R.id.me:
                                intent=new Intent(MeActivity.this, MeActivity.class);
                                intent.putExtra("username",currentusername);
                                startActivity(intent);
                                MeActivity.this.finish();
                                break;
                        }
                        return true;
                    }
                }
        );

        figure = findViewById(R.id.image);

        final ImageManage imageManage=new  ImageManage();
        DBOpenHelper helper = new DBOpenHelper(MeActivity.this,currentusername+"_2.db",null,1);
        final SQLiteDatabase db = helper.getWritableDatabase();

        NetWorkUtil netWorkUtil = new NetWorkUtil();
        boolean judge= netWorkUtil.isNetworkAvailable(MeActivity.this);

        if(judge) {
            BmobQuery<MyUser> query = new BmobQuery<>();
            query.addWhereEqualTo("username", currentusername);
            query.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            Picasso.with(MeActivity.this).load(list.get(0).getFigureimage()).memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .transform(new CircleTransform()).into(figure);
                            Cursor c = db.query("user", null, "username=?", new String[]{currentusername}, null, null, null);
                            if (c == null) {
                                //insert data
                                ContentValues values = new ContentValues();
                                values.put("username", currentusername);
                                values.put("coachimage", imageManage.bitmabToBytes(list.get(0).getFigureimage()));//图片转为二进制
                                long rowid = db.insert("user",null,values);
                            }
                            c.close();
                        }
                        db.close();

                    }
                }
            });
        }else {
            Cursor c = db.query("user", new String[]{"image"}, "username", new String[]{currentusername}, null, null, null);
            while (c.moveToNext()) {

                //将Blob数据转化为字节数组
                byte[] imgData = c.getBlob(c.getColumnIndex("image"));
                Bitmap bitmap = imageManage.getBitmapFromByte(imgData);
                figure.setImageBitmap(bitmap);

            }
            c.close();
            db.close();

        }

        username = findViewById(R.id.textView2);
        username.setText((String) BmobUser.getObjectByKey("username"));
        /*
        BmobFile f=(BmobFile) BmobUser.getObjectByKey("figureimage");

      //  Uri uri = Uri.fromFile(f);
      //  Bitmap bitmap=null;
       // try {
             bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
        }catch(Exception e){

        }
        Bitmap bitmap1 = new CircleTransform().transform(bitmap);
        iv_avator.setImageBitmap(bitmap1);
        */

        setPicture();
    }

    public void logOut(View view) {
        BmobUser.logOut();   //清除缓存用户对象
        BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
        Intent intent;
        intent = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(intent);
        MeActivity.this.finish();
    }
    TextView.OnClickListener listener1 = new TextView.OnClickListener() {
        public void onClick(View v) {
            inputTitleDialog();
        }
    };


    public void setPicture() {

        iv_avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "选择获取图片方式";
                String[] items = new String[]{"拍照", "相册"};

                new AlertDialog.Builder(MeActivity.this)
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        /*
                                            if (ContextCompat.checkSelfPermission(MeActivity.this,
                                                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                //先判断有没有权限 ，没有就在这里进行权限的申请
                                                ActivityCompat.requestPermissions(MeActivity.this,
                                                        PERMISSIONS_STORAGE1, 1);

                                            }
                                        else{
                                                if (ContextCompat.checkSelfPermission(MeActivity.this,
                                                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                    //先判断有没有权限 ，没有就在这里进行权限的申请
                                                    ActivityCompat.requestPermissions(MeActivity.this,PERMISSIONS_STORAGE,3);
                                                }else {
                                                    pickImageFromCamera();
                                                }
                                            }
                                            */
                                        break;
                                    case 1:
                                               if (ActivityCompat.checkSelfPermission(MeActivity.this,
                                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MeActivity.this, PERMISSIONS_STORAGE, 2);
                                        }
                                               else{
                                                   pickImageFromAlbum();
                                               }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }
    //拍照
    public void pickImageFromCamera(){


        //先验证手机是否有sdcard
        String status=Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED))
        {
            //创建File对象，用于存储拍照后的照片
            File dir = new File(Environment.getExternalStorageDirectory(),"/cam");
            dir.mkdirs();


           // File dir1 = new File(dir,"image.jpg");
            String name=System.currentTimeMillis() + ".jpg";
            path=Environment.getExternalStorageDirectory().toString()+System.currentTimeMillis() + ".jpg";

            File outputImage=new File(dir,name);//SD卡的应用关联缓存目录
            try {


                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoOutputUri=FileProvider.getUriForFile(MeActivity.this,
                            "com.example.asus.handbook.activity.fileprovider",outputImage);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                }else{
                    photoOutputUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutputUri);
                startActivityForResult(intent, TAKE_PHOTO);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println(e);
            }
        }else{
            Toast.makeText(MeActivity.this, "没有储存卡",Toast.LENGTH_LONG).show();
        }





    }
    //从相册获取图片
    public void pickImageFromAlbum(){
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, CHOOSE_PHOTO);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:

                        //启动图像裁剪
                    iv_avator.setImageURI(photoOutputUri);
                   // startPhotoZoom(photoOutputUri);

                   // mBitmap = bundle.getParcelable("data");
                    //iv_avator.setImageBitmap(mBitmap);

                    MyUser newUser = new MyUser();
                    newUser.setFigureimage(newUser,path);


                    break;

                case CHOOSE_PHOTO:

                    if (data == null || data.getData() == null) {
                        return;
                    }
                    Bitmap bm = null;
                    try {

                        Uri originalUri = data.getData();        //获得图片的uri
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);        //显得到bitmap图片
                        //这里开始的第二部分，获取图片的路径：
                        //String[] proj = {MediaStore.Images.Media.DATA};
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        //Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        //获得用户选择的图片的索引值
                       // int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                       // cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                       // path = cursor.getString(column_index);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //startPhotoZoom(data.getData());
                    iv_avator.setImageBitmap(bm);
                    break;
                case CUT_PHOTO:

                    if (data != null) {
                        setPicToView(data);
                    }
                    break;


            }
        }

  //}
    /**
     * 打开系统图片裁剪功能
     *
     * @param uri  uri
     */

    private void startPhotoZoom(Uri uri) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("scale", true); //黑边
            intent.putExtra("scaleUpIfNeeded", true); //黑边
            intent.putExtra("return-data", true);
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, CUT_PHOTO);

            //setPicToView(intent);
        }


    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {

            mBitmap = bundle.getParcelable("data");

            Bitmap bitmap = new CircleTransform().transform(mBitmap);
            iv_avator.setImageBitmap(bitmap);
            MyUser newUser = new MyUser();
            newUser.setFigureimage(newUser,path);
            //fetchUserInfo();




        }
    }
    private void inputTitleDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("修改密码");

        builder.setNegativeButton("Cancel", null);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View view=LayoutInflater.from(this).inflate(
                R.layout.modify_password, null);
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                EditText editText1 =(EditText)view.findViewById(R.id.od);
                final  String old_password=editText1.getText().toString();


                EditText editText2 =(EditText)view.findViewById(R.id.ne);
                final   String new_password=editText2.getText().toString();
                String username = (String) BmobUser.getObjectByKey("username");
                  //MyUser中的扩展属性



                MyUser.updateCurrentUserPassword(old_password , new_password, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        builder.show();


    }
    // builder.show();
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromCamera();
                }else {

                    Toast.makeText(MeActivity.this,"请手动打开相机权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromAlbum();
                }else {
                    Toast.makeText(MeActivity.this,"请手动打开存储权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromCamera();
                }else {
                    Toast.makeText(MeActivity.this,"请手动打开写入存储权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }

    }
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static String[] PERMISSIONS_STORAGE1 = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


}
