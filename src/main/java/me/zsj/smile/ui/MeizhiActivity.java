package me.zsj.smile.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.squareup.picasso.Picasso;


import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zsj.smile.MyApp;
import me.zsj.smile.R;
import me.zsj.smile.model.GirlCollect;
import me.zsj.smile.ui.fragment.MeizhiFragment;
import me.zsj.smile.utils.RxMeizhi;
import me.zsj.smile.widget.PullBackLayout;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zsj on 2015/9/18 0018.
 */
public class MeizhiActivity extends AppCompatActivity {


    @Bind(R.id.iv_photo) ImageView mImageView;
    @Bind(R.id.pullBackLayout) PullBackLayout mPullBackLayout;
    @Bind(R.id.fav) FloatingActionButton mFavFAB;
    @Bind(R.id.share) FloatingActionButton mShareFAB;

    private PhotoViewAttacher mViewAttacher;
    private Subscription mSubscription;


    private String mMeizhiUrl;
    private String mMeizhiDate;
    public static final String TRANSIT_PIC = "picture";

    private boolean mIsGirlCollected;
    private boolean mIsFabAppear = true;

    final AnimatorSet set = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meizhi_view);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.BLACK);

        mMeizhiUrl = getIntent().getExtras().getString(MeizhiFragment.MEIZHI_URL);
        mMeizhiDate = getIntent().getExtras().getString(MeizhiFragment.MEIZHI_DATE);

        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);
        Picasso.with(this).load(mMeizhiUrl).into(mImageView);

        mViewAttacher = new PhotoViewAttacher(mImageView);
        mPullBackLayout.setPullCallBack(new PullBackLayout.PullCallBack() {
            @Override
            public void onPullCompleted() {
                MeizhiActivity.this.onBackPressed();
            }
        });

        imageTap();
        checkWhetherCollected();

    }

    private void imageTap() {
        mViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                int yHeight = pxTodp(100);
                if (mIsFabAppear) {
                    set.playTogether(ObjectAnimator.ofFloat(mFavFAB, "translationY", 0, yHeight),
                            ObjectAnimator.ofFloat(mShareFAB, "translationY", 0, yHeight));
                    mIsFabAppear = false;
                } else {
                    set.playTogether(ObjectAnimator.ofFloat(mFavFAB, "translationY", yHeight, 0),
                            ObjectAnimator.ofFloat(mShareFAB, "translationY", yHeight, 0));
                    mIsFabAppear = true;
                }
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.setDuration(180);
                set.start();
            }
        });
    }

    private int pxTodp(int dp) {
        float size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
        return (int) size;
    }


    private void checkWhetherCollected() {
        QueryBuilder query = new QueryBuilder(GirlCollect.class).where("girlUrl = ?", new String[]{mMeizhiUrl});
        long nums = MyApp.mLiteOrm.queryCount(query);
        if (nums == 1) {
            mFavFAB.setImageResource(R.mipmap.ab_fav_active);
            mIsGirlCollected = true;
        } else {
            mIsGirlCollected = false;
        }
    }

    @OnClick(R.id.fav)
    public void favoriteGril() {
        if (mIsGirlCollected) {
            MyApp.mLiteOrm.delete(GirlCollect.class,
                    WhereBuilder.create(GirlCollect.class).equals("girlUrl", mMeizhiUrl));
            mIsGirlCollected = false;
            mFavFAB.setImageResource(R.mipmap.ab_fav_normal);
            toast("取消收藏妹纸...");
        } else {
            saveMeizhi();
            mFavFAB.setImageResource(R.mipmap.ab_fav_active);
            mIsGirlCollected = true;
            toast("成功收藏妹纸...");
        }   }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void saveMeizhi() {
        final GirlCollect girlCollect = new GirlCollect();
        mSubscription = getBitmap(mMeizhiUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        girlCollect.girlDate = mMeizhiDate;
                        girlCollect.girlUrl = mMeizhiUrl;
                        girlCollect.width = bitmap.getWidth();
                        girlCollect.height = bitmap.getHeight();
                        MyApp.mLiteOrm.save(girlCollect);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(MeizhiActivity.this,
                                throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private Observable<Bitmap> getBitmap(final String meizhiUrl) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Glide.with(MeizhiActivity.this).load(meizhiUrl).asBitmap()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    subscriber.onNext(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }finally {
                    subscriber.onCompleted();
                }
            }
        });
    }


    @OnClick(R.id.share)
    public void sharePicture() {
        RxMeizhi.saveImageAndGetPath(this, mMeizhiUrl, mMeizhiDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, (Uri) o);
                        shareIntent.setType("image/jpeg");
                        startActivity(Intent.createChooser(shareIntent, "分享图片到..."));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ButterKnife.unbind(this);
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }
}
