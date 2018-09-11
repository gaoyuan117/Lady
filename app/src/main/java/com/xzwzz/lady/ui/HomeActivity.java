package com.xzwzz.lady.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.PayDialogBean;
import com.xzwzz.lady.bean.TextAdBean;
import com.xzwzz.lady.bean.UserInfoBean;
import com.xzwzz.lady.ui.adapter.ViewPagerAdapter;
import com.xzwzz.lady.ui.fragment.AVHomeFragment;
import com.xzwzz.lady.ui.fragment.AvFragment;
import com.xzwzz.lady.ui.fragment.DiamondFragment;
import com.xzwzz.lady.ui.fragment.HomeFragment;
import com.xzwzz.lady.ui.fragment.MineFragment;
import com.xzwzz.lady.utils.DialogHelp;
import com.xzwzz.lady.utils.UpdateManager;
import com.xzwzz.lady.view.AlphaTabView;
import com.xzwzz.lady.view.AlphaTabsIndicator;
import com.xzwzz.lady.view.OnTabChangedListner;
import com.xzwzz.lady.widget.NoScrollViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements OnTabChangedListner {

    NoScrollViewPager viewPager;
    AlphaTabsIndicator mAlphaTabsIndicator;
    AlphaTabView tabMine;

    private List<Fragment> mList;
    private ViewPagerAdapter adapter;
    private long firstTime = 0;

    private final static String[] AUTH_BASE_ARR =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA
                    , Manifest.permission.READ_PHONE_STATE};
    private final static int AUTH_BASE_REQUEST_CODE = 1;
    private final static int AUTH_COM_REQUEST_CODE = 2;
    private int current = 0;
    private SimpleDateFormat format;

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_home;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void initView() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        viewPager = findViewById(R.id.view_pager);
        mAlphaTabsIndicator = findViewById(R.id.alphaIndicator);
        tabMine = findViewById(R.id.tab_mine);
    }

    @Override
    public void initData() {
        viewPager = findViewById(R.id.view_pager);
        mAlphaTabsIndicator = findViewById(R.id.alphaIndicator);
        viewPager.setOffscreenPageLimit(4);
        mList = new ArrayList<>();
        mList.add(new AVHomeFragment());
        mList.add(new DiamondFragment());
        mList.add(new AvFragment());
        mList.add(new MineFragment());
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, mList);
        viewPager.setAdapter(adapter);

        mAlphaTabsIndicator.setViewPager(viewPager);
        mAlphaTabsIndicator.setOnTabChangedListner(this);
        mAlphaTabsIndicator.removeAllBadge();

        String type = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(type) && type.equals("message")) {
            if (viewPager == null || mAlphaTabsIndicator == null) {
                return;
            }
            viewPager.setCurrentItem(1);
            mAlphaTabsIndicator.setTabCurrenItem(1);
        }
        init();
        check();
        getChargeRule();
        getAvChargeRule();
        getCoinChargeRule();
        getTextAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMember();
    }

    @Override
    public void onTabSelected(int tabNum) {
        viewPager.setCurrentItem(tabNum);
        current = tabNum;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra("exit", false);
            if (isExit) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            ToastUtils.showShort("再按一次退出程序");
            firstTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUTH_BASE_REQUEST_CODE) {
            for (int ret : grantResults) {
                Log.d("GuideActivity", "ret:" + ret);
                if (ret == 0) {
                    continue;
                } else {
//                    ToastUtils.showShort("缺少导航基本的权限");
                    return;
                }
            }
            init();
        } else if (requestCode == AUTH_COM_REQUEST_CODE) {
            ToastUtils.showShort("初始化完毕");
        }
    }

    private void init() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(AUTH_BASE_ARR, AUTH_BASE_REQUEST_CODE);
                return;
            }
        }
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : AUTH_BASE_ARR) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void check() {
        if (AppConfig.MAINTAIN_SWITCC == 1) {
            String maintain_tips = AppConfig.maintain_tips;
            try {
                DialogHelp.showMainTainDialog(HomeActivity.this, maintain_tips);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new UpdateManager(HomeActivity.this, AppConfig.apk_ver, AppConfig.apk_url, false).checkUpdate();
    }

    //是否为会员
    private void isMember() {
        String loginUid = AppContext.getInstance().getLoginUid();
        if (loginUid.equals("0")) return;
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                            AppConfig.AVMEMBER = (bean.is_av_member == 1);

                        }
                    }
                });
    }

    private void getChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getChargeRule")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;

                        AppContext.zbChargeList.clear();
                        AppContext.zbChargeList.addAll(list);
                    }
                });
    }

    private void getAvChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getAvChargeRule")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;

                        AppContext.avChargeList.clear();
                        AppContext.avChargeList.addAll(list);
                    }
                });
    }

    private void getCoinChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getCoinChargeRule")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;

                        AppContext.zsChargeList.clear();
                        AppContext.zsChargeList.addAll(list);
                    }
                });
    }

    private void getTextAd() {
        RetrofitClient.getInstance().createApi().textAd("Home.avList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<TextAdBean>() {
                    @Override
                    protected void onHandleSuccess(List<TextAdBean> list) {
                        AppContext.textAdBean = list.get(0);
                    }
                });
    }
}
