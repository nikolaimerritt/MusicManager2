package sample

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import java.util.*
import kotlin.collections.ArrayList

class Main : Application()
{
	private val mp3Player = MP3Player()
	private val PLACEHOLDER_PLAYLIST_NAME = "<New Playlist>"
	private val currentUser = User("laudo", "laudo_")
	private var currentPlaylist = Playlist("All Songs", currentUser.username, true)

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

	    // add seek slider
	    val seekSlider = Slider()
	    seekSlider.valueProperty().addListener { _, _, newValue -> if (seekSlider.isPressed) { mp3Player.seekTo(newValue.toDouble()) } }
	    GridPane.setConstraints(seekSlider, 1, 3, 94, 1)
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
        GridPane.setConstraints(listView, 0, 2, 98, 1)
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

	    // add playlist combo box
	    val playlistMenu = ComboBox<String>(FXCollections.observableList<String>(MP3Player.allPlaylistNames()))
	    playlistMenu.valueProperty().addListener { _, _, newPlaylistName
	    ->
		    currentPlaylist = MP3Player.playlistFromName(newPlaylistName)
		    observableList.clear()
		    observableList.addAll(MP3Player.titlesInPlaylist(currentPlaylist))
	    }
	    playlistMenu.selectionModel.select("All Songs")
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
        GridPane.setConstraints(skipBackwardsButton, 95, 3)
        grid.children.add(skipBackwardsButton)

        // add skip forwards button
        val skipForwardsButton = Button("▶")
        skipForwardsButton.setOnMouseClicked { mp3Player.skipForward() }
        GridPane.setConstraints(skipForwardsButton, 97, 3)
        grid.children.add(skipForwardsButton)

	    // add Add options
	    val functComboBox = ComboBox<String>(FXCollections.observableArrayList(arrayListOf("Function...", "Add", "Edit", "Delete")))
	    functComboBox.selectionModel.selectFirst()
	    GridPane.setConstraints(functComboBox, 70, 1, 10, 1)
	    grid.children.add(functComboBox)

	    // add Rename options
	    val toComboBox = ComboBox<String>(FXCollections.observableArrayList(arrayListOf("To...", "Playlist", "Song")))
	    toComboBox.selectionModel.selectFirst()
	    GridPane.setConstraints(toComboBox, 80, 1, 10, 1)
	    grid.children.add(toComboBox)

	    // add Execute button
	    val executeButton = Button("Go!")
	    executeButton.setOnMouseClicked {
		    if (functComboBox.value == "Add")
		    {
			    if (toComboBox.value == "Playlist")
			    {
				    val dbConn = DBConn()
				    dbConn.executeChangeQuery("INSERT INTO Playlist VALUES ('$PLACEHOLDER_PLAYLIST_NAME', '${currentUser.username}', TRUE);")
				    dbConn.close()
				    spawnEditPlaylistWindow(MP3Player.playlistFromName(PLACEHOLDER_PLAYLIST_NAME))
			    }
			    else if (toComboBox.value == "Song") {}
		    }
		    else if (functComboBox.value == "Edit")
		    {
			    if (toComboBox.value == "Playlist")
			    {
				    if (currentPlaylist.isUserEditable) { spawnEditPlaylistWindow(currentPlaylist) }
				    else { Alert(Alert.AlertType.ERROR, "Cannot edit \"${currentPlaylist.playlistName}\".").showAndWait() }
			    }
		    }
	    }
	    GridPane.setConstraints(executeButton, 97, 1)
	    grid.children.add(executeButton)

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
				    seekSlider.adjustValue(0.0)
				    mp3Player.newSong = false
			    }
		    }
	    })
	    incrementSliderThread.start()
    }

    private fun login() = showNotImplemented()

	private fun spawnEditPlaylistWindow(playlist: Playlist)
	{
		val grid = GridPane()
		val scene = Scene(grid)
		val stage = Stage()
		stage.scene = scene
		stage.title = "Edit ${playlist.playlistName}"
		stage.setOnCloseRequest {
			val confirmationResult = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close without saving?").showAndWait()
			if (confirmationResult.get() == ButtonType.OK) { stage.close() }
		}
		grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
		grid.vgap = 5.0
		grid.hgap = 5.0

		// add name text box
		val playlistNameBox = TextField()
		playlistNameBox.promptText = "New playlist name..."
		GridPane.setConstraints(playlistNameBox, 0, 0, 90, 1)
		grid.children.add(playlistNameBox)

		// add explanatory header
		val hintHeader = TextField("Click a song to move it to the opposite playlist.")
		hintHeader.isEditable = false
		GridPane.setConstraints(hintHeader, 0, 1, 80, 1)
		grid.children.add(hintHeader)

		// add All Songs header
		val allSongsHeader = TextField("All Songs")
		allSongsHeader.isEditable = false
		GridPane.setConstraints(allSongsHeader, 0, 2, 44, 1)
		grid.children.add(allSongsHeader)

		// add Current Playlist header
		val currentPlaylistHeader = TextField(playlist.playlistName)
		currentPlaylistHeader.isEditable = false
		GridPane.setConstraints(currentPlaylistHeader, 45, 2, 44, 1)
		grid.children.add(currentPlaylistHeader)

		// add All Songs table
		val allSongs = MP3Player.songsInPlaylist(MP3Player.playlistFromName("All Songs"))
		val allSongNames = FXCollections.observableArrayList<String>(MP3Player.titlesInPlaylist(MP3Player.playlistFromName("All Songs")))
		val currentSongs = ArrayList<Song>(MP3Player.songsInPlaylist(playlist))
		val currentSongNames = FXCollections.observableArrayList<String>(MP3Player.titlesInPlaylist(MP3Player.playlistFromName(playlist.playlistName)))

		val allSongsLV = ListView(allSongNames)
		var songHistory = ArrayDeque<Song>()
		allSongsLV.selectionModel.selectedItemProperty().addListener { _, _, selectedSongName
		->
			if (selectedSongName in allSongNames)
			{
				val newSong = allSongs[allSongsLV.selectionModel.selectedIndex]
				if (newSong !in currentSongs)
				{
					songHistory.add(newSong)
					currentSongs.add(newSong)
					currentSongNames.add(newSong.title)
				}
			}
		}
		GridPane.setConstraints(allSongsLV, 0, 3, 44, 10)
		grid.children.add(allSongsLV)

		// add Current Playlist table
		val currentSongsLV = ListView(currentSongNames)
		GridPane.setConstraints(currentSongsLV, 45, 3, 44, 10)
		grid.children.add(currentSongsLV)

		val undoButton = Button("Undo")
		undoButton.setOnMouseClicked {
			if (songHistory.isNotEmpty())
			{
				val songToRemove = songHistory.pop()
				currentSongsLV.items.removeAt(currentSongs.indexOf(songToRemove))
				currentSongs.remove(songToRemove)
			}
		}
		GridPane.setConstraints(undoButton, 88, 1)
		grid.children.add(undoButton)

		// add publish button
		val publishButton = Button("Publish!")
		publishButton.setOnMouseClicked {
			if (playlist.playlistName == PLACEHOLDER_PLAYLIST_NAME && playlistNameBox.text == "") { Alert(Alert.AlertType.ERROR, "Please give your new playlist a name").showAndWait() }
			else
			{
				val dbConn = DBConn()
				val newPlaylistName = if (playlistNameBox.text.isNotBlank()) playlistNameBox.text else playlist.playlistName
				if (playlist.playlistName == PLACEHOLDER_PLAYLIST_NAME)
				{
					dbConn.executeChangeQuery("SET FOREIGN_KEY_CHECKS = 0;")
					dbConn.executeChangeQuery("UPDATE Playlist SET playlistName = '$newPlaylistName' WHERE playlistName = '$PLACEHOLDER_PLAYLIST_NAME';")
					dbConn.executeChangeQuery("UPDATE PlaylistSong SET playlistName = '$newPlaylistName' WHERE playlistName = '$PLACEHOLDER_PLAYLIST_NAME';")
					dbConn.executeChangeQuery("SET FOREIGN_KEY_CHECKS = 1;")
				}
				else { dbConn.executeChangeQuery("INSERT INTO Playlist VALUES ('$newPlaylistName', '${currentUser.username}', TRUE);") }
				currentSongs
						.map { "INSERT INTO PlaylistSong VALUES ('$newPlaylistName', ${it.songID});" }
						.forEach { dbConn.executeChangeQuery(it) }
				dbConn.close()
				stage.close()
			}
		}
		GridPane.setConstraints(publishButton, 0, 13)
		grid.children.add(publishButton)

		stage.show()
	}

    private fun showNotImplemented() = Alert(Alert.AlertType.WARNING, "Not implemented yet... But watch this space!").showAndWait()

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>) = launch(Main::class.java)
    }
}