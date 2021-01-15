package fileio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Information about an action, retrieved from parsing the input test files
 */
public final class ActionInputData {
    /**
     * Action id
     */
    private final int actionId;
    /**
     * Type of action
     */
    private final String actionType;
    /**
     * Used for commands
     */
    private final String type;
    /**
     * Username of user
     */
    private final String username;
    /**
     * The type of object on which the actions will be performed
     */
    private final String objectType;
    /**
     * Sorting type: ascending or descending
     */
    private final String sortType;
    /**
     * The criterion according to which the sorting will be performed
     */
    private final String criteria;
    /**
     * Video title
     */
    private final String title;
    /**
     * Video genre
     */
    private final String genre;
    /**
     * Query limit
     */
    private final int number;
    /**
     * Grade for rating - aka value of the rating
     */
    private final double grade;
    /**
     * Season number
     */
    private final int seasonNumber;
    /**
     * Filters used for selecting videos
     */
    private final List<List<String>> filters;

    private ActionInputData(Builder builder) {
        this.actionId = builder.actionId;
        this.actionType = builder.actionType;
        this.type = builder.type;
        this.username = builder.username;
        this.objectType = builder.objectType;
        this.sortType = builder.sortType;
        this.criteria = builder.criteria;
        this.title = builder.title;
        this.genre = builder.genre;
        this.number = builder.number;
        this.grade = builder.grade;
        this.seasonNumber = builder.seasonNumber;
        this.filters = builder.filters;
    }

    // BUILDER DESIGN PATTERN
    public static class Builder {
        private final int actionId;
        private String actionType;
        private String type;
        private String username;
        private String objectType;
        private String sortType;
        private String criteria;
        private String title;
        private String genre;
        private int number;
        private double grade;
        private int seasonNumber;
        private final List<List<String>> filters = new ArrayList<>();

        public Builder(final int actionId) {
            this.actionId = actionId;
        }

        public Builder inputActionType(final String actionType) {
            this.actionType = actionType;
            return this;
        }

        public Builder inputType(final String type) {
            this.type = type;
            return this;
        }

        public Builder inputUsername(final String username) {
            this.username = username;
            return this;
        }

        public Builder inputObjectType(final String objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder inputSortType(final String sortType) {
            this.sortType = sortType;
            return this;
        }

        public Builder inputCriteria(final String criteria) {
            this.criteria = criteria;
            return this;
        }

        public Builder inputTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder inputGenre(final String genre) {
            this.genre = genre;
            return this;
        }

        public Builder inputNumber(final int number) {
            this.number = number;
            return this;
        }

        public Builder inputGrade(final double grade) {
            this.grade = grade;
            return this;
        }

        public Builder inputSeasonNumber(final int seasonNumber) {
            this.seasonNumber = seasonNumber;
            return this;
        }

        public Builder addFilter(final List<String> list) {
            this.filters.add(list);
            return this;
        }

        public Builder addFilter(final String word) {
            this.filters.add(new ArrayList<>(Collections.singleton(word)));
            return this;
        }

        public ActionInputData build() {
            return new ActionInputData(this);
        }

    }

    public int getActionId() {
        return actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getSortType() {
        return sortType;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumber() {
        return number;
    }

    public double getGrade() {
        return grade;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<List<String>> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "ActionInputData{"
                + "actionId=" + actionId
                + ", actionType='" + actionType + '\''
                + ", type='" + type + '\''
                + ", username='" + username + '\''
                + ", objectType='" + objectType + '\''
                + ", sortType='" + sortType + '\''
                + ", criteria='" + criteria + '\''
                + ", title='" + title + '\''
                + ", genre='" + genre + '\''
                + ", number=" + number
                + ", grade=" + grade
                + ", seasonNumber=" + seasonNumber
                + ", filters=" + filters
                + '}' + "\n";
    }
}
