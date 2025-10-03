package Entities;

public class Movie implements Comparable<Movie> {

    String title;
    String description;
    int releaseYear;
    double rating;

    public Movie() {
        this.releaseYear = -1;
        this.title = null;
        this.description = null;
        this.rating = -1;
    }

    public Movie(String title, String description, int releaseYear, double rating) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int compareTo(Movie other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Movie movie = (Movie) o;
        return title.equals(movie.title);
    }

}