package com.constant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

import com.dao.base.BaseSortFilterModelInter;
import com.util.DrawableUtils;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/11.
 * 标签,筛选器类型
 */
public enum TagEnum implements BaseSortFilterModelInter<TagEnum> {

    GROUP(0, Color.parseColor("#3FBAB0"), "团", "只看团购券"),//团购券
    LOTTERY(1, Color.parseColor("#F3746C"), "券", "只看代金券"),//代金券
    NO_WAIT(2, Color.parseColor("#24C0AD"), "免", "免预订"),//免预订(美食,酒店)
    SEAT(3, Color.parseColor("#E75700"), "座", "可选座"),//选座(电影)
    SEND_POINT(4, Color.parseColor("#FD6A64"), "分", "送积分"),//送积分
    SEND_LOTTERY(5, Color.parseColor("#FEA100"), "满", "送满减券");//送满减券


    private int id;
    private int color;
    private String name;
    private String filterName;

    TagEnum(int id, int color, String name, String filterName) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.filterName = filterName;
    }

    @Override
    public String getFilterName() {
        return filterName;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public static TagEnum getTagById(int tagId) {
        for (TagEnum tag : TagEnum.values()) {
            if (tag.getId() == tagId)
                return tag;
        }
        return null;
    }

    public static TextView getTagView(Context context, int tagId) {
        TagEnum tag = TagEnum.getTagById(tagId);
        if (tag == null)
            return null;
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setText(tag.getName());
        textView.setBackground(DrawableUtils.getDrawable(UIUtils.dp2px(context, 3), tag.getColor()));
        return textView;
    }

    public static Drawable getTagBack(Context context, int tagId) {
        TagEnum tag = TagEnum.getTagById(tagId);
        if (tag == null)
            return null;
        return DrawableUtils.getDrawable(UIUtils.dp2px(context, 3), tag.getColor());
    }

    public static String getTagText(int tagId) {
        TagEnum tag = TagEnum.getTagById(tagId);
        if (tag == null)
            return null;
        return tag.getName();
    }

    @Override
    public int getFilterId() {
        return id;
    }

    @Override
    public int getFilterParentId() {
        return ALL_FILTER_ID;
    }

    @Override
    public TagEnum getAllFirstFilter() {
        return null;
    }

    @Override
    public TagEnum getAllSubFilter(int parentFilterId) {
        return null;
    }

    @Override
    public boolean equalFilter(TagEnum target) {
        return target != null && (target == this || (getFilterId() == target.getFilterId() && getFilterName().equals(target.getFilterName()) && getFilterParentId() == target.getFilterParentId()));
    }

    @Override
    public boolean isAllFirstFilter() {
        return false;
    }

    @Override
    public boolean isAllSubFilter() {
        return false;
    }

    @Override
    public boolean isFirstFilter() {
        return true;
    }

    @Override
    public boolean isSubFilter() {
        return false;
    }
}
