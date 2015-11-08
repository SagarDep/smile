package me.zsj.smile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.zsj.smile.DataRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.widget.RevealLayout;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.ScreenUtils;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zsj on 2015/9/15 0015.
 */
public class MeizhiAndSmileActivity extends ToolbarActivity {

    @Bind(R.id.contentView) FrameLayout contentView;
    @Bind(R.id.revealLayout) RevealLayout mRevealLayout;
    @Bind(R.id.image_meizhi) ImageView mMeizhiView;
    @Bind(R.id.smile_joke) TextView mSmileJoke;

    public static final String SMILE_DESCRIPTION = "desc";

    private String mSmileContent;
    private List<Meizhi> mGankMeizhiList;
    private AnimatorSet set = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAppBar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBar.setElevation(0f);
        }

        setNavigationListener();

        mSmileContent = getIntent().getExtras().getString(SMILE_DESCRIPTION);
        mGankMeizhiList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_and_smile;
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeizhiAndSmileActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (NetUtils.checkNet(MeizhiAndSmileActivity.this)) {
            loadBeautifulGril();
        } else {
            SnackUtils.showAction(contentView, "赶紧联网获取最新的妹纸和笑话");
        }
    }

    private void loadBeautifulGril() {
        int page = (int) (Math.random() * 5);
        Subscription s = new DataRetrofit().getService().getMeizhi(page)
                .map(new Func1<MeizhiData, String>() {
                    @Override
                    public String call(MeizhiData meizhiData) {
                        int meizhi = (int) (Math.random() * 10);
                        return meizhiData.results.get(meizhi).url;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String url) {
                        mSmileJoke.setTextColor(Color.TRANSPARENT);
                        mSmileJoke.setText(mSmileContent);
                        Picasso.with(MeizhiAndSmileActivity.this)
                                .load(url)
                                .into(mMeizhiView);
                    }
                });
        addSubscription(s);
    }


    @Override
    public void onBackPressed() {
        contentView.animate().translationY(ScreenUtils.getHeight(this))
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (animation.getDuration() >= 500) {
                            MeizhiAndSmileActivity.super.onBackPressed();
                            overridePendingTransition(0, 0);
                        }
                    }
                })
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meizhi_smile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "分享个笑话给大家: " + mSmileContent);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "分享到..."));
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.reveal_fab)
    public void revealFab() {
        mRevealLayout.setOnRevealFinishListener(new RevealLayout.OnRevealFinishListener() {
            @Override
            public void onRevealFinish(boolean isReveal) {
                if (!isReveal) {
                    smileTextEntrance(set);
                } else {
                    mSmileJoke.setTextColor(Color.TRANSPARENT);
                }

            }
        });
        mRevealLayout.startReveal();
    }

    private void smileTextEntrance(AnimatorSet set) {
        mSmileJoke.setTextColor(Color.parseColor("#ffffff"));
        set.playTogether(
                ObjectAnimator.ofFloat(mSmileJoke, "scaleX", 0.3f, 0.85f, 1.3f, 1f),
                ObjectAnimator.ofFloat(mSmileJoke, "scaleY", 0.3f, 0.85f, 1.3f, 1f),
                ObjectAnimator.ofFloat(mSmileJoke, "alpha", 0, 1)
        );
        set.setDuration(1000);
        set.start();
    }

}
