package com.xzwzz.lady.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.bean.HotBean;
import com.xzwzz.lady.ui.TvActivity;
import com.xzwzz.lady.ui.WebViewActivity;
import com.xzwzz.lady.ui.login.LoginActivity;
import com.xzwzz.lady.utils.LoginUtils;
import com.xzwzz.lady.utils.MemberUtil;
import com.xzwzz.lady.utils.MyImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyuan on 2018/6/7.
 */
public class YingFragment extends BaseFragment implements OnBannerClickListener, View.OnClickListener {
    private android.support.v7.widget.Toolbar mToolbar;
    private Banner mBanner;
    private List<HotBean.BannerBean> hotBeans = new ArrayList<>();
    private List<String> bannerList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ying;
    }

    @Override
    public void initView(View view) {

        mToolbar = view.findViewById(R.id.toolbar);
        mBanner = view.findViewById(R.id.banner);
        mBanner.setOnBannerClickListener(this);
//        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        view.findViewById(R.id.img_ying).setOnClickListener(this);
        view.findViewById(R.id.img_wei).setOnClickListener(this);

    }

    @Override
    public void initData() {
        RetrofitClient.getInstance().createApi().getBanner().compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<HotBean>() {
                    @Override
                    protected void onHandleSuccess(List<HotBean> list) {
                        if (list != null && list.size() > 0) {

                            List<HotBean.BannerBean> slide = list.get(0).slide;
                            hotBeans.clear();
                            hotBeans.addAll(slide);

                            bannerList.clear();
                            for (HotBean.BannerBean bean : slide) {
                                bannerList.add(bean.slide_pic);
                            }

                            setBanner();

                        }
                    }
                });
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void OnBannerClick(int position) {

        if (TextUtils.isEmpty(hotBeans.get(position - 1).slide_url)) return;

        Uri uri = Uri.parse(hotBeans.get(position - 1).slide_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * 设置轮播图
     */
    private void setBanner() {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setImageLoader(new MyImageLoader());
        mBanner.setImages(bannerList);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.isAutoPlay(true);
        mBanner.setViewPagerIsScroll(true);
        mBanner.setDelayTime(3000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_ying://VIP影视

                toActivity(WebViewActivity.class);

                break;
            case R.id.img_wei://卫视直播
                toActivity(TvActivity.class);

                break;
        }
    }


    private void toActivity(Class<?> activity) {
        if (AppConfig.ISVIP == 0) {
            startActivity(new Intent(getActivity(), activity));
            return;
        }

        if (!AppContext.getInstance().isLogin()) {
            ActivityUtils.startActivity(LoginActivity.class);
            return;
        }

        MemberUtil.delayCheckMember(new WeakReference<>(new MemberUtil.MemberListener() {
            @Override
            public void isMemeber() {
                startActivity(new Intent(getActivity(), activity));
            }

            @Override
            public void noMember() {
                LoginUtils.vipDialog(getActivity());
            }
        }));
    }
}
