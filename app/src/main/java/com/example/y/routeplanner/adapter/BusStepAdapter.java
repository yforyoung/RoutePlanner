package com.example.y.routeplanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;

import com.example.y.routeplanner.R;

import java.util.List;


public class BusStepAdapter extends RecyclerView.Adapter<BusStepAdapter.ViewHolder> implements AdapterView.OnClickListener, AdapterView.OnLongClickListener {
    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener = null;

    private List<BusLineItem> list;

    public BusStepAdapter(List<BusLineItem> list) {
        this.list = list;
    }

    @Override
    public BusStepAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_step, parent, false);
        BusStepAdapter.ViewHolder holder = new BusStepAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BusStepAdapter.ViewHolder holder, int position) {
        String s = list.get(position).getBusLineName();
        s = s.replaceFirst("\\(", "\n");
        s = s.substring(0, s.length() - 1);

        holder.name.setText(s.split("\n")[0]);
        holder.forward.setText(s.split("\n")[1]);
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
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
        }
        return true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, forward;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.line_name);
            forward = itemView.findViewById(R.id.line_forward);
        }
    }

    public void setOnItemClickListener(BusStepAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }
}
