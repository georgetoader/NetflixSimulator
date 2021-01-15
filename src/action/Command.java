package action;

import jdk.jshell.spi.ExecutionControl;
import models.Action;
import models.Show;
import models.User;

import java.util.List;
import java.util.Map;

public final class Command extends ActionCommon {

    public Command(final Action action) {
        super(action);
    }

    @Override
    public String execute() throws ExecutionControl.NotImplementedException {
        return switch (action.getType()) {
            case "view" -> view();
            case "favorite" -> favorite();
            case "rating" -> rating();
            default -> throw new ExecutionControl.NotImplementedException("action not"
                    + "implemented");
        };
    }

    private String view() {
        User user = database.getUserByUsername(action.getUsername());
        Show show = database.getShowByTitle(action.getTitle());

        // Get user history
        Map<String, Integer> history = user.getHistory();

        // Try to get no of views for the title or 0 if none
        Integer noViews = history.getOrDefault(show.getTitle(), 0);
        noViews += 1;

        // Update history
        history.put(show.getTitle(), noViews);

        // Update show view count
        show.addViewCount();
        return "success -> " + show.getTitle() + " was viewed with total views of " + noViews;
    }

    private String favorite() {
        User user = database.getUserByUsername(action.getUsername());
        Show show = database.getShowByTitle(action.getTitle());

        List<String> favoriteMovies = user.getFavoriteMovies();

        // Check if show was not viewed
        if (user.isShowNotViewed(show.getTitle())) {
            return "error -> " + show.getTitle() + " is not seen";
        }

        // Duplicate check
        if (favoriteMovies.contains(show.getTitle())) {
            return "error -> " + show.getTitle() + " is already in favourite list";
        }

        // Add show to fav
        show.addFavoriteCount();
        favoriteMovies.add(show.getTitle());
        return "success -> " + show.getTitle() + " was added as favourite";
    }

    private String rating() {
        User user = database.getUserByUsername(action.getUsername());
        Show show = database.getShowByTitle(action.getTitle());

        List<String> userRatingsHistory = user.getRatingsHistory();

        // Check if show was not viewed
        if (user.isShowNotViewed(show.getTitle())) {
            return "error -> " + show.getTitle() + " is not seen";
        }

        // Duplicate check
        // Get season title or movie title (show title + $ + seasonNumber -> if this is a serial)
        String seasonTitle = show.getTitle()
                + ((action.getSeasonNumber() != 0) ? "$" + action.getSeasonNumber() : null);
        if (userRatingsHistory.contains(seasonTitle)) {
            return "error -> " + show.getTitle() + " has been already rated";
        }

        // Add rating to show
        show.addRating(action.getGrade(), action.getSeasonNumber());
        userRatingsHistory.add(seasonTitle);
        return "success -> " + show.getTitle() + " was rated with " + action.getGrade() + " by "
                + user.getUsername();
    }
}
