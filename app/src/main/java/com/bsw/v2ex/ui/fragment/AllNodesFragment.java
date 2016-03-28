package com.bsw.v2ex.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.bsw.v2ex.R;
import com.bsw.v2ex.api.HttpRequestHandler;
import com.bsw.v2ex.api.V2EXManager;
import com.bsw.v2ex.model.NodeModel;
import com.bsw.v2ex.ui.adapter.AllNodesAdapter;
import com.bsw.v2ex.utils.MessageUtils;
import com.bsw.v2ex.widget.IndexableRecyclerView;

import java.util.ArrayList;

/**
 * Created by baishiwei on 2016/3/28.
 */
public class AllNodesFragment extends BaseFragment implements HttpRequestHandler<ArrayList<NodeModel>> {

    private static final String TAG = "AllNodesFragment";
    private IndexableRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private SwipeRefreshLayout mSwipeLayout;
    private AllNodesAdapter mNodeAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_all_nodes, container, false);

        final Context context = getActivity();
        mNodeAdapter = new AllNodesAdapter(context);
        mRecyclerView = (IndexableRecyclerView) layout.findViewById(R.id.grid_all_node);
        mLayoutManger = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mNodeAdapter);
        mRecyclerView.setFastScrollEnabled(true);


        mSwipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNode(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeLayout.setRefreshing(true);
        requestNode(false);
    }

    private void requestNode(boolean refresh) {
        V2EXManager.getAllNodes(getActivity(), refresh, this);
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data) {
        mSwipeLayout.setRefreshing(false);
        mNodeAdapter.update(data);
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data, int totalPages, int currentPage) {
        mSwipeLayout.setRefreshing(false);
        mNodeAdapter.update(data);
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        MessageUtils.showErrorMessage(getActivity(), error);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_nodes, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_nodes_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_nodes_hint));
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNodeAdapter.filterText(newText);
                return false;
            }
        });

    }
}
