import Entities.Movie;
import Entities.MovieCatalog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class App extends Application {

    private MovieCatalog catalog = new MovieCatalog();
    private int[] currentTreeIndex = { 0 }; // using reference array to change index within lambda expressions
    private TableView<Movie> tableView;
    private Label currentTreeLabel;
    private ComboBox<String> sortOrderComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        // MENU BAR
        MenuBar menuBar = buildMenuBar(primaryStage);

        // TABLE VIEW
        tableView = buildTableView();

        // NAVIGATION + SORT CONTROLS
        HBox navigationBox = buildNavigationBox();

        // ACTION BUTTONS
        HBox actionButtonsBox = buildActionButtons();

        // CENTER LAYOUT
        VBox centerBox = new VBox(15, tableView, navigationBox, actionButtonsBox);
        centerBox.setPadding(new Insets(15));
        centerBox.setAlignment(Pos.CENTER);

        root.setTop(menuBar);
        root.setCenter(centerBox);

        primaryStage.setTitle("Movie Catalog Management System");
        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.show();
    }

    private MenuBar buildMenuBar(Stage primaryStage) {

        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        fileMenu.getItems().addAll(openItem, saveItem);

        // Movie Menu
        Menu movieMenu = new Menu("Movie");
        MenuItem addMovieItem = new MenuItem("Add Movie");
        MenuItem updateMovieItem = new MenuItem("Update Movie");
        MenuItem deleteMovieItem = new MenuItem("Delete Movie");
        MenuItem searchMovieItem = new MenuItem("Search Movie");
        MenuItem printTopLeastItem = new MenuItem("Print Top and Least Ranked Movies");

        movieMenu.getItems().addAll(addMovieItem, updateMovieItem, deleteMovieItem, searchMovieItem, printTopLeastItem);

        // Exit Menu
        Menu exitMenu = new Menu("Exit");
        MenuItem exitItem = new MenuItem("Exit");
        exitMenu.getItems().add(exitItem);

        menuBar.getMenus().addAll(fileMenu, movieMenu, exitMenu);

        // FILE ACTIONS
        openItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Movie File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    catalog.loadMoviesFromFile(selectedFile);
                    currentTreeIndex[0] = 0;
                    showMoviesInCurrentTree();
                } catch (IOException ex) {
                    showErrorAlert("Load Error", ex.getMessage());
                }
            }
        });

        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Movie File");
            File selectedFile = fileChooser.showSaveDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    catalog.saveMoviesToFile(selectedFile);
                } catch (IOException ex) {
                    showErrorAlert("Save Error", ex.getMessage());
                }
            }
        });

        exitItem.setOnAction(e -> {
            Platform.exit();
        });

        // MOVIE ACTIONS
        addMovieItem.setOnAction(e -> showAddMovieForm());
        updateMovieItem.setOnAction(e -> showUpdateMovieForm());
        deleteMovieItem.setOnAction(e -> deleteSelectedMovie());
        searchMovieItem.setOnAction(e -> showSearchMovieForm());
        printTopLeastItem.setOnAction(e -> printTopAndLeastMovies());

        return menuBar;
    }

    private TableView<Movie> buildTableView() {
        TableView<Movie> tableView = new TableView<>();

        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Movie, Integer> releaseYearColumn = new TableColumn<>("Release Year");
        releaseYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

        TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        tableView.getColumns().addAll(titleColumn, descriptionColumn, releaseYearColumn, ratingColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        return tableView;
    }

    private HBox buildNavigationBox() {
        currentTreeLabel = new Label("Index: 0 - Height: 0");

        Button nextTreeButton = new Button("Next");
        Button previousTreeButton = new Button("Previous");

        nextTreeButton.setOnAction(e -> {
            try {
                currentTreeIndex[0] = catalog.nextTree(currentTreeIndex[0]);
                showMoviesInCurrentTree();
            } catch (IllegalStateException ex) {
                showErrorAlert("Navigation Error", ex.getMessage());
            }
        });

        previousTreeButton.setOnAction(e -> {
            try {
                currentTreeIndex[0] = catalog.previousTree(currentTreeIndex[0]);
                showMoviesInCurrentTree();
            } catch (IllegalStateException ex) {
                showErrorAlert("Navigation Error", ex.getMessage());
            }
        });

        // ComboBox for sort order
        sortOrderComboBox = new ComboBox<>();
        sortOrderComboBox.getItems().addAll("Ascending", "Descending");
        sortOrderComboBox.setValue("Ascending");

        Button sortButton = new Button("Sort");
        sortButton.setOnAction(e -> printSortedMovies());

        HBox navButtonsBox = new HBox(10, previousTreeButton, nextTreeButton,
                new Label("Sort Order:"), sortOrderComboBox, sortButton, currentTreeLabel);
        navButtonsBox.setAlignment(Pos.CENTER);
        navButtonsBox.setPadding(new Insets(10));

        return navButtonsBox;
    }

    private HBox buildActionButtons() {
        Button addButton = new Button("Add Movie");
        Button updateButton = new Button("Update Selected Movie");
        Button deleteButton = new Button("Delete Selected Movie");

        addButton.setOnAction(e -> showAddMovieForm());
        updateButton.setOnAction(e -> showUpdateMovieForm());
        deleteButton.setOnAction(e -> deleteSelectedMovie());

        HBox actionButtonsBox = new HBox(10, addButton, updateButton, deleteButton);
        actionButtonsBox.setAlignment(Pos.CENTER);
        actionButtonsBox.setPadding(new Insets(10));

        return actionButtonsBox;
    }

    private void showAddMovieForm() {
        Stage addStage = new Stage();
        addStage.setTitle("Add Movie");

        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        DatePicker releaseYearPicker = new DatePicker();
        TextField ratingField = new TextField();

        titleField.setPromptText("Title");
        descriptionField.setPromptText("Description");
        releaseYearPicker.setPromptText("Release Year");
        ratingField.setPromptText("Rating (0.0 - 10.0)");

        Button addButton = new Button("Add Movie");

        addButton.setOnAction(e -> {
            try {
                String title = titleField.getText();
                String description = descriptionField.getText();
                int releaseYear = releaseYearPicker.getValue().getYear();
                double rating = Double.parseDouble(ratingField.getText());

                if (releaseYear > LocalDate.now().getYear() || releaseYear < 0) {
                    throw new IllegalArgumentException("Release Year cannot be in the future.");
                }
                if (rating < 0.0 || rating > 10.0) {
                    throw new IllegalArgumentException("Rating must be between 0.0 and 10.0.");
                }

                Movie movie = new Movie(title, description, releaseYear, rating);
                catalog.add(movie);
                showMoviesInCurrentTree();
                addStage.close();
            } catch (Exception ex) {
                showErrorAlert("Add Movie Error", "Please enter valid data. " + ex.getMessage());
            }
        });

        pane.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Description:"), descriptionField,
                new Label("Release Year:"), releaseYearPicker,
                new Label("Rating:"), ratingField,
                addButton);

        Scene scene = new Scene(pane, 400, 400);
        addStage.setScene(scene);
        addStage.show();
    }

    private void showUpdateMovieForm() {
        Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showErrorAlert("Update Movie Error", "Please select a movie to update.");
            return;
        }

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Movie");

        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);

        TextField titleField = new TextField(selectedMovie.getTitle());
        String originalTitle = selectedMovie.getTitle();
        TextField descriptionField = new TextField(selectedMovie.getDescription());
        DatePicker releaseYearPicker = new DatePicker(LocalDate.of(selectedMovie.getReleaseYear(), 1, 1));
        TextField ratingField = new TextField(String.valueOf(selectedMovie.getRating()));

        Button updateButton = new Button("Update");

        updateButton.setOnAction(e -> {
            try {
                String description = descriptionField.getText();
                int releaseYear = releaseYearPicker.getValue().getYear();
                double rating = Double.parseDouble(ratingField.getText());

                if (releaseYear > LocalDate.now().getYear() || releaseYear < 0) {
                    throw new IllegalArgumentException("Release Year cannot be in the future.");
                }
                if (rating < 0.0 || rating > 10.0) {
                    throw new IllegalArgumentException("Rating must be between 0.0 and 10.0.");
                }

                catalog.erase(originalTitle);
                Movie updatedMovie = new Movie(titleField.getText(), description, releaseYear, rating);
                catalog.add(updatedMovie);
                showMoviesInCurrentTree();
                updateStage.close();
            } catch (Exception ex) {
                showErrorAlert("Update Movie Error", "Please enter valid data. " + ex.getMessage());
            }
        });

        pane.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Description:"), descriptionField,
                new Label("Release Year:"), releaseYearPicker,
                new Label("Rating:"), ratingField,
                updateButton);

        Scene scene = new Scene(pane, 400, 400);
        updateStage.setScene(scene);
        updateStage.show();
    }

    private void deleteSelectedMovie() {
        Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showErrorAlert("Delete Movie Error", "Please select a movie to delete.");
            return;
        }
        catalog.erase(selectedMovie.getTitle());
        showMoviesInCurrentTree();
    }

        private void showSearchMovieForm() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search Movie");

        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        TextField releaseYearField = new TextField();

        titleField.setPromptText("Title");
        releaseYearField.setPromptText("Release Year");

        Button searchButton = new Button("Search");

        searchButton.setOnAction(e -> {
            ArrayList<Movie> results = new ArrayList<>();

            if (!titleField.getText().isEmpty()) {
                Movie movie = catalog.get(titleField.getText());
                if (movie != null) {
                    results.add(movie);
                }
            } else if (!releaseYearField.getText().isEmpty()) {
                try {
                    int year = Integer.parseInt(releaseYearField.getText());
                    for (int i = 0; i < catalog.getCapacity(); i++) {
                        for (Movie m : catalog.getTree(i).toList()) {
                            if (m.getReleaseYear() == year) {
                                results.add(m);
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    showErrorAlert("Invalid Year", "Please enter a valid year.");
                    return;
                }
            }

            tableView.setItems(FXCollections.observableArrayList(results));
            currentTreeLabel.setText("---");
            searchStage.close();
        });

        pane.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Release Year:"), releaseYearField,
                searchButton);

        Scene scene = new Scene(pane, 400, 300);
        searchStage.setScene(scene);
        searchStage.show();
    }

    private void printSortedMovies() {
        ArrayList<Movie> sortedMovies = new ArrayList<>();
        if(currentTreeLabel.getText()!="---")
            sortedMovies = catalog.getTree(currentTreeIndex[0]).toList();
        else{
            for(Movie item : tableView.getItems()){
                sortedMovies.add(item);            }
        }
        if (sortOrderComboBox.getValue().equals("Ascending")) {
            sortedMovies.sort((m1, m2) -> m1.getTitle().compareTo(m2.getTitle()));
        } else {
            sortedMovies.sort((m1, m2) -> m2.getTitle().compareTo(m1.getTitle()));
        }
        tableView.setItems(FXCollections.observableArrayList(sortedMovies));
        if(currentTreeLabel.getText()!="---")
        currentTreeLabel.setText(
                "Index: " + currentTreeIndex[0] + " - Height: " + catalog.getTree(currentTreeIndex[0]).height());
    }

    private void printTopAndLeastMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        if(currentTreeLabel.getText() == "---"){
            for(Movie item : tableView.getItems()){
                movies.add(item);
            }
        }else{
        movies = catalog.getTree(currentTreeIndex[0]).toList();
        }
        if (movies.isEmpty()) {
            showErrorAlert("Top/Least Movie", "Current tree is empty.");
            return;
        }

        Movie topRated = movies.get(0);
        Movie leastRated = movies.get(0);

        for (Movie m : movies) {
            if (m.getRating() > topRated.getRating()) {
                topRated = m;
            }
            if (m.getRating() < leastRated.getRating()) {
                leastRated = m;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Top and Least Rated Movies");
        if(currentTreeLabel.getText() != "---")
            alert.setHeaderText("In Tree Index " + currentTreeIndex[0]);
        else
            alert.setHeaderText("");
        alert.setContentText("Top Rated: " + topRated.getTitle() + " (" + topRated.getRating() + ")\n"
                + "Least Rated: " + leastRated.getTitle() + " (" + leastRated.getRating() + ")");
        alert.showAndWait();
    }

    private void showMoviesInCurrentTree() {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        movies.addAll(catalog.getTree(currentTreeIndex[0]).toList());
        tableView.setItems(movies);
        currentTreeLabel.setText(
                "Index: " + currentTreeIndex[0] + " - Height: " + catalog.getTree(currentTreeIndex[0]).height());
        tableView.refresh();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
