package me.zsj.smile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.zsj.model.Smile;

/**
 * Created by zsj on 2015/7/17 0017.
 */
public class SmileListAdapter extends RecyclerView.Adapter<SmileListAdapter.MyViewHolder> {

    private List<Smile> mDatas;
    private LayoutInflater mInflater;

    public SmileListAdapter(Context context, List<Smile> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    public void addAll(List<Smile> datas) {
        mDatas.addAll(datas);
    }

    public void setDatas(List<Smile> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.smile_itemview, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (mDatas.get(position).getSmileContent() != null &&
                !mDatas.get(position).getSmileContent().equals("")) {

            holder.tv_smile.setText(" ^_^");
            holder.tv_content.setText(mDatas.get(position).getSmileContent());
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_smile, tv_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_smile = (TextView)itemView.findViewById(R.id.tv_smile);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);

        }
    }
}
