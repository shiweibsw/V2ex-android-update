package com.bsw.v2ex.utils;

import android.content.Context;

import com.bsw.v2ex.model.PersistenceHelper;
import com.bsw.v2ex.model.ProfileModel;

import java.util.HashSet;

/**
 * 登录帐号管理
 * Created by baishiwei on 2016/3/25.
 */
public class AccountUtils {
    public static final int REQUEST_LOGIN = 0;
    private static final String key_login_member = "logined@profile";
    private static final String key_fav_nodes = "logined@fav_nodes";

    /**
     * 帐号登陆登出监听接口
     */
    public static interface OnAccountListener {
        public abstract void onLogout();

        public abstract void onLogin(ProfileModel member);
    }

    /**
     * 接口集合
     */
    private static HashSet<OnAccountListener> listeners = new HashSet<OnAccountListener>();

    /**
     * 注册登录接口
     *
     * @param listener
     */
    public static void registerAccountListener(OnAccountListener listener) {
        listeners.add(listener);
    }

    /**
     * 取消登陆接口
     *
     * @param listener
     */
    public static void unregisterAccountListener(OnAccountListener listener) {
        listeners.remove(listener);
    }

    /**
     * 用户是否已经登录
     *
     * @param context
     * @return
     */
    public static boolean isLogined(Context context) {
        return FileUtils.isExistDataCache(context, key_login_member);
    }

    /**
     * 获取登录用户信息
     *
     * @param context
     * @return
     */
    public static ProfileModel readLoginMember(Context context) {
        return PersistenceHelper.loadModel(context, key_login_member);
    }


}
