package me.zsj.smile.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import me.zsj.smile.ui.base.BaseFragment;

/**
 * Created by zsj on 2015/11/30 0030.
 */
public class SwipeRefreshFragment extends BaseFragment {

    @Bind(R.id.refreshlayout) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        doRefresh();
    }

    public void requestDataRefresh(){}

    private void doRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(
                    R.color.refresh_progress_3, R.color.refresh_progress_2,
                    R.color.refresh_progress_1);

            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshLayout == null) {
            return;
        }
        if (!refreshing) {
            mRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }else {
            mRefreshLayout.setRefreshing(true);
        }
    }

    public void handleError(Throwable throwable) {
        throwable.printStackTrace();
        setRefreshing(false);
        Toast.makeText(getActivity(), getString(R.string.observable_error), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
