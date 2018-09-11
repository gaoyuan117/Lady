package com.xzwzz.lady.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.MoviesLinkListBean;
import com.xzwzz.lady.module.video.VideoPlayActivity;
import com.xzwzz.lady.ui.adapter.DiamondAdapter;

import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;

    private DiamondAdapter adapter;
    private List<MoviesLinkListBean> list = new ArrayList<>();

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_buy;
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbar("已购视频", true);

        recyclerView = findViewById(R.id.recycler);
        refresh = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        adapter = new DiamondAdapter(R.layout.item_diamond, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        refresh.setOnRefreshListener(this);
        getMoviesLinkList();

        adapter.setGone(true);

    }

    private void getMoviesLinkList() {
        RetrofitClient.getInstance().createApi().moviesLinkList("Home.mybuyvideo", AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MoviesLinkListBean bean = list.get(position);
        toActivity(bean);
    }

    private void toActivity(MoviesLinkListBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", bean.getTitle());
        bundle.putSerializable("url", bean.getUrl());
        bundle.putSerializable("type", "2");
        bundle.putSerializable("id", bean.getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    @Override
    public void onRefresh() {
        getMoviesLinkList();
    }
}
