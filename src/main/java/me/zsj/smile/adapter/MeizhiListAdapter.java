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
import me.zsj.smile.ui.view.MeizhiImageView;

/**
 * Created by zsj on 2015/9/17 0017.
 */
public class MeizhiListAdapter extends RecyclerView.Adapter<MeizhiListAdapter.MeizhiHolder> {

    private Context mContext;
    private List<Meizhi> mMeizhiList;
    private OnMeizhiItemTouchListener onMeizhiItemTouchListener;


    public void setOnMeizhiItemTouchListener(OnMeizhiItemTouchListener onMeizhiItemTouchListener) {
        this.onMeizhiItemTouchListener = onMeizhiItemTouchListener;
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

        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mMeizhiList.size();
    }


    class MeizhiHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.meizhi_imageview) MeizhiImageView imageView;
        @Bind(R.id.meizhi_desc) TextView desc_text;
        @Bind(R.id.meizhi_desc_item) LinearLayout meizhi_item;
        int position;

        public MeizhiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageView.setOnClickListener(this);
            meizhi_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMeizhiItemTouchListener.onItemClick(v, position);
        }
    }
}
