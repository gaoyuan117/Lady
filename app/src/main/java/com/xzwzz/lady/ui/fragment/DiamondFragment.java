package com.xzwzz.lady.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.bean.AdBean;
import com.xzwzz.lady.bean.AdListBean;
import com.xzwzz.lady.bean.MoviesLinkListBean;
import com.xzwzz.lady.ui.VideoDetailActivity;
import com.xzwzz.lady.ui.adapter.DiamondAdapter;
import com.xzwzz.lady.utils.MyImageLoader;
import com.xzwzz.lady.utils.PayUtils;
import com.xzwzz.lady.utils.StatusBarUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

public class DiamondFragment extends BaseFragment implements OnBannerClickListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private Toolbar mToolbar;
    private Banner mBanner;
    private List<String> bannerList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private android.widget.TextView mTvTitle;
    private DiamondAdapter adapter;
    private List<MoviesLinkListBean> list = new ArrayList<>();
    private List<AdListBean> adListBean;
    private LinearLayout layout;

    @Override
    public int getLayoutId() {
        return R.layout.fragmnet_diamond;
    }

    @Override
    public void initView(View view) {

        View headView = View.inflate(getActivity(), R.layout.head_diamond, null);
        mToolbar = view.findViewById(R.id.toolbar);
        mBanner = headView.findViewById(R.id.banner);
        recyclerView = view.findViewById(R.id.recycler);
        refresh = view.findViewById(R.id.refresh);
        mBanner.setOnBannerClickListener(this);
        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);
        mTvTitle = headView.findViewById(R.id.tv_title);
        layout = headView.findViewById(R.id.layout);
        headView.findViewById(R.id.img_close).setOnClickListener(v -> layout.setVisibility(View.GONE));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new DiamondAdapter(R.layout.item_diamond, list);
        adapter.addHeaderView(headView);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        adapter.setOnItemChildClickListener(this);
    }

    @Override
    public void initData() {
        getBanner();
        getAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMoviesLinkList();
    }

    @Override
    protected void setListener() {
        refresh.setOnRefreshListener(this);
    }

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBean.get(position - 1).getUrl()) || !adListBean.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBean.get(position - 1).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getBanner();
        getMoviesLinkList();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
       toActivity(position);
    }

    private void toActivity(int position){
        Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
    }

    //获取广告
    private void getAd() {
        RetrofitClient.getInstance().createApi().getAd().compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AdBean>() {
                    @Override
                    protected void onHandleSuccess(List<AdBean> list) {
                        if (list.size() > 0) {
                            mTvTitle.setText(list.get(0).content);
                            mTvTitle.setSelected(true);
                        }
                    }
                });
    }

    private void getBanner() {
        RetrofitClient.getInstance().createApi().adsList("Home.coin_adsList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AdListBean>() {
                    @Override
                    protected void onHandleSuccess(List<AdListBean> list) {
                        adListBean = list;
                        if (list == null || list.size() == 0) return;
                        bannerList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            bannerList.add(list.get(i).getThumb());
                        }
                        setBanner();
                    }
                });
    }

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

    private void getMoviesLinkList() {
        RetrofitClient.getInstance().createApi().moviesLinkList("Home.MoviesLinkList", AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<MoviesLinkListBean>(refresh) {
                    @Override
                    protected void onHandleSuccess(List<MoviesLinkListBean> moviesLinkList) {
                        if (moviesLinkList == null || moviesLinkList.size() == 0) return;

                        list.clear();
                        list.addAll(moviesLinkList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        noBuyDialog(position);
    }

    private void noBuyDialog(int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage("还未购买该视频,是否花费" + list.get(position).getCoin() + "钻石购买")
                .setPositiveButton("购买", (dialog, which) -> {
                    buy(position);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }

    @SuppressLint("CheckResult")
    private void buy(int position) {
        RetrofitClient.getInstance().createApi().buyVideo("Home.buyvideo", AppContext.getInstance().getLoginUid(), list.get(position).getId())
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                    if (httpResult.ret == 200) {
                        if (httpResult.data.code == 0) {
                            ToastUtils.showShort("购买成功");
                            getMoviesLinkList();
                            toActivity(position);
                        } else {
                            PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "钻石区", "新用户免费观看5部影片", 3, AppContext.zsChargeList);
                        }
                    }
                });
    }
}
