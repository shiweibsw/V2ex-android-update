package com.bsw.v2ex;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * Created by baishiwei on 2016/3/25.
 */
public class AppConfig {
    private final static String APP_CONFIG = "config";
    public final static String CONF_NOIMAGE_NOWIFI = "perf_noimage_nowif";
    public final static String CONF_USE_HTTPS = "perf_https";
    public final static String CONF_EFFECT = "perf_list_effect";
    public final static String CONF_JSONAPI = "perf_jsonapi";
    public final static String CONF_MESSAGE = "perf_message_push";
    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    public String get(String key) {
        Properties props = getProps();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties getProps() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);//将会在/data/data/com.bsw.v2ex/目录下创建config文件夹
            fis = new FileInputStream(dirConf.getPath() + File.separator + APP_CONFIG);//File.separator系统默认分隔符，跨平台需要，这是一种良好的编程习惯
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }
}
