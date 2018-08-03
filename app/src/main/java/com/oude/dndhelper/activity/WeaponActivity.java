package com.oude.dndhelper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.content.ContentValues;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.util.TypedValue;
import java.util.LinkedList;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.database.DatabaseErrorHandler;
import android.support.design.widget.Snackbar;
import com.oude.dndhelper.entity.*;
import com.oude.dndhelper.utils.*;
import com.oude.dndhelper.*;
import android.support.v7.widget.*;
import android.widget.AdapterView.*;
import android.graphics.*;
import android.view.*;
import android.text.*;

public class WeaponActivity extends BaseActivity
{
	//数据库相关
    private DBListAdapter adapter;
    private ShopDatabaseHelper shopdb;
    public static final String DB_NAME = "shop.db";
    public static final String Table_NAME = "weapon";
    //用item表的name作为筛选条件，因此需要保证item的name的唯一性
    private String weapon_name,weapon_type,attack_type,use_type,damage_type,damage_s,damage_m,weapon_crit,weapon_explain,weapon_source;
    private Float weapon_price,weapon_weight;
	private Integer weapon_range;
    //存储取出来的item名字
    private List<ItemsList> list = new ArrayList<>();
    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;
    //下拉表
    private Spinner sp_fixed,sp_change;
    private ArrayAdapter<String> adapter_fixed,adapter_change;
    String[] change;
    private Button bn_query,bn_insert;
    //使用数组获取array的值，不同语言下显获取到的不同
    ArrayList<String> spinner_chose,spinner_type,spinner_source;
    String[] spinner_choses,spinner_weapon_types,spinner_attack_types,spinner_use_types,spinner_damage_types,spinner_sources;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_weapon);
		initview();	
    }


	//初始化布局
	private void initview()
    {
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.weapon_toolbar);
        toolbar.setTitle(WeaponActivity.this.getResources().getText(R.string.shop_weapon));
        setSupportActionBar(toolbar);
        //按钮绑定
        bn_query = (Button) findViewById(R.id.weapon_query);
        bn_insert = (Button) findViewById(R.id.weapon_insert);
        //下拉表绑定
        sp_fixed = (Spinner) findViewById(R.id.weapon_Spinner1);
        sp_change = (Spinner) findViewById(R.id.weapon_Spinner2);
        //下拉刷新绑定控件
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.shop_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
                @Override
                public void onRefresh()
                {
                    refreshItems();
                }
            });

        //创建数据库，并指定数据库文件名称和版本，完成初始化
        //数据库文件会自动在/data/data/<package name>/databases/目录下创建
        //如果item.db数据库已创建，不会重复调用MyDatabaseHelper的onCreate()方法创建
        shopdb = new ShopDatabaseHelper(this, DB_NAME, null, 1);
        //以读写操作方式打开数据库
        shopdb.getWritableDatabase();
        initItems();

        //Recycle实现
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.shop_RecyclerView1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        adapter = new DBListAdapter(WeaponActivity.this, list);
		adapter.setOnItemClickListener(new mItemClickListener());
        recycleView.setAdapter(adapter);

        //下拉表
        //父下拉表，下拉内容固定
        String[] fix =  this.getResources().getStringArray(R.array.spinner_chose_weapon);
        adapter_fixed = new ArrayAdapter<String>(this, R.layout.spinner_item, fix);
        adapter_fixed.setDropDownViewResource(R.layout.spinner_down);
        sp_fixed.setAdapter(adapter_fixed);
        sp_fixed.setOnItemSelectedListener(new spinnerListener());

        //查询按钮
        bn_query.setOnClickListener(new queryListener());
        //插入按钮
        bn_insert.setOnClickListener(new insertListener());

        //使用数据获取不同语言下的各种string-array     
        spinner_choses = getResources().getStringArray(R.array.spinner_chose_weapon);
        spinner_weapon_types = getResources().getStringArray(R.array.spinner_type_weapon_type);
        spinner_attack_types = getResources().getStringArray(R.array.spinner_type_attack_type);
        spinner_use_types = getResources().getStringArray(R.array.spinner_type_use_type);
        spinner_damage_types = getResources().getStringArray(R.array.spinner_type_damage_type);
        spinner_sources = getResources().getStringArray(R.array.spinner_source_weapon);

	}

	//实现toolbar按钮功能
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.shop_toolbar, menu);
        return true;
    }

    //菜单文件功能实现
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.imports:
                Toast.makeText(this, "导入功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exports:
                Toast.makeText(this, "导出功能", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

	//Recycle的的点击和长按事件实现
	class mItemClickListener implements DBListAdapter.OnItemClickListener
    {

		@Override
		public void onLongClick(int position, View v)
		{
			ItemsList itmsList = list.get(position);
			switch (position)
			{
				default: 
					DeleteItem(itmsList.getName(), position , v);
					break;
			}
		}


		@Override
		public void onClick(int position, View v)
		{
			ItemsList itmsList = list.get(position);

			//在初始化详情之前获取当前列的name
			weapon_name = itmsList.getName();
			switch (position)
			{
				default: 
				    Detail();
					break;
			}
		}
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //关闭数据库，一个Activity中可以只使用一个数据库实例，节省性能
        shopdb.close();

    }

    //下拉表关联实现
    class spinnerListener implements OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
        {
            switch (p3)
            {
                case 0: 
                    change =  WeaponActivity.this.getResources().getStringArray(R.array.spinner_type_weapon_type);
                    adapter_change = new ArrayAdapter<String>(WeaponActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);

                    break;
                case 1:
                    change =  WeaponActivity.this.getResources().getStringArray(R.array.spinner_type_attack_type);
                    adapter_change = new ArrayAdapter<String>(WeaponActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);
                    break;
			    case 2:
                    change =  WeaponActivity.this.getResources().getStringArray(R.array.spinner_type_use_type);
                    adapter_change = new ArrayAdapter<String>(WeaponActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);
                    break;
				case 3:
                    change =  WeaponActivity.this.getResources().getStringArray(R.array.spinner_type_damage_type);
                    adapter_change = new ArrayAdapter<String>(WeaponActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);
                    break;
				case 4:
                    change =  WeaponActivity.this.getResources().getStringArray(R.array.spinner_source_weapon);
                    adapter_change = new ArrayAdapter<String>(WeaponActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> p1)
        {
            // TODO: Implement this method
        }

    }

    //查询按钮实现
    class queryListener implements OnClickListener
    {
        @Override
        public void onClick(View p1)
        {
            String querytype = sp_fixed.getSelectedItem().toString();
            String subtype = sp_change.getSelectedItem().toString();
            quert(querytype, subtype);
        }
    }

    //插入按钮实现
    class insertListener implements OnClickListener
    {
        @Override
        public void onClick(View p1)
        {
            AlertDialog.Builder builder= new AlertDialog.Builder(WeaponActivity.this);
            View detailView = LayoutInflater.from(WeaponActivity.this).inflate(R.layout.weaponsdetails, null);
            builder.setTitle(WeaponActivity.this.getResources().getText(R.string.insert));
            //使用自定义xml
            builder.setView(detailView);
            //打开数据库
            final SQLiteDatabase  db = shopdb.getReadableDatabase();
            //详情页面view加载和控件绑定
            final EditText name = detailView.findViewById(R.id.weapon_name);
            final EditText source = detailView.findViewById(R.id.weapon_source);
            final EditText type = detailView.findViewById(R.id.weapon_type);
			final EditText attack = detailView.findViewById(R.id.attack_type);
			final EditText use = detailView.findViewById(R.id.use_type);
			final EditText damage = detailView.findViewById(R.id.damage_type);
			final EditText damage_st = detailView.findViewById(R.id.damage_s);
			final EditText damage_mt = detailView.findViewById(R.id.damage_m);
			final EditText crit = detailView.findViewById(R.id.weapon_crit);
			final EditText range = detailView.findViewById(R.id.weapon_range);
            final EditText price = detailView.findViewById(R.id.weapon_price);
            final EditText weight = detailView.findViewById(R.id.weapon_weight);
            final EditText explain = detailView.findViewById(R.id.weapon_explain);
            //修改数据库中取出数据后的字体大小和风格，尽量与旁边的TextView显示风格对齐
            List<EditText> ets = new LinkedList<EditText>();
            ets.add(name);
            ets.add(source);
            ets.add(type);
			ets.add(attack);
			ets.add(use);
			ets.add(damage);
			ets.add(damage_st);
			ets.add(damage_mt);
			ets.add(crit);
			ets.add(range);
            ets.add(price);
            ets.add(weight);
            ets.add(explain);
            for (int i=0;i < ets.size();i++)
            {
                ets.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                ets.get(i).setTypeface(Typeface.DEFAULT_BOLD);
            }

            //取消按钮和修改按钮，按钮的值都写在string文件中，此处使用java方式获取
            builder.setPositiveButton(WeaponActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_add_cancel), Toast.LENGTH_SHORT).show();
                    }
                });

            builder.setNegativeButton(WeaponActivity.this.getResources().getText(R.string.insert), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {

                        //获取EditText的值
                        weapon_name = name.getText().toString();
                        if (weapon_name.equals(""))
                        {
                            Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_name_null), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            //查询新增资料名称是否重复
                            Cursor cursor = db.query(Table_NAME, null, "name=?", new String[]{weapon_name}, null, null, null);
                            cursor.moveToFirst();
                            if (cursor.moveToPosition(0) != false)
                            {
                                Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_name_exist), Toast.LENGTH_SHORT).show();
                                cursor.close();
                            }
                            else
                            {
                                //name不冲突后获取
                                weapon_source = source.getText().toString().trim();
                                weapon_type = type.getText().toString().trim();
                                //输入空时赋予0,否则报错
								if (range.getText().toString().trim().equals(""))
                                {
                                    weapon_range = (Integer)0;
                                }
                                else
                                {
                                    weapon_range = Integer.parseInt(range.getText().toString().trim());
                                }
                                if (price.getText().toString().trim().equals(""))
                                {
                                    weapon_price = (float)0;
                                }
                                else
                                {
                                    weapon_price = Float.parseFloat(price.getText().toString().trim());
                                }
                                if (weight.getText().toString().trim().equals(""))
                                {
                                    weapon_weight = (float)0;
                                }
                                else
                                {
                                    weapon_weight = Float.parseFloat(weight.getText().toString().trim());
                                }
                                weapon_explain = explain.getText().toString();
                                //更新数据库
                                ContentValues insertValue = new ContentValues();
                                insertValue.put("name", weapon_name);
                                insertValue.put("source", weapon_source);
                                insertValue.put("weapon_type", weapon_type);
								insertValue.put("attack_type", attack_type);
								insertValue.put("use_type", use_type);
								insertValue.put("damage_type", damage_type);
								insertValue.put("damage_s", damage_s);
								insertValue.put("damage_m", damage_m);
								insertValue.put("crit", weapon_crit);
								insertValue.put("range", weapon_range);
                                insertValue.put("price", weapon_price);
                                insertValue.put("weight", weapon_weight);
                                insertValue.put("explain", weapon_explain);
                                db.insert(Table_NAME, null, insertValue);
                                insertValue.clear();
                                Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_add_success), Toast.LENGTH_SHORT).show();
                                cursor.close();
                            }
                        }

                    }
                });

            builder.show();

        }
    }

    //查询功能的实现
    private void quert(String querytype, String subtype)
    {
        list.clear();
        Cursor cursor = null;
        SQLiteDatabase  quertdb = shopdb.getReadableDatabase();
        //根据父下拉表筛选类型
        //spinner_choses[0]为所以父选择项
		//武器类型下拉表
        if (querytype.equals(spinner_choses[0]))
        {
            if (subtype.equals(spinner_weapon_types[0]))
            {
                cursor = quertdb.query(Table_NAME, null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query(Table_NAME, null, "weapon_type=?", new String[]{subtype}, null, null, null);
            }
        }
		//攻击类型下拉表
        if (querytype.equals(spinner_choses[1]))
        {
            if (subtype.equals(spinner_attack_types[0]))
            {
                cursor = quertdb.query(Table_NAME, null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query(Table_NAME, null, "attack_type=?", new String[]{subtype}, null, null, null);
            }
        }
		//使用类型下拉表
		if (querytype.equals(spinner_choses[2]))
        {
            if (subtype.equals(spinner_use_types[0]))
            {
                cursor = quertdb.query(Table_NAME, null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query(Table_NAME, null, "use_type=?", new String[]{subtype}, null, null, null);
            }
        }
		//伤害类型下拉表
		if (querytype.equals(spinner_choses[3]))
        {
            if (subtype.equals(spinner_damage_types[0]))
            {
                cursor = quertdb.query(Table_NAME, null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query(Table_NAME, null, "damage_type=?", new String[]{subtype}, null, null, null);
            }
        }
		//来源下拉表
		if (querytype.equals(spinner_choses[4]))
        {
            if (subtype.equals(spinner_sources[0]))
            {
                cursor = quertdb.query(Table_NAME, null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query(Table_NAME, null, "source=?", new String[]{subtype}, null, null, null);
            }
        }
		

        //根据查询，取出数据
        cursor.moveToFirst();
        if (cursor.moveToPosition(0) != true)
        {  
            Toast.makeText(this, WeaponActivity.this.getResources().getText(R.string.hint_query_null), Toast.LENGTH_SHORT).show();
        }
        else
        {
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                ItemsList  item =new ItemsList(name);
                list.add(item);
            }while (cursor.moveToNext());
            cursor.close();
        }
        //刷新下
        adapter.notifyDataSetChanged();

    }

    //获取所有items资源并显示，初始化
    private void initItems()
    {
        list.clear();
        //以读写操作方式打开数据库
        SQLiteDatabase  sqlLite = shopdb.getReadableDatabase();
        //取出数据
        Cursor cursor = sqlLite.query(Table_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        //取出所有的item项目时，如果数据库为空，则报错Indpex 0 requested, with a size of 0
        if (cursor.moveToPosition(0) != true)
        {  
            Toast.makeText(this, WeaponActivity.this.getResources().getText(R.string.hint_query_null), Toast.LENGTH_SHORT).show();
        }
        else
        {
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                ItemsList  item =new ItemsList(name);
                list.add(item);
            }while (cursor.moveToNext());
            cursor.close();
        }

    }

    //item长按删除功能
    public void DeleteItem(final String deleteName, final int pos, final View view)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(WeaponActivity.this);
        builder.setTitle(WeaponActivity.this.getResources().getText(R.string.delete));
        builder.setIcon(R.drawable.timg);
        builder.setMessage(WeaponActivity.this.getResources().getText(R.string.delete_confirm) + " " + deleteName + "？");
        builder.setPositiveButton(WeaponActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    final SQLiteDatabase  db = shopdb.getReadableDatabase();
                    //现将要删除的内容获取,以取消
                    Cursor cursor = db.query(Table_NAME, null, "name=?", new String[]{deleteName}, null, null, null);
                    if (cursor.moveToFirst())
                    {
                        do{
                            //遍历获取数据库中的值并给EditText赋值
                            weapon_source = cursor.getString(cursor.getColumnIndex("source"));
                            weapon_type = cursor.getString(cursor.getColumnIndex("weapon_type"));
							attack_type = cursor.getString(cursor.getColumnIndex("attack_type"));
							use_type = cursor.getString(cursor.getColumnIndex("use_type"));
							damage_type = cursor.getString(cursor.getColumnIndex("damage_type"));
							damage_s = cursor.getString(cursor.getColumnIndex("damage_s"));
							damage_m = cursor.getString(cursor.getColumnIndex("damage_m"));
							weapon_crit = cursor.getString(cursor.getColumnIndex("crit"));
							weapon_range = cursor.getInt(cursor.getColumnIndex("range"));
                            weapon_price = cursor.getFloat(cursor.getColumnIndex("price"));
                            weapon_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                            weapon_explain = cursor.getString(cursor.getColumnIndex("explain"));
                        }while(cursor.moveToNext());
                    }

                    //数据库及Recycle中都需要进行删除
                    db.delete(Table_NAME, "name=?", new String[]{deleteName});
                    adapter.removeItem(pos);

                    Snackbar.make(view, WeaponActivity.this.getResources().getText(R.string.hint_delete_success), Snackbar.LENGTH_SHORT).setAction(WeaponActivity.this.getResources().getText(R.string.hint_delete_undo), new View.OnClickListener(){

                            @Override
                            public void onClick(View p1)
                            {
                                //重新插入数据
                                //更新数据库
                                ContentValues undoValue = new ContentValues();
                                undoValue.put("name", deleteName);
                                undoValue.put("source", weapon_source);
                                undoValue.put("weapon_type", weapon_type);
								undoValue.put("attack_type", attack_type);
								undoValue.put("use_type", use_type);
								undoValue.put("damage_type", damage_type);
								undoValue.put("damage_s", damage_s);
								undoValue.put("damage_m", damage_m);
								undoValue.put("crit", weapon_crit);
								undoValue.put("range", weapon_range);
                                undoValue.put("price", weapon_price);
                                undoValue.put("weight", weapon_weight);
                                undoValue.put("explain", weapon_explain);
                                db.insert(Table_NAME, null, undoValue);
                                undoValue.clear();
                                Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_delete_cancel), Toast.LENGTH_SHORT).show();
                                refreshItems();
                            }
                        }).show(); 
                    //关闭
                    cursor.close();

                }
            });


        builder.setNegativeButton(WeaponActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                }
            });
        builder.show();
    }

    //item单击后查看详情
    public void Detail()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(WeaponActivity.this);
        View detailView = LayoutInflater.from(WeaponActivity.this).inflate(R.layout.weaponsdetails, null);
        builder.setTitle(this.getResources().getText(R.string.detail));
        //使用自定义xml
        builder.setView(detailView);
        //详情页面view加载和控件绑定
        final EditText name = detailView.findViewById(R.id.weapon_name);
        final EditText source = detailView.findViewById(R.id.weapon_source);
        final EditText type = detailView.findViewById(R.id.weapon_type);
		final EditText attack = detailView.findViewById(R.id.attack_type);
		final EditText use = detailView.findViewById(R.id.use_type);
		final EditText damage = detailView.findViewById(R.id.damage_type);
		final EditText damage_st = detailView.findViewById(R.id.damage_s);
		final EditText damage_mt = detailView.findViewById(R.id.damage_m);
		final EditText crit = detailView.findViewById(R.id.weapon_crit);
		final EditText range = detailView.findViewById(R.id.weapon_range);
        final EditText price = detailView.findViewById(R.id.weapon_price);
        final EditText weight = detailView.findViewById(R.id.weapon_weight);
        final EditText explain = detailView.findViewById(R.id.weapon_explain);
        //修改数据库中取出数据后的字体大小和风格，尽量与旁边的TextView显示风格对齐
        List<EditText> ets = new LinkedList<EditText>();
        ets.add(name);
        ets.add(source);
        ets.add(type);
		ets.add(attack);
		ets.add(use);
		ets.add(damage);
		ets.add(damage_st);
		ets.add(damage_mt);
		ets.add(crit);
		ets.add(range);
        ets.add(price);
        ets.add(weight);
        ets.add(explain);
        for (int i=0;i < ets.size();i++)
        {
            ets.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            ets.get(i).setTypeface(Typeface.DEFAULT_BOLD);
        }

        //从数据库获取值在详情中显示
        final SQLiteDatabase  db = shopdb.getReadableDatabase();
        Cursor cursor = db.query(Table_NAME, null, "name=?", new String[]{weapon_name}, null, null, null);
        if (cursor.moveToFirst())
        {
            do{
                //遍历获取数据库中的值并给EditText赋值
                weapon_source = cursor.getString(cursor.getColumnIndex("source"));
                weapon_type = cursor.getString(cursor.getColumnIndex("weapon_type"));
                attack_type = cursor.getString(cursor.getColumnIndex("attack_type"));
                use_type = cursor.getString(cursor.getColumnIndex("use_type"));
                damage_type = cursor.getString(cursor.getColumnIndex("damage_type"));
                damage_s = cursor.getString(cursor.getColumnIndex("damage_s"));
                damage_m = cursor.getString(cursor.getColumnIndex("damage_m"));
                weapon_crit = cursor.getString(cursor.getColumnIndex("crit"));
                weapon_range = cursor.getInt(cursor.getColumnIndex("range"));
                weapon_price = cursor.getFloat(cursor.getColumnIndex("price"));
                weapon_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                weapon_explain = cursor.getString(cursor.getColumnIndex("explain"));

                name.setText(weapon_name);
                source.setText(weapon_source);
                type.setText(weapon_type);
				attack.setText(attack_type);
				use.setText(use_type);
				damage.setText(damage_type);
				damage_st.setText(damage_s);
				damage_mt.setText(damage_m);
				crit.setText(weapon_crit);
				range.setText(String.valueOf(weapon_range));	
                price.setText(String.valueOf(weapon_price));
                weight.setText(String.valueOf(weapon_weight));
                explain.setText(weapon_explain);
                //存储在SharedPreferences中，用于在后面进行校验
                SharedPreferences.Editor editor =  getSharedPreferences("weapons", MODE_PRIVATE).edit();               
                editor.putString("weapon_name", weapon_name);
                editor.putString("weapon_source", weapon_source);
                editor.putString("weapon_type", weapon_type);
				editor.putString("attack_type", attack_type);
				editor.putString("use_type", use_type);
				editor.putString("damage_type", damage_type);
				editor.putString("damage_s", damage_s);
				editor.putString("damage_m", damage_m);
				editor.putString("weapon_crit", weapon_crit);
				editor.putInt("weapon_range", weapon_range);
                editor.putFloat("weapon_price", weapon_price);
                editor.putFloat("weapon_weight", weapon_weight);
                editor.putString("weapon_explain", weapon_explain);
                editor.apply();  
            }while(cursor.moveToNext());

        }
        cursor.close();

        //取消按钮和修改按钮，按钮的值都写在string文件中，此处使用java方式获取
        builder.setPositiveButton(this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    //取消目前不做啥，以后可以优化
                }
            });

        builder.setNegativeButton(this.getResources().getText(R.string.modify), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    //检查Interget和float的值是否为空
                    if (TextUtils.isEmpty(price.getText().toString().trim()) ||
                        TextUtils.isEmpty(weight.getText().toString().trim()) ||
						TextUtils.isEmpty(range.getText().toString().trim())
                        )
                    {
                        Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_value_null), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //修改检查，检查是否有更新
                        //重新获取EditText的值
                        weapon_name = name.getText().toString().trim();
                        weapon_source = source.getText().toString().trim();
                        weapon_type = type.getText().toString().trim();
						attack_type = attack.getText().toString().trim();
						use_type = use.getText().toString().trim();
						damage_type = damage.getText().toString().trim();
						damage_s = damage_st.getText().toString().trim();
						damage_m = damage_mt.getText().toString().trim();
						weapon_crit = crit.getText().toString().trim();
						weapon_range = Integer.parseInt(range.getText().toString().trim());
                        weapon_price = Float.parseFloat(price.getText().toString().trim());
                        weapon_weight = Float.parseFloat(weight.getText().toString().trim());
                        weapon_explain = explain.getText().toString();
                        //将新的值和之前保存的比较，无改变则不更新数据库
                        SharedPreferences sp = getSharedPreferences("weapons", MODE_PRIVATE);
                        if (weapon_name.equals(sp.getString("weapon_name", "")) && 
                            weapon_source.equals(sp.getString("weapon_source", "")) && 
                            weapon_type.equals(sp.getString("weapon_type", ""))  &&
							attack_type.equals(sp.getString("attack_type", ""))  &&
							use_type.equals(sp.getString("use_type", ""))  &&
							damage_type.equals(sp.getString("damage_type", ""))  &&
							damage_s.equals(sp.getString("damage_s", ""))  &&
							damage_m.equals(sp.getString("damage_m", ""))  &&
							weapon_crit.equals(sp.getString("weapon_crit", ""))  &&
							weapon_range.equals(sp.getInt("weapon_range", 0))  &&
                            Math.abs(weapon_price - sp.getFloat("weapon_price", 0)) < 0.00001  &&
                            Math.abs(weapon_weight - sp.getFloat("weapon_weight", 0)) < 0.00001 &&
                            weapon_explain.equals(sp.getString("weapon_explain", "")))
                        {
                            Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_modify), Toast.LENGTH_SHORT).show();                     
                        }
                        else
                        {
                            //更新数据库
                            ContentValues updateValue = new ContentValues();
                            updateValue.put("name", weapon_name);
                            updateValue.put("source", weapon_source);
                            updateValue.put("weapon_type", weapon_type);
							updateValue.put("attack_type", attack_type);
							updateValue.put("use_type", use_type);
							updateValue.put("damage_type", damage_type);
							updateValue.put("damage_s", damage_s);
							updateValue.put("damage_m", damage_m);
							updateValue.put("crit", weapon_crit);
							updateValue.put("range", weapon_range);
                            updateValue.put("price", weapon_price);
                            updateValue.put("weight", weapon_weight);
                            updateValue.put("explain", weapon_explain);
                            db.update(Table_NAME, updateValue, "name=?", new String[]{sp.getString("weapon_name", "")});
                            updateValue.clear();
                            Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_modify_success), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

        builder.setNeutralButton(this.getResources().getText(R.string.buy), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    Toast.makeText(WeaponActivity.this, WeaponActivity.this.getResources().getText(R.string.hint_to_achieve), Toast.LENGTH_SHORT).show();
                }
            });
        builder.show();
    }

    //下拉刷新的实现
    private void refreshItems()
    {
        new Thread(new Runnable(){
                @Override
                public void run()
                {
                    try
                    {
                        //因为本地刷新速度太快看不到效果，延迟下
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable(){

                            @Override
                            public void run()
                            {
                                initItems();
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                }

            }).start();
    }
	
    
}
