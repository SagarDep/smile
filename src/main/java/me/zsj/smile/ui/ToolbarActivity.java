package me.zsj.smile.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;

/**
 * Created by zsj on 2015/9/19 0019.
 */
public abstract class ToolbarActivity extends AppCompatActivity{

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.appbar) AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

    abstract protected int getLayoutId();

    public void setBarAlpha(float alpha) {
        mAppBar.setAlpha(alpha);
    }
}
