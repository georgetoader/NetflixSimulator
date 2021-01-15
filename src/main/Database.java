package main;

import entertainment.Genre;
import fileio.Input;
import models.Action;
import models.Actor;
import models.Show;
import models.User;
import models.Movie;
import models.Serial;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Database {
    private final List<Actor> actors = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Show> shows = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();

    // Singleton
    private static Database INSTANCE = null;

    private Database() {
    }

    /**
     * @return new clean database
     */
    public static Database getNewInstance() {
        INSTANCE = new Database();
        return INSTANCE;
    }

    /**
     * @return current database
     */
    public static Database getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    /**
     * Parses <code>input</code> to populate the {@link Database}
     * class.
     *
     * @param input from json
     */
    public void populate(final Input input) {
        // Actors
        input.getActors()
             .forEach(actor -> actors.add(new Actor(
                            actor.getName(),
                            actor.getCareerDescription(),
                            actor.getFilmography(),
                            actor.getAwards()
                     )
             ));

        // Movies
        input.getMovies()
             .forEach(movie -> shows.add(new Movie(
                            movie.getTitle(),
                            movie.getYear(),
                            movie.getCast(),
                            movie.getGenres(),
                            movie.getDuration()
                     )
             ));

        // Serials
        input.getSerials()
             .forEach(serial -> shows.add(new Serial(
                            serial.getTitle(),
                            serial.getYear(),
                            serial.getCast(),
                            serial.getGenres(),
                            serial.getNumberSeason(),
                            serial.getSeasons()
                     )
             ));

        // Users
        // Placed under shows to parse favorites
        input.getUsers()
             .forEach(user -> {
                 users.add(new User(
                            user.getUsername(),
                            user.getSubscriptionType(),
                            user.getHistory(),
                            user.getFavoriteMovies()
                         )
                 );
                 // Parse favorites to update each show
                 parseUserFavorites(user.getFavoriteMovies());
                 parseUserViews(user.getHistory());
             });

        // Actions
        input.getCommands()
             .forEach(cmd -> actions.add(new Action(
                            cmd.getActionId(),
                            cmd.getActionType(),
                            cmd.getType(),
                            cmd.getUsername(),
                            cmd.getObjectType(),
                            cmd.getSortType(),
                            cmd.getCriteria(),
                            cmd.getTitle(),
                            cmd.getGenre(),
                            cmd.getNumber(),
                            cmd.getGrade(),
                            cmd.getSeasonNumber(),
                            cmd.getFilters()
                     )
             ));
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Show> getShows() {
        return shows;
    }

    public List<Action> getActions() {
        return actions;
    }

    /**
     * Search username in database and return it.
     *
     * @param username to be searched
     * @return found user or null
     */
    public User getUserByUsername(final String username) {
        return users.stream()
                    .filter(x -> username.equals(x.getUsername()))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Search show in database and return it.
     *
     * @param title to be searched
     * @return found show or null
     */
    public Show getShowByTitle(final String title) {
        return shows.stream()
                    .filter(x -> title.equals(x.getTitle()))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Search the database for shows that have the actor
     *
     * @param actorName to be searched
     * @return found shows
     */
    public List<Show> getShowsByActorName(final String actorName) {
        return shows.stream()
                    .filter(x -> x.getCast().contains(actorName))
                    .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * This method returns a list of all Genres names
     * ordered by their popularity (number of views of all the shows
     * in that genre)
     *
     * @return list of sorted genres
     */
    public List<String> getGenresByPopularity() {
        return Stream.of(Genre.values())
                     .map(Genre::toString)
                     .sorted(Comparator.comparing(this::getGenreRating)
                                                           .reversed())
                     .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * This method returns a list with all the shows that are in a given genre
     *
     * @param genre to be present in shows
     * @return show list
     */
    public List<Show> getShowsByGenre(final String genre) {
        return shows.stream()
                    .filter(x -> x.getGenres().contains(genre))
                    .collect(Collectors.toCollection(ArrayList::new));
    }

    private void parseUserFavorites(final List<String> favoriteShows) {
        for (String showTitle : favoriteShows) {
            Show show = getShowByTitle(showTitle);
            show.addFavoriteCount();
        }
    }

    private void parseUserViews(final Map<String, Integer> history) {
        for (Map.Entry<String, Integer> historyEntry : history.entrySet()) {
            Show show = getShowByTitle(historyEntry.getKey());
            show.addViewCount(historyEntry.getValue());
        }
    }

    private Double getGenreRating(final String genre) {
        List<Show> showsInGenre = getShowsByGenre(genre);
        return showsInGenre.stream().mapToDouble(Show::getViewsCount).sum();
    }
}
