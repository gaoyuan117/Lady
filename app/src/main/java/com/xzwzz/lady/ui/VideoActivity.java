package com.xzwzz.lady.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.bean.VideoListBean;
import com.xzwzz.lady.module.video.VideoPlayActivity;
import com.xzwzz.lady.ui.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RelativeLayout layout;
    private ListView listView;
    private TextView tvTitle;
    private TextView tvRight;
    private VideoAdapter adapter;
    private List<VideoListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        loadData();
    }

    protected void initView() {

        layout = findViewById(R.id.layout);
        listView = findViewById(R.id.listView);
        listView = findViewById(R.id.listView);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        tvRight.setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setOnClickListener(this);
        tvRight.setOnClickListener(this);

        tvTitle.setText("精彩视频");
        adapter = new VideoAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_right:
                loadData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        VideoListBean bean = list.get(i);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", bean);
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    protected void loadData() {
        RetrofitClient.getInstance().createApi().tiyan("Home.getVideo").compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<VideoListBean>() {
                    @Override
                    protected void onHandleSuccess(List<VideoListBean> lists) {
                        if (lists == null || lists.size() == 0) return;
                        list.clear();
                        list.addAll(lists);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


}
