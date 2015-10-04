package me.zsj.smile.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import me.zsj.smile.R;

/**
 * Created by zsj on 2015/9/19 0019.
 */
public class AboutActivity extends ToolbarActivity {

    @Bind(R.id.my_github)
    TextView mGithubText;
    @Bind(R.id.my_weibo)
    TextView mWeiboText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("关于App");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });

        mGithubText.setMovementMethod(LinkMovementMethod.getInstance());
        mWeiboText.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
