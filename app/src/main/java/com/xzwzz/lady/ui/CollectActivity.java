package com.xzwzz.lady.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.R;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.ChannelDataBean;
import com.xzwzz.lady.module.live.adapter.LiveChannelAdapter;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

    private LiveChannelAdapter adapter;
    private List<ChannelDataBean.DataBean> mList = new ArrayList<>();
    private RecyclerView recyclerView;

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
        mList.clear();
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

        adapter = new LiveChannelAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        adapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

        new AlertDialog.Builder(this).setMessage("是否移除该收藏")
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("确定", (dialog, which) -> {


                }).show();

        return true;
    }
}
