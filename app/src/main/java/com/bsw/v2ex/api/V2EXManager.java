package com.bsw.v2ex.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.bsw.v2ex.Application;
import com.bsw.v2ex.model.NodeModel;
import com.bsw.v2ex.model.PersistenceHelper;
import com.bsw.v2ex.model.ProfileModel;
import com.bsw.v2ex.model.TopicListModel;
import com.bsw.v2ex.model.TopicModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class V2EXManager {
    public static final String TAG = "V2EXManager";
    public static final boolean DEBUG = true;
    private static Application mApp = Application.getInstance();
    private static AsyncHttpClient sClient = null;

    private static final String HTTP_API_URL = "http://www.v2ex.com/api";
    private static final String HTTPS_API_URL = "https://www.v2ex.com/api";
    public static final String HTTP_BASE_URL = "http://www.v2ex.com";
    public static final String HTTPS_BASE_URL = "https://www.v2ex.com";

    private static final String API_LATEST = "/topics/latest.json";//最新话题
    private static final String API_HOT = "/topics/hot.json";//最热话题
    private static final String API_ALL_NODE = "/nodes/all.json";//节点列表
    private static final String API_REPLIES = "/replies/show.json";
    private static final String API_TOPIC = "/topics/show.json";
    private static final String API_USER = "/members/show.json";

    /**
     * 登录和注册使用https的方式
     */
    public static final String SIGN_UP_URL = HTTPS_BASE_URL + "/signup";//注册
    public static final String SIGN_IN_URL = HTTPS_BASE_URL + "/signin";//登录

    public static String getBaseAPIUrl() {
        return mApp.isHttpsFromCache() ? HTTPS_API_URL : HTTP_API_URL;
    }

    public static String getBaseUrl() {
        return mApp.isHttpsFromCache() ? HTTPS_BASE_URL : HTTP_BASE_URL;
    }


    public static void getProfile(final Context context, final HttpRequestHandler<ProfileModel> handler, boolean direct) {
        getClient(context, false).get(getBaseUrl() + (direct ? "/my/nodes" : ""), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                SafeHandler.onFailure(handler, V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorGetProfileFailure));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                new AsyncTask<Void, Void, ProfileModel>() {
                    ProfileModel profile = new ProfileModel();

                    @Override
                    protected ProfileModel doInBackground(Void... params) {
                        try {
                            if (DEBUG) {
                                Log.e(TAG, "---getProfile-----responseBody:" + responseString);
                            }
                            profile.parse(responseString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return profile;
                    }

                    @Override
                    protected void onPostExecute(ProfileModel profile) {
                        if (profile != null) {
                            SafeHandler.onSuccess(handler, profile);
                        } else {
                            SafeHandler.onFailure(handler, V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorGetProfileFailure));
                        }
                    }
                }.execute();
            }
        });

    }

    public static void getFavoriteNodes(final Context context, final HttpRequestHandler<ArrayList<NodeModel>> handler) {
        getClient(context).get(getBaseUrl() + "/my/nodes", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                SafeHandler.onFailure(handler, V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorFavNodeFailure));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (DEBUG) {
                    Log.e(TAG, "---getProfile-----responseBody:" + responseString);
                }
                ArrayList<NodeModel> nodeModels = getNodeModelsFromResponse(responseString);
                SafeHandler.onSuccess(handler, nodeModels);
            }
        });
    }

    /**
     * 获取收藏的话题
     *
     * @param context
     * @param handler
     */
    public static void getMyFavoriteTopics(Context context, int page, boolean refresh, HttpRequestHandler<ArrayList<TopicModel>> handler) {
        getCategoryTopics(context, getBaseUrl() + "/my/topics?p=" + page, refresh, handler);
    }

    /**
     * 获取首页分类话题列表 (包括技术,创意,好玩,Apple,酷工作,交易,城市,问与答,R2)
     *
     * @param context
     * @param urlString
     * @param refresh
     * @param handler
     */
    public static void getCategoryTopics(final Context context, final String urlString, boolean refresh, final HttpRequestHandler<ArrayList<TopicModel>> handler) {
        final String key = Uri.parse(urlString).getEncodedQuery();
        if (!refresh) { //不刷新即是直接从缓存中加载
            ArrayList<TopicModel> topics = PersistenceHelper.loadModelList(context, key);
            if (topics != null && topics.size() > 0) {
                SafeHandler.onSuccess(handler, topics);
                return;
            }
        }
        final AsyncHttpClient client = getClient(context);
        client.addHeader("Referer", getBaseUrl());
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.get(urlString, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                SafeHandler.onFailure(handler, V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorGetTopicListFailure));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                if (DEBUG) {
                    Log.e(TAG, "---getCategoryTopics-----responseBody:" + responseString);
                }
                new AsyncTask<Void, Void, TopicListModel>() {

                    @Override
                    protected TopicListModel doInBackground(Void... params) {
                        TopicListModel topics = new TopicListModel();
                        try {
                            topics.parse(responseString);
                            if (topics.size() > 0 && topics.mCurrentPage == 1) {
                                PersistenceHelper.saveModelList(context, topics, key);
                            }
                            return topics;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(TopicListModel topics) {
                        if (topics != null) {
                            SafeHandler.onSuccess(handler, topics, topics.mTotalPage, topics.mCurrentPage);
                        } else {
                            SafeHandler.onFailure(handler, V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorGetTopicListFailure));
                        }
                    }
                }.execute();
            }
        });
    }

    /**
     * 获取最新话题
     *
     * @param context
     * @param refersh
     * @param handler
     */
    public static void getLatestTopics(Context context, boolean refersh, HttpRequestHandler<ArrayList<TopicModel>> handler) {
        getTopics(context, getBaseAPIUrl() + API_LATEST, refersh, handler);
    }

    /**
     * 获取最热话题
     *
     * @param context
     * @param refersh
     * @param handler
     */
    public static void getHotTopics(Context context, boolean refersh, HttpRequestHandler<ArrayList<TopicModel>> handler) {
        getTopics(context, getBaseAPIUrl() + API_HOT, refersh, handler);
    }

    public static void getTopicsByTab(Context context, String tab, boolean refresh, final HttpRequestHandler<ArrayList<TopicModel>> handler) {
        getCategoryTopics(context, getBaseUrl() + "/?tab=" + tab, refresh, handler);
    }


    /**
     * 获取各类话题列表
     *
     * @param context
     * @param urlString
     * @param refersh
     * @param handler
     */
    public static void getTopics(Context context, String urlString, boolean refersh, HttpRequestHandler<ArrayList<TopicModel>> handler) {
        Uri uri = Uri.parse(urlString);
        String path = uri.getLastPathSegment();
        String param = uri.getEncodedQuery();
        String key = path;
        if (param != null) {
            key += param;
        }
        if (!refersh) {
            ArrayList<TopicModel> topics = PersistenceHelper.loadModelList(context, key);
            if (topics != null && topics.size() > 0) {
                SafeHandler.onSuccess(handler, topics);
                return;
            }
        }
        new AsyncHttpClient().get(context, urlString, new WrappedJsonHttpResponseHandler<TopicModel>(context, TopicModel.class, key, handler));

    }

    /**
     * 获取所有节点
     *
     * @param context
     * @param refresh
     * @param handler
     */
    public static void getAllNodes(Context context, boolean refresh, HttpRequestHandler<ArrayList<NodeModel>> handler) {
        final String key = "allnodes";
        if (!refresh) {
            ArrayList<NodeModel> nodes = PersistenceHelper.loadModelList(context, key);
            if (nodes != null && nodes.size() > 0) {
                SafeHandler.onSuccess(handler, nodes);
                return;
            }
        }
        new AsyncHttpClient().get(context, getBaseAPIUrl() + API_ALL_NODE, new WrappedJsonHttpResponseHandler<NodeModel>(context, NodeModel.class, key, handler));
    }


    private static AsyncHttpClient getClient(Context context) {
        return getClient(context, true);
    }

    private static AsyncHttpClient getClient(Context context, boolean mobile) {
        if (context == null) {
            context = mApp.getBaseContext();
        }
        if (sClient == null) {
            sClient = new AsyncHttpClient();
            sClient.setEnableRedirects(true);
            sClient.setCookieStore(new PersistentCookieStore(context));
            sClient.addHeader("Cache-Control", "max-age=0");
            sClient.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            sClient.addHeader("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
            sClient.addHeader("Accept-Language", "zh-CN, en-US");
            sClient.addHeader("Host", "www.v2ex.com");
        }
        if (mobile) {
            sClient.addHeader("X-Requested-With", "com.android.browser");
            sClient.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.2.1; en-us; M040 Build/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        } else {
            sClient.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        }
        return sClient;
    }


    private static ArrayList<NodeModel> getNodeModelsFromResponse(String content) {
        Pattern pattern = Pattern.compile("<a class=\"grid_item\" href=\"/go/([^\"]+)\" id=([^>]+)><div([^>]+)><img src=\"([^\"]+)([^>]+)><([^>]+)></div>([^<]+)");
        Matcher matcher = pattern.matcher(content);
        ArrayList<NodeModel> collections = new ArrayList<NodeModel>();
        while (matcher.find()) {
            NodeModel node = new NodeModel();
            node.name = matcher.group(1);
            node.title = matcher.group(7);
            node.url = matcher.group(4);
            if (node.url.startsWith("//"))
                node.url = "http:" + node.url;
            else
                node.url = HTTP_BASE_URL + node.url;
            collections.add(node);
        }
        return collections;
    }

}
