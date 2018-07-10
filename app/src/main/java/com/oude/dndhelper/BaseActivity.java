package com.oude.dndhelper;

import android.support.v7.app.AppCompatActivity;
import com.oude.dndhelper.utils.LocalManageUtil;
import android.content.Context;

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
