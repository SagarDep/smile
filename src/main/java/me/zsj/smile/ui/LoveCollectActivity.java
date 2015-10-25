package me.zsj.smile.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import me.zsj.smile.R;

/**
 * Created by zsj on 2015/10/23 0023.
 */
public class LoveCollectActivity extends ToolbarActivity {

    TextView lovetext;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_love_collect;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lovetext = (TextView) findViewById(R.id.lovetext);

    }

}
