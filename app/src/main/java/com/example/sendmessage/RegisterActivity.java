package com.example.sendmessage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.TheMovieCity.http.HttpRegister;
//import com.example.TheMovieCity.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
//import utils.MyTypeAdapter;

public class RegisterActivity extends AppCompatActivity {
    EditText username,email,code,password,rePassword;
    private TextView sendCode;
    private int times = 60;
    public String appkey = "m3005e98e**";//你的appkey
    public String zone = "86";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();
    }


    private void init(){
        sendCode = (TextView)findViewById(R.id.get_code);//发送验证码
        username = (EditText)findViewById(R.id.edusername);
        email = (EditText)findViewById(R.id.edemail);
        code = (EditText)findViewById(R.id.edCode);
        password = (EditText)findViewById(R.id.edpassword);
        rePassword = (EditText)findViewById(R.id.edRepassword);
        Log.i("tests","tests___________");
    }
    public void goBack(View v){//返回
        finish();
    }
    public void getCode(View v) {//获取验证码
        String phone = email.getText().toString();
        countNum();
        //在onCreate方法初始化SDK  短信注册

        MobSDK.init(RegisterActivity.this,"你的appkey","你的appSecret");
        SMSSDK.registerEventHandler(eh);
        Log.i("test","testc"+phone);
        SMSSDK.getVerificationCode("86",phone);
    }

    EventHandler eh=new EventHandler() {//短信回调
        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.i("EventHandler", "提交验证码成功");
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功

                    Log.i("EventHandler", "获取验证码成功");
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表

                    Log.i("EventHandler", "返回支持发送验证码的国家列表");
                }
            }else{
                ((Throwable)data).printStackTrace();
                Log.i("EventHandler", "回调失败");
            }
        }
    };


    private void countNum(){//发送验证码计时
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendCode.setEnabled(false);
                sendCode.setText("已发送(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                sendCode.setEnabled(true);
                sendCode.setText("重新获取");
            }
        }.start();
    }

    public void registerInfo(View v){//注册
        String phone = email.getText().toString();
        String codes = code.getText().toString();

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey", appkey)
                .add("zone", zone)//区号 86
                .add("code", codes)//接收的验证码
                .add("phone", phone)//手机号
                .build();

        HttpRegister.sendOkHttpRequestCheckCode("https://webapi.sms.mob.com/sms/verify",requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resposeData = response.body().string();

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<String, Long>>(){
                        }.getType(),new MyTypeAdapter()).create();
                Map<String,Long> dataDetail = gson.fromJson(resposeData,new TypeToken<Map<String,Long>>(){

                }.getType());//gson类型转换
                Long codeId = dataDetail.get("status");

                Map<String,String> users = new HashMap<>();
                users.put("userName",username.getText().toString());
                users.put("password",password.getText().toString());
                users.put("phone",email.getText().toString());

                if (codeId == 200) {
                    Log.d("msg","注册成功");

//                    注册逻辑
                    register(users);
                } else if (codeId == 405) {
                    Log.d("msg","AppKey为空");
                } else if (codeId == 406) {
                    Log.d("msg","AppKey无效");
                } else if (codeId == 456) {
                    Log.d("msg","手机号码为空");
                } else if (codeId == 457) {
                    Log.d("msg","手机号码格式错误");
                } else if (codeId == 466) {
                    Log.d("msg","请求验证的验证码为空");
                } else if (codeId == 467) {
                    Log.d("msg","请求验证验证码重复");
                } else if (codeId == 468) {
                    Log.d("msg","验证码错误");
                } else if (codeId == 474) {
                    Log.d("msg","没有打开服务端验证开关");
                } else {
                    throw new IllegalStateException("Unexpected value: " + codeId);
                }
//
            }
        });
    }

    public void register(Map users) {//注册

    }



}
