package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.constant.TagEnum;
import com.dao.generate.City;
import com.group.MerchantDetailActivity;
import com.group.R;
import com.imageLoader.ImageLoader;
import com.model.Area;
import com.model.Category;
import com.model.Merchant;
import com.util.AppSetting;
import com.util.UIUtils;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

import java.util.List;

/**
 * Created by cwj on 16/3/11.
 * 商家列表adapter
 */
public class MerchantAdapter extends RecyclerViewAdapter<Merchant> implements LoadMoreRecyclerView.OnItemClickListener {

    private BDLocation bdLocation;


    public MerchantAdapter(Context context) {
        super(context);
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Merchant merchant = getDataItem(position);
            MerchantViewHolder viewHolder = (MerchantViewHolder) holder;
            viewHolder.nameTv.setText(merchant.getName());
            viewHolder.remarkNumTv.setText(merchant.getRemarkNum() + "评论");
            viewHolder.averageTv.setText("人均¥" + merchant.getAverage());
            viewHolder.servicesTv.setText(getServices(merchant.getServices()));
            viewHolder.categoryTv.setText(getCategoryName(merchant));
            viewHolder.areaTv.setText(getAreaName(merchant));
            viewHolder.distanceTv.setText(getDistance(merchant));
            viewHolder.ratingBar.setRating((float) merchant.getPoint());
            setTags(viewHolder.tagsLayout, merchant.getTags());//添加tag
            if (merchant.getImages() != null && merchant.getImages().size() > 0)
                ImageLoader.displayImage(viewHolder.imageView, merchant.getImages().get(0));//加载第一张图片
            else viewHolder.imageView.setImageDrawable(null);
        }
    }

    private void setTags(LinearLayout tagsLayout, List<Integer> tags) {
        tagsLayout.removeAllViews();
        if (tags != null && tags.size() > 0) {
            for (Integer integer : tags) {
                TextView textView = TagEnum.getTagView(context, integer);
                if (textView != null) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(context, 16), UIUtils.dp2px(context, 16));
                    params.leftMargin = UIUtils.dp2px(context, 8);
                    tagsLayout.addView(textView, params);
                }
            }
        }
    }

    private String getDistance(Merchant merchant) {
        City city = AppSetting.getCity();
        //没有定位信息或商家不在当前城市则不计算距离
        if (bdLocation == null || city == null || city.getCityId() != merchant.getCityId() || merchant.getLocation() == null)
            return "";
        LatLng cur = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        LatLng des = new LatLng(merchant.getLocation().getLatitude(), merchant.getLocation().getLongitude());
        double distance = DistanceUtil.getDistance(cur, des);//米,错误时-1
        if (distance == -1)
            return "";
        double dis = Double.parseDouble(String.format("%.1f", distance / 1000));
        if (dis < 0.5)
            return "<0.5km";
        return dis + "km";
    }

    private String getAreaName(Merchant merchant) {
        Area subArea = merchant.getSubArea();
        Area area = merchant.getArea();
        if (subArea != null)//优先显示子商圈
            return subArea.getName();
        if (area != null)
            return area.getName();
        return "";
    }

    private String getCategoryName(Merchant merchant) {
        Category category = merchant.getCategory();
        Category subCat = merchant.getSubCategory();
        if (subCat != null)//优先显示子品类
            return subCat.getName();
        if (category != null)
            return category.getName();
        return "";
    }

    private String getServices(List<String> services) {
        if (services == null)
            return "";
        StringBuilder sb = new StringBuilder("");
        for (String service : services) {
            sb.append(service).append(" ");
        }
        return sb.toString();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new MerchantViewHolder(layoutInflater.inflate(R.layout.merchant_item, parent, false));
    }

    @Override
    public void onItemClick(int position) {
        //跳转到详情页
        Intent intent = new Intent(context, MerchantDetailActivity.class);
        intent.putExtra(MerchantDetailActivity.MERCHANT_KEY, getDataItem(position));
        context.startActivity(intent);
    }

    public class MerchantViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        LinearLayout tagsLayout;
        RatingBar ratingBar;

        TextView nameTv;
        TextView remarkNumTv;
        TextView averageTv;
        TextView servicesTv;
        TextView categoryTv;
        TextView areaTv;
        TextView distanceTv;

        public MerchantViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.merchant_item_iv);
            nameTv = (TextView) itemView.findViewById(R.id.merchant_item_name);
            tagsLayout = (LinearLayout) itemView.findViewById(R.id.merchant_item_tags);
            ratingBar = (RatingBar) itemView.findViewById(R.id.merchant_item_rating);
            remarkNumTv = (TextView) itemView.findViewById(R.id.merchant_item_remark_num);
            averageTv = (TextView) itemView.findViewById(R.id.merchant_item_average);
            servicesTv = (TextView) itemView.findViewById(R.id.merchant_item_services);
            categoryTv = (TextView) itemView.findViewById(R.id.merchant_item_category);
            areaTv = (TextView) itemView.findViewById(R.id.merchant_item_area);
            distanceTv = (TextView) itemView.findViewById(R.id.merchant_item_distance);
        }
    }

    public void setBdLocation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
        notifyDataSetChanged();
    }
}
