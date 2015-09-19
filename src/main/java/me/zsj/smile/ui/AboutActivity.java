package me.zsj.smile.ui;

import android.os.Bundle;
import android.view.View;

import me.zsj.smile.R;

/**
 * Created by zsj on 2015/9/19 0019.
 */
public class AboutActivity extends ToolbarActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });
    }

}
