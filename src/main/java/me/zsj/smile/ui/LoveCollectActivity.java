package me.zsj.smile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.zsj.smile.MyApp;
import me.zsj.smile.R;
import me.zsj.smile.adapter.GirlCollectAdapter;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.GirlCollect;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/10/23 0023.
 */
public class LoveCollectActivity extends SwipeRefreshActivity {

    @Bind(R.id.recyclerview)
    RecyclerView mCollectRecyclerView;
    private GirlCollectAdapter mCollectAdapter;

    List<GirlCollect> mGirlCollectList = new ArrayList<>();
    QueryBuilder queryBuilder = new QueryBuilder(GirlCollect.class);

    private int mStart = 1;
    private boolean mIsFirstTimeComeIn = true;


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
                        >= mCollectAdapter.getDatas().size() - 4;
                if (!mRefreshLayout.isRefreshing() && isButtom) {
                    mStart += 10;
                    setRefreshing(true);
                    if (mGirlCollectList.size() == 0) {
                        setRefreshing(false);
                        return;
                    }
                    fetchData();
                }
            }
        });
    }

    private void onMeizhiTouch() {
        mCollectAdapter.setOnMeizhiItemTouchListener(new OnMeizhiItemTouchListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(LoveCollectActivity.this, MeizhiActivity.class);
                intent.putExtra(MeizhiListActivity.MEIZHI_URL,
                        mCollectAdapter.getDatas().get(position).girlUrl);
                intent.putExtra(MeizhiListActivity.MEIZHI_DATE,
                        mCollectAdapter.getDatas().get(position).girlDate);
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(LoveCollectActivity.this,
                                view, MeizhiActivity.TRANSIT_PIC);
                ActivityCompat.startActivity(LoveCollectActivity.this, intent, optionsCompat.toBundle());
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mCollectRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 350);
    }

    private void refresh() {
        mStart = 1;
        setRefreshing(true);
        fetchData();
    }

    private void fetchData() {
        Subscription s = Observable.just(mStart)
                .map(new Func1<Integer, List<GirlCollect>>() {
                    @Override
                    public List<GirlCollect> call(Integer integer) {
                        queryBuilder.limit(integer, 10).orderBy("_id desc");
                        return MyApp.mLiteOrm.query(queryBuilder);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GirlCollect>>() {
                    @Override
                    public void call(List<GirlCollect> collectList) {
                        mGirlCollectList = collectList;
                        notifyDataSetChanged(collectList);
                    }
                });
        addSubscription(s);
    }


    private void notifyDataSetChanged(List<GirlCollect> collectList) {
        if (mStart >= 10) {
            mCollectAdapter.addAll(collectList);
        } else {
            mCollectAdapter.setDatas(collectList);
        }
        mCollectAdapter.notifyDataSetChanged();
        setRefreshing(false);
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当在MeizhiActivity中取消收藏妹纸时，回到收藏界面应当重新刷新数据
        if (!mIsFirstTimeComeIn) {
            mStart = 1;
            fetchData();
        }
        mIsFirstTimeComeIn = false;
    }
}
