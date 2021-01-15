package models;

import entertainment.Season;

import java.util.List;

public final class Serial extends Show {
    private final int numberOfSeasons;
    private final List<Season> seasons;

    public Serial(final String title, final int year, final List<String> cast,
                  final List<String> genres, final int numberOfSeasons,
                  final List<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    @Override
    public void addRating(final double grade, final int season) {
        Season currSeason = seasons.stream()
                                   .filter(x -> x.getCurrentSeason() == season)
                                   .findFirst()
                                   .orElse(null);
        assert currSeason != null;
        currSeason.addRating(grade);
    }

    @Override
    public double getShowRating() {
        return seasons.stream().mapToDouble(Season::getSeasonRating).sum() / seasons.size();
    }

    @Override
    public boolean isRated() {
        return seasons.stream().anyMatch(Season::isRated);
    }

    @Override
    public int getDuration() {
        return seasons.stream().mapToInt(Season::getDuration).sum();
    }
}
