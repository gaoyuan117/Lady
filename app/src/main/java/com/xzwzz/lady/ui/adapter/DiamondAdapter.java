package com.xzwzz.lady.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.lady.R;
import com.xzwzz.lady.bean.MoviesLinkListBean;
import com.xzwzz.lady.utils.GlideUtils;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/5.
 */

public class DiamondAdapter extends BaseQuickAdapter<MoviesLinkListBean, BaseViewHolder> {

    boolean isGone;

    public DiamondAdapter(int layoutResId, @Nullable List<MoviesLinkListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MoviesLinkListBean item) {
        ImageView imageView = helper.getView(R.id.img_diamond);
        GlideUtils.glide(mContext, item.getImg_url(), imageView);
        helper.setText(R.id.tv_diamond_title, item.getTitle());
        helper.setText(R.id.tv_diamond_time, item.getUptime());
        helper.setText(R.id.tv_diamond_view, item.getWatch_num());
        helper.setText(R.id.tv_diamond, item.getCoin() + "");
        View view = helper.getView(R.id.view);
        ImageView img_buy = helper.getView(R.id.img_buy);
        if (item.getIs_buy()==0){
            img_buy.setVisibility(View.VISIBLE);
        }else {
            img_buy.setVisibility(View.GONE);
        }
        if (isGone) {
            view.setVisibility(View.GONE);
            img_buy.setVisibility(View.GONE);
        }
        helper.addOnClickListener(R.id.img_buy);
    }

    public void setGone(boolean b) {
        isGone = b;
    }
}
