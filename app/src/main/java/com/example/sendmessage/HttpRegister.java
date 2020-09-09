package com.example.sendmessage;

import android.util.Log;

//import com.example.TheMovieCity.log.LogUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRegister {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//    验证短信
    public static void sendOkHttpRequestCheckCode(String address,RequestBody requestBody, okhttp3.Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

//注册
    public static void sendOkHttpRequestRegister(String address,RequestBody requestBody, okhttp3.Callback callback){
//                                                  地址，参数，回调
//        RequestBody body = RequestBody.create(JSON, requestBody);
//        Log.d(requestBody+"27____");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
