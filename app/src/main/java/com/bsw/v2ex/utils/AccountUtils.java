package com.bsw.v2ex.utils;

import android.content.Context;

import com.bsw.v2ex.Application;
import com.bsw.v2ex.api.HttpRequestHandler;
import com.bsw.v2ex.api.V2EXManager;
import com.bsw.v2ex.model.NodeModel;
import com.bsw.v2ex.model.PersistenceHelper;
import com.bsw.v2ex.model.ProfileModel;

import java.util.ArrayList;
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

    /**
     * 刷新登陆用户资料
     *
     * @param context
     */
    public static void refreshProfile(final Context context) {
        V2EXManager.getProfile(context, new HttpRequestHandler<ProfileModel>() {
            @Override
            public void onSuccess(ProfileModel data) {
                writeLoginMember(context, data, true);
            }

            @Override
            public void onSuccess(ProfileModel data, int totalPages, int currentPage) {
                onSuccess(data);
            }

            @Override
            public void onFailure(String error) {

            }
        }, true);

    }

    public static void refreshFavoriteNodes(final Context context, final OnAccountFavoriteNodesListener listener) {
        V2EXManager.getFavoriteNodes(context, new HttpRequestHandler<ArrayList<NodeModel>>() {
            @Override
            public void onSuccess(ArrayList<NodeModel> data) {
                writeFavoriteNodes(context,data);
                if (listener != null)
                    listener.onAccountFavoriteNodes(data);
            }

            @Override
            public void onSuccess(ArrayList<NodeModel> data, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


    public static interface OnAccountFavoriteNodesListener {
        void onAccountFavoriteNodes(ArrayList<NodeModel> nodes);
    }

    /**
     * 保存节点收藏信息
     *
     * @param context
     * @param nodeModels
     */
    public static void writeFavoriteNodes(Context context, ArrayList<NodeModel> nodeModels) {
        PersistenceHelper.saveObject(context, nodeModels, key_fav_nodes);
        for (NodeModel node : nodeModels) {
            Application.getDataSource().favoriteNode(node.name, true);
        }
    }

    /**
     * 保存登录用户资料
     *
     * @param context
     * @param profile
     * @param broadcast
     */
    public static void writeLoginMember(Context context, ProfileModel profile, boolean broadcast) {
        PersistenceHelper.saveModel(context, profile, key_login_member);
        if (broadcast) { //通知所有页面,登录成功,更新用户信息
            for (OnAccountListener listener : listeners) {
                listener.onLogin(profile);
            }
        }
    }


}
