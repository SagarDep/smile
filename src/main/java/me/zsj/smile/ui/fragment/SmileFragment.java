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
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    QueryBuilder query = new QueryBuilder(Smile.class);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (NetUtils.checkNet(getActivity())) {
            mRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchSmileData(true);
                }
            }, 358);
        } else {
            SnackUtils.show(mRecyclerView, R.string.net_unconnected);
        }

        setupRecyclerView();
        itemClick();
    }

    private void setupRecyclerView() {

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
                if (!mRefreshLayout.isRefreshing() && isButtom && NetUtils.checkNet(getActivity())) {
                    mIndex += 1;
                    fetchSmileData(false);
                }
            }
        });

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

    private void fetchSmileData(final boolean clean) {
        setRefreshing(true);
        Subscription s = getSmileData(clean)
                .doOnNext(new Action1<List<Smile>>() {
                    @Override
                    public void call(List<Smile> smileList) {
                        if (clean) saveSmile(smileList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Smile>>() {
                    @Override
                    public void call(List<Smile> smileList) {
                        if (clean) mSmileDatas.clear();
                        mSmileDatas.addAll(smileList);
                        mSmileListAdapter.notifyDataSetChanged();
                        setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleError(throwable);
                    }
                });
        addSubscription(s);

    }

    private Observable<List<Smile>> getSmileData(final boolean clean) {
        return Observable.create(new Observable.OnSubscribe<List<Smile>>() {
            @Override
            public void call(Subscriber<? super List<Smile>> subscriber) {
                String smileUrl;
                if (clean) {
                    smileUrl = SMILE_URL;
                } else {
                    smileUrl = SMILE_URL + "index_" + mIndex + ".html";
                }
                subscriber.onNext(SmileParser.getInstance().getSimle(smileUrl));
            }
        });
    }

    private void saveSmile(List<Smile> smileList) {
        MyApp.mLiteOrm.deleteAll(Smile.class);
        MyApp.mLiteOrm.save(smileList);
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        mIndex = 1;
        fetchSmileData(true);
    }

}
