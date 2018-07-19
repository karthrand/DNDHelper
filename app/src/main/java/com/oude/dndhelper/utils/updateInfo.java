package com.oude.dndhelper.utils;

public class updateInfo
{
    private String version;
    private Integer code;
    private String describe;
    private String url;
    private String pwd;
    public updateInfo(){
        
    }
    public updateInfo(String version,Integer code,String describe,String url,String pwd){
        this.version = version;
        this.code = code;
        this.describe = describe;
        this.url = url;
        this.pwd = pwd;
    }
    
    public String getVersion(){
        return version;
    }
    
    public void setVersion(String version){
        this.version = version;        
    }
    
    public Integer getCode(){
        return code;
    }

    public void setCode(Integer code){
        this.code = code;        
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
