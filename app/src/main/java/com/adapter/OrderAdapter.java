package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.constant.CategoryEnum;
import com.constant.CloudFunction;
import com.constant.OrderStateEnum;
import com.group.AddUpdRemarkActivity;
import com.group.CouponActivity;
import com.group.OrderDetailActivity;
import com.group.R;
import com.group.VoucherDetailActivity;
import com.imageLoader.ImageLoader;
import com.leancloud.SafeFunctionCallback;
import com.model.Category;
import com.model.Order;
import com.model.Voucher;
import com.util.DateUtils;
import com.util.DrawableUtils;
import com.util.JsonUtils;
import com.util.UIUtils;
import com.util.Utils;
import com.widget.RoundImageView;
import com.widget.dialog.LoadingDialog;
import com.widget.dialog.MessageDialog;
import com.widget.rlrView.adapter.RecyclerViewAdapter;
import com.widget.rlrView.view.LoadMoreRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cwj on 16/4/7.
 * 订单adapter
 */
public class OrderAdapter extends RecyclerViewAdapter<Order> implements LoadMoreRecyclerView.OnItemClickListener, LoadMoreRecyclerView.OnItemLongClickListener {

    private Map<TextView, String> viewToId = new HashMap<>();
    private Map<String, Long> idToTime = new HashMap<>();

    private MessageDialog messageDialog;
    private LoadingDialog loadingDialog;

    public OrderAdapter(Context context) {
        super(context);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //更新所有剩余时间
                for (Map.Entry<String, Long> entry : idToTime.entrySet()) {
                    long spareSeconds = entry.getValue() - 1;
                    if (spareSeconds < 0)
                        spareSeconds = 0;
                    entry.setValue(spareSeconds);
                }
                //更新所有textView
                for (Map.Entry<TextView, String> entry : viewToId.entrySet()) {
                    TextView countTv = entry.getKey();
                    String id = entry.getValue();
                    long newSpareSeconds = idToTime.get(id);
                    if (newSpareSeconds <= 0) {//过期
                        //移除相应订单的记录
                        idToTime.remove(id);
                        viewToId.remove(countTv);
                        countTv.setText("已过期");
                    } else {
                        String spareTime = DateUtils.getTimeStringOfSeconds(newSpareSeconds);
                        countTv.setText(spareTime);
                    }
                }
                this.sendEmptyMessageDelayed(0, 1000);
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);//每隔一秒发送
    }

    @Override
    public void onHolderBind(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Order order = getDataItem(position);
            OrderViewHolder viewHolder = (OrderViewHolder) holder;
            viewHolder.merchantNameTv.setText(order.getVoucher().getMerchant().getName());
            viewHolder.voucherNameTv.setText(order.getVoucher().getName());
            viewHolder.numTv.setText("购买数量:" + order.getNum());
            viewHolder.priceTv.setText("总价¥" + order.getTotalPrice());
            //品类图标
            setCatIv(viewHolder.categoryIv, order.getVoucher().getMerchant().getCategory());
            //商家图片
            setMerchantIv(viewHolder.merchantIv, order.getVoucher());
            //设置状态
            setStatus(viewHolder.statusTv, order.getStatus());
            //设置剩余时间提示
            setSpareTime(viewHolder.spareTimeLayout, viewHolder.countTimeTv, order);
            //设置功能按钮
            setFunc(viewHolder, order);
        }
    }

    private void setFunc(final OrderViewHolder viewHolder, final Order order) {
        TextView functionTv = viewHolder.functionTv;
        final OrderStateEnum stateEnum = OrderStateEnum.getEnumMap(order.getStatus());
        if (stateEnum == null) {
            functionTv.setVisibility(View.INVISIBLE);
            return;
        }
        functionTv.setVisibility(View.VISIBLE);
        switch (stateEnum) {
            case WAIT_PAY:
                functionTv.setText("去付款");
                break;
            case WAIT_USE:
                functionTv.setText("团购券");
                break;
            case USED:
                functionTv.setText("去评价");
                break;
            case REMARKED:
                functionTv.setText("再来一单");
                break;
            case OVERDUE:
                functionTv.setText("订单详情");
                break;
        }
        functionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (stateEnum) {
                    case WAIT_PAY://支付,直接和item的点击一样
                        viewHolder.itemView.performClick();
                        break;
                    case WAIT_USE://查看团购券
                        coupon(order);
                        break;
                    case USED://评价
                        remark(order);
                        break;
                    case REMARKED://再来一单
                        gotoVoucher(order);
                        break;
                    case OVERDUE://查看详情,直接和item的点击一样
                        viewHolder.itemView.performClick();
                        break;
                }
            }
        });
    }

    private void coupon(Order order) {
        Intent intent = new Intent(context, CouponActivity.class);
        intent.putExtra(CouponActivity.ORDER_KEY, order);
        context.startActivity(intent);
    }

    private void gotoVoucher(Order order) {
        Intent intent = new Intent(context, VoucherDetailActivity.class);
        intent.putExtra(VoucherDetailActivity.VOUCHER_KEY, order.getVoucher());
        intent.putExtra(VoucherDetailActivity.MERCHANT_KEY, order.getVoucher().getMerchant());
        context.startActivity(intent);
    }

    private void remark(Order order) {
        Intent intent = new Intent(context, AddUpdRemarkActivity.class);
        intent.putExtra(AddUpdRemarkActivity.ORDER_KEY, order);
        intent.putExtra(AddUpdRemarkActivity.VOUCHER_KEY, order.getVoucher());
        intent.putExtra(AddUpdRemarkActivity.MERCHANT_KEY, order.getVoucher().getMerchant());
        intent.putExtra(AddUpdRemarkActivity.IS_UPD_KEY, false);
        context.startActivity(intent);
    }

    private void setSpareTime(LinearLayout spareTimeLayout, TextView countTimeTv, Order order) {
        if (order.getStatus() != OrderStateEnum.WAIT_PAY.getId() || order.getLimitMinute() <= 0) {//非待支付状态或没有限制时间不显示
            spareTimeLayout.setVisibility(View.INVISIBLE);
            //移除相应订单的记录
            idToTime.remove(order.getObjectId());
            viewToId.remove(countTimeTv);
        } else {//有限时
            spareTimeLayout.setVisibility(View.VISIBLE);
            long spareSeconds = order.getSpareSeconds();
            if (spareSeconds <= 0) {//已过期
                //移除相应订单的记录
                idToTime.remove(order.getObjectId());
                viewToId.remove(countTimeTv);
                countTimeTv.setText("已过期");
            } else {//未过期
                if (idToTime.containsKey(order.getObjectId())) {//已经有过该记录,直接获取最新的即可
                    viewToId.put(countTimeTv, order.getObjectId());//加入view-id关系
                    long newSpareSeconds = idToTime.get(order.getObjectId());
                    if (newSpareSeconds <= 0) {//过期了
                        //移除相应订单的记录
                        idToTime.remove(order.getObjectId());
                        viewToId.remove(countTimeTv);
                        countTimeTv.setText("已过期");
                    } else {//显示最新剩余时间
                        String spareTime = DateUtils.getTimeStringOfSeconds(newSpareSeconds);
                        countTimeTv.setText(spareTime);
                    }
                } else {//第一次,放入关系map
                    idToTime.put(order.getObjectId(), spareSeconds);
                    viewToId.put(countTimeTv, order.getObjectId());
                    String spareTime = DateUtils.getTimeStringOfSeconds(spareSeconds);
                    countTimeTv.setText(spareTime);
                }
            }
        }
    }

    private void setStatus(TextView statusTv, int status) {
        OrderStateEnum stateEnum = OrderStateEnum.getEnumMap(status);
        if (stateEnum == null) {
            statusTv.setText("");
            return;
        }
        statusTv.setText(stateEnum.getState());
    }

    private void setMerchantIv(RoundImageView merchantIv, Voucher voucher) {
        if (voucher.getImages() != null && voucher.getImages().size() > 0) {
            ImageLoader.displayImage(merchantIv, voucher.getImages().get(0));
        } else if (voucher.getMerchant().getImages() != null && voucher.getMerchant().getImages().size() > 0) {
            ImageLoader.displayImage(merchantIv, voucher.getMerchant().getImages().get(0));
        } else {
            merchantIv.setImageDrawable(null);
        }
    }

    private void setCatIv(ImageView categoryIv, Category category) {
        CategoryEnum categoryEnum = CategoryEnum.getEnumMap(category);
        if (categoryEnum == null) {
            categoryIv.setImageDrawable(null);
            return;
        }
        categoryIv.setImageResource(categoryEnum.getSmallIconId());
    }

    @Override
    public void addData(List<Order> data) {
        //把新加入的数据的剩余时间全部初始化加入到关系map中
        for (Order order : data) {
            if (order.getStatus() == OrderStateEnum.WAIT_PAY.getId() && order.getLimitMinute() > 0 && order.getSpareSeconds() > 0) {//有剩余时间
                idToTime.put(order.getObjectId(), order.getSpareSeconds());//加入
            }
        }
        super.addData(data);
    }

    @Override
    public void clearData() {
        idToTime.clear();
        viewToId.clear();
        super.clearData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(layoutInflater.inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onItemClick(int position) {
        //跳转到订单详情页
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ORDER_ID_KEY, getDataItem(position).getObjectId());
        context.startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        //删除订单(待支付和已过期的订单可以删除)
        final Order order = getDataItem(position);
        if (order.getStatus() == OrderStateEnum.WAIT_PAY.getId() || order.getStatus() == OrderStateEnum.OVERDUE.getId()) {//可删除
            if (messageDialog == null) {
                messageDialog = new MessageDialog(context);
                messageDialog.setTitle("删除订单");
                messageDialog.setMessage("是否删除该订单");
                messageDialog.setNegativeText("取消");
                messageDialog.setPositiveText("删除");
            }
            messageDialog.setPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrder(order);
                }
            });
            messageDialog.show();
        }
    }

    private void deleteOrder(final Order order) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
        }
        //自定义删除订单方法,只删除待支付和已过期的订单
        loadingDialog.show("删除订单中...");
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", order.getObjectId());
        AVCloud.rpcFunctionInBackground(CloudFunction.DELETE_ORDER, params, new SafeFunctionCallback<Object>(context) {
            @Override
            protected void functionBack(Object o, AVException e) {
                if (e != null) {
                    String reason = JsonUtils.getStrValueOfJsonStr(e.getMessage(), "error");
                    Utils.showToast(context, "删除订单失败" + (reason == null ? "" : ":" + reason));
                } else {
                    removeData(order);
                    Utils.showToast(context, "删除订单成功");
                }
                loadingDialog.cancel();
            }
        });
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout spareTimeLayout;
        RoundImageView merchantIv;
        ImageView categoryIv;
        TextView merchantNameTv;
        TextView statusTv;
        TextView voucherNameTv;
        TextView numTv;
        TextView priceTv;
        TextView countTimeTv;
        TextView functionTv;

        public OrderViewHolder(View itemView) {
            super(itemView);
            spareTimeLayout = (LinearLayout) itemView.findViewById(R.id.order_item_spare_time_layout);
            merchantIv = (RoundImageView) itemView.findViewById(R.id.order_item_merchant_iv);
            categoryIv = (ImageView) itemView.findViewById(R.id.order_item_category_iv);
            merchantNameTv = (TextView) itemView.findViewById(R.id.order_item_merchant_name_tv);
            statusTv = (TextView) itemView.findViewById(R.id.order_item_status_tv);
            voucherNameTv = (TextView) itemView.findViewById(R.id.order_item_voucher_name_tv);
            numTv = (TextView) itemView.findViewById(R.id.order_item_num_tv);
            priceTv = (TextView) itemView.findViewById(R.id.order_item_price_tv);
            countTimeTv = (TextView) itemView.findViewById(R.id.order_item_count_time_tv);
            functionTv = (TextView) itemView.findViewById(R.id.order_item_fun_tv);
            setFuncTvStyle();
        }

        private void setFuncTvStyle() {
            int orange = context.getResources().getColor(R.color.orange);
            int strokeWidth = UIUtils.dp2px(context, 1);
            int corner = UIUtils.dp2px(context, 3);
            functionTv.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(new int[]{DrawableUtils.STATE_PRESSED}, corner, orange)
                    , new DrawableUtils.LayerStateDrawable(new int[]{}, corner, Color.WHITE, strokeWidth, strokeWidth, strokeWidth, strokeWidth, orange)));
            functionTv.setTextColor(DrawableUtils.getStateColor(new DrawableUtils.StateColor(new int[]{DrawableUtils.STATE_PRESSED}, Color.WHITE)
                    , new DrawableUtils.StateColor(new int[]{}, orange)));
        }
    }
}
