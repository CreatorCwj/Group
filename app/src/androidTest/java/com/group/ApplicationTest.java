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
        String str = "a:";
        String s1 = str.substring(str.indexOf(":")+1);
    }
}