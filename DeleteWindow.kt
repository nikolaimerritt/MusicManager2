package sample

import javafx.collections.FXCollections.observableArrayList
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import sample.MP3Player.Companion.allPlaylists
import sample.MP3Player.Companion.playlistFromName
import sample.MP3Player.Companion.songsInPlaylist
import java.lang.reflect.Type

enum class DeleteType { PLAYLIST, SONG }
class DeleteWindow
{
	companion object
	{
		fun spawnDeleteWindow(deleteType: DeleteType, type: Type)
		{
			val grid = GridPane()
			val scene = Scene(grid)
			val stage = Stage()
			stage.scene = scene
			val deletingSongs = deleteType == DeleteType.SONG
			val typeName = if (type is Song) "Song" else "Playlist"
			stage.title = "Delete ${typeName}s."
			stage.setOnCloseRequest {
				val confirmationResult = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close without saving?").showAndWait()
				if (confirmationResult.get() == ButtonType.OK) { stage.close() }
			}
			grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
			grid.vgap = 5.0
			grid.hgap = 5.0

			// add explanatory header
			val hintHeader = TextField("Click a $typeName to move it to the opposite category.")
			hintHeader.isEditable = false
			GridPane.setConstraints(hintHeader, 0, 0, 80, 1)
			grid.children.add(hintHeader)

			// add All Songs/Playlists header
			val allSongsOrPlaylistsHeader = TextField("All ${typeName}s")
			allSongsOrPlaylistsHeader.isEditable = false
			GridPane.setConstraints(allSongsOrPlaylistsHeader, 0, 2, 80, 1)
			grid.children.add(allSongsOrPlaylistsHeader)

			// add All Songs ListView
			val allSongsOrPlaylists = if (deletingSongs) songsInPlaylist(playlistFromName("All Songs"))
				else allPlaylists()
			val allNames = observableArrayList<String>( if (deletingSongs)
				MP3Player.titlesInPlaylist(playlistFromName("All Songs"))
			else MP3Player.allPlaylistNames()
			)
			val songsOrPlaylistsToBin = if (deletingSongs) ArrayList<Song>() else ArrayList<Playlist>()
			val songOrPlaylistNamesToBin = observableArrayList<String>()

			val allSongsLV = ListView(allNames)
			allSongsLV.setCellFactory { _ -> DelCell("->", PlaylistGroupType.ALL_PLAYLISTS, ) }
			allSongsLV.cellFactoryProperty()
			GridPane.setConstraints(allSongsLV, 0, 3, 80, 10)
			grid.children.add(allSongsLV)

			// add Bin header
			val binHeader = TextField("Bin :(")
			binHeader.isEditable = false
			GridPane.setConstraints(binHeader, 81, 2, 80, 1)
			grid.children.add(binHeader)

			// add Bin ListView
			val songNamesToBinLV = ListView(songOrPlaylistNamesToBin)
			songNamesToBinLV.setCellFactory { _ -> DelCell("<-", PlaylistType.REGULAR_PLAYLIST, allSongsOrPlaylists, songsOrPlaylistsToBin, songOrPlaylistNamesToBin) }
			GridPane.setConstraints(songNamesToBinLV, 81, 3, 80, 10)
			grid.children.add(songNamesToBinLV)

			// add publish button
			val publishButton = Button("Publish!")
			publishButton.setOnMouseClicked { stage.close() }

			GridPane.setConstraints(publishButton, 0, 13)
			grid.children.add(publishButton)

			stage.show()
		}
	}
}