package com.example.y.routeplanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.y.routeplanner.R;
import com.example.y.routeplanner.gson.CollectionPoint;

import java.util.List;


public class CollectionPointAdapter extends RecyclerView.Adapter<CollectionPointAdapter.ViewHolder> implements AdapterView.OnClickListener,AdapterView.OnLongClickListener {
    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener=null;

    private List<CollectionPoint> points;

    public CollectionPointAdapter(List<CollectionPoint> points) {
        this.points = points;
    }

    @Override
    public CollectionPointAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent,false);
        CollectionPointAdapter.ViewHolder holder = new CollectionPointAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(CollectionPointAdapter.ViewHolder holder, int position) {
        holder.textView.setText(points.get(position).getLocation_name());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return points.size();
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
            textView = itemView.findViewById(R.id.text1);
        }
    }

    public void setOnItemClickListener(CollectionPointAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View v,int position);
    }
}
