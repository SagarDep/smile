package me.zsj.smile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zsj.smile.R;
import me.zsj.smile.event.OnSmileItemTouchListener;
import me.zsj.smile.model.Smile;

/**
 * Created by zsj on 2015/7/17 0017.
 */
public class SmileListAdapter extends RecyclerView.Adapter<SmileListAdapter.MyViewHolder> {

    private List<Smile> mDatas;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnSmileItemTouchListener onSmileItemTouchListener;


    public void setOnSmileItemClickListener(OnSmileItemTouchListener onSmileItemTouchListener) {
        this.onSmileItemTouchListener = onSmileItemTouchListener;
    }

    public SmileListAdapter(Context context, List<Smile> datas) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mDatas = datas;
    }

    public void addAll(List<Smile> datas) {
        mDatas.addAll(datas);
    }

    public void setDatas(List<Smile> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }

    public List<Smile> getDatas() {
        return mDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.item_smile, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Smile smile = mDatas.get(position);
        holder.smile = smile;
        holder.tv_smile.setText(mDatas.get(position).getTitle());
        holder.tv_content.setText(new SpannableString(mDatas.get(position).getSmileContent()));

    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.tv_smile) TextView tv_smile;
        @Bind(R.id.tv_content) TextView tv_content;
        @Bind(R.id.smile_item) LinearLayout linearLayout;
        Smile smile;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSmileItemTouchListener.onItemClick(v, smile);
        }
    }

}
