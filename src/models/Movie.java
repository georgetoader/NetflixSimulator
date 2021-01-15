package models;

import java.util.ArrayList;
import java.util.List;

public final class Movie extends Show {
    private final int duration;
    private final List<Double> ratings;

    public Movie(final String title, final int year, final List<String> cast,
                 final List<String> genres, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        ratings = new ArrayList<>();
    }

    @Override
    public void addRating(final double grade, final int season) {
        ratings.add(grade);
    }

    @Override
    public double getShowRating() {
        if (ratings.size() == 0) {
            return 0;
        }
        return ratings.stream().mapToDouble(Double::doubleValue).sum() / ratings.size();
    }

    @Override
    public boolean isRated() {
        return ratings.size() != 0;
    }

    @Override
    public int getDuration() {
        return duration;
    }
}
