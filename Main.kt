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

class Main : Application()
{
    private val trackQueue = PriorityQueue<String>()
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
        val listView = ListView<String>(FXCollections.observableArrayList(trackQueue))
        listView.orientation = Orientation.VERTICAL
        GridPane.setConstraints(listView, 0, 2, 100, 1)
        grid.children.add(listView)

        // add play/pause button
        val playPauseButton = Button("▮▶")
        playPauseButton.setOnAction { playPause() }
        GridPane.setConstraints(playPauseButton, 0, 3)
        grid.children.add(playPauseButton)

        // add progress bar
        val progressBar = ProgressBar(0.0)
        progressBar.maxWidth = Double.MAX_VALUE
        GridPane.setConstraints(progressBar, 1, 3, 97, 1)
        grid.children.add(progressBar)

        // add skip backwards button
        val skipBackwardsButton = Button("◀")
        skipBackwardsButton.setOnMouseClicked { seek() }
        GridPane.setConstraints(skipBackwardsButton, 98, 3)
        grid.children.add(skipBackwardsButton)

        // add skip forwards button
        val skipForwardsButton = Button("▶")
        skipForwardsButton.setOnMouseClicked { seek() }
        GridPane.setConstraints(skipForwardsButton, 99, 3)
        grid.children.add(skipForwardsButton)

        stage.show()
    }

    private fun login() = showNotImplemented()
    private fun addSong() = showNotImplemented()
    private fun playPause() = showNotImplemented()
    private fun seek() = showNotImplemented()

    private fun showNotImplemented() = Alert(Alert.AlertType.WARNING, "Not implemented yet... But watch this space!").showAndWait()

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>) { launch(Main::class.java) }
    }
}