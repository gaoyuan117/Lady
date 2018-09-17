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
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.BaseObjObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.CollectionBean;
import com.xzwzz.lady.bean.DiamondAdBean;
import com.xzwzz.lady.bean.UserInfoBean;
import com.xzwzz.lady.bean.VideoDetailBean;
import com.xzwzz.lady.module.video.VideoPlayActivity;
import com.xzwzz.lady.ui.adapter.AvDetailAdapter;
import com.xzwzz.lady.ui.adapter.VideoDetailAdapter;
import com.xzwzz.lady.utils.GlideUtils;
import com.xzwzz.lady.utils.PayUtils;
import com.xzwzz.lady.widget.XzwzzPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VideoDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

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
    private String[] s;
    private Disposable disposable;
    private String type;
    private ImageView collectImg;


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
        type = getIntent().getStringExtra("type");
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
        collectImg = findViewById(R.id.img_collcet);
        collectImg.setOnClickListener(v -> collection());

        addViewNum();
    }

    private void timer() {
        if (disposable != null) disposable.dispose();
        Observable.interval(1, TimeUnit.SECONDS)
                .compose(RxUtils.io_main())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        long currentPositionWhenPlaying = jzVideoPlayerStandard.getCurrentPositionWhenPlaying();
                        Log.e("gy", "当前位置：" + currentPositionWhenPlaying);
                        if (currentPositionWhenPlaying > 10000) {
                            JZVideoPlayer.releaseAllVideos();
                            PayUtils.payDialog(VideoDetailActivity.this, R.mipmap.zb_pay_bg, "视频区", "", 1, AppContext.zbChargeList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
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

    @SuppressLint("CheckResult")
    private void collection() {
        RetrofitClient.getInstance().createApi().collection("Home.collect", detailBean.getDetails().getId(), AppContext.getInstance().getLoginUid(), type)
                .compose(RxUtils.io_main())
                .subscribe(new Consumer<CollectionBean>() {
                    @Override
                    public void accept(CollectionBean collectionBean) throws Exception {
                        ToastUtils.showShort(collectionBean.getData().getMsg());
                        if (collectionBean.getData().getMsg().contains("成功")) {
                            collectImg.setImageResource(R.mipmap.collect_true);
                        } else {
                            collectImg.setImageResource(R.mipmap.collect_false);
                        }
                    }
                });
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

                            if (!AppConfig.IS_MEMBER) {
                                jzVideoPlayerStandard.fullscreenButton.setVisibility(View.INVISIBLE);
                                timer();
                            } else {
                                jzVideoPlayerStandard.fullscreenButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (detailBean == null || adBean == null) return;
        Intent intent = new Intent(this, AvDetailActivity.class);
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
                            s[i] = "线路" + (i + 1);
                        }
                        if (detailBean.getDetails().getIs_free() == 1) {
                            jzVideoPlayerStandard.fullscreenButton.setVisibility(View.VISIBLE);
                        } else {
                            isMember();
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
        RetrofitClient.getInstance().createApi().addViewNum("Home.addvideonum", id, type)
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                });
    }
}
