package me.zsj.smile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.adapter.AppPagerAdapter;
import me.zsj.smile.R;
import me.zsj.smile.ui.fragment.MeizhiFragment;
import me.zsj.smile.ui.fragment.SmileFragment;

public class MainActivity extends ToolbarActivity {

    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.tabs) TabLayout mTabLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (mViewPager != null)
            setupViewPager(mViewPager);

        if (mTabLayout != null)
            mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void setupViewPager(ViewPager viewPager) {
        AppPagerAdapter adapter = new AppPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MeizhiFragment(), "妹纸");
        adapter.addFragment(new SmileFragment(), "笑话");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
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
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.love_collect_activity:
                Intent i = new Intent(this, LoveCollectActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
