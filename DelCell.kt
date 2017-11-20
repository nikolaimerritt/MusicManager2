package sample

import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.Priority
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

enum class PlaylistType { ALL_SONGS, NOT_ALL_SONGS }

internal class DelCell(delText: String, type: PlaylistType, allSongs: ArrayList<Song>, currentSongs: ArrayList<Song>, currentSongNames: ObservableList<String>) : ListCell<String>()
{
	var hbox = HBox()
	var label = Label()
	var pane = Pane()
	var button = Button(delText)

	companion object { var amountSoFar = 0 }

	init
	{
		hbox.children.addAll(button, label, pane)
		HBox.setHgrow(pane, Priority.ALWAYS)
		button.setOnAction {
			if (type == PlaylistType.ALL_SONGS)
			{
				val songToAdd = allSongs[this.index]
				currentSongs.add(songToAdd)
				currentSongNames.add(songToAdd.title)
			}
			else
			{
				currentSongs.removeAt(this.index)
				listView.items.remove(item)
			}

		}
	}

	override fun updateItem(item: String?, empty: Boolean)
	{
		super.updateItem(item, empty)
		text = null
		graphic = null

		if (item != null && !empty) {
			label.text = item
			graphic = hbox
		}
	}
}
