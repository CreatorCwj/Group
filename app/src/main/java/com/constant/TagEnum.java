package com.constant;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.util.DrawableUtils;
import com.util.UIUtils;

/**
 * Created by cwj on 16/3/11.
 */
public enum TagEnum {

    GROUP(0, Color.parseColor("#3FBAB0"), "团"),//团购券
    LOTTERY(1, Color.parseColor("#F3746C"), "券"),//满减券
    NO_WAIT(2, Color.parseColor("#24C0AD"), "免"),//免预约
    SEAT(3, Color.parseColor("#E75700"), "座");//选座

    private int id;
    private int color;
    private String name;

    TagEnum(int id, int color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
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
}
