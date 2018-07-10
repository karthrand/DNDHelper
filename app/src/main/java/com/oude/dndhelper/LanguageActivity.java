package com.oude.dndhelper;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import com.oude.dndhelper.utils.LocalManageUtil;
import android.content.Context;
import android.content.Intent;

public class LanguageActivity extends BaseActivity implements OnClickListener
{
    private Button bn_en,bn_chs,bn_cht;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.language_toolbar);

        toolbar.setTitle(LanguageActivity.this.getResources().getText(R.string.language_settings));
        setSupportActionBar(toolbar);
        bn_en = (Button) findViewById(R.id.bn_english);
        bn_chs = (Button) findViewById(R.id.bn_simpleChinese);
        bn_cht = (Button) findViewById(R.id.bn_traditionalChinese);

        bn_en.setOnClickListener(this);
        bn_chs.setOnClickListener(this);
        bn_cht.setOnClickListener(this);
    }
    
    //修改语言，0为简体中文，1为繁体中文，2为英文
    @Override
    public void onClick(View p1)
    {
        switch (p1.getId())
        {
            case R.id.bn_simpleChinese:
                selectLanguage(0);
                break;
            case R.id.bn_traditionalChinese:
                selectLanguage(1);
                break;
            case R.id.bn_english:
                selectLanguage(2);
                break;
            default:
                break;
        }
    }
    
    //切换语言，并跳转到主界面
    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(this, select);
        MainActivity.reStart(this);
    }
    
    
}
