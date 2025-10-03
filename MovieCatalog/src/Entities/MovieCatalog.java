package Entities;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieCatalog {

    private HashTable movieTable;

    public MovieCatalog() {
        movieTable = new HashTable();
    }

    public void allocate(int size) {
        movieTable = new HashTable(size);
    }

    public void add(Movie movie) {
        movieTable.insert(movie);
    }

    public Movie get(String title) {
        return movieTable.get(title);
    }

    public void erase(String title) {
        movieTable.delete(title);
    }

    public boolean contains(String title) {
        return movieTable.search(title);
    }

    public void deallocate() {
        movieTable.deallocate();
    }

    public int getSize() {
        return movieTable.getSize();
    }

    public int getCapacity() {
        return movieTable.getCapacity();
    }

    public AVLTree<Movie> getTree(int index) {
        return movieTable.getTree(index);
    }

    public void saveMoviesToFile(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < movieTable.getCapacity(); i++) {
                AVLTree<Movie> tree = movieTable.getTree(i);
                ArrayList<Movie> movies = tree.toList();

                for (Movie movie : movies) {
                    writer.println("Title: " + movie.getTitle());
                    writer.println("Description: " + movie.getDescription());
                    writer.println("Release Year: " + movie.getReleaseYear());
                    writer.println("Rating: " + movie.getRating());
                    writer.println();
                }
            }
        }
    }

    public void loadMoviesFromFile(File file) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;

                String title = line.substring(7);
                String description = scanner.nextLine().substring(13);
                int releaseYear = Integer.parseInt(scanner.nextLine().substring(14));
                double rating = Double.parseDouble(scanner.nextLine().substring(8));
                if(releaseYear > LocalDate.now().getYear()|| releaseYear < 0 || rating < 0 || rating > 10)
                    continue;

                Movie movie = new Movie(title, description, releaseYear, rating);
                add(movie);

                if (scanner.hasNextLine()) scanner.nextLine();
            }
        }
    }

    public int nextTree(int currentIndex) {
        return movieTable.nextTree(currentIndex);
    }

    public int previousTree(int currentIndex) {
        return movieTable.previousTree(currentIndex);
    }
}
