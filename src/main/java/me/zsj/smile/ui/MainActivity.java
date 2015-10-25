package me.zsj.smile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.zsj.smile.event.OnItemTouchListener;
import me.zsj.smile.model.Smile;
import me.zsj.smile.R;
import me.zsj.smile.adapter.SmileListAdapter;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.SmileParser;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends SwipeRefreshActivity {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    private SmileListAdapter mSmileListAdapter;
    private List<Smile> mSmileDatas;

    /**
     * 当前url 的页数
     */
    private int mIndex = 1;
    private static final String SMILE_URL = "http://www.yikedou.com/wenzi/";
    public static final String SMILE_DATA_URL = "SMILE";
    /**
     * 刷新数据的标识
     */
    private static final int LOAD_REFRESH = 1;
    /**
     * 上啦加载更多的标识
     */
    private static final int LOAD_MORE = 2;

    private int mOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmileDatas = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (mNavigationView != null) {
            setUpDrawerContent(mNavigationView);
        }
        initRecyclerView();
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.meizhi) {
                            Intent intent = new Intent(MainActivity.this, MeizhiListActivity.class);
                            startActivity(intent);
                        } else if (menuItem.getItemId() == R.id.collect) {
                            // Toast.makeText(MainActivity.this, "功能还在开发中...", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoveCollectActivity.class);
                            startActivity(intent);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();

        mIndex = 1;
        mOffset = 0;
        getSmileData(LOAD_REFRESH);
    }

    private void setRefresh(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSmileData(LOAD_REFRESH);
            }
        }, 350);
    }

    private void initRecyclerView() {

        mSmileDatas = DataSupport.offset(mOffset).limit(10)
                .order("id asc").find(Smile.class);
        mSmileListAdapter = new SmileListAdapter(MainActivity.this, mSmileDatas);

        //final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSmileListAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isButtom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                        >= mSmileListAdapter.getItemCount() - 4;
                if (!mRefreshLayout.isRefreshing() && isButtom) {
                    mIndex++;
                    mOffset += 10;
                    if (mIndex <= 50) {
                        getSmileData(LOAD_MORE);
                    } else {
                        SnackUtils.show(mRecyclerView, R.string.last_detail);
                    }
                }
            }

        });

        click();

    }

    private void click() {
        mSmileListAdapter.setOnSmileItemClickListener(new OnItemTouchListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {
                Intent intent = new Intent(MainActivity.this, MeizhiAndSmileActivity.class);
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                intent.putExtra(SMILE_DATA_URL, mSmileListAdapter.getDatas().get(position).getTitleUrl());
                //intent.putExtra(MeizhiAndSmileActivity.MEIZHI_STRATING_LOCATION, startingLocation[1]);
                startActivity(intent);
                //overridePendingTransition(0, 0);
            }
        });
    }


    private void getSmileData(int index) {
        setRefresh(true);
        Observable.just(index)
                .map(new Func1<Integer, Object>() {
                    @Override
                    public Object call(Integer integer) {
                        int success = 1;
                        if (integer == LOAD_REFRESH) {
                            getRefreshDatas();
                        } else if (integer == LOAD_MORE) {
                            loadMoreDatas();
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
                            mSmileListAdapter.notifyDataSetChanged();
                            setRefresh(false);
                        }
                    }
                });
    }

    /**
     * 刷新，加载数据
     */
    private void getRefreshDatas() {
        try {
            if (NetUtils.checkNet(MainActivity.this)) {
                mSmileDatas = SmileParser.getInstance().getSimle(SMILE_URL);
                //刷新过程中删除旧数据
                DataSupport.deleteAll(Smile.class);
                //保存下载解析到的新数据
                DataSupport.saveAll(mSmileDatas);
                mSmileListAdapter.setDatas(mSmileDatas);
            } else {
                SnackUtils.show(mRecyclerView, R.string.net_unconnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页加载，加载更多数据
     */
    private void loadMoreDatas() {
        try {
            if (NetUtils.checkNet(MainActivity.this)) {
                mSmileDatas = SmileParser.getInstance().getSimle(SMILE_URL + "index_" + mIndex + ".html");
                //将解析得到的数据全部添加到数据库中做缓存
                DataSupport.saveAll(mSmileDatas);
                mSmileListAdapter.addAll(mSmileDatas);
            } else {
                // 分页查询，id 是 litepal 自动生成的
                mSmileDatas = DataSupport.offset(mOffset).limit(10)
                        .order("id asc").find(Smile.class);
                mSmileListAdapter.addAll(mSmileDatas);
                SnackUtils.show(mRecyclerView, R.string.net_unconnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * FloatingActionButton 的点击事件
     *
     * @param view
     */
    @OnClick(R.id.fab)
    public void onFab(View view) {

        mIndex = 1;
        mOffset = 0;
        /**
         * 保证了 RecyclerView 在回滚到 item 为 0位置后应用不会出现崩溃的情况
         */
        if (mSmileListAdapter.getItemCount() / 10 <= 3) {
            mRecyclerView.smoothScrollToPosition(0);
        }
        getSmileData(LOAD_REFRESH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.smile_author:
                break;
            case R.id.about_app:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
