package com.bsw.v2ex.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bsw.v2ex.R;
import com.bsw.v2ex.ui.adapter.AggregateTopicsAdapter;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class ViewPagerFragment extends BaseFragment {
    public static final int TypeViewPager_Aggregation = 0;  //首页Tab
    public static final int TypeViewPager_Favorite = 1;     //节点收藏

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;

    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.pager_tabstrip);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mType = getArguments().getInt("type");
        if (mType == TypeViewPager_Favorite) {

        } else {
            mViewPager.setAdapter(new AggregateTopicsAdapter(getChildFragmentManager(), getActivity()));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        }
    }
}
