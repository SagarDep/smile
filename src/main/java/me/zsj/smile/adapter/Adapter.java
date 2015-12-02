package me.zsj.smile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

/**
 * Created by zsj on 2015/11/30 0030.
 */
public class Adapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    Context mContext;
    List<E> mDataLists;
    RequestManager requestManager;

    public Adapter(Context context, List<E> dataLists) {
        this.mContext = context;
        this.mDataLists = dataLists;
        this.requestManager = Glide.with(context);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
