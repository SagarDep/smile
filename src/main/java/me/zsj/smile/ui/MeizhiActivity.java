package me.zsj.smile.ui;

import android.content.Intent;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zsj on 2015/9/18 0018.
 */
public class MeizhiActivity extends ToolbarActivity{

    @Bind(R.id.imageview_meizhi) ImageView mImageView;

    private String mMeizhiUrl;
    private String mMeizhiDate;
    private boolean mIsHidden;
    public static final String TRANSIT_PIC = "picture";

    //PhotoViewAttacher mPhotoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarAlpha();

        mMeizhiUrl = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_URL);
        mMeizhiDate = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_DATE);

        setTitle(mMeizhiDate);
        setNavigationListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);

        Glide.with(MeizhiActivity.this)
                .load(mMeizhiUrl)
                .into(mImageView);

        //setPhotoAttcher();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.meizhi_view;
    }

    private void setToolbarAlpha() {
        setBarAlpha(0.7f);
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeizhiActivity.this.onBackPressed();
            }
        });
    }

    private void setPhotoAttcher() {

       //mPhotoViewAttacher = new PhotoViewAttacher(mImageView);

    }

    protected void hideOrShowToolbar() {
        mAppBar.animate()
                .translationY(mIsHidden ? 0 : -mAppBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

        mIsHidden = !mIsHidden;
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.Meizhi_Smile));
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
