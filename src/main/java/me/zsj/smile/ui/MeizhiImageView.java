package me.zsj.smile.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiImageView extends ImageView {

    private int originalWidth;
    private int originalHeight;

    public MeizhiImageView(Context context) {
        super(context);
    }

    public MeizhiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MeizhiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOriginalSize(int originalWidth, int originalHeight) {
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (originalWidth > 0 && originalHeight > 0) {
            float ratio = (float) originalWidth / (float) originalHeight;

            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            // TODO: 现在只支持固定宽度
            if (width > 0) {
                height = (int) ((float) width / ratio);
            }

            setMeasuredDimension(width, height);
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
