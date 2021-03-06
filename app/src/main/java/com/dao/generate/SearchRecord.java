package com.dao.generate;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "SEARCH_RECORD".
 */
public class SearchRecord {

    private Long id;
    /**
     * Not-null value.
     */
    private String name;
    private long lastUseTime;

    public SearchRecord() {
    }

    public SearchRecord(Long id) {
        this.id = id;
    }

    public SearchRecord(String name) {
        this.name = name;
    }

    public SearchRecord(String name, long lastUseTime) {
        this.name = name;
        this.lastUseTime = lastUseTime;
    }

    public SearchRecord(Long id, String name, long lastUseTime) {
        this.id = id;
        this.name = name;
        this.lastUseTime = lastUseTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Not-null value.
     */
    public String getName() {
        return name;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setName(String name) {
        this.name = name;
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

}
