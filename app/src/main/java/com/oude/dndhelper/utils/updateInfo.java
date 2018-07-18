package com.oude.dndhelper.utils;

public class updateInfo
{
    private String appname;
    private Integer version;
    private String describe;
    private String url;
    private String pwd;
    public updateInfo(){
        
    }
    public updateInfo(String appname,Integer version,String describe,String url,String pwd){
        this.appname = appname;
        this.version = version;
        this.describe = describe;
        this.url = url;
        this.pwd = pwd;
    }
    
    public String getAppName(){
        return appname;
    }
    
    public void setAppName(String appname){
        this.appname = appname;        
    }
    
    public Integer getVersion(){
        return version;
    }

    public void setVersion(Integer version){
        this.version = version;        
    }
    
    public String getDescribe(){
        return describe;
    }

    public void setDescribe(String describe){
        this.describe = describe;        
    }
    
    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;        
    }
    
    public String getPwd(){
        return pwd;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;        
    }
    
    
    
}
