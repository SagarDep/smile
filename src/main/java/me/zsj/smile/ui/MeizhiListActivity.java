package me.zsj.smile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.zsj.smile.Data;
import me.zsj.smile.DataRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.adapter.MeizhiListAdapter;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.data.RestVideoData;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.Gank;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.utils.FABAnimation;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListActivity extends SwipeRefreshActivity {

    private int mPage = 1;
    private static final int PRELOAD_SIZE = 6;
    public static final String MEIZHI_URL = "MEIZHI_URL";
    public static final String MEIZHI_DATE = "MEIZHI_DATE";
    public static final String VEDIO_URL = "VEDIO_URL";
    public static final String VEDIO_DESC = "VEDIO_DESC";


    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.fab)
    FloatingActionButton FAB;
    private MeizhiListAdapter mMeizhiListAdapter;


    private boolean mIsFirstTouch = true;

    private List<Meizhi> mMeizhiLists = new ArrayList<>();
    private List<Gank> mGankLists = new ArrayList<>();
    private Data sData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (sData == null) {
            sData = new DataRetrofit().getService();
        }
        setRecyclerView();
        setNavigationListener();
        itemClick();
    }

    private void itemClick() {
        mMeizhiListAdapter.setOnMeizhiItemTouchListener(new OnMeizhiItemTouchListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.meizhi_imageview) {
                    startToMeizhiActivity(view, position);
                } else if (view.getId() == R.id.meizhi_desc_item) {
                    if (getVedioDataCount() > position) {
                        Intent intent = new Intent(MeizhiListActivity.this, VedioActivity.class);
                        intent.putExtra(VEDIO_URL, mGankLists.get(position).url);
                        intent.putExtra(VEDIO_DESC, mGankLists.get(position).desc);
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
        intent.putExtra(MEIZHI_URL, mMeizhiLists.get(position).url);
        intent.putExtra(MEIZHI_DATE,
                mMeizhiLists.get(position).publishedAt.substring(0, 10));
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
        if (NetUtils.checkNet(MeizhiListActivity.this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRefresh(true);
                    getMeizhiData(true);
                }
            }, 350);
        }

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
                        getMeizhiData(false);
                    } else {
                        mIsFirstTouch = false;
                    }
                }
                FABAnimation.fabAnimation(FAB, dy);
            }
        });
    }

    private void setRefresh(boolean refreshing) {
        setRefreshing(refreshing);
    }

    private void getMeizhiData(final boolean clean) {
        Subscription s = Observable.zip(sData.getMeizhi(mPage), sData.getRestVedioData(mPage),
                new Func2<MeizhiData, RestVideoData, List<Meizhi>>() {
                    @Override
                    public List<Meizhi> call(MeizhiData meizhiData, RestVideoData restVideoData) {
                        createMeizhiDataWithVedioData(meizhiData, restVideoData);
                        if (clean) saveVedioData(restVideoData.results);
                        else addAllVedioData(restVideoData.results);

                        return meizhiData.results;
                    }
                })
                .map(new Func1<List<Meizhi>, List<Meizhi>>() {
                    @Override
                    public List<Meizhi> call(List<Meizhi> meizhiList) {
                        return meizhiList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Meizhi>>() {
                    @Override
                    public void call(List<Meizhi> meizhiList) {
                        if (clean) mMeizhiLists.clear();
                        mMeizhiLists.addAll(meizhiList);
                        mMeizhiListAdapter.notifyDataSetChanged();
                        setRefresh(false);
                    }
                });
        addSubscription(s);
    }

    private MeizhiData createMeizhiDataWithVedioData(MeizhiData meizhiData, RestVideoData restVideoData) {

        for (int i = 0; i < restVideoData.results.size(); i++) {
            Meizhi meizhi = meizhiData.results.get(i);
            meizhi.desc = meizhi.desc + restVideoData.results.get(i).desc;
        }
        return meizhiData;
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
        getMeizhiData(true);
    }

    @OnClick(R.id.fab)
    public void onRefresh(View view) {
        mPage = 1;
        setRefresh(true);
        getMeizhiData(true);
    }


}
