package com.group;

import android.app.Application;
import android.test.ApplicationTestCase;

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
        String[] strings = new String[]{"","",""};
        List<String> list = Arrays.asList(strings);
        list.add("1");
    }
}