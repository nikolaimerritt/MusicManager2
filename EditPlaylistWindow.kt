package sample

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import sample.Main.Companion.FRESH_AF_PLAYLIST_NAME

class EditPlaylistWindow
{
	companion object
	{
		fun spawnEditPlaylistWindow(playlist: Playlist)
		{
			val grid = GridPane()
			val scene = Scene(grid)
			val stage = Stage()
			stage.scene = scene
			stage.title = "Edit ${playlist.playlistName}"
			stage.setOnCloseRequest {
				val confirmationResult = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close without saving?").showAndWait()
				if (confirmationResult.get() == ButtonType.OK)
				{
					val dbConn = DBConn()
					dbConn.linesFromQuery("DELETE FROM Playlist WHERE playlistName = '$FRESH_AF_PLAYLIST_NAME';")
					dbConn.close()
					stage.close()
				}
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
			GridPane.setConstraints(allSongsHeader, 0, 2, 80, 1)
			grid.children.add(allSongsHeader)

			// add Current Playlist header
			val currentPlaylistHeader = TextField(playlist.playlistName)
			currentPlaylistHeader.isEditable = false
			GridPane.setConstraints(currentPlaylistHeader, 81, 2, 80, 1)
			grid.children.add(currentPlaylistHeader)

			// add All Songs table
			val allSongs = MP3Player.songsInPlaylist(MP3Player.playlistFromName("All Songs"))
			val allSongNames = FXCollections.observableArrayList<String>(MP3Player.songTitlesInPlaylist(MP3Player.playlistFromName("All Songs")))
			val currentSongs = ArrayList<Song>(MP3Player.songsInPlaylist(playlist))
			val currentSongNames = FXCollections.observableArrayList<String>(MP3Player.songTitlesInPlaylist(MP3Player.playlistFromName(playlist.playlistName)))

			val allSongsLV = ListView(allSongNames)
			allSongsLV.setCellFactory { _ -> DelCell("->", PlaylistType.ALL_SONGS, allSongs, currentSongs, currentSongNames) }
			allSongsLV.cellFactoryProperty()
			GridPane.setConstraints(allSongsLV, 0, 3, 80, 10)
			grid.children.add(allSongsLV)

			// add Current Playlist table
			val currentSongsLV = ListView(currentSongNames)
			currentSongsLV.setCellFactory { _ -> DelCell("<-", PlaylistType.NOT_ALL_SONGS, allSongs, currentSongs, currentSongNames) }
			GridPane.setConstraints(currentSongsLV, 81, 3, 80, 10)
			grid.children.add(currentSongsLV)

			// add publish button
			val publishButton = Button("Publish!")
			publishButton.setOnMouseClicked {
				if (playlist.playlistName == FRESH_AF_PLAYLIST_NAME && playlistNameBox.text == "") { Alert(Alert.AlertType.ERROR, "Please give your new playlist a name").showAndWait() }
				else
				{
					val dbConn = DBConn()
					val oldPlaylistName = playlist.playlistName
					val newPlaylistName = if (playlistNameBox.text.isNotBlank()) playlistNameBox.text else playlist.playlistName
					if (newPlaylistName != oldPlaylistName)
					{
						dbConn.linesFromQuery("SET FOREIGN_KEY_CHECKS = 0;")
						dbConn.linesFromQuery("UPDATE Playlist SET playlistName = '$newPlaylistName' WHERE playlistName = '$oldPlaylistName';")
						dbConn.linesFromQuery("UPDATE PlaylistSong SET playlistName = '$newPlaylistName' WHERE playlistName = '$oldPlaylistName';")
						dbConn.linesFromQuery("SET FOREIGN_KEY_CHECKS = 1;")
					}
					else { dbConn.linesFromQuery("DELETE FROM PlaylistSong WHERE playlistName = '$oldPlaylistName'") }
					for (song in currentSongs)
					{
						dbConn.linesFromQuery("INSERT IGNORE INTO PlaylistSong VALUES ('$newPlaylistName', ${song.songID});")
					}
					dbConn.close()
					stage.close()
				}
			}
			GridPane.setConstraints(publishButton, 0, 13)
			grid.children.add(publishButton)

			stage.show()
		}
	}
}