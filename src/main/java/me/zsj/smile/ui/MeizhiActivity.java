package me.zsj.smile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zsj.smile.R;
import me.zsj.smile.utils.RxMeizhi;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zsj on 2015/9/18 0018.
 */
public class MeizhiActivity extends AppCompatActivity {

    private String mMeizhiUrl;
    private String mMeizhiDate;
    public static String TRANSIT_PIC = "picture";
    PhotoViewAttacher mAttacher;
    @Bind(R.id.iv_photo) ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meizhi_view);
        ButterKnife.bind(this);


        mMeizhiUrl = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_URL);
        mMeizhiDate = getIntent().getExtras().getString(MeizhiListActivity.MEIZHI_DATE);

        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);
        Glide.with(this).load(mMeizhiUrl).into(mImageView);

        mAttacher = new PhotoViewAttacher(mImageView);

    }

    @OnClick(R.id.fav)
    public void favoriteGril() {
        Toast.makeText(this, "功能还在开发当中", Toast.LENGTH_LONG).show();
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
                        shareIntent.putExtra(Intent.EXTRA_STREAM, (Uri)o);
                        shareIntent.setType("image/jpeg");
                        startActivity(Intent.createChooser(shareIntent, "分享图片到..."));
                    }
                });
    }


}
