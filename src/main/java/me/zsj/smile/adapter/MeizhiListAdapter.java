package me.zsj.smile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import me.zsj.smile.event.OnItemTouchListener;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.ui.view.MeizhiImageView;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListAdapter extends RecyclerView.Adapter<MeizhiListAdapter.MeizhiHolder> {

    private Context mContext;
    private List<Meizhi> mMeizhiList;
    private OnItemTouchListener onItemTouchListener;

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public MeizhiListAdapter(Context context, List<Meizhi> meizhiList) {
        this.mContext = context;
        this.mMeizhiList = meizhiList;
    }

    public void addAll(List<Meizhi> datas) {
        mMeizhiList.addAll(datas);
    }

    public void setDatas(List<Meizhi> datas) {
        mMeizhiList.clear();
        mMeizhiList.addAll(datas);
    }

    public List<Meizhi> getDatas() {
        return mMeizhiList;
    }

    @Override
    public MeizhiHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MeizhiHolder holder = new MeizhiHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_meizhi, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MeizhiHolder holder, final int position) {

        Meizhi meizhi = mMeizhiList.get(position);
        int limit = 48;
        String desc = meizhi.getDesc().length() > limit ? meizhi.getDesc().substring(0, limit) + "..." :
                meizhi.getDesc();
        if (desc != null) {
            holder.desc_text.setText(desc);
        }
        holder.imageView.setOriginalSize(50, 53);
        Glide.with(mContext)
                .load(meizhi.getUrl())
                .centerCrop()
                .crossFade()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new ItemClickListener(position));
        holder.meizhi_item.setOnClickListener(new ItemClickListener(position));
    }

    @Override
    public int getItemCount() {
        return mMeizhiList.size();
    }


    class ItemClickListener implements View.OnClickListener {

        int position;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            triggerClickListener(v, position);
        }
    }

    private void triggerClickListener(View v, int position) {
        if (onItemTouchListener != null) {
            onItemTouchListener.setOnItemClickListener(v, position);
        }
    }


    class MeizhiHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.meizhi_imageview) MeizhiImageView imageView;
        @Bind(R.id.meizhi_desc) TextView desc_text;
        @Bind(R.id.meizhi_desc_item) LinearLayout meizhi_item;

        public MeizhiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
