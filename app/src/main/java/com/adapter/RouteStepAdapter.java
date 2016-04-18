package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.group.R;
import com.widget.rlrView.adapter.RecyclerViewAdapter;

/**
 * Created by cwj on 16/4/21.
 * 路线阶段adapter
 */
public class RouteStepAdapter extends RecyclerViewAdapter<RouteStep> {

    public RouteStepAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            RouteStep step = getDataItem(position);
            RouteStepViewHolder viewHolder = (RouteStepViewHolder) holder;
            setIcon(viewHolder.imageView, step);
            setDescribe(viewHolder.describeTv, step);
        }
    }

    private void setDescribe(TextView describeTv, RouteStep step) {
        if (step instanceof TransitRouteLine.TransitStep) {
            describeTv.setText(((TransitRouteLine.TransitStep) step).getInstructions());
        } else if (step instanceof DrivingRouteLine.DrivingStep) {
            describeTv.setText(((DrivingRouteLine.DrivingStep) step).getInstructions());
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            describeTv.setText(((WalkingRouteLine.WalkingStep) step).getInstructions());
        } else {
            describeTv.setText("");
        }
    }

    private void setIcon(ImageView imageView, RouteStep step) {
        if (step instanceof TransitRouteLine.TransitStep) {
            TransitRouteLine.TransitStep.TransitRouteStepType type = ((TransitRouteLine.TransitStep) step).getStepType();
            switch (type) {
                case BUSLINE:
                    imageView.setImageResource(R.drawable.bus_icon_unselected);
                    break;
                case SUBWAY:
                    imageView.setImageResource(R.drawable.subway_icon);
                    break;
                case WAKLING:
                    imageView.setImageResource(R.drawable.walk_icon_unselected);
                    break;
            }
        } else if (step instanceof DrivingRouteLine.DrivingStep) {
            imageView.setImageResource(R.drawable.drive_icon_unselected);
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            imageView.setImageResource(R.drawable.walk_icon_unselected);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new RouteStepViewHolder(layoutInflater.inflate(R.layout.route_step_item, parent, false));
    }

    public class RouteStepViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView describeTv;

        public RouteStepViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.route_step_iv);
            describeTv = (TextView) itemView.findViewById(R.id.route_step_describe_tv);
        }
    }
}
