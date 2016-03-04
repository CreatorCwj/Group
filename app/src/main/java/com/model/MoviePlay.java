package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.model.base.BaseModel;

import java.util.Date;
import java.util.List;

/**
 * Created by cwj on 16/3/4.
 */
@SuppressWarnings("unchecked")
@AVClassName("MoviePlay")
public class MoviePlay extends BaseModel {

    public static final String CLASS_NAME = "MoviePlay";

    public static final String MERCHANT = "merchant";
    public static final String HALL = "hall";
    public static final String MOVIE = "movie";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    public static final String SELECTED_SEAT = "selectedSeat";

    public void setType(String type) {
        put(TYPE, type);
    }

    public String getType() {
        return getString(TYPE);
    }

    public void setSelectedSeat(List<String> selectedSeat) {
        put(SELECTED_SEAT, selectedSeat);
    }

    public List<String> getSelectedSeat() {
        return getList(SELECTED_SEAT);
    }

    public void setDate(Date date) {
        put(DATE, date);
    }

    public Date getDate() {
        return getDate(DATE);
    }

    public void setMerchant(String merchantId) {
        try {
            put(MERCHANT, AVObject.createWithoutData(Merchant.class, merchantId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setMerchant(Merchant merchant) {
        put(MERCHANT, merchant);
    }

    public Merchant getMerchant() {
        try {
            return getAVObject(MERCHANT, Merchant.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setHall(String hallId) {
        try {
            put(HALL, AVObject.createWithoutData(CinemaHall.class, hallId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setHall(CinemaHall hall) {
        put(HALL, hall);
    }

    public CinemaHall getHall() {
        try {
            return getAVObject(HALL, CinemaHall.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMovie(String movieId) {
        try {
            put(MOVIE, AVObject.createWithoutData(Movie.class, movieId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setMovie(Movie movie) {
        put(MOVIE, movie);
    }

    public Movie getMovie() {
        try {
            return getAVObject(MOVIE, Movie.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
