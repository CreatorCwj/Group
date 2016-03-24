package com.group;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.model.Remark;

import java.util.Arrays;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test() {
        Remark remark = new Remark();
        remark.setPoint(4);
        remark.setVoucher("56f65d416be3ff005ceca37f");
        remark.saveInBackground();
    }
}