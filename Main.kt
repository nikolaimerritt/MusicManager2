package sample

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*

class Main : Application()
{
	private val mp3Player = MP3Player()
	//private val allPlaylists = MP3Player.allPlaylists()
	private var currentPlaylist = Playlist("All Songs", "laudo", true)

    override fun start(stage: Stage)
    {
        // initialising stage
        val grid = GridPane()
        val scene = Scene(grid)
        stage.scene = scene
        stage.title = "Music Manager"
        stage.setOnCloseRequest { System.exit(0) }
        grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
        grid.vgap = 5.0
        grid.hgap = 5.0

        // adding user login button
        val loginButton = Button("Login")
        loginButton.setOnAction { login() }
        GridPane.setConstraints(loginButton, 0, 0)
        grid.children.add(loginButton)

        // adding search box
        val searchBox = TextField()
        searchBox.promptText = "Search me baby..."
        GridPane.setConstraints(searchBox, 1, 0, 97, 1)
        grid.children.add(searchBox)

        // add plus button
        val plusButton = Button("+")
        plusButton.setOnAction { addSong() }
        GridPane.setConstraints(plusButton, 98, 0)
        grid.children.add(plusButton)

        // add song table
        val observableList = FXCollections.observableList<String>(arrayListOf(""))//MP3Player.titlesInPlaylist(currentPlaylist))
	    //mp3Player.push(MP3Player.songsInPlaylist(currentPlaylist))
	    val listView = ListView(observableList)
	    listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
		    val newIndex = observableList.indexOf(newValue)
		    if (newIndex >= 0 && newIndex < observableList.size) { mp3Player.skipTo(observableList.indexOf(newValue)) }
	     }
        listView.orientation = Orientation.VERTICAL
        GridPane.setConstraints(listView, 0, 2, 100, 1)
        grid.children.add(listView)

	    // add drop-down playlist menu
	    val playlistMenu = ComboBox<String>(FXCollections.observableList<String>(MP3Player.allPlaylistNames()))
	    playlistMenu.valueProperty().addListener { _, _, newPlaylistName
	    ->
		    currentPlaylist = MP3Player.playlistFromName(newPlaylistName)
		    observableList.clear()
		    observableList.addAll(MP3Player.titlesInPlaylist(currentPlaylist))
	    }
	    playlistMenu.selectionModel.selectFirst()
	    GridPane.setConstraints(playlistMenu, 0 ,1, 5, 1)
	    grid.children.add(playlistMenu)

	    // add play playlist button
	    val playPlaylistButton = Button("Play playlist")
	    playPlaylistButton.setOnAction {
		    mp3Player.stop()
		    mp3Player.clear()
		    mp3Player.push(MP3Player.songsInPlaylist(currentPlaylist))
		    mp3Player.play()
	    }
	    GridPane.setConstraints(playPlaylistButton,6, 1)
	    grid.children.add(playPlaylistButton)

        // add play/pause button
        val playPauseButton = Button("| | ▶")
        playPauseButton.setOnAction { mp3Player.togglePlayPause() }
        GridPane.setConstraints(playPauseButton, 0, 3)
        grid.children.add(playPauseButton)

        // add seek slider
        val slider = Slider()
	    slider.valueProperty().addListener { _, _, newValue -> mp3Player.seekTo(newValue.toDouble()) }
        GridPane.setConstraints(slider, 1, 3, 97, 1)
        grid.children.add(slider)

        // add skip backwards button
        val skipBackwardsButton = Button("◀")
        skipBackwardsButton.setOnMouseClicked { mp3Player.skipBackward() }
        GridPane.setConstraints(skipBackwardsButton, 98, 3)
        grid.children.add(skipBackwardsButton)

        // add skip forwards button
        val skipForwardsButton = Button("▶")
        skipForwardsButton.setOnMouseClicked { mp3Player.skipForward() }
        GridPane.setConstraints(skipForwardsButton, 99, 3)
        grid.children.add(skipForwardsButton)

	    mp3Player.push(MP3Player.songsInPlaylist(MP3Player.playlistFromName("All Songs")))
	    mp3Player.startFromBeginning()
        stage.show()
    }

    private fun login() = showNotImplemented()
    private fun addSong() = mp3Player.seekTo(50.0)

    private fun showNotImplemented() = Alert(Alert.AlertType.WARNING, "Not implemented yet... But watch this space!").showAndWait()

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>) = launch(Main::class.java)
    }
}