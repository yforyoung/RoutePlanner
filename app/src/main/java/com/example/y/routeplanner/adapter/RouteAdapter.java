package com.example.y.routeplanner.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import java.util.List;


public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> implements AdapterView.OnClickListener {
    private List<BusPath> busPathList;
    private static final int MAX=100000;
    private OnItemClickListener onItemClickListener = null;
    private int minTime = -1;
    private float minWalk = -1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_path, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        BusPath busPath = busPathList.get(position);
        holder.money.setText(busPath.getCost() + "元");
        holder.walk.setText("步行" + busPath.getWalkDistance() + "米");
        setFirst();
        String s = "";
        List<BusStep> stepList = busPath.getSteps();
        int t = 0;
        if (stepList != null) {
            for (BusStep bs : stepList) {
                if (bs.getWalk() != null) {
                    t += bs.getWalk().getDuration();//步行时间
                }
                List<RouteBusLineItem> lineItemList = bs.getBusLines(); //必有
                String routeBusName = "";
                for (RouteBusLineItem lineItem : lineItemList) {
                    t += lineItem.getDuration();//乘车时间
                    if (routeBusName.equals(""))
                        routeBusName = lineItem.getBusLineName().replaceAll("\\(.*\\)", "");
                    else
                        routeBusName += "/" + lineItem.getBusLineName().replaceAll("\\(.*\\)", "");
                }
                if (s.equals(""))
                    s = routeBusName;
                else if (!routeBusName.equals(""))
                    s = s + "  →  " + routeBusName;
            }
        }
        if (busPath.getWalkDistance() == minWalk) {
            holder.pathFirst.setVisibility(View.VISIBLE);
            holder.pathFirst.setText("步行最少");
        }
        if (t == minTime) {
            holder.pathFirst.setVisibility(View.VISIBLE);
            holder.pathFirst.setText("时间最短");
            holder.pathFirst.setBackgroundColor(Color.RED);
        }
        if (position==busPathList.size()-1){
            minWalk=-1;
            minTime=-1;
        }
        holder.time.setText((t / 60) + "分钟");
        holder.step.setText(s);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (busPathList != null)
            return busPathList.size();
        return 0;
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(view, (int) view.getTag());

    }


    private void setFirst() {
        if (minWalk == -1 || minTime == -1) {      //找出最小
            minWalk = MAX;
            minTime = MAX;
            for (BusPath busPath : busPathList) {
                if (minWalk > busPath.getWalkDistance())
                    minWalk = busPath.getWalkDistance();

                List<BusStep> stepList = busPath.getSteps();
                int t = 0;
                if (stepList != null) {
                    for (BusStep bs : stepList) {
                        if (bs.getWalk() != null) {
                            t += bs.getWalk().getDuration();//步行时间
                        }
                        List<RouteBusLineItem> lineItemList = bs.getBusLines();

                        for (RouteBusLineItem lineItem : lineItemList) {
                            t += lineItem.getDuration();//乘车时间
                        }
                    }
                }
                if (t < minTime) {
                    minTime = t;
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, walk, money, step, pathFirst;
        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.route_time);
            walk = itemView.findViewById(R.id.route_walk);
            money = itemView.findViewById(R.id.route_money);
            step = itemView.findViewById(R.id.route_step);
            pathFirst = itemView.findViewById(R.id.path_first);
        }
    }


    public RouteAdapter(List<BusPath> busPathList) {
        this.busPathList = busPathList;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
