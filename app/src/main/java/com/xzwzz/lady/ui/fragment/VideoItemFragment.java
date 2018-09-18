package com.xzwzz.lady.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.bean.AdListBean;
import com.xzwzz.lady.bean.AvVideoListBean;
import com.xzwzz.lady.ui.AvDetailActivity;
import com.xzwzz.lady.ui.VideoDetailActivity;
import com.xzwzz.lady.ui.adapter.AvAdapter;
import com.xzwzz.lady.utils.MyImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyuan on 2018/9/17.
 */

public class VideoItemFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, OnBannerClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recycler;
    private SwipeRefreshLayout refresh;
    private List<AvVideoListBean> list = new ArrayList<>();
    private AvAdapter adapter;
    private String id;
    private Banner mBanner;

    private List<String> bannerList = new ArrayList<>();
    private View headView;
    private List<AdListBean> adListBeans;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_av_item;
    }

    @Override
    public void initView(View view) {
        id = getArguments().getString("id");
        Log.e("gy", "id:" + id);

        headView = View.inflate(getActivity(), R.layout.view_header_home, null);
        mBanner = headView.findViewById(R.id.banner);
        recycler = view.findViewById(R.id.recycler);
        refresh = view.findViewById(R.id.refresh);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                int offest = SizeUtils.dp2px(6f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest / 2, 0);
                } else if (position % 2 == 2) {
                    outRect.set(offest / 2, offest, 0, 0);
                }
            }
        });

        adapter = new AvAdapter(R.layout.item_av, list);
        recycler.setAdapter(adapter);
        adapter.addHeaderView(headView);

        adapter.setOnItemClickListener(this);
        refresh.setOnRefreshListener(this);
        mBanner.setOnBannerClickListener(this);

    }

    @Override
    public void initData() {
        getVideoList();
        getBanner();
    }

    public void getVideoList() {
        RetrofitClient.getInstance().createApi().videoList2("Home.MoviesLinkList", id).compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AvVideoListBean>(refresh) {
                    @Override
                    protected void onHandleSuccess(List<AvVideoListBean> avList) {
                        if (avList == null || avList.size() == 0) return;
                        list.clear();
                        list.addAll(avList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        AvVideoListBean bean = list.get(position);
        toActivity(bean);
    }

    private void toActivity(AvVideoListBean bean) {
        Bundle bundle = new Bundle();
        bundle.putString("title", bean.getTitle());
        bundle.putString("url", bean.getVideo_url());
        bundle.putString("id", bean.getId());
        bundle.putSerializable("type", "1");
        ActivityUtils.startActivity(bundle, VideoDetailActivity.class);
    }


    private void getBanner() {
        RetrofitClient.getInstance().createApi().adsList("Home.coin_adsList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AdListBean>() {

                    @Override
                    protected void onHandleSuccess(List<AdListBean> list) {
                        adListBeans = list;
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

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBeans.get(position - 1).getUrl()) || !adListBeans.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBeans.get(position - 1).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getBanner();
        getVideoList();
    }
}
