package com.xzwzz.lady.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.base.BaseFragment;
import com.xzwzz.lady.ui.adapter.ViewPagerAdapter;
import com.xzwzz.lady.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class AVHomeFragment extends BaseFragment {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments;
    private List<String> titles;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_av_home;
    }

    @Override
    public void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mTabLayout = view.findViewById(R.id.tb_my);
        mViewPager = view.findViewById(R.id.vp_my);

        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        fragments = new ArrayList<>();
        titles = new ArrayList<>();

        AvItemFragment f1 = new AvItemFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("id","-1");
        f1.setArguments(bundle1);
        fragments.add(f1);
        titles.add("最新");

        AvItemFragment f2 = new AvItemFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("id","-2");
        f2.setArguments(bundle2);
        fragments.add(f2);
        titles.add("排行");

        for (int i = 0; i < AppContext.novelTermList.size(); i++) {
            AvItemFragment f = new AvItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", AppContext.novelTermList.get(i).getTerm_id());
            f.setArguments(bundle);
            fragments.add(f);
            titles.add(AppContext.novelTermList.get(i).getName());
        }

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity(), fragments, titles);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOffscreenPageLimit(10);

    }

    @Override
    public void initData() {
    }


}