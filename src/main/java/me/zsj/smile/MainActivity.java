package me.zsj.smile;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import me.zsj.model.Smile;
import me.zsj.utils.CommonException;
import me.zsj.utils.NetUtils;
import me.zsj.utils.SmileParser;
import me.zsj.utils.SnackUtils;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private SmileListAdapter mSmileListAdapter;
    private List<Smile> mSmileDatas;
    private Handler mHandler;
    /**
     * 笑话解析类，用来解析HTML 数据
     */
    private SmileParser mSmileParser;
    /**
     * 当前url 的页数
     */
    private int mIndex = 1;
    private static final String mSmileUrl = "http://www.yikedou.com/wenzi/";
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
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSmileParser = new SmileParser();
        mSmileDatas = new ArrayList<>();
        initRecyclerView();

        mHandler = new Handler();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSmileData(LOAD_REFRESH);
            }
        }, 350);
    }

    private void initRecyclerView() {
        mSmileListAdapter = new SmileListAdapter(MainActivity.this, mSmileDatas);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        mRefreshLayout.setColorSchemeColors(
                R.color.refresh_progress_3, R.color.refresh_progress_2,
                R.color.refresh_progress_1);
        mRefreshLayout.setOnRefreshListener(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSmileListAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mRefreshLayout.isRefreshing() && layoutManager.findLastCompletelyVisibleItemPosition()
                        >= mSmileListAdapter.getItemCount() - 2) {
                    mIndex++;
                    mOffset += 10;
                    if (mIndex <= 50) {
                        mRefreshLayout.setRefreshing(true);
                        getSmileData(LOAD_MORE);
                    } else {
                        SnackUtils.show(mRecyclerView, R.string.last_detail);
                    }
                }
            }

        });

    }

    private void getSmileData(int index) {

        mRefreshLayout.setRefreshing(true);
        if (Build.VERSION.SDK_INT >= 11) {
            new SmileAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    index);
        } else {
            new SmileAsyncTask().execute(index);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.smile_author) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * FloatingActionButton 的点击事件
     *
     * @param view
     */
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

    /**
     * SwipeRefreshLayout 的刷新回调事件
     */
    @Override
    public void onRefresh() {

        mIndex = 1;
        mOffset = 0;
        getSmileData(LOAD_REFRESH);
    }

    class SmileAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {

            switch (params[0]) {
                case LOAD_REFRESH:
                    getRefreshDatas();
                    break;
                case LOAD_MORE:
                    loadMoreDatas();
                    break;
            }
            return -1;
        }

        /**
         * 刷新，加载数据
         */
        private void getRefreshDatas() {
            try {
                if (NetUtils.checkNet(MainActivity.this)) {
                    mSmileDatas = mSmileParser.getSimle(mSmileUrl);
                    //刷新过程中删除旧数据
                    DataSupport.deleteAll(Smile.class);
                    //保存下载解析到的新数据
                    DataSupport.saveAll(mSmileDatas);
                    mSmileListAdapter.setDatas(mSmileDatas);
                }else {
                    // 分页查询，id 是 litepal 自动生成的
                    mSmileDatas = DataSupport.offset(mOffset).limit(10)
                            .order("id asc").find(Smile.class);
                    mSmileListAdapter.setDatas(mSmileDatas);
                    SnackUtils.show(mRecyclerView, R.string.net_unconnected);
                }
            } catch (CommonException e) {
                e.printStackTrace();
            }
        }

        /**
         * 分页加载，加载更多数据
         */
        private void loadMoreDatas() {
            try {
                if (NetUtils.checkNet(MainActivity.this)) {
                    mSmileDatas = mSmileParser.getSimle(mSmileUrl + "index_" + mIndex + ".html");
                    //将解析得到的数据全部添加到数据库中做缓存
                    DataSupport.saveAll(mSmileDatas);
                    mSmileListAdapter.addAll(mSmileDatas);
                }else {
                    // 分页查询，id 是 litepal 自动生成的
                    mSmileDatas = DataSupport.offset(mOffset).limit(10)
                            .order("id asc").find(Smile.class);
                    mSmileListAdapter.addAll(mSmileDatas);
                    SnackUtils.show(mRecyclerView, R.string.net_unconnected);
                }
            } catch (CommonException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            mSmileListAdapter.notifyDataSetChanged();
            mRefreshLayout.setRefreshing(false);
        }
    }
}
