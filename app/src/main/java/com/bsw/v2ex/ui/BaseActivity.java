package com.bsw.v2ex.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bsw.v2ex.model.ProfileModel;
import com.bsw.v2ex.ui.fragment.BaseFragment;
import com.bsw.v2ex.utils.AccountUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class BaseActivity extends ActionBarActivity implements AccountUtils.OnAccountListener, BaseFragment.BackHandledInterface {
    private ProgressDialog mProgressDialog;
    protected boolean mIsLogin;
    protected ProfileModel mLoginProfile;
    private BaseFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = AccountUtils.isLogined(this);
        if (mIsLogin)
            mLoginProfile = AccountUtils.readLoginMember(this);
        AccountUtils.registerAccountListener(this);
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

    private void initProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
    }

    public void showProgressBar(boolean show, String message) {
        initProgressBar();
        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    public void showProgressBar(int messageId) {
        String message = getString(messageId);
        showProgressBar(true, message);
    }

    public void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }


    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        AccountUtils.unregisterAccountListener(this);
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.toString());
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.toString());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
