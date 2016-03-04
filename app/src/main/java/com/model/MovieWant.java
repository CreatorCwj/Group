package com.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.model.base.BaseModel;

/**
 * Created by cwj on 16/3/4.
 */
@AVClassName("MovieWant")
public class MovieWant extends BaseModel {

    public static final String CLASS_NAME = "MovieWant";

    public static final String MOVIE = "movie";
    public static final String USER = "user";

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

    public void setUser(String userId) {
        try {
            put(USER, AVUser.createWithoutData(User.class, userId));
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        put(USER, user);
    }

    public User getUser() {
        return getAVUser(USER, User.class);
    }
}
