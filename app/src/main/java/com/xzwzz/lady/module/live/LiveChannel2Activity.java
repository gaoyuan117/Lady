package com.xzwzz.lady.module.live;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.bean.ChannelDataBean;
import com.xzwzz.lady.bean.UserInfoBean;
import com.xzwzz.lady.bean.ZhuBoBean;
import com.xzwzz.lady.module.AbsModuleActivity;
import com.xzwzz.lady.module.live.adapter.LiveChannelAdapter;
import com.xzwzz.lady.utils.PayUtils;
import com.xzwzz.lady.utils.SharePrefUtil;
import com.xzwzz.lady.widget.ViewStatusManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiveChannel2Activity extends AbsModuleActivity {
    private String id, logo;
    private ProgressDialog mProgressDialog;
    private View headView;
    private ImageView imgLogo;
    private TextView tvNum, tvName;
    private String title1;
    private Random random;

    @Override
    protected void initView() {
        super.initView();
        Bundle extras = getIntent().getExtras();
        title1 = extras.getString("plamform");
        logo = extras.getString("logo");
        setToolbar(title1);
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(v -> finish());
        id = extras.getString("id");
        mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.loading);

        headView = View.inflate(this, R.layout.view_channel_head, null);
        imgLogo = headView.findViewById(R.id.img_channel_logo);
        tvNum = headView.findViewById(R.id.tv_num);
        tvName = headView.findViewById(R.id.tv_name);
        tvName.setText(title1);
        Glide.with(this).load(logo).into(imgLogo);

        mAdapter.addHeaderView(headView);

    }

    @Override
    protected void setRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                if (position == 0) return;
                int offest = SizeUtils.dp2px(5f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest, 0);
                }
            }
        });
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new LiveChannelAdapter();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void loadData() {
        random = new Random();
        String url = SharePrefUtil.getString("dataurl", "");
        if (TextUtils.isEmpty(url)) return;
        RetrofitClient.getInstance().createApi().getChannelData2(url + id)
                .compose(RxUtils.io_main())
                .subscribe(zhuBoBean -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (zhuBoBean.getZhubo() != null && zhuBoBean.getZhubo().size() > 0) {

                        List<ChannelDataBean.DataBean> list = new ArrayList<>();

                        for (int i = 0; i < zhuBoBean.getZhubo().size(); i++) {
                            ChannelDataBean.DataBean bean = new ChannelDataBean.DataBean();
                            ZhuBoBean.ZhuboBean zhuboBean = zhuBoBean.getZhubo().get(i);
                            bean.setBigpic(zhuboBean.getImg());
                            bean.setName(zhuboBean.getTitle());
                            bean.setUrl(zhuboBean.getAddress());
                            bean.setNum(random.nextInt(1000) + 100 + "");

                            list.add(bean);
                        }
                        mAdapter.setNewData(list);
                        mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.success);
                        tvNum.setText(list.size() + "");
                    } else {
                        mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.empty);
                    }
                });


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        ChannelDataBean.DataBean item = (ChannelDataBean.DataBean) adapter.getItem(position);
        goRoomV1(item);
    }

    private void goRoomV1(ChannelDataBean.DataBean item) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("data", item);
//        ActivityUtils.startActivity(bundle, LivePlayActivity.class);
        Intent intent = new Intent(this,LivePlayActivity.class);
        intent.putExtra("data",item);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                PayUtils.payDialog(this, R.mipmap.zb_pay_bg, "直播区", "", 1, AppContext.zbChargeList);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMember();
    }

    private void isMember() {
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                        }
                    }
                });
    }
}
