package com.example.y.routeplanner.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.example.y.routeplanner.R;

import java.util.ArrayList;
import java.util.List;



public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> implements AdapterView.OnClickListener{
    private List<BusPath> busPathList;
    private OnItemClickListener onItemClickListener=null;
    private int minTime=100000;
    private int minWalk=100000;
    private int minChange=100000;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_path,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BusPath busPath=busPathList.get(position);
        holder.money.setText(busPath.getCost()+"元");
        holder.walk.setText("步行"+busPath.getWalkDistance()+"米");

        String s="";
        List<BusStep> stepList=busPath.getSteps();
        int t=0;
        if (stepList!=null){
            for (BusStep bs:stepList){
                if (bs.getWalk()!=null){
                    t+=bs.getWalk().getDuration();//步行时间
                }

                List<RouteBusLineItem> lineItemList=bs.getBusLines(); //必有
                String routeBusName="";
                for (RouteBusLineItem lineItem:lineItemList){
                    t+=lineItem.getDuration();//乘车时间
                    if (routeBusName=="")
                        routeBusName=lineItem.getBusLineName().replaceAll("\\(.*\\)","");
                    else
                        routeBusName+="/"+lineItem.getBusLineName().replaceAll("\\(.*\\)","");
                }
                if (s=="")
                    s=routeBusName;
                else
                    if (routeBusName!="")
                        s=s+"  →  "+routeBusName;
            }
        }
        holder.time.setText((t/60)+"分钟");
        holder.step.setText(s);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (busPathList!=null)
            return busPathList.size();
        return 0;
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener!=null)
            onItemClickListener.onItemClick(view, (int) view.getTag());

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView time,walk,money,step,pathFirst;


        ViewHolder(View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.route_time);
            walk=itemView.findViewById(R.id.route_walk);
            money=itemView.findViewById(R.id.route_money);
            step=itemView.findViewById(R.id.route_step);
            pathFirst=itemView.findViewById(R.id.path_first);
        }
    }


    public RouteAdapter(List<BusPath> busPathList) {
        this.busPathList = busPathList;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
}
