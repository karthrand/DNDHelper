package com.oude.dndhelper;

import android.app.*;
import android.os.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v4.view.GravityCompat;
import android.support.design.widget.NavigationView;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.Context;

public class MainActivity extends BaseActivity 
{
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initView();
        
    }

    //初始化布局
    private void initView()
    {
        setContentView(R.layout.main);
        //标题栏,手动设置标题栏名称，使修改语言后自动更改
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(MainActivity.this.getResources().getText(R.string.app_name));
        setSupportActionBar(toolbar);
        //侧滑栏
        navView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout1);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_language);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationViewListener());

    }

    //侧滑栏功能监听器
    class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener
    {

        @Override
        public boolean onNavigationItemSelected(MenuItem p1)
        {
            int id = p1.getItemId();
            switch (id)
            {
                case R.id.nav_language:
                    Intent intent1 =new Intent(MainActivity.this, LanguageActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.nav_setting:
                    mDrawerLayout.closeDrawers();
                    break;
                default:
                    break;
            }
            return true;
        }

    }


    //侧滑栏左上角功能设置
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    //刷新主页面
    public static void reStart(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
