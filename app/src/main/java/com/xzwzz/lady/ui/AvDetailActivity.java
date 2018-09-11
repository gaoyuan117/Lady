package com.xzwzz.lady.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseObjObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.DiamondAdBean;
import com.xzwzz.lady.bean.VideoDetailBean;
import com.xzwzz.lady.module.video.VideoPlayActivity;
import com.xzwzz.lady.ui.adapter.AvDetailAdapter;
import com.xzwzz.lady.utils.GlideUtils;
import com.xzwzz.lady.utils.PayUtils;
import com.xzwzz.lady.widget.XzwzzPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class AvDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private LinearLayout layoutTips;
    private ImageView imgAd;
    private TextView tvTitle;
    private TextView tvWatch;
    private TextView tvTime;
    private RecyclerView recyclerView;

    private String id;
    private List<VideoDetailBean.ListBean> list = new ArrayList<>();
    private AvDetailAdapter adapter;
    private DiamondAdBean adBean;
    private boolean vip = false;
    private VideoDetailBean detailBean;

    private XzwzzPlayer jzVideoPlayerStandard;
    private int a = 0;
    private String[] s ;
    private int num = 1;


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
        imgAd = findViewById(R.id.img_ad);
        tvTitle = findViewById(R.id.tv_diamond_title);
        tvWatch = findViewById(R.id.tv_watch);
        tvTime = findViewById(R.id.tv_time);
        recyclerView = findViewById(R.id.recycler);
        layoutTips.setVisibility(View.GONE);
        imgAd.setVisibility(View.GONE);
        tvWatch.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        adapter = new AvDetailAdapter(R.layout.item_av_detail, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        findViewById(R.id.close).setOnClickListener(v -> layoutTips.setVisibility(View.GONE));
        imgAd.setOnClickListener(v -> toBrower());

        jzVideoPlayerStandard = findViewById(R.id.videoplayer);
        JZVideoPlayer.SAVE_PROGRESS = false;
        jzVideoPlayerStandard.backButton.setVisibility(View.GONE);

        findViewById(R.id.img_jubao).setOnClickListener(v -> toReport());
        findViewById(R.id.img_change).setOnClickListener(v -> change());



        Observable.interval(1, TimeUnit.SECONDS)
                .compose(RxUtils.io_main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        long currentPositionWhenPlaying = jzVideoPlayerStandard.getCurrentPositionWhenPlaying();
                        Log.e("gy","当前位置："+currentPositionWhenPlaying);
                        if (currentPositionWhenPlaying>10000){
                            toReport();
                            JZVideoPlayer.releaseAllVideos();
                        }
                    }
                });
    }

    public void change() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("切换线路")
                .setSingleChoiceItems(s, a, (dialog1, which) -> {
                    a = which;
                    jzVideoPlayerStandard.setUp(detailBean.getDetails().getVideo_url().get(which), JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
                    jzVideoPlayerStandard.backButton.setVisibility(View.GONE);
                    jzVideoPlayerStandard.startVideo();
                    dialog1.dismiss();
                })
                .create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void collection(){

    }

    private void toReport() {
        new AlertDialog.Builder(this)
                .setTitle("举报")
                .setMessage("是否举报该视频？")
                .setPositiveButton("举报", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        video();
        ad();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, AvDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
        finish();
    }

    private void video() {
        RetrofitClient.getInstance().createApi().videoDetail("Home.Videodetails", AppContext.getInstance().getLoginUid(), id)
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<VideoDetailBean>() {
                    @Override
                    protected void onHandleSuccess(VideoDetailBean bean) {
                        detailBean = bean;
                        GlideUtils.glide(mContext, bean.getDetails().getVideo_img(), jzVideoPlayerStandard.thumbImageView);
                        tvTitle.setText(bean.getDetails().getTitle());
                        tvTime.setText(bean.getDetails().getUptime());
                        tvWatch.setText(bean.getDetails().getWatch_num() + "");
                        list.clear();
                        if (bean.getList() == null || bean.getList().size() == 0) return;
                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();

                        jzVideoPlayerStandard.setUp(detailBean.getDetails().getVideo_url().get(0), JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
                        jzVideoPlayerStandard.backButton.setVisibility(View.GONE);

                        s = new String[bean.getDetails().getVideo_url().size()];

                        for (int i = 0; i < bean.getDetails().getVideo_url().size(); i++) {
                            s[i] = "线路"+(i+1);
                        }
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
                        GlideUtils.glide(AvDetailActivity.this, bean.getThumb(), imgAd);
                    }
                });
    }

    private void toBrower() {
        if (adBean == null) return;
        Uri uri = Uri.parse(adBean.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void toActivity() {
        if (vip) {
            startActivity();
        } else {
            if (!AppConfig.AVMEMBER) {
                PayUtils.payDialog(AvDetailActivity.this, R.mipmap.av_pay_bg, "AV区", "新用户免费观看5部影片", 2, AppContext.avChargeList);
                return;
            }
            startActivity();
        }
    }

    private void startActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", detailBean.getDetails().getTitle());
        bundle.putSerializable("url", s[a]);
        bundle.putSerializable("type", "1");
        bundle.putSerializable("id", detailBean.getDetails().getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    @SuppressLint("CheckResult")
    private void addViewNum() {
        RetrofitClient.getInstance().createApi().addViewNum("Home.addvideonum", id,"1")
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                });
    }
}
