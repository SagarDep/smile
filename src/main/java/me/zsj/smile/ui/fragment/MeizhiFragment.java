package me.zsj.smile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.zsj.smile.Data;
import me.zsj.smile.DataRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.adapter.MeizhiListAdapter;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.data.RestVideoData;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.Gank;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.ui.MeizhiActivity;
import me.zsj.smile.ui.VideoActivity;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/11/29 0029.
 */
public class MeizhiFragment extends SwipeRefreshFragment {

    private int mPage = 1;
    public static final String MEIZHI_URL = "MEIZHI_URL";
    public static final String MEIZHI_DATE = "MEIZHI_DATE";
    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String VIDEO_DESC = "VIDEO_DESC";

    private MeizhiListAdapter mMeizhiListAdapter;

    private boolean mIsFirstTouch = true;

    private List<Meizhi> mMeizhiLists = new ArrayList<>();
    private List<Gank> mGankLists = new ArrayList<>();
    private Data sData;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sData == null) {
            sData = new DataRetrofit().getService();
        }
        setRecyclerView();
        itemClick();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (NetUtils.checkNet(getActivity())) {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                }
            }, 358);
            fetchMeizhiData(true);
        }else {
            Toast.makeText(getActivity(), "请连接网络获取数据!", Toast.LENGTH_LONG).show();
        }

    }

    private void itemClick() {
        mMeizhiListAdapter.setOnMeizhiItemTouchListener(new OnMeizhiItemTouchListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                if (view.getId() == R.id.meizhi_imageview) {
                    Picasso.with(getActivity()).load(mMeizhiLists.get(position).url).fetch(new Callback() {
                        @Override
                        public void onSuccess() {
                            startToMeizhiActivity(view, position);
                        }
                        @Override
                        public void onError() {}
                    });
                } else if (view.getId() == R.id.meizhi_desc_item) {
                    startToVideoActivity(position);
                }
            }
        });

    }

    private void startToVideoActivity(int position) {
        if (getVideoDataCount() > position) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra(VIDEO_URL, mGankLists.get(position).url);
            intent.putExtra(VIDEO_DESC, mGankLists.get(position).desc);
            startActivity(intent);
        } else {
            SnackUtils.show(mRecyclerView, "没有视频啦啦啦!!!");
        }
    }

    private void startToMeizhiActivity(View view, int position) {
        Intent intent = new Intent(getActivity(), MeizhiActivity.class);
        intent.putExtra(MEIZHI_URL, mMeizhiLists.get(position).url);
        intent.putExtra(MEIZHI_DATE,
                mMeizhiLists.get(position).publishedAt.substring(0, 10));
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        view, MeizhiActivity.TRANSIT_PIC);
        ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
    }

    private void setRecyclerView() {

        mRecyclerView.setHasFixedSize(true);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mMeizhiListAdapter = new MeizhiListAdapter(getActivity(), mMeizhiLists);
        mRecyclerView.setAdapter(mMeizhiListAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean isButtom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                        >= mMeizhiLists.size() - 4;
                if (!mRefreshLayout.isRefreshing() && isButtom) {
                    if (!mIsFirstTouch) {
                        setRefreshing(true);
                        mPage += 1;
                        fetchMeizhiData(false);
                    } else {
                        mIsFirstTouch = false;
                    }
                }
            }
        });
    }


    private void fetchMeizhiData(final boolean clean) {
        Subscription s = Observable.zip(sData.getMeizhi(mPage), sData.getRestVideoData(mPage),
                new Func2<MeizhiData, RestVideoData, List<Meizhi>>() {
                    @Override
                    public List<Meizhi> call(MeizhiData meizhiData, RestVideoData restVideoData) {
                        meizhiData = createMeizhiDataWithVedioData(meizhiData, restVideoData);
                        if (clean) saveVideoData(restVideoData.results);
                        else addAllVideoData(restVideoData.results);

                        return meizhiData.results;
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

    private MeizhiData createMeizhiDataWithVedioData(MeizhiData meizhiData, RestVideoData restVideoData) {

        for (int i = 0; i < restVideoData.results.size(); i++) {
            Meizhi meizhi = meizhiData.results.get(i);
            meizhi.desc = meizhi.desc + " " + restVideoData.results.get(i).desc;
        }
        return meizhiData;
    }

    private void saveVideoData(List<Gank> videoData) {
        mGankLists.clear();
        mGankLists.addAll(videoData);
    }

    private void addAllVideoData(List<Gank> videoData) {
        mGankLists.addAll(videoData);
    }

    private int getVideoDataCount() {
        return mGankLists.size();
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();

        mPage = 1;
        setRefreshing(true);
        fetchMeizhiData(true);
    }

}
