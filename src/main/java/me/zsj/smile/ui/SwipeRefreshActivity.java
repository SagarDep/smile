package me.zsj.smile.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import butterknife.Bind;
import me.zsj.smile.R;

/**
 * Created by zsj on 2015/9/14 0014.
 */
public abstract class SwipeRefreshActivity extends ToolbarActivity {

    @Bind(R.id.refreshlayout) SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doRefresh();
    }

    public void requestDataRefresh(){}

    private void doRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeColors(
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
            }, 500);
        }else {
            mRefreshLayout.setRefreshing(true);
        }
    }
}
