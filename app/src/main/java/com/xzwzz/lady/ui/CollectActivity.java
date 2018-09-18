package com.xzwzz.lady.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.AvVideoListBean;
import com.xzwzz.lady.bean.ChannelDataBean;
import com.xzwzz.lady.bean.CollectBean;
import com.xzwzz.lady.bean.TextAdBean;
import com.xzwzz.lady.module.live.adapter.LiveChannelAdapter;
import com.xzwzz.lady.ui.adapter.AvAdapter;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private List<AvVideoListBean> mList = new ArrayList<>();
    private AvAdapter adapter;
    private RecyclerView recyclerView;
    private List<CollectBean> collectBeans;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initView() {
        setToolbar("收藏", true);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                int offest = SizeUtils.dp2px(5f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest, 0);
                }
            }
        });

        adapter = new AvAdapter(R.layout.item_av, mList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {
        getCollection();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (collectBeans==null) return;
        AvVideoListBean bean = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", bean.getTitle());
        bundle.putString("url", bean.getVideo_url());
        bundle.putString("id", bean.getId());
        bundle.putSerializable("type", collectBeans.get(position).getType());
        if (collectBeans.get(position).getType().equals("2")){
            ActivityUtils.startActivity(bundle, AvDetailActivity.class);
        }else {
            ActivityUtils.startActivity(bundle, VideoDetailActivity.class);
        }
    }



    private void getCollection() {
        RetrofitClient.getInstance().createApi().collcet("Home.collectList", AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<CollectBean>() {
                    @Override
                    protected void onHandleSuccess(List<CollectBean> list) {
                        if (list == null || list.size() == 0) return;
                        collectBeans = list;
                        mList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            AvVideoListBean bean = new AvVideoListBean();
                            bean.setId(list.get(i).getVideo_id());
                            bean.setTitle(list.get(i).getTitle());
                            bean.setImg_url(list.get(i).getVideo_img());
                            bean.setVideo_img(list.get(i).getVideo_img());
                            bean.setUptime(list.get(i).getUptime());
                            mList.add(bean);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
