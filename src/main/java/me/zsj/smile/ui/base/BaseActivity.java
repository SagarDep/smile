package me.zsj.smile.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.zsj.smile.R;
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

    public void handleError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, getString(R.string.observable_error), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
