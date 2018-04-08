package com.example.y.routeplanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import java.util.List;


public class BusLineAdapter extends RecyclerView.Adapter<BusLineAdapter.ViewHolder> implements AdapterView.OnClickListener,AdapterView.OnLongClickListener {
    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener=null;

    private List<BusStationItem> list;

    public BusLineAdapter(List<BusStationItem> list) {
        this.list = list;
    }

    @Override
    public BusLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent,false);
        BusLineAdapter.ViewHolder holder = new BusLineAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BusLineAdapter.ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getBusStationName());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(view, (int) view.getTag());
    }

    @Override
    public boolean onLongClick(View view) {
        if (onItemLongClickListener!=null){
            onItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
        }
        return true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public void setOnItemClickListener(BusLineAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View v, int position);
    }
}
