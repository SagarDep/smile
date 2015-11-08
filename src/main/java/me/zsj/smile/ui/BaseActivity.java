package me.zsj.smile.ui;

import android.support.v7.app.AppCompatActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zsj on 2015/11/3 0003.
 */
public class BaseActivity extends AppCompatActivity{

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public void addSubscription(Subscription s) {

        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }

        mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }


}
