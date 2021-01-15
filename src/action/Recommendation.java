package action;

import jdk.jshell.spi.ExecutionControl;
import models.Action;
import models.Show;
import models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Recommendation extends ActionCommon {
    public Recommendation(final Action action) {
        super(action);
    }

    @Override
    public String execute() throws ExecutionControl.NotImplementedException {
        return switch (action.getType()) {
            case "standard" -> standard();
            case "best_unseen" -> bestUnseen();
            case "favorite" -> favorite();
            case "popular" -> popular();
            case "search" -> search();
            default -> throw new ExecutionControl.NotImplementedException("action not"
                    + "implemented");
        };
    }

    private static final class Helper {

        private static Show returnFistUnseen(final User user, final List<Show> shows) {
            Map<String, Integer> userHistory = user.getHistory();
            return shows.stream().filter(show -> !userHistory.containsKey(show.getTitle())).findFirst().orElse(null);
        }

        private static String resultSingle(final Show show) {
            if (show == null)
                return "";
            return show.getTitle();
        }

        private static ArrayList<Show> sortShows(final List<Show> shows,
                                                final Comparator<Show> comparator, final String order) {
            Comparator<Show> actualComparator = comparator;
            if (order.equals("desc")) {
                actualComparator = comparator.reversed();
            }
            // Apply sort
            return shows.stream()
                        .sorted(actualComparator)
                        .collect(Collectors.toCollection(ArrayList::new));
        }

        private static ArrayList<Show> customFilterShows(final List<Show> shows,
                                                        final Predicate<Show> customFilter) {
            Stream<Show> stream = shows.stream();
            stream = stream.filter(customFilter);
            return stream.collect(Collectors.toCollection(ArrayList::new));
        }

        private static String generateResult(final List<Show> shows) {
            // Select only titles
            List<String> titles = shows.stream()
                                            .map(Show::getTitle)
                                            .collect(Collectors
                                                    .toCollection(ArrayList::new));
            return String.join(", ", titles);
        }
    }

    private String standard() {
        User user = database.getUserByUsername(action.getUsername());

        Show firstUnseen = Helper.returnFistUnseen(user, database.getShows());

        if (firstUnseen == null) {
            return "StandardRecommendation cannot be applied!";
        }

        return "StandardRecommendation result: " + Helper.resultSingle(firstUnseen);
    }

    private String bestUnseen() {
        User user = database.getUserByUsername(action.getUsername());
        List<Show> shows = database.getShows();

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getShowRating);
        shows = Helper.sortShows(shows, comparator, "desc");

        Show firstUnseen = Helper.returnFistUnseen(user, shows);

        if (firstUnseen == null) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }

        return "BestRatedUnseenRecommendation result: " + Helper.resultSingle(firstUnseen);
    }

    private String popular() {
        User user = database.getUserByUsername(action.getUsername());
        List<String> genresByPopularity = database.getGenresByPopularity();

        // Assert premium user
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "PopularRecommendation cannot be applied!";
        }

        for (String genre : genresByPopularity) {
            List<Show> showsInGenre = database.getShowsByGenre(genre);
            Show firstUnseen = Helper.returnFistUnseen(user, showsInGenre);
            if (firstUnseen != null) {
                return "PopularRecommendation result: " + Helper.resultSingle(firstUnseen);
            }
        }
        return "PopularRecommendation cannot be applied!";
    }

    private String favorite() {
        User user = database.getUserByUsername(action.getUsername());
        List<Show> shows = database.getShows();

        // Assert premium user
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "FavoriteRecommendation cannot be applied!";
        }

        // Filter for shows that appear on at least one user's favorites list
        shows = Helper.customFilterShows(shows, (show) -> (show.getFavoritesCount() != 0));

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getFavoritesCount);
        shows = Helper.sortShows(shows, comparator, "desc");

        Show firstUnseen = Helper.returnFistUnseen(user, shows);

        if (firstUnseen == null) {
            return "FavoriteRecommendation cannot be applied!";
        }

        return "FavoriteRecommendation result: " + Helper.resultSingle(firstUnseen);
    }

    private String search() {
        User user = database.getUserByUsername(action.getUsername());

        // Assert premium user
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "SearchRecommendation cannot be applied!";
        }

        String genre = action.getGenre();
        List<Show> showsInGenre = database.getShowsByGenre(genre);

        // Custom sort
        Comparator<Show> comparator = Comparator.comparingDouble(Show::getShowRating)
                                                .thenComparing(Show::getTitle);
        showsInGenre = Helper.sortShows(showsInGenre, comparator, "asc");

        // Remove seen videos
        Map<String, Integer> userHistory = user.getHistory();
        showsInGenre.removeIf(show -> userHistory.containsKey(show.getTitle()));

        if (showsInGenre.size() == 0) {
            return "SearchRecommendation cannot be applied!";
        }

        return "SearchRecommendation result: [" + Helper.generateResult(showsInGenre) + "]";
    }
}
