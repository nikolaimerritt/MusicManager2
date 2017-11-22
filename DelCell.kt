package sample

import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.Priority
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

enum class PlaylistType { ALL_SONGS, REGULAR_PLAYLIST }
enum class PlaylistGroupType { ALL_PLAYLISTS, BIN }

internal class DelCell(delText: String) : ListCell<String>()
{
	private var hbox = HBox()
	private var label = Label()
	private var pane = Pane()
	private var button = Button(delText)

	constructor(delText: String, playlistType: PlaylistType, allSongs: ArrayList<Song>, currentSongs: ArrayList<Song>, currentSongNames: ObservableList<String>): this(delText)
	{
		hbox.children.addAll(button, label, pane)
		HBox.setHgrow(pane, Priority.ALWAYS)
		button.setOnAction {
			if (playlistType == PlaylistType.ALL_SONGS)
			{
				val songToAdd = allSongs[this.index]
				if (songToAdd !in currentSongs)
				{
					currentSongs.add(songToAdd)
					currentSongNames.add(songToAdd.title)
				}
			}
			else
			{
				currentSongs.removeAt(this.index)
				listView.items.remove(item)
			}

		}
	}

	constructor(delText: String, playlistGroupType: PlaylistGroupType, allPlaylistNames: ArrayList<String>, playlistBin: ArrayList<Playlist>, playlistNamesInBin: ArrayList<String>): this(delText)
	{
		hbox.children.addAll(button, label, pane)
		HBox.setHgrow(pane, Priority.ALWAYS)
		button.setOnAction {
			val allPlaylists = MP3Player.allPlaylists()
			if (playlistGroupType == PlaylistGroupType.ALL_PLAYLISTS)
			{
				val playlistToBin = allPlaylists[index]
				if (playlistToBin !in playlistBin)
				{
					playlistBin.add(playlistToBin)
					playlistNamesInBin.add(playlistToBin.playlistName)
					allPlaylistNames.remove(playlistToBin.playlistName)
				}
			}
			else
			{
				val playlistToRestore = playlistBin[index]
				playlistBin.remove(playlistToRestore)
				playlistNamesInBin.remove(playlistToRestore.playlistName)
				allPlaylistNames.add(playlistToRestore.playlistName)
			}
		}
	}

	constructor(delText: String, )

	override fun updateItem(item: String?, empty: Boolean)
	{
		super.updateItem(item, empty)
		text = null
		graphic = null

		if (item != null && !empty)
		{
			label.text = item
			graphic = hbox
		}
	}
}
