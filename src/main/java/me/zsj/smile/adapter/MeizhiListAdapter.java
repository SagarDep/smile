package me.zsj.smile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.widget.MeizhiImageView;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListAdapter extends Adapter<Meizhi, MeizhiListAdapter.MeizhiHolder> {

    private OnMeizhiItemTouchListener onMeizhiItemTouchListener;

    public void setOnMeizhiItemTouchListener(OnMeizhiItemTouchListener onMeizhiItemTouchListener) {
        this.onMeizhiItemTouchListener = onMeizhiItemTouchListener;
    }

    public MeizhiListAdapter(Context context, List<Meizhi> meizhiList) {
        super(context, meizhiList);
    }

    @Override
    public MeizhiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MeizhiHolder holder = new MeizhiHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_meizhi, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MeizhiHolder holder, final int position) {

        Meizhi meizhi = mDataLists.get(position);
        int limit = 48;
        String desc = meizhi.desc.length() > limit ? meizhi.desc.substring(0, limit) + "..." :
                meizhi.desc;
        holder.desc_text.setText(desc);
        Glide.clear(holder.imageView);
        requestManager.load(meizhi.url)
                .centerCrop()
                .into(holder.imageView);

        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mDataLists.size();
    }


    class MeizhiHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.meizhi_imageview) MeizhiImageView imageView;
        @Bind(R.id.meizhi_desc) TextView desc_text;
        @Bind(R.id.meizhi_desc_item) LinearLayout meizhi_item;
        int position;

        public MeizhiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            meizhi_item.setOnClickListener(this);
            imageView.setOnClickListener(this);
            imageView.setOriginalSize(50, 50);
        }

        @Override
        public void onClick(View v) {
            onMeizhiItemTouchListener.onItemClick(v, position);
        }

    }
}
