package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final List<String> favoriteMovies;
    private final List<String> ratingsHistory;

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final List<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
        this.ratingsHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public List<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public List<String> getRatingsHistory() {
        return ratingsHistory;
    }

    /**
     * Check if show was viewed
     *
     * @param title title of the show
     * @return true if show was viewed at least once else false
     */
    public boolean isShowNotViewed(final String title) {
        return !history.containsKey(title);
    }

    public int getRatingsCount() {
        return ratingsHistory.size();
    }
}
