package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.group.MapNavigateActivity;
import com.group.R;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by cwj on 16/4/20.
 * 线路adapter
 */
public class RouteLineAdapter extends RecyclerViewAdapter<RouteLine> implements LoadMoreRecyclerView.OnItemClickListener {

    public RouteLineAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            RouteLine routeLine = getDataItem(position);
            RouteLineViewHolder viewHolder = (RouteLineViewHolder) holder;
            viewHolder.statisticTv.setText(getStatistics(routeLine));
            viewHolder.nameTv.setText(getName(routeLine, position));
        }
    }

    private String getName(RouteLine routeLine, int position) {
        if (routeLine instanceof TransitRouteLine) {
            return getTransitName(((TransitRouteLine) routeLine).getAllStep());
        } else {
            return "线路" + (position + 1);
        }
    }

    private String getTransitName(List<TransitRouteLine.TransitStep> steps) {
        StringBuilder sb = new StringBuilder("");
        for (TransitRouteLine.TransitStep step : steps) {
            if (step.getVehicleInfo() != null) {
                sb.append(step.getVehicleInfo().getTitle()).append("-");
            }
        }
        if (sb.charAt(sb.length() - 1) == '-')
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String getStatistics(RouteLine routeLine) {
        StringBuilder sb = new StringBuilder("");
        sb.append(getTime(routeLine.getDuration())).append("  |  ")
                .append(getDis(routeLine.getDistance()));
        if (routeLine instanceof TransitRouteLine)//公交则加上步行距离
            sb.append("  |  步行")
                    .append(getWalkDis(((TransitRouteLine) routeLine).getAllStep()));
        return sb.toString();
    }

    private String getDis(int meters) {
        if (meters <= 1000)
            return meters + "米";
        float kilos = (float) meters / 1000;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(kilos) + "公里";
    }

    private String getTime(int seconds) {
        int minute = seconds / 60;
        if (minute <= 0) minute = 1;
        if (minute <= 60)
            return minute + "分钟";
        int hours = minute / 60;
        int spareMinute = minute % 60;
        return hours + "小时" + spareMinute + "分钟";
    }

    private String getWalkDis(List<TransitRouteLine.TransitStep> allStep) {
        int count = 0;
        for (TransitRouteLine.TransitStep step : allStep) {
            if (step.getStepType() == TransitRouteLine.TransitStep.TransitRouteStepType.WAKLING)
                count += step.getDistance();
        }
        return getDis(count);
    }

    @Override
    public void onItemClick(int position) {
        RouteLine routeLine = getDataItem(position);
        setStepWayPoints(routeLine);//需要获取一下路径节点,否则传过去后路径节点为0,无法重新计算
        Intent intent = new Intent(context, MapNavigateActivity.class);
        intent.putExtra(MapNavigateActivity.ROUTE_LINE_KEY, routeLine);
        intent.putExtra(MapNavigateActivity.ROUTE_NAME_KEY, getName(routeLine, position));
        intent.putExtra(MapNavigateActivity.ROUTE_STATISTIC_KEY, getStatistics(routeLine));
        context.startActivity(intent);
    }

    private void setStepWayPoints(RouteLine routeLine) {
        if (routeLine instanceof TransitRouteLine) {
            for (TransitRouteLine.TransitStep step : ((TransitRouteLine) routeLine).getAllStep()) {
                step.getWayPoints();
            }
        } else if (routeLine instanceof WalkingRouteLine) {
            for (WalkingRouteLine.WalkingStep step : ((WalkingRouteLine) routeLine).getAllStep()) {
                step.getWayPoints();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new RouteLineViewHolder(layoutInflater.inflate(R.layout.route_line_item, parent, false));
    }

    public class RouteLineViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView statisticTv;

        public RouteLineViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.route_line_item_name_tv);
            statisticTv = (TextView) itemView.findViewById(R.id.route_line_item_statistics_tv);
        }
    }
}
