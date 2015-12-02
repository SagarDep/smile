package me.zsj.smile.ui.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import me.zsj.smile.R;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zsj on 2015/11/30 0030.
 */
public class BaseFragment extends Fragment {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public void addSubscription(Subscription s) {

        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }

        mCompositeSubscription.add(s);
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
