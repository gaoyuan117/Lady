package com.xzwzz.lady.ui.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.bean.PromotionBean;
import com.xzwzz.lady.ui.adapter.PromotionAdapter;
import com.xzwzz.lady.utils.StatusBarUtil;
import com.xzwzz.lady.widget.ViewStatusManager;

import java.util.List;

/*
 * @Project_Name :SweetBox
 * @package_Name :com.xzwzz.sweetbox.ui.fragment
 * @AUTHOR      :xzwzz@vip.QqBean.com
 * @DATE        :2018/4/20
 */
public class PromotionFragment extends BaseFragment {
    private ViewStatusManager mViewStatusManager;
    private RecyclerView mRecyclerView;
    private PromotionAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_promotion;
    }

    @Override
    public void initView(View view) {
        StatusBarUtil.getInstance().setPaddingSmart(mContext, view.findViewById(R.id.toolbar));
        mViewStatusManager = view.findViewById(R.id.viewstatusmanager);
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PromotionAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.setFooterView(iv);
    }

    @Override
    public void initData() {
        RetrofitClient.getInstance().createApi().getPromotion("Home.DailiList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<PromotionBean>(mViewStatusManager) {
                    @Override
                    protected void onHandleSuccess(List<PromotionBean> list) {
                        mAdapter.setNewData(list);
                    }
                });
    }
}
