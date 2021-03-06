package me.zsj.smile.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


import com.litesuits.orm.db.assit.QueryBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.zsj.smile.MyApp;
import me.zsj.smile.R;
import me.zsj.smile.adapter.GirlCollectAdapter;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.GirlCollect;
import me.zsj.smile.ui.fragment.MeizhiFragment;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/10/23 0023.
 */
public class LoveCollectActivity extends SwipeRefreshActivity {

    @Bind(R.id.recyclerview) RecyclerView mCollectRecyclerView;
    private GirlCollectAdapter mCollectAdapter;

    List<GirlCollect> mGirlCollectList = new ArrayList<>();
    QueryBuilder queryBuilder = new QueryBuilder(GirlCollect.class);
    private int mStart = 0;
    private static final int QUERY_TOTAL = 30;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_love_collect;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationListener();
        setupRecyclerView();
        onMeizhiTouch();
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoveCollectActivity.this.onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {

        mCollectAdapter = new GirlCollectAdapter(this, mGirlCollectList);
        final StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mCollectRecyclerView.setHasFixedSize(true);
        mCollectRecyclerView.setLayoutManager(layoutManager);
        mCollectRecyclerView.setAdapter(mCollectAdapter);

        mCollectRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isButtom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                        >= mGirlCollectList.size() - 10;
                if (!mRefreshLayout.isRefreshing() && isButtom) {
                    mStart += QUERY_TOTAL;
                    setRefreshing(true);
                    if (mGirlCollectList.size() == 0) {
                        setRefreshing(false);
                        return;
                    }
                    fetchData(false);
                }
            }
        });
    }

    private void onMeizhiTouch() {
        mCollectAdapter.setOnMeizhiItemTouchListener(new OnMeizhiItemTouchListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                Picasso.with(LoveCollectActivity.this).load(mGirlCollectList.get(position).girlUrl)
                        .fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                startToMeizhiActivity(view, position);
                            }
                            @Override
                            public void onError() {}
                        });
            }
        });
    }

    private void startToMeizhiActivity(View view, int position) {
        Intent intent = new Intent(LoveCollectActivity.this, MeizhiActivity.class);
        intent.putExtra(MeizhiFragment.MEIZHI_URL,
                mGirlCollectList.get(position).girlUrl);
        intent.putExtra(MeizhiFragment.MEIZHI_DATE,
                mGirlCollectList.get(position).girlDate);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        view, MeizhiActivity.TRANSIT_PIC);
        ActivityCompat.startActivity(LoveCollectActivity.this, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mCollectRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
            }
        }, 350);
        fetchData(true);
    }

    private void refresh() {
        mStart = 0;
        fetchData(true);
    }

    private void fetchData(final boolean clean) {
        //MyApp.mLiteOrm.<GirlCollect>query(GirlCollect.class)
        queryBuilder = queryBuilder.limit(mStart, QUERY_TOTAL).orderBy("_id desc");
        Subscription s = Observable.just(MyApp.mLiteOrm.<GirlCollect>query(queryBuilder))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GirlCollect>>() {
                    @Override
                    public void call(List<GirlCollect> collectList) {
                        if (clean) mGirlCollectList.clear();
                        mGirlCollectList.addAll(collectList);
                        mCollectAdapter.notifyDataSetChanged();
                        setRefreshing(false);
                    }
                });
        addSubscription(s);
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        setRefreshing(true);
        refresh();
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        //当在MeizhiActivity中取消收藏妹纸时，回到收藏界面应当重新刷新数据
        if (!mIsFirstTimeComeIn) {
           // mStart = 1;
            setRefreshing(true);
            fetchData(true);
        }
        mIsFirstTimeComeIn = false;
    }*/
}
