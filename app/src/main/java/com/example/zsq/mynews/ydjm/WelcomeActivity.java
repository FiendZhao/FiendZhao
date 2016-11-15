package com.example.zsq.mynews.ydjm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.zsq.mynews.MainActivity;
import com.example.zsq.mynews.R;

/**
 * Created by ZSQ on 2016/10/28.
 */
public class WelcomeActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomeactivity);

        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (isFirstRun()){
                    //应用程序第一次运行加载引导页面
                    startActivity(new Intent(WelcomeActivity.this,GuideActivity.class));
                }else {
                    //如果应用程序不是第一次运行，就跳转到主界面
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                }
                //跳转之后关闭此界面
                finish();
                //设置Activity的跳转动画
                overridePendingTransition(R.anim.activity_enter,R.anim.ativity_exit);
                return false;
            }
        }).sendEmptyMessageDelayed(0,3000);
    }
    /**
     * 判断应用是否是第一次运行
     * */
    private boolean isFirstRun(){
        //获取preferences，如果没有则自动创建
        SharedPreferences preferences = getSharedPreferences("app",MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("first_run",true);
        //如果是第一次运行
        if (isFirst){
            //改变为不是第一次
            preferences.edit().putBoolean("first_run",false).commit();
        }
        return isFirst;
    }

}
