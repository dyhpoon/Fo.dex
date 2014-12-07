package com.dyhpoon.fodex.model;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class PageItem {

    private String mTitle;
    private Class mFragmentClass;

    public PageItem(String title, Class fragmentClass) {
        this.mTitle = title;
        this.mFragmentClass = fragmentClass;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Class getFragmentClass() {
        return this.mFragmentClass;
    }

}
