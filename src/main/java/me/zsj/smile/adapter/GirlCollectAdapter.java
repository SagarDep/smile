package me.zsj.smile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import me.zsj.smile.event.OnMeizhiItemTouchListener;
import me.zsj.smile.model.GirlCollect;
import me.zsj.smile.widget.MeizhiImageView;

/**
 * Created by zsj on 2015/11/5 0005.
 */
public class GirlCollectAdapter extends RecyclerView.Adapter<GirlCollectAdapter.GirlCollectHolder> {

    private Context mContext;
    private List<GirlCollect> mLoveCollect;
    private OnMeizhiItemTouchListener onMeizhiItemTouchListener;

    public void setOnMeizhiItemTouchListener(OnMeizhiItemTouchListener onMeizhiItemTouchListener) {
        this.onMeizhiItemTouchListener = onMeizhiItemTouchListener;
    }

    public GirlCollectAdapter(Context context, List<GirlCollect> collectList) {
        this.mContext = context;
        this.mLoveCollect = collectList;
    }


    @Override
    public GirlCollectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GirlCollectHolder holder = new GirlCollectHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_love, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(GirlCollectHolder holder, int position) {

        holder.imageView.setOriginalSize(50, 53);
        Glide.with(mContext)
                .load(mLoveCollect.get(position).girlUrl)
                .centerCrop()
                .into(holder.imageView);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mLoveCollect.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    class GirlCollectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.love_imageView)
        MeizhiImageView imageView;
        int position;

        public GirlCollectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onMeizhiItemTouchListener != null) {
                onMeizhiItemTouchListener.onItemClick(v, position);
            }
        }
    }
}
