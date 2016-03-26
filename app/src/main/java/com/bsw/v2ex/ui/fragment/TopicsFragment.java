package com.bsw.v2ex.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsw.v2ex.Application;
import com.bsw.v2ex.R;
import com.bsw.v2ex.api.HttpRequestHandler;
import com.bsw.v2ex.api.V2EXManager;
import com.bsw.v2ex.model.NodeModel;
import com.bsw.v2ex.model.TopicModel;
import com.bsw.v2ex.ui.adapter.HeaderViewRecyclerAdapter;
import com.bsw.v2ex.ui.adapter.TopicsAdapter;
import com.bsw.v2ex.utils.MessageUtils;
import com.bsw.v2ex.utils.OnScrollToBottomListener;
import com.bsw.v2ex.widget.FootUpdate;
import com.melnykov.fab.FloatingActionButton;
import com.twotoasters.jazzylistview.effects.FlyEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;

/**
 * 显示单个节点下的话题或最新/最热话题类
 * Created by baishiwei on 2016/3/26.
 */
public class TopicsFragment extends BaseFragment implements HttpRequestHandler<ArrayList<TopicModel>>, OnScrollToBottomListener {
    //0表示最新话题,-1表示最热话题,-2表示收藏的话题,-3表示我的特别关注,其他表示节点下的话题
    public static final int LatestTopics = 0;
    public static final int HotTopics = -1;
    public static final int MyFavoriteTopics = -2;
    public static final int MyFollowerTopics = -3;
    public static final int InvalidTopics = -4;

    FloatingActionButton mAddButton;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;

    int mNodeId = InvalidTopics;
    boolean mAttachMain;
    boolean mShowMenu;
    String mNodeName;
    String mTabName;

    int mPage = 1;
    TopicsAdapter mAdapter;
    HeaderViewRecyclerAdapter mHeaderAdapter;

    boolean mNoMore = true;
    boolean mIsLoading;

    NodeModel mNode;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        mAttachMain = args.getBoolean("attach_main", false);
        mNodeId = args.getInt("node_id", InvalidTopics);
        mNodeName = args.getString("node_name", "");
        mShowMenu = args.getBoolean("show_menu", false);
        mTabName = args.getString("tab", "");

        setHasOptionsMenu(mShowMenu);//右上角的各种按钮
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topics, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_topics);
        mAddButton = (FloatingActionButton) rootView.findViewById(R.id.add_topic_button);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (Application.getInstance().isShowEffectFromCache()) {//列表滚动时的动画效果
            JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener();
            mRecyclerView.setOnScrollListener(scrollListener);
            scrollListener.setTransitionEffect(new FlyEffect());
        }
        if (mNodeId > 0 || !mNodeName.isEmpty() && mIsLogin) {
            mAddButton.setVisibility(View.VISIBLE);
            mAddButton.attachToRecyclerView(mRecyclerView);
        } else {
            mAddButton.hide(false);
        }
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUtils.showToast(getActivity(), "测试");
            }
        });
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                // requestTopics(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mAdapter = new TopicsAdapter(getActivity(), this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                requestMoreTopics();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        mSwipeLayout.setRefreshing(true);
        if (args.containsKey("node_name")) {
            mNodeName = args.getString("node_name");
//            requestTopicsByName(false);
        } else if (args.containsKey("node_id")) {
            mNodeId = args.getInt("node_id");
            requestTopicsById(false);
        }

    }

    @Override
    public void onSuccess(ArrayList<TopicModel> data) {
        onSuccess(data, 1, 1);
    }

    @Override
    public void onSuccess(ArrayList<TopicModel> data, int totalPages, int currentPage) {
        mSwipeLayout.setRefreshing(true);
        mIsLoading = false;
        mPage = currentPage;
        mNoMore = totalPages == currentPage;//如果当前页currentPage=总页数totalPages，则mNoMore=false
        if (data.size() == 0)
            return;
        if (mNode == null)
            mNode = data.get(0).node;
        if (!mAttachMain && mNodeName.isEmpty())
            mNodeName = data.get(0).node.name;
        mAdapter.insertAtBack(data, currentPage != 1);
        if (mNoMore) {
            mFootUpdate.dismiss();
        } else {
            mFootUpdate.showLoading();
        }
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(true);
        mIsLoading = false;
        MessageUtils.showErrorMessage(getActivity(), error);
        if (mAdapter.getItemCount() > 0 && !mNoMore) {
            mFootUpdate.showFail();
        } else {
            mFootUpdate.dismiss();
        }

    }

    @Override
    public void onLoadMore() {
        if (!mNoMore && !mIsLoading) {
            requestMoreTopics();
        }
    }

    private void requestMoreTopics() {
        mIsLoading = true;
        if (mNodeId == MyFavoriteTopics) {//收藏的话题
            V2EXManager.getMyFavoriteTopics(getActivity(), mPage + 1, true, this);
        } else if (mNodeId == MyFollowerTopics) {//关注的话题

        } else {

        }
    }

    private void requestTopicsById(boolean refresh) {
        if (mNodeId==LatestTopics){

        }
    }
}
