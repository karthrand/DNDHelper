package com.oude.dndhelper.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Locale;
import com.oude.dndhelper.R;

public class LocalManageUtil {

    //获取语言类型
    public static String getSelectLanguage(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return context.getString(R.string.language_cn);
            case 1:
                return context.getString(R.string.language_traditional);
            case 2:
                return context.getString(R.string.language_en);
            case 3:
            default:
                return context.getString(R.string.language_cn);
        }
    }
    
    public static Locale getSetLanguageLocale(Context context) {

        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return Locale.SIMPLIFIED_CHINESE;
            case 1:
                return Locale.TRADITIONAL_CHINESE;
            case 2:
                return Locale.ENGLISH;
            case 3:
            default:
                return Locale.SIMPLIFIED_CHINESE;
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    //设置应用语言类型
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void onConfigurationChanged(Context context){
        setLocal(context);
        setApplicationLanguage(context);
    }
}
