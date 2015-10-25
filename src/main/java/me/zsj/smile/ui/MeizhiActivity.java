package me.zsj.smile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import me.zsj.smile.utils.RxMeizhi;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by zsj on 2015/9/18 0018.
 */
public class MeizhiActivity extends ToolbarActivity{

    private String mMeizhiUrl;
    private String mMeizhiDate;
    public static String TRANSIT_PIC = "picture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView mImageView = (ImageView)findViewById(R.id.imageview_meizhi);
        setToolbarAlpha();

        mMeizhiUrl = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_URL);
        mMeizhiDate = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_DATE);

        setTitle(mMeizhiDate);
        setNavigationListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);
        Picasso.with(this)
                .load(mMeizhiUrl)
                .into(mImageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.meizhi_view;
    }

    private void setToolbarAlpha() {
        setBarAlpha(0.5f);
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeizhiActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meizhi_smile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            RxMeizhi.saveImageAndGetPath(this, mMeizhiUrl, mMeizhiDate)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, (Uri)o);
                            shareIntent.setType("image/jpeg");
                            startActivity(Intent.createChooser(shareIntent, "分享图片到..."));
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }
}
