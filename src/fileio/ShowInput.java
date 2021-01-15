package fileio;

import java.util.List;

/**
 * General information about show (video), retrieved from parsing the input test files
 */
public abstract class ShowInput {
    /**
     * Show's title
     */
    private final String title;
    /**
     * The year the show was released
     */
    private final int year;
    /**
     * Show casting
     */
    private final List<String> cast;
    /**
     * Show genres
     */
    private final List<String> genres;

    public ShowInput(final String title, final int year,
                     final List<String> cast, final List<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final List<String> getCast() {
        return cast;
    }

    public final List<String> getGenres() {
        return genres;
    }
}
