package me.zsj.smile.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.zsj.smile.MeizhiRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.adapter.MeizhiListAdapter;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.event.OnItemTouchListener;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.TaskUtils;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListActivity extends SwipeRefreshActivity{

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    private MeizhiListAdapter mMeizhiListAdapter;

    private int mPage = 1;
    private static int PRELOAD_SIZE = 6;
    private static int TASK_RESULT = 1;
    private static int LOAD_REFRESH = 1;
    private static int LOAD_MORE = 2;
    public static String MEIZHI_URL = "MEIZHI_URL";
    public static String MEIZHI_DATE = "MEIZHI_DATE";
    

    private List<Meizhi> mMeizhiLists = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRecyclerView();
        setNavigationListener();
        itemClick();
    }

    private void itemClick() {
        mMeizhiListAdapter.setOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {

                Intent intent = new Intent(MeizhiListActivity.this, MeizhiActivity.class);
                intent.putExtra(MEIZHI_URL, mMeizhiListAdapter.getDatas().get(position).getUrl());
                intent.putExtra(MEIZHI_DATE,
                        mMeizhiListAdapter.getDatas().get(position).getPublishedAt().substring(0, 10));
                startActivity(intent);
            }
        });
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeizhiListActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMeizhiData(LOAD_REFRESH);
            }
        }, 500);
    }

    private void setRecyclerView() {

        mRecyclerView.setHasFixedSize(true);
        final StaggeredGridLayoutManager layoutManager= new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mMeizhiListAdapter = new MeizhiListAdapter(this, mMeizhiLists);
        mRecyclerView.setAdapter(mMeizhiListAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean isButtom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                        >= mMeizhiListAdapter.getItemCount() - PRELOAD_SIZE;

                if (!mRefreshLayout.isRefreshing() && isButtom) {
                    setRefresh(true);
                    mPage++;
                    getMeizhiData(LOAD_MORE);
                }
            }
        });
    }

    private void setRefresh(boolean refreshing) {
        setRefreshing(refreshing);
    }

    private void getMeizhiData(int index) {

        TaskUtils.executeTask(new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {

                if (params[0] == LOAD_REFRESH) {
                    getRefreshMeizhi();
                } else if (params[0] == LOAD_MORE) {
                    getMoreMeizhi();
                }
                return TASK_RESULT;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                if (integer.intValue() == TASK_RESULT) {
                    mMeizhiListAdapter.notifyDataSetChanged();
                    setRefresh(false);
                }
            }
        }, index);

    }

    private void getRefreshMeizhi() {
        if (NetUtils.checkNet(MeizhiListActivity.this)) {
            MeizhiData meizhiData = new MeizhiRetrofit().getService().getMeizhi(1);
            mMeizhiLists = meizhiData.results;
            mMeizhiListAdapter.setDatas(mMeizhiLists);
        }
    }

    private void getMoreMeizhi() {
        if (NetUtils.checkNet(MeizhiListActivity.this)) {
            MeizhiData meizhiData = new MeizhiRetrofit().getService().getMeizhi(mPage);
            mMeizhiLists = meizhiData.results;
            mMeizhiListAdapter.addAll(mMeizhiLists);
        }
    }
    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();

        int refresh = 1;
        getMeizhiData(refresh);
    }

    @OnClick(R.id.meizhi_FAB)
    public void onRefresh(View view) {
        getMeizhiData(1);
    }

}
