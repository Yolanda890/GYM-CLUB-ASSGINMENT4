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
import com.example.asus.handbook.userdefined.CircleTransform;
import com.example.asus.handbook.userdefined.DBOpenHelper;
import com.example.asus.handbook.userdefined.ImageManage;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    @NonNull

    private List<String> list1,list2,list3;
    private List<byte[]> list4,list5;
    private int rowLayout;
    private Context mContext;
    private int symbol=0;

    public CommunityAdapter(List<String> list1, List<String> list2,List<String> list3, List<byte[]> list4,List<byte[]> list5,int rowLayout, Context context,int symbol) {
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        this.list4 = list4;
        this.list5 = list5;
        this.rowLayout = rowLayout;
        this.mContext = context;
        this.symbol=symbol;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);  //this is the major change here.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String entry = list1.get(i);
        viewHolder.myContext.setText(entry);
        viewHolder.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        if(symbol==0) {
            Picasso.with(mContext).load(list3.get(i)).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new CircleTransform()).into(viewHolder.myPic2);
        }
        /*
        else{
            ImageManage manage = new ImageManage();
            Bitmap bitmap = manage.getBitmapFromByte(list5.get(i));
            viewHolder.myPic2.setImageBitmap(bitmap);
        }
        */
    }

    @Override
    public int getItemCount() {
        return list1 == null ? 0 : list1.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myContext;
        public ImageView myPic,myPic2;
        public Button myButton; //对应头像的按钮
        public ViewHolder(View itemView) {
            super(itemView);
            myContext = (TextView) itemView.findViewById(R.id.textView);
            myPic= (ImageView)itemView.findViewById(R.id.imageView);
            myPic2= (ImageView)itemView.findViewById(R.id.imageView2);
            myButton = (Button) itemView.findViewById(R.id.button);
        }
    }
}
