package com.example.y.routeplanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.y.routeplanner.R;
import com.example.y.routeplanner.gson.Notify;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {
    private List<Notify> notifies;

    public NotifyAdapter(List<Notify> notifies) {
        this.notifies = notifies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notify notify=notifies.get(position);
        holder.title.setText(notify.getTitle());
        holder.content.setText(notify.getContent());

    }

    @Override
    public int getItemCount() {
        if (notifies==null){
            return 0;
        }
        return notifies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,content;
        ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.notify_title);
            content=itemView.findViewById(R.id.notify_content);
        }
    }
}
