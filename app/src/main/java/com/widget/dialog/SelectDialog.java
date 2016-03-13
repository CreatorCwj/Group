package com.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.group.R;
import com.util.DrawableUtils;
import com.util.UIUtils;
import com.widget.dialog.base.BaseDialog;

import java.util.List;

/**
 * Created by cwj on 16/2/6.
 * 简单的多个文本选项的dialog
 */
public class SelectDialog extends BaseDialog {

    private ListView listView;
    private List<String> items;

    private AdapterView.OnItemClickListener customListener;

    public SelectDialog(Context context, List<String> items) {
        super(context);
        this.items = items;//items可能动态改变,所以最后不要传参时用Arrays.asList方法生成list
    }

    public SelectDialog(Context context, int themeResId, List<String> items) {
        super(context, themeResId);
        this.items = items;
    }

    /**
     * 可以动态的增加item
     *
     * @param items
     */
    public void addItems(List<String> items) {
        this.items.addAll(items);
    }

    public ListView getListView() {
        return listView;
    }

    /**
     * 设置监听器
     *
     * @param customListener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener customListener) {
        this.customListener = customListener;
    }

    @Override
    protected View onCreateView() {
        listView = new ListView(context);
        return listView;
    }

    @Override
    protected void onViewCreated(View view) {
        listView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setDivider(context.getResources().getDrawable(R.color.dividerColor, null));
        listView.setDividerHeight(context.getResources().getDimensionPixelSize(R.dimen.divider_height));
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cancel();
                if (customListener != null)
                    customListener.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void show() {
        if (listView.getAdapter() == null)
            listView.setAdapter(new SelectDialogAdapter(context, items));
        //根据个数决定显示高度
        int height = getListViewHeight();
        if (height > maxHeight)
            height = maxHeight;
        listView.setLayoutParams(new FrameLayout.LayoutParams(maxWidth, height));
        super.show();
    }

    private int getListViewHeight() {
        ListAdapter adapter = listView.getAdapter();
        int height = 0;
        for (int i = 0; i < adapter.getCount(); ++i) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        height += listView.getDividerHeight() * (adapter.getCount() - 1);
        return height;
    }

    private class SelectDialogAdapter extends BaseAdapter {

        private Context context;
        private List<String> items;

        private int[] pressed;
        private int[] unPressed;

        public SelectDialogAdapter(Context context, List<String> items) {
            this.context = context;
            this.items = items;
            pressed = new int[]{DrawableUtils.STATE_PRESSED};
            unPressed = new int[]{DrawableUtils.STATE_UNPRESSED};
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = new TextView(context);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                //设置
                viewHolder.textView = (TextView) convertView;
                viewHolder.textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                viewHolder.textView.setGravity(Gravity.START);
                viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.black));
                int padding = UIUtils.dp2px(context, 13);
                viewHolder.textView.setPadding(padding, padding, padding, padding);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //设置background
            if (position == 0 && getCount() == 1) {//就一个
                viewHolder.textView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(unPressed, radius, radius, radius, radius, Color.WHITE)
                        , new DrawableUtils.CornerStateDrawable(pressed, radius, radius, radius, radius, context.getResources().getColor(R.color.dividerColor))));
            } else if (position == 0) {//第一个
                viewHolder.textView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(unPressed, radius, radius, 0, 0, Color.WHITE)
                        , new DrawableUtils.CornerStateDrawable(pressed, radius, radius, 0, 0, context.getResources().getColor(R.color.dividerColor))));
            } else if (position == getCount() - 1 && getCount() != 1) {//最后一个
                viewHolder.textView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.CornerStateDrawable(unPressed, 0, 0, radius, radius, Color.WHITE)
                        , new DrawableUtils.CornerStateDrawable(pressed, 0, 0, radius, radius, context.getResources().getColor(R.color.dividerColor))));
            } else {//中间的
                viewHolder.textView.setBackground(DrawableUtils.getStateDrawable(new DrawableUtils.RectStateDrawable(unPressed, Color.WHITE)
                        , new DrawableUtils.RectStateDrawable(pressed, context.getResources().getColor(R.color.dividerColor))));
            }
            viewHolder.textView.setText(getItem(position));
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}
