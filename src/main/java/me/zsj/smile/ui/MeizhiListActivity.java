package me.zsj.smile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import me.zsj.smile.data.RestVedioData;
import me.zsj.smile.event.OnItemTouchListener;
import me.zsj.smile.model.Gank;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListActivity extends SwipeRefreshActivity {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private MeizhiListAdapter mMeizhiListAdapter;

    private int mPage = 1;
    private static int PRELOAD_SIZE = 6;
    private static int LOAD_REFRESH = 1;
    private static int LOAD_MORE = 2;
    public static String MEIZHI_URL = "MEIZHI_URL";
    public static String MEIZHI_DATE = "MEIZHI_DATE";
    public static final String VEDIO_URL = "VEDIO_URL";
    public static final String VEDIO_DESC = "VEDIO_DESC";

    private boolean mIsFirstTouch = true;

    private List<Meizhi> mMeizhiLists = new ArrayList<>();
    private List<Gank> mGankLists = new ArrayList<>();
    MeizhiRetrofit mMeizhiRetrofit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mMeizhiRetrofit == null) {
            mMeizhiRetrofit = new MeizhiRetrofit();
        }

        setRecyclerView();
        setNavigationListener();
        itemClick();
    }

    private void itemClick() {
        mMeizhiListAdapter.setOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {
                if (view.getId() == R.id.meizhi_imageview) {
                    startToMeizhiActivity(view, position);
                } else if (view.getId() == R.id.meizhi_desc_item) {
                    if (getVedioDataCount() > position) {
                        Intent intent = new Intent(MeizhiListActivity.this, VedioActivity.class);
                        intent.putExtra(VEDIO_URL, mGankLists.get(position).getUrl());
                        intent.putExtra(VEDIO_DESC, mGankLists.get(position).getDesc());
                        startActivity(intent);
                    } else {
                        SnackUtils.show(mRecyclerView, "没有视频啦啦啦!!!");
                    }
                }
            }
        });

    }

    private void startToMeizhiActivity(View view, int position) {
        Intent intent = new Intent(MeizhiListActivity.this, MeizhiActivity.class);
        intent.putExtra(MEIZHI_URL, mMeizhiListAdapter.getDatas().get(position).getUrl());
        intent.putExtra(MEIZHI_DATE,
                mMeizhiListAdapter.getDatas().get(position).getPublishedAt().substring(0, 10));
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(MeizhiListActivity.this,
                        view, MeizhiActivity.TRANSIT_PIC);
        ActivityCompat.startActivity(MeizhiListActivity.this, intent, optionsCompat.toBundle());
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
                setRefresh(true);
            }
        }, 500);

    }


    private void setRecyclerView() {

        mRecyclerView.setHasFixedSize(true);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
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
                    if (!mIsFirstTouch) {
                        setRefresh(true);
                        mPage += 1;
                        getMeizhiData(LOAD_MORE);
                    } else {
                        mIsFirstTouch = false;
                    }
                }
            }
        });
    }

    private void setRefresh(boolean refreshing) {
        setRefreshing(refreshing);
    }

    private void getMeizhiData(final int index) {
        Observable.just(index)
                .map(new Func1<Integer, Object>() {
                    @Override
                    public Object call(Integer integer) {
                        int success = 1;
                        if (index == LOAD_REFRESH) {
                            getRefreshMeizhi();
                        } else if (index == LOAD_MORE) {
                            getMoreMeizhi();
                        }
                        return success;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if ((Integer) o == 1) {
                            mMeizhiListAdapter.notifyDataSetChanged();
                            setRefresh(false);
                        }
                    }
                });
    }

    private MeizhiData createMeizhiDataWithVedioData(MeizhiData meizhiData, RestVedioData restVedioData) {

        for (int i = 0; i < restVedioData.results.size(); i++) {
            Meizhi meizhi = meizhiData.results.get(i);
            meizhi.setDesc(meizhi.getDesc() + restVedioData.results.get(i).getDesc());
        }
        return meizhiData;
    }

    private void getRefreshMeizhi() {
        if (NetUtils.checkNet(MeizhiListActivity.this)) {
            MeizhiData meizhiData = mMeizhiRetrofit.getService().getMeizhi(1);
            RestVedioData restVedioData = mMeizhiRetrofit.getService().getRestVedioData(1);
            meizhiData = createMeizhiDataWithVedioData(meizhiData, restVedioData);
            saveVedioData(restVedioData.results);
            mMeizhiLists = meizhiData.results;
            mMeizhiListAdapter.setDatas(mMeizhiLists);
        }
    }

    private void getMoreMeizhi() {
        if (NetUtils.checkNet(MeizhiListActivity.this)) {
            MeizhiData meizhiData = mMeizhiRetrofit.getService().getMeizhi(mPage);
            RestVedioData restVedioData = mMeizhiRetrofit.getService().getRestVedioData(mPage);
            meizhiData = createMeizhiDataWithVedioData(meizhiData, restVedioData);
            addAllVedioData(restVedioData.results);
            mMeizhiLists = meizhiData.results;
            mMeizhiListAdapter.addAll(mMeizhiLists);
        }
    }

    private void saveVedioData(List<Gank> vedioData) {
        mGankLists.clear();
        mGankLists.addAll(vedioData);
    }

    private void addAllVedioData(List<Gank> vedioData) {
        mGankLists.addAll(vedioData);
    }

    private int getVedioDataCount() {
        return mGankLists.size();
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();

        mPage = 1;
        setRefresh(true);
        getMeizhiData(mPage);
    }

    @OnClick(R.id.fab)
    public void onRefresh(View view) {
        mPage = 1;
        setRefresh(true);
        getMeizhiData(mPage);
    }


}
