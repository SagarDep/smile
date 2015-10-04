package me.zsj.smile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.zsj.smile.MeizhiRetrofit;
import me.zsj.smile.R;
import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.utils.NetUtils;
import me.zsj.smile.utils.ScreenUtils;
import me.zsj.smile.SmileParser;
import me.zsj.smile.utils.SnackUtils;
import me.zsj.smile.utils.TaskUtils;

/**
 * Created by zsj on 2015/9/15 0015.
 */
public class MeizhiAndSmileActivity extends ToolbarActivity {

    @Bind(R.id.contentView) ScrollView contentView;
    @Bind(R.id.image_meizhi) ImageView mMeizhiView;
    @Bind(R.id.smile_joke) TextView mSmileJoke;

    public static final String MEIZHI_STRATING_LOCATION = "Mezhi_Activity";
    private static final int TASK_RESULT = 1;

    private int drawingStartLocation;
    private String mJokeUrl;
    private String mSmileContent;

    private List<Meizhi> mGankMeizhiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationListener();

        drawingStartLocation = getIntent().getExtras().getInt(MEIZHI_STRATING_LOCATION, 0);
        mJokeUrl = getIntent().getExtras().getString(MainActivity.SMILE_DATA_URL);
        setOnPreDraw(savedInstanceState);

        mGankMeizhiList = new ArrayList<>();

        startToMeizhi();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_and_smile;
    }

    private void startToMeizhi() {

        mMeizhiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeizhiAndSmileActivity.this, MeizhiListActivity.class);
                startActivity(intent);
            }
        });
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

        getSmileJoke();
    }

    private void getSmileJoke() {
        TaskUtils.executeTask(new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                if (NetUtils.checkNet(MeizhiAndSmileActivity.this)) {
                    mSmileContent = SmileParser.getInstance().getJokeInfo(params[0]);
                    getMeizhi();
                    return TASK_RESULT;
                }else {
                    return -1;
                }

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                if (NetUtils.checkNet(MeizhiAndSmileActivity.this)) {
                    if (integer.intValue() == TASK_RESULT) {
                        mSmileJoke.setText(mSmileContent);
                        int meizhi = (int) (Math.random() * 10);
                        String imageUrl = mGankMeizhiList.get(meizhi).getUrl();
                        Glide.with(MeizhiAndSmileActivity.this)
                                .load(imageUrl)
                                .centerCrop()
                                .crossFade()
                                .into(mMeizhiView);
                    }
                }else {
                    SnackUtils.showAction(contentView, "赶紧联网获取最新的妹纸和笑话");
                }
            }
        }, mJokeUrl);

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

        contentView.setScaleY(0.1f);
        contentView.setPivotY(drawingStartLocation);

        contentView.animate().scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1000)
                .start();
    }

    @Override
    public void onBackPressed() {

        contentView.animate().translationY(ScreenUtils.getHeight(this))
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        MeizhiAndSmileActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
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

        if (item.getItemId() == R.id.share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "分享个笑话给大家: " + mSmileContent);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
