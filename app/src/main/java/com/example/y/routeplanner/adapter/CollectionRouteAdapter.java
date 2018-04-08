package com.example.y.routeplanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.y.routeplanner.R;
import com.example.y.routeplanner.gson.MyPath;

import java.util.ArrayList;
import java.util.List;


public class CollectionRouteAdapter extends RecyclerView.Adapter<CollectionRouteAdapter.ViewHolder> implements AdapterView.OnClickListener,View.OnLongClickListener{
    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener=null;
    private List<MyPath> myPaths;

    public CollectionRouteAdapter(List<MyPath> myPaths) {
        this.myPaths = myPaths;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(myPaths.get(position).getCollection_name());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return myPaths.size();
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
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
