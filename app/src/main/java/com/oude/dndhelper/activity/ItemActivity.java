package com.oude.dndhelper.activity;

import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView.SavedState;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.content.ContentValues;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.graphics.Typeface;
import java.util.LinkedList;
import java.math.BigDecimal;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView.FixedViewInfo;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.database.DatabaseErrorHandler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SnapHelper;
import com.oude.dndhelper.entity.*;
import com.oude.dndhelper.utils.*;
import com.oude.dndhelper.*;
import android.support.v7.widget.*;


public class ItemActivity extends BaseActivity
{
    private DBListAdapter adapter;

    private ShopDatabaseHelper itemdb;
    //用item表的name作为筛选条件，因此需要保证item的name的唯一性
    private String item_name,item_type,item_explain,item_source;
    private Float item_price,item_weight;
    //存储取出来的item名字
    private List<ItemsList> list = new ArrayList<>();
    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;
    //下拉表
    private Spinner sp_fixed,sp_change;
    private ArrayAdapter<String> adapter_fixed,adapter_change;
    String[] change;
    private Button bn_query,bn_insert;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_item);
		Toolbar toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        toolbar.setTitle(ItemActivity.this.getResources().getText(R.string.shop_item));
        setSupportActionBar(toolbar);
        //按钮绑定
        bn_query = (Button) findViewById(R.id.item_query);
        bn_insert = (Button) findViewById(R.id.item_insert);
        //下拉表绑定
        sp_fixed = (Spinner) findViewById(R.id.item_Spinner1);
        sp_change = (Spinner) findViewById(R.id.item_Spinner2);
        //下拉刷新绑定控件
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.item_swipe_refresh);
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
        itemdb = new ShopDatabaseHelper(this, "item.db", null, 1);
        //以读写操作方式打开数据库
        itemdb.getWritableDatabase();
        initItems();

        //Recycle实现
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.item_RecyclerView1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        adapter = new DBListAdapter(list);
        recycleView.setAdapter(adapter);

        //下拉表
        //父下拉表，下拉内容固定
        String[] fix =  this.getResources().getStringArray(R.array.spinner_chose);
        adapter_fixed = new ArrayAdapter<String>(this, R.layout.spinner_item, fix);
        adapter_fixed.setDropDownViewResource(R.layout.spinner_down);
        sp_fixed.setAdapter(adapter_fixed);
        sp_fixed.setOnItemSelectedListener(new spinnerListener());

        //查询按钮
        bn_query.setOnClickListener(new queryListener());
        //插入按钮
        bn_insert.setOnClickListener(new insertListener());

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //关闭数据库，一个Activity中可以只使用一个数据库实例，节省性能
        itemdb.close();

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
                    change =  ItemActivity.this.getResources().getStringArray(R.array.spinner_type);
                    adapter_change = new ArrayAdapter<String>(ItemActivity.this, R.layout.spinner_item, change);
                    adapter_change.setDropDownViewResource(R.layout.spinner_down);
                    sp_change.setAdapter(adapter_change);

                    break;
                case 1:
                    change =  ItemActivity.this.getResources().getStringArray(R.array.spinner_source);
                    adapter_change = new ArrayAdapter<String>(ItemActivity.this, R.layout.spinner_item, change);
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


    //RecycleView的Adapter
    public class DBListAdapter extends RecyclerView.Adapter<DBListAdapter.ViewHolder>
    {
        private List<ItemsList> mItemsList;
        class ViewHolder extends RecyclerView.ViewHolder
        {
            View ItemsView;
            TextView itemsName;

            public ViewHolder(View view)
            {
                super(view);
                ItemsView = view;
                itemsName = view.findViewById(R.id.items_list_name);
            }
        }

        public DBListAdapter(List<ItemsList> itemsList)
        {
            mItemsList = itemsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemslist, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            //点击事件
            holder.ItemsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        int position = holder.getAdapterPosition();
                        ItemsList itmsList = mItemsList.get(position);
                        
                        //在初始化详情之前获取当前列的name
                        item_name = itmsList.getName();
                        switch (position)
                        {
                            default: Detail();
                                break;
                        }
                    }
                });
            //长按事件
            holder.ItemsView.setOnLongClickListener(new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(View p1)
                    {
                        int position = holder.getAdapterPosition();
                        ItemsList itmsList = mItemsList.get(position);
                        switch (position)
                        {
                            default: DeleteItem(itmsList.getName(), position,p1);
                                break;
                        }
                        return true;
                    }
                });            
   
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            ItemsList itemsList = mItemsList.get(position);
            holder.itemsName.setText(itemsList.getName());
            
        }

        @Override
        public int getItemCount()
        {
            return mItemsList.size();
        }
        //自定义删除操作
        public void removeItem(int pos)
        {
            mItemsList.remove(pos);
            notifyItemRemoved(pos);
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
            AlertDialog.Builder builder= new AlertDialog.Builder(ItemActivity.this);
            View detailView = LayoutInflater.from(ItemActivity.this).inflate(R.layout.itemsdetails, null);
            builder.setTitle(ItemActivity.this.getResources().getText(R.string.insert));
            //使用自定义xml
            builder.setView(detailView);
            //打开数据库
            final SQLiteDatabase  db = itemdb.getReadableDatabase();
            //详情页面view加载和控件绑定
            final EditText name = detailView.findViewById(R.id.item_name);
            final EditText source = detailView.findViewById(R.id.item_source);
            final EditText type = detailView.findViewById(R.id.item_type);
            final EditText price = detailView.findViewById(R.id.item_price);
            final EditText weight = detailView.findViewById(R.id.item_weight);
            final EditText explain = detailView.findViewById(R.id.item_explain);

            //修改数据库中取出数据后的字体大小和风格，尽量与旁边的TextView显示风格对齐
            List<EditText> ets = new LinkedList<EditText>();
            ets.add(name);
            ets.add(source);
            ets.add(type);
            ets.add(price);
            ets.add(weight);
            ets.add(explain);
            for (int i=0;i < ets.size();i++)
            {
                ets.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                ets.get(i).setTypeface(Typeface.DEFAULT_BOLD);
            }


            //取消按钮和修改按钮，按钮的值都写在string文件中，此处使用java方式获取
            builder.setPositiveButton(ItemActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        Toast.makeText(ItemActivity.this, "新增已取消！", Toast.LENGTH_SHORT).show();
                    }
                });

            builder.setNegativeButton(ItemActivity.this.getResources().getText(R.string.insert), new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {

                        //获取EditText的值
                        item_name = name.getText().toString();
                        if (item_name.equals(""))
                        {
                            Toast.makeText(ItemActivity.this,"名称不能为空",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            //查询新增资料名称是否重复
                            Cursor cursor = db.query("item", null, "name=?", new String[]{item_name}, null, null, null);
                            cursor.moveToFirst();
                            if (cursor.moveToPosition(0) != false)
                            {
                                Toast.makeText(ItemActivity.this, "相同名称已存在，请更换名称！", Toast.LENGTH_SHORT).show();
                                cursor.close();
                            }
                            else
                            {
                                //name不冲突后获取
                                item_source = source.getText().toString();
                                item_type = type.getText().toString();
                                //输入空时赋予0,否则报错
                                if (price.getText().toString().equals(""))
                                {
                                    item_price = (float)0;
                                }
                                else
                                {
                                    item_price = Float.parseFloat(price.getText().toString());
                                }
                                if (weight.getText().toString().equals(""))
                                {
                                    item_weight = (float)0;
                                }
                                else
                                {
                                    item_weight = Float.parseFloat(weight.getText().toString());
                                }
                                item_explain = explain.getText().toString();
                                //更新数据库
                                ContentValues insertValue = new ContentValues();
                                insertValue.put("name", item_name);
                                insertValue.put("source", item_source);
                                insertValue.put("type", item_type);
                                insertValue.put("price", item_price);
                                insertValue.put("weight", item_weight);
                                insertValue.put("explain", item_explain);
                                db.insert("item", null, insertValue);
                                insertValue.clear();
                                Toast.makeText(ItemActivity.this, "新数据插入成功!", Toast.LENGTH_SHORT).show();
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
        SQLiteDatabase  quertdb = itemdb.getReadableDatabase();
        //根据父下拉表筛选类型
        if (querytype.equals("物品类别"))
        {
            if (subtype.equals("所有类别"))
            {
                cursor = quertdb.query("item", null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query("item", null, "type=?", new String[]{subtype}, null, null, null);
            }
        }
        else
        {
            if (subtype.equals("所有来源"))
            {
                cursor = quertdb.query("item", null, null, null, null, null, null);
            }
            else
            {
                cursor = quertdb.query("item", null, "source=?", new String[]{subtype}, null, null, null);
            }
        }

        //根据查询，取出数据
        cursor.moveToFirst();
        if (cursor.moveToPosition(0) != true)
        {  
            Toast.makeText(this, "查询为空!", Toast.LENGTH_SHORT).show();
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
        SQLiteDatabase  sqlLite = itemdb.getReadableDatabase();
        //取出数据
        Cursor cursor = sqlLite.query("item", null, null, null, null, null, null);
        cursor.moveToFirst();
        //取出所有的item项目时，如果数据库为空，则报错Indpex 0 requested, with a size of 0
        if (cursor.moveToPosition(0) != true)
        {  
            //新增一条数据，以免报报空指针错误
            ContentValues values = new ContentValues();
            values.put("name", "背包");
            values.put("type", "冒险物品");
            values.put("price", 2.1);
            values.put("weight", 2);
            values.put("explain", "普通的背包");
            values.put("source", "玩家手册");
            sqlLite.insert("item", null, values);
            values.clear();
            values.put("name", "斩龙巨剑");
            values.put("type", "技能职业");
            values.put("price", 2.1);
            values.put("weight", 2);
            values.put("explain", "瞎编的");
            values.put("source", "瞎编");
            sqlLite.insert("item", null, values);
            values.clear();
            Toast.makeText(this, "无Item，已自动添加初始，请下拉刷新!", Toast.LENGTH_SHORT).show();
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
    public void DeleteItem(final String deleteName, final int pos,final View view)
    {
        
        AlertDialog.Builder builder= new AlertDialog.Builder(ItemActivity.this);
        builder.setTitle("删除");
        builder.setIcon(R.drawable.timg);
        builder.setMessage("确定删除" + deleteName + "吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    final SQLiteDatabase  db = itemdb.getReadableDatabase();
                    //现将要删除的内容获取,以取消
                    Cursor cursor = db.query("item", null, "name=?", new String[]{deleteName}, null, null, null);
                    if (cursor.moveToFirst())
                    {
                        do{
                            //遍历获取数据库中的值并给EditText赋值
                            item_source = cursor.getString(cursor.getColumnIndex("source"));
                            item_type = cursor.getString(cursor.getColumnIndex("type"));
                            item_price = cursor.getFloat(cursor.getColumnIndex("price"));
                            item_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                            item_explain = cursor.getString(cursor.getColumnIndex("explain"));
                        }while(cursor.moveToNext());
                    }
                    
                    //数据库及Recycle中都需要进行删除
                    db.delete("item", "name=?", new String[]{deleteName});
                    adapter.removeItem(pos);
                    
                    Snackbar.make(view, "数据已删除", Snackbar.LENGTH_SHORT).setAction("撤销删除", new View.OnClickListener(){

                            @Override
                            public void onClick(View p1)
                            {
                                //重新插入数据
                                //更新数据库
                                ContentValues undoValue = new ContentValues();
                                undoValue.put("name", deleteName);
                                undoValue.put("source", item_source);
                                undoValue.put("type", item_type);
                                undoValue.put("price", item_price);
                                undoValue.put("weight", item_weight);
                                undoValue.put("explain", item_explain);
                                db.insert("item", null, undoValue);
                                undoValue.clear();
                                Toast.makeText(ItemActivity.this, "删除已取消!", Toast.LENGTH_SHORT).show();
                                refreshItems();
                            }
                    }).show(); 
                    //关闭
                    cursor.close();
                    
                }
            });


        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

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

        AlertDialog.Builder builder= new AlertDialog.Builder(ItemActivity.this);
        View detailView = LayoutInflater.from(ItemActivity.this).inflate(R.layout.itemsdetails, null);
        builder.setTitle(this.getResources().getText(R.string.detail));
        //使用自定义xml
        builder.setView(detailView);
        //详情页面view加载和控件绑定
        final EditText name = detailView.findViewById(R.id.item_name);
        final EditText source = detailView.findViewById(R.id.item_source);
        final EditText type = detailView.findViewById(R.id.item_type);
        final EditText price = detailView.findViewById(R.id.item_price);
        final EditText weight = detailView.findViewById(R.id.item_weight);
        final EditText explain = detailView.findViewById(R.id.item_explain);

        //修改数据库中取出数据后的字体大小和风格，尽量与旁边的TextView显示风格对齐
        List<EditText> ets = new LinkedList<EditText>();
        ets.add(name);
        ets.add(source);
        ets.add(type);
        ets.add(price);
        ets.add(weight);
        ets.add(explain);
        for (int i=0;i < ets.size();i++)
        {
            ets.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            ets.get(i).setTypeface(Typeface.DEFAULT_BOLD);
        }

        //从数据库获取值在详情中显示
        final SQLiteDatabase  db = itemdb.getReadableDatabase();
        Cursor cursor = db.query("item", null, "name=?", new String[]{item_name}, null, null, null);
        if (cursor.moveToFirst())
        {
            do{
                //遍历获取数据库中的值并给EditText赋值
                item_source = cursor.getString(cursor.getColumnIndex("source"));
                item_type = cursor.getString(cursor.getColumnIndex("type"));
                item_price = cursor.getFloat(cursor.getColumnIndex("price"));
                item_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                item_explain = cursor.getString(cursor.getColumnIndex("explain"));

                name.setText(item_name);
                source.setText(item_source);
                type.setText(item_type);               
                price.setText(String.valueOf(item_price));
                weight.setText(String.valueOf(item_weight));
                explain.setText(item_explain);
                //存储在SharedPreferences中，用于在后面进行校验
                SharedPreferences.Editor editor =  getSharedPreferences("items", MODE_PRIVATE).edit();               
                editor.putString("item_name", item_name);
                editor.putString("item_source", item_source);
                editor.putString("item_type", item_type);
                editor.putFloat("item_price", item_price);
                editor.putFloat("item_weight", item_weight);
                editor.putString("item_explain", item_explain);
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
                    //修改检查，检查是否有更新
                    //重新获取EditText的值
                    item_name = name.getText().toString();
                    item_source = source.getText().toString();
                    item_type = type.getText().toString();
                    item_price = Float.parseFloat(price.getText().toString());
                    item_weight = Float.parseFloat(weight.getText().toString());
                    item_explain = explain.getText().toString();
                    //将新的值和之前保存的比较，无改变则不更新数据库
                    SharedPreferences sp = getSharedPreferences("items", MODE_PRIVATE);
                    if (item_name.equals(sp.getString("item_name", "")) && 
                        item_source.equals(sp.getString("item_source", "")) && 
                        item_type.equals(sp.getString("item_type", ""))  &&
                        Math.abs(item_price - sp.getFloat("item_price", 0)) < 0.00001  &&
                        Math.abs(item_weight - sp.getFloat("item_weight", 0)) < 0.00001 &&
                        item_explain.equals(sp.getString("item_explain", "")))
                    {
                        Toast.makeText(ItemActivity.this, "请修改至少一个值！", Toast.LENGTH_SHORT).show();                     
                    }
                    else
                    {
                        //更新数据库
                        ContentValues updateValue = new ContentValues();
                        updateValue.put("name", item_name);
                        updateValue.put("source", item_source);
                        updateValue.put("type", item_type);
                        updateValue.put("price", item_price);
                        updateValue.put("weight", item_weight);
                        updateValue.put("explain", item_explain);
                        db.update("item", updateValue, "name=?", new String[]{sp.getString("item_name", "")});
                        updateValue.clear();
                    }

                }
            });
            
        builder.setNeutralButton(this.getResources().getText(R.string.buy), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    Toast.makeText(ItemActivity.this,"待实现！",Toast.LENGTH_SHORT).show();
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
                        Thread.sleep(500);
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
