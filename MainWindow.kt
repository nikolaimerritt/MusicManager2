package sample

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.mp3.Mp3Parser
import org.xml.sax.helpers.DefaultHandler
import java.io.FileInputStream

class MainWindow
{
	companion object
	{
		private val mp3Player = MP3Player()

		fun show(currentUser: User)
		{
			var currentPlaylist = Playlist("All Songs", currentUser.username, false)

			// initialising stage
			val stage = Stage()
			val grid = GridPane()
			val scene = Scene(grid)
			stage.scene = scene
			stage.title = "Music Manager"
			stage.setOnCloseRequest { System.exit(0) }
			grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
			grid.vgap = 5.0
			grid.hgap = 5.0

			val functComboBox = ComboBox<String>(FXCollections.observableArrayList(arrayListOf("Function...", "Add", "Edit", "Delete")))
			val runActionOnComboBox = ComboBox<String>(FXCollections.observableArrayList(arrayListOf("To...", "Playlist", "Song")))
			fun editingSongs() = functComboBox.value == "Edit" && runActionOnComboBox.value == "Song"
			fun deletingSongs() = functComboBox.value == "Delete" && runActionOnComboBox.value == "Song"
			fun deletingPlaylist() = functComboBox.value == "Delete" && runActionOnComboBox.value == "Playlist"

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
			val listView = ListView(FXCollections.observableList(arrayListOf("")))
			listView.selectionModel.selectedItemProperty().addListener { _, _, _ ->
				val index = listView.selectionModel.selectedIndex
				if (index >= 0 && index < listView.items.size) // if the user pressed on a row with a song on it, not just a blank row
				{
					when
					{
						editingSongs() ->
						{
							val oldSongName = MP3Player.songTitlesInPlaylist(currentPlaylist)[index]
							val dialog = TextInputDialog() // showing a "rename this song to __ " dialog
							dialog.title = "Rename Song"
							dialog.headerText = "Rename $oldSongName:"
							dialog.showAndWait().ifPresent { // if the user inputted a new name
								val dbConn = DBConn()
								dbConn.runQuery("UPDATE Song SET title = '$it' WHERE songID = ${index + 1}") // SQL uses 1-based indexes. `it` is a kotlin-made variable that stores the song name
								dbConn.close()
							}
						}
						deletingSongs() ->
						{
							val songNameToDelete = MP3Player.songTitlesInPlaylist(currentPlaylist)[index]
							val confirmation = Alert(Alert.AlertType.CONFIRMATION)
							confirmation.title = "Delete $songNameToDelete"
							confirmation.headerText = "Are you sure you want to delete $songNameToDelete from ${currentPlaylist.playlistName}?"
							if (confirmation.showAndWait().get() == ButtonType.OK) // if the user said yes to deleting the song
							{
								val dbConn = DBConn()
								val songID = MP3Player.nthSongInPlaylist(currentPlaylist, index).songID
								dbConn.runQuery("DELETE FROM PlaylistSong WHERE songID = $songID") // will delete the song entirely -- from all playlists
								dbConn.runQuery("DELETE FROM Song WHERE songID = $songID")
								dbConn.close()
							}
						}
						else -> // pressing a song without any special mode selected. just playing the song normally
						{
							mp3Player.reload(MP3Player.songsInPlaylist(currentPlaylist))
							mp3Player.skipTo(index)
							seekSlider.value = 0.0 // starting from beginning
						}
					}
				}
			}
			listView.orientation = Orientation.VERTICAL
			GridPane.setConstraints(listView, 0, 2, 98, 1)
			grid.children.add(listView)

			// adding search box
			val searchBox = TextField()
			val songTitlesCached = FXCollections.observableArrayList(MP3Player.songTitlesInPlaylist(currentPlaylist))
			searchBox.textProperty().addListener { _, _, searchText
				->
				val searchTrimmed = searchText.trim().toLowerCase()
				if (searchTrimmed.isNotBlank())
				{
					listView.items.clear()
					songTitlesCached.forEach { if (it.toLowerCase().contains(searchTrimmed)) listView.items.add(it) }
				}
				else { listView.items = songTitlesCached }
			}
			searchBox.promptText = "Search the current playlist..."
			GridPane.setConstraints(searchBox, 1, 0, 97, 1)
			grid.children.add(searchBox)

			// add playlist combo box
			val playlistMenu = ComboBox<String>(FXCollections.observableList<String>(MP3Player.allPlaylistNames()))
			playlistMenu.valueProperty().addListener { _, _, newPlaylistName
				->
				currentPlaylist = MP3Player.playlistFromName(newPlaylistName)
				listView.items.clear()
				listView.items.addAll(MP3Player.songTitlesInPlaylist(currentPlaylist))
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
			functComboBox.selectionModel.selectFirst()
			GridPane.setConstraints(functComboBox, 70, 1, 10, 1)
			grid.children.add(functComboBox)

			// add Rename options
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
						dbConn.runQuery("INSERT INTO Playlist VALUES ('${Main.FRESH_AF_PLAYLIST_NAME}', '${currentUser.username}', TRUE);")
						dbConn.close()
						EditPlaylistWindow.spawnEditPlaylistWindow(MP3Player.playlistFromName(Main.FRESH_AF_PLAYLIST_NAME))
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
							val newTitle = if (metadata.get("title") != null) { metadata.get("title") } else songFile.name.substringBefore(".").trim()
							val newArtist = if (metadata.get("xmpDM:artist") != null) { metadata.get("artist") } else ""
							val newAlbum = if (metadata.get("xmpDM:album") != null) { metadata.get("album") } else ""
							val newSongPath = songFile.absolutePath.replace("\\", "@@")

							val dbConn = DBConn()
							println("INSERT INTO Song (songID, title, artist, album, filepath) VALUES ($newSongIndex, '$newTitle', '$newArtist', '$newAlbum', '$newSongPath');")
							dbConn.runQuery("INSERT INTO Song (songID, title, artist, album, filepath) VALUES ($newSongIndex, '$newTitle', '$newArtist', '$newAlbum', '$newSongPath');")
							dbConn.runQuery("INSERT INTO PlaylistSong VALUES ('All Songs', $newSongIndex);")
							println("INSERT INTO PlaylistSong VALUES ('All Songs', $newSongIndex);")

							dbConn.close()
						}
					}
				}
				else if (functComboBox.value == "Edit")
				{
					if (runActionOnComboBox.value == "Playlist")
					{
						if (currentPlaylist.isUserEditable) {
							EditPlaylistWindow.spawnEditPlaylistWindow(currentPlaylist)
						}
						else { Alert(Alert.AlertType.ERROR, "Cannot edit \"${currentPlaylist.playlistName}\".").showAndWait() }
					}
					else if (runActionOnComboBox.value == "Song") {  }
				}
				else if (functComboBox.value == "Delete" && runActionOnComboBox.value == "Playlist")
				{
					if (currentPlaylist.isUserEditable)
					{
						val confirmation = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete ${currentPlaylist.playlistName}?").showAndWait()
						if (confirmation.get() == ButtonType.OK)
						{
							val dbConn = DBConn()
							dbConn.runQuery("DELETE FROM PlaylistSong WHERE playlistName = '${currentPlaylist.playlistName}';")
							dbConn.runQuery("DELETE FROM Playlist WHERE playlistName = '${currentPlaylist.playlistName}';")
							dbConn.close()
						}
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
	}
}