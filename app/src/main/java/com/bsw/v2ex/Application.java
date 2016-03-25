package com.bsw.v2ex;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.bsw.v2ex.database.DatabaseHelper;
import com.bsw.v2ex.database.V2EXDataSource;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;


/**
 * Created by baishiwei on 2016/3/25.
 */
public class Application extends android.app.Application {
    private static Application mContext;
    private static V2EXDataSource mDataSource;

    private boolean mJsonAPI;
    private boolean mHttps;
    private boolean mShowEffect;
    private boolean mLoadImage;
    private boolean mPushMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());//初始化SSL证书相关配置用于使用https加密连接
        mContext = this;//获得Application的引用
        initDatabase();//初始化数据库
        initImageLoader();//初始化ImageLoader
        initAppConfig();//初始化应用相关配置
    }

    private void initDatabase() {
        mDataSource = new V2EXDataSource(DatabaseHelper.getInstacce(mContext));
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(200)).showImageOnLoading(R.drawable.ic_avatar).build();
        File cacheDir;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);//数据将保存到SDCard/Android/data/com.bsw.v2ex/files/目录下
        } else {
            cacheDir = getCacheDir();
        }
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(mContext).threadPoolSize(4).memoryCache(new WeakMemoryCache()).denyCacheImageMultipleSizesInMemory().diskCache(new UnlimitedDiscCache(cacheDir)).defaultDisplayImageOptions(options);
        if (BuildConfig.DEBUG) {
            configBuilder.writeDebugLogs();
        }
        ImageLoader.getInstance().init(configBuilder.build());

    }

    private void initAppConfig() {
        mHttps = isHttps();
        mJsonAPI = isJsonAPI();
        mShowEffect = isShowEffect();
        mLoadImage = isLoadImageInMobileNetwork();
        mPushMessage = isMessagePush();
    }

    /**
     * 是否Https登录
     * 使用到了Properties用来读取本地配置
     *
     * @return
     */
    public boolean isHttps() {
        String perf_https = getProperty(AppConfig.CONF_USE_HTTPS);
        if (TextUtils.isEmpty(perf_https))
            return true;
        else
            return Boolean.parseBoolean(perf_https);
    }

    /**
     * 是否以JsonAPI的形式访问
     *
     * @return
     */
    public boolean isJsonAPI() {
        String perf_json = getProperty(AppConfig.CONF_JSONAPI);
        if (TextUtils.isEmpty(perf_json))
            return false;
        else
            return Boolean.parseBoolean(perf_json);
    }

    /**
     * 是否显示动画效果
     *
     * @return
     */
    public boolean isShowEffect() {
        String perf_effect = getProperty(AppConfig.CONF_EFFECT);
        if (TextUtils.isEmpty(perf_effect))
            return false;
        else
            return Boolean.parseBoolean(perf_effect);
    }

    /**
     * 3G网络下是否加载显示文章图片
     *
     * @return
     */
    public boolean isLoadImageInMobileNetwork() {
        String perf_loadimage = getProperty(AppConfig.CONF_NOIMAGE_NOWIFI);
        if (TextUtils.isEmpty(perf_loadimage))
            return false;
        else
            return Boolean.parseBoolean(perf_loadimage);
    }

    /**
     * 是否消息推送
     *
     * @return
     */
    public boolean isMessagePush() {
        String perf_message = getProperty(AppConfig.CONF_MESSAGE);
        if (TextUtils.isEmpty(perf_message))
            return true;
        else
            return Boolean.parseBoolean(perf_message);
    }


    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }


}
