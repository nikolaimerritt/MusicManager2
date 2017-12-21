package sample

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.stage.FileChooser
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.mp3.Mp3Parser
import org.xml.sax.helpers.DefaultHandler
import sample.EditPlaylistWindow.Companion.spawnEditPlaylistWindow
import java.io.FileInputStream

class Main : Application()
{
	private val mp3Player = MP3Player()
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
	    listView.selectionModel.selectedItemProperty().addListener { _, _, _ ->
		    val newIndex = listView.selectionModel.selectedIndex
		    if (newIndex >= 0 && newIndex < listView.items.size)
		    {
			    println("current playlist: ${currentPlaylist.playlistName}")
			    mp3Player.reload(MP3Player.songsInPlaylist(currentPlaylist))
			    mp3Player.skipTo(newIndex)
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
		    MP3Player.songTitlesInPlaylist(currentPlaylist).forEach { if (it.toLowerCase().contains(searchText.toLowerCase()) || searchText == "") listView.items.add(it) }
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
		    observableList.addAll(MP3Player.songTitlesInPlaylist(currentPlaylist))
	    }
	    playlistMenu.selectionModel.select("All Songs")
	    GridPane.setConstraints(playlistMenu, 0 ,1, 5, 1)
	    grid.children.add(playlistMenu)

	    // add play playlist button
	    val playPlaylistButton = Button("Play playlist")
	    playPlaylistButton.setOnAction {
		    mp3Player.stop()
		    mp3Player.reload(MP3Player.songsInPlaylist(currentPlaylist))
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
	    val runActionOnComboBox = ComboBox<String>(FXCollections.observableArrayList(arrayListOf("To...", "Playlist", "Song")))
	    runActionOnComboBox.selectionModel.selectFirst()
	    GridPane.setConstraints(runActionOnComboBox, 80, 1, 10, 1)
	    grid.children.add(runActionOnComboBox)

	    // add Execute button
	    val executeButton = Button("Go!")
	    executeButton.setOnMouseClicked {
		    if (functComboBox.value == "Add")
		    {
			    if (runActionOnComboBox.value == "Playlist")
			    {
				    val dbConn = DBConn()
				    dbConn.linesFromQuery("INSERT INTO Playlist VALUES ('$FRESH_AF_PLAYLIST_NAME', '${currentUser.username}', TRUE);")
				    dbConn.close()
				    spawnEditPlaylistWindow(MP3Player.playlistFromName(FRESH_AF_PLAYLIST_NAME))
			    }
			    else if (runActionOnComboBox.value == "Song")
			    {
				    val songFile =  FileChooser().showOpenDialog(stage)
				    if (songFile != null)
				    {
					    val input = FileInputStream(songFile)
					    val metadata = Metadata()
						Mp3Parser().parse(input, DefaultHandler(), metadata, ParseContext())
					    input.close()

					    val newSongIndex = MP3Player.songsInPlaylist(MP3Player.playlistFromName("All Songs")).last().songID + 1
					    println(newSongIndex)
					    val newSong = Song(newSongIndex, metadata.get("title"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), songFile.absolutePath.replace("\\", "\\\\"))
					    val dbConn = DBConn()
					    dbConn.linesFromQuery("INSERT INTO Song (songID, title, artist, album, filepath) VALUES (${newSong.songID}, '${newSong.title}', '${newSong.artist}', '${newSong.album}', '${newSong.filepath}');")
					    dbConn.linesFromQuery("INSERT INTO PlaylistSong VALUES ('All Songs', ${newSong.songID});")
					    dbConn.close()
				    }
			    }
		    }
		    else if (functComboBox.value == "Edit")
		    {
			    if (runActionOnComboBox.value == "Playlist")
			    {
				    if (currentPlaylist.isUserEditable) { spawnEditPlaylistWindow(currentPlaylist) }
				    else { Alert(Alert.AlertType.ERROR, "Cannot edit \"${currentPlaylist.playlistName}\".").showAndWait() }
			    }
		    }
	    }
	    GridPane.setConstraints(executeButton, 97, 1)
	    grid.children.add(executeButton)

	    mp3Player.reload(MP3Player.songsInPlaylist(MP3Player.playlistFromName("All Songs")))
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

    private fun showNotImplemented() = Alert(Alert.AlertType.WARNING, "Not implemented yet... But watch this space!").showAndWait()

    companion object
    {
	    val FRESH_AF_PLAYLIST_NAME = "<New Playlist>"
        @JvmStatic fun main(args: Array<String>)
        {
	        launch(Main::class.java)
        }
    }
}