package com.oude.dndhelper.activity;

import android.os.*;
import android.support.v7.widget.*;
import com.oude.dndhelper.*;
import android.view.View.*;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import android.net.*;
import android.content.*;
import android.support.v7.app.*;
import android.preference.*;
import android.widget.TextView;
import java.net.*;
import java.io.*;
import android.widget.EditText;
import android.util.*;
import android.content.pm.*;
import okhttp3.*;
import org.json.*;
import com.oude.dndhelper.utils.*;

public class AboutActivity extends BaseActivity implements OnClickListener
{
    private Button update,info,feedback;
    private String username,email,comment,email_encode,name_encode;
    public static final int Success = 200;
    public static final int Fail = -1;
    public static final int Repeat = 409;
    public static final int HaveUpdate = 1;
    public static final int Latest = 0;
    private TextView version;
    //下载更新链接
    public static final String UPDATE_URL = "http://oudezhinu.site/download/update.json";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        //初始化
        update = (Button) findViewById(R.id.update);
        info = (Button) findViewById(R.id.info);
        feedback = (Button) findViewById(R.id.feedback);
        version = (TextView) findViewById(R.id.version);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitle(AboutActivity.this.getResources().getText(R.string.about));
        setSupportActionBar(toolbar);
        //设置当前版本
        version.setText(getVersionName(AboutActivity.this));
        //按钮监听
        update.setOnClickListener(this);
        info.setOnClickListener(this);
        feedback.setOnClickListener(this);
        //获取设置中当前用户名和邮箱
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(AboutActivity.this);
        username = sp.getString("user_name", "匿名");
        email = sp.getString("user_mail", "null");
        //加密信息以供反馈使用
        email_encode = java.net.URLEncoder.encode(email);
        name_encode= java.net.URLEncoder.encode(username);
        
    }


    @Override
    public void onClick(View p1)
    {

        switch (p1.getId())
        {
            case R.id.update:
                update();
                break;
            case R.id.info:
                Uri uri = Uri.parse("http://oudezhinu.site/dndhelper/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.feedback:
                AlertDialog.Builder builder= new AlertDialog.Builder(AboutActivity.this);
                View detailView = LayoutInflater.from(AboutActivity.this).inflate(R.layout.feedback, null);
                builder.setTitle(AboutActivity.this.getResources().getText(R.string.feedback));
                builder.setView(detailView);
                TextView fb_name = detailView.findViewById(R.id.fb_name);
                TextView fb_email = detailView.findViewById(R.id.fb_email);
                final EditText comments =detailView.findViewById(R.id.comments);
                fb_name.setText(AboutActivity.this.getResources().getText(R.string.fb_user) + username);
                fb_email.setText(AboutActivity.this.getResources().getText(R.string.fb_email) + email);
                builder.setNegativeButton(AboutActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {

                        }
                    });
                builder.setPositiveButton(AboutActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {

                            //获取评论内容
                            comment = comments.getText().toString();
                            comment= java.net.URLEncoder.encode("客户端问题反馈:" + "\n" +comment);
                            sendRequestWithHttpClient();
                            
                        }
                    });
                builder.show();
                break;
            default:
                break;
        }
    }
    //根据返回码判断反馈是否发送成功
    private Handler handler =new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what){
                case Success:
                    Toast.makeText(AboutActivity.this,AboutActivity.this.getResources().getText(R.string.comment_success),Toast.LENGTH_SHORT).show();
                    break;
                case Fail:
                    Toast.makeText(AboutActivity.this,AboutActivity.this.getResources().getText(R.string.comment_fail),Toast.LENGTH_SHORT).show();
                    break;
                case Repeat:
                    Toast.makeText(AboutActivity.this,AboutActivity.this.getResources().getText(R.string.comment_repeat),Toast.LENGTH_SHORT).show();
                    break;
                case HaveUpdate:
                    AlertDialog.Builder builder= new AlertDialog.Builder(AboutActivity.this);
                    View detailView = LayoutInflater.from(AboutActivity.this).inflate(R.layout.update, null);
                    builder.setTitle(AboutActivity.this.getResources().getText(R.string.update));
                    builder.setView(detailView);
                    builder.show();        
                    break;
                case Latest:
                    Toast.makeText(AboutActivity.this, AboutActivity.this.getResources().getText(R.string.callback), Toast.LENGTH_SHORT).show();
                    break;
                default:
                break;                
            }
        }
        
    };
    
    //将反馈信息通过线程以评论方式发送给wordpress
    private void sendRequestWithHttpClient(){
        
        
        new Thread(new Runnable() {
                @Override
                public void run()
                {
                    HttpURLConnection connection = null;
                    String url_path = "http://oudezhinu.site/wp-comments-post.php";
                    Integer statusCode =0;
                    try
                    {
                        //使用url加载路径
                        URL url = new URL(url_path);
                        //创建HttpURLConnection对象
                        connection = (HttpURLConnection)url.openConnection();                    
                        //设置头域
                        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                        //设置超时
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);
                        //使用POST方法
                        connection.setRequestMethod("POST");
                        //使用输入流
                        connection.setDoInput(false);
                        //使用输出流
                        connection.setDoOutput(true);
                        //Post方式不能缓存,需手动设置为false
                        connection.setUseCaches(false);
                        //请求的body体                    
                        String data = "comment="+comment+"&author="+name_encode+"&email=" + email_encode + "&url=&submit=%E5%8F%91%E8%A1%A8%E8%AF%84%E8%AE%BA&comment_post_ID=890&comment_parent=0";
                        Log.d("AboutActivity",data);
                        //设置输出流，写body体
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(data);
                        //outputStream.flush();
                        //获取返回码
                        statusCode = connection.getResponseCode();                   
                        //使用handle处理结果
                        Log.d("AboutActivity","返回码："+statusCode.toString());             
                                                
                        //关闭流
                        outputStream.close();

                        //关闭连接
                        connection.disconnect();
                                                                
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }finally{
                        //根据返回码返回不同的msg
                        switch(statusCode){
                            case 200:
                                Message message1 = new Message();  
                                message1.what = Success;                        
                                handler.sendMessage(message1); 
                                break;
                            case 409:
                                Message message2 = new Message();  
                                message2.what = Repeat;                        
                                handler.sendMessage(message2);
                                break;
                            default:
                                Message message = new Message();  
                                message.what = Fail;                        
                                handler.sendMessage(message);
                                break;                         
                        }            
                    }
                }
            }).start();                   
        
    }
    //获取当前应用版本名称
    public String getVersionName(Context ctx){
        
        String appVersioName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersioName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersioName;
    }
    
    //获取当前应用版本号
    public Integer getVersionCode(Context ctx){

        Integer appVersioCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersioCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersioCode;
    }
    
    //获取更新地址
    public void update(){
        final updateInfo  response = new updateInfo();
        OkHttpClient client = new OkHttpClient();
        //使用eequest，并指定GET方法
        Request request = new Request.Builder().get().url(UPDATE_URL).build();
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback(){

                @Override
                public void onFailure(Call p1, IOException p2)
                {
                    
                }

                @Override
                public void onResponse(Call p1, Response p2) throws IOException
                {
                    final String body = p2.body().string();
                    try
                    {
                        //获取服务器json文件并解析                
                        JSONObject object = new JSONObject(body);                 
                        response.setAppName(object.getString("appname"));
                        response.setVersion(object.getInt("version"));
                        response.setDescribe(object.getString("describe"));
                        response.setUrl(object.getString("url"));
                        response.setPwd(object.getString("pwd"));
                        //跳出弹出并提示更新
                        Log.d("AboutActivity","版本号"+response.getVersion());
                        if(getVersionCode(AboutActivity.this) < response.getVersion()){
                            Message message = new Message();  
                            message.what = HaveUpdate;                        
                            handler.sendMessage(message);
                        }else{
                            Message message1 = new Message();  
                            message1.what = Latest;                        
                            handler.sendMessage(message1);
                        }
                        
                    }
                    catch (JSONException e)
                    {}
                                      
                }
            });
            
       
    }
    
}
