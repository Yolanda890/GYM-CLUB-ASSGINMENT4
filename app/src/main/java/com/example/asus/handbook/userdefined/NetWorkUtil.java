package com.example.asus.handbook.userdefined;






import android.content.Context;

import android.net.ConnectivityManager;

import android.net.NetworkInfo;



public class NetWorkUtil {



    /**

     * 检查网络是否可用

     *

     * @param context

     * @return

     */



        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected())
                {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        // 当前所连接的网络可用
                        return true;
                    }
                }
            }
            return false;
        }



    }

