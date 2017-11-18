package sample

import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import java.io.File
import java.util.*
import javafx.beans.InvalidationListener



class Main : Application()
{
	val mp3Player = MP3Player()


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

        // add drop-down playlist menu
        val playlistMenu = ComboBox<String>()
        GridPane.setConstraints(playlistMenu, 0 ,1, 98, 1)
        grid.children.add(playlistMenu)

        // add song table
        val observableList = FXCollections.observableList<String>(getAllSongNames())
	    mp3Player.push(getAllSongNames())
	    val listView = ListView(observableList)
	    listView.selectionModel.selectedItemProperty().addListener { _, _, newValue -> mp3Player.skipTo(observableList.indexOf(newValue)) }
        listView.orientation = Orientation.VERTICAL
        GridPane.setConstraints(listView, 0, 2, 100, 1)
        grid.children.add(listView)

        // add play/pause button
        val playPauseButton = Button("▮▶")
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

        stage.show()
    }

    private fun login() = showNotImplemented()
    private fun addSong() = mp3Player.seekTo(0.5)

    private fun showNotImplemented() = Alert(Alert.AlertType.WARNING, "Not implemented yet... But watch this space!").showAndWait()

	private fun getAllSongNames(): ArrayList<String>
	{
		val directory = System.getProperty("user.dir") + "\\Songs\\"
		val files = File(directory).listFiles()
		val allNames = ArrayList<String>()
		files.forEach { if (it.isFile) allNames.add(it.name) }

		return allNames
	}

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>) = launch(Main::class.java)
    }
}