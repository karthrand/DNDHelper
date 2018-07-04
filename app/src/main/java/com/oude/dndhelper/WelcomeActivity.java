package com.oude.dndhelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.view.WindowManager;
import android.view.Window;
import java.util.Random;

public class WelcomeActivity extends Activity 
{
    //欢迎页背景图数量
    private static int min=1,max=5;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //隐藏标题栏以及状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //使用背景，减少内存消耗,随机选择已有图片展示  
        Random random = new Random();
        int num = random.nextInt(max)%(max-min+1) + min;
        switch(num){
            case 1: 
                getWindow().getDecorView().setBackgroundResource(R.mipmap.welcome01);
                break;
            case 2: 
                getWindow().getDecorView().setBackgroundResource(R.mipmap.welcome02);
                break;
            case 3: 
                getWindow().getDecorView().setBackgroundResource(R.mipmap.welcome03);
                break;
            case 4: 
                getWindow().getDecorView().setBackgroundResource(R.mipmap.welcome04);
                break;
            case 5: 
                getWindow().getDecorView().setBackgroundResource(R.mipmap.welcome05);
                break;      
        }

        super.onCreate(savedInstanceState);
        //延迟三秒后跳转
        handler.sendEmptyMessageDelayed(0,3000);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
