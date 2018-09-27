package com.xzwzz.lady.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.bean.BookBean;
import com.xzwzz.lady.module.book.BookAdapter;
import com.xzwzz.lady.module.book.BookDetailActivity;
import com.xzwzz.lady.utils.PayUtils;

import java.util.ArrayList;
import java.util.List;

public class NovelFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    BookAdapter adapter;
    List<BookBean> data = new ArrayList<>();
    String id;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_novel;
    }

    @Override
    public void initView(View view) {

        id = getArguments().getString("id");
        LogUtils.e("id：" + id);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new BookAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        RetrofitClient.getInstance().createApi().getBookList(id).compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<BookBean>() {
                    @Override
                    protected void onHandleSuccess(List<BookBean> list) {
                        if (list == null || list.size() == 0) return;
                        data.clear();
                        data.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (AppConfig.AVMEMBER){
            BookBean item = (BookBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", item.id);
            bundle.putString("name", item.post_title);
            ActivityUtils.startActivity(bundle, BookDetailActivity.class);
        }else {
            PayUtils.payDialog(getActivity(), R.mipmap.av_pay_bg, "开通会员", "", 2, AppContext.avChargeList);
        }
    }
}
