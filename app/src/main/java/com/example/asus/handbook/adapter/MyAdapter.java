package com.example.asus.handbook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.handbook.R;
import com.example.asus.handbook.userdefined.ImageManage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    /* 加1个成员变量 */
    private String currentusername;

    private List<String> list1,list2;
    private List<String> list3;
    private List<byte[]> list4;
    private int rowLayout;
    private Context mContext;
    private VideoAdapter sAdapter;
    private int symbol=0;

                    /* 加2个参数 */
    public MyAdapter(String type,String currentusername,List<String> list1,List<String> list2,List<byte[]> list4, int rowLayout, Context context,int symbol) {

        /* 加1个成员变量 */
        this.currentusername = currentusername;

        this.list1 = list1;
        this.list2 = list2;
        this.list4 = list4;
        this.symbol=symbol;
        this.list3=new ArrayList<>();
        this.rowLayout = rowLayout;
        this.mContext = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String entry = list1.get(i);
        viewHolder.myName.setText(entry);
        viewHolder.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list3.add(entry);
                sAdapter = new VideoAdapter(list3,R.layout.layout_videocard, mContext);
            }
        });
        if(symbol==0) {
            Picasso.with(mContext).load(list2.get(i)).into(viewHolder.myPic);
        }
        else{
                ImageManage manage = new ImageManage();
                Bitmap bitmap = manage.getBitmapFromByte(list4.get(i));
                viewHolder.myPic.setImageBitmap(bitmap);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);  //this is the major change here.
    }

    @Override
    public int getItemCount() {
        return list1 == null ? 0 : list1.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myName;
        public ImageView myPic;
        public Button myButton;
        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.textView);
            myPic= (ImageView)itemView.findViewById(R.id.imageView);
            myButton = (Button) itemView.findViewById(R.id.button);
        }
    }

}

