package action;

import actor.ActorsAwards;
import jdk.jshell.spi.ExecutionControl;
import models.Action;
import models.Actor;
import models.Movie;
import models.Serial;
import models.Show;
import models.User;
import utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.Constants.FIRST_ELEMENT_INDEX;
import static common.Constants.ORDER_FILTER_AWARDS;
import static common.Constants.ORDER_FILTER_GENRE;
import static common.Constants.ORDER_FILTER_WORDS;
import static common.Constants.ORDER_FILTER_YEAR;

public final class Query extends ActionCommon {

    public Query(final Action action) {
        super(action);
    }

    @Override
    public String execute() throws ExecutionControl.NotImplementedException {
        String result;
        result = switch (action.getCriteria()) {
            case "average" -> average();
            case "awards" -> awards();
            case "filter_description" -> filterDescription();
            case "ratings" -> ratings();
            case "favorite" -> favorite();
            case "longest" -> longest();
            case "most_viewed" -> mostViewed();
            case "num_ratings" -> numRatings();
            default -> throw new ExecutionControl.NotImplementedException("query criteria not"
                    + "implemented");
        };
        return "Query result: [" + result + "]";
    }

    private class QueryActor {
        private List<Actor> actors;

        QueryActor() {
            this.actors = database.getActors();
        }

        QueryActor(final List<Actor> actors) {
            this.actors = actors;
        }

        public void customFilter(final Predicate<Actor> customFilter) {
            Stream<Actor> stream = actors.stream();
            stream = stream.filter(customFilter);
            actors = stream.collect(Collectors.toCollection(ArrayList::new));
        }

        public String generateResult() {
            // Truncate shows at number elements
            int upperBound = Math.min(actors.size(), action.getNumber());
            List<Actor> truncatedUsers = new ArrayList<>(actors.subList(0, upperBound));

            // Select only usernames
            List<String> names = truncatedUsers.stream()
                                                .map(Actor::getName)
                                                .collect(Collectors.toCollection(ArrayList::new));
            return String.join(", ", names);
        }

        public void sort(final Comparator<Actor> comparator) {
            Comparator<Actor> actualComparator = comparator;
            if (action.getSortType().equals("desc")) {
                actualComparator = comparator.reversed();
            }
            // Apply sort
            this.actors = this.actors.stream()
                                     .sorted(actualComparator)
                                     .collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Actor> getActors() {
            return actors;
        }

        public void setActors(final ArrayList<Actor> actors) {
            this.actors = actors;
        }
    }

    private class QueryVideo {
        private List<Show> shows;

        QueryVideo() {
            this.shows = database.getShows();
        }

        public void applyFilter() {
            Stream<Show> stream = shows.stream();

            // Object type
            stream = switch (action.getObjectType()) {
                case "movies" -> stream.filter(x -> x.getClass() == Movie.class);
                case "shows" -> stream.filter(x -> x.getClass() == Serial.class);
                default -> stream;
            };

            // Year
            if (action.getFilters().get(ORDER_FILTER_YEAR).get(FIRST_ELEMENT_INDEX) != null) {
                // Get year
                int yearFilter = Integer.parseInt(action.getFilters()
                                                        .get(ORDER_FILTER_YEAR)
                                                        .get(FIRST_ELEMENT_INDEX));
                // Apply filter
                stream = stream.filter(x -> x.getYear() == yearFilter);
            }

            // Genre
            if (action.getFilters().get(ORDER_FILTER_GENRE).get(FIRST_ELEMENT_INDEX) != null) {
                // Get genre
                String genreFilter = action.getFilters()
                                           .get(ORDER_FILTER_GENRE)
                                           .get(FIRST_ELEMENT_INDEX);
                // Apply filter
                stream = stream.filter(x -> x.getGenres().contains(genreFilter));
            }

            // Update shows
            shows = stream.collect(Collectors.toCollection(ArrayList::new));
        }

        public void customFilter(final Predicate<Show> customFilter) {
            Stream<Show> stream = shows.stream();
            stream = stream.filter(customFilter);
            shows = stream.collect(Collectors.toCollection(ArrayList::new));
        }

        public String generateResult() {
            // Truncate shows at number elements
            int upperBound = Math.min(shows.size(), action.getNumber());
            List<Show> truncatedShows = new ArrayList<>(shows.subList(0, upperBound));

            // Select only titles
            List<String> titles = truncatedShows.stream()
                                                .map(Show::getTitle)
                                                .collect(Collectors.toCollection(ArrayList::new));
            return String.join(", ", titles);
        }

        public void sort(final Comparator<Show> comparator) {
            Comparator<Show> actualComparator = comparator;
            if (action.getSortType().equals("desc")) {
                actualComparator = comparator.reversed();
            }
            // Apply sort
            this.shows = this.shows.stream()
                                   .sorted(actualComparator)
                                   .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private class QueryUser {
        private List<User> users;

        QueryUser() {
            this.users = database.getUsers();
        }

        public void applyFilter() {
            Stream<User> stream = users.stream();

            // Select only users that have rated
            stream = stream.filter(x -> x.getRatingsCount() != 0);
            users = stream.collect(Collectors.toCollection(ArrayList::new));
        }

        public String generateResult() {
            // Truncate shows at number elements
            int upperBound = Math.min(users.size(), action.getNumber());
            List<User> truncatedUsers = new ArrayList<>(users.subList(0, upperBound));

            // Select only usernames
            List<String> usernames = truncatedUsers.stream()
                                                    .map(User::getUsername)
                                                    .collect(Collectors.toCollection(ArrayList::new));
            return String.join(", ", usernames);
        }

        public void sort(final Comparator<User> comparator) {
            Comparator<User> actualComparator = comparator;
            if (action.getSortType().equals("desc")) {
                actualComparator = comparator.reversed();
            }
            // Apply sort
            this.users = this.users.stream()
                                   .sorted(actualComparator)
                                   .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    // Actual action execution
    private String average() {
        List<Actor> actors = database.getActors();
        List<Actor> chosenActors = new ArrayList<>();

        for (Actor actor : actors) {
            List<Show> shows = database.getShowsByActorName(actor.getName());
            // Remove shows with rating 0
            shows = shows.stream()
                         .filter(x -> x.getShowRating() != 0)
                         .collect(Collectors.toCollection(ArrayList::new));
            // Calculate rating
            double ratingSum = shows.stream().mapToDouble(Show::getShowRating).sum();
            if (ratingSum != 0) {
                double rating = ratingSum / shows.size();
                actor.setAverageShowRating(rating);
                chosenActors.add(actor);
            }
        }

        // Custom sort
        Comparator<Actor> comparator = Comparator.comparingDouble(Actor::getAverageShowRating)
                                                 .thenComparing(Actor::getName);
        if (action.getSortType().equals("desc")) {
            comparator = comparator.reversed();
        }
        // Apply sort
        chosenActors = chosenActors.stream()
                                   .sorted(comparator)
                                   .collect(Collectors.toCollection(ArrayList::new));

        QueryActor queryActor = new QueryActor(chosenActors);
        return queryActor.generateResult();
    }

    private String awards() {
        QueryActor queryActor = new QueryActor();

        // Apply custom filter
        List<ActorsAwards> awardFilter = action.getFilters()
                                                    .get(ORDER_FILTER_AWARDS)
                                                    .stream()
                                                    .map(Utils::stringToAwards)
                                                    .collect(Collectors
                                                            .toCollection(ArrayList::new));
        queryActor.customFilter(actor -> awardFilter.stream()
                                                    .allMatch(award -> actor.getAwards().containsKey(award)));

        // Custom sort
        Comparator<Actor> comparator = Comparator.comparingInt(Actor::getAwardsCount)
                                                 .thenComparing(Actor::getName);
        queryActor.sort(comparator);

        return queryActor.generateResult();
    }

    private String filterDescription() {
        QueryActor queryActor = new QueryActor();

        // Apply custom filter
        List<String> wordsFilter = new ArrayList<>(action.getFilters()
                                                            .get(ORDER_FILTER_WORDS));
        queryActor.customFilter(actor -> {
            List<String> descWords = Utils.getWordsFromText(actor.getCareerDescription());
            return wordsFilter.stream().allMatch(keyword -> Utils.isWordInText(descWords, keyword));
        });

        // Custom sort
        Comparator<Actor> comparator = Comparator.comparing(Actor::getName);
        queryActor.sort(comparator);

        return queryActor.generateResult();
    }

    private String ratings() {
        QueryVideo queryVideo = new QueryVideo();

        // Apply common & custom filters
        queryVideo.applyFilter();
        queryVideo.customFilter(Show::isRated);

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getShowRating)
                                                .thenComparing(Show::getTitle);
        queryVideo.sort(comparator);

        return queryVideo.generateResult();
    }

    private String favorite() {
        QueryVideo queryVideo = new QueryVideo();

        // Apply common filters
        queryVideo.applyFilter();
        queryVideo.customFilter((show) -> (show.getFavoritesCount() != 0));

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getFavoritesCount)
                                                .thenComparing(Show::getTitle);
        queryVideo.sort(comparator);

        return queryVideo.generateResult();
    }

    private String longest() {
        QueryVideo queryVideo = new QueryVideo();

        // Apply common filters
        queryVideo.applyFilter();

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getDuration)
                                                .thenComparing(Show::getTitle);
        queryVideo.sort(comparator);

        return queryVideo.generateResult();
    }

    private String mostViewed() {
        QueryVideo queryVideo = new QueryVideo();

        // Apply common filters
        queryVideo.applyFilter();
        queryVideo.customFilter((show) -> (show.getViewsCount() != 0));

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getViewsCount)
                                                .thenComparing(Show::getTitle);
        queryVideo.sort(comparator);

        return queryVideo.generateResult();
    }

    private String numRatings() {
        QueryUser queryUser = new QueryUser();

        // Apply common filters
        queryUser.applyFilter();

        // Custom sort
        Comparator<User> comparator = Comparator.comparingInt(User::getRatingsCount)
                                                .thenComparing(User::getUsername);
        queryUser.sort(comparator);

        return queryUser.generateResult();
    }
}
