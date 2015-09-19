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
import me.zsj.smile.event.OnItemTouchListener;
import me.zsj.smile.model.Meizhi;
import me.zsj.smile.ui.MeizhiImageView;

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
                LayoutInflater.from(mContext).inflate(R.layout.meizhi_listitem_view, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MeizhiHolder holder, final int position) {

        Meizhi meizhi = mMeizhiList.get(position);
        holder.imageView.setOriginalSize(50, 50);
        Glide.with(mContext)
                .load(meizhi.getUrl())
                .centerCrop()
                .crossFade()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemTouchListener != null) {
                    onItemTouchListener.setOnItemClickListener(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeizhiList.size();
    }

    class MeizhiHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.meizhi_imageview)
        MeizhiImageView imageView;

        public MeizhiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
