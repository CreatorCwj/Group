package com.constant;

/**
 * Created by cwj on 16/3/22.
 * 等级成长值对照
 */
public enum GradeEnum {

    LV0(0, 0),
    LV1(1, 100),
    LV2(2, 300),
    LV3(3, 500),
    LV4(4, 800),
    LV5(5, 1100),
    LV6(6, 1500),
    LV7(7, 2000),
    LV8(8, 3000);

    private int grade;
    private int value;

    GradeEnum(int grade, int value) {
        this.grade = grade;
        this.value = value;
    }

    public int getGrade() {
        return grade;
    }

    public int getValue() {
        return value;
    }

    public static GradeEnum getGradeByValue(int value) {
        GradeEnum grade = null;
        for (GradeEnum gradeEnum : GradeEnum.values()) {
            if (gradeEnum.getValue() > value) {//找到第一个大的
                grade = GradeEnum.values()[gradeEnum.ordinal() - 1];//前一个既是
                break;
            }
        }
        if (grade == null) {//比最大的还大就是最大等级
            grade = GradeEnum.values()[GradeEnum.values().length - 1];
        }
        return grade;
    }
}
