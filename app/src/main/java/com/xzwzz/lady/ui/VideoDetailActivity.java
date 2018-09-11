package com.xzwzz.lady.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseObjObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.DiamondAdBean;
import com.xzwzz.lady.bean.VideoDetailBean;
import com.xzwzz.lady.module.video.VideoPlayActivity;
import com.xzwzz.lady.ui.adapter.VideoDetailAdapter;
import com.xzwzz.lady.utils.GlideUtils;
import com.xzwzz.lady.utils.PayUtils;
import com.xzwzz.lady.widget.XzwzzPlayer;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;

public class VideoDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private LinearLayout layoutTips;
    private ImageView videoImg;
    private ImageView imgAd;
    private TextView tvTitle;
    private TextView tvCount;
    private RecyclerView recyclerView;

    private String id;
    private List<VideoDetailBean.ListBean> list = new ArrayList<>();
    private VideoDetailAdapter adapter;
    private DiamondAdBean adBean;
    private boolean vip = false;
    private VideoDetailBean detailBean;

    private XzwzzPlayer jzVideoPlayerStandard;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        if (title.length() > 8) {
            title = title.substring(0, 8);
        }
        setToolbar(title, true);

        layoutTips = findViewById(R.id.layout_tips);
        videoImg = findViewById(R.id.img_diamond);
        imgAd = findViewById(R.id.img_ad);
        tvTitle = findViewById(R.id.tv_diamond_title);
        tvCount = findViewById(R.id.tv_diamond);
        recyclerView = findViewById(R.id.recycler);
        layoutTips.setVisibility(View.GONE);
        imgAd.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        adapter = new VideoDetailAdapter(R.layout.item_video_detail, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        ad();
        videoImg.setOnClickListener(v -> toActivity());
        findViewById(R.id.close).setOnClickListener(v -> layoutTips.setVisibility(View.GONE));
        imgAd.setOnClickListener(v -> toBrower());

        jzVideoPlayerStandard = findViewById(R.id.videoplayer);

        jzVideoPlayerStandard.fullscreenButton.setVisibility(View.INVISIBLE);
        jzVideoPlayerStandard.thumbImageView.setVisibility(View.GONE);

        jzVideoPlayerStandard.backButton.setOnClickListener(v -> finish());
        jzVideoPlayerStandard.titleTextView.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        video();
        getFreeNum();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
        finish();
    }

    private void video() {
        RetrofitClient.getInstance().createApi().videoDetail("Home.MoviesLinkDetail", AppContext.getInstance().getLoginUid(), id)
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<VideoDetailBean>() {

                    @Override
                    protected void onHandleSuccess(VideoDetailBean bean) {
                        detailBean = bean;
                        GlideUtils.glide(mContext,bean.getDetails().getVideo_img(),videoImg);
                        tvTitle.setText(bean.getDetails().getTitle());
//                        tvCount.setText(bean.getDetails().getCoin() + "");

                        list.clear();

                        if (bean.getList() == null || bean.getList().size() == 0) return;

                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    private void ad() {
        RetrofitClient.getInstance().createApi().diamondAv("Home.avneiye")
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<DiamondAdBean>() {
                    @Override
                    protected void onHandleSuccess(DiamondAdBean bean) {
                        imgAd.setVisibility(View.VISIBLE);
                        adBean = bean;
                        GlideUtils.glide(VideoDetailActivity.this, bean.getThumb(), imgAd);
                    }
                });
    }

    private void toBrower() {
        if (adBean == null) return;
        Uri uri = Uri.parse(adBean.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void getFreeNum() {
        RetrofitClient.getInstance().createApi().getfreenum("Home.getfreenum", AppContext.getInstance().getLoginUid(), "2")
                .compose(RxUtils.io_main())
                .subscribe(bean -> {
                    if (bean.ret == 200) {
                        if (bean.data.code == 0) {
                            vip = true;
                            layoutTips.setVisibility(View.VISIBLE);
                        } else {
                            vip = false;
                            layoutTips.setVisibility(View.GONE);

                        }
                    }
                });
    }


    private void toActivity() {
        if (vip) {
            startActivity();
        } else {
//            if (detailBean.getDetails().getIs_buy() == 0) {
//                noBuyDialog();
//                return;
//            }
            startActivity();
        }

    }

    private void startActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", detailBean.getDetails().getTitle());
        bundle.putSerializable("url", detailBean.getDetails().getVideo_url().get(0));
        bundle.putSerializable("type", "2");
        bundle.putSerializable("id", detailBean.getDetails().getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    private void noBuyDialog() {
//        new AlertDialog.Builder(this)
//                .setMessage("还未购买该视频,是否花费" + detailBean.getDetails().getCoin() + "钻石购买")
//                .setPositiveButton("购买", (dialog, which) -> {
//                    buy();
//                    dialog.dismiss();
//                })
//                .setNegativeButton("取消", (dialog, which) -> {
//                })
//                .show();
    }

    private void buy() {
        RetrofitClient.getInstance().createApi().buyVideo("Home.buyvideo", AppContext.getInstance().getLoginUid(), detailBean.getDetails().getId())
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                    if (httpResult.ret == 200) {
                        if (httpResult.data.code == 0) {
                            ToastUtils.showShort("购买成功");
                            startActivity();
                            video();
                        } else {
                            PayUtils.payDialog(VideoDetailActivity.this, R.mipmap.zb_pay_bg, "钻石区", "新用户免费观看5部影片", 3, AppContext.zsChargeList);
                        }
                    }
                });
    }
}
