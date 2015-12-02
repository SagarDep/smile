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
public class SmileListAdapter extends Adapter<Smile, SmileListAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private OnSmileItemTouchListener onSmileItemTouchListener;


    public void setOnSmileItemClickListener(OnSmileItemTouchListener onSmileItemTouchListener) {
        this.onSmileItemTouchListener = onSmileItemTouchListener;
    }

    public SmileListAdapter(Context context, List<Smile> datas) {
        super(context, datas);
        mInflater = LayoutInflater.from(context);
    }

    public void addAll(List<Smile> datas) {
        mDataLists.addAll(datas);
    }

    public void setDatas(List<Smile> datas) {
        mDataLists.clear();
        mDataLists.addAll(datas);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(
                mInflater.inflate(R.layout.item_smile, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Smile smile = mDataLists.get(position);
        holder.smile = smile;
        holder.tv_smile.setText(mDataLists.get(position).title);
        holder.tv_content.setText(new SpannableString(mDataLists.get(position).smileContent));

    }


    @Override
    public int getItemCount() {
        return mDataLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.tv_smile) TextView tv_smile;
        @Bind(R.id.tv_content) TextView tv_content;
        @Bind(R.id.smile_item) LinearLayout layout;
        Smile smile;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSmileItemTouchListener.onItemClick(v, smile);
        }
    }

}
