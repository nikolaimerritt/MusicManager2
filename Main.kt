package sample

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import java.util.*

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

        // add plus button
        val plusButton = Button("+")
        plusButton.setOnAction { addSong() }
        GridPane.setConstraints(plusButton, 98, 0)
        grid.children.add(plusButton)

	    // add seek slider
	    val seekSlider = Slider()
	    seekSlider.valueProperty().addListener { _, _, newValue -> if (seekSlider.isPressed) { mp3Player.seekTo(newValue.toDouble()) } }
	    GridPane.setConstraints(seekSlider, 1, 3, 97, 1)
	    grid.children.add(seekSlider)

        // add song table
        val observableList = FXCollections.observableList<String>(arrayListOf(""))
	    val listView = ListView(observableList)
	    listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
		    val newIndex = observableList.indexOf(newValue)
		    if (newIndex >= 0 && newIndex < observableList.size)
		    {
			    mp3Player.skipTo(observableList.indexOf(newValue))
			    seekSlider.value = 0.0
		    }
	     }
        listView.orientation = Orientation.VERTICAL
        GridPane.setConstraints(listView, 0, 2, 100, 1)
        grid.children.add(listView)

	    // adding search box
	    val searchBox = TextField()
	    searchBox.textProperty().addListener { _, _, searchText
	    ->
			listView.items.clear()
		    MP3Player.titlesInPlaylist(currentPlaylist).forEach { if (it.toLowerCase().contains(searchText.toLowerCase()) || searchText == "") listView.items.add(it) }
	    }
	    searchBox.promptText = "Search me baby..."
	    GridPane.setConstraints(searchBox, 1, 0, 97, 1)
	    grid.children.add(searchBox)

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
        stage.show()
	    val incrementSliderThread = Thread(Runnable
	    {
		    var unixTime = System.currentTimeMillis()
		    while (true)
		    {
			    if (System.currentTimeMillis() - unixTime >= 100 && mp3Player.playing)
			    {
				    val tick = mp3Player.currentSongLength() / 10000000
				    seekSlider.value += tick
				    unixTime = System.currentTimeMillis()
			    }
			    if (mp3Player.newSong)
			    {
				    println("newwww")
				    seekSlider.adjustValue(0.0)
				    mp3Player.newSong = false
			    }
		    }
	    })
	    incrementSliderThread.start()
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