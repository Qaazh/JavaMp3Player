package studiplayer.ui;

import javafx.stage.FileChooser;
import java.util.concurrent.CountDownLatch;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class Player extends Application {
    public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
    private static final String PLAYLIST_DIRECTORY = "playlists/";
    private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
    private static final String NO_CURRENT_SONG = " - ";

    private PlayList playList;
    private boolean useCertPlayList = false;

    private Button playButton = createButton("play.jpg");
    private Button pauseButton = createButton("pause.jpg");
    private Button stopButton = createButton("stop.jpg");
    private Button nextButton = createButton("next.jpg");
    private Button filterButton = new Button("Display");

    private Label playListLabel = new Label(DEFAULT_PLAYLIST);
    private Label playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
    private Label currentSongLabel = new Label(NO_CURRENT_SONG);

    private ChoiceBox<SortCriterion> sortChoiceBox = new ChoiceBox<>();
    private TextField searchTextField = new TextField("");

    private SongTable songTable;

    public enum PlaybackState {
        STOPPED,
        PLAYING,
        PAUSED
    }
    private PlaybackState playbackState;
    private PlayerThread playerThread;
    private TimerThread timerThread;
    

    public Player() {
        useCertPlayList = false;
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(String pathname) throws FileNotFoundException {
    	loadPlayList(pathname);
    }

    

    public void setUseCertPlayList(boolean value) {
        this.useCertPlayList = value;
    }

    public void loadPlayList(String pathname) {
        if (pathname == null || pathname.isEmpty()) {
            pathname = DEFAULT_PLAYLIST;
        }

        try {
            playList = new PlayList(pathname); // Load the playlist

            // Update the UI elements and current song
            playListLabel.setText(pathname);
            if (playList.size() > 0) {
                // Manually move to the first song
                playList.resetIterator();
                updateSongInfo(playList.currentAudioFile());
            } else {
                updateSongInfo(null);
            }

            
            updateButtonStates(true, false, false, playList != null && playList.size() > 1);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private Button createButton(String iconfile) {
        Button button = null;
        try {
            URL url = getClass().getResource("/icons/" + iconfile);
            Image icon = new Image(url.toString());
            ImageView imageView = new ImageView(icon);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            button = new Button("", imageView);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setStyle("-fx-background-color: #fff;");
        } catch (Exception e) {
            System.out.println("Image " + "icons/" + iconfile + " not found!");
            System.exit(-1);
        }
        return button;
    }
    
    private void updateButtonStates(boolean playButtonState, boolean pauseButtonState, boolean stopButtonState, boolean nextButtonState) {
        playButton.setDisable(!playButtonState); //call disable, but i want it to be false because i want it enabled! 
        pauseButton.setDisable(!pauseButtonState);
        stopButton.setDisable(!stopButtonState);
        nextButton.setDisable(!nextButtonState);
    }
    
    private void playCurrentSong() {
        if (playbackState == PlaybackState.PAUSED) {
        	pauseCurrentSong();
        } else {
            if (playList != null && playList.currentAudioFile() != null) {
                playbackState = PlaybackState.PLAYING;
                startThreads(false);
                updateButtonStates(false, true, true, true);
            }
        }
    }

    private void pauseCurrentSong() {
        if (playbackState == PlaybackState.PLAYING) {
        	terminateThreads(true);
        	playbackState = PlaybackState.PAUSED;
        }else if (playbackState == PlaybackState.PAUSED) {
        	startThreads(true);
        	playbackState = PlaybackState.PLAYING;
        	
        }
            //playerThread.setPause();
            //terminateThreads(true); // Terminate timer thread only
            AudioFile currentSong = playList.currentAudioFile();
    		currentSong.togglePause();
    		playTimeLabel.setText(currentSong.formatPosition());
            updateButtonStates(false, true, true, true);
        
    }

    private void stopCurrentSong() {
    	if (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.PAUSED) {
            playbackState = PlaybackState.STOPPED;
            terminateThreads(false); // Terminate both threads
            System.out.println(playTimeLabel);

         // Reset the play time label to its initial value
            Platform.runLater(() -> {
                playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
            });
            //updateSongInfo(null);
            System.out.println(playTimeLabel);
            updateButtonStates(true, false, false, true);
        }
    }

    public void playNextSong() {
        if (playList != null) {
        	playList.nextSong();
            AudioFile nextSong = playList.currentAudioFile();
            if (nextSong != null) {
                updateSongInfo(nextSong);
                playCurrentSong();
                //updateButtonStates(false, true, true, playList != null && playList.size() > 1);
            }
        }
    }

    private void filterSongs() {
        String searchText = searchTextField.getText();
        SortCriterion sortOption = sortChoiceBox.getValue();
        // Logic to filter and sort the songs in the playlist
        playList.setSearch(searchText);
        
        playList.setSortCriterion(sortOption);
        
        songTable.refreshSongs();
    }

    public void updateSongInfo(AudioFile currentAudioFile) {
        Platform.runLater(() -> {
            if (currentAudioFile == null) {
                currentSongLabel.setText(NO_CURRENT_SONG);
                playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
            } else {
                currentSongLabel.setText(currentAudioFile.toString());
                playTimeLabel.setText(currentAudioFile.formatPosition());
            }
        });
    }

    private class PlayerThread extends Thread {
        private AudioFile audioFile;
        private Player player;
        private boolean stopped;

        public PlayerThread(AudioFile audioFile, Player player) {
            this.audioFile = audioFile;
            this.player = player;
            this.stopped = false;
        }

        public synchronized void terminate() {
            stopped = true;
            audioFile.stop();  // Ensure the current song is stopped
            Platform.runLater(() -> playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL));
            notify();
        }

        @Override
        public void run() {
            try {
                audioFile.play();
            } catch (NotPlayableException e) {
                e.printStackTrace();
            }

            // Check for end of song (and not stopped manually)
            if (!stopped && audioFile.formatPosition().equals(audioFile.formatDuration())) {
                Platform.runLater(() -> {
                    playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
                    playNextSong(); // Move to the next song
                });
            }
        }
    }
    private class TimerThread extends Thread {
        private Player player;
        private boolean stopped;

        public synchronized void terminate() {
            stopped = true;
            notify();
        }
        
        public void setPlayer(Player player) {
            this.player = player;
        }
        
        @Override
        public void run() {
            while (!stopped) {
                Platform.runLater(() -> {
                    if (player.getPlayList() != null && player.getPlayList().currentAudioFile() != null) {
                        player.updateSongInfo(playList.currentAudioFile());
                    }
                });

                try {
                    Thread.sleep(1000); // Update every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void startThreads(boolean Timer) {
    	// Start TimerThread
        
            timerThread = new TimerThread();
            timerThread.setPlayer(this);
            timerThread.start();
        
        // Start PlayerThread
            if (!Timer) {
                playerThread = new PlayerThread(playList.currentAudioFile(), this);
                playerThread.start();
            }
        }
	
	private void terminateThreads(boolean Timer) {
		// Terminate TimerThread
	        timerThread.terminate();
	        timerThread = null;

	    if(!Timer){// Terminate PlayerThread
		        playerThread.terminate();
		        playerThread = null;
		    }
	    }
	    
	    /*if (playbackState != PlaybackState.PAUSED) {
            playbackState = PlaybackState.STOPPED;
            Platform.runLater(() -> playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL));
        }*/
    public void start(Stage stage) throws Exception {
    	if (useCertPlayList) {
    		playList = new PlayList(DEFAULT_PLAYLIST);
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Playlist File");
            fileChooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                String pathname = selectedFile.getAbsolutePath();
                playList = new PlayList(pathname);
            }
        }

        stage.setTitle("APA Player");

        songTable = new SongTable(playList);
     
        songTable.setRowSelectionHandler(e -> {
            TableViewSelectionModel<Song> selectionModel = songTable.getSelectionModel();
            ObservableList<Integer> selectedItems = selectionModel.getSelectedIndices();
            if (!selectedItems.isEmpty()) {
                playList.jumpToAudioFile(playList.getList().get(selectedItems.get(0)));
                playbackState = PlaybackState.PLAYING;
                playCurrentSong();
                updateSongInfo(playList.currentAudioFile());
                
            }
        });

        playButton.setOnAction(e -> playCurrentSong());
        pauseButton.setOnAction(e -> pauseCurrentSong());
        stopButton.setOnAction(e -> stopCurrentSong());
        nextButton.setOnAction(e -> playNextSong());

        sortChoiceBox.getItems().addAll(SortCriterion.values());
        sortChoiceBox.setValue(SortCriterion.DEFAULT);

        filterButton.setOnAction(e -> filterSongs());

        BorderPane mainPane = new BorderPane();
        TitledPane filterForm = new TitledPane();
        GridPane gridPaneFilter = new GridPane();

        gridPaneFilter.add(new Label("Search text"), 0, 0);
        gridPaneFilter.add(searchTextField, 1, 0);
        gridPaneFilter.add(new Label("Sort by"), 0, 1);
        gridPaneFilter.add(sortChoiceBox, 1, 1);
        gridPaneFilter.add(filterButton, 2, 1);

        gridPaneFilter.setHgap(10);
        gridPaneFilter.setVgap(10);

        filterForm.setContent(gridPaneFilter);

        VBox bottomBox = new VBox();
        bottomBox.setPadding(new Insets(10, 10, 10, 10));
        GridPane songInfoPane = new GridPane();
        songInfoPane.add(new Label("Playlist"), 0, 0);
        songInfoPane.add(playListLabel, 1, 0);
        songInfoPane.add(new Label("Current Song"), 0, 1);
        songInfoPane.add(currentSongLabel, 1, 1);
        songInfoPane.add(new Label("Playtime"), 0, 2);
        songInfoPane.add(playTimeLabel, 1, 2);
        songInfoPane.setHgap(10);

        HBox controlButtons = new HBox();
        controlButtons.setAlignment(Pos.CENTER);
        controlButtons.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);

        bottomBox.getChildren().addAll(songInfoPane, controlButtons);

        mainPane.setTop(filterForm);
        mainPane.setCenter(songTable);
        mainPane.setBottom(bottomBox);

        Scene scene = new Scene(mainPane, 750, 500);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}


