package com.bsw.v2ex.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bsw.v2ex.model.ProfileModel;
import com.bsw.v2ex.ui.BaseActivity;
import com.bsw.v2ex.utils.AccountUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class BaseFragment extends Fragment implements AccountUtils.OnAccountListener {
    protected boolean mIsLogin;
    protected ProfileModel mLoginProfile;
    protected BackHandledInterface mBackHandledInterface;

    public static interface BackHandledInterface {
        public abstract void setSelectedFragment(BaseFragment selectedFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = AccountUtils.isLogined(getActivity());
        if (mIsLogin) {
            mLoginProfile = AccountUtils.readLoginMember(getActivity());
        }
        AccountUtils.registerAccountListener(this);
        mBackHandledInterface = (BackHandledInterface) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackHandledInterface.setSelectedFragment(this);//告诉FragmentActivity，当前Fragment在栈顶
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.toString());
    }

    @Override
    public void onDestroy() {
        AccountUtils.unregisterAccountListener(this);
        super.onDestroy();
    }

    @Override
    public void onLogout() {
        mIsLogin = false;
    }

    @Override
    public void onLogin(ProfileModel member) {
        mIsLogin = true;
        mLoginProfile = member;
    }

    public boolean onBackPressed() {
        return false;
    }

    public final BaseActivity getBaseActivity() {
        return (BaseActivity) super.getActivity();
    }

    public final void showProgress(int messageId) {
        getBaseActivity().showProgressBar(messageId);
    }

    public final void showProgress(boolean show) {
        getBaseActivity().showProgressBar(show);
    }

}
