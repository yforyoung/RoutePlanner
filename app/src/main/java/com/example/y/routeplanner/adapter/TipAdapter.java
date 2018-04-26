package com.example.y.routeplanner.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.R;

import java.util.List;



public class TipAdapter extends RecyclerView.Adapter<TipAdapter.ViewHolder> implements AdapterView.OnClickListener{

    private List<Tip> tipList;
    private OnItemClickListener onItemClickListener=null;

    @Override
    public void onClick(View view) {
        if (onItemClickListener!=null){
            onItemClickListener.onItemClick(view,(int)view.getTag());
        }

    }

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    public TipAdapter(List<Tip> tipList) {
        this.tipList = tipList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (tipList.get(position).getPoint()!=null&&tipList.get(position).getPoiID()!=null) {
            holder.localName.setText(tipList.get(position).getName());
            holder.localType.setText(tipList.get(position).getDistrict());
        }
        else if (tipList.get(position).getPoiID()!=null&&tipList.get(position).getPoint()==null){
            holder.localName.setText(tipList.get(position).getName());
            holder.localType.setText("公交线路");
        }
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
        if (tipList!=null)
            return tipList.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView localName;
        TextView localType;
        ViewHolder(View itemView) {
            super(itemView);
            localName=itemView.findViewById(R.id.local_name);
            localType=itemView.findViewById(R.id.local_type);
        }
    }
}
