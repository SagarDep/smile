package me.zsj.smile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import me.zsj.smile.MyApp;
import me.zsj.smile.R;
import me.zsj.smile.SmileParser;
import me.zsj.smile.adapter.SmileListAdapter;
import me.zsj.smile.event.OnSmileItemTouchListener;
import me.zsj.smile.model.Smile;
import me.zsj.smile.ui.MeizhiAndSmileActivity;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/11/29 0029.
 */
public class SmileFragment extends SwipeRefreshFragment {

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

    private int mStart = 1;
    QueryBuilder query = new QueryBuilder(Smile.class);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchSmileData(LOAD_REFRESH);
            }
        }, 358);

        initRecyclerView();

    }

    private void initRecyclerView() {

        query.limit(1, 10);
        mSmileDatas = MyApp.mLiteOrm.query(query);
        mSmileListAdapter = new SmileListAdapter(getActivity(), mSmileDatas);

        final StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
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
                    if (mIndex <= 50) {
                        fetchSmileData(LOAD_MORE);
                    } else {
                        SnackUtils.show(mRecyclerView, R.string.last_detail);
                    }
                }
            }
        });

        itemClick();

    }

    private void itemClick() {
        mSmileListAdapter.setOnSmileItemClickListener(new OnSmileItemTouchListener() {

            @Override
            public void onItemClick(View view, Smile smile) {
                Intent intent = new Intent(getActivity(), MeizhiAndSmileActivity.class);
                intent.putExtra(SMILE_DATA_URL, smile.titleUrl);
                intent.putExtra(MeizhiAndSmileActivity.SMILE_DESCRIPTION, smile.smileContent);
                startActivity(intent);
            }
        });
    }

    private void fetchSmileData(int index) {
        setRefreshing(true);
        Subscription s = Observable.just(index)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
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
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer == 1) {
                            mSmileListAdapter.notifyDataSetChanged();
                            setRefreshing(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleError(throwable);
                    }
                });
        addSubscription(s);
    }

    /**
     * 刷新，加载数据
     */
    private void getRefreshDatas() {
        try {
            if (NetUtils.checkNet(getActivity())) {
                mSmileDatas = SmileParser.getInstance().getSimle(SMILE_URL);
                //刷新过程中删除旧数据
                MyApp.mLiteOrm.deleteAll(Smile.class);
                //保存下载解析到的新数据
                MyApp.mLiteOrm.save(mSmileDatas);
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
            if (NetUtils.checkNet(getActivity())) {
                mSmileDatas = SmileParser.getInstance().getSimle(SMILE_URL + "index_" + mIndex + ".html");
                //将解析得到的数据全部添加到数据库中做缓存
                mSmileListAdapter.addAll(mSmileDatas);
            } else {
                // 分页查询
                mSmileDatas = MyApp.mLiteOrm.query(query.limit(mStart + 10, 10));
                mSmileListAdapter.addAll(mSmileDatas);
                SnackUtils.show(mRecyclerView, R.string.net_unconnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        mIndex = 1;
        fetchSmileData(LOAD_REFRESH);
    }
}
