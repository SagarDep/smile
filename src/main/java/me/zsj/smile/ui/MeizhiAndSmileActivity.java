package me.zsj.smile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.zsj.smile.MeizhiRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.ui.view.RevealLayout;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.ScreenUtils;
import me.zsj.smile.SmileParser;
import me.zsj.smile.utils.SnackUtils;
import rx.Observable;
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

    public static final String MEIZHI_STRATING_LOCATION = "Mezhi_Activity";

    private int drawingStartLocation;
    private String mJokeUrl;
    private String mSmileContent;
    private boolean mIsFirst = true;

    private List<Meizhi> mGankMeizhiList;

    private AnimatorSet set = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBarAlpha(0.7f);

        setNavigationListener();

        drawingStartLocation = getIntent().getExtras().getInt(MEIZHI_STRATING_LOCATION, 0);
        mJokeUrl = getIntent().getExtras().getString(MainActivity.SMILE_DATA_URL);
        //setOnPreDraw(savedInstanceState);
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
            getSmileJoke();
        } else {
            SnackUtils.showAction(contentView, "赶紧联网获取最新的妹纸和笑话");
        }
    }

    private void getSmileJoke() {
        Observable.just("smile")
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        mSmileContent = s = SmileParser.getInstance().getJokeInfo(mJokeUrl);
                        getMeizhi();
                        return s;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        int meizhi = (int) (Math.random() * 10);
                        String imageUrl = mGankMeizhiList.get(meizhi).getUrl();
                        Glide.with(MeizhiAndSmileActivity.this)
                                .load(imageUrl)
                                .centerCrop()
                                .crossFade()
                                .into(mMeizhiView);
                    }
                });
    }

    private void getMeizhi() {
        MeizhiData meizhiData = new MeizhiRetrofit().getService().getMeizhi(1);
        if (!meizhiData.error) {
            mGankMeizhiList.addAll(meizhiData.results);
        }
    }

    private void setOnPreDraw(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startAnimation();
                    return true;
                }
            });
        }
    }

    private void startAnimation() {

        contentView.setScaleX(0.1f);
        contentView.setScaleY(0.1f);
        contentView.setPivotY(drawingStartLocation);

        contentView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1000)
                .start();
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
            startActivity(shareIntent);
        } else if (itemId == R.id.collect_smile) {
            item.setIcon(R.mipmap.ab_fav_active);
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
                }else {
                    mSmileJoke.setText("");
                }
            }
        });
        mRevealLayout.startReveal();
    }

    private void smileTextEntrance(AnimatorSet set) {
        set.playTogether(
                ObjectAnimator.ofFloat(mSmileJoke, "scaleX", 0.3f, 0.85f, 1.3f, 1f),
                ObjectAnimator.ofFloat(mSmileJoke, "scaleY", 0.3f, 0.85f, 1.3f, 1f),
                ObjectAnimator.ofFloat(mSmileJoke, "alpha", 0, 1)
        );
        set.setDuration(1000);
        set.start();
        mSmileJoke.setText(mSmileContent);
    }

}
