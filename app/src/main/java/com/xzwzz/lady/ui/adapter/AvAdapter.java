package com.xzwzz.lady.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.lady.R;
import com.xzwzz.lady.bean.AvVideoListBean;
import com.xzwzz.lady.utils.GlideUtils;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/5.
 */

public class AvAdapter extends BaseQuickAdapter<AvVideoListBean,BaseViewHolder>{

    public AvAdapter(int layoutResId, @Nullable List<AvVideoListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AvVideoListBean item) {
        ImageView imageView = helper.getView(R.id.img_av);
        if (TextUtils.isEmpty(item.getVideo_img())){
            GlideUtils.glide(mContext,item.getImg_url(),imageView);
        }else {
            GlideUtils.glide(mContext,item.getVideo_img(),imageView);
        }
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_time,item.getUptime());

        ImageView freeImg = helper.getView(R.id.img_free);
        if (item.getIs_free()==0){
            freeImg.setVisibility(View.GONE);
        }else {
            freeImg.setVisibility(View.VISIBLE);
        }
    }
}
