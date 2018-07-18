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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.oude.dndhelper.fragment.*;
import android.widget.*;
import com.oude.dndhelper.activity.*;

public class MainActivity extends BaseActivity 
{
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private DiceFragment mDiceFragment;
    private BookFragment mBookFragment;
    private ShopFragment mShopFragment;
    private CharacterFragmnet mCharacterFragmnet;
    private Fragment[] mFragments;
    private BottomNavigationBar bottomNavigationBar;
    private int index;//点击的fragment的下标
    private int currentTabIndex=0;//当前的fragment的下标
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initView();
        initBottomNavigationBar();
        initListener();
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
        //底部导航栏和碎片
        mDiceFragment = new DiceFragment();
        mCharacterFragmnet = new CharacterFragmnet();
        mShopFragment = new ShopFragment();
        mBookFragment = new BookFragment();
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
		mFragments = new Fragment[]{mDiceFragment,mShopFragment,mCharacterFragmnet,mBookFragment};


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
                    Intent intent2 =new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.nav_about:
                    Intent intent3 =new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent3);
                    //mDrawerLayout.closeDrawers();
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

    //初始化底部导航栏
    private void initBottomNavigationBar()
    {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING)
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        //模式跟背景的设置都要在添加tab前面，不然不会有效果。
        bottomNavigationBar
            .setActiveColor(R.color.colorPrimary)
            .setInActiveColor(R.color.grey)//默认未选择颜色
            .setBarBackgroundColor(R.color.white);//默认背景色;//选中颜色 图标和文字

        bottomNavigationBar
            .addItem(new BottomNavigationItem(R.drawable.ico_dice, R.string.tab_dice))
            .addItem(new BottomNavigationItem(R.drawable.ico_shop, R.string.tab_shop))
            .addItem(new BottomNavigationItem(R.drawable.ico_character, R.string.tab_character))
            .addItem(new BottomNavigationItem(R.drawable.ico_book, R.string.tab_book))
            .setFirstSelectedPosition(0)//设置默认选择的按钮
            .initialise();//所有的设置需在调用该方法前完成
    }
    //初始化Fragment监听器
    private void initListener()
    {
        //设置默认选择的Fragment
        FragmentTransaction init = getSupportFragmentManager().beginTransaction();
        init.add(R.id.fl, mFragments[0]);
        init.show(mFragments[0]).commit();

        //设置lab点击事件
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                @Override
                public void onTabSelected(int position)
                {
                    switch (position)
                    {
                        case 0:
                            index = 0;
                            break;
                        case 1:
                            index = 1;
                            break;
                        case 2:
                            index = 2;
                            break;
                        case 3:
                            index = 3;
                            break;
                    }
                    if (currentTabIndex != index)
                    {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        trx.hide(mFragments[currentTabIndex]);
                        if (!mFragments[index].isAdded())
                        {
                            trx.add(R.id.fl, mFragments[index]);
                        }
                        trx.show(mFragments[index]).commit();
                    }
                    currentTabIndex = index;
                    //Toast.makeText(MainActivity.this,"onTabSelected"+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTabUnselected(int position)
                {
                    //Toast.makeText(MainActivity.this,"onTabUnselected"+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTabReselected(int position)
                {
                    //Toast.makeText(MainActivity.this,"onTabReselected"+position,Toast.LENGTH_SHORT).show();

                }
            });
    }
}
